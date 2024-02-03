package com.example.hexastar.Hexagons

class Indexes(var i: Int = 0, var j: Int = 0) {
    operator fun plus(otherIndexes: Indexes) = Indexes(i + otherIndexes.i, j + otherIndexes.j)
    override fun toString() = "[$i, $j]"
    companion object {
        val EMPTY_INDEXES = Indexes(-1, -1)
    }
}