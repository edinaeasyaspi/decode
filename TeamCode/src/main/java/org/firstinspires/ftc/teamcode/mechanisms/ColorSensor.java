package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * Java equivalent of the original Kotlin ColorSensor companion object.
 * Provides static helpers to read HSV from a RevColorSensorV3.
 */
public final class ColorSensor {
    private static final float[] hsvValues = new float[3];

    private ColorSensor() { /* utility class */ }

    public static HSV getColor(RevColorSensorV3 colorSensor) {
        if (colorSensor == null) {
            throw new IllegalArgumentException("colorSensor must not be null");
        }

        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsvValues);
        return new HSV(hsvValues[0], hsvValues[1], hsvValues[2]);
    }

    public static boolean isGreen(RevColorSensorV3 colorSensor) {
        HSV color = getColor(colorSensor);
        return color.getHue() >= 100f && color.getHue() <= 200f;
    }

    public static boolean isPurple(RevColorSensorV3 colorSensor) {
        HSV color = getColor(colorSensor);
        return color.getHue() >= 200f && color.getHue() <= 350f;
    }

    public static float hue(RevColorSensorV3 colorSensor) {
        return getColor(colorSensor).getHue();
    }
}

