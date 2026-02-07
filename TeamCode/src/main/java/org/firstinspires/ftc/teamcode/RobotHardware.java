package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagManager;
import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;

/// **
/*
 * This is NOT an opmode.
 * This class can be used to define all the specific hardware for a single robot.
 * Only gamepad need to live in opmodes that require user input.
 */
@Config
public class RobotHardware {
    /* Declare OpMode members. */
    private final OpMode myOpMode;   // gain access to methods in the calling OpMode.
    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellPositions = new double[]{0.768, 0.486, 0.79}; //ups
    private final double[] cellDownPositions = new double[]{1.000, 0.709, 0.746}; //downs

    public MecanumDrive mecanumDrive;
    public IntakeManager intakeManager;
    public LaunchManager launchManager;
    public ArtifactCellManager cellManager;
    public AprilTagManager aprilTagManager;
    public RevColorSensorV3 leftColorSensor;
    public RevColorSensorV3 centerColorSensor;
    public RevColorSensorV3 rightColorSensor;
    public WebcamName webcamName;

    public RobotHardware(OpMode opmode) {
        myOpMode = opmode;
    }

    public void init() {
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration the DS.
        MotorEx frontLeftDrive = new MotorEx(myOpMode.hardwareMap, "frontleftdrive", Motor.GoBILDA.RPM_312);
        MotorEx backLeftDrive = new MotorEx(myOpMode.hardwareMap, "backleftdrive", Motor.GoBILDA.RPM_312);
        MotorEx frontRightDrive = new MotorEx(myOpMode.hardwareMap, "frontrightdrive", Motor.GoBILDA.RPM_312);
        MotorEx backRightDrive = new MotorEx(myOpMode.hardwareMap, "backrightdrive", Motor.GoBILDA.RPM_312);
        frontLeftDrive.setInverted(true);
        backLeftDrive.setInverted(true);
        frontRightDrive.setInverted(true);
        backRightDrive.setInverted(true);
        frontLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backLeftDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        frontRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        backRightDrive.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        MotorEx leftIntakeMotor = new MotorEx(myOpMode.hardwareMap, "leftintakemotor", Motor.GoBILDA.RPM_435);
        MotorEx rightIntakeMotor = new MotorEx(myOpMode.hardwareMap, "rightintakemotor", Motor.GoBILDA.RPM_435);
        intakeManager = new IntakeManager(leftIntakeMotor, rightIntakeMotor);

        MotorEx leftLaunchMotor = new MotorEx(myOpMode.hardwareMap, "leftlaunchmotor", Motor.GoBILDA.BARE);
        MotorEx rightLaunchMotor = new MotorEx(myOpMode.hardwareMap, "rightlaunchmotor", Motor.GoBILDA.BARE);
        CRServo launchServo = myOpMode.hardwareMap.get(CRServo.class, "launchservo");
        launchManager = new LaunchManager(leftLaunchMotor, rightLaunchMotor, launchServo);

        webcamName = myOpMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        aprilTagManager = new AprilTagManager(myOpMode, webcamName, mecanumDrive);

        leftColorSensor = (RevColorSensorV3) myOpMode.hardwareMap.get("colorsensorone");
        centerColorSensor = (RevColorSensorV3) myOpMode.hardwareMap.get("colorsensortwo");
        rightColorSensor = (RevColorSensorV3) myOpMode.hardwareMap.get("colorsensorthree");

        // Initialize the artifact cell servos and manager
        ServoEx leftCell = new ServoEx(myOpMode.hardwareMap, "cellLeft");
        ServoEx centerCell = new ServoEx(myOpMode.hardwareMap, "cellCenter");
        ServoEx rightCell = new ServoEx(myOpMode.hardwareMap, "cellRight");
        cellManager = new ArtifactCellManager(
                cellPositions, //ups
                cellDownPositions, //downs
                leftColorSensor,
                centerColorSensor,
                rightColorSensor,
                leftCell,
                centerCell,
                rightCell
        );
    }
}
