package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.VoltageSensor
import org.firstinspires.ftc.robotcore.external.Telemetry
import kotlin.concurrent.thread

class InOutSys(var motor1: DcMotor, var motor2: DcMotor, var motor3: DcMotor , var motor4: DcMotor, var vs: VoltageSensor, var telemetry: Telemetry) {
    var motpow1: Double = 0.0
    var motpow2: Double = 0.0
    var motpow3: Double = 0.0
    var motpow4: Double = 0.0
    var cv: Double = vs.getVoltage()
    fun outon() {
        cv = vs.getVoltage();
        motpow3 = 0.41 + (-cv + 11.75)/2;
        motpow4 = -0.41 - (-cv + 11.75)/2;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        motor3.setPower(motpow3 + 0.3);
        motor4.setPower(motpow4 - 0.3);
    }

    fun outoff() {
        motpow3 = 0.0;
        motpow4 = 0.0;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        motor3.setPower(motpow3 + 0.3);
        motor4.setPower(motpow4 - 0.3);
    }

    fun out1on() {
        cv = vs.getVoltage();
        motpow3 = 0.45 + (-cv + 11.75)/2;
        motpow4 = -0.45 - (-cv + 11.75)/2;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        motor3.setPower(motpow3 + 0.3);
        motor4.setPower(motpow4 - 0.3);
    }

    fun inon() {
        motpow1 = 1.0;
        motpow2 = -0.75;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        motor3.setPower(motpow3 + 0.3);
        motor4.setPower(motpow4 - 0.3);
    }

    fun inoff() {
        motpow1 = -1.0;
        motpow2 = 0.5;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        motor3.setPower(motpow3 + 0.3);
        motor4.setPower(motpow4 - 0.3);
        val Thread = thread {
            Thread.sleep(1000);
            motpow1 = 0.0;
            motpow2 = 0.0;
            motor1.setPower(motpow1);
            motor2.setPower(motpow2);
            motor3.setPower(motpow3 + 0.3);
            motor4.setPower(motpow4 - 0.3);
        }
        Thread.start();
    }
}