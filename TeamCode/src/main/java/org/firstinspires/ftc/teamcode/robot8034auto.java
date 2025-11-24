package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.mechanisms.InOutSys;
import org.firstinspires.ftc.teamcode.mechanisms.ServoK;

@Autonomous(name="BasicMotor")
public class robot8034auto extends LinearOpMode {
    private DcMotor fl;
    private DcMotor bl;
    private DcMotor fr;
    private DcMotor br;
    private ServoK servoOne;
    private ServoK servoTwo;
    private ServoK servoThree;
    private CRServo servoFour;
    private InOutSys IOsys;

    @Override
    public void runOpMode() throws InterruptedException {
        fl = hardwareMap.get(DcMotor.class, "MotorOne");
        bl = hardwareMap.get(DcMotor.class, "MotorTwo");
        fr = hardwareMap.get(DcMotor.class, "MotorThree");
        br = hardwareMap.get(DcMotor.class, "MotorFour");
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);
        servoOne = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoOne"),
                1.000,0.766);
        servoTwo = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoTwo"),
                0.709, 0.486);
        servoThree = new ServoK(
                hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, "ServoThree"),
                0.746, 0.78);
        IOsys = new InOutSys(
                hardwareMap.get(DcMotor.class, "MotorFive"),
                hardwareMap.get(DcMotor.class, "MotorSix"),
                hardwareMap.get(DcMotor.class, "MotorSeven"),
                hardwareMap.get(DcMotor.class, "MotorEight")
        );
        servoFour = hardwareMap.get(CRServo.class, "ServoFive");
        waitForStart();
        forward(1);
        sleep(700);
        forward(0);
        servoFour.setPower(-1);
        IOsys.outon();
        shootall(1);
        sleep(2000);
        IOsys.outoff();
        servoFour.setPower(0);
        turnRight(-1);
        sleep(400);
        forward(0);
        while (opModeIsActive()) sleep(1000);
    }
    public void forward(double power) {
        fl.setPower(power);
        bl.setPower(power);
        fr.setPower(power);
        br.setPower(power);
    }
    public void right(double power) {
        fl.setPower(power);
        bl.setPower(-1*power);
        fr.setPower(-1*power);
        br.setPower(power);
    }
    public void turnRight(double power) {
        fl.setPower(power);
        bl.setPower(power);
        fr.setPower(-1*power);
        br.setPower(-1*power);
    }
    public void shootall(int start) {
        sleep(1500);
        servoTwo.upDown();
        sleep(4000);
        servoThree.upDown();
//        switch (start) {
//            case 1:
//                servoOne.upDown();
//            case 2:
//                servoTwo.upDown();
//            case 3:
//                servoThree.upDown();
//            case 4:
//                servoOne.upDown();
//            case 5:
//                servoTwo.upDown();
//        }
        sleep(4000);
        servoOne.upDown();
    }
}
