package org.firstinspires.ftc.teamcode;

import java.util.List;

public class MovementPattern {
    private List<Double> frontLeftSpeeds;
    private List<Double> backLeftSpeeds;
    private List<Double> frontRightSpeeds;
    private List<Double> backRightSpeeds;
    public long cycleTime;

    public void init(List<Double>fls,List<Double>bls,List<Double>frs,List<Double>brs,long cycle) {
        frontLeftSpeeds = fls;
        backLeftSpeeds = bls;
        frontRightSpeeds = frs;
        backRightSpeeds = brs;
        cycleTime = cycle;
    }
    public List<Double> getFrontLeftSpeeds() {
        return frontLeftSpeeds;
    }
    public List<Double> getBackLeftSpeeds() {
        return  backLeftSpeeds;
    }
    public List<Double> getFrontRightSpeeds() {
        return  frontRightSpeeds;
    }
    public List<Double> getBackRightSpeeds() {
        return backRightSpeeds;
    }
}
