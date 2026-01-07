package org.firstinspires.ftc.teamcode;

import java.io.Serializable;

/**
This class stores autonomous options for use by autonomous or teleop op modes.
It is currently set for the Decode season but can be modified for upcoming seasons.
It is a Java Serializable class to it can be saved to a file.
 */
public class AutonomousOptions implements Serializable {
    private static final long serialVersionUID = 7829136421241571165L;

    private int delayStartSeconds;
    private AllianceColor allianceColor;
    private StartPosition startPosition;
    private RetrieveFromSpike retrieveFromSpike;

    public AllianceColor getAllianceColor() {
        return allianceColor;
    }

    public void setAllianceColor(AllianceColor allianceColor) {
        this.allianceColor = allianceColor;
    }

    public StartPosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(StartPosition startPosition) {
        this.startPosition = startPosition;
    }

    public RetrieveFromSpike getRetrieveFromSpike() {
        return retrieveFromSpike;
    }

    public void setRetrieveFromSpike(RetrieveFromSpike retrieveFromSpike) {
        this.retrieveFromSpike = retrieveFromSpike;
    }

    public int getDelayStartSeconds() {
        return delayStartSeconds;
    }

    public void setDelayStartSeconds(int delayStartSeconds) {
        this.delayStartSeconds = delayStartSeconds;
    }

    public String toString() {
        return "AllianceColor: " + getAllianceColor().toString() + "\nStartLocation: " + getStartPosition().toString();
    }

    /*
     * Alliance color. Default to None so driver must select it.
     */
    public enum AllianceColor {
        None,
        Red,
        Blue
    }

    /*
     * Where do we start the robot
     * GoalGate is on the gate side of the goal.
     * GoalWall is on the wallside of the goal.
     * AudienceCenter is in the center of the audience wall.
     * AudienceTeam is on the team side of the audience wall.
     */
    public enum StartPosition {
        None,
        GoalGate,
        GoalWall,
        AudienceCenter,
        AudienceTeam,
    }

    /*
     * Yes means look at obelisk and retrieve artifacts from a spike.
     * Default is No.
     */
    public enum RetrieveFromSpike {
        No,
        Yes;

        public RetrieveFromSpike getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

}
