package com.maxmelandriii.contapuntimarafone

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.ads.MobileAds
import com.maxmelandriii.contapuntimarafone.ui.theme.ContaPuntiMarafoneTheme
import com.maxmelandriii.contapuntimarafone.data.local.AppDatabase
import com.maxmelandriii.contapuntimarafone.data.local.PartitaDao
import com.maxmelandriii.contapuntimarafone.ui.components.MenuPartita
import com.maxmelandriii.contapuntimarafone.ui.components.PopupChangePoint
import com.maxmelandriii.contapuntimarafone.ui.components.PopupVictory
import com.maxmelandriii.contapuntimarafone.ui.screens.History
import com.maxmelandriii.contapuntimarafone.ui.screens.HorizontalLayout
import com.maxmelandriii.contapuntimarafone.ui.screens.VerticalLayout
import com.maxmelandriii.contapuntimarafone.ui.viewmodel.MainViewModel
val db: AppDatabase get() = MarafoneApplication.instance.database

class MainViewModelFactory(private val dao: PartitaDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}

        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(db.partitaDao()))
            
            val windowSizeClass = calculateWindowSizeClass(this)
            val configuration = LocalConfiguration.current
            val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

            LaunchedEffect(isCompact) {
                requestedOrientation = if (isCompact) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            ContaPuntiMarafoneTheme {
                val isDark = isSystemInDarkTheme()
                val iosBgColor = if (isDark) Color.Black else Color.White

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = iosBgColor
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isLandscape && !isCompact) {
                            HorizontalLayout(
                                innerPadding = innerPadding,
                                noi = viewModel.noi,
                                voi = viewModel.voi,
                                puntiInseritiNoi = viewModel.puntiInseritiNoi,
                                puntiInseritiVoi = viewModel.puntiInseritiVoi,
                                isMaraffaNoi = viewModel.isMaraffaNoi,
                                isMaraffaVoi = viewModel.isMaraffaVoi,
                                onNomeNoiChange = {
                                    viewModel.noi.nomeSquad = it; viewModel.salvaStatoInDB()
                                },
                                onNomeVoiChange = {
                                    viewModel.voi.nomeSquad = it; viewModel.salvaStatoInDB()
                                },
                                onPuntiNoiChange = { viewModel.handlePuntiChange(it, true) },
                                onPuntiVoiChange = { viewModel.handlePuntiChange(it, false) },
                                onMaraffaNoiChange = { viewModel.isMaraffaNoi = it },
                                onMaraffaVoiChange = { viewModel.isMaraffaVoi = it },
                                onAddClick = { viewModel.addPoints() },
                                onUndoClick = { viewModel.undo() },
                                onMenuClick = { viewModel.mostraMenuSheet = true }
                            )
                        } else {
                            VerticalLayout(
                                innerPadding = innerPadding,
                                noi = viewModel.noi,
                                voi = viewModel.voi,
                                puntiInseritiNoi = viewModel.puntiInseritiNoi,
                                puntiInseritiVoi = viewModel.puntiInseritiVoi,
                                isMaraffaNoi = viewModel.isMaraffaNoi,
                                isMaraffaVoi = viewModel.isMaraffaVoi,
                                onNomeNoiChange = {
                                    viewModel.noi.nomeSquad = it; viewModel.salvaStatoInDB()
                                },
                                onNomeVoiChange = {
                                    viewModel.voi.nomeSquad = it; viewModel.salvaStatoInDB()
                                },
                                onPuntiNoiChange = { viewModel.handlePuntiChange(it, true) },
                                onPuntiVoiChange = { viewModel.handlePuntiChange(it, false) },
                                onMaraffaNoiChange = { viewModel.isMaraffaNoi = it },
                                onMaraffaVoiChange = { viewModel.isMaraffaVoi = it },
                                onAddClick = { viewModel.addPoints() },
                                onUndoClick = { viewModel.undo() },
                                onMenuClick = { viewModel.mostraMenuSheet = true }
                            )
                        }

                        MenuPartita(
                            showMenu = viewModel.mostraMenuSheet,
                            onDismiss = { viewModel.mostraMenuSheet = false },
                            onApriPopup = { viewModel.mostraPopup = true },
                            onNuovaPartita = { viewModel.nuovaPartita() },
                            onApriCronologia = {
                                val intent = Intent(this@MainActivity, History::class.java)
                                startActivity(intent)
                            },
                            vittoriaSoglia = viewModel.vittoriaSoglia,
                            onVittoriaSogliaChange = {
                                viewModel.vittoriaSoglia = it; viewModel.salvaStatoInDB()
                            }
                        )

                        PopupChangePoint(
                            showPopup = viewModel.mostraPopup,
                            onDismiss = { viewModel.mostraPopup = false },
                            onSave = { pNoi, pVoi ->
                                viewModel.noi.setPoint(pNoi.toIntOrNull() ?: 0)
                                viewModel.voi.setPoint(pVoi.toIntOrNull() ?: 0)
                                viewModel.salvaStatoInDB()
                            },
                            nomeNoi = viewModel.noi.nomeSquad,
                            nomeVoi = viewModel.voi.nomeSquad
                        )

                        PopupVictory(
                            showPopup = viewModel.mostraPopupVittoria,
                            onDismiss = { viewModel.mostraPopupVittoria = false },
                            onNuovaPartita = { viewModel.nuovaPartita() },
                            onContinue = {
                                viewModel.continuaOltreSoglia =
                                    true; viewModel.mostraPopupVittoria = false
                            },
                            nomeNoi = viewModel.noi.nomeSquad,
                            nomeVoi = viewModel.voi.nomeSquad,
                            puntiNoi = viewModel.noi.punti,
                            puntiVoi = viewModel.voi.punti
                        )
                    }
                }
            }
        }
    }
}
