package com.maxmelandriii.contapuntimarafone.feature

import android.os.Build
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxmelandriii.contapuntimarafone.R
import com.maxmelandriii.contapuntimarafone.components.SlotNumber
import com.maxmelandriii.contapuntimarafone.components.SquadMaraffaSwitch
import com.maxmelandriii.contapuntimarafone.components.SquadNameField
import com.maxmelandriii.contapuntimarafone.components.SquadScoreInputField
import com.maxmelandriii.contapuntimarafone.components.animateTrophy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ✨ UTILITY PER IL LOOK COORDINATO (SAMSUNG VS OTHERS) ✨
@Composable
fun getDynamicRadius(isLarge: Boolean = true): RoundedCornerShape {
    val isSamsung = Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    return if (isLarge) {
        RoundedCornerShape(if (isSamsung) 21.dp else 32.dp)
    } else {
        RoundedCornerShape(if (isSamsung) 14.dp else 24.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuPartita(
    showMenu: Boolean,
    onDismiss: () -> Unit,
    onApriPopup: () -> Unit,
    onNuovaPartita: () -> Unit,
    onApriCronologia: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val smallRadius = getDynamicRadius(false)

    if (showMenu) {
        var startAnimation by remember { mutableStateOf(false) }
        val tempoDiAttesa = 65L

        LaunchedEffect(Unit) {
            delay(tempoDiAttesa)
            startAnimation = true
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = getDynamicRadius(),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 48.dp, top = 8.dp)
            ) {
                Text(
                    text = "Opzioni Partita",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AnimatedVisibility(
                    visible = startAnimation,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(150))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(smallRadius)
                                .clickable {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            onDismiss()
                                            onApriPopup()
                                        }
                                    }
                                }
                                .padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Create, contentDescription = "Modifica Punti", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Modifica Punteggio", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(smallRadius)
                                .clickable{
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if(!sheetState.isVisible) {
                                            onDismiss()
                                            onApriCronologia()
                                        }
                                    }
                                }
                                .padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(Icons.Default.History, contentDescription = "Cronologia", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Cronologia Partite", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(smallRadius)
                                .clickable {
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            onDismiss()
                                            onNuovaPartita()
                                        }
                                    }
                                }
                                .padding(vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Nuova Partita", tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Nuova Partita", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PopupChangePoint(
    showPopup: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    nomeNoi: String,
    nomeVoi: String
) {
    var inputPuntiNoi by remember { mutableStateOf("") }
    var inputPuntiVoi by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showPopup) {
        AlertDialog(
            shape = getDynamicRadius(),
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Modifica Punteggio", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            },
            text = {
                val labelNoi = if (nomeNoi.isEmpty()) "Noi" else nomeNoi
                val labelVoi = if (nomeVoi.isEmpty()) "Voi" else nomeVoi
                val smallRadius = getDynamicRadius(false)

                Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = inputPuntiNoi,
                        onValueChange = { inputPuntiNoi = it.filter { char -> char.isDigit() } },
                        placeholder = { Text("Punti $labelNoi") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, shape = smallRadius,
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )

                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = inputPuntiVoi,
                        onValueChange = { inputPuntiVoi = it.filter { char -> char.isDigit() } },
                        placeholder = { Text("Punti $labelVoi") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true, shape = smallRadius,
                        colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val numeroNoi = inputPuntiNoi.toIntOrNull() ?: 0
                    val numeroVoi = inputPuntiVoi.toIntOrNull() ?: 0
                    val puntiTot = numeroNoi + numeroVoi

                    if (puntiTot % 11 != 0 && puntiTot != 0) {
                        showConfirmDialog = true
                    } else {
                        onSave(inputPuntiNoi, inputPuntiVoi)
                        inputPuntiNoi = ""
                        inputPuntiVoi = ""
                    }
                }) {
                    Text("Salva", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismiss()
                    inputPuntiNoi = ""
                    inputPuntiVoi = ""
                }) {
                    Text("Annulla", fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                }
            },
        )

        if (showConfirmDialog) {
            AlertDialog(
                modifier = Modifier.height(190.dp),
                shape = getDynamicRadius(),
                containerColor = MaterialTheme.colorScheme.surface,
                onDismissRequest = { showConfirmDialog = false },
                title = {
                    Text("ATTENZIONE", fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                },
                text = {
                    Text("I punti inseriti non sono multipli di 11!\n", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                },
                confirmButton = {
                    TextButton(onClick = {
                        onSave(inputPuntiNoi, inputPuntiVoi)
                        showConfirmDialog = false
                        inputPuntiNoi = ""
                        inputPuntiVoi = ""
                    }) {
                        Text("Inserisci", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Annulla", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    }
}

@Composable
fun PopupVictory(
    showPopup: Boolean,
    onDismiss: () -> Unit,
    onNuovaPartita: () -> Unit,
    onContinue: () -> Unit,
    nomeNoi: String,
    nomeVoi: String,
    puntiNoi: Int,
    puntiVoi: Int
) {
    if (showPopup) {
        AlertDialog(
            shape = getDynamicRadius(),
            containerColor = MaterialTheme.colorScheme.surface,
            onDismissRequest = onDismiss,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = "Vittoria",
                        tint = Color(0xFFD4AF37),
                        modifier = Modifier.size(60.dp)
                    )
                }
            },
            text = {
                val labelNoi = if (nomeNoi.isEmpty()) "Noi" else nomeNoi
                val labelVoi = if (nomeVoi.isEmpty()) "Voi" else nomeVoi
                val vincitore = if (puntiNoi > puntiVoi) labelNoi else labelVoi

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VITTORIA PER $vincitore!",
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onNuovaPartita) {
                    Text("Nuova Partita", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = onContinue) {
                    Text("Continua", fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                }
            },
        )
    }
}

@Composable
fun WinnerBannerRow(puntiNoi: Int, puntiVoi: Int, customIconSize: Dp) {
    val vittoriaNoi = puntiNoi > puntiVoi
    val vittoriaVoi = puntiVoi > puntiNoi

    val animNoi = animateTrophy(vittoria = vittoriaNoi, labelPrefix = "Noi")
    val animVoi = animateTrophy(vittoria = vittoriaVoi, labelPrefix = "Voi")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Vittoria Noi",
                tint = Color(0xFFD4AF37),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(customIconSize)
                    .graphicsLayer(
                        alpha = animNoi.alpha,
                        scaleX = animNoi.scale,
                        scaleY = animNoi.scale
                    )
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                contentDescription = "Vittoria Voi",
                tint = Color(0xFFD4AF37),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(customIconSize)
                    .graphicsLayer(
                        alpha = animVoi.alpha,
                        scaleX = animVoi.scale,
                        scaleY = animVoi.scale
                    )
            )
        }
    }
}

@Composable
fun NameBarRow(
    nomeNoi: String,
    onNomeNoiChange: (String) -> Unit,
    nomeVoi: String,
    onNomeVoiChange: (String) -> Unit,
    customFontSize: Int = 18
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, start = 15.dp, end = 15.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SquadNameField(
            value = nomeNoi,
            onValueChange = onNomeNoiChange,
            placeholder = "Noi",
            customFontSize = customFontSize
        )

        SquadNameField(
            value = nomeVoi,
            onValueChange = onNomeVoiChange,
            placeholder = "Voi",
            customFontSize = customFontSize
        )

    }
}

@Composable
fun PointSquadRow(puntiNoi: Int, puntiVoi: Int, customFontSize: Int = 85) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, start = 15.dp, end = 15.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SlotNumber(
            punti = puntiNoi,
            modifier = Modifier.weight(1f),
            customFontSize = customFontSize
        )
        SlotNumber(
            punti = puntiVoi,
            modifier = Modifier.weight(1f),
            customFontSize = customFontSize
        )
    }
}

@Composable
fun InsertPointSquadRow(
    puntiNoi: String,
    onPuntiNoiChange: (String) -> Unit,
    puntiVoi: String,
    onPuntiVoiChange: (String) -> Unit,
    customFontSize: Int = 32,
    customHeight: Dp = 68.dp // ✨ FISSATO PER IL MOBILE, LIBERO PER IL TABLET ✨
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        /*Box(
            modifier = Modifier
                .weight(1f)
                .height(customHeight) // ✨ ALTEZZA DINAMICA ✨
                .background(MaterialTheme.colorScheme.surface, getDynamicRadius(false)),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = puntiNoi,
                onValueChange = onPuntiNoiChange,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = customFontSize.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (puntiNoi.isEmpty()) Text("0", fontSize = customFontSize.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    innerTextField()
                }
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(customHeight) // ✨ ALTEZZA DINAMICA ✨
                .background(MaterialTheme.colorScheme.surface, getDynamicRadius(false)),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = puntiVoi,
                onValueChange = onPuntiVoiChange,
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = customFontSize.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (puntiVoi.isEmpty()) Text("0", fontSize = customFontSize.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    innerTextField()
                }
            )
        }*/
        SquadScoreInputField(puntiNoi, onPuntiNoiChange, customFontSize, customHeight)
        SquadScoreInputField(puntiVoi, onPuntiVoiChange, customFontSize, customHeight)
    }
}

@Composable
fun SwitchMaraffaRow(
    isMaraffaNoi: Boolean,
    onMaraffaNoiChange: (Boolean) -> Unit,
    isMaraffaVoi: Boolean,
    onMaraffaVoiChange: (Boolean) -> Unit,
    customFontSize: Int = 14,
    customHeight: Dp = 68.dp
) {
    var nomeMossa by remember { mutableStateOf("Maraffa") }
    val view = LocalView.current

    // Isoliamo la logica dell'haptic feedback e del cambio nome per passarla comodamente ai figli
    val handleLongPress: () -> Unit = {
        nomeMossa = if (nomeMossa == "Maraffa") "Cricca" else "Maraffa"
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SquadMaraffaSwitch(nomeMossa, isMaraffaNoi, onMaraffaNoiChange, handleLongPress, customFontSize, customHeight)
        SquadMaraffaSwitch(nomeMossa, isMaraffaVoi, onMaraffaVoiChange, handleLongPress, customFontSize, customHeight)
        /*Card(
            modifier = Modifier
                .weight(1f)
                .height(customHeight), // ✨ ALTEZZA DINAMICA: Identico all'insert! ✨
            shape = smallRadius,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize() // ✨ Prende tutta l'altezza senza forzare padding strani ✨
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = nomeMossa,
                    fontSize = customFontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                nomeMossa = if (nomeMossa == "Maraffa") "Cricca" else "Maraffa"
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                        )
                    }
                )
                Switch(
                    checked = isMaraffaNoi,
                    onCheckedChange = onMaraffaNoiChange,
                    thumbContent = if (isMaraffaNoi) { { Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) } } else null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary, uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(customHeight), // ✨ ALTEZZA DINAMICA: Identico all'insert! ✨
            shape = smallRadius,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize() // ✨ Prende tutta l'altezza senza forzare padding strani ✨
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = nomeMossa,
                    fontSize = customFontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                nomeMossa = if (nomeMossa == "Maraffa") "Cricca" else "Maraffa"
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            }
                        )
                    }
                )
                Switch(
                    checked = isMaraffaVoi,
                    onCheckedChange = onMaraffaVoiChange,
                    thumbContent = if (isMaraffaVoi) { { Icon(imageVector = Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) } } else null,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary, uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }*/
    }
}

@Composable
fun CardImageRow(customHeight: Dp = 200.dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.card),
            contentDescription = "Foto carte briscola",
            modifier = Modifier
                .height(customHeight)
                .padding(top = 4.dp, bottom = 16.dp),
            alignment = Alignment.Center
        )
    }
}


