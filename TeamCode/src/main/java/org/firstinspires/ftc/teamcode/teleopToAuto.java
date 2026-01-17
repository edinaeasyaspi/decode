package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;

import java.util.List;

@Autonomous(name="teleopToAuto")
public class teleopToAuto extends OpMode {
    public MovementPattern movementPattern;
    public MovementPatternRetrieve movementPatternRetrieve;

    public List<Double> frontLeftSpeeds;
    public List<Double> backLeftSpeeds;
    public List<Double> frontRightSpeeds;
    public List<Double> backRightSpeeds;

    public MotorEx frontLeftDrive;
    public MotorEx backLeftDrive;
    public MotorEx frontRightDrive;
    public MotorEx backRightDrive;

    public int repCount = 0;
    public List<Long> cycleTimes;

    public GamepadEx gamepadEx;

    public boolean loaded = false;

    public LaunchManager launchManager;
    public final double longShot = 0.315;
    public final double shortShot = 0.25;
    public boolean shootShort = true;

    public int load = 0;

    @Override
    public void init() {
        movementPatternRetrieve = new MovementPatternRetrieve(hardwareMap.appContext);

        frontLeftDrive = new MotorEx(hardwareMap, "frontleftdrive");
        backLeftDrive = new MotorEx(hardwareMap, "backleftdrive");
        frontRightDrive = new MotorEx(hardwareMap, "frontrightdrive");
        backRightDrive = new MotorEx(hardwareMap, "backrightdrive");

        gamepadEx = new GamepadEx(gamepad1);

        RobotHardware robotHardware = new RobotHardware(this);
        robotHardware.launchManager = launchManager;


    }

    @Override
    public void init_loop() {
        if (gamepad1.a && !loaded) {
            movementPattern = movementPatternRetrieve.getObject("saveOne.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
            load = 1;
        }
        if (gamepad1.b && !loaded) {
            movementPattern = movementPatternRetrieve.getObject("saveTwo.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
            load = 2;
        }
        if (gamepad1.x && !loaded) {
            movementPattern = movementPatternRetrieve.getObject("saveThree.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
            load = 3;
        }
        if (gamepad1.y && !loaded) {
            movementPattern = movementPatternRetrieve.getObject("saveFour.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
            load = 4;
        }
        if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            shootShort = false;
        }
        if (gamepadEx.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            shootShort = true;
        }
        telemetry.addLine("A: save one, B: save two, X: save three, Y: save four");
        telemetry.addData("Loaded:", loaded);
        telemetry.addLine("\nToggle short and long the same way you would the shooter");
        telemetry.addData("Shooting Far", !shootShort);
        telemetry.addData("Shooting Short", shootShort);
        telemetry.addData("Save number", load);
    }
    @Override
    public void start() {
        if (!loaded) {
            movementPattern = movementPatternRetrieve.getObject("saveOne.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
        }
        if (shootShort) launchManager.launchOn(shortShot); else launchManager.launchOn(longShot);
    }
    @Override
    public void loop() {
        long startTime = System.nanoTime();

        frontLeftDrive.set(frontLeftSpeeds.get(repCount));
        backLeftDrive.set(backLeftSpeeds.get(repCount));
        frontRightDrive.set(frontRightSpeeds.get(repCount));
        backRightDrive.set(backRightSpeeds.get(repCount));

        long endTime = System.nanoTime();
        while (!((endTime-startTime) > cycleTimes.get(repCount))) {
            endTime = System.nanoTime();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        repCount++;
    }
}
