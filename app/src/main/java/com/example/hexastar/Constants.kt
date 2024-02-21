package com.example.hexastar

import android.graphics.Color
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.Indexes

// TODO BUGS
// Drawing
// TODO if click on impassable hexes in step mode they color sets to start`s color
// TODO if click on impassable hexes they color disappear when search starts
// TODO when changing PathFindingMode and etc. in HexagonFieldView calling onLayout
//  because text of switch is changing, so field is regenerating
//// if hover start and target hexes them initial color do not back
//// when path found duplicate color of target hex at place of last tap
//// color blink at the place of tap in touch to clear mode when starting new path finding
// TODO hexes color do not clear at place of second touch when multiple touches
// TODO draw connection line at place of second touch when multiple touches
// A Star
//// on the bag field path is not shortest because connections lines do not changes

// TODO FEATURES
// TODO Map creator
////  (hex size scroll, hexMode switcher, imitatively stepByStep switcher)
// TODO Map zooming
// TODO Favourite settings
// TODO Logarithmic scale for radius
//// Print G, H, F in hexes
////  Field seed (copy (Copying to copybuffer) and field to past and button Generate)

// Field params
val INIT_SEARCH_PATH_MODE = SearchPathMode.IMMEDIATELY
val INIT_HEXAGON_ORIENTATION = Hexagon.HORIZONTAL_HEXAGONS

const val NUMBERS_DISTANT_OFFSET = 0.6f
const val NUMBERS_VERTICAL_OFFSET = 0.3f
const val NUMBERS_F_VERTICAL_OFFSET = 0.6f

// Sizes
const val HEXAGON_RADIUS_MIN = 10f
const val HEXAGON_RADIUS_MAX = 240f

const val HEXAGON_BORDER_WIDTH_PERCENTAGE_OF_HEX_RADIUS: Float = 0.1f
const val CONNECTION_LINE_WIDTH_PERCENTAGE_OF_HEX_RADIUS = 0.2f
const val TOUCH_CIRCLE_RADIUS_PERCENTAGE_OF_HEX_RADIUS = 0.3f

// Colors
val TOUCH_CIRCLE_COLOR = Color.parseColor("#21001a")

val CONNECTION_LINE_COLOR = Color.parseColor("#c8001c")

val NUMBERS_COLOR = Color.parseColor("#000000")

val DEFAULT_HEXAGON_COLOR = Color.parseColor("#bcb4bb")
val DEFAULT_HEXAGON_BORDER_COLOR = Color.parseColor("#868285")

val HOVERED_HEXAGON_COLOR = Color.parseColor("#595959")
val HOVERED_HEXAGON_BORDER_COLOR = Color.parseColor("#000000")

val IMPASSABLE_HEXAGON_COLOR = Color.parseColor("#1c1c1c")
val IMPASSABLE_HEXAGON_BORDER_COLOR = Color.parseColor("#4a4649")

// A Star colors
val START_HEXAGON_COLOR = Color.parseColor("#c76af2")
val START_HEXAGON_BORDER_COLOR = Color.parseColor("#633571")

val PATH_HEXAGON_COLOR = Color.parseColor("#7979ff")
val PATH_HEXAGON_BORDER_COLOR = Color.parseColor("#404080")

val TARGET_HEXAGON_COLOR = Color.parseColor("#8e0071")
val TARGET_HEXAGON_BORDER_COLOR = Color.parseColor("#480030")

val NEIGHBOR_HEXAGON_COLOR = Color.parseColor("#00aa4d")
val NEIGHBOR_HEXAGON_BORDER_COLOR = Color.parseColor("#005526")

val PROCESSED_HEXAGON_COLOR = Color.parseColor("#aa7b00")
val PROCESSED_HEXAGON_BORDER_COLOR = Color.parseColor("#553500")

// Seed params
const val SEED_DELIMITER = "_"

//// Constants
const val NUM_SEEDS_METADATA_ELEMENTS = 5

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

const val BUG_FIELD_SEED = "120.0_13_6_0_21_0_3_0_8_0_11_1_10_2_0_2_1_2_4_2_6_3_0_3_2_3_3_3_10_3_12_4_1_4_3_4_4_4_7_4_8_5_4_5_6_5_11"

const val DEBUG = "GlobalDebug"

const val HEX_CORDS_CALCULATION_DEBUG = "HexCordsDebug"
const val NEIGHBORS_DEBUG = "neighborsDebug"
const val A_STAR_DEBUG = "AStarDebug"
const val SEED_DEBUG = "SeedDebug"
