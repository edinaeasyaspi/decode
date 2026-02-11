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

import android.graphics.Bitmap;

import com.acmerobotics.dashboard.FtcDashboard;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.simplemotor.MovementManager;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

/*
 * Auto opMode for FTC Team 8034.
 * It has some parts of PedroPathing, but due to time constraints it is not fully implemented.
 //TODO: Refactor to fully use PedroPathing.
 */

@Autonomous(name = "Auto8034", group = "Autonomous", preselectTeleOp = "Robot8034")
//@Disabled
public class Auto8034 extends OpMode {
    private final RobotHardware robot = new RobotHardware(this);
    AutonomousConfiguration autonomousConfiguration = new AutonomousConfiguration();
    private ArtifactCellManager cellManager;

    private final ElapsedTime runtime = new ElapsedTime();
    private final ElapsedTime delayTimer = new ElapsedTime();
    private ElapsedTime driveTimer;
    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    // Used to track the current state of the autonomous path.
    // For better documentation, consider using an enum.
    private int pathState;
    // Get the alliance color from the autonomous configuration
    private AutonomousOptions.AllianceColor allianceColor;
    private AutonomousOptions.StartPosition startPosition;
    private int startDelaySeconds;
    // Default motif assumes we haven't detected it yet.
    private ArtifactCellManager.motif currentMotif = ArtifactCellManager.motif.NONE;

    private double allianceGoalOffset;
    private double allianceAudienceOffset;
    private Pose startPoseGoalGate = new Pose(28.5 + allianceGoalOffset, 128, Math.toRadians(180));
    private Pose startPoseGoalWall = new Pose(62 + allianceGoalOffset, 134, Math.toRadians(135));
    private Pose startPoseAudienceTeam = new Pose(48 + allianceAudienceOffset, 9, Math.toRadians(105));
    private Pose startPoseGoalAudienceCenter = new Pose(28.5 + allianceAudienceOffset, 128, Math.toRadians(180));
    private Pose scorePreload = new Pose(62 + allianceGoalOffset, 81, Math.toRadians(135));
    private Pose moveOffLaunchLine = new Pose(54 + allianceGoalOffset, 69, Math.toRadians(135));

    private Path scorePreloadPath;
    private Path moveOffLaunchLinePath;
    private FtcDashboard dashboard;
    private Telemetry telemetry2;

    private MovementManager movementManager;
    private IntakeManager intakeManager;


    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        // Enable dashboard.
        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();

        // Initialize the robot hardware
        robot.init();

        movementManager = new MovementManager();
        movementManager.load(robot.mecanumDrive);

        intakeManager = robot.intakeManager;

        // Let driver select autonomous configuration.
        autonomousConfiguration.init(this.gamepad1, this.telemetry, hardwareMap.appContext);
        autonomousConfiguration.ShowHelp();
        cellManager = robot.cellManager;
        // Enable camera stream to dashboard.
        //TODO If this is not useful, remove it to save resources.
        final CameraStreamProcessor processor = new CameraStreamProcessor();
        new VisionPortal.Builder()
                .addProcessor(processor)
                .setCamera(robot.webcamName)
                .build();
        dashboard.startCameraStream(processor, 0);

        driveTimer = new ElapsedTime();
//        follower = Constants.createFollower(hardwareMap);
//        buildPaths();
//        follower.setStartingPose(getStartPose());
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
        //TODO: These are for PedroPathing implementation.
//        startPoseGoalGate = new Pose(28.5 + allianceGoalOffset, 128, Math.toRadians(180));
//        startPoseGoalWall = new Pose(62 + allianceGoalOffset, 134, Math.toRadians(135));
//        startPoseAudienceTeam = new Pose(48 + allianceAudienceOffset, 9, Math.toRadians(105));
//        startPoseGoalAudienceCenter = new Pose(28.5 + allianceAudienceOffset, 128, Math.toRadians(180));
//        scorePreload = new Pose(62 + allianceGoalOffset, 81, Math.toRadians(135));
//        moveOffLaunchLine = new Pose(54 + allianceGoalOffset, 69, Math.toRadians(135));

        allianceColor = autonomousConfiguration.getAlliance();
        startPosition = autonomousConfiguration.getStartPosition();
        startDelaySeconds = autonomousConfiguration.getDelayStartSeconds();
        allianceGoalOffset =
                autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 96; // Offset to be added/subtracted based on alliance color
        allianceAudienceOffset =
                autonomousConfiguration.getAlliance() == AutonomousOptions.AllianceColor.Blue ? 0 : 48; // Offset to be added/subtracted based on alliance color

        // Apply any requested delay before starting
        delayTimer.reset();
        while (delayTimer.seconds() < startDelaySeconds) {
        }

