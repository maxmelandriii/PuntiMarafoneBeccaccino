package com.maxmelandriii.contapuntimarafone.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
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

// ✨ COMPONENTE DEDICATO ALL'EFFETTO SLOT (ODOMETER) CON EFFETTO FUOCO DISSOLVENTE ✨
@Composable
fun SlotNumber(punti: Int, modifier: Modifier = Modifier, isFire: Boolean = false, customFontSize: Int) {
    val puntiAnimati by animateIntAsState(
        targetValue = punti,
        animationSpec = tween(durationMillis = 1500),
        label = "PuntiAnimati"
    )

    // ✨ ANIMAZIONE DISSOLVENZA FUOCO (DIMMERAZIONE) ✨
    val fireIntensity by animateFloatAsState(
        targetValue = if (isFire) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing),
        label = "FireIntensity"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "FireTransition")
    val fireOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "FireOffset"
    )

    val fireBrush = Brush.linearGradient(
        colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF4500), Color(0xFFFFD700)),
        start = Offset(fireOffset, fireOffset),
        end = Offset(fireOffset + 300f, fireOffset + 300f)
    )

    AnimatedContent(
        targetState = puntiAnimati,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp), // ✨ SPAZIO EXTRA PER NON TAGLIARE IL BAGLIORE ✨
        contentAlignment = Alignment.Center,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(animationSpec = tween(800)) { height -> height } + fadeIn(animationSpec = tween(200))).togetherWith(
                    slideOutVertically(animationSpec = tween(800)) { height -> -height } + fadeOut(animationSpec = tween(200))
                )
            } else {
                (slideInVertically(animationSpec = tween(800)) { height -> -height } + fadeIn(animationSpec = tween(200))).togetherWith(
                    slideOutVertically(animationSpec = tween(800)) { height -> height } + fadeOut(animationSpec = tween(200))
                )
            }
        },
        label = "SlotAnimation"
    ) { targetCount ->
        Box(contentAlignment = Alignment.Center) {
            // 1. Il numero base (bianco/onSurface)
            Text(
                text = targetCount.toString(),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                fontSize = customFontSize.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // 2. Il numero "infuocato" sopra, che sfuma (dimmerazione)
            if (fireIntensity > 0f) {
                Text(
                    text = targetCount.toString(),
                    modifier = Modifier.fillMaxWidth().alpha(fireIntensity),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = customFontSize.sp,
                    style = MaterialTheme.typography.displayLarge.copy(
                        brush = fireBrush,
                        shadow = Shadow(
                            color = Color(0xFFFF8C00).copy(alpha = 0.8f * fireIntensity),
                            offset = Offset(0f, 0f),
                            blurRadius = 25f * fireIntensity
                        )
                    )
                )
            }
        }
    }
}
