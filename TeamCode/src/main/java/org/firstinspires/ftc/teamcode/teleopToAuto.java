package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

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

    @Override
    public void init() {
        movementPatternRetrieve = new MovementPatternRetrieve(hardwareMap.appContext);

        frontLeftDrive = new MotorEx(hardwareMap, "frontleftdrive");
        backLeftDrive = new MotorEx(hardwareMap, "backleftdrive");
        frontRightDrive = new MotorEx(hardwareMap, "frontrightdrive");
        backRightDrive = new MotorEx(hardwareMap, "backrightdrive");

        cycleTimes = movementPattern.cycleTime;

        gamepadEx = new GamepadEx(gamepad1);
    }

    @Override
    public void init_loop() {
        if (gamepadEx.wasJustReleased(GamepadKeys.Button.A)) {
            movementPattern = movementPatternRetrieve.getObject("saveOne.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
        }
        if (gamepadEx.wasJustReleased(GamepadKeys.Button.B)) {
            movementPattern = movementPatternRetrieve.getObject("saveTwo.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
        }
        if (gamepadEx.wasJustReleased(GamepadKeys.Button.X)) {
            movementPattern = movementPatternRetrieve.getObject("saveThree.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
        }
        if (gamepadEx.wasJustReleased(GamepadKeys.Button.X)) {
            movementPattern = movementPatternRetrieve.getObject("saveFour.txt");

            frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
            backLeftSpeeds = movementPattern.getBackLeftSpeeds();
            frontRightSpeeds = movementPattern.getFrontRightSpeeds();
            backRightSpeeds = movementPattern.getBackRightSpeeds();

            cycleTimes = movementPattern.cycleTime;

            loaded = true;
        }
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
