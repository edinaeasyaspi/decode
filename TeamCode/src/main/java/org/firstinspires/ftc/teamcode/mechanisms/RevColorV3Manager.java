package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * RevColorV3Manager - A class to manage Rev Color Sensor V3 functionality.
 */
public class RevColorV3Manager {
    //TODO: Tune the gain to optimize calibrated values.
    private float GAIN = 4.0f; // Sensor gain
    public boolean useRGB = true; // Flag to use RGB or HSV
    private float redAverage = 0;
    private float greenAverage = 0;
    private float blueAverage = 0;

    // default constructor.
    public RevColorV3Manager() {
    }

    // Allow selection of RGB or HSV.
    public RevColorV3Manager(boolean useRGB) {
        this.useRGB = useRGB;
    }

    public ArtifactCellManager.CELL_COLOR GetCellColor(RevColorSensorV3 sensor) {
        return useRGB ? getCellColorRGB(sensor) : getCellColorHSV(sensor);
    }

    private ArtifactCellManager.CELL_COLOR getCellColorHSV(RevColorSensorV3 sensor) {
        HSV hsv = getHSV(sensor);
        float hue = hsv.getHue();
        float saturation = hsv.getSaturation();
        float value = hsv.getValue();

        // Simple threshold-based color detection using HSV
        if (hue >= 85 && hue <= 150 && saturation > 0.4 && value > 0.2) {
            return ArtifactCellManager.CELL_COLOR.Green;
        } else if (hue >= 250 && hue <= 290 && saturation > 0.4 && value > 0.2) {
            return ArtifactCellManager.CELL_COLOR.Purple;
        } else {
            return ArtifactCellManager.CELL_COLOR.None;
        }
    }

    private ArtifactCellManager.CELL_COLOR getCellColorRGB(RevColorSensorV3 sensor) {
        NormalizedRGBA colors = getRGBA(sensor);
        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        // Simple threshold-based color detection
        if (green > red && green > blue && green > 0.075) {
            return ArtifactCellManager.CELL_COLOR.Green;
        } else if (green > red && green < blue && blue > 0.07) {
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
        // Compensate for gain and use low pass filter to smooth values.
        colors.red = lowPass(redAverage, colors.red / colors.alpha);
        redAverage = colors.red;
        colors.green = lowPass(greenAverage, colors.green / colors.alpha);
        greenAverage = colors.green;
        colors.blue = lowPass(blueAverage, colors.blue / colors.alpha);
        blueAverage = colors.blue;
        return colors;
    }

    // Get HSV values from the color sensor.
    public HSV getHSV(RevColorSensorV3 colorSensor) {
        int redAverage = 0;
        int greenAverage = 0;
        int blueAverage = 0;
        setSensorGain(colorSensor);
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float[] hsvValues = new float[3];
        // Convert the RGB values to HSV values with filter smoothing.
        android.graphics.Color.RGBToHSV((int) colors.red * 255,
                (int) colors.green * 255,
                (int) colors.blue * 255, hsvValues);

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

    protected float lowPass(float colorAverage, float colorSample) {
        // (0 - .99) Lower value results in stronger smoothing.
        final float FILTER_COEFFICIENT = .2F;
        // Used to filter out values that are way out of range. Tune for expected range.
        final float THRESHOLD = .1F;

        // Optional code to remove outliers.
        if (Math.abs(colorSample - colorAverage) > THRESHOLD) {
            colorSample = colorAverage;
        }

        colorAverage = ((1.0F - FILTER_COEFFICIENT) * (FILTER_COEFFICIENT + colorSample));
        return colorAverage;
    }

    protected float lowPassInt(int colorAverage, int colorSample) {
        // (0 - .99) Lower value results in stronger smoothing.
        final float FILTER_COEFFICIENT = .2F;
        // Used to filter out values that are way out of range. Tune for expected range.
        final int THRESHOLD = 100;

        // Optional code to remove outliers.
        if (Math.abs(colorSample - colorAverage) > THRESHOLD) {
            colorSample = colorAverage;
        }

        colorAverage = (int) ((1.0F - FILTER_COEFFICIENT) * (FILTER_COEFFICIENT + colorSample));
        return colorAverage;
    }

}
