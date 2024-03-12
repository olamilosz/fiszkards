package com.example.fiszki.ui.konfetti

import android.graphics.drawable.Drawable
import com.example.fiszki.ui.konfetti.DrawableImage
import nl.dionsegijn.konfetti.core.models.Shape

object ImageUtil {
    @JvmStatic
    fun loadDrawable(
        drawable: Drawable,
        tint: Boolean = true,
        applyAlpha: Boolean = true,
    ): Shape.DrawableShape {
        val width = drawable.intrinsicWidth
        val height = drawable.intrinsicHeight
        val drawableImage = DrawableImage(drawable, width, height)
        return Shape.DrawableShape(drawable, tint, applyAlpha)
    }
}