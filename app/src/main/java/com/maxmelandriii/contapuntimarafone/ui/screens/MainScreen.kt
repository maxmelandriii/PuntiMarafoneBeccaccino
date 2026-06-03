package com.maxmelandriii.contapuntimarafone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxmelandriii.contapuntimarafone.ui.components.*
import com.maxmelandriii.contapuntimarafone.domain.models.Player

@Composable
fun VerticalLayout(
    innerPadding: PaddingValues,
    noi: Player,
    voi: Player,
    puntiInseritiNoi: String,
    puntiInseritiVoi: String,
    isMaraffaNoi: Boolean,
    isMaraffaVoi: Boolean,
    onNomeNoiChange: (String) -> Unit,
    onNomeVoiChange: (String) -> Unit,
    onPuntiNoiChange: (String) -> Unit,
    onPuntiVoiChange: (String) -> Unit,
    onMaraffaNoiChange: (Boolean) -> Unit,
    onMaraffaVoiChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onUndoClick: () -> Unit,
    onMenuClick: () -> Unit,
    // ✨ I TUOI DATI ESATTI MESSI COME DEFAULT! ✨
    iconSize: Dp = 25.dp,
    nameSize: Int = 18,
    pointSize: Int = 85,
    insertFontSize: Int = 32,
    insertHeight: Dp = 68.dp,
    maraffaFontSize: Int = 14,
    imageHeight: Dp = 230.dp,
    buttonHeight: Dp = 68.dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 600.dp)
            .padding(
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        BannerPubblicitario()
        
        // --- BLOCCO 1: PUNTEGGI ---
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp)) {
            WinnerBannerRow(puntiVoi = voi.punti, puntiNoi = noi.punti, customIconSize = iconSize)
            NameBarRow(
                nomeNoi = noi.nomeSquad, onNomeNoiChange = onNomeNoiChange,
                nomeVoi = voi.nomeSquad, onNomeVoiChange = onNomeVoiChange,
                customFontSize = nameSize
            )
            PointSquadRow(puntiNoi = noi.punti, puntiVoi = voi.punti, customFontSize = pointSize)
        }

        // --- BLOCCO 2: INSERIMENTO E MARAFFA ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .padding(horizontal = 15.dp, vertical = 15.dp),
            shape = getDynamicRadius(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                InsertPointSquadRow(
                    puntiNoi = puntiInseritiNoi, onPuntiNoiChange = onPuntiNoiChange,
                    puntiVoi = puntiInseritiVoi, onPuntiVoiChange = onPuntiVoiChange,
                    customFontSize = insertFontSize, customHeight = insertHeight
                )
                SwitchMaraffaRow(
                    isMaraffaNoi = isMaraffaNoi, onMaraffaNoiChange = onMaraffaNoiChange,
                    isMaraffaVoi = isMaraffaVoi, onMaraffaVoiChange = onMaraffaVoiChange,
                    customFontSize = maraffaFontSize, customHeight = insertHeight
                )

                // ✨ NASCONDI IMMAGINE SE LO SCHERMO È TROPPO BASSO ✨
                val screenHeight = LocalConfiguration.current.screenHeightDp
                if (screenHeight > 450) {
                    CardImageRow(
                        modifier = Modifier.weight(1f, fill = false),
                        customHeight = imageHeight
                    )
                }
            }
        }

        // --- BLOCCO 3: COMANDI ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 0.dp),
            shape = getDynamicRadius(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ActionButtonsRow(
                    onAddClick = onAddClick, onUndoClick = onUndoClick, onMenuClick = onMenuClick,
                    customHeight = buttonHeight
                )
            }
        }
    }
}

@Composable
fun HorizontalLayout(
    innerPadding: PaddingValues,
    noi: Player,
    voi: Player,
    puntiInseritiNoi: String,
    puntiInseritiVoi: String,
    isMaraffaNoi: Boolean,
    isMaraffaVoi: Boolean,
    onNomeNoiChange: (String) -> Unit,
    onNomeVoiChange: (String) -> Unit,
    onPuntiNoiChange: (String) -> Unit,
    onPuntiVoiChange: (String) -> Unit,
    onMaraffaNoiChange: (Boolean) -> Unit,
    onMaraffaVoiChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onUndoClick: () -> Unit,
    onMenuClick: () -> Unit,
    iconSize: Dp = 40.dp,
    nameSize: Int = 24,
    pointSize: Int = 120,
    insertFontSize: Int = 32,
    insertHeight: Dp = 80.dp,
    maraffaFontSize: Int = 14,
    imageHeight: Dp = 290.dp,
    buttonHeight: Dp = 80.dp
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 1000.dp)
            .padding(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 24.dp,
                end = 24.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- COLONNA SINISTRA: I Punteggi ---
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center, // Rende il gruppo nomi/punti/corone più compatto
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            WinnerBannerRow(puntiVoi = voi.punti, puntiNoi = noi.punti, customIconSize = iconSize)
            NameBarRow(
                nomeNoi = noi.nomeSquad, onNomeNoiChange = onNomeNoiChange,
                nomeVoi = voi.nomeSquad, onNomeVoiChange = onNomeVoiChange,
                customFontSize = nameSize
            )
            PointSquadRow(puntiNoi = noi.punti, puntiVoi = voi.punti, customFontSize = pointSize)
        }

        // --- COLONNA DESTRA: I Controlli ---
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            BannerPubblicitario()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                shape = getDynamicRadius(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    InsertPointSquadRow(
                        puntiNoi = puntiInseritiNoi, onPuntiNoiChange = onPuntiNoiChange,
                        puntiVoi = puntiInseritiVoi, onPuntiVoiChange = onPuntiVoiChange,
                        customFontSize = insertFontSize, customHeight = insertHeight
                    )
                    SwitchMaraffaRow(
                        isMaraffaNoi = isMaraffaNoi, onMaraffaNoiChange = onMaraffaNoiChange,
                        isMaraffaVoi = isMaraffaVoi, onMaraffaVoiChange = onMaraffaVoiChange,
                        customFontSize = maraffaFontSize, customHeight = insertHeight
                    )
                    
                    // ✨ NASCONDI IMMAGINE SE LO SCHERMO È TROPPO BASSO (ES: TELEFONI IN LANDSCAPE) ✨
                    val screenHeight = LocalConfiguration.current.screenHeightDp
                    if (screenHeight > 450) {
                        CardImageRow(
                            modifier = Modifier.weight(1f, fill = false),
                            customHeight = imageHeight
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                shape = getDynamicRadius(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ActionButtonsRow(
                        onAddClick = onAddClick,
                        onUndoClick = onUndoClick,
                        onMenuClick = onMenuClick,
                        customHeight = buttonHeight
                    )
                }
            }
        }
    }
}
