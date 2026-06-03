package com.maxmelandriii.contapuntimarafone

import com.maxmelandriii.contapuntimarafone.domain.models.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class DomainLogicTest {

    @Test
    fun testPlayerPointsAddition() {
        val player = Player("Test")
        assertEquals(0, player.punti)
        
        player.aggiungiPunti(5, false)
        assertEquals(5, player.punti)
    }

    @Test
    fun testPlayerMaraffa() {
        val player = Player("Test")
        // Maraffa aggiunge 3 punti bonus (11 + 3 = 14)
        player.aggiungiPunti(11, true)
        assertEquals(14, player.punti)
    }

    @Test
    fun testPlayerUndo() {
        val player = Player("Test")
        player.aggiungiPunti(10, false)
        assertEquals(10, player.punti)
        
        player.aggiungiPunti(5, false)
        assertEquals(15, player.punti)
        
        player.annullaErrore()
        assertEquals(10, player.punti)
    }

    @Test
    fun testPlayerReset() {
        val player = Player("Test")
        player.aggiungiPunti(10, false)
        player.azzeraPartita()
        assertEquals(0, player.punti)
    }
}
