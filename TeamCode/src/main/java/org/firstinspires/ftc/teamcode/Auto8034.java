/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.purepursuit.Path;
import com.seattlesolvers.solverslib.purepursuit.Waypoint;

import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;

/*
 * Auto opMode for FTC Team 8034.
 * It has some parts of PedroPathing, but due to time constraints it is not fully implemented.
 //TODOs:
 * * A state machine that uses mecanum drive to follow paths and perform actions is the current goal.
 */
@TeleOp(name = "Auto8034", group = "Autonomous")
//@Disabled
public class Auto8034 extends OpMode {
    private RobotHardware robot = new RobotHardware(this);
    AutonomousConfiguration autonomousConfiguration = new AutonomousConfiguration();
    private MecanumDrive mecanumDrive;
    private IntakeManager intakeManager;
    private LaunchManager launchManager;
    private ArtifactCellManager cellManager;

    private ElapsedTime runtime = new ElapsedTime();
    // Get the alliance color from the autonomous configuration
    private AutonomousOptions.AllianceColor allianceColor = autonomousConfiguration.getAlliance();
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
    private double allianceGoalOffset =
            autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 96; // Offset to be added/subtracted based on alliance color
    private double allianceAudienceOffset =
            autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 48; // Offset to be added/subtracted based on alliance color
    private final Pose startPoseGoalGate = new Pose(28.5 + allianceGoalOffset, 128, Math.toRadians(180));
    private final Pose startPoseGoalWall = new Pose(62 + allianceGoalOffset, 134, Math.toRadians(135));
    private final Pose startPoseAudienceTeam = new Pose(48 + allianceAudienceOffset, 9, Math.toRadians(105));
    private final Pose startPoseGoalAudienceCenter = new Pose(28.5 + allianceAudienceOffset, 128, Math.toRadians(180));
    private final Pose scorePose = new Pose(62 + allianceGoalOffset, 81, Math.toRadians(135));


    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        // Initialize the robot hardware
        robot.init();
        autonomousConfiguration.init(this.gamepad1, this.telemetry, hardwareMap.appContext);
        mecanumDrive = robot.mecanumDrive;
        intakeManager = robot.intakeManager;
        launchManager = robot.launchManager;
        cellManager = robot.cellManager;
    }

    /**
     * This method will be called repeatedly during the period between when
     * the INIT button is pressed and when the START button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
        // Call the autonomous configuration init loop to allow option selection
        autonomousConfiguration.init_loop();
    }

    /**
     * This method will be called once, when the START button is pressed.
     */
    @Override
    public void start() {
        if (!autonomousConfiguration.getReadyToStart()) {
            telemetry.addData("Alert", "Not ready to start!");
            telemetry.speak("Not ready to start!");
            runtime.reset();
            while (runtime.seconds() < 2) {
            }
            requestOpModeStop();
        }

        runtime.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the START button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        telemetry.addData("Alliance", autonomousConfiguration.getAlliance());
        telemetry.addData("Start Position", autonomousConfiguration.getStartPosition());
        telemetry.addData("Retrieve from Spike", autonomousConfiguration.getRetrieveFromSpike());
        telemetry.addData("Delay Start", autonomousConfiguration.getDelayStartSeconds());
        telemetry.addData("Ready to Start", autonomousConfiguration.getReadyToStart());
        telemetry.update();

    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {

    }

    public void buildPaths() {

    }

    public void updatePaths() {

    }
}
