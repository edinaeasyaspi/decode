package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.ColorSensor;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;

public class RobotHardware {
    /* Declare OpMode members. */
    private OpMode myOpMode = null;   // gain access to methods in the calling OpMode.
    private MotorEx frontLeftDrive;
    private MotorEx backLeftDrive;
    private MotorEx frontRightDrive;
    private MotorEx backRightDrive;
    public MecanumDrive mecanumDrive;
    private MotorEx leftIntakeMotor;
    private MotorEx rightIntakeMotor;
    public IntakeManager intakeManager;

    private MotorEx leftLaunchMotor;
    private MotorEx rightLaunchMotor;
    public LaunchManager launchManager;
    private CRServo launchServo;

    private ServoEx leftCell;
    private ServoEx centerCell;
    private ServoEx rightCell;
    public ArtifactCellManager cellManager;

    public ColorSensor colorSensorOne;
    public ColorSensor colorSensorTwo;
    public ColorSensor colorSensorThree;

    public RobotHardware(OpMode opmode) {
        myOpMode = opmode;
    }

    public void init() {
        //TODO: Adjust motor types. Verify the motor type for each motor.
        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration the DS.
        frontLeftDrive = new MotorEx(myOpMode.hardwareMap, "frontleftdrive", Motor.GoBILDA.RPM_312);
        backLeftDrive = new MotorEx(myOpMode.hardwareMap, "backleftdrive", Motor.GoBILDA.RPM_312);
        frontRightDrive = new MotorEx(myOpMode.hardwareMap, "frontrightdrive", Motor.GoBILDA.RPM_312);
        backRightDrive = new MotorEx(myOpMode.hardwareMap, "backrightdrive", Motor.GoBILDA.RPM_312);
        frontLeftDrive.setInverted(true);
        backLeftDrive.setInverted(true);
        frontRightDrive.setInverted(true);
        backRightDrive.setInverted(true);
        mecanumDrive = new MecanumDrive(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive);

        leftIntakeMotor = new MotorEx(myOpMode.hardwareMap, "leftintakemotor", Motor.GoBILDA.RPM_435);
        rightIntakeMotor = new MotorEx(myOpMode.hardwareMap, "rightintakemotor", Motor.GoBILDA.RPM_435);
        intakeManager = new IntakeManager(leftIntakeMotor, rightIntakeMotor);

        leftLaunchMotor = new MotorEx(myOpMode.hardwareMap, "leftlaunchmotor", Motor.GoBILDA.BARE);
        rightLaunchMotor = new MotorEx(myOpMode.hardwareMap, "rightlaunchmotor", Motor.GoBILDA.BARE);
        launchServo = myOpMode.hardwareMap.get(CRServo.class, "launchservo");
        launchManager = new LaunchManager(leftLaunchMotor, rightLaunchMotor, launchServo);

        // Initialize the artifact cell servos and manager
        leftCell = new ServoEx(myOpMode.hardwareMap, "cellLeft");
        centerCell = new ServoEx(myOpMode.hardwareMap, "cellCenter");
        rightCell = new ServoEx(myOpMode.hardwareMap, "cellRight");
        cellManager = new ArtifactCellManager(
                new double[]{0.786, 0.486, 0.78},//ups
                new double[]{1.000, 0.709, 0.746},//downs
                colorSensorOne,
                colorSensorTwo,
                colorSensorThree,
                leftCell,
                centerCell,
                rightCell
        );
    }
}
