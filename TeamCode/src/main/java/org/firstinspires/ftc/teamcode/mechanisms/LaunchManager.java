package org.firstinspires.ftc.teamcode.mechanisms;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.controller.wpilibcontroller.SimpleMotorFeedforward;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

@Config
public class LaunchManager {
    /**
     * Manages the launch mechanism, which consists of two motors and a continuous rotation servo.
     * The motors are used to launch the game elements, while the servo is used to feed the elements into the launchers.
     * The launch power can be adjusted to control the speed of the launch
     */
    public LaunchManager(MotorEx leftLaunchMotor,
                         MotorEx rightLaunchMotor,
                         CRServo launchServo) {
        this.launchServo = launchServo;
        leftLaunchMotor.setInverted(false);
        rightLaunchMotor.setInverted(true);

        this.launchMotors = new MotorGroup(rightLaunchMotor, leftLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private final CRServo launchServo;
    public final MotorGroup launchMotors;
    public static double launchPower = 0.0;
    // Feedforward constants
    // The feedfoward controller is used by the launch manager to maintain consistent launch speed.
    public static double FF_S = 0.135;
    public static double FF_V = 0.85;
    public static double FF_A = 0.0;
    public static double launchMotorLeftVelocity;
    public static double launchMotorRightVelocity;
    public double launchMotorLeftSpeed;
    public double launchMotorRightSpeed;

    private SimpleMotorFeedforward feedforward =
            new SimpleMotorFeedforward(FF_S, FF_V, FF_A);


    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        this.launchPower = launchPower;
        launchMotors.set(launchPower);
        launchServo.setPower(1);
    }

    public void launchOff() {
        launchMotors.stopMotor();
        launchServo.setPower(0);
    }

    // Call this method periodically to maintain the desired launch power using feedforward control.
    public void execute() {
        //TODO: This is for debugging, remove later.
        feedforward = new SimpleMotorFeedforward(FF_S, FF_V, FF_A);
        List<Double> velocities = launchMotors.getVelocities();
        launchMotorLeftVelocity = velocities.get(0);
        launchMotorRightVelocity = velocities.get(1);
        List<Double> speeds = launchMotors.getSpeeds();
        launchMotorLeftSpeed = speeds.get(0);
        launchMotorRightSpeed = speeds.get(1);

        launchMotors.set(feedforward.calculate(this.launchPower));
    }
}
