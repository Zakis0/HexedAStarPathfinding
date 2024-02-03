package com.example.hexastar

import android.graphics.Color

// Params
const val FIELD_WIDTH = 20
const val FIELD_HEIGHT = 27

const val HEXAGON_RADIUS = 50f
const val HEXAGON_BORDER_WIDTH: Float = 5f

val SEARCH_PATH_MODE = SearchPathMode.IMMEDIATELY

val DEFAULT_HEXAGON_COLOR = Color.parseColor("#FF9122")
val DEFAULT_HEXAGON_BORDER_COLOR = Color.parseColor("#FFFFFF")

// Constants

enum class SearchPathMode {
    IMMEDIATELY,
    CLICK_BY_CLICK,
}

const val DEBUG = "MyLog"