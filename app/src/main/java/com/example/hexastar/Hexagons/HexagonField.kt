package com.example.hexastar.Hexagons

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.hexastar.HEXAGON_BORDER_WIDTH
import kotlin.math.sqrt

class HexagonField(
    fieldWidth: Int,
    fieldHeight: Int,
    val hexagonRadius: Float,
    viewWidth: Int,
    viewHeight: Int,
) {
    val INIT_HEXAGON_OFFSET = hexagonRadius

    private val field: Array<Array<Hexagon?>> = Array(fieldHeight) { Array(fieldWidth) { null } }
    private var horizontalOffset: Float = 0f
    private var verticalOffset: Float = 0f

    init {
        val hexagon = Hexagon(
            viewWidth / 2f,
            viewHeight / 2f,
            hexagonRadius,
            Color.GREEN,
            HEXAGON_BORDER_WIDTH,
            Color.WHITE
        )
        val hexagon2 = Hexagon(
            viewWidth / 2f,
            viewHeight / 2f,
            hexagonRadius,
            Color.RED,
            HEXAGON_BORDER_WIDTH,
            Color.BLACK
        )
        fillFieldWithHexagon(hexagon)
        countConstants(Hexagon.HORIZONTAL_HEXAGONS)

        field[2][4] = null
        field[2][5] = null
        field[3][5] = null
        field[5][8] = null

        field[6][4] = hexagon2
        field[6][3] = hexagon2
        field[11][2] = hexagon2
//        field[0][0] = hexagon
//        field[0][1] = hexagon
    }
    fun fillFieldWithHexagon(hexagon: Hexagon) {
        field.forEachIndexed { i, hexagonsRow ->
            hexagonsRow.forEachIndexed { j, _ ->
                field[i][j] = hexagon.apply {
                    radius = hexagonRadius
                    borderWidth = HEXAGON_BORDER_WIDTH
                }
            }
        }
    }
    fun drawField(canvas: Canvas, paint: Paint, hexagonOrientation: Int = Hexagon.HORIZONTAL_HEXAGONS) {
        countConstants(hexagonOrientation)
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> drawHorizontalHexagons(canvas, paint)
            Hexagon.VERTICAL_HEXAGONS -> drawVerticalHexagons(canvas, paint)
        }
    }
    private fun drawHorizontalHexagons(canvas: Canvas, paint: Paint) {
        var totalVerticalOffset: Float = INIT_HEXAGON_OFFSET

        var currentHorizontalOffset = 0f
        var currentVerticalOffset: Float

        for (hexagonsRow in field) {
            for ((j, hexagon) in hexagonsRow.withIndex()) {
                currentVerticalOffset = if (j % 2 != 0)
                    verticalOffset
                else
                    0f
                currentHorizontalOffset += horizontalOffset
                hexagon?.apply {
                    centerX = currentHorizontalOffset
                    centerY = currentVerticalOffset + totalVerticalOffset
                    drawHexagon(canvas, paint, Hexagon.HORIZONTAL_HEXAGONS, true)
                }
            }
            currentHorizontalOffset = 0f
            totalVerticalOffset += verticalOffset * 2
        }
    }
    private fun drawVerticalHexagons(canvas: Canvas, paint: Paint) {
        var totalVerticalOffset: Float = INIT_HEXAGON_OFFSET
        var totalHorizontalOffset: Float = INIT_HEXAGON_OFFSET

        var currentHorizontalOffset = 0f

        for ((i, hexagonsRow) in field.withIndex()) {
            for (hexagon in hexagonsRow) {
                hexagon?.apply {
                    centerX = currentHorizontalOffset + totalHorizontalOffset
                    centerY = totalVerticalOffset
                    drawHexagon(canvas, paint, Hexagon.VERTICAL_HEXAGONS, true)
                }
                currentHorizontalOffset += horizontalOffset * 2
            }
            totalHorizontalOffset = if (i % 2 == 0)
                horizontalOffset + INIT_HEXAGON_OFFSET
            else
                INIT_HEXAGON_OFFSET
            currentHorizontalOffset = 0f
            totalVerticalOffset += verticalOffset
        }
    }
    private fun countConstants(hexagonOrientation: Int) {
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> {
                horizontalOffset = (3 / 2f) * hexagonRadius
                verticalOffset = (sqrt(3.0) / 2f).toFloat() * hexagonRadius
            }
            Hexagon.VERTICAL_HEXAGONS -> {
                horizontalOffset = (sqrt(3.0) / 2f).toFloat() * hexagonRadius
                verticalOffset = (3 / 2f) * hexagonRadius
            }
        }
    }
}