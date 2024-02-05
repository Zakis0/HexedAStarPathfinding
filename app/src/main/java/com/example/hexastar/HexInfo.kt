package com.example.hexastar

import com.example.hexastar.Hexagons.Hexagon
import kotlin.math.abs
import kotlin.math.min

class HexInfo(
    var G: Float = 0f,
    var H: Float = 0f,
    var isPassable: Boolean,
) {
    var connection: Hexagon? = null
    var F = G + H

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

    override fun toString() = "\tG: $G,   H: $H,   F: $F\n"
    fun toStringWithConnection(): String = toString() +
            "ConnectedNode:\n" +
            "$connection\n"

    companion object {
        fun getDistance(curHexagon: Hexagon, targetHexagon: Hexagon): Float {
            val i1 = curHexagon.indexesInField.i
            val i2 = targetHexagon.indexesInField.i

            val j1 = curHexagon.indexesInField.j
            val j2 = targetHexagon.indexesInField.j

            val iDistance: Int = abs(i1 - i2)
            val jDistance: Int = abs(j1 - j2)

            val diagonalSteps: Int = min(iDistance, jDistance)

            val iRemainingDistance = iDistance - diagonalSteps
            val jRemainingDistance = jDistance - diagonalSteps

            return if (iRemainingDistance > 0)
                (iRemainingDistance / 2 + diagonalSteps).toFloat()
            else (jRemainingDistance + diagonalSteps).toFloat()
        }
//        fun getDistance(curHexagon: Hexagon, targetHexagon: Hexagon): Float {
//            val curHexagonCords = Cords(curHexagon.centerX, curHexagon.centerY)
//            val targetHexagonCords = Cords(targetHexagon.centerX, targetHexagon.centerY)
//            return curHexagonCords.getDistant(targetHexagonCords)
//        }
    }
}