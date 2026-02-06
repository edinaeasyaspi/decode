package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

/**
 * RevColorV3Manager - A class to manage Rev Color Sensor V3 functionality.
 */
public class RevColorV3Manager {
    //TODO: Tune the gain to optimize calibrated values.
    private float GAIN = 4.0f; // Sensor gain
    // Gain value for Low pass filter.
    // Lower values = more smoothing, but more lag.
    public static double lowPassGain = 0.9;
    public boolean useRGB = true; // Flag to use RGB or HSV
    private float redAverage = 0;
    private float greenAverage = 0;
    private float blueAverage = 0;
    private LowPassFilter lowPassFilterR = new LowPassFilter(lowPassGain);
    private LowPassFilter lowPassFilterG = new LowPassFilter(lowPassGain);
    private LowPassFilter lowPassFilterB = new LowPassFilter(lowPassGain);


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
        //TODO: Tune these thresholds based on testing in opmode SensorColor.
        if (green > 0.15 && red > 0.04 && blue > 0.12) {
            return ArtifactCellManager.CELL_COLOR.Green;
        } else if (blue > 0.11 && red > 0.065 && green > 0.07) {
            return ArtifactCellManager.CELL_COLOR.Purple;
        } else {
            return ArtifactCellManager.CELL_COLOR.None;
        }
    }

    // Used to filter all color sensor readings.
    private NormalizedRGBA applylowPassFilter(NormalizedRGBA colors) {
        // Compensate for gain and use low pass filter to smooth values.
        colors.red = (float) lowPassFilterR.estimate(colors.red);
        colors.green = (float) lowPassFilterG.estimate(colors.green);
        colors.blue = (float) lowPassFilterB.estimate(colors.blue);
        return colors;
    }

    /*
        Return Normalized RGBA values, with low pass filter applied.
     */
    public NormalizedRGBA getRGBA(RevColorSensorV3 colorSensor) {
        setSensorGain(colorSensor);
        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        colors.red = colors.red / colors.alpha;
        colors.green = colors.green / colors.alpha;
        colors.blue = colors.blue / colors.alpha;
        return applylowPassFilter(colors);
    }

    /*
        Returns HSV values, with low pass filter applied.
     */
    public HSV getHSV(RevColorSensorV3 colorSensor) {
        setSensorGain(colorSensor);
        NormalizedRGBA colors = applylowPassFilter(getRGBA(colorSensor));
        float[] hsvValues = new float[3];
        // Convert the RGB values to HSV values with filter smoothing.
        android.graphics.Color.RGBToHSV((int) colors.red * 255,
                (int) colors.green * 255,
                (int) colors.blue * 255, hsvValues);

        return new HSV(hsvValues[0], hsvValues[1], hsvValues[2]);
    }

    /*
        Return HSV values with the low pass filter applied.
     */
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

    public class LowPassFilter {
        protected double gain;
        protected double previousEstimate = 0;

        /**
         * gain of the low pass filter.
         * <p>
         * (0 < x < 1)
         * <p>
         * High values of A are smoother but have more phase lag, low values of A allow more noise but
         * will respond faster to quick changes in the measured state.
         *
         * @param gain Aforementioned Gain. (0 < x < 1)
         */
        public LowPassFilter(double gain) {
            this.gain = gain;
        }

        // Added to allow changes using dashboard.
        public void setGain(double gain) {
            this.gain = gain;
        }

        /**
         * Low Pass Filter estimate
         *
         * @param measurement current measurement
         * @return filtered value
         */
        public double estimate(double measurement) {
            if (measurement > 1) return previousEstimate;
            double estimate = gain * previousEstimate + (1 - gain) * measurement;
            previousEstimate = estimate;
            return estimate;
        }
    }
}
