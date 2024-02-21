package com.example.hexastar.Hexagons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.hexastar.AStar.AStar
import com.example.hexastar.AStar.Heap
import com.example.hexastar.A_STAR_DEBUG
import com.example.hexastar.CONNECTION_LINE_COLOR
import com.example.hexastar.CONNECTION_LINE_WIDTH_PERCENTAGE_OF_HEX_RADIUS
import com.example.hexastar.DEFAULT_HEXAGON_BORDER_COLOR
import com.example.hexastar.DEFAULT_HEXAGON_COLOR
import com.example.hexastar.HOVERED_HEXAGON_BORDER_COLOR
import com.example.hexastar.HOVERED_HEXAGON_COLOR
import com.example.hexastar.HexInfo
import com.example.hexastar.MainActivity
import com.example.hexastar.NUMBERS_COLOR
import com.example.hexastar.NUMBERS_DISTANT_OFFSET
import com.example.hexastar.NUMBERS_F_VERTICAL_OFFSET
import com.example.hexastar.NUMBERS_VERTICAL_OFFSET
import com.example.hexastar.NodeTypes
import com.example.hexastar.PATH_HEXAGON_BORDER_COLOR
import com.example.hexastar.PATH_HEXAGON_COLOR
import com.example.hexastar.Params
import com.example.hexastar.SEED_DEBUG
import com.example.hexastar.START_HEXAGON_BORDER_COLOR
import com.example.hexastar.START_HEXAGON_COLOR
import com.example.hexastar.SearchPathMode
import com.example.hexastar.TARGET_HEXAGON_BORDER_COLOR
import com.example.hexastar.TARGET_HEXAGON_COLOR
import com.example.hexastar.TOUCH_CIRCLE_COLOR
import com.example.hexastar.TOUCH_CIRCLE_RADIUS_PERCENTAGE_OF_HEX_RADIUS
import kotlin.math.sqrt

