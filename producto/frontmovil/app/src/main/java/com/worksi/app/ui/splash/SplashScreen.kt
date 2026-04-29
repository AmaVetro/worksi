package com.worksi.app.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.worksi.app.ui.theme.CyanPrimary
import com.worksi.app.ui.theme.OrangeAccent
import com.worksi.app.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
        delay(1500)
        alpha.animateTo(0f, animationSpec = tween(500))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyanPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Texto compuesto: "Work" en blanco, "SÍ" en naranja
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                ) {
                    append("Work")
                }
                withStyle(
                    SpanStyle(
                        color = OrangeAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp
                    )
                ) {
                    append("SÍ")
                }
            },
            modifier = Modifier.alpha(alpha.value)
        )
    }
}