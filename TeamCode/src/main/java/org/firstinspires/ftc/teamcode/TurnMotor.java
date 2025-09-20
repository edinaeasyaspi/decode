package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.inputsys.Input;
import org.firstinspires.ftc.teamcode.inputsys.KeyCode;
import org.firstinspires.ftc.teamcode.mechanisms.Servo;

@TeleOp(name="TurnMotor")
public class TurnMotor extends LinearOpMode{
    DcMotor motor = null;
    public void runOpMode() {
        motor = hardwareMap.get(DcMotor.class, "MotorOne");
        waitForStart();
        motor.setPower(-1);
        sleep(10000);
        motor.setPower(0);
    }
}
