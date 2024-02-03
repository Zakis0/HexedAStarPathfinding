package com.example.hexastar.Hexagons

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.example.hexastar.DEBUG
import com.example.hexastar.HEXAGON_BORDER_WIDTH
import com.example.hexastar.HEX_CORDS_CALCULATION_DEBUG
import com.example.hexastar.HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS
import com.example.hexastar.VERTICAL_NEIGHBORS_INDEXES_OFFSETS
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt

class HexagonField(
    val fieldWidth: Int,
    val fieldHeight: Int,
    val hexagonRadius: Float,
    viewWidth: Int,
    viewHeight: Int,
) {
    val INIT_HEXAGON_OFFSET = hexagonRadius

    val field: Array<Array<Hexagon?>> = Array(fieldHeight) { Array(fieldWidth) { null } }
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
        fillFieldWithHexagon(hexagon)
    }
    fun fillFieldWithHexagon(hexagon: Hexagon) {
        field.forEachIndexed { i, hexagonsRow ->
            hexagonsRow.forEachIndexed { j, _ ->
                field[i][j] = Hexagon.getCopyOfHexagon(hexagon).apply {
                    radius = hexagonRadius
                    borderWidth = HEXAGON_BORDER_WIDTH
                }
            }
        }
    }
    private fun setHexesCords(hexagonOrientation: Int) {
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> setHorizontalCordsToHexes()
            Hexagon.VERTICAL_HEXAGONS -> setVerticalCordsToHexes()
        }
    }
    private fun setHorizontalCordsToHexes() {
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
                }
            }
            currentHorizontalOffset = 0f
            totalVerticalOffset += verticalOffset * 2
        }
    }
    private fun setVerticalCordsToHexes() {
        var totalVerticalOffset: Float = INIT_HEXAGON_OFFSET
        var totalHorizontalOffset: Float = INIT_HEXAGON_OFFSET

        var currentHorizontalOffset = 0f

        for ((i, hexagonsRow) in field.withIndex()) {
            for (hexagon in hexagonsRow) {
                hexagon?.apply {
                    centerX = currentHorizontalOffset + totalHorizontalOffset
                    centerY = totalVerticalOffset
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
    fun drawField(canvas: Canvas, paint: Paint, hexagonOrientation: Int = Hexagon.HORIZONTAL_HEXAGONS) {
        countConstants(hexagonOrientation)
        setHexesCords(hexagonOrientation)
        field.forEach { row ->
            row.forEach { hex ->
                hex?.drawHexagon(
                    canvas,
                    paint,
                    if (hexagonOrientation == Hexagon.HORIZONTAL_HEXAGONS) Hexagon.HORIZONTAL_HEXAGONS
                    else Hexagon.VERTICAL_HEXAGONS,
                    true
                )
            }
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
    private fun clamp(value: Int, minValue: Int, maxValue: Int) = max(min(value, maxValue), minValue)
    private fun getRoughIndexes(touchCords: Cords, hexagonOrientation: Int): Indexes {
        val roughIndexes = Indexes()
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> {
                roughIndexes.i = clamp(
                    round((touchCords.y - INIT_HEXAGON_OFFSET) / (2f * verticalOffset)).toInt(),
                    0,
                    fieldHeight - 1
                )
                roughIndexes.j = clamp(
                    round((touchCords.x - INIT_HEXAGON_OFFSET) / (horizontalOffset)).toInt(),
                    0,
                    fieldWidth-1
                )
            }
            Hexagon.VERTICAL_HEXAGONS -> {
                roughIndexes.i = clamp(
                    round((touchCords.y - INIT_HEXAGON_OFFSET) / (verticalOffset)).toInt(),
                    0,
                    fieldHeight - 1
                )
                roughIndexes.j = clamp(
                    round((touchCords.x - INIT_HEXAGON_OFFSET) / (2 * horizontalOffset)).toInt(),
                    0,
                    fieldWidth-1
                )
            }
        }
        return roughIndexes
    }
    fun getHexIndexesByCords(touchCords: Cords, hexagonOrientation: Int): Indexes {
        var closestHexagonsIndexes = Indexes.EMPTY_INDEXES

        countConstants(hexagonOrientation)
        setHexesCords(hexagonOrientation)

        val roughIndexes = getRoughIndexes(touchCords, hexagonOrientation)

        Log.d(HEX_CORDS_CALCULATION_DEBUG, "roughCords [${roughIndexes.i}, ${roughIndexes.j}]")

        var minDistant = Float.MAX_VALUE
        var curDistant: Float
        var curHexagon: Hexagon?

        getNeighborsList(Indexes(roughIndexes.i, roughIndexes.j), hexagonOrientation).forEach {
            if (field[it.i][it.j] != null) {
                curHexagon = Hexagon.getCopyOfHexagon(field[it.i][it.j]!!)
                curDistant = touchCords.getDistant(Cords(curHexagon!!.centerX, curHexagon!!.centerY))

                Log.e(HEX_CORDS_CALCULATION_DEBUG, "Index $it CenterCords [${curHexagon!!.centerX}, ${curHexagon!!.centerY}] TouchCords $touchCords Distant $curDistant")

                if (curDistant < minDistant) {
                    minDistant = curDistant
                    closestHexagonsIndexes = it
                }
            }
        }
        return closestHexagonsIndexes
    }
    private fun getNeighborsList(indexes: Indexes, hexagonOrientation: Int): List<Indexes> {
        val listOfNeighborsIndex = mutableListOf<Indexes>()
        var neighborsIndexes: Indexes
        val neighborsOffsetIndexesList: MutableList<Indexes>
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> neighborsOffsetIndexesList = HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS
            Hexagon.VERTICAL_HEXAGONS -> {
                neighborsOffsetIndexesList = VERTICAL_NEIGHBORS_INDEXES_OFFSETS
                neighborsOffsetIndexesList.add(Indexes(-1, if(indexes.i % 2 == 0) 1 else -1))
                neighborsOffsetIndexesList.add(Indexes(1, if(indexes.i % 2 == 0) 1 else -1))
            }
            else -> neighborsOffsetIndexesList = mutableListOf()
        }
        neighborsOffsetIndexesList.forEach {
            neighborsIndexes = indexes + it
            if (checkIsNewIndexesInField(neighborsIndexes)) {
                field[neighborsIndexes.i][neighborsIndexes.j]?.let {
                    listOfNeighborsIndex.add(Indexes(neighborsIndexes.i, neighborsIndexes.j))
                }
            }
        }
        Log.w(HEX_CORDS_CALCULATION_DEBUG, "NeighborsIndexes $listOfNeighborsIndex")
        return listOfNeighborsIndex
    }
    private fun checkIsNewIndexesInField(indexes: Indexes): Boolean {
        return indexes.i in 0..<fieldHeight && indexes.j in 0..<fieldWidth
    }
}