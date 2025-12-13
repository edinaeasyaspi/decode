package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

public class LaunchManager {
    public LaunchManager(MotorEx leftLaunchMotor, MotorEx rightLaunchMotor, CRServo launchServo) {
        this.leftLaunchMotor = leftLaunchMotor;
        this.rightLaunchMotor = rightLaunchMotor;
        this.launchServo = launchServo;
        this.leftLaunchMotor.setInverted(false);
        this.rightLaunchMotor.setInverted(true);

        this.launchMotors = new MotorGroup(leftLaunchMotor, rightLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private final CRServo launchServo;
    private final MotorEx leftLaunchMotor;
    private final MotorEx rightLaunchMotor;
    public final MotorGroup launchMotors;

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        launchMotors.set(launchPower);
        launchServo.setPower(-1);
    }

    public void launchOff() {
        launchMotors.set(0);
        launchServo.setPower(0);
    }
}
