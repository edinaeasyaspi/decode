package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * RevColorV3Manager - A class to manage Rev Color Sensor V3 functionality.
 */
public class RevColorV3Manager {
    RevColorSensorV3 colorSensor;
    //TODO: Tune the gain to optimize calibrated values.float
    private float GAIN = 4.0f; // Sensor gain

    /*
        Return the Normalized RGBA values from the color sensor.
     */
    public NormalizedRGBA getRGBA(RevColorSensorV3 colorSensor) {
        setSensorGain(colorSensor);
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        // Compensate for gain
        colors.red = colors.red / colors.alpha;
        colors.green = colors.green / colors.alpha;
        colors.blue = colors.blue / colors.alpha;
        return colors;

    }

    public HSV getHSV(RevColorSensorV3 colorSensor) {
        setSensorGain(colorSensor);
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float[] hsvValues = new float[3];
        // Convert the RGB values to HSV values
        android.graphics.Color.RGBToHSV(
                (int) (colors.red * 255),
                (int) (colors.green * 255),
                (int) (colors.blue * 255),
                hsvValues);
        return new HSV(hsvValues[0], hsvValues[1], hsvValues[2]);
    }

    private RevColorSensorV3 setSensorGain(RevColorSensorV3 colorSensor) {
        colorSensor.setGain(GAIN);
        return colorSensor;
    }
}
