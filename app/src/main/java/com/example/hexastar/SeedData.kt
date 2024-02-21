package com.example.hexastar

import com.example.hexastar.Hexagons.Indexes

class SeedData(
    val hexagonRadius: Float,
    val fieldWidth: Int,
    val fieldHeight: Int,
    val numOfNullHexes: Int,
    val numOfImpassableHexes: Int,
    val listOfNullHexes: MutableList<Indexes>,
    val listOfImpassableHexes: MutableList<Indexes>,
) {
    companion object {
        val EMPTY = SeedData(
            -1f,
            -1,
            -1,
            -1,
            -1,
            mutableListOf(),
            mutableListOf()
        )
    }
}
