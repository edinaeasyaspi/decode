package org.firstinspires.ftc.teamcode.mechanisms

import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.Robot8034

class ServoK (val servo: Servo, val min: Double, val max: Double) {
    var stat: String = "inactive";

    init {
        servo.position = min
    }
    fun upDown() {
        val thread = Thread {
            stat = "begin"
            servo.position = max
            stat = "max - sleeping"
            Thread.sleep(700)
            stat = "sleep fin"
            servo.position = min
            stat = "min - finish"
        }
        stat = "prestart"
        thread.start()
        stat = "post start"
    }
}