package com.maxmelandriii.contapuntimarafone.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxmelandriii.contapuntimarafone.data.local.PartitaEntity
import com.maxmelandriii.contapuntimarafone.domain.models.Player
import com.maxmelandriii.contapuntimarafone.ui.theme.ContaPuntiMarafoneTheme
import com.maxmelandriii.contapuntimarafone.ui.screens.HistoryCard
import com.maxmelandriii.contapuntimarafone.ui.screens.HorizontalLayout
import com.maxmelandriii.contapuntimarafone.ui.components.MenuPartita
import com.maxmelandriii.contapuntimarafone.ui.components.PopupChangePoint
import com.maxmelandriii.contapuntimarafone.ui.components.PopupVictory
import com.maxmelandriii.contapuntimarafone.ui.screens.VerticalLayout

@Preview(showBackground = true, device = Devices.PIXEL_7, name = "1. Layout Verticale (Smartphone)")
@Composable
fun VerticalLayoutPreview() {
    val noi = Player("Noi").apply { setPoint(24) }
    val voi = Player("Voi").apply { setPoint(11) }
    ContaPuntiMarafoneTheme {
        Surface {
            VerticalLayout(
                innerPadding = PaddingValues(0.dp),
                noi = noi,
                voi = voi,
                puntiInseritiNoi = "5",
                puntiInseritiVoi = "6",
                isMaraffaNoi = true,
                isMaraffaVoi = false,
                onNomeNoiChange = {},
                onNomeVoiChange = {},
                onPuntiNoiChange = {},
                onPuntiVoiChange = {},
                onMaraffaNoiChange = {},
                onMaraffaVoiChange = {},
                onAddClick = {},
                onUndoClick = {},
                onMenuClick = {}
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape", name = "2. Layout Orizzontale (Tablet)")
@Composable
fun HorizontalLayoutPreview() {
    val noi = Player("Noi").apply { setPoint(35) }
    val voi = Player("Voi").apply { setPoint(40) }
    ContaPuntiMarafoneTheme {
        Surface {
            HorizontalLayout(
                innerPadding = PaddingValues(0.dp),
                noi = noi,
                voi = voi,
                puntiInseritiNoi = "0",
                puntiInseritiVoi = "11",
                isMaraffaNoi = false,
                isMaraffaVoi = true,
                onNomeNoiChange = {},
                onNomeVoiChange = {},
                onPuntiNoiChange = {},
                onPuntiVoiChange = {},
                onMaraffaNoiChange = {},
                onMaraffaVoiChange = {},
                onAddClick = {},
                onUndoClick = {},
                onMenuClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "3. Menu Opzioni")
@Composable
fun MenuPartitaPreview() {
    ContaPuntiMarafoneTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MenuPartita(
                showMenu = true,
                onDismiss = {},
                onApriPopup = {},
                onNuovaPartita = {},
                onApriCronologia = {},
                vittoriaSoglia = 41,
                onVittoriaSogliaChange = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "4. Popup Modifica Punti")
@Composable
fun PopupsPreview() {
    ContaPuntiMarafoneTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PopupChangePoint(
                showPopup = true,
                onDismiss = {},
                onSave = { _, _ -> },
                nomeNoi = "Noi",
                nomeVoi = "Voi"
            )
        }
    }
}

@Preview(showBackground = true, name = "5. Popup Vittoria")
@Composable
fun VictoryPreview() {
    ContaPuntiMarafoneTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PopupVictory(
                showPopup = true,
                onDismiss = {},
                onNuovaPartita = {},
                onContinue = {},
                nomeNoi = "Noi",
                nomeVoi = "Voi",
                puntiNoi = 41,
                puntiVoi = 38
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "6. Pagina Cronologia (Mock)")
@Composable
fun HistoryPagePreview() {
    val mockPartite = listOf(
        PartitaEntity(1, "Noi", "Voi", 41, 22, "Noi", "20 Mag 2024, 14:30", false, 41),
        PartitaEntity(2, "I biondi", "I mori", 15, 31, "I mori", "19 Mag 2024, 21:00", true, 31),
        PartitaEntity(3, "Max", "Leo", 44, 40, "Max", "18 Mag 2024, 10:15", false, 41)
    )

    ContaPuntiMarafoneTheme {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("STORICO", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 600.dp)
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    items(mockPartite) { partita ->
                        HistoryCard(partita = partita, onResume = {})
                    }
                }
            }
        }
    }
}
