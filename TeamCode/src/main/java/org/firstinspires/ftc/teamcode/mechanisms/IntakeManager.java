package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.Motor;
import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class IntakeManager {
    public IntakeManager(MotorEx leftIntakeMotor, MotorEx rightIntakeMotor) {
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
        this.leftIntakeMotor.setInverted(false);
        this.rightIntakeMotor.setInverted(true);
        intakeMotors = new MotorGroup(leftIntakeMotor, rightIntakeMotor);
        intakeMotors.setRunMode(Motor.RunMode.VelocityControl);
    }

    private final MotorEx leftIntakeMotor;
    private final MotorEx rightIntakeMotor;
    private final MotorGroup intakeMotors;
    private final double INTAKE_ON_POWER = 0.8;

    public void intakeOn() {
        leftIntakeMotor.set(1);
        rightIntakeMotor.set(0.75);
    }

    public void intakeOff() {
        intakeMotors.set(0);
    }
}
