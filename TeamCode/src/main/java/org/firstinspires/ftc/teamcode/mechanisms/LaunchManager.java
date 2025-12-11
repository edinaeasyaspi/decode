package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class LaunchManager {
    public LaunchManager(MotorEx leftLaunchMotor, MotorEx rightLaunchMotor) {
        this.leftLaunchMotor = leftLaunchMotor;
        this.rightLaunchMotor = rightLaunchMotor;
        this.leftLaunchMotor.setInverted(true);
        this.rightLaunchMotor.setInverted(false);

        this.launchMotors = new MotorGroup(leftLaunchMotor, rightLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private final MotorEx leftLaunchMotor;
    private final MotorEx rightLaunchMotor;
    private final MotorGroup launchMotors;

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        launchMotors.set(launchPower);
    }

    public void launchOff() {
        launchMotors.set(0);
    }
}