@Composable
fun ActionButtonsRow(onAddClick: () -> Unit, onUndoClick: () -> Unit, onMenuClick: () -> Unit, customHeight: Dp = 60.dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 5.dp,
                start = 15.dp,
                end = 15.dp,
                bottom = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val btnRadius = getDynamicRadius(false)

        Button(
            onClick = onMenuClick,
            modifier = Modifier.weight(1f).height(customHeight), // ✨ ALTEZZA DINAMICA ✨
            shape = btnRadius,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(Icons.Filled.Menu, "Menu", modifier = Modifier.size(28.dp))
        }

        Button(
            onClick = onUndoClick,
            modifier = Modifier.weight(1f).height(customHeight), // ✨ ALTEZZA DINAMICA ✨
            shape = btnRadius,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(imageVector = Icons.Default.Undo, contentDescription = "Annulla", modifier = Modifier.size(ButtonDefaults.IconSize))
        }

        Button(
            onClick = onAddClick,
            modifier = Modifier.weight(2f).height(customHeight),
            shape = btnRadius,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Icona Aggiungi", modifier = Modifier.size(ButtonDefaults.IconSize))
        }
    }
}

@Composable
fun BannerPubblicitario(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // ✨ ID DEL BANNER DI TEST DI GOOGLE ✨
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}