package com.example.hexastar.Hexagons

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.core.graphics.minus
import com.example.hexastar.DEFAULT_HEXAGON_BORDER_COLOR
import com.example.hexastar.DEFAULT_HEXAGON_COLOR
import kotlin.math.sqrt

class Hexagon(
    var centerX: Float = 0f,
    var centerY: Float = 0f,
    var radius: Float = 0f,
    var hexagonColor: Int = DEFAULT_HEXAGON_COLOR,
    var borderWidth: Float = 0f,
    var borderColor: Int = DEFAULT_HEXAGON_BORDER_COLOR,
) {
    private var borderRadius: Float = 0f
    private var triangleHeight: Float = 0f
    private var borderTriangleHeight: Float = 0f

    class HexPath(var outerPath: Path = Path(), var innerPath: Path = Path())
        
    init {
        calculateConstants()
    }
    fun calculateConstants() {
        borderRadius = radius - borderWidth
        triangleHeight = (sqrt(3.0) * radius / 2).toFloat()
        borderTriangleHeight = (sqrt(3.0) * borderRadius / 2).toFloat()
    }
    fun drawHexagon(
        canvas: Canvas,
        paint: Paint,
        drawMode: Int = VERTICAL_HEXAGONS,
        drawBorder: Boolean = false
    ) {
        val paintInitColor = paint.color

        val hexPath = when (drawMode) {
            VERTICAL_HEXAGONS -> calculatePathVerticalHexagon(drawBorder)
            HORIZONTAL_HEXAGONS -> calculatePathHorizontalHexagon(drawBorder)
            else -> HexPath()
        }
        paint.color = hexagonColor
        if (drawBorder && borderRadius > 0) {
            canvas.drawPath(hexPath.innerPath, paint)
            paint.color = borderColor
        }
        canvas.drawPath(hexPath.outerPath, paint)

        paint.color = paintInitColor
    }
    private fun calculatePathVerticalHexagon(drawBorder: Boolean): HexPath {
        val hexPath = HexPath()
        hexPath.outerPath.moveTo(centerX, centerY + radius)
        hexPath.outerPath.lineTo(centerX - triangleHeight, centerY + radius / 2)
        hexPath.outerPath.lineTo(centerX - triangleHeight, centerY - radius / 2)
        hexPath.outerPath.lineTo(centerX, centerY - radius)
        hexPath.outerPath.lineTo(centerX + triangleHeight, centerY - radius / 2)
        hexPath.outerPath.lineTo(centerX + triangleHeight, centerY + radius / 2)
        hexPath.outerPath.moveTo(centerX, centerY + radius)
        if (drawBorder) {
            hexPath.innerPath.moveTo(centerX, centerY + borderRadius)
            hexPath.innerPath.lineTo(centerX - borderTriangleHeight, centerY + borderRadius / 2)
            hexPath.innerPath.lineTo(centerX - borderTriangleHeight, centerY - borderRadius / 2)
            hexPath.innerPath.lineTo(centerX, centerY - borderRadius)
            hexPath.innerPath.lineTo(centerX + borderTriangleHeight, centerY - borderRadius / 2)
            hexPath.innerPath.lineTo(centerX + borderTriangleHeight, centerY + borderRadius / 2)
            hexPath.innerPath.moveTo(centerX, centerY + borderRadius)

            hexPath.outerPath = hexPath.outerPath.minus(hexPath.innerPath)
        }
        return hexPath
    }
    fun calculatePathHorizontalHexagon(drawBorder: Boolean): HexPath {
        val hexPath = HexPath()
        hexPath.outerPath.moveTo(centerX - radius, centerY)
        hexPath.outerPath.lineTo(centerX - radius / 2, centerY - triangleHeight)
        hexPath.outerPath.lineTo(centerX + radius / 2, centerY - triangleHeight)
        hexPath.outerPath.lineTo(centerX + radius, centerY)
        hexPath.outerPath.lineTo(centerX + radius / 2, centerY + triangleHeight)
        hexPath.outerPath.lineTo(centerX - radius / 2, centerY + triangleHeight)
        hexPath.outerPath.moveTo(centerX - radius, centerY)
        if (drawBorder) {
            hexPath.innerPath.moveTo(centerX - borderRadius, centerY)
            hexPath.innerPath.lineTo(centerX - borderRadius / 2, centerY - borderTriangleHeight)
            hexPath.innerPath.lineTo(centerX + borderRadius / 2, centerY - borderTriangleHeight)
            hexPath.innerPath.lineTo(centerX + borderRadius, centerY)
            hexPath.innerPath.lineTo(centerX + borderRadius / 2, centerY + borderTriangleHeight)
            hexPath.innerPath.lineTo(centerX - borderRadius / 2, centerY + borderTriangleHeight)
            hexPath.innerPath.moveTo(centerX - borderRadius, centerY)

            hexPath.outerPath = hexPath.outerPath.minus(hexPath.innerPath)
        }
        return hexPath
    }
    companion object {
        const val VERTICAL_HEXAGONS = 0
        const val HORIZONTAL_HEXAGONS = 1
    }
}