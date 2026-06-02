// NOME FILE: MainActivity.kt
// PERCORSO: app/src/main/java/com/maxmelandriii/contapuntimarafone/MainActivity.kt

package com.maxmelandriii.contapuntimarafone

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.room.Room
import com.maxmelandriii.contapuntimarafone.ui.theme.ContaPuntiMarafoneTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.gms.ads.MobileAds // Importalo in cima
import com.maxmelandriii.contapuntimarafone.data.AppDatabase
import com.maxmelandriii.contapuntimarafone.data.PartitaEntity
import com.maxmelandriii.contapuntimarafone.domain.Player
import com.maxmelandriii.contapuntimarafone.feature.History
import com.maxmelandriii.contapuntimarafone.feature.HorizontalLayout
import com.maxmelandriii.contapuntimarafone.feature.MenuPartita
import com.maxmelandriii.contapuntimarafone.feature.PopupChangePoint
import com.maxmelandriii.contapuntimarafone.feature.PopupVictory
import com.maxmelandriii.contapuntimarafone.feature.VerticalLayout


lateinit var db: AppDatabase

class MainActivity : ComponentActivity() {

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    // ✨ L'ANNOTAZIONE DA VERA BOSS CHE HAI SCOVATO TU! ✨
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "marafone-db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

        enableEdgeToEdge()