        //TODO: This assumes the robot can see the obelisk at start. If that is not true, move
        // this to the state machine so the robot can move into position..
        currentMotif = robot.aprilTagManager.findMotif();
        // Set the starting pose based on the selected starting position
        if (startPosition == AutonomousOptions.StartPosition.GoalGate) {
            robot.launchManager.launchOn(0.25);
            setPathState(0);
        } else {
            robot.launchManager.launchOn(0.315);
            setPathState(2);
        }
        runtime.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the START button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
//        follower.update();
        cellManager.execute();
        robot.launchManager.execute();
        if (startPosition == AutonomousOptions.StartPosition.GoalGate) {
            startGoalPathUpdate();
        } else {
            startAudiencePathUpdate();
        }

        telemetry.addData("Status", "Run Time: " + runtime);
        telemetry.addData("Alliance", allianceColor);
        telemetry.addData("Start Position", startPosition);
        telemetry.addData("Retrieve from Spike", autonomousConfiguration.getRetrieveFromSpike());
        telemetry.addData("Delay Start", startDelaySeconds);
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
    /// This is a rough draft not the final disign there are many parts missing
    public int launches = 0;
    public List<ArtifactCellManager.CELL> launchOrer;
    public int afterShoot;
    public void startAudiencePathUpdate() {
        switch (pathState) {
            case 0:
                launchOrer = cellManager.noColorLaunch();
                cellManager.openCell(launchOrer.get(launches));
                robot.launchManager.launchOn(0.255);
                launches++;
                driveTimer.reset();
                afterShoot = 2;
                setPathState(1);
                break;
            case 1:
                if (driveTimer.milliseconds() >= 1200) {
                    cellManager.openCell(launchOrer.get(launches));
                    launches++;
                    driveTimer.reset();
                    if (launches == 3) {
                        setPathState(afterShoot);
                        break;
                    }
                }
            case 2:
                if (allianceColor == AutonomousOptions.AllianceColor.Red) {
                    movementManager.turn(1.233, 1);
                    movementManager.moveStrafe(1,-1);
                } else {
                    movementManager.turn(0.74, -1);
                    movementManager.moveStrafe(1,1);
                }
                setPathState(3);
                break;
            case 3:
                intakeManager.intakeOn();
                movementManager.moveForward(1.25, 1);
                movementManager.moveForward(1.25, -1);
                setPathState(4);
                break;
            case 4:
                if (allianceColor == AutonomousOptions.AllianceColor.Red) {
                    movementManager.moveStrafe(1,1);
                    movementManager.turn(1.233, -1);
                } else {
                    movementManager.moveStrafe(1,-1);
                    movementManager.turn(0.74, 1);
                }
                setPathState(5);
            case 5:
                launchOrer = cellManager.noColorLaunch();
                cellManager.openCell(launchOrer.get(launches));
                robot.launchManager.launchOn(0.255);
                launches++;
                driveTimer.reset();
                afterShoot = 6;
                setPathState(1);
                break;
            case 6:
                //Wait here
        }
    }
    public void startGoalPathUpdate() {
        switch (pathState) {
            case 0:
                //I know that holding the loop up to move is controversial
                //I'm still going to do it
                movementManager.moveForward(1.5, 1);
                setPathState(1);
                break;
            case 1:
                //We don't have enough time to figure out how to look at the colors
                //If you want to make it look at them you can
                launchOrer = cellManager.noColorLaunch();
                robot.launchManager.launchOn(0.2);
                cellManager.openCell(launchOrer.get(launches));
                launches++;
                driveTimer.reset();
                setPathState(2);
                break;
            case 2:
                if (driveTimer.milliseconds() >= 1200) {
                    cellManager.openCell(launchOrer.get(launches));
                    launches++;
                    driveTimer.reset();
                    if (launches == 3) {
                        setPathState(3);
                        break;
                    }
                }
            case 3:
                if (allianceColor == AutonomousOptions.AllianceColor.Blue) {
                    //The accuracy of the fifty degree turn is adjustable because it is 0.55...
                    movementManager.turn(0.555,-1);
                    movementManager.moveStrafe(0.5, -1);
                } else {
                    movementManager.turn(0.555, 1);
                    movementManager.moveStrafe(0.5, 1);
                }
                setPathState(4); //I didn't feel like shoving all this in one case
                break;
            case 4:
                intakeManager.intakeOn();
                movementManager.moveForward(1.5, 1);
                movementManager.moveForward(1.5, -1);
                //We don't turn the intake off because then a ball could get stuck
                setPathState(5);
                break;
            case 5:
                if (allianceColor == AutonomousOptions.AllianceColor.Blue) {
                    movementManager.moveStrafe(0.5, 1);
                    movementManager.turn(0.555,1);
                } else {
                    movementManager.moveStrafe(0.5, -1);
                    movementManager.turn(0.555, -1);
                }
                setPathState(6);
                break;
            case 6:
                launches = 0;
                launchOrer = cellManager.noColorLaunch();
                cellManager.openCell(launchOrer.get(launches));
                launches++;
                driveTimer.reset();
                setPathState(7);
                break;
            case 7:
                if (driveTimer.milliseconds() >= 1200) {
                    cellManager.openCell(launchOrer.get(launches));
                    launches++;
                    driveTimer.reset();
                    if (launches == 3) {
                        setPathState(8);
                        break;
                    }
                }
            case 8:
                //Once we test this we can expand this to go pick up different spots
                //This can just wait here
        }
    }

