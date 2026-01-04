package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

import java.util.List;

public class LaunchManager {
    public final MotorEx rlm;//right launch motor
    public final MotorEx llm;//left launch motor

    public double leftVelocity;
    public double rightVelocity;

    private double expectedVelocity = 0;

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
    public void launchOn(double launchVol) {
        //launchMotors.set(launchPower);
        rightVelocity = launchVol;
        leftVelocity = launchVol;
        llm.setVelocity(leftVelocity);
        rlm.setVelocity(rightVelocity);
        expectedVelocity = launchVol;
        launchServo.setPower(-1);
    }

    public void launchOff() {
        launchMotors.set(0);
        launchServo.setPower(0);
    }

    public void getToExpectedVelocity() {
        double currentRightVol = rlm.getVelocity();
        double currentLeftVol = llm.getVelocity();

        //I really could have done this in one line if statements :(
        if (currentRightVol > expectedVelocity + 20) {
            rightVelocity -= 20;
        } else if (currentRightVol < expectedVelocity - 20) {
            rightVelocity += 20;
        }
        if (currentLeftVol > expectedVelocity + 20) {
            leftVelocity -= 20;
        } else if (currentLeftVol < expectedVelocity - 20) {
            leftVelocity += 20;
        }

        rlm.setVelocity(rightVelocity);
        llm.setVelocity(leftVelocity);
    }
}
