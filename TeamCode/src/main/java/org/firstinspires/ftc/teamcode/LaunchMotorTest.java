package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

@TeleOp(name = "Launch Motors Test", group = "test")
public class LaunchMotorTest extends LinearOpMode {
    MotorEx launchMotorLeft = null;
    MotorEx launchMotorRight = null;
    double newPower = 0;
    GamepadEx gamePadEx = new GamepadEx(gamepad1);

    public void runOpMode() {
        telemetry.addLine("a increases power, b decreases power");
        telemetry.update();
        launchMotorLeft = hardwareMap.get(MotorEx.class, "leftlauncher");
        launchMotorRight = hardwareMap.get(MotorEx.class, "rightlauncher");
        MotorGroup launcherMotors = new MotorGroup(launchMotorLeft, launchMotorRight);
        launcherMotors.setRunMode(Motor.RunMode.VelocityControl);

        waitForStart();
        while (opModeIsActive()) {
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.A)) {
                newPower += 0.01;
            }

            if (gamepad1.b) {
                newPower -= 0.01;
            }

            launcherMotors.set(newPower);
            telemetry.addData("Left Power:", launcherMotors.getSpeeds().get(0));
            telemetry.addData("Right Power:", launcherMotors.getSpeeds().get(1));
            telemetry.update();
        }
    }
}
