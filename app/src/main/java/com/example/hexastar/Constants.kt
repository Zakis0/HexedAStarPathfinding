package com.example.hexastar

import android.graphics.Color
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.Indexes

// Params
const val FIELD_WIDTH = 20
const val FIELD_HEIGHT = 20

const val HEXAGON_RADIUS = 50f
const val HEXAGON_BORDER_WIDTH: Float = HEXAGON_RADIUS / 10f

val SEARCH_PATH_MODE = SearchPathMode.IMMEDIATELY
const val HEXAGON_ORIENTATION = Hexagon.VERTICAL_HEXAGONS

val DEFAULT_HEXAGON_COLOR = Color.parseColor("#FF9122")
val DEFAULT_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

// Constants
enum class SearchPathMode {
    IMMEDIATELY,
    STEP_BY_STEP,
}

val HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS = mutableListOf(
    Indexes(0, 0),
    Indexes(-1, 0),
    Indexes(1, 0),
    Indexes(0, -1),
    Indexes(0, 1),
    Indexes(-1, -1),
    Indexes(-1, 1),
)

val VERTICAL_NEIGHBORS_INDEXES_OFFSETS = mutableListOf(
    Indexes(0, 0),
    Indexes(-1, 0),
    Indexes(0, -1),
    Indexes(0, 1),
    Indexes(1, 0),
)

const val DEBUG = "MyLog"

const val HEX_CORDS_CALCULATION_DEBUG = "HexCordsDebug"