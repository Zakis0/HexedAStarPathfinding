package com.example.hexastar.Hexagons

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import com.example.hexastar.DEFAULT_HEXAGON_BORDER_COLOR
import com.example.hexastar.DEFAULT_HEXAGON_COLOR
import com.example.hexastar.HEXAGON_BORDER_WIDTH
import com.example.hexastar.HEX_CORDS_CALCULATION_DEBUG
import com.example.hexastar.HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS
import com.example.hexastar.IMPASSABLE_HEXAGON_BORDER_COLOR
import com.example.hexastar.IMPASSABLE_HEXAGON_COLOR
import com.example.hexastar.NEIGHBORS_DEBUG
import com.example.hexastar.NUM_OF_IMPASSABLE_HEXES
import com.example.hexastar.VERTICAL_NEIGHBORS_INDEXES_OFFSETS
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt

class HexagonField(
    val fieldWidth: Int,
    val fieldHeight: Int,
    val hexagonRadius: Float,
    val viewWidth: Int,
    val viewHeight: Int,
) {
    val INIT_HEXAGON_OFFSET = hexagonRadius

    val field: Array<Array<Hexagon?>> = Array(fieldHeight) { Array(fieldWidth) { null } }
    private var horizontalOffset: Float = 0f
    private var verticalOffset: Float = 0f

    init {
        fillFieldWithHexagon()
    }
    private fun makeRandomNHexesImpassable(n: Int) {
        val set = mutableSetOf<Indexes>()
        var newIndexes: Indexes
        while (set.size != n) {
            newIndexes = Indexes((0..< fieldHeight).random(), (0..< fieldWidth).random())
            if (set.add(newIndexes)) {
                field[newIndexes.i][newIndexes.j]?.apply {
                    hexInfo.isPassable = false
                    hexagonColor = IMPASSABLE_HEXAGON_COLOR
                    borderColor = IMPASSABLE_HEXAGON_BORDER_COLOR
                }
            }
        }
    }
    fun fillFieldWithHexagon() {
        field.forEachIndexed { i, hexagonsRow ->
            hexagonsRow.forEachIndexed { j, it ->
                field[i][j] = Hexagon()
                field[i][j]!!.apply {
                    hexagonColor = DEFAULT_HEXAGON_COLOR
                    borderColor = DEFAULT_HEXAGON_BORDER_COLOR
                    borderWidth = HEXAGON_BORDER_WIDTH
                    radius = hexagonRadius
                    indexesInField.i = i
                    indexesInField.j = j
                }
            }
        }
        makeRandomNHexesImpassable(NUM_OF_IMPASSABLE_HEXES)
    }
    fun fillFieldWithColor() {
        field.forEachIndexed { i, hexagonsRow ->
            hexagonsRow.forEachIndexed { j, it ->
                field[i][j]?.apply {
                    if (hexInfo.isPassable) {
                        hexagonColor = DEFAULT_HEXAGON_COLOR
                        borderColor = DEFAULT_HEXAGON_BORDER_COLOR
                    }
                    else {
                        hexagonColor = IMPASSABLE_HEXAGON_COLOR
                        borderColor = IMPASSABLE_HEXAGON_BORDER_COLOR
                    }
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
            totalHorizontalOffset = if(isEven(i))
                horizontalOffset + INIT_HEXAGON_OFFSET
            else
                INIT_HEXAGON_OFFSET
            currentHorizontalOffset = 0f
            totalVerticalOffset += verticalOffset
        }
    }
    fun drawField(canvas: Canvas, paint: Paint, hexagonOrientation: Int) {
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

        getNeighborsIndexesList(Indexes(roughIndexes.i, roughIndexes.j), hexagonOrientation).forEach {
            if (field[it.i][it.j] != null) {
                curHexagon = field[it.i][it.j]!!
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
    private fun getNeighborsIndexesList(indexes: Indexes, hexagonOrientation: Int): List<Indexes> {
        val listOfNeighborsIndex = mutableListOf<Indexes>()
        var neighborsIndexes: Indexes
        val neighborsOffsetIndexesList = mutableListOf<Indexes>()
        when (hexagonOrientation) {
            Hexagon.HORIZONTAL_HEXAGONS -> {
                neighborsOffsetIndexesList.addAll(HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS)
                neighborsOffsetIndexesList.add(Indexes(if(isEven(indexes.j)) -1 else 1, -1))
                neighborsOffsetIndexesList.add(Indexes(if(isEven(indexes.j)) -1 else 1, 1))
            }
            Hexagon.VERTICAL_HEXAGONS -> {
                neighborsOffsetIndexesList.addAll(VERTICAL_NEIGHBORS_INDEXES_OFFSETS)
                neighborsOffsetIndexesList.add(Indexes(-1, if(isEven(indexes.i)) -1 else 1))
                neighborsOffsetIndexesList.add(Indexes(1, if(isEven(indexes.i)) -1 else 1))
            }
        }
        Log.w(NEIGHBORS_DEBUG, "neighborsOffsetIndexesList ${neighborsOffsetIndexesList.size}")
        Log.e(NEIGHBORS_DEBUG, "indexes $indexes")
        neighborsOffsetIndexesList.forEach {
            neighborsIndexes = indexes + it
            Log.d(NEIGHBORS_DEBUG, "neighborsIndexes $neighborsIndexes")
            if (checkIsNewIndexesInField(neighborsIndexes)) {
                field[neighborsIndexes.i][neighborsIndexes.j]?.let {
                    listOfNeighborsIndex.add(Indexes(neighborsIndexes.i, neighborsIndexes.j))
                }
            }
        }
        Log.w(HEX_CORDS_CALCULATION_DEBUG, "NeighborsIndexes $listOfNeighborsIndex")
        return listOfNeighborsIndex
    }
    fun getNeighborsList(indexes: Indexes, hexagonOrientation: Int): List<Hexagon> {
        val neighborsList = mutableListOf<Hexagon>()
        val neighborsIndexesList = getNeighborsIndexesList(indexes, hexagonOrientation)
        neighborsIndexesList.forEach {
            field[it.i][it.j]?.let { hexagon -> neighborsList.add(hexagon) }
        }
        return neighborsList
    }
    private fun checkIsNewIndexesInField(indexes: Indexes): Boolean {
        return indexes.i in 0..<fieldHeight && indexes.j in 0..<fieldWidth
    }
    private fun isEven(value: Int) = value % 2 == 0
}