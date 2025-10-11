package org.firstinspires.ftc.teamcode.mechanisms

import android.graphics.Color
import com.qualcomm.robotcore.hardware.NormalizedColorSensor

class ColorSensor(var colorSensor: NormalizedColorSensor? = null) {
    private val hsvValues = FloatArray(3)

    fun getColor(): HSV {
        val colors = colorSensor!!.getNormalizedColors()
        Color.colorToHSV(colors.toColor(), hsvValues)
        return HSV(hsvValues[0], hsvValues[1], hsvValues[2])
    }
}

/*@JvmInline
value class HSV(val hue: Float, val saturation: Float, val value: Float){
    init{}
}*/
data class HSV(
    val hue: Float,
    val saturation: Float,
    val value: Float
)
