package com.example.hexastar

import android.graphics.Color
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.Indexes

class FieldSize(val fieldWidth: Int, val fieldHeight: Int, val hexagonRadius: Float)
val horizontalFieldSizesList = listOf(
    FieldSize(6, 3, 230f),
    FieldSize(13, 6, 120f),
    FieldSize(27, 13, 60f),
    FieldSize(56, 27, 30f),
    FieldSize(113, 55, 15f)
)
//// Params
const val FIELD_SIZE = 1

const val TO_DRAW_TOUCH_CIRCLE_GENERAL = true

const val ENABLE_PATH_FINDING = true

const val DRAW_AUXILIARY_HEXES = false

val SEARCH_PATH_MODE = SearchPathMode.IMMEDIATELY

const val HEXAGON_ORIENTATION = Hexagon.HORIZONTAL_HEXAGONS

const val PERCENTAGE_OF_IMPASSABLE_HEXES = 0.3f

// Hex Field colors
const val TOUCH_CIRCLE_RADIUS_IS_N_PERCENT_OF_HEX_RADIUS = 0.3f
val TOUCH_CIRCLE_COLOR = Color.parseColor("#21001a")

val DEFAULT_HEXAGON_COLOR = Color.parseColor("#aca4ab")
val DEFAULT_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val TOUCH_HEXAGON_COLOR = Color.parseColor("#494949")
val TOUCH_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val IMPASSABLE_HEXAGON_COLOR = Color.parseColor("#2c2c2c")
val IMPASSABLE_HEXAGON_BORDER_COLOR = Color.parseColor("#7a7679")

// A Star colors
val START_HEXAGON_COLOR = Color.parseColor("#c76af2")
val START_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val PATH_HEXAGON_COLOR = Color.parseColor("#7979ff")
val PATH_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val TARGET_HEXAGON_COLOR = Color.parseColor("#8e0071")
val TARGET_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val NEIGHBOR_HEXAGON_COLOR = Color.parseColor("#00aa4d")
val NEIGHBOR_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

val PROCESSED_HEXAGON_COLOR = Color.parseColor("#aa7b00")
val PROCESSED_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

//// Constants
enum class SearchPathMode {
    IMMEDIATELY,
    STEP_BY_STEP,
}

val HORIZONTAL_NEIGHBORS_INDEXES_OFFSETS = listOf(
    Indexes(0, 0),
    Indexes(-1, 0),
    Indexes(1, 0),
    Indexes(0, -1),
    Indexes(0, 1),
)

val VERTICAL_NEIGHBORS_INDEXES_OFFSETS = listOf(
    Indexes(0, 0),
    Indexes(-1, 0),
    Indexes(1, 0),
    Indexes(0, -1),
    Indexes(0, 1),
)

val FIELD_WIDTH = horizontalFieldSizesList[FIELD_SIZE].fieldWidth
val FIELD_HEIGHT = horizontalFieldSizesList[FIELD_SIZE].fieldHeight

val HEXAGON_RADIUS: Float = horizontalFieldSizesList[FIELD_SIZE].hexagonRadius
val HEXAGON_BORDER_WIDTH: Float = HEXAGON_RADIUS * 0.1f

val NUM_OF_IMPASSABLE_HEXES = (FIELD_WIDTH * FIELD_HEIGHT * PERCENTAGE_OF_IMPASSABLE_HEXES).toInt()

const val DEBUG = "GlobalDebug"

const val HEX_CORDS_CALCULATION_DEBUG = "HexCordsDebug"
const val NEIGHBORS_DEBUG = "neighborsDebug"
const val A_STAR_DEBUG = "AStarDebug"