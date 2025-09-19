package org.firstinspires.ftc.teamcode.mechanisms;

public class Servo {
    com.qualcomm.robotcore.hardware.Servo servo;
    int min;
    int max;
    public Servo(com.qualcomm.robotcore.hardware.Servo servo, int min, int max) {
        this.servo = servo;
        this.min= min;
        this.max =  max;
    }
}
