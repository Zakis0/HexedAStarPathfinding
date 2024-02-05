package com.example.hexastar.Hexagons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.hexastar.AStar.AStar
import com.example.hexastar.AStar.Heap
import com.example.hexastar.A_STAR_DEBUG
import com.example.hexastar.ENABLE_PATH_FINDING
import com.example.hexastar.TARGET_HEXAGON_BORDER_COLOR
import com.example.hexastar.TARGET_HEXAGON_COLOR
import com.example.hexastar.FIELD_HEIGHT
import com.example.hexastar.FIELD_WIDTH
import com.example.hexastar.HEXAGON_ORIENTATION
import com.example.hexastar.HEXAGON_RADIUS
import com.example.hexastar.TOUCH_HEXAGON_BORDER_COLOR
import com.example.hexastar.TOUCH_HEXAGON_COLOR
import com.example.hexastar.PATH_HEXAGON_BORDER_COLOR
import com.example.hexastar.PATH_HEXAGON_COLOR
import com.example.hexastar.TO_DRAW_TOUCH_CIRCLE_GENERAL
import com.example.hexastar.TOUCH_CIRCLE_RADIUS_IS_N_PERCENT_OF_HEX_RADIUS
import com.example.hexastar.SEARCH_PATH_MODE
import com.example.hexastar.START_HEXAGON_BORDER_COLOR
import com.example.hexastar.START_HEXAGON_COLOR
import com.example.hexastar.SearchPathMode
import com.example.hexastar.TOUCH_CIRCLE_COLOR

