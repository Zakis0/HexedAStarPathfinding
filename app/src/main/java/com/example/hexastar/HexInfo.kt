package com.example.hexastar

import com.example.hexastar.Hexagons.Hexagon
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class HexInfo(
    var G: Float = 0f,
    var H: Float = 0f,
    var isPassable: Boolean = true,
) {
    var connection: Hexagon? = null
    var F = G + H

    var type: NodeTypes = NodeTypes.NULL

    fun set_Connection(node: Hexagon) { connection = node }
    fun set_G(g: Float) {
        G = g
        updateF()
    }
    fun set_H(h: Float) {
        H = h
        updateF()
    }
    private fun updateF() { F = G + H }

    fun reset() {
        G = 0.0f
        H = 0.0f
        F = 0.0f
        type = NodeTypes.NULL
        connection = null
    }

    override fun toString() = "\tG: $G,   H: $H,   F: $F\n"
    fun toStringWithConnection(): String = toString() +
            "ConnectedNode:\n" +
            "$connection\n"
    companion object {
        fun getDistance(curHexagon: Hexagon, targetHexagon: Hexagon): Float {
            var i = targetHexagon.indexesInField.i - curHexagon.indexesInField.i
            var j = targetHexagon.indexesInField.j - curHexagon.indexesInField.j

            when (Params.HEXAGON_ORIENTATION) { // x < 0 and odd
                Hexagon.HORIZONTAL_HEXAGONS -> i -= floor((j + abs(curHexagon.indexesInField.j) % 2) / 2f).toInt()
                Hexagon.VERTICAL_HEXAGONS -> j -= floor((i + abs(curHexagon.indexesInField.i) % 2) / 2f).toInt()
            }
            return max(max(abs(i), abs(j)), abs(i + j)).toFloat()
        }
//        fun getDistance(curHexagon: Hexagon, targetHexagon: Hexagon): Float {
//            val i1 = curHexagon.indexesInField.i
//            val i2 = targetHexagon.indexesInField.i
//
//            val j1 = curHexagon.indexesInField.j
//            val j2 = targetHexagon.indexesInField.j
//
//            val iDistance: Int = abs(i1 - i2)
//            val jDistance: Int = abs(j1 - j2)
//
//            val diagonalSteps: Int = min(iDistance, jDistance)
//
//            val iRemainingDistance = iDistance - diagonalSteps
//            val jRemainingDistance = jDistance - diagonalSteps
//
//            return if (iRemainingDistance > 0)
//                (iRemainingDistance / 2 + diagonalSteps).toFloat()
//            else (jRemainingDistance + diagonalSteps).toFloat()
//        }
    }
}