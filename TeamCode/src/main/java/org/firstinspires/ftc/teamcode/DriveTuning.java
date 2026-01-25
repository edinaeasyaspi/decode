package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
@TeleOp(name="Drive Tuning")
public class DriveTuning extends LinearOpMode {
    private GamepadEx gamepadEx;
    private RobotHardware robot = new RobotHardware(this);
    private MecanumDrive mecanumDrive;

    private ElapsedTime moveTimer = new ElapsedTime();
    private boolean autoDriveing = false;
    public int driveTime = 2000;

    public Telemetry telemetry;
    public FtcDashboard ftcDashboard;

    @Override
    public void runOpMode() {
        robot.init();
        mecanumDrive = robot.mecanumDrive;
        gamepadEx = new GamepadEx(gamepad1);

        ftcDashboard = FtcDashboard.getInstance();
        telemetry = ftcDashboard.getTelemetry();

        while (opModeIsActive()) {
            if (!autoDriveing) {
                mecanumDrive.driveRobotCentric(gamepadEx.getLeftX(),gamepadEx.getLeftY(),gamepadEx.getRightX());
            }

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.A)) {
                autoDriveing = true;
                moveTimer.reset();
                mecanumDrive.driveRobotCentric(1,0,0);
            }

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.B)) {
                autoDriveing = true;
                moveTimer.reset();
                mecanumDrive.driveRobotCentric(0,1,0);
            }

            if (gamepadEx.wasJustReleased(GamepadKeys.Button.Y)) {
                autoDriveing = true;
                moveTimer.reset();
                mecanumDrive.driveRobotCentric(0,0,1);
            }

            if (autoDriveing && moveTimer.milliseconds() >= driveTime) {
                autoDriveing = false;
                mecanumDrive.driveRobotCentric(0,0,0);
            }
        }
    }
}
