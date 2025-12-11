package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class LaunchManager {
    public LaunchManager(MotorEx leftlaunchMotor, MotorEx rightlaunchMotor) {
        this.leftLaunchMotor = leftlaunchMotor;
        this.rightLaunchMotor = rightlaunchMotor;
        this.leftLaunchMotor.setInverted(true);
        this.rightLaunchMotor.setInverted(false);

        this.launchMotors = new MotorGroup(leftlaunchMotor, rightlaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private MotorEx leftLaunchMotor;
    private MotorEx rightLaunchMotor;
    private MotorGroup launchMotors;

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        launchMotors.set(launchPower);
    }

    public void launchOff() {
        launchMotors.set(0);
    }
}
