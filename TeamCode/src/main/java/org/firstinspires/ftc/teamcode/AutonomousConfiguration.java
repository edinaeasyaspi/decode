package org.firstinspires.ftc.teamcode;

import android.content.Context;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by Ron on 11/16/2016.
 * Modified: 10/31/2022
 * <p>
 * This class provides configuration for an autonomous opMode.
 * Most games benefit from autonomous opModes that can implement
 * different behavior based on an alliance strategy agreed upon
 * for a specific match.
 * </p>
 * <p>
 * Creating multiple opModes to meet this requirement results in duplicate
 * code and an environment that makes it too easy for a driver to
 * choose the wrong opMode "in the heat of battle."
 * </p>
 * <p>
 * This class is a way to solve these problems.
 * It is designed to used from opMode (iterative) class.
 * The selected options can also be saved to a file, allowing the
 * configuration options to be set before a match and to be available
 * to any op Mode.
 * </p>
 */

public class AutonomousConfiguration {
    private AutonomousOptions autonomousOptions;
    private Gamepad gamepad;
    private Context context;
    private boolean readyToStart;
    private boolean savedToFile;
    private Telemetry telemetry;
    private Telemetry.Item teleAlliance;
    private Telemetry.Item teleStartPosition;
    private Telemetry.Item teleParkOnSignalZone;
    private Telemetry.Item teleDelayStartSeconds;
    private Telemetry.Item teleReadyToStart;
    private Telemetry.Item teleSavedToFile;

    /*
     * Pass in the gamepad and telemetry from your opMode.
     */
    public void init(Gamepad gamepad, Telemetry telemetry1, Context context) {
        this.gamepad = gamepad;
        this.context = context;
        AutonomousConfigSaveRetrieve autonomousConfigSaveRetrieve = new AutonomousConfigSaveRetrieve(context);
        this.telemetry = telemetry1;
        // See if we saved the options yet. If not, save the defaults.
        autonomousOptions = new AutonomousOptions();
        if (!autonomousConfigSaveRetrieve.optionsAreSaved()) {
            resetOptions();
            this.SaveOptions();
        } else {
            autonomousOptions = getSaveAutoOptions();
        }

        ShowHelp();
    }

    public AutonomousOptions.AllianceColor getAlliance() {
        return autonomousOptions.getAllianceColor();
    }

    public AutonomousOptions.StartPosition getStartPosition() {
        return autonomousOptions.getStartPosition();
    }

    public AutonomousOptions.RetrieveFromSpike getRetrieveFromSpike() {
        return autonomousOptions.getRetrieveFromSpike();
    }

    public int getDelayStartSeconds() {
        return autonomousOptions.getDelayStartSeconds();
    }

    public boolean getReadyToStart() {
        return readyToStart;
    }

    private void ShowHelp() {
        teleAlliance = telemetry.addData("X = Blue, B = Red", autonomousOptions.getAllianceColor());
        teleStartPosition = telemetry.addData("D-pad left/right, select start position", autonomousOptions.getStartPosition());
        teleParkOnSignalZone = telemetry.addData("D-pad down to cycle retrieve from spike", autonomousOptions.getRetrieveFromSpike());
        teleDelayStartSeconds = telemetry.addData("Left & Right buttons, Delay Start", autonomousOptions.getDelayStartSeconds());
        teleReadyToStart = telemetry.addData("Ready to start: ", getReadyToStart());
        teleSavedToFile = telemetry.addData("Saved to file (Start button to save):", savedToFile);
        telemetry.addLine("Back button resets all options.");
        telemetry.update();
    }