class HexagonFieldView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint()
    lateinit var hexagonField: HexagonField

    var activity: MainActivity? = null

    private var startNode: Hexagon? = null
    private var targetNode: Hexagon? = null
    private var path: MutableList<Hexagon>? = null

    private var touchedHexagon: Hexagon? = null
    private var touchedHexagonsLastColor: Int? = null
    private var touchedHexagonsLastColorBorder: Int? = null

    private var touchCircleCords = Cords()
    private var drawTouchCircle = false

    private var isOddClick = false
    private var isFieldSetup = true

    private var isTouchToClean = false

    private var isStepByStepPathFound = false
    private var stepByStepPathFinding = false

    private var toSearch: MutableList<Hexagon>? = null
    private var processed: MutableList<Hexagon>? = null
    private var heap: Heap<Hexagon>? = null

    init {
        paint.isAntiAlias = true
    }
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hexagonField.drawField(canvas, paint, Params.HEXAGON_ORIENTATION)
        if (Params.DRAW_TOUCH_CIRCLE_GENERAL &&
            drawTouchCircle &&
            !stepByStepPathFinding
            ) {
            canvas.drawCircle(
                touchCircleCords.x,
                touchCircleCords.y,
                hexagonField.hexagonRadius * TOUCH_CIRCLE_RADIUS_PERCENTAGE_OF_HEX_RADIUS,
                paint.apply {
                    color = TOUCH_CIRCLE_COLOR
                }
            )
        }
        if (Params.DRAW_CONNECTION_LINES) {
            drawConnectionLines(canvas, paint)
        }
        if (Params.SHOW_A_STAR_VALUES) {
            drawHexesInfo(canvas, paint)
        }
    }
    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        generateField()
        Toast.makeText(context, "asd", Toast.LENGTH_SHORT).show()
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
                if (!stepByStepPathFinding
                    && !isTouchToClean
                    ) {
                    setTouchCircleCords(touchCords)
                    touchedHexagonIndexes =
                        hexagonField.getHexIndexesByCords(touchCords, Params.HEXAGON_ORIENTATION)
                    setbackColorOfLastTouchedHex()
                    changeColorOfTouchedHex(touchedHexagonIndexes)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                drawTouchCircle = false
                if (isTouchToClean) {
                    cleaningPhase()
                    return true
                }
                touchedHexagonIndexes =
                    hexagonField.getHexIndexesByCords(touchCords, Params.HEXAGON_ORIENTATION)
                if (touchedHexagonIndexes != Indexes.EMPTY_INDEXES) {
                    touchedHexagon =
                        hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]!!
                }
                if (!Params.ENABLE_PATH_FINDING ||
                    touchedHexagon == null ||
                    (
                        !stepByStepPathFinding &&
                        !touchedHexagon!!.hexInfo.isPassable
                    )
                ) {
                    setbackColorOfLastTouchedHex() // ?
                    return true
                }
                if (!stepByStepPathFinding) {
                    changeParityOfClick()
                }
                if (isFieldSetup) {
                    if (isOddClick) {
                        setStartPoint(touchedHexagon!!)
                        activity?.changeEnablementOfSettings()
                        invalidate()
                        return true
                    }
                    else {
                        setTargetPoint(touchedHexagon!!)
                        initPathFinding()
                        invalidate()
                        isFieldSetup = false
                    }
                }
                when (Params.SEARCH_PATH_MODE) {
                    SearchPathMode.IMMEDIATELY -> {
                        pathFindImmediately()
                    }
                    SearchPathMode.STEP_BY_STEP -> {
                        stepByStepPathFinding = true
                        pathFindStepByStep()
                    }
                }
            }
        }
        return true
    }
    private fun setStartPoint(touchedHexagon: Hexagon) {
        startNode = touchedHexagon
        startNode!!.hexInfo.type = NodeTypes.PATH
        startNode!!.hexagonColor = START_HEXAGON_COLOR
        startNode!!.borderColor = START_HEXAGON_BORDER_COLOR
        rememberHexagonColor()
    }
    private fun setTargetPoint(touchedHexagon: Hexagon) {
        targetNode = touchedHexagon
        targetNode!!.hexagonColor = TARGET_HEXAGON_COLOR
        targetNode!!.borderColor = TARGET_HEXAGON_BORDER_COLOR
        rememberHexagonColor()
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
    fun resetHexesColors() {
        hexagonField.fillFieldWithColor()
    }
    private fun changeParityOfClick() {
        isOddClick = !isOddClick
    }
    private fun pathFindImmediately() {
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
        endPathFinding()
    }
    private fun pathFindStepByStep() {
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
        if (isStepByStepPathFound) {
            endPathFinding()
        }
    }
    private fun initPathFinding() {
        Log.e(A_STAR_DEBUG, "StartNode: ${startNode!!.indexesInField}\n" +
                "TargetNode: ${targetNode!!.indexesInField}")
        toSearch = mutableListOf( startNode!! )
        processed = mutableListOf()
        path = mutableListOf()
        heap = Heap(hexagonField.fieldWidth * hexagonField.fieldHeight)
        heap!!.add(startNode!!)
    }
    private fun endPathFinding() {
        printPath()
        isTouchToClean = true
    }
    private fun setbackColorOfLastTouchedHex() {
        if (touchedHexagon != null) {
            touchedHexagon?.hexagonColor = touchedHexagonsLastColor!!
            touchedHexagon?.borderColor = touchedHexagonsLastColorBorder!!
        }
    }
    private fun changeColorOfTouchedHex( touchedHexagonIndexes: Indexes) {
        if (touchedHexagonIndexes != Indexes.EMPTY_INDEXES) {
            touchedHexagon =
                hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]!!
            rememberHexagonColor()
            touchedHexagon!!.hexagonColor = HOVERED_HEXAGON_COLOR
            touchedHexagon!!.borderColor = HOVERED_HEXAGON_BORDER_COLOR
        }
    }
    private fun rememberHexagonColor() {
        touchedHexagonsLastColor = touchedHexagon!!.hexagonColor
        touchedHexagonsLastColorBorder = touchedHexagon!!.borderColor
    }
    private fun resetRememberedHexagonColor() {
        touchedHexagonsLastColor = DEFAULT_HEXAGON_COLOR
        touchedHexagonsLastColorBorder = DEFAULT_HEXAGON_BORDER_COLOR
    }
    private fun setTouchCircleCords(touchCords: Cords) {
        touchCircleCords.x = touchCords.x
        touchCircleCords.y = touchCords.y
    }
    private fun cleaningPhase() {
        isTouchToClean = false
        resetHexesColors()
        clearConnectionLines()
        clearHexesInfo()
        resetRememberedHexagonColor()
        activity?.changeEnablementOfSettings()
        invalidate()
        isOddClick = false
        isFieldSetup = true
        isStepByStepPathFound = false
        stepByStepPathFinding = false
    }
    private fun drawConnectionLines(canvas: Canvas, paint: Paint) {
        val initPaintColor = paint.color
        val initPaintStrokeWidth = paint.strokeWidth
        paint.color = CONNECTION_LINE_COLOR
        paint.strokeWidth = hexagonField.hexagonRadius * CONNECTION_LINE_WIDTH_PERCENTAGE_OF_HEX_RADIUS
        hexagonField.field.forEach {
            it.forEach { hex ->
                if (hex?.hexInfo?.connection != null) {
                    canvas.drawLine(
                        hex.centerX,
                        hex.centerY,
                        hex.hexInfo.connection!!.centerX,
                        hex.hexInfo.connection!!.centerY,
                        paint
                    )
                }
            }
        }
        paint.color = initPaintColor
        paint.strokeWidth = initPaintStrokeWidth
    }
    private fun drawHexesInfo(canvas: Canvas, paint: Paint) {
        for (i in 0..< hexagonField.fieldHeight) {
            for (hex in hexagonField.field[i]) {
                if (hex == null ||
                    hex.hexInfo.type == NodeTypes.NULL ||
                    hex == startNode || hex == targetNode) {
                    continue
                }
                drawHexagonInfo(hex, canvas, paint)
            }
        }
    }
    private fun drawHexagonInfo(hexagon: Hexagon, canvas: Canvas, paint: Paint) {
        val G = hexagon.hexInfo.G.toInt().toString()
        val H = hexagon.hexInfo.H.toInt().toString()
        val F = hexagon.hexInfo.F.toInt().toString()

        val xMaxNumberDistant = 0.75f * hexagon.radius
        val yMaxNumberDistant = sqrt(3f) / 4f * hexagon.radius

        val gBound = Rect()
        paint.getTextBounds(G, 0, 1, gBound)
        val hBound = Rect()
        paint.getTextBounds(H, 0, 1, hBound)

        val GCords = Cords(
            hexagon.centerX * (1 - NUMBERS_DISTANT_OFFSET) + NUMBERS_DISTANT_OFFSET * (hexagon.centerX - xMaxNumberDistant + gBound.width()),
            hexagon.centerY * (1 - NUMBERS_DISTANT_OFFSET) + NUMBERS_DISTANT_OFFSET * (hexagon.centerY - yMaxNumberDistant + NUMBERS_VERTICAL_OFFSET* hexagon.radius + gBound.height())
        )
        val HCords = Cords(
            hexagon.centerX * (1 - NUMBERS_DISTANT_OFFSET) + NUMBERS_DISTANT_OFFSET * (hexagon.centerX + xMaxNumberDistant - hBound.width()),
            hexagon.centerY * (1 - NUMBERS_DISTANT_OFFSET) + NUMBERS_DISTANT_OFFSET * (hexagon.centerY - yMaxNumberDistant + NUMBERS_VERTICAL_OFFSET * hexagon.radius + hBound.height())
        )
        val FCords = Cords(
            hexagon.centerX,
            hexagon.centerY * (1 - NUMBERS_DISTANT_OFFSET) + NUMBERS_DISTANT_OFFSET * (hexagon.centerY + (2 * yMaxNumberDistant) * NUMBERS_F_VERTICAL_OFFSET + NUMBERS_VERTICAL_OFFSET * hexagon.radius)
        )
        val initPaintColor = paint.color
        val initPaintTextSize = paint.textSize
        paint.textSize = hexagon.radius * 0.4f
        paint.textAlign = Paint.Align.CENTER
        paint.color = NUMBERS_COLOR
        canvas.drawText(F, FCords.x, FCords.y, paint)
        canvas.drawText(G, GCords.x, GCords.y, paint)
        canvas.drawText(H, HCords.x, HCords.y, paint)
        paint.color = initPaintColor
        paint.textSize = initPaintTextSize
        paint.textAlign = Paint.Align.LEFT
    }
    private fun clearHexesInfo() {
        hexagonField.field.forEach { row ->
            row.forEach {
                it?.hexInfo?.reset()
            }
        }
    }
    private fun clearConnectionLines() {
        hexagonField.field.forEach {
            it.forEach { hex ->
                if (hex?.hexInfo?.connection != null) {
                    hex.hexInfo.connection = null
                }
            }
        }
    }

    private fun generateField() {
        hexagonField = if (Params.GENERATE_FIELD_BY_INIT_FIELD_SEED) {
            HexagonField.createHexagonFieldBySeed(Params.INIT_FIELD_SEED)
        }
        else {
            HexagonField.createHexagonFieldByViewSize(
                measuredWidth,
                measuredHeight,
                Params.HEXAGON_RADIUS
            )
        }
        resetHexesColors()
        Log.d(SEED_DEBUG, "Seed: ${hexagonField.getFieldSeed()}")
    }
}
