package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class LaunchManager {
    public LaunchManager(MotorEx leftlaunchMotor, MotorEx rightlaunchMotor) {
        this.leftlaunchMotor = leftlaunchMotor;
        this.rightlaunchMotor = rightlaunchMotor;
    }

    private MotorEx leftlaunchMotor;
    private MotorEx rightlaunchMotor;
    private MotorGroup launchMotors = new MotorGroup(leftlaunchMotor, rightlaunchMotor);

    public void launchOn(double launchPower) {
        launchMotors.set(launchPower);
    }

    public void launchOff() {
        launchMotors.set(0);
    }
}
