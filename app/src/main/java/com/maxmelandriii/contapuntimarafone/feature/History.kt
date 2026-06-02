package com.maxmelandriii.contapuntimarafone.feature

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxmelandriii.contapuntimarafone.MainActivity
import com.maxmelandriii.contapuntimarafone.data.PartitaEntity
import com.maxmelandriii.contapuntimarafone.db
import com.maxmelandriii.contapuntimarafone.ui.theme.ContaPuntiMarafoneTheme
import kotlinx.coroutines.launch

class History : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ContaPuntiMarafoneTheme {
                val scope = rememberCoroutineScope()
                val dao = db.partitaDao()

                val isDark = isSystemInDarkTheme()
                val iosBgColor = if (isDark) Color.Black else Color.White
                val iosContentColor = if (isDark) Color.White else Color.Black

                val listaPartite by dao.getAllPartite().collectAsState(initial = emptyList())
                var mostraDialogSvuota by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    "STORICO",
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    color = iosContentColor
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Indietro",
                                        tint = iosContentColor
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { mostraDialogSvuota = true }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Svuota Tutto",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = iosBgColor,
                                titleContentColor = iosContentColor,
                                navigationIconContentColor = iosContentColor
                            )
                        )
                    },
                    containerColor = iosBgColor
                ) { innerPadding ->
                    // ✨ IL BOX CENTRATORE PER IL TABLET ✨
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        if (listaPartite.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .widthIn(max = 600.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Nessuna partita salvata",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    // ✨ IL LIMITE DI LARGHEZZA PER NON FARLA STIRARE ✨
                                    .fillMaxHeight()
                                    .widthIn(max = 600.dp)
                                    .padding(innerPadding)
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(vertical = 20.dp)
                            ) {
                                items(items = listaPartite, key = { it.id }) { partita ->
                                    val dismissState = rememberSwipeToDismissBoxState()

                                    LaunchedEffect(dismissState.currentValue) {
                                        if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                            dao.deletePartita(partita)
                                        }
                                    }

                                    SwipeToDismissBox(
                                        state = dismissState,
                                        enableDismissFromEndToStart = false,
                                        enableDismissFromStartToEnd = true,
                                        backgroundContent = {
                                            val contentAlpha by animateFloatAsState(
                                                targetValue = if (dismissState.progress > 0.05f) 1f else 0f,
                                                label = "AlphaCestino"
                                            )
                                            val bgColor by animateColorAsState(
                                                if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd)
                                                    MaterialTheme.colorScheme.errorContainer else Color.Transparent,
                                                label = "BgColor"
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    // ✨ ROUNDED SMART PER SWIPE ✨
                                                    .clip(getDynamicRadius())
                                                    .background(bgColor)
                                                    .padding(horizontal = 24.dp)
                                                    .graphicsLayer { alpha = contentAlpha },
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Elimina",
                                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }
                                    ) {
                                        HistoryCard(partita, onResume = { id ->
                                            val intent = Intent(this@History, MainActivity::class.java)
                                            intent.putExtra("ID_PARTITA_RIPRENDI", id)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                                            startActivity(intent)
                                            finish()
                                        })
                                    }
                                }
                            }
                        }

                        if (mostraDialogSvuota) {
                            AlertDialog(
                                // ✨ ROUNDED SMART PER POPUP SVUOTA ✨
                                shape = getDynamicRadius(),
                                onDismissRequest = { mostraDialogSvuota = false },
                                title = { Text("Svuota Cronologia") },
                                text = { Text("Vuoi cancellare tutta la cronologia?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        scope.launch { dao.svuotaCronologia() }
                                        mostraDialogSvuota = false
                                    }) { Text("Svuota", color = MaterialTheme.colorScheme.error) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { mostraDialogSvuota = false }) { Text("Annulla") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(partita: PartitaEntity, onResume: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        // ✨ ROUNDED SMART PER LA CARD ✨
        shape = getDynamicRadius(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                // ✨ ALLARGATO A 130.dp: Niente più riga spezzata per i punteggi a 2 cifre! ✨
                modifier = Modifier.width(130.dp)
            ) {
                Text(
                    text = "${partita.puntiNoi} - ${partita.puntiVoi}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 32.sp
                )
                Text(
                    text = "${partita.nomeNoi.lowercase()} - ${partita.nomeVoi.lowercase()}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = partita.dataPartita.split(",")[0],
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 16.dp)
                )

                if (partita.inCorso) {
                    IconButton(
                        onClick = { onResume(partita.id) },
                        modifier = Modifier
                            .size(44.dp)
                            // ✨ ROUNDED SMART PER PULSANTE PLAY ✨
                            .clip(getDynamicRadius(false))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Riprendi",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
    }
}