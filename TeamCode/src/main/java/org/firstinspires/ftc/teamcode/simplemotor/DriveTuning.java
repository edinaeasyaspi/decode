package org.firstinspires.ftc.teamcode.simplemotor;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotHardware;

@Config
@TeleOp(name="Drive Tuning")
public class DriveTuning extends LinearOpMode {
    private GamepadEx gamepadEx;
    private RobotHardware robot = new RobotHardware(this);
    private MecanumDrive mecanumDrive;

    private ElapsedTime moveTimer = new ElapsedTime();
    private boolean autoDriveing = false;
    private int driveTime = 2000;
    public static int strafeDriveTime = 2000;
    public static int forwardDriveTime = 2000;
    public static int turnDriveTime = 2000;

    public Telemetry telemetry;
    public FtcDashboard ftcDashboard;

    public MovementManager movementManager;

    @Override
    public void runOpMode() {
        robot.init();
        mecanumDrive = robot.mecanumDrive;
        movementManager = new MovementManager();
        movementManager.load(robot.mecanumDrive);

        gamepadEx = new GamepadEx(gamepad1);

        ftcDashboard = FtcDashboard.getInstance();
        telemetry = ftcDashboard.getTelemetry();

        while (opModeIsActive()) {
            gamepadEx.readButtons();

            movementManager.load(robot.mecanumDrive);

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.A)) {
                movementManager.turn(1,1);
            }

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.B)) {
                movementManager.moveForward(1,1);
            }

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.Y)) {
                movementManager.moveStrafe(1,1);
            }


            telemetry.addData("Forward(ms)", forwardDriveTime);
            telemetry.addData("Strafe(ms)", strafeDriveTime);
            telemetry.addData("Turn(ms)", turnDriveTime);
        }
    }
}
