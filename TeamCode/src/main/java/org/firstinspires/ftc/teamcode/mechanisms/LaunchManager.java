package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

public class LaunchManager {
    public final MotorEx rlm;//right launch motor
    public final MotorEx llm;//left launch motor

    private double leftVelocity;
    private double rightVelocity;

    private double minCheckVelocity;
    public boolean speedCheckConfimed;
    public boolean actuallyWorking;

    public LaunchManager(MotorEx leftLaunchMotor, MotorEx rightLaunchMotor, CRServo launchServo) {
        this.launchServo = launchServo;
        leftLaunchMotor.setInverted(false);
        rightLaunchMotor.setInverted(true);

        this.llm = leftLaunchMotor;
        this.rlm = rightLaunchMotor;

        this.launchMotors = new MotorGroup(leftLaunchMotor, rightLaunchMotor);
        this.launchMotors.setRunMode(MotorEx.RunMode.VelocityControl);
    }

    private final CRServo launchServo;
    public final MotorGroup launchMotors;

    // Launch power allows a variable speed launch.
    public void launchOn(double launchPower) {
        //launchMotors.set(launchPower);
        rightVelocity = 800;
        leftVelocity = 800;
        llm.setVelocity(leftVelocity);
        rlm.setVelocity(rightVelocity);
        minCheckVelocity = 550;
        speedCheckConfimed = false;
        actuallyWorking = false;
        launchServo.setPower(-1);
    }

    public void launchOff() {
        launchMotors.set(0);
        launchServo.setPower(0);
    }

    public void matchVelocities() {
        double currentRightVelocity = rlm.getVelocity();
        double currentLeftVelocity = llm.getVelocity();
        if ((currentLeftVelocity > minCheckVelocity && currentRightVelocity > minCheckVelocity) || speedCheckConfimed) {
            speedCheckConfimed = true;
            double difference;
            if (currentLeftVelocity > currentRightVelocity) {
                difference = currentLeftVelocity - currentRightVelocity;
            } else if (currentRightVelocity > currentLeftVelocity) {
                difference = currentRightVelocity - currentLeftVelocity;
            } else {
                difference = 0.0;
            }

            if (difference >= 40) {
                actuallyWorking = true;
                if (currentLeftVelocity > currentRightVelocity) {
                    leftVelocity -= 20;
                    rightVelocity += 20;
                    llm.setVelocity(leftVelocity);
                    rlm.setVelocity(rightVelocity);
                }
                if (currentRightVelocity > currentLeftVelocity) {
                    rightVelocity -= 20;
                    leftVelocity += 20;
                    llm.setVelocity(leftVelocity);
                    rlm.setVelocity(rightVelocity);
                }
            }
        }

    }
}
