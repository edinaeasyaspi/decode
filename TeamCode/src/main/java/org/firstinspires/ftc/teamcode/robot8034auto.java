package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.mechanisms.InOutSys;
import org.firstinspires.ftc.teamcode.mechanisms.ServoK;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous(name="BasicMotor")
@Disabled
public class robot8034auto extends LinearOpMode {
    final double DESIRED_DISTANCE = 24.0;
    final double SPEED_GAIN  =  0.02;
    final double STRAFE_GAIN =  0.015;
    final double TURN_GAIN   =  0.01;
    final double MAX_AUTO_SPEED = 0;
    final double MAX_AUTO_STRAFE= 0;
    final double MAX_AUTO_TURN  = 0.3;
    private static final boolean USE_WEBCAM = true;
    private static final int DESIRED_TAG_ID = -1;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;
    private DcMotor fl;
    private DcMotor bl;
    private DcMotor fr;
    private DcMotor br;
    private ServoK servoOne;
    private ServoK servoTwo;
    private ServoK servoThree;
    private CRServo servoFour;
    private InOutSys IOsys;
    private int repititions;
    @Override
    public void runOpMode() throws InterruptedException {
        boolean targetFound = false;
        double drive = 0;
        double strafe = 0;
        double turn = 0;
        initAprilTag();
        setManualExposure(6, 250);
        fl = hardwareMap.get(DcMotor.class, "MotorOne");
        bl = hardwareMap.get(DcMotor.class, "MotorTwo");
        fr = hardwareMap.get(DcMotor.class, "MotorThree");
        br = hardwareMap.get(DcMotor.class, "MotorFour");
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);
        servoOne = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoOne"),
                1.000,0.766);
        servoTwo = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoTwo"),
                0.709, 0.486);
        servoThree = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoThree"),
                0.746, 0.78);
        IOsys = new InOutSys(
                hardwareMap.get(DcMotor.class, "MotorFive"),
                hardwareMap.get(DcMotor.class, "MotorSix"),
                hardwareMap.get(DcMotor.class, "MotorSeven"),
                hardwareMap.get(DcMotor.class, "MotorEight"),
                hardwareMap.get(VoltageSensor.class, "Control Hub"),
                telemetry
        );
        servoFour = hardwareMap.get(CRServo.class, "ServoFive");
        waitForStart();
        forward(1);
        sleep(700);
        forward(0);
        repititions = 0;
        while (!(repititions > 200)) {
            repititions += 1;
            desiredTag  = null;
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
            }}

            // Tell the driver what we see, and what to do.
            if (targetFound) {
                telemetry.addData("\n>","Driving to target\n");
                telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
                telemetry.addData("Range",  "%5.1f inches", desiredTag.ftcPose.range);
                telemetry.addData("Bearing","%3.0f degrees", desiredTag.ftcPose.bearing);
                telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
            }

            // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
            if (targetFound) {

                // Determine heading, range and Yaw (tag image rotation) error so we can use them to control the robot automatically.
                double  rangeError      = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
                double  headingError    = desiredTag.ftcPose.bearing;
                double  yawError        = desiredTag.ftcPose.yaw;

                // Use the speed and turn "gains" to calculate how we want the robot to move.
                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
            }
            telemetry.update();

            // Apply desired axes motions to the drivetrain.
            moveRobot(drive, strafe, turn);
            sleep(10);
        }
        repititions = 0;
        servoFour.setPower(-1);
        IOsys.outon();
        shootall(1);
        sleep(2000);
        IOsys.outoff();
        servoFour.setPower(0);
        turnRight(-1);
        sleep(400);
        forward(0);
        while (opModeIsActive()) sleep(1000);
    }
    public void forward(double power) {
        fl.setPower(power);
        bl.setPower(power);
        fr.setPower(power);
        br.setPower(power);
    }
    public void right(double power) {
        fl.setPower(power);
        bl.setPower(-1*power);
        fr.setPower(-1*power);
        br.setPower(power);
    }
    public void turnRight(double power) {
        fl.setPower(power);
        bl.setPower(power);
        fr.setPower(-1*power);
        br.setPower(-1*power);
    }
    public void shootall(int start) {
        sleep(1500);
        servoTwo.upDown();
        sleep(4000);
        servoThree.upDown();
//        switch (start) {
//            case 1:
//                servoOne.upDown();
//            case 2:
//                servoTwo.upDown();
//            case 3:
//                servoThree.upDown();
//            case 4:
//                servoOne.upDown();
//            case 5:
//                servoTwo.upDown();
//        }
        sleep(4000);
        servoOne.upDown();
    }
    public void moveRobot(double x, double y, double yaw) {
        // Calculate wheel powers.
        double frontLeftPower    =  x - y - yaw;
        double frontRightPower   =  x + y + yaw;
        double backLeftPower     =  x + y - yaw;
        double backRightPower    =  x - y + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Send powers to the wheels.
        bl.setPower(frontLeftPower);
        br.setPower(frontRightPower);
        fl.setPower(backLeftPower);
        fr.setPower(backRightPower);
    }
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
    private void    setManualExposure(int exposureMS, int gain) {
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
        if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
        }
    }
}
