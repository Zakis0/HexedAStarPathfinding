package com.example.hexastar

import android.content.ClipboardManager
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.hexastar.Hexagons.Hexagon
import com.example.hexastar.Hexagons.HexagonField
import com.example.hexastar.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.hexagonField.activity = this
        setInitSwitchesChecks()

        mainBinding.percentOfImpassableHexesSeekBar.progress =
            (Params.PERCENTAGE_OF_IMPASSABLE_HEXES * 100).toInt()
        mainBinding.percentOfImpassableHexesSeekBar.
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                Params.PERCENTAGE_OF_IMPASSABLE_HEXES = progress / 100f
                mainBinding.hexagonField.hexagonField = HexagonField.createHexagonFieldByViewSize(
                    mainBinding.hexagonField.measuredWidth,
                    mainBinding.hexagonField.measuredHeight,
                    Params.HEXAGON_RADIUS
                )
                mainBinding.hexagonField.invalidate()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mainBinding.hexRadiusSeekBar.progress = countHexRadiusSeekBarProgress()
        mainBinding.hexRadiusSeekBar.
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?, progress: Int, fromUser: Boolean
            ) {
                Params.HEXAGON_RADIUS = hexRadiusSeekBarProgressToHexRadius(progress)
                mainBinding.hexagonField.hexagonField = HexagonField.createHexagonFieldByViewSize(
                    mainBinding.hexagonField.measuredWidth,
                    mainBinding.hexagonField.measuredHeight,
                    Params.HEXAGON_RADIUS
                )
                mainBinding.hexagonField.invalidate()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mainBinding.hexOrientationSwitch.setOnCheckedChangeListener { _, _ ->
            switchHexOrientation()
        }
        mainBinding.pathFindingModeSwitch.setOnCheckedChangeListener { _, _ ->
            switchPathFindingMode()
        }
        mainBinding.drawTouchCircleSwitch.setOnCheckedChangeListener { _, _ ->
            drawTouchCircleSwitch()
        }
        mainBinding.drawAuxiliaryHexesSwitch.setOnCheckedChangeListener { _, _ ->
            drawAuxiliaryHexesSwitch()
        }
        mainBinding.drawConnectionLinesSwitch.setOnCheckedChangeListener { _, _ ->
            drawConnectionLinesSwitch()
        }
        mainBinding.showAStarValuesSwitch.setOnCheckedChangeListener { _, _ ->
            showAStarValuesSwitch()
        }
        mainBinding.useFieldSeedBtn.setOnClickListener {
            useSeedFromClipboard()
        }
        mainBinding.copyFieldSeedBtn.setOnClickListener {
            copySeedToClipboard()
        }
    }
    private fun countHexRadiusSeekBarProgress(): Int {
        return ((Params.HEXAGON_RADIUS - HEXAGON_RADIUS_MIN) / (HEXAGON_RADIUS_MAX - HEXAGON_RADIUS_MIN) * 100).roundToInt()
    }
    private fun hexRadiusSeekBarProgressToHexRadius(progress: Int): Float {
        return (HEXAGON_RADIUS_MAX - HEXAGON_RADIUS_MIN) * (progress / 100f) + HEXAGON_RADIUS_MIN
    }
    private fun switchHexOrientation() {
        when (Params.HEXAGON_ORIENTATION) {
            Hexagon.VERTICAL_HEXAGONS -> {
                mainBinding.hexOrientationSwitch.text =
                    resources.getString(R.string.hexagonOrientationHorizontal)
                Params.HEXAGON_ORIENTATION = Hexagon.HORIZONTAL_HEXAGONS
            }
            Hexagon.HORIZONTAL_HEXAGONS -> {
                mainBinding.hexOrientationSwitch.text =
                    resources.getString(R.string.hexagonOrientationVertical)
                Params.HEXAGON_ORIENTATION = Hexagon.VERTICAL_HEXAGONS
            }
        }
        mainBinding.hexagonField.invalidate()
    }
    private fun switchPathFindingMode() {
        when (Params.SEARCH_PATH_MODE) {
            SearchPathMode.STEP_BY_STEP -> {
                mainBinding.pathFindingModeSwitch.text =
                    resources.getString(R.string.pathFindingModeImmediately)
                Params.SEARCH_PATH_MODE = SearchPathMode.IMMEDIATELY
            }
            SearchPathMode.IMMEDIATELY -> {
                mainBinding.pathFindingModeSwitch.text =
                    resources.getString(R.string.pathFindingModeStepByStep)
                Params.SEARCH_PATH_MODE = SearchPathMode.STEP_BY_STEP
            }
        }
    }
    private fun drawTouchCircleSwitch() {
        Params.DRAW_TOUCH_CIRCLE_GENERAL = !Params.DRAW_TOUCH_CIRCLE_GENERAL
        mainBinding.hexagonField.invalidate()
    }
    private fun drawAuxiliaryHexesSwitch() {
        Params.DRAW_AUXILIARY_HEXES = !Params.DRAW_AUXILIARY_HEXES
        mainBinding.hexagonField.invalidate()
    }
    private fun drawConnectionLinesSwitch() {
        Params.DRAW_CONNECTION_LINES = !Params.DRAW_CONNECTION_LINES
        mainBinding.hexagonField.invalidate()
    }
    private fun showAStarValuesSwitch() {
        Params.SHOW_A_STAR_VALUES = !Params.SHOW_A_STAR_VALUES
        mainBinding.hexagonField.invalidate()
    }
    private fun useSeedFromClipboard() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val seed = clipboard.text.toString()
        val seedData = HexagonField.parseSeed(seed)
        if (seedData == SeedData.EMPTY) {
            return
        }
        val percentOfImpassableHexesSeekBarProgress = (((seedData.numOfImpassableHexes).toFloat()
                / (seedData.fieldWidth * seedData.fieldHeight)
                ) * 100).toInt()

        Params.HEXAGON_RADIUS = seedData.hexagonRadius
        Params.PERCENTAGE_OF_IMPASSABLE_HEXES =
            percentOfImpassableHexesSeekBarProgress / 100f

        mainBinding.hexRadiusSeekBar.progress = countHexRadiusSeekBarProgress()
        mainBinding.percentOfImpassableHexesSeekBar.progress =
            percentOfImpassableHexesSeekBarProgress

        mainBinding.hexagonField.hexagonField = HexagonField.createHexagonFieldBySeedData(seedData)

        mainBinding.hexagonField.resetHexesColors()
        mainBinding.hexagonField.invalidate()
    }
    private fun copySeedToClipboard() {
        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.text = mainBinding.hexagonField.hexagonField.getFieldSeed()
    }
    private fun setInitSwitchesChecks() {
        mainBinding.drawTouchCircleSwitch.isChecked = Params.DRAW_TOUCH_CIRCLE_GENERAL
        mainBinding.drawAuxiliaryHexesSwitch.isChecked = Params.DRAW_AUXILIARY_HEXES
        mainBinding.drawConnectionLinesSwitch.isChecked = Params.DRAW_CONNECTION_LINES
        mainBinding.showAStarValuesSwitch.isChecked = Params.SHOW_A_STAR_VALUES
        switchPathFindingMode()
        switchPathFindingMode()
        switchHexOrientation()
        switchHexOrientation()
    }

    fun changeEnablementOfSettings() {
        mainBinding.hexRadiusSeekBar.isEnabled = !mainBinding.hexRadiusSeekBar.isEnabled
        mainBinding.percentOfImpassableHexesSeekBar.isEnabled = !mainBinding.percentOfImpassableHexesSeekBar.isEnabled

        mainBinding.pathFindingModeSwitch.isEnabled = !mainBinding.pathFindingModeSwitch.isEnabled
        mainBinding.hexOrientationSwitch.isEnabled = !mainBinding.hexOrientationSwitch.isEnabled
        mainBinding.drawAuxiliaryHexesSwitch.isEnabled = !mainBinding.drawAuxiliaryHexesSwitch.isEnabled

        mainBinding.useFieldSeedBtn.isEnabled = !mainBinding.useFieldSeedBtn.isEnabled
        mainBinding.copyFieldSeedBtn.isEnabled = !mainBinding.copyFieldSeedBtn.isEnabled
    }
}