        setContent {
            // ✨ 1. ACCENDIAMO IL RADAR DEGLI SCHERMI E DELL'ORIENTAMENTO ✨
            val windowSizeClass = calculateWindowSizeClass(this)
            val configuration = LocalConfiguration.current

            // ✨ 2. CAPIAMO IL DISPOSITIVO ✨
            val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            // ✨ 3. LA DECISIONE DA CEO: Blocchiamo o liberiamo la rotazione ✨
            LaunchedEffect(isCompact) {
                if (isCompact) {
                    // Su telefono: Bloccato in verticale (Portrait)
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    // Su Tablet (Medium/Expanded): Libero di girare!
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            ContaPuntiMarafoneTheme {

                val isSamsung = Build.MANUFACTURER.equals("samsung", ignoreCase = true)
                val dynamicCornerRadius = if (isSamsung) 21.dp else 32.dp

                // ✨ IL LOOK iOS: NERO PURO O BIANCO PURO ✨
                val isDark = isSystemInDarkTheme()
                val iosBgColor = if (isDark) Color.Black else Color.White
                val iosContentColor = if (isDark) Color.White else Color.Black

                val ctx = LocalContext.current
                val scope = rememberCoroutineScope()
                val dao = db.partitaDao()
                val lifecycleOwner = LocalLifecycleOwner.current

                val noi = remember { Player("") }
                val voi = remember { Player("") }
                var idPartitaAttuale by remember { mutableStateOf<Int?>(null) }

                var puntiInseritiNoi by remember { mutableStateOf("") }
                var puntiInseritiVoi by remember { mutableStateOf("") }
                var isMaraffaNoi by remember { mutableStateOf(false) }
                var isMaraffaVoi by remember { mutableStateOf(false) }

                var mostraPopup by remember { mutableStateOf(false) }
                var mostraMenuSheet by remember { mutableStateOf(false) }
                var mostraPopupVittoria by remember { mutableStateOf(false) }
                var continuaDopo41 by remember { mutableStateOf(false) }

                val handlePuntiChange = { input: String, isNoi: Boolean ->
                    val s = input.filter { it.isDigit() }
                    if (s.isEmpty()) { puntiInseritiNoi = ""; puntiInseritiVoi = "" }
                    else {
                        s.toIntOrNull()?.let {
                            if (it in 0..11) {
                                if (isNoi) { puntiInseritiNoi = it.toString(); puntiInseritiVoi = (11 - it).toString() }
                                else { puntiInseritiVoi = it.toString(); puntiInseritiNoi = (11 - it).toString() }
                            }
                        }
                    }
                }//TODO


                val salvaStatoInDB = { forzaChiusura: Boolean ->
                    val pNoi = noi.punti
                    val pVoi = voi.punti
                    val nNoi = noi.nomeSquad.ifEmpty { "Noi" }
                    val nVoi = voi.nomeSquad.ifEmpty { "Voi" }
                    val idPartita = idPartitaAttuale

                    val partitaVeramenteFinita = forzaChiusura || pNoi >= 41 || pVoi >= 41
                    val statoInCorso = !partitaVeramenteFinita

                    scope.launch {
                        val partita = PartitaEntity(
                            id = idPartita ?: 0,
                            nomeNoi = nNoi,
                            nomeVoi = nVoi,
                            puntiNoi = pNoi,
                            puntiVoi = pVoi,
                            vincitore = when {
                                pNoi >= 41 && pNoi > pVoi -> nNoi
                                pVoi >= 41 && pVoi > pNoi -> nVoi
                                forzaChiusura -> "Resettata"
                                else -> ""
                            },
                            dataPartita = ottieniDataDiOggi(),
                            inCorso = statoInCorso
                        )

                        if (idPartita == null) {
                            val nuovoId = dao.insertPartita(partita)
                            idPartitaAttuale = nuovoId.toInt()
                        } else {
                            dao.updatePartita(partita)
                        }
                    }
                }

                val ricaricaPartita = {
                    scope.launch {
                        val idDaCaricare = intent.getIntExtra("ID_PARTITA_RIPRENDI", -1)
                        val sospesa = if (idDaCaricare != -1) {
                            dao.getPartitaById(idDaCaricare)
                        } else {
                            dao.getPartitaInCorso()
                        }

                        if (sospesa != null) {
                            idPartitaAttuale = sospesa.id
                            noi.nomeSquad = sospesa.nomeNoi
                            voi.nomeSquad = sospesa.nomeVoi
                            noi.setPoint(sospesa.puntiNoi)
                            voi.setPoint(sospesa.puntiVoi)
                            intent.removeExtra("ID_PARTITA_RIPRENDI")
                        }
                    }
                }

                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) ricaricaPartita()
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
                }

                Scaffold(
                    containerColor = iosBgColor,
                    contentColor = iosContentColor
                ) { innerPadding ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        // ✨ IL BIVIO DI LUSSO: COME DISEGNARE LO SCHERMO ✨
                        if (isLandscape && !isCompact) {
                            HorizontalLayout(
                                innerPadding = innerPadding,
                                noi = noi,
                                voi = voi,
                                dynamicCornerRadius = dynamicCornerRadius,
                                puntiInseritiNoi = puntiInseritiNoi,
                                puntiInseritiVoi = puntiInseritiVoi,
                                isMaraffaNoi = isMaraffaNoi,
                                isMaraffaVoi = isMaraffaVoi,
                                onNomeNoiChange = { noi.nomeSquad = it; salvaStatoInDB(false) },
                                onNomeVoiChange = { voi.nomeSquad = it; salvaStatoInDB(false) },
                                onPuntiNoiChange = { handlePuntiChange(it, true) },
                                onPuntiVoiChange = { handlePuntiChange(it, false) },
                                onMaraffaNoiChange = { isMaraffaNoi = it },
                                onMaraffaVoiChange = { isMaraffaVoi = it },
                                onAddClick = {
                                    noi.aggiungiPunti(
                                        puntiInseritiNoi.toIntOrNull() ?: 0, isMaraffaNoi
                                    ); voi.aggiungiPunti(
                                    puntiInseritiVoi.toIntOrNull() ?: 0,
                                    isMaraffaVoi
                                ); puntiInseritiNoi = ""; puntiInseritiVoi = ""; isMaraffaNoi =
                                    false; isMaraffaVoi = false; salvaStatoInDB(false)
                                },
                                onUndoClick = {
                                    noi.annullaErrore(); voi.annullaErrore(); isMaraffaNoi =
                                    false; isMaraffaVoi = false; salvaStatoInDB(false)
                                },
                                onMenuClick = { mostraMenuSheet = true }
                            )
                        } else {
                            if(!isLandscape && !isCompact){
                                VerticalLayout(
                                    innerPadding = innerPadding,
                                    noi = noi,
                                    voi = voi,
                                    dynamicCornerRadius = dynamicCornerRadius,
                                    puntiInseritiNoi = puntiInseritiNoi,
                                    puntiInseritiVoi = puntiInseritiVoi,
                                    isMaraffaNoi = isMaraffaNoi,
                                    isMaraffaVoi = isMaraffaVoi,
                                    onNomeNoiChange = { noi.nomeSquad = it; salvaStatoInDB(false) },
                                    onNomeVoiChange = { voi.nomeSquad = it; salvaStatoInDB(false) },
                                    onPuntiNoiChange = { handlePuntiChange(it, true) },
                                    onPuntiVoiChange = { handlePuntiChange(it, false) },
                                    onMaraffaNoiChange = { isMaraffaNoi = it },
                                    onMaraffaVoiChange = { isMaraffaVoi = it },
                                    onAddClick = {
                                        noi.aggiungiPunti(
                                            puntiInseritiNoi.toIntOrNull() ?: 0, isMaraffaNoi
                                        ); voi.aggiungiPunti(
                                        puntiInseritiVoi.toIntOrNull() ?: 0,
                                        isMaraffaVoi
                                    ); puntiInseritiNoi = ""; puntiInseritiVoi = ""; isMaraffaNoi =
                                        false; isMaraffaVoi = false; salvaStatoInDB(false)
                                    },
                                    onUndoClick = {
                                        noi.annullaErrore(); voi.annullaErrore(); isMaraffaNoi =
                                        false; isMaraffaVoi = false; salvaStatoInDB(false)
                                    },
                                    onMenuClick = { mostraMenuSheet = true },
                                    iconSize = 50.dp,
                                    nameSize = 40,
                                    pointSize = 190,
                                    insertFontSize = 50,
                                    insertHeight = 90.dp,
                                    maraffaFontSize = 16,
                                    imageHeight = 350.dp,
                                    buttonHeight = 90.dp
                                )
                            }
                            else {
                                VerticalLayout(
                                    innerPadding = innerPadding,
                                    noi = noi,
                                    voi = voi,
                                    dynamicCornerRadius = dynamicCornerRadius,
                                    puntiInseritiNoi = puntiInseritiNoi,
                                    puntiInseritiVoi = puntiInseritiVoi,
                                    isMaraffaNoi = isMaraffaNoi,
                                    isMaraffaVoi = isMaraffaVoi,
                                    onNomeNoiChange = { noi.nomeSquad = it; salvaStatoInDB(false) },
                                    onNomeVoiChange = { voi.nomeSquad = it; salvaStatoInDB(false) },
                                    onPuntiNoiChange = { handlePuntiChange(it, true) },
                                    onPuntiVoiChange = { handlePuntiChange(it, false) },
                                    onMaraffaNoiChange = { isMaraffaNoi = it },
                                    onMaraffaVoiChange = { isMaraffaVoi = it },
                                    onAddClick = {
                                        noi.aggiungiPunti(
                                            puntiInseritiNoi.toIntOrNull() ?: 0, isMaraffaNoi
                                        ); voi.aggiungiPunti(
                                        puntiInseritiVoi.toIntOrNull() ?: 0,
                                        isMaraffaVoi
                                    ); puntiInseritiNoi = ""; puntiInseritiVoi = ""; isMaraffaNoi =
                                        false; isMaraffaVoi = false; salvaStatoInDB(false)
                                    },
                                    onUndoClick = {
                                        noi.annullaErrore(); voi.annullaErrore(); isMaraffaNoi =
                                        false; isMaraffaVoi = false; salvaStatoInDB(false)
                                    },
                                    onMenuClick = { mostraMenuSheet = true }
                                )
                            }
                        }
                        }

                        // ✨ I POPUP RIMANGONO COMUNI A ENTRAMBI I LAYOUT ✨
                    MenuPartita(
                        showMenu = mostraMenuSheet,
                        onDismiss = { mostraMenuSheet = false },
                        onApriPopup = { mostraPopup = true },
                        onNuovaPartita = {
                            salvaStatoInDB(false)
                            noi.azzeraPartita(); voi.azzeraPartita()
                            idPartitaAttuale = null
                            puntiInseritiNoi = ""; puntiInseritiVoi = ""
                        },
                        onApriCronologia = {
                            ctx.startActivity(
                                Intent(
                                    ctx,
                                    History::class.java
                                )
                            )
                        }
                    )

                    PopupChangePoint(
                        showPopup = mostraPopup,
                        onDismiss = { mostraPopup = false },
                        onSave = { pN, pV ->
                            noi.setPoint(pN.toIntOrNull() ?: 0)
                            voi.setPoint(pV.toIntOrNull() ?: 0)
                            mostraPopup = false; salvaStatoInDB(false)
                        },
                        nomeNoi = noi.nomeSquad,
                        nomeVoi = voi.nomeSquad
                    )

                        if ((noi.punti >= 41 || voi.punti >= 41) && noi.punti != voi.punti && !continuaDopo41) {
                            mostraPopupVittoria = true
                        }

                    PopupVictory(
                        showPopup = mostraPopupVittoria,
                        onDismiss = { mostraPopupVittoria = false },
                        onNuovaPartita = {
                            salvaStatoInDB(true)
                            mostraPopupVittoria = false
                            noi.azzeraPartita(); voi.azzeraPartita()
                            idPartitaAttuale = null
                        },
                        onContinue = {
                            continuaDopo41 = true
                            mostraPopupVittoria = false
                            salvaStatoInDB(false)
                        },
                        nomeNoi = noi.nomeSquad,
                        nomeVoi = voi.nomeSquad,
                        puntiNoi = noi.punti,
                        puntiVoi = voi.punti
                    )
                    }
                }
            }
        }
    }

fun ottieniDataDiOggi(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ITALY)
    return formatter.format(Date())
}