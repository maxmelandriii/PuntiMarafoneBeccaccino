package com.maxmelandriii.contapuntimarafone.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun animatedPunti(punti: Int): Int {
    val puntiAnimati by animateIntAsState(
        targetValue = punti,
        label = "Punti Animati",
        animationSpec = tween(durationMillis = 900, easing = LinearOutSlowInEasing)
    )
    return puntiAnimati
}

data class TrophyAnimations(val alpha: Float, val scale: Float)

@Composable
fun animateTrophy(vittoria: Boolean, labelPrefix: String): TrophyAnimations {
    val alphaAnimato by animateFloatAsState(
        targetValue = if (vittoria) 1f else 0.15f,
        animationSpec = tween(durationMillis = 800),
        label = "Alpha $labelPrefix"
    )

    val scaleAnimato by animateFloatAsState(
        targetValue = if (vittoria) 1.2f else 1f, // 20% più grande se vinci!
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
        label = "Scale $labelPrefix"
    )

    return TrophyAnimations(alpha = alphaAnimato, scale = scaleAnimato)
}

// ✨ COMPONENTE DEDICATO ALL'EFFETTO SLOT (ODOMETER) - SISTEMATO! ✨
@Composable
fun SlotNumber(punti: Int, modifier: Modifier = Modifier, customFontSize: Int) {
    val puntiAnimati by animateIntAsState(
        targetValue = punti,
        animationSpec = tween(durationMillis = 1000),
        label = "PuntiAnimati"
    )

    AnimatedContent(
        targetState = puntiAnimati,
        modifier = modifier,
        transitionSpec = {
            if (targetState > initialState) {
                // Se sale: entra dal basso, esce dall'alto
                (slideInVertically(animationSpec = tween(500)) { height -> height } + fadeIn(animationSpec = tween(100))).togetherWith(
                    slideOutVertically(animationSpec = tween(500)) { height -> -height } + fadeOut(animationSpec = tween(100))
                )
            } else {
                // Se scende: entra dall'alto (-height), esce dal basso (height)
                (slideInVertically(animationSpec = tween(500)) { height -> -height } + fadeIn(animationSpec = tween(100))).togetherWith(
                    slideOutVertically(animationSpec = tween(500)) { height -> height } + fadeOut(animationSpec = tween(100))
                )
            }
        },
        label = "SlotAnimation"
    ) { targetCount ->
        Text(
            text = targetCount.toString(),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = customFontSize.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
