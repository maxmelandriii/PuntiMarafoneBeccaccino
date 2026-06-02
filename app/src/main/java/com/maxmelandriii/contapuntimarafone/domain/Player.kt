package com.maxmelandriii.contapuntimarafone.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
class Player(override val nome: String) : Squadra {
    override var punti by mutableIntStateOf(0)
        private set

    override var prevPunti by mutableIntStateOf(0)
        private set

    var nomeSquad by mutableStateOf(nome)
    override fun aggiungiPunti(quantita: Int, maraffa: Boolean) {
        prevPunti = punti
        if(maraffa){
            punti += quantita+3
        }else{
            punti += quantita
        }
    }

    override fun annullaErrore() {
        punti = prevPunti
    }

    override fun azzeraPartita() {
        punti = 0
        prevPunti = 0
    }

    override fun setPoint(newPoint: Int) {
        punti = newPoint
    }
}