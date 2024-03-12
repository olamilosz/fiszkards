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
    val grey: Color = Color(0xFFCCCAC8),
    val greyBackground: Color = Color(0xFFE6E6E6),
    val deckListItemBackground: Color = Color(0xFFf6edff),
    val beige: Color = Color(0xFF816EB1),
    val fabButton: Color = Color(0xFF43CA94)
)

val LocalColors = compositionLocalOf { CustomColors() }