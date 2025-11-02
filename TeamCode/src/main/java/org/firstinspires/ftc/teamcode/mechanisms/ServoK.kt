package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Robot8034
import kotlin.concurrent.thread

class ServoK (val servo: Servo, val min: Double, val max: Double) {
    init {
        servo.position = min
    }

    fun upDown() {
        val thread = Thread {
            servo.position = max
            Thread.sleep(700)
            servo.position = min
        }
        thread.start()
    }
    fun supDown() {
        val thread = Thread {
            Thread.sleep(1500);
            servo.position = max
            Thread.sleep(700);
            servo.position = min;
        }
        thread.start()
    }
}