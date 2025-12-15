/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 *the following conditions are met:
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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.inputsys.Input;
import org.firstinspires.ftc.teamcode.inputsys.KeyCode;
import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.ColorSensor;
import org.firstinspires.ftc.teamcode.mechanisms.InOutSys;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;
import org.firstinspires.ftc.teamcode.mechanisms.ServoK;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.ToggleButtonReader;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * This OpMode is the main teleOp for Decode.
 */
@TeleOp(name = "Robot8034")
public class Robot8034 extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    final double DESIRED_DISTANCE = 24.0;
    private static final boolean USE_WEBCAM = true;
    private static final int DESIRED_TAG_ID = -1;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;

    private MotorEx frontLeftDrive;
    private MotorEx backLeftDrive;
    private MotorEx frontRightDrive;
    private MotorEx backRightDrive;
    private MecanumDrive mecanumDrive;
    private double SLOW_MODE_FACTOR = 0.4;

    private MotorEx leftIntakeMotor;
    private MotorEx rightIntakeMotor;
    private IntakeManager intakeManager;
    private MotorEx leftLaunchMotor;
    private MotorEx rightLaunchMotor;
    private LaunchManager launchManager;
    private boolean SHOOT_FAR = false;

    //TODO: Adjust shot variables as needed
    private final double SHORT_SHOT = 0.20;
    private final double LONG_SHOT = 0.25;

    private ServoEx leftCell;
    private ServoEx centerCell;
    private ServoEx rightCell;
    ArtifactCellManager cellManager;
    public ColorSensor colorSensorOne;
    public ColorSensor colorSensorTwo;
    public ColorSensor colorSensorThree;

    private CRServo launchServo;

    boolean isSlowMode = false;
    boolean isLauncherReady = false;

    GamepadEx gamePadEx;
    ToggleButtonReader aReader;
    TriggerReader leftTriggerReader;
    TriggerReader rightTriggerReader;

    @Override
    public void runOpMode() {
        boolean targetFound = false;
        double drive = 0;
        double strafe = 0;
        double turn = 0;
        initAprilTag();
        setManualExposure(6, 250);

        //Define gamepad (from SolversLib)
        gamePadEx = new GamepadEx(gamepad1);
        aReader = new ToggleButtonReader(gamePadEx, GamepadKeys.Button.A);
        leftTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.RIGHT_TRIGGER);

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration the DS.
        frontLeftDrive = new MotorEx(hardwareMap, "frontleftdrive", Motor.GoBILDA.RPM_435);
        backLeftDrive = new MotorEx(hardwareMap, "backleftdrive", Motor.GoBILDA.RPM_435);
        frontRightDrive = new MotorEx(hardwareMap, "frontrightdrive", Motor.GoBILDA.RPM_435);
        backRightDrive = new MotorEx(hardwareMap, "backrightdrive", Motor.GoBILDA.RPM_435);
        frontLeftDrive.setInverted(true);
        backLeftDrive.setInverted(true);
        frontRightDrive.setInverted(true);
        backRightDrive.setInverted(true);
        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        //TODO: device name consistency (the motor string start with front/back).
        leftIntakeMotor = new MotorEx(hardwareMap, "leftintakemotor", Motor.GoBILDA.RPM_435);
        rightIntakeMotor = new MotorEx(hardwareMap, "rightintakemotor", Motor.GoBILDA.RPM_435);
        intakeManager = new IntakeManager(leftIntakeMotor, rightIntakeMotor);

        //TODO: device name consistency (the motor string start with front/back).
        leftLaunchMotor = new MotorEx(hardwareMap, "leftlaunchmotor", Motor.GoBILDA.BARE);
        rightLaunchMotor = new MotorEx(hardwareMap, "rightlaunchmotor", Motor.GoBILDA.BARE);
        launchServo = hardwareMap.get(CRServo.class, "launchservo");
        launchManager = new LaunchManager(leftLaunchMotor, rightLaunchMotor, launchServo);

        // Initialize the artifact cell servos and manager
        //TODO: device name consistency (the motor string start with front/back).
        leftCell = new ServoEx(hardwareMap, "cellLeft");
        centerCell = new ServoEx(hardwareMap, "cellCenter");
        rightCell = new ServoEx(hardwareMap, "cellRight");
        //TODO: device name consistency (the motor string start with front/back).
        colorSensorOne = new ColorSensor(hardwareMap.get(NormalizedColorSensor.class, "colorsensorone"));
        colorSensorTwo = new ColorSensor(hardwareMap.get(NormalizedColorSensor.class, "colorsensortwo"));
        colorSensorThree = new ColorSensor(hardwareMap.get(NormalizedColorSensor.class, "colorsensorthree"));

        //TODO: Adjust positions as needed
        // Maybe move these to constants in ArtifactCellManager
        cellManager = new ArtifactCellManager(
                new double[]{0.766, 0.486, 0.78},//ups
                new double[]{1.000, 0.709, 0.746},//downs
                colorSensorOne,
                colorSensorTwo,
                colorSensorThree,
                leftCell,
                centerCell,
                rightCell
        );


        //TODO: What does this servo do?

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("a: Toggle Slow Mode");
        telemetry.addLine("x: Open left cell");
        telemetry.addLine("y: Open center cell");
        telemetry.addLine("b: Open right cell");
        telemetry.addLine("Right Trigger: Intake On");
        telemetry.addLine("Right Bumper: Intake Off");
        telemetry.addLine("Left Trigger: Launch On");
        telemetry.addLine("Left Bumper: Launch Off");
        telemetry.update();

        // Ready for start of OpMode
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Read gamepad inputs
            gamePadEx.readButtons();
            // Check all cell states
            cellManager.execute();

            // Check color sensors and passive effects
            cellManager.checkColors();
            cellManager.passiveProccessAll(leftCell, centerCell, rightCell);


            // toggle slow
            if (gamePadEx.isDown(GamepadKeys.Button.A)) {
                isSlowMode = !isSlowMode;
            }


            // Activate the appropriate cell servo
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.X)) {
                cellManager.openCell(ArtifactCellManager.CELL.Left);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.Y)) {
                cellManager.openCell(ArtifactCellManager.CELL.Center);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.B)) {
                cellManager.openCell(ArtifactCellManager.CELL.Right);
            }

            // Intake and Launch controls
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                intakeManager.intakeOn();
            }

            // Launch controls
            if (rightTriggerReader.isDown()) { //You have to hold it down
                //Depending on where we are trying to shoot from
                if (SHOOT_FAR) {
                    launchManager.launchOn(LONG_SHOT);
                } else {
                    launchManager.launchOn(SHORT_SHOT);
                }
            } else {
                launchManager.launchOff();
            }

            //Distance toggles
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_UP)) {
                SHOOT_FAR = true;
            } else if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_DOWN)) {
                SHOOT_FAR = false;
            }
            // Send drive power to the wheels
            mecanumDrive.driveRobotCentric(isSlowMode ? gamePadEx.getLeftX() * SLOW_MODE_FACTOR : gamePadEx.getLeftX(),
                    isSlowMode ? gamePadEx.getLeftY() * SLOW_MODE_FACTOR : gamePadEx.getLeftY(),
                    isSlowMode ? gamePadEx.getRightX() * SLOW_MODE_FACTOR : gamePadEx.getRightX());

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Movement Speed", "%s", isSlowMode ? "SLOW" : "FAST");
            telemetry.addData("Colors", "%s", ArtifactCellManager.colors());
            telemetry.addData("Velocities", launchManager.launchMotors.getVelocities());
            telemetry.update();
        }
    }

    /**
     * Initialize the AprilTag processor.
     */
    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }

    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
    private void setManualExposure(int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (!isStopRequested()) {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
        }
    }

    /**
     * Check to see if the launcher should be activated.
     *
     * @return true if the launcher is ready.
     */
    private boolean checkIsLauncherReady() {
        boolean targetFound = false;
        desiredTag = null;
        if (!targetFound) {
            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                // Look to see if we have size info on this tag.
                if (detection.metadata != null) {
                    //  Check to see if we want to track towards this tag.
                    if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
                        // Yes, we want to use this tag.
                        targetFound = true;
                        desiredTag = detection;
                        break;  // don't look any further.
                    } else {
                        // This tag is in the library, but we do not want to track it right now.
                        telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                    }
                } else {
                    // This tag is NOT in the library, so we don't have enough information to track to it.
                    telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
                }
            }
        }

        // Tell the driver what we see, and what to do.
        if (targetFound) {
            telemetry.addData("\n>", "HOLD Left-Bumper to Drive to Target\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("Range", "%5.1f inches", desiredTag.ftcPose.range);
            telemetry.addData("Bearing", "%3.0f degrees", desiredTag.ftcPose.bearing);
            telemetry.addData("Yaw", "%3.0f degrees", desiredTag.ftcPose.yaw);
        } else {
            telemetry.addData("\n>", "Drive using joysticks to find valid target\n");
        }

        // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
        if (targetFound) {
            // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.
            double rangeError = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
            double headingError = desiredTag.ftcPose.bearing;
            double yawError = desiredTag.ftcPose.yaw;

            // Use the speed and turn "gains" to calculate how we want the robot to move.
//            drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
//            turn = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
//            strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

//            telemetry.addData("Auto", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
        } else {
            // drive using manual POV Joystick mode.  Slow things down to make the robot more controlable.
//            drive = -gamepad1.left_stick_y / 2.0;  // Reduce drive rate to 50%.
//            strafe = -gamepad1.left_stick_x / 2.0;  // Reduce strafe rate to 50%.
//            turn = -gamepad1.right_stick_x;  // Reduce turn rate to 33%.
//            telemetry.addData("Manual", "Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
        }
        telemetry.update();

        // Apply desired axes motions to the drivetrain.
        mecanumDrive.driveRobotCentric(gamePadEx.getLeftY(), gamePadEx.getLeftX(), gamePadEx.getRightY());
        sleep(10);
        return true;
    }
}