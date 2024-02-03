package com.example.hexastar.Hexagons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.hexastar.AStar.AStar
import com.example.hexastar.AStar.Node
import com.example.hexastar.DEBUG
import com.example.hexastar.FIELD_HEIGHT
import com.example.hexastar.FIELD_WIDTH
import com.example.hexastar.HEXAGON_BORDER_WIDTH
import com.example.hexastar.HEXAGON_ORIENTATION
import com.example.hexastar.HEXAGON_RADIUS
import com.example.hexastar.SEARCH_PATH_MODE
import com.example.hexastar.SearchPathMode

class HexagonFieldView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint()
    private lateinit var hexagonField: HexagonField

    private var isOddClick = false

    private lateinit var startNode: Node
    private lateinit var targetNode: Node
    private lateinit var path: MutableList<Node>

    var touchedHexagon: Hexagon? = null
    private var touchCircleCords = Cords()

    init {
        paint.isAntiAlias = true
    }
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hexagonField.drawField(canvas, paint, HEXAGON_ORIENTATION)
        canvas.drawCircle(touchCircleCords.x, touchCircleCords.y, hexagonField.hexagonRadius / 4f, paint.apply {
            color = Color.RED
        })
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
            MotionEvent.ACTION_MOVE -> {
                touchedHexagon?.hexagonColor = Color.GREEN
                touchCircleCords.x = touchCords.x
                touchCircleCords.y = touchCords.y
                touchedHexagonIndexes = hexagonField.getHexIndexesByCords(touchCords, HEXAGON_ORIENTATION)
                if (touchedHexagonIndexes != Indexes.EMPTY_INDEXES) {
                    touchedHexagon = hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]!!
                    touchedHexagon!!.hexagonColor = Color.YELLOW
                }
                invalidate()
            }
            MotionEvent.ACTION_DOWN -> {

//                touchedHexagon = hexagonField.field[touchedHexagonIndexes.i][touchedHexagonIndexes.j]
//                when (SEARCH_PATH_MODE) {
//                    SearchPathMode.IMMEDIATELY -> {
//                        changeParityOfClick()
//                        if (touchedHexagon != null) {
//                            if (isOddClick) {
//                                setStartPoint(touchedHexagon)
//                            } else {
//                                setEndPoint(touchedHexagon)
//                                path = AStar.findPath(startNode, targetNode)
//                                printPath()
//                            }
//                        }
//                    }
//                    SearchPathMode.CLICK_BY_CLICK -> {
//
//                    }
//                }
//                invalidate()
            }
        }
        return true
    }
    private fun setStartPoint(touchedHexagon: Hexagon) {

    }
    private fun setEndPoint(touchedHexagon: Hexagon) {

    }
    private fun printPath() {

    }
    private fun changeParityOfClick() {
        isOddClick = !isOddClick
    }
}
