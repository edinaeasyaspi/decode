/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 *the following conditions are met:
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

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanisms.ArtifactCellManager;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeManager;
import org.firstinspires.ftc.teamcode.mechanisms.LaunchManager;

import com.seattlesolvers.solverslib.drivebase.MecanumDrive;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import com.seattlesolvers.solverslib.gamepad.ToggleButtonReader;
import com.seattlesolvers.solverslib.gamepad.TriggerReader;

import java.util.List;

/*
 * This OpMode is the main teleOp for Decode.
 */
@Config
@TeleOp(name = "Robot8034", group = "TeleOp")
public class Robot8034 extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    public boolean isAprilTagAligned = false;

    private final RobotHardware robot = new RobotHardware(this);

    ArtifactCellManager cellManager;

    //TODO: Adjust shot variables as needed
    public static double SHORT_SHOT = 0.25;
    public static double LONG_SHOT = 0.315;
    public boolean launching = false;
    public int launchStage = 0;
    public List<ArtifactCellManager.CELL> launchOrder;
    public ElapsedTime launchTimer = new ElapsedTime();
    // Track whether we are shooting far or short.
    // If there is time, implement the AprilTag to calculate a variable distance.
    private boolean SHOOT_FAR = false;

    // Control the slow mode for driving.
    boolean isSlowMode = false;

    FtcDashboard dashboard;
    Telemetry telemetry;

    GamepadEx gamePadEx;
    ToggleButtonReader aReader;
    TriggerReader leftTriggerReader;
    TriggerReader rightTriggerReader;

    @Override
    public void runOpMode() {
        // Initialize the robot hardware
        robot.init();
        // FtcDashboard setup
        dashboard = FtcDashboard.getInstance();
        telemetry = dashboard.getTelemetry();
        // Initialize the autonomous configuration to get camera settings.
        AutonomousConfiguration autonomousConfiguration = new AutonomousConfiguration();
        autonomousConfiguration.init(gamepad1, telemetry, hardwareMap.appContext);
        boolean targetFound = false;
        double drive = 0;
        double strafe = 0;
        double turn = 0;

        //Define gamepad (from SolversLib)
        gamePadEx = new GamepadEx(gamepad1);
        aReader = new ToggleButtonReader(gamePadEx, GamepadKeys.Button.A);
        leftTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.LEFT_TRIGGER);
        rightTriggerReader = new TriggerReader(gamePadEx, GamepadKeys.Trigger.RIGHT_TRIGGER);

        MecanumDrive mecanumDrive = robot.mecanumDrive;
        IntakeManager intakeManager = robot.intakeManager;
        LaunchManager launchManager = robot.launchManager;
        cellManager = robot.cellManager;

        telemetry.addData("Status", "Initialized");
        telemetry.addLine("a: Toggle Slow Mode");
        telemetry.addLine("x: Open left cell");
        telemetry.addLine("y: Open center cell");
        telemetry.addLine("b: Open right cell");
        telemetry.addLine("Right Trigger: Intake On");
        telemetry.addLine("Right Bumper: Intake Off");
        telemetry.addLine("D-Pad Up: Long shot");
        telemetry.addLine("D-Pad Down: Short shot");
        telemetry.addLine("D-Pad Left: Auto Aim");
        telemetry.addLine("D-Pad Right: turn off launch motors");
        telemetry.update();

        // Ready for start of OpMode
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Read gamepad inputs
            gamePadEx.readButtons();
            // Update the cell manager and launch manager
            cellManager.execute();
            cellManager.checkColors();
            launchManager.execute();

            // toggle slow drive mode
            if (gamePadEx.isDown(GamepadKeys.Button.A)) {
                isSlowMode = !isSlowMode;
            }

            // Activate the appropriate cell servo to launch an artifact
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.X)) {
                cellManager.setCurrentMotif(ArtifactCellManager.motif.GPP);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.Y)) {
                cellManager.setCurrentMotif(ArtifactCellManager.motif.PGP);
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.B)) {
                cellManager.setCurrentMotif(ArtifactCellManager.motif.PPG);
            }

            // Intake controls
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                intakeManager.intakeOn();
            }

            if (gamePadEx.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                intakeManager.intakeOff();
            }

            //Distance control
            // If there is time, the auto-centering could also set the power for distance.
            if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_UP)) {
                SHOOT_FAR = true;
                launchManager.launchOn(LONG_SHOT);
                launching = true;
                launchStage = 0;
            } else if (gamePadEx.wasJustReleased(GamepadKeys.Button.DPAD_DOWN)) {
                SHOOT_FAR = false;
                launchManager.launchOn(SHORT_SHOT);
                launching = true;
                launchStage = 0;
            }

            if (launching) {
                switch (launchStage) {
                    case 0:
                        launchOrder = cellManager.launchOrder();
                        launchStage = 1;
                    case 1:
                        cellManager.openCell(launchOrder.get(0));
                        launchTimer.reset();
                        launchStage = 2;
                        break;
                    case 2:
                        if (launchTimer.milliseconds() >= 1500) {
                            cellManager.openCell(launchOrder.get(1));
                            launchTimer.reset();
                            launchStage = 3;
                        }
                    case 3:
                        if (launchTimer.milliseconds() >= 1500) {
                            cellManager.openCell(launchOrder.get(2));
                        }
                }
            }

            // Auto-align to AprilTag when D-Pad left is pressed.
            if (gamePadEx.isDown(GamepadKeys.Button.DPAD_LEFT)) {
                isAprilTagAligned = robot.aprilTagManager.execute(SHOOT_FAR);
            }

            // Turn off the launch motors to save the battery.
            if (gamePadEx.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
                launchManager.launchOff();
                launching = false;
            }

            // If D-Pad left is pressed, we are auto-aligning, so don't accept joystick inputs.
            if (!gamePadEx.isDown(GamepadKeys.Button.DPAD_LEFT)) {
                double SLOW_MODE_FACTOR = 0.4;
                mecanumDrive.driveRobotCentric(isSlowMode ? gamePadEx.getLeftX() * SLOW_MODE_FACTOR : gamePadEx.getLeftX(),
                        isSlowMode ? gamePadEx.getLeftY() * SLOW_MODE_FACTOR : gamePadEx.getLeftY(),
                        isSlowMode ? gamePadEx.getRightX() * SLOW_MODE_FACTOR : gamePadEx.getRightX());
            }


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime);
            telemetry.addData("Colors", cellManager.colors());
            telemetry.addData("Movement Speed", "%s", isSlowMode ? "SLOW" : "FAST");
            telemetry.addData("Launch left speed:", launchManager.launchMotorLeftSpeed);
            telemetry.addData("Launch right speed:", launchManager.launchMotorRightSpeed);
            telemetry.addData("April tag aligned:", isAprilTagAligned);
            telemetry.update();
        }
    }
}