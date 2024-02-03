package com.example.hexastar.Hexagons

import kotlin.math.pow
import kotlin.math.sqrt

class Cords(var x: Float = 0f, var y: Float = 0f) {
    operator fun minus(otherCords: Cords) = Cords(x - otherCords.x, y - otherCords.y)
    fun getDistant(otherCords: Cords) = sqrt((x - otherCords.x).pow(2) + (y - otherCords.y).pow(2))
    override fun toString() = "[$x, $y]"
}
