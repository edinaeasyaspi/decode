package org.firstinspires.ftc.teamcode;

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

    public void init(MotorEx frontLeft, MotorEx backLeft, MotorEx frontRight, MotorEx backRight) {
        frontLeftDrive = frontLeft;
        backLeftDrive = backLeft;
        frontRightDrive = frontRight;
        backRightDrive = backRight;
    }

    public void update() {
        frontLeftSpeeds.add(frontLeftDrive.get());
        backLeftSpeeds.add(backLeftDrive.get());
        frontRightSpeeds.add(frontRightDrive.get());
        backRightSpeeds.add(backRightDrive.get());
    }
}