    // Call this in the init_loop from your opMode. It will returns true if you press the
    // game pad Start.
    public void init_loop() {
        //Set default options (ignore what was saved to the file.)
        if (gamepad.backWasReleased()) {
            resetOptions();
        }
        //Alliance Color
        if (gamepad.xWasReleased()) {
            autonomousOptions.setAllianceColor(AutonomousOptions.AllianceColor.Blue);
            telemetry.speak("blue");
        }

        if (gamepad.bWasReleased()) {
            autonomousOptions.setAllianceColor(AutonomousOptions.AllianceColor.Red);
            telemetry.speak("red");
        }
        teleAlliance.setValue(autonomousOptions.getAllianceColor());

        //Start Position

        if (gamepad.dpadRightWasReleased()) {
            autonomousOptions.setStartPosition(AutonomousOptions.StartPosition.GoalWall);
            telemetry.speak("start right");
        }
        if (gamepad.dpadLeftWasReleased()) {
            autonomousOptions.setStartPosition(AutonomousOptions.StartPosition.GoalGate);
            telemetry.speak("start left");
        }
        teleStartPosition.setValue(autonomousOptions.getStartPosition());

        //Retrieve from spike.
        if (gamepad.dpadDownWasReleased()) {
            AutonomousOptions.RetrieveFromSpike retrieveFromSpike = autonomousOptions.getRetrieveFromSpike().getNext();
            switch (retrieveFromSpike) {
                case Yes:
                    telemetry.speak("retrieve from spike, yes");
                    break;
                case No:
                    telemetry.speak("retrieve from spike, no");
                    break;
            }
            autonomousOptions.setRetrieveFromSpike(retrieveFromSpike);
            teleParkOnSignalZone.setValue(retrieveFromSpike);
        }

        // Keep range within 0-15 seconds. Wrap at either end.
        if (gamepad.leftBumperWasReleased()) {
            autonomousOptions.setDelayStartSeconds(autonomousOptions.getDelayStartSeconds() - 1);
            autonomousOptions.setDelayStartSeconds((autonomousOptions.getDelayStartSeconds() < 0) ? 15 : autonomousOptions.getDelayStartSeconds());
            telemetry.speak("delay start " + autonomousOptions.getDelayStartSeconds() + " seconds");
        }
        if (gamepad.rightBumperWasReleased()) {
            autonomousOptions.setDelayStartSeconds(autonomousOptions.getDelayStartSeconds() + 1);
            autonomousOptions.setDelayStartSeconds((autonomousOptions.getDelayStartSeconds() > 15) ? 0 : autonomousOptions.getDelayStartSeconds());
            telemetry.speak("delay start " + autonomousOptions.getDelayStartSeconds() + " seconds");
        }
        teleDelayStartSeconds.setValue(autonomousOptions.getDelayStartSeconds());

        //Have the required options been set?
        readyToStart = !(autonomousOptions.getAllianceColor() == AutonomousOptions.AllianceColor.None || autonomousOptions.getStartPosition() == AutonomousOptions.StartPosition.None);
        teleReadyToStart.setValue(readyToStart);

        //Save the options to a file if ready to start and start button is pressed.
        if (gamepad.startWasReleased() && getReadyToStart()) {
            SaveOptions();
            savedToFile = true;
            teleSavedToFile.setValue(true);
        }
        telemetry.update();
    }

    // Default selections if driver does not select anything.
    private void resetOptions() {
        autonomousOptions.setAllianceColor(AutonomousOptions.AllianceColor.None);
        autonomousOptions.setStartPosition(AutonomousOptions.StartPosition.None);
        autonomousOptions.setRetrieveFromSpike(AutonomousOptions.RetrieveFromSpike.No);
        autonomousOptions.setDelayStartSeconds(0);
        readyToStart = false;
        savedToFile = false;
    }

    private void SaveOptions() {
        AutonomousConfigSaveRetrieve autonomousConfigSaveRetrieve = new AutonomousConfigSaveRetrieve(context);
        autonomousConfigSaveRetrieve.storeObject(autonomousOptions);
    }

    public AutonomousOptions getSaveAutoOptions() {
        AutonomousConfigSaveRetrieve autonomousConfigSaveRetrieve = new AutonomousConfigSaveRetrieve(context);
        AutonomousOptions temp = autonomousConfigSaveRetrieve.getObject();
        telemetry.addData("Start: ", temp.getStartPosition());
        telemetry.update();
        return temp;
    }
}
