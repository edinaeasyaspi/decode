package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

public class LaunchManager {
    public LaunchManager(MotorEx leftLaunchMotor, MotorEx rightLaunchMotor, CRServo launchServo) {
        this.launchServo = launchServo;
        leftLaunchMotor.setInverted(false);
        rightLaunchMotor.setInverted(true);

        this.launchMotors = new MotorGroup(leftLaunchMotor, rightLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private final CRServo launchServo;
    public final MotorGroup launchMotors;
    private double launchPower = 0.0;
    // Create a new SimpleMotorFeedforward with gains kS, kV, and kA
    //TODO: TUNE THESE VALUES
    private SimpleMotorFeedforward feedforward =
            new SimpleMotorFeedforward(0.1, 1.0, 0.0);

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        this.launchPower = launchPower;
        launchMotors.set(launchPower);
        launchServo.setPower(-1);
    }

    public void launchOff() {
        launchMotors.set(0);
        launchServo.setPower(0);
    }

    // Call this method periodically to maintain the desired launch power using feedforward control.
    public void execute() {
        launchMotors.set(feedforward.calculate(this.launchPower));
    }
}
