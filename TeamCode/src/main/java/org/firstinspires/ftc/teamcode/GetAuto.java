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

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.ToggleButtonReader;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 * This OpMode is the main teleOp for Decode.
 */
@Config
@TeleOp(name = "GetAuto", group = "TeleOp")
/// Later we will use gamepad tracer
public class GetAuto extends LinearOpMode {
    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    /**
     * Variables to store the position and orientation of the camera on the robot. Setting these
     * values requires a definition of the axes of the camera and robot:
     * <p>
     * Camera axes:
     * Origin location: Center of the lens
     * Axes orientation: +x right, +y down, +z forward (from camera's perspective)
     * <p>
     * Robot axes (this is typical, but you can define this however you want):
     * Origin location: Center of the robot at field height
     * Axes orientation: +x right, +y forward, +z upward
     * <p>
     * Position:
     * If all values are zero (no translation), that implies the camera is at the center of the
     * robot. Suppose your camera is positioned 5 inches to the left, 7 inches forward, and 12
     * inches above the ground - you would need to set the position to (-5, 7, 12).
     * <p>
     * Orientation:
     * If all values are zero (no rotation), that implies the camera is pointing straight up. In
     * most cases, you'll need to set the pitch to -90 degrees (rotation about the x-axis), meaning
     * the camera is horizontal. Use a yaw of 0 if the camera is pointing forwards, +90 degrees if
     * it's pointing straight left, -90 degrees for straight right, etc. You can also set the roll
     * to +/-90 degrees if it's vertical, or 180 degrees if it's upside-down.
     */
    private Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 9, 12, 0);
    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -90, 0, 0);

    private AutonomousConfiguration autonomousConfiguration;

    //TODO: Define desired distances for short and long shots.
    // This is used for auto-alignment. Currently not implemented!
    final double DESIRED_SHORT_DISTANCE = 24.0;
    final double DESIRED_LONG_DISTANCE = 48.0;

    private static final boolean USE_WEBCAM = true;
    public boolean isAprilTagAligned = false;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;

    //TODO: Auto align debug variables for use with ftc dashboard.
    // Remove after debugging.
    public static double bearing = 0;
    public static double bearingTolerance = 5.0;


    private RobotHardware robot = new RobotHardware(this);

    private MecanumDrive mecanumDrive;
    private double SLOW_MODE_FACTOR = 0.4;

    private IntakeManager intakeManager;
    private LaunchManager launchManager;
    ArtifactCellManager cellManager;

    //TODO: Adjust shot variables as needed
    public static double SHORT_SHOT = 0.25;
    public static double LONG_SHOT = 0.315;
    // Track whether we are shooting far or short.
    // If there is time, implement the AprilTag to calculate a variable distance.
    private boolean SHOOT_FAR = false;

    FtcDashboard dashboard;
    Telemetry telemetry;
    // Control the slow mode for driving.
    boolean isSlowMode = false;

    GamepadEx gamePadEx;
    ToggleButtonReader aReader;
    TriggerReader leftTriggerReader;
    TriggerReader rightTriggerReader;

    MovementTracker movementTracker;

    public List<Long> cycles = new ArrayList<>();

    @Override
    public void runOpMode() {
        // Initialize the robot hardware
        robot.init();
        // Initialize the autonomous configuration to get camera settings.
        autonomousConfiguration = new AutonomousConfiguration();
        autonomousConfiguration.init(gamepad1, telemetry, hardwareMap.appContext);
        boolean targetFound = false;
        double drive = 0;
        double strafe = 0;
        double turn = 0;
        // FtcDashboard setup
        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        movementTracker = new MovementTracker();
        movementTracker.init(robot.frontLeftDrive,robot.backLeftDrive,robot.frontRightDrive,robot.backRightDrive,hardwareMap.appContext);

        initAprilTag();
        // Set the exposure and gain for the camera.
        setManualExposure();


        //Define gamepad (from SolversLib)
        gamePadEx = new GamepadEx(gamepad1);
        aReader = new ToggleButtonReader(gamePadEx, GamepadKeys.Button.A);
        leftTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.RIGHT_TRIGGER);

        mecanumDrive = robot.mecanumDrive;
        intakeManager = robot.intakeManager;
        launchManager = robot.launchManager;
        cellManager = robot.cellManager;

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("a: Toggle Slow Mode");
        telemetry.addLine("x: Open left cell");
        telemetry.addLine("y: Open center cell");
        telemetry.addLine("b: Open right cell");
        telemetry.addLine("Right Trigger: Intake On");
        telemetry.addLine("Right Bumper: Intake Off");
        telemetry.addLine("D-Pad Up: Long shot");
        telemetry.addLine("D-Pad Down: Short shot");
        telemetry.addLine("D-Pad Left: Auto Aim");
        telemetry.addLine("D-Pad Right: turn off launch motors");
        telemetry.update();

        // Ready for start of OpMode
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            long starttime = System.nanoTime();
            // Read gamepad inputs
            gamePadEx.readButtons();
            // Update the cell manager and launch manager
            cellManager.execute();
            launchManager.execute();
            movementTracker.update();

