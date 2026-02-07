package org.firstinspires.ftc.teamcode.simplemotor;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;

public class MovementManager {
    private MecanumDrive mecanumDrive;
    private ElapsedTime driveTimer;

    private int forwordDriveTime;
    private int strafeDriveTime;
    private int turnDriveTime;

    //Sets everything up
    public void load(MecanumDrive mecanumDrive1) {
        this.mecanumDrive = mecanumDrive1;
        driveTimer = new ElapsedTime();
        Constants constants = new Constants();
        forwordDriveTime = constants.forwordDriveTime;
        strafeDriveTime = constants.strafeDriveTime;
        turnDriveTime = constants.turnDriveTime;
    }

    //Scalable movement and speed for turning so we can be exact
    //Used all the functions
    public void turn(double multiplier, double speed) {
        mecanumDrive.driveRobotCentric(0,0,1 * speed);
        driveTimer.reset();
        while (driveTimer.milliseconds() * Math.abs(speed) < turnDriveTime * multiplier) {
            sleep(100);
        }
        mecanumDrive.stop(); //I don't know if the motors actually stop moving which is what I want
    }
    public void moveForward(double multiplier, double speed) {
        mecanumDrive.driveRobotCentric(0,1 * speed,0);
        driveTimer.reset();
        while (driveTimer.milliseconds() * Math.abs(speed) < forwordDriveTime * multiplier) {
            sleep(100);
        }
        mecanumDrive.stop(); //I don't know if the motors actually stop moving which is what I want
    }

    public void moveStrafe(double multiplier, double speed) {
        mecanumDrive.driveRobotCentric(1 * speed,0,0);
        driveTimer.reset();
        while (driveTimer.milliseconds() * Math.abs(speed) < strafeDriveTime * multiplier) {
            sleep(100);
        }
        mecanumDrive.stop(); //I don't know if the motors actually stop moving which is what I want
    }

}
