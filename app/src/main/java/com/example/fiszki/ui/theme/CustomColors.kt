package com.example.fiszki.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val content: Color = Color(0xFFDD0D3C),
    val component: Color = Color(0xFFC20029),
    val background: List<Color> = listOf(Color.White, Color(0xFFF8BBD0)),
    val flashcardBackground: Color = Color(0xFFFFFFFF),
    val correctButton: Color = Color(0xFF0FB895),
    val wrongButton: Color = Color(0xFFEB485B),
    val closeIconBackground: Color = Color(0xFFFFFFFF),
    val grey: Color = Color(0xFFDAD6D6)
)

val LocalColors = compositionLocalOf { CustomColors() }