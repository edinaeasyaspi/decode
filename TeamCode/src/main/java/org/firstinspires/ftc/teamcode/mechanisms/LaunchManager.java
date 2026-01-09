package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

public class LaunchManager {
    /**
     * Manages the launch mechanism, which consists of two motors and a continuous rotation servo.
     * The motors are used to launch the game elements, while the servo is used to feed the elements into the launchers.
     * The launch power can be adjusted to control the speed of the launch
     */
    public LaunchManager(MotorEx leftLaunchMotor,
                         MotorEx rightLaunchMotor,
                         CRServo launchServo,
                         SimpleMotorFeedforward feedforward) {
        this.launchServo = launchServo;
        leftLaunchMotor.setInverted(false);
        rightLaunchMotor.setInverted(true);

        this.launchMotors = new MotorGroup(rightLaunchMotor, leftLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
        this.feedforward = feedforward;
    }

    private final CRServo launchServo;
    public final MotorGroup launchMotors;
    private double launchPower = 0.0;
    private SimpleMotorFeedforward feedforward;

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        this.launchPower = launchPower;
        launchMotors.set(launchPower);
        launchServo.setPower(-1);
    }

    public void launchOff() {
        launchMotors.stopMotor();
        launchServo.setPower(0);
    }

    // Call this method periodically to maintain the desired launch power using feedforward control.
    public void execute() {
        launchMotors.set(feedforward.calculate(this.launchPower));
    }
}
