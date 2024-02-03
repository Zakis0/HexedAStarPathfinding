package com.example.hexastar.Hexagons

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.hexastar.AStar.AStar
import com.example.hexastar.AStar.Node
import com.example.hexastar.FIELD_HEIGHT
import com.example.hexastar.FIELD_WIDTH
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

    init {
        paint.isAntiAlias = true
    }
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hexagonField.drawField(canvas, paint, Hexagon.HORIZONTAL_HEXAGONS)
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
        val x = event.x
        val y = event.y

        val touchedHexagon: Hexagon

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchedHexagon = touchCordsToHex(x, y)
                when (SEARCH_PATH_MODE) {
                    SearchPathMode.IMMEDIATELY -> {
                        changeParityOfClick()
                        if (isOddClick) {
                            setStartPoint(touchedHexagon)
                        }
                        else {
                            setEndPoint(touchedHexagon)
                            path = AStar.findPath(startNode, targetNode)
                            printPath()
                        }
                    }
                    SearchPathMode.CLICK_BY_CLICK -> {

                    }
                }
                invalidate()
            }
        }
        return true
    }
    private fun touchCordsToHex(x: Float, y: Float): Hexagon {
        return Hexagon()
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
