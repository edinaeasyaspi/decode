package org.firstinspires.ftc.teamcode.mechanisms;

/**
 * Simple immutable holder for HSV color components.
 */
public final class HSV {
    private final float hue;
    private final float saturation;
    private final float value;

    public HSV(float hue, float saturation, float value) {
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public float getHue() {
        return hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "HSV{" +
                "hue=" + hue +
                ", saturation=" + saturation +
                ", value=" + value +
                '}';
    }
}

