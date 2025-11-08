package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="IntakeTesting")
public class MotorTest extends LinearOpMode{
    DcMotor intakeMotor = null;
    DcMotor intakeMotor2 = null;
    double newpower = 0;
    double newpower2 = 0;
    public void runOpMode() {
        intakeMotor = hardwareMap.get(DcMotor.class, "MotorSeven");
        intakeMotor2 = hardwareMap.get(DcMotor.class, "MotorEight");
        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                newpower += 0.01;
            }
            if (gamepad1.b) {
                newpower -= 0.01;
            }
            if (gamepad1.y) {
                newpower2 -= 0.01;
            }
            if (gamepad1.x) {
                newpower2 += 0.01;
            }
            telemetry.addData("Power:", newpower);
            telemetry.addData("Power2:", newpower2);
            intakeMotor.setPower(newpower);
            intakeMotor2.setPower(newpower2);
            telemetry.update();
            sleep(100);
        }
    }
}