class HexagonFieldView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint()
    private lateinit var hexagonField: HexagonField

    private var isOddClick = false

    private var startNode: Hexagon? = null
    private var targetNode: Hexagon? = null
    private var path: MutableList<Hexagon>? = null

    private var touchedHexagon: Hexagon? = null
    private var touchedHexagonsLastColor: Int? = null
    private var touchedHexagonsLastColorBorder: Int? = null

    private var touchCircleCords = Cords()
    private var drawTouchCircle = false

    private var isTouchToClearPath = false

    private var isStepByStepPathFound = false
    private var startStepByStepPathFinding = false

    private var toSearch: MutableList<Hexagon>? = null
    private var processed: MutableList<Hexagon>? = null
    private var heap: Heap<Hexagon>? = null

    init {
        paint.isAntiAlias = true
    }
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hexagonField.drawField(canvas, paint, HEXAGON_ORIENTATION)
        if (TO_DRAW_TOUCH_CIRCLE_GENERAL &&
            drawTouchCircle &&
            !startStepByStepPathFinding
            ) {
            canvas.drawCircle(
                touchCircleCords.x,
                touchCircleCords.y,
                hexagonField.hexagonRadius * TOUCH_CIRCLE_RADIUS_IS_N_PERCENT_OF_HEX_RADIUS,
                paint.apply {
                    color = TOUCH_CIRCLE_COLOR
                }
            )
        }
    }
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        hexagonField = HexagonField(
            FIELD_WIDTH,
            FIELD_HEIGHT,
            HEXAGON_RADIUS,
            measuredWidth,
            measuredHeight
        )
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchCords = Cords(event.x, event.y)
        val touchedHexagonIndexes: Indexes
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawTouchCircle = true
            }
            MotionEvent.ACTION_MOVE -> {
                if (!startStepByStepPathFinding) {
                    setTouchCircleCords(touchCords)
                    touchedHexagonIndexes =
                        hexagonField.getHexIndexesByCords(touchCords, HEXAGON_ORIENTATION)
                    setbackColorOfLastTouchedHex()
                    changeColorOfTouchedHex(touchedHexagonIndexes)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                drawTouchCircle = false
                touchedHexagonIndexes = hexagonField.getHexIndexesByCords(touchCords, HEXAGON_ORIENTATION)
                if (touchedHexagonIndexes != Indexes.EMPTY_INDEXES) {
                    touchedHexagon = hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]!!
                }
                if (!ENABLE_PATH_FINDING ||
                    touchedHexagon == null ||
                    (!touchedHexagon!!.hexInfo.isPassable && !isTouchToClearPath)) {
                    setbackColorOfLastTouchedHex()
                    return true
                }
                if (!startStepByStepPathFinding) {
                    changeParityOfClick()
                }
                when (SEARCH_PATH_MODE) {
                    SearchPathMode.IMMEDIATELY -> pathFindImmediately()
                    SearchPathMode.STEP_BY_STEP -> pathFindStepByStep()
                }
            }
        }
        return true
    }
    private fun setStartPoint(touchedHexagon: Hexagon) {
        startNode = touchedHexagon
        startNode!!.hexagonColor = START_HEXAGON_COLOR
        startNode!!.borderColor = START_HEXAGON_BORDER_COLOR
    }
    private fun setEndPoint(touchedHexagon: Hexagon) {
        targetNode = touchedHexagon
        targetNode!!.hexagonColor = TARGET_HEXAGON_COLOR
        targetNode!!.borderColor = TARGET_HEXAGON_BORDER_COLOR
    }
    private fun printPath() {
        for (node in path!!) {
            if (node == targetNode) {
                continue
            }
            node.hexagonColor = PATH_HEXAGON_COLOR
            node.borderColor = PATH_HEXAGON_BORDER_COLOR
        }
    }
    private fun resetHexesColors() {
        hexagonField.fillFieldWithColor()
    }
    private fun changeParityOfClick() {
        isOddClick = !isOddClick
    }
    private fun pathFindImmediately() {
        if (isTouchToClearPath) {
            touchToClearPath()
            return
        }
        if (isOddClick) {
            resetHexesColors()
            setStartPoint(touchedHexagon!!)
            invalidate()
        }
        else {
            setEndPoint(touchedHexagon!!)

            toSearch = mutableListOf( startNode!! )
            processed = mutableListOf()
            path = mutableListOf()
            heap = Heap(hexagonField.fieldWidth * hexagonField.fieldHeight)

            path = AStar.findPath(
                this,
                hexagonField,
                startNode!!,
                targetNode!!,
                toSearch!!,
                processed!!,
                path!!,
                heap!!,
            )
            printPath()
            isTouchToClearPath = true
        }
    }
    private fun pathFindStepByStep() {
        if (isTouchToClearPath) {
            touchToClearPath()
            return
        }
        if (!startStepByStepPathFinding) {
            initStepByStepPathFinding()
        }
        else {
            isStepByStepPathFound = AStar.doStepOfFindingPath(
                hexagonField,
                startNode!!,
                targetNode!!,
                toSearch!!,
                processed!!,
                path!!,
                heap!!,
            )
            invalidate()
        }
        if (isStepByStepPathFound) {
            printPath()
            isTouchToClearPath = true
            isStepByStepPathFound = false
            startStepByStepPathFinding = false
        }
    }
    private fun initStepByStepPathFinding() {
        if (isOddClick) {
            resetHexesColors()
            setStartPoint(touchedHexagon!!)
            invalidate()
        }
        else {
            setEndPoint(touchedHexagon!!)
            Log.e(A_STAR_DEBUG, "StartNode: ${startNode!!.indexesInField}\n" +
                    "TargetNode: ${targetNode!!.indexesInField}")
            invalidate()
            toSearch = mutableListOf( startNode!! )
            processed = mutableListOf()
            path = mutableListOf()
            heap = Heap(hexagonField.fieldWidth * hexagonField.fieldHeight)
            heap!!.add(startNode!!)
            startStepByStepPathFinding = true
        }
    }
    private fun setbackColorOfLastTouchedHex() {
        if (touchedHexagon != null &&
            touchedHexagon != startNode &&
            touchedHexagon != targetNode
        ) {
            touchedHexagon?.hexagonColor = touchedHexagonsLastColor!!
            touchedHexagon?.borderColor = touchedHexagonsLastColorBorder!!
        }
    }
    private fun changeColorOfTouchedHex( touchedHexagonIndexes: Indexes) {
        if (touchedHexagonIndexes != Indexes.EMPTY_INDEXES) {
            touchedHexagon =
                hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]!!
            touchedHexagonsLastColor = touchedHexagon!!.hexagonColor
            touchedHexagonsLastColorBorder = touchedHexagon!!.borderColor
            touchedHexagon!!.hexagonColor = TOUCH_HEXAGON_COLOR
            touchedHexagon!!.borderColor = TOUCH_HEXAGON_BORDER_COLOR
        }
    }
    private fun setTouchCircleCords(touchCords: Cords) {
        touchCircleCords.x = touchCords.x
        touchCircleCords.y = touchCords.y
    }
    private fun touchToClearPath() {
        resetHexesColors()
        isTouchToClearPath = false
        changeParityOfClick()
    }
}
