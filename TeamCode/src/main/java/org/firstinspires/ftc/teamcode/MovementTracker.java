package org.firstinspires.ftc.teamcode;

import android.content.Context;

import com.seattlesolvers.solverslib.hardware.motors.MotorEx;

import java.util.ArrayList;
import java.util.List;

public class MovementTracker {
    private MotorEx frontLeftDrive;
    private MotorEx backLeftDrive;
    private MotorEx frontRightDrive;
    private MotorEx backRightDrive;

    private List<Double> frontLeftSpeeds = new ArrayList<>();
    private List<Double> backLeftSpeeds = new ArrayList<>();
    private List<Double> frontRightSpeeds = new ArrayList<>();
    private List<Double> backRightSpeeds = new ArrayList<>();

    private Context context;

    public void init(MotorEx frontLeft, MotorEx backLeft, MotorEx frontRight, MotorEx backRight, Context context1) {
        frontLeftDrive = frontLeft;
        backLeftDrive = backLeft;
        frontRightDrive = frontRight;
        backRightDrive = backRight;
        context = context1;
    }

    public void update() {
        frontLeftSpeeds.add(frontLeftDrive.get());
        backLeftSpeeds.add(backLeftDrive.get());
        frontRightSpeeds.add(frontRightDrive.get());
        backRightSpeeds.add(backRightDrive.get());
    }

    public void recordMovement(long cycle) {
        MovementPatternRetrieve movementPatternRetrieve = new MovementPatternRetrieve(context);
        MovementPattern movementPattern = new MovementPattern();
        movementPattern.init(frontLeftSpeeds,backLeftSpeeds,frontRightSpeeds,backRightSpeeds,cycle);
        movementPatternRetrieve.storeObject(movementPattern);
    }
}
