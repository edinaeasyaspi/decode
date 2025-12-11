package org.firstinspires.ftc.teamcode.mechanisms;

import com.seattlesolvers.solverslib.hardware.motors.MotorEx;
import com.seattlesolvers.solverslib.hardware.motors.MotorGroup;

public class IntakeManager {
    public IntakeManager(MotorEx leftIntakeMotor, MotorEx rightIntakeMotor) {
        this.leftIntakeMotor = leftIntakeMotor;
        this.rightIntakeMotor = rightIntakeMotor;
    }

    private MotorEx leftIntakeMotor;
    private MotorEx rightIntakeMotor;
    private MotorGroup intakeMotors = new MotorGroup(leftIntakeMotor, rightIntakeMotor);
    private double INTAKE_ON_POWER = 0.8;

    public void intakeOn() {
        intakeMotors.set(INTAKE_ON_POWER);
    }

    public void intakeOff() {
        intakeMotors.set(0);
    }
}