    // State machine for autonomous path following and actions
    //TODO: Refine this and convert it to enums for better documentation.
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                robot.mecanumDrive.driveRobotCentric(0, .5, 0, false);
                driveTimer.reset();
                setPathState(1);
                break;
            case 1:
                if (driveTimer.milliseconds() > 1750) {
                    robot.mecanumDrive.stop();
                    setPathState(2);
                    driveTimer.reset();
                }
                break;
            case 2:
                //TODO: Change this to use the obelisk motif to determine the sequence for launching.
                robot.cellManager.openCell(ArtifactCellManager.CELL.Left);
                driveTimer.reset();
                setPathState(5);
                break;
            case 3:
                // This case will move the robot off the launch line
                if (driveTimer.milliseconds() > 2000) {
                    // On the goal side color controls the strafe direction.
                    if (startPosition == AutonomousOptions.StartPosition.GoalGate) {
                        if (allianceColor == AutonomousOptions.AllianceColor.Blue) {
                            robot.mecanumDrive.driveRobotCentric(.5, 0, 0, false);
                        } else {
                            robot.mecanumDrive.driveRobotCentric(-0.5, 0, 0, false);

                        }
                    } else {
                        // Audience side just drives forward
                        robot.mecanumDrive.driveRobotCentric(0, 0.5, 0, false);
                    }
                    driveTimer.reset();
                    setPathState(4);
                }
                break;
            case 4:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (driveTimer.milliseconds() > 750) {
                    robot.mecanumDrive.stop();
                    /* Set the state to a Case we won't use or define, so it just stops running an new paths */
                    setPathState(-1);
                }
                break;
            case 5:
                if (driveTimer.milliseconds() > 2000) {
                    robot.cellManager.openCell(ArtifactCellManager.CELL.Center);
                    driveTimer.reset();
                    setPathState(6);
                }
            case 6:
                if (driveTimer.milliseconds() > 2000) {
                    robot.cellManager.openCell(ArtifactCellManager.CELL.Right);
                    driveTimer.reset();
                    setPathState(3);
                }
        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
//        pathTimer.resetTimer();
    }

    public void buildPaths() {
        Pose startPose = getStartPose();
        /* This is our scorePreload path. We are using a BezierLine, which is a straight line. */
        scorePreloadPath = new Path(new BezierLine(startPose, scorePreload));
        scorePreloadPath.setLinearHeadingInterpolation(startPose.getHeading(), scorePreload.getHeading());
        moveOffLaunchLinePath = new Path(new BezierLine(scorePreload, moveOffLaunchLine));
        moveOffLaunchLinePath.setLinearHeadingInterpolation(scorePreload.getHeading(), moveOffLaunchLine.getHeading());
    }

    private Pose getStartPose() {
        switch (autonomousConfiguration.getStartPosition()) {
            case GoalGate:
                return startPoseGoalGate;
            case AudienceTeam:
                return startPoseAudienceTeam;
            default:
                // If something went wrong, return a default pose
                return new Pose(0, 0, 0);
        }
    }

    public static class CameraStreamProcessor implements VisionProcessor, CameraStreamSource {
        private final AtomicReference<Bitmap> lastFrame =
                new AtomicReference<>(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

        @Override
        public void init(int width, int height, CameraCalibration calibration) {
            lastFrame.set(Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565));
        }

        @Override
        public Object processFrame(Mat frame, long captureTimeNanos) {
            Bitmap b = Bitmap.createBitmap(frame.width(), frame.height(), Bitmap.Config.RGB_565);
            Utils.matToBitmap(frame, b);
            lastFrame.set(b);
            return null;
        }

        @Override
        public void onDrawFrame(android.graphics.Canvas canvas,
                                int onscreenWidth,
                                int onscreenHeight,
                                float scaleBmpPxToCanvasPx,
                                float scaleCanvasDensity,
                                Object userContext) {
        }

        @Override
        public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
            continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(lastFrame.get()));
        }
    }
}
