/* Copyright (c) 2017-2020 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.mechanisms.HSV;
import org.firstinspires.ftc.teamcode.mechanisms.RevColorV3Manager;

/*
 * This OpMode shows how to use a color sensor in a generic
 * way, regardless of which particular make or model of color sensor is used. The OpMode
 * assumes that the color sensor is configured with a name of "sensor_color".
 *
 * There will be some variation in the values measured depending on the specific sensor you are using.
 *
 * If the color sensor supports adjusting the gain, you can increase the gain (a multiplier to make
 * the sensor report higher values) by holding down the A button on the gamepad, and decrease the
 * gain by holding down the B button on the gamepad. The AndyMark Proximity & Color Sensor does not
 * support this.
 *
 * If the color sensor has a light which is controllable from software, you can use the X button on
 * the gamepad to toggle the light on and off. The REV sensors don't support this, but instead have
 * a physical switch on them to turn the light on and off, beginning with REV Color Sensor V2. The
 * AndyMark Proximity & Color Sensor does not support this.
 *
 * If the color sensor also supports short-range distance measurements (usually via an infrared
 * proximity sensor), the reported distance will be written to telemetry. As of September 2025,
 * the only color sensors that support this are the ones from REV Robotics and the AndyMark
 * Proximity & Color Sensor. These infrared proximity sensor measurements are only useful at very
 * small distances, and are sensitive to ambient light and surface reflectivity. You should use a
 * different sensor if you need precise distance measurements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */
@TeleOp(name = "Sensor: Color", group = "test")
//@Disabled
public class SensorColor extends LinearOpMode {

    RobotHardware robot = new RobotHardware(this);
    /**
     * The colorSensor field will contain a reference to our color sensor hardware object
     */
    RevColorSensorV3 colorSensor;
    // Default gain value for Rev Color Sensor V3.
    float gain = 4.0f;
    RevColorV3Manager revColorV3Manager = new RevColorV3Manager();
    float[] hsvValues = new float[3];


    /**
     * The relativeLayout field is used to aid in providing interesting visual feedback
     * in this sample application; you probably *don't* need this when you use a color sensor on your
     * robot. Note that you won't see anything change on the Driver Station, only on the Robot Controller.
     */
    View relativeLayout;
    int numtoreturn = 0;

    /*
     * The runOpMode() method is the root of this OpMode, as it is in all LinearOpModes.
     * Our implementation here, though is a bit unusual: we've decided to put all the actual work
     * in the runSample() method rather than directly in runOpMode() itself. The reason we do that is
     * that in this sample we're changing the background color of the robot controller screen as the
     * OpMode runs, and we want to be able to *guarantee* that we restore it to something reasonable
     * and palatable when the OpMode ends. The simplest way to do that is to use a try...finally
     * block around the main, core logic, and an easy way to make that all clear was to separate
     * the former from the latter in separate methods.
     */
    @Override
    public void runOpMode() {
        // All hardware lives here.
        robot.init();

        // Get a reference to the RelativeLayout so we can later change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);
        while (opModeInInit()) {
            if (gamepad1.a) {
                colorSensor = robot.leftColorSensor;
            }
            if (gamepad1.b) {
                colorSensor = robot.centerColorSensor;
            }
            if (gamepad1.y) {
                colorSensor = robot.rightColorSensor;
            }
            telemetry.addData("Color Sensor", "Using %s", colorSensor.getDeviceName());
            telemetry.update();
        }
        try {
            waitForStart();
            while (opModeIsActive()) {
                runSample(); // actually execute the sample
            }
        } finally {
            // On the way out, *guarantee* that the background is reasonable. It doesn't actually start off
            // as pure white, but it's too much work to dig out what actually was used, and this is good
            // enough to at least make the screen reasonable again.
            // Set the panel back to the default color
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.WHITE);
                }
            });
        }
    }

    protected int runSample() {

        if (colorSensor instanceof SwitchableLight) {
            ((SwitchableLight) colorSensor).enableLight(true);
        }
        // Explain basic gain information via telemetry
        telemetry.addLine("Hold the A button on gamepad 1 to increase gain, or B to decrease it.\n");
        telemetry.addLine("Higher gain values mean that the sensor will report larger numbers for Red, Green, and Blue, and Value\n");
        telemetry.addLine("X toggles light.");
        // Update the gain value if either of the A or B gamepad buttons is being held
        if (gamepad1.a) {
            // Only increase the gain by a small amount, since this loop will occur multiple times per second.
            gain += 0.005;
        } else if (gamepad1.b && gain > 1) { // A gain of less than 1 will make the values smaller, which is not helpful.
            gain -= 0.005;
        }

        // Tell the sensor our desired gain value (normally you would do this during initialization,
        // not during the loop)
        colorSensor.setGain(gain);

        // Get the colors from the sensor
        NormalizedRGBA rgbColors = revColorV3Manager.getRGBA(colorSensor);
        HSV hsvColors = revColorV3Manager.getHSV(colorSensor);
        hsvValues = revColorV3Manager.getHSVArray(colorSensor);

        /* Use telemetry to display feedback on the driver station. We show the red, green, and blue
         * normalized values from the sensor (in the range of 0 to 1), as well as the equivalent
         * HSV (hue, saturation and value) values.
         * See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
         * for an explanation of HSV color. */

        telemetry.addLine()
                .addData("Red", "%.3f", rgbColors.red)
                .addData("Green", "%.3f", rgbColors.green)
                .addData("Blue", "%.3f", rgbColors.blue)
                .addData("Alpha", "%.3f", rgbColors.alpha);
        telemetry.addLine()
                .addData("Hue", "%.3f", hsvColors.getHue())
                .addData("Saturation", "%.3f", hsvColors.getSaturation())
                .addData("Value", "%.3f", hsvColors.getValue())
                .addData("argb Color", "%3", Color.HSVToColor(hsvValues));


        /* If this color sensor also has a distance sensor, display the measured distance.
         * Note that the reported distance is only useful at very close range, and is impacted by
         * ambient light and surface reflectivity. */
        if (colorSensor instanceof DistanceSensor) {
            telemetry.addData("Distance (cm)", "%.3f", colorSensor.getDistance(DistanceUnit.CM));
        }

        telemetry.update();

        // Change the Robot Controller's background color to match the color detected by the color sensor.
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.HSVToColor(hsvValues));
            }
        });
        return numtoreturn;
    }
}
