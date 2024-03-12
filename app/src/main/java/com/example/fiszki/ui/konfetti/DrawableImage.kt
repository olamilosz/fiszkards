package com.example.fiszki.ui.konfetti

import android.graphics.drawable.Drawable
import com.example.fiszki.ui.konfetti.CoreImage

data class DrawableImage(
    val drawable: Drawable,
    override val width: Int,
    override val height: Int,
) : CoreImage
