package com.maxmelandriii.contapuntimarafone.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.maxmelandriii.contapuntimarafone.MarafoneApplication
import com.maxmelandriii.contapuntimarafone.data.local.PartitaEntity
import com.maxmelandriii.contapuntimarafone.data.local.PartitaDao
import com.maxmelandriii.contapuntimarafone.domain.models.Player
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val dao: PartitaDao) : ViewModel() {

    private val prefs = MarafoneApplication.instance.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // --- STATO DEL GIOCO ---
    val noi = Player(prefs.getString("nomeNoi", "") ?: "")
    val voi = Player(prefs.getString("nomeVoi", "") ?: "")
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

    var lastIncrementWas11Noi by mutableStateOf(false)
    var lastIncrementWas11Voi by mutableStateOf(false)

    // --- LOGICA DI BUSINESS ---

    fun updateNomeSquadra(nuovoNome: String, isNoi: Boolean) {
        if (isNoi) {
            noi.nomeSquad = nuovoNome
            prefs.edit().putString("nomeNoi", nuovoNome).apply()
        } else {
            voi.nomeSquad = nuovoNome
            prefs.edit().putString("nomeVoi", nuovoNome).apply()
        }
        salvaStatoInDB()
    }

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
        
        lastIncrementWas11Noi = (pNoi == 11)
        lastIncrementWas11Voi = (pVoi == 11)

        noi.aggiungiPunti(pNoi, isMaraffaNoi)
        voi.aggiungiPunti(pVoi, isMaraffaVoi)
        
        // ✨ SPEGNI L'EFFETTO FUOCO DOPO 4 SECONDI (Animation + Attesa) ✨
        if (lastIncrementWas11Noi || lastIncrementWas11Voi) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(4000)
                lastIncrementWas11Noi = false
                lastIncrementWas11Voi = false
            }
        }

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
        mostraPopupVittoria = false
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
