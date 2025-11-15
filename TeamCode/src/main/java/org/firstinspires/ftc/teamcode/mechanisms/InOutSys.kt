package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.DcMotor
import kotlin.concurrent.thread

class InOutSys(var motor1: DcMotor, var motor2: DcMotor, var motor3: DcMotor , var motor4: DcMotor) {
    var motpow1: Double = 0.0
    var motpow2: Double = 0.0
    var motpow3: Double = 0.0
    var motpow4: Double = 0.0
    init {
        val Thread = thread {
            while (true) {
                motor1.setPower(motpow1);
                motor2.setPower(motpow2);
                motor3.setPower(motpow3);
                motor4.setPower(motpow4);
                Thread.sleep(500);
            }
        }
        Thread.start()
    }
    fun outon() {
        motpow3 = 0.4;
        motpow4 = -0.4;
    }

    fun outoff() {
        motpow3 = 0.0;
        motpow4 = 0.0;
    }

    fun out1on() {
        motpow3 = 0.30;
        motpow4 = -0.30;
    }

    fun inon() {
        motpow1 = 1.0;
        motpow2 = -0.65;
    }

    fun inoff() {
        motpow1 = -1.0;
        motpow2 = 0.5;
        val Thread = thread {
            Thread.sleep(1000);
            motpow1 = 0.0;
            motpow2 = 0.0;
        }
        Thread.start();
    }
}