//          No real reason to check the color sensors, just that we can say we ahve the code
//          cellManager.checkColors();
//          cellManager.passiveProccessAll(leftCell, centerCell, rightCell);

            // toggle slow drive mode
            if (gamePadEx.isDown(GamepadKeys.Button.A)) {
                isSlowMode = !isSlowMode;
            }

            // Activate the appropriate cell servo to launch an artifact
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.X)) {
                cellManager.openCell(ArtifactCellManager.CELL.Left);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.Y)) {
                cellManager.openCell(ArtifactCellManager.CELL.Center);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.B)) {
                cellManager.openCell(ArtifactCellManager.CELL.Right);
            }

            // Intake controls
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                intakeManager.intakeOn();
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                intakeManager.intakeOff();
            }

            //Distance control
            // If there is time, the auto-centering could also set the power for distance.
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_UP)) {
                SHOOT_FAR = true;
                launchManager.launchOn(LONG_SHOT);
            } else if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_DOWN)) {
                SHOOT_FAR = false;
                launchManager.launchOn(SHORT_SHOT);
            }

            //TODO: Verify how this should actually work. It aligns to the tag while
            // the D-Pad left is held down.
            if (gamePadEx.isDown(GamepadKeys.Button.DPAD_LEFT)) {
                isAprilTagAligned = isAprilTagAligned();
            }

            // Turn off the launch motors to save the battery.
            if (gamePadEx.isDown(GamepadKeys.Button.DPAD_RIGHT)) {
                if (gamePadEx.wasJustPressed(GamepadKeys.Button.A)) {
                    movementTracker.recordMovement(cycles, "saveOne.txt");
                }
                if (gamePadEx.wasJustPressed(GamepadKeys.Button.B)) {
                    movementTracker.recordMovement(cycles,"saveTwo");
                }
                if (gamePadEx.wasJustPressed(GamepadKeys.Button.X)) {
                    movementTracker.recordMovement(cycles,"saveThree");
                }
                if (gamePadEx.wasJustPressed(GamepadKeys.Button.Y)) {
                    movementTracker.recordMovement(cycles,"saveFour");
                }
            }

            // Send drive power to the wheels when not Auto-aligning.
            //TODO: Verify this behavior with drivers.
            // If D-Pad left is pressed, we are auto-aligning, so don't accept joystick inputs.
            if (!gamePadEx.isDown(GamepadKeys.Button.DPAD_LEFT)) {
                mecanumDrive.driveRobotCentric(isSlowMode ? gamePadEx.getLeftX() * SLOW_MODE_FACTOR : gamePadEx.getLeftX(),
                        isSlowMode ? gamePadEx.getLeftY() * SLOW_MODE_FACTOR : gamePadEx.getLeftY(),
                        isSlowMode ? gamePadEx.getRightX() * SLOW_MODE_FACTOR : gamePadEx.getRightX());
            }

            long endtime = System.nanoTime();
            long cycletime = endtime - starttime;
            cycles.add(cycletime);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Movement Speed", "%s", isSlowMode ? "SLOW" : "FAST");
            telemetry.addData("Launch left speed:", launchManager.launchMotorLeftSpeed);
            telemetry.addData("Launch right speed:", launchManager.launchMotorRightSpeed);
            telemetry.addData("April tag aligned:", isAprilTagAligned);
            telemetry.addData("April tag bearing", bearing);
            telemetry.update();
        }
    }

    /**
     * Initialize the AprilTag processor.
     */
    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        //TODO: Adjust the camera position and orientation values to match your robot
        aprilTag = new AprilTagProcessor.Builder()
                .setCameraPose(cameraPosition, cameraOrientation)
                .build();

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
    private void setManualExposure() {
        // The default camera settings initially are set in AutonomousOptions and saved to the
        // autonomous configuration file.
        int exposureMS = 3;
        int gain = 25;
        switch (autonomousConfiguration.getAlliance()) {
            case Blue:
                exposureMS = autonomousConfiguration.getExposureBlue();
                gain = autonomousConfiguration.getGainBlue();
                break;
            case Red:
                exposureMS = autonomousConfiguration.getExposureRed();
                gain = autonomousConfiguration.getGainRed();
                break;
            default:
                break;
        }
        switch (autonomousConfiguration.getAlliance()) {
            case Blue:
                exposureMS = autonomousConfiguration.getExposureBlue();
                gain = autonomousConfiguration.getGainBlue();
                break;
            case Red:
                exposureMS = autonomousConfiguration.getExposureRed();
                gain = autonomousConfiguration.getGainRed();
                break;
            default:
                break;
        }

        // Wait for the camera to be open, then use the controls
        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
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
     * Check to see if we are aligned to the desired AprilTag.
     *
     * @return true if aligned.
     */
    //TODO: Something is causeing the robot to spin when this function is called, the mecanum drive is not the problem
    private boolean isAprilTagAligned() {
        boolean aligned = false;
        // Assume there are 2 launch distances: short and long
        double desiredRange = SHOOT_FAR ? DESIRED_LONG_DISTANCE : DESIRED_SHORT_DISTANCE;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if (detection.id == 20 || detection.id == 24) {
                    // Check if the tag is within alignment tolerances
                    double rangeError = Math.abs(detection.ftcPose.range - desiredRange);
                    // Use static variable for dashboard tuning
                    bearing = detection.ftcPose.bearing;
                    double yawError = Math.abs(detection.ftcPose.yaw);

                    //TODO: Adjust tolerances as needed
                    // Define tolerances
                    double rangeTolerance = 2.0; // inches
                    // Use static variable for dashboard tuning
                    bearingTolerance = 1; // degrees
                    double yawTolerance = 12.0; // degrees

//TODO: Decide if you want to use range and yaw corrections as well
//                    if (rangeError <= rangeTolerance && bearingError <= bearingTolerance && yawError <= yawTolerance) {
//                        aligned = true;
//                    } else {
                    // Only correct for bearing for now
                    if (Math.abs(bearing) <= bearingTolerance) {
                        aligned = true;
                        mecanumDrive.driveRobotCentric(0, 0, 0);
                    } else {
                        // The parameters are set to only center. You may want to add range control as well.
                        mecanumDrive.driveRobotCentric(0, 0, scale(-bearing, -45, 45, -1., 1));
                    }

                    break; // No need to check further tags
                }
            }
        }

        return aligned;
    }

    // Scale a value from one range to another.
    private static double scale(double value, double inMin, double inMax, double outMin, double outMax) {
        double result = (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;

        if (result < outMin) {
            return outMin;
        } else if (result > outMax) {
            return outMax;
        }
        return result;
    }
}