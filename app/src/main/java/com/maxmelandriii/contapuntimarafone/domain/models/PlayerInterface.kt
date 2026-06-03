package com.maxmelandriii.contapuntimarafone.domain.models

interface Squadra {
    val nome: String
    val punti: Int

    val prevPunti: Int

    fun aggiungiPunti(quantita: Int, maraffa: Boolean)
    fun annullaErrore()
    fun azzeraPartita()

    fun setPoint(newPoint: Int){
    }
}
