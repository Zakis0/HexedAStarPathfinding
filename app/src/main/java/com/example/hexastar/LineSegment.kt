package com.example.hexastar

import android.graphics.Canvas
import android.graphics.Paint

// Does not used

data class LineSegment(var x1: Float, var y1: Float, var x2: Float, var y2: Float) {
    fun centralScale(scale: Float) {
        val X1 = (1 - scale) / 2 * x2 + (1 + scale) / 2 * x1
        val Y1 = (1 - scale) / 2 * y2 + (1 + scale) / 2 * y1
        val X2 = (1 + scale) / 2 * x2 + (1 - scale) / 2 * x1
        val Y2 = (1 + scale) / 2 * y2 + (1 - scale) / 2 * y1
        x1 = X1
        y1 = Y1
        x2 = X2
        y2 = Y2
    }
    fun drawLineSegment(canvas: Canvas, paint: Paint) = canvas.drawLine(x1, y1, x2, y2, paint)
    override fun toString() = "{[$x1, $y1], [$x2, $y2]}"
}