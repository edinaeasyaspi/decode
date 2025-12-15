package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class IntakeManager {
    public IntakeManager(MotorEx leftIntakeMotor, MotorEx rightIntakeMotor) {
        leftIntakeMotor.setInverted(false);
        rightIntakeMotor.setInverted(true);
        intakeMotors = new MotorGroup(leftIntakeMotor, rightIntakeMotor);
        intakeMotors.setRunMode(Motor.RunMode.VelocityControl);
    }

    private final MotorGroup intakeMotors;

    public void intakeOn() {
        double INTAKE_ON_POWER = 0.8;
        intakeMotors.set(INTAKE_ON_POWER);
    }

    public void intakeOff() {
        intakeMotors.set(0);
    }
}
