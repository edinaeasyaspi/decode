package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Robot8034

class ServoK (val servo: Servo, val min: Double, val max: Double) {
    fun UpDown() {
        val thread = Thread {
            servo.setPosition(max)
            Thread.sleep(700)
            servo.setPosition(min)
        }
        thread.start()

    }
}