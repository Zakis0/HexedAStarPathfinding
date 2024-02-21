package com.example.hexastar

import com.example.hexastar.Hexagons.Hexagon

object Params {
    val ENABLE_PATH_FINDING = true
    var SEARCH_PATH_MODE = INIT_SEARCH_PATH_MODE

    var DRAW_TOUCH_CIRCLE_GENERAL = true
    var DRAW_CONNECTION_LINES = false
    var DRAW_AUXILIARY_HEXES = true
    var SHOW_A_STAR_VALUES = false

    var HEXAGON_ORIENTATION = INIT_HEXAGON_ORIENTATION

    var PERCENTAGE_OF_IMPASSABLE_HEXES = 0.5f

    var HEXAGON_RADIUS: Float = 60f

    val GENERATE_FIELD_BY_INIT_FIELD_SEED = false
    var INIT_FIELD_SEED = "110.0_13_6_0_21_0_3_0_8_0_11_1_10_2_0_2_1_2_4_2_6_3_0_3_2_3_3_3_10_3_12_4_1_4_3_4_4_4_7_4_8_5_4_5_6_5_11"
}