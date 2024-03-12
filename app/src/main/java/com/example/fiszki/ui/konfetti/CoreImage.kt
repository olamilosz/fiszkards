package com.example.fiszki.ui.konfetti

interface CoreImage {
    val width: Int
    val height: Int
}

data class ReferenceImage(
    val reference: Int,
    override val width: Int,
    override val height: Int,
) : CoreImage