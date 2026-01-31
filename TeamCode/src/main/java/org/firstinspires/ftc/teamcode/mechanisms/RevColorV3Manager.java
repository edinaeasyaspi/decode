package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * RevColorV3Manager - A class to manage Rev Color Sensor V3 functionality.
 */
public class RevColorV3Manager {
    //TODO: Tune the gain to optimize calibrated values.
    private float GAIN = 4.0f; // Sensor gain

    public ArtifactCellManager.CELL_COLOR GetCellColor(RevColorSensorV3 sensor) {
        NormalizedRGBA colors = getRGBA(sensor);
        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        // Simple threshold-based color detection
        if (green > red && green > blue && green > 0.075) {
            return ArtifactCellManager.CELL_COLOR.Green;
        } else if (green > red && green< blue && blue > 0.07) {
            return ArtifactCellManager.CELL_COLOR.Purple;
        } else {
            return ArtifactCellManager.CELL_COLOR.None;
        }
    }

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

    // Get HSV values from the color sensor.
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

    // Get HSV as an array.
    public float[] getHSVArray(RevColorSensorV3 colorSensor) {
        NormalizedRGBA colors = getRGBA(colorSensor);
        float[] hsvValues = new float[3];
        // Convert the RGB values to HSV values
        android.graphics.Color.RGBToHSV(
                (int) (colors.red * 255),
                (int) (colors.green * 255),
                (int) (colors.blue * 255),
                hsvValues);
        return hsvValues;
    }

    // Set the gain for the color sensor
    public void setSensorGain(RevColorSensorV3 colorSensor) {
        colorSensor.setGain(GAIN);
    }
}
