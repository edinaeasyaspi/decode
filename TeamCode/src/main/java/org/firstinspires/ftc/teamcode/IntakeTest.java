package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.inputsys.Input;
import org.firstinspires.ftc.teamcode.inputsys.KeyCode;
@TeleOp(name="IntakeTesting")
public class IntakeTest extends LinearOpMode{
    DcMotor intakeMotor = null;
    int newpower = 0;
    public void runOpMode() {
        intakeMotor = hardwareMap.get(DcMotor.class, "MotorOne");
        waitForStart();
        while (opModeIsActive()) {
            newpower = 0;
            if (gamepad1.a) {
                newpower = 1;
            }
            if (gamepad1.b) {
                newpower = -1;
            }
            intakeMotor.setPower(newpower);
            sleep(100);
        }
    }
}
