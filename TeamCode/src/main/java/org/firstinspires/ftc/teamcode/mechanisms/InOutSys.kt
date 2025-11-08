package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.DcMotor
class InOutSys(var motor1: DcMotor, var motor2: DcMotor, var motor3: DcMotor , var motor4: DcMotor) {
    fun outon() {
        motor3.setPower(0.40);
        motor4.setPower(-0.40);
    }
    fun outoff() {
        motor3.setPower(0.0);
        motor4.setPower(0.0);
    }
    fun out1on() {
        motor3.setPower(0.33);
        motor4.setPower(-0.33);
    }
    fun inon() {
        motor1.setPower(1.0);
        motor2.setPower(-0.65);
    }
    fun inoff() {
        motor1.setPower(0.0);
        motor2.setPower(0.0);
    }
}