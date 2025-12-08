package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.ToggleButtonReader;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name = "Intake/Launch Motors Test", group = "test")
public class IntakeLaunchMotorTest extends OpMode {
    MotorEx leftMotor = null;
    MotorEx rightMotor = null;
    MotorGroup motorGroup = null;
    // Default to launch motors at start
    private String leftMotorName = "leftintake";
    private String rightMotorName = "rightintake";
    private boolean isLauncher = true;
    double newPower = 0;
    GamepadEx gamePadEx = new GamepadEx(gamepad1);
    ToggleButtonReader aReader = new ToggleButtonReader(gamePadEx, GamepadKeys.Button.A);

    @Override
    public void init() {
        telemetry.addLine("a: Toggle launch/intake motors");
        telemetry.addLine("press start to begin");
        telemetry.addLine("dpad up: increase power, dpad down: decreases power");
        telemetry.update();
        initMotors(leftMotorName, rightMotorName);
    }

    @Override
    public void init_loop() {
        aReader.readValue();
        if (aReader.getState()) {
            leftMotorName = "leftlauncher";
            rightMotorName = "rightlauncher";
            isLauncher = true;
            initMotors(leftMotorName, rightMotorName);
        } else {
            leftMotorName = "leftintake";
            rightMotorName = "rightintake";
            isLauncher = false;
            initMotors(leftMotorName, rightMotorName);
        }
        telemetry.addData("Current Motors:", isLauncher ? "Launch Motors" : "Intake Motors");
        telemetry.update();
    }

    @Override
    public void start() {

    }

    @Override
    public void loop() {

        if (gamePadEx.getButton(GamepadKeys.Button.DPAD_UP)) {
            newPower += 0.01;
        }

        if (gamePadEx.getButton(GamepadKeys.Button.DPAD_DOWN)) {
            newPower -= 0.01;
        }

        motorGroup.set(newPower);
        telemetry.addData("Left %:", motorGroup.getSpeeds().get(0));
        telemetry.addData("Right %:", motorGroup.getSpeeds().get(1));
        telemetry.update();
    }

    @Override
    public void updateTelemetry(Telemetry telemetry) {
        super.updateTelemetry(telemetry);
    }

    private void initMotors(String leftMotorName, String rightMotorName) {
        leftMotor = hardwareMap.get(MotorEx.class, leftMotorName);
        rightMotor = hardwareMap.get(MotorEx.class, rightMotorName);
        motorGroup = new MotorGroup(leftMotor, rightMotor);
        motorGroup.setRunMode(Motor.RunMode.VelocityControl);
    }
}
