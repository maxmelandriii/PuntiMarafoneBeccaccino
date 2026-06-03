package com.maxmelandriii.contapuntimarafone.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxmelandriii.contapuntimarafone.data.local.PartitaEntity
import com.maxmelandriii.contapuntimarafone.data.local.PartitaDao
import com.maxmelandriii.contapuntimarafone.domain.models.Player
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val dao: PartitaDao) : ViewModel() {

    // --- STATO DEL GIOCO ---
    val noi = Player("Noi")
    val voi = Player("Voi")
    var idPartitaAttuale by mutableStateOf<Int?>(null)
    
    var puntiInseritiNoi by mutableStateOf("")
    var puntiInseritiVoi by mutableStateOf("")
    var isMaraffaNoi by mutableStateOf(false)
    var isMaraffaVoi by mutableStateOf(false)

    var mostraPopup by mutableStateOf(false)
    var mostraMenuSheet by mutableStateOf(false)
    var mostraPopupVittoria by mutableStateOf(false)
    var continuaOltreSoglia by mutableStateOf(false)
    var vittoriaSoglia by mutableIntStateOf(41)

    // --- LOGICA DI BUSINESS ---

    fun handlePuntiChange(input: String, isNoi: Boolean) {
        val s = input.filter { it.isDigit() }
        if (s.isEmpty()) {
            puntiInseritiNoi = ""; puntiInseritiVoi = ""
        } else {
            s.toIntOrNull()?.let {
                if (it in 0..11) {
                    if (isNoi) {
                        puntiInseritiNoi = it.toString()
                        puntiInseritiVoi = (11 - it).toString()
                    } else {
                        puntiInseritiVoi = it.toString()
                        puntiInseritiNoi = (11 - it).toString()
                    }
                }
            }
        }
    }

    fun addPoints() {
        val pNoi = puntiInseritiNoi.toIntOrNull() ?: 0
        val pVoi = puntiInseritiVoi.toIntOrNull() ?: 0
        
        noi.aggiungiPunti(pNoi, isMaraffaNoi)
        voi.aggiungiPunti(pVoi, isMaraffaVoi)
        
        // Reset campi
        puntiInseritiNoi = ""; puntiInseritiVoi = ""
        isMaraffaNoi = false; isMaraffaVoi = false

        checkVittoria()
        salvaStatoInDB()
    }

    private fun checkVittoria() {
        if (!continuaOltreSoglia) {
            if (noi.punti >= vittoriaSoglia || voi.punti >= vittoriaSoglia) {
                mostraPopupVittoria = true
            }
        }
    }

    fun undo() {
        noi.annullaErrore()
        voi.annullaErrore()
        salvaStatoInDB()
    }

    fun nuovaPartita() {
        noi.azzeraPartita()
        voi.azzeraPartita()
        idPartitaAttuale = null
        continuaOltreSoglia = false
        puntiInseritiNoi = ""; puntiInseritiVoi = ""
    }

    fun salvaStatoInDB(forzaChiusura: Boolean = false) {
        val pNoi = noi.punti
        val pVoi = voi.punti
        val nNoi = noi.nomeSquad.ifEmpty { "Noi" }
        val nVoi = voi.nomeSquad.ifEmpty { "Voi" }

        val terminata = forzaChiusura || (pNoi >= vittoriaSoglia || pVoi >= vittoriaSoglia)

        viewModelScope.launch {
            val entity = PartitaEntity(
                id = idPartitaAttuale ?: 0,
                nomeNoi = nNoi,
                nomeVoi = nVoi,
                puntiNoi = pNoi,
                puntiVoi = pVoi,
                vincitore = when {
                    pNoi >= vittoriaSoglia && pNoi > pVoi -> nNoi
                    pVoi >= vittoriaSoglia && pVoi > pNoi -> nVoi
                    else -> "In corso"
                },
                dataPartita = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date()),
                inCorso = !terminata,
                sogliaVittoria = vittoriaSoglia
            )
            val result = dao.insertPartita(entity)
            if (idPartitaAttuale == null) idPartitaAttuale = result.toInt()
        }
    }
}
