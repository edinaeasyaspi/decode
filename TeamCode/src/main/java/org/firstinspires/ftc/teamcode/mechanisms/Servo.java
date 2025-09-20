package org.firstinspires.ftc.teamcode.mechanisms;

public class Servo extends Thread {
    com.qualcomm.robotcore.hardware.Servo servo;
    float min;
    float max;
    public Servo(com.qualcomm.robotcore.hardware.Servo servo, float min, float max) {
        this.servo = servo;
        this.min= min;
        this.max =  max;
        servo.setPosition(min);
    }
    public void DownUpDown() {
        start();
    }
    public void run() {
        servo.setPosition(max);
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        servo.setPosition(min);
    }
}
