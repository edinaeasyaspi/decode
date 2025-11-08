package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Robot8034
import kotlin.collections.minusAssign
import kotlin.compareTo
import kotlin.concurrent.thread



class ServoK (val servo: Servo, val min: Double, val max: Double) {
    var sleept: Int = 0
    init {
        servo.position = min
        val sthread = Thread {
            if (sleept > 0) {
                sleept -= 1;
            }
            Thread.sleep(1000);
        }
        sthread.start()
    }
    fun sleep() {
        sleept = 2
    }
    fun upDown() {
        val thread = Thread {
            if (sleept < 0.1) {
                servo.position = max
                Thread.sleep(700)
                servo.position = min
            }
        }
        thread.start()
    }
    fun supDown() {
        val thread = Thread {
            if (sleept < 0.1) {
                Thread.sleep(1500);
                servo.position = max
                Thread.sleep(700);
                servo.position = min;
            }
        }
        thread.start()
    }
}