package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="IntakeTesting")
public class MotorTest extends LinearOpMode{
    DcMotor intakeMotor = null;
    DcMotor intakeMotor2 = null;
    double newpower = 0;
    double negative_power = 0;
    public void runOpMode() {
        intakeMotor = hardwareMap.get(DcMotor.class, "MotorOne");
        intakeMotor2 = hardwareMap.get(DcMotor.class, "MotorTwo");
        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                newpower += 0.01;
            }
            if (gamepad1.b) {
                newpower -= 0.01;
            }
            negative_power = -1 * newpower;
            telemetry.addData("Power:", newpower);
            intakeMotor.setPower(negative_power);
            intakeMotor2.setPower(newpower);
            telemetry.update();
            sleep(100);
        }
    }
}
