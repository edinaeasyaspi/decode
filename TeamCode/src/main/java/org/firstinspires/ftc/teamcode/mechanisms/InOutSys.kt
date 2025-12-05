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
    var mod: Double = 0.0;
    var moddiv: Double = 17.0;
    fun outon() {
        cv = vs.getVoltage();
        motpow3 = 0.4 + (-cv + 11.75)/moddiv;
        motpow4 = -0.4 - (-cv + 11.75)/moddiv;
        mod = (-cv + 11.75)/moddiv;
        motor3.setPower(motpow3);
        motor4.setPower(motpow4);
    }

    fun outoff() {
        motpow3 = 0.0;
        motpow4 = 0.0;
        motor3.setPower(motpow3);
        motor4.setPower(motpow4);
    }

    fun out1on() {
        cv = vs.getVoltage();
        motpow3 = 0.45 + (-cv + 11.75)/moddiv;
        motpow4 = -0.45 - (-cv + 11.75)/moddiv;
        mod = (-cv + 11.75)/moddiv;
        motor3.setPower(motpow3);
        motor4.setPower(motpow4);
    }

    fun inon() {
        motpow1 = 1.0;
        motpow2 = -0.75;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
    }

    fun inoff() {
        motpow1 = -1.0;
        motpow2 = 0.5;
        motor1.setPower(motpow1);
        motor2.setPower(motpow2);
        val Thread = thread {
            Thread.sleep(1000);
            motpow1 = 0.0;
            motpow2 = 0.0;
            motor1.setPower(motpow1);
            motor2.setPower(motpow2);
        }
        Thread.start();
    }
}