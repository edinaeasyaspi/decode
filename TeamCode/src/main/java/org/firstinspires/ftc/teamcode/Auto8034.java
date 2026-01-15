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
import com.pedropathing.paths.Path;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;

import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

/*
 * Auto opMode for FTC Team 8034.
 * It has some parts of PedroPathing, but due to time constraints it is not fully implemented.
 //TODOs:
 * * A state machine that uses mecanum drive to follow paths and perform actions is the current goal.
 */
@Autonomous(name = "Auto8034", group = "Autonomous", preselectTeleOp = "TeleOp8034")
//@Disabled
public class Auto8034 extends OpMode {
    private RobotHardware robot = new RobotHardware(this);
    AutonomousConfiguration autonomousConfiguration = new AutonomousConfiguration();
    private MecanumDrive mecanumDrive;
    private IntakeManager intakeManager;
    private LaunchManager launchManager;
    private ArtifactCellManager cellManager;

    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime delayTimer = new ElapsedTime();
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    // Used to track the current state of the autonomous path.
    // For better documentation, consider using an enum.
    // Get the alliance color from the autonomous configuration
    private int pathState;
    private AutonomousOptions.AllianceColor allianceColor = autonomousConfiguration.getAlliance();
    private int startDelaySeconds = autonomousConfiguration.getDelayStartSeconds();

    private double allianceGoalOffset =
            autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 96; // Offset to be added/subtracted based on alliance color
    private double allianceAudienceOffset =
            autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 48; // Offset to be added/subtracted based on alliance color
    private final Pose startPoseGoalGate = new Pose(28.5 + allianceGoalOffset, 128, Math.toRadians(180));
    private final Pose startPoseGoalWall = new Pose(62 + allianceGoalOffset, 134, Math.toRadians(135));
    private final Pose startPoseAudienceTeam = new Pose(48 + allianceAudienceOffset, 9, Math.toRadians(105));
    private final Pose startPoseGoalAudienceCenter = new Pose(28.5 + allianceAudienceOffset, 128, Math.toRadians(180));
    private final Pose scorePreload = new Pose(62 + allianceGoalOffset, 81, Math.toRadians(135));

    private Path scorePreloadPath;


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
        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(getStartPose());
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

        // Apply any requested delay before starting
        delayTimer.reset();
        while (delayTimer.seconds() < startDelaySeconds) {
        }

        // Set the starting pose based on the selected starting position
        setPathState(0);
        runtime.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the START button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

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

    // State machine for autonomous path following and actions
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(scorePreloadPath);
                setPathState(1);
                break;
            case 1:
                // This case will execute the AprilTag detection and open the artifact cells
                if (robot.aprilTagManager.execute(true)) {
                    cellManager.openCell(ArtifactCellManager.CELL.Left);
                    cellManager.openCell(ArtifactCellManager.CELL.Center);
                    cellManager.openCell(ArtifactCellManager.CELL.Right);
                } else {
                    break;
                }

                setPathState(2);
                break;
            case 2:
                //TODO: Keep building paths while there is time.
                setPathState(3);
                break;
            case 3:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
                }
                break;
        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void buildPaths() {
        Pose startPose = getStartPose();
        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
        scorePreloadPath = new Path(new BezierLine(startPose, scorePreload));
        scorePreloadPath.setLinearHeadingInterpolation(startPose.getHeading(), scorePreload.getHeading());
    }

    public void updatePaths() {

    }

    private Pose getStartPose() {
        switch (autonomousConfiguration.getStartPosition()) {
            case GoalGate:
                return startPoseGoalGate;
            case GoalWall:
                return startPoseGoalWall;
            case AudienceTeam:
                return startPoseAudienceTeam;
            case AudienceCenter:
                return startPoseGoalAudienceCenter;
            default:
                // If something went wrong, return a default pose
                return new Pose(0, 0, 0);
        }
    }
}
