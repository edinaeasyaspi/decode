package org.firstinspires.ftc.teamcode.mechanisms

import android.graphics.Color
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.robotcore.hardware.NormalizedColorSensor

class ColorSensor() {
    companion object {
        private val hsvValues = FloatArray(3)

        fun getColor(colorSensor: RevColorSensorV3): HSV {
            val colors = colorSensor!!.getNormalizedColors()
            Color.colorToHSV(colors.toColor(), hsvValues)
            return HSV(hsvValues[0], hsvValues[1], hsvValues[2])
        }

        @JvmStatic
        fun isGreen(colorSensor: RevColorSensorV3): Boolean {
            val color = getColor(colorSensor);
            return color.hue >= 100 && color.hue <= 200
        }

        @JvmStatic fun isPurple(colorSensor: RevColorSensorV3): Boolean {
            val color = getColor(colorSensor);
            return color.hue >= 200 && color.hue <= 350
        }
    }
}

/*@JvmInline
value class HSV(val hue: Float, val saturation: Float, val value: Float){
    init{}
}*/ //<--Do we really need this comment
data class HSV(
    val hue: Float,
    val saturation: Float,
    val value: Float
)
