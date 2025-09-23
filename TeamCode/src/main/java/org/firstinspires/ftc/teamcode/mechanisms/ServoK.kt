package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Robot8034

class ServoK (val servo: Servo, val min: Double, val max: Double) {
    fun upDown() {
        val thread = Thread {
            servo.position = max
            Thread.sleep(700)
            servo.position = min
        }
        thread.start()

    }
}