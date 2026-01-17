package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
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

    @Override
    public void init() {
        movementPatternRetrieve = new MovementPatternRetrieve(hardwareMap.appContext);
        movementPattern = movementPatternRetrieve.getObject("saveOne.txt");

        frontLeftSpeeds = movementPattern.getFrontLeftSpeeds();
        backLeftSpeeds = movementPattern.getBackLeftSpeeds();
        frontRightSpeeds = movementPattern.getFrontRightSpeeds();
        backRightSpeeds = movementPattern.getBackRightSpeeds();

        frontLeftDrive = new MotorEx(hardwareMap, "frontleftdrive");
        backLeftDrive = new MotorEx(hardwareMap, "backleftdrive");
        frontRightDrive = new MotorEx(hardwareMap, "frontrightdrive");
        backRightDrive = new MotorEx(hardwareMap, "backrightdrive");

        cycleTimes = movementPattern.cycleTime;
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
