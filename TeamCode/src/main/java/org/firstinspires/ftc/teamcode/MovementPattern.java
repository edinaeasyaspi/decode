package org.firstinspires.ftc.teamcode;

import java.io.Serializable;
import java.util.List;

public class MovementPattern implements Serializable {
    private List<Double> frontLeftSpeeds;
    private List<Double> backLeftSpeeds;
    private List<Double> frontRightSpeeds;
    private List<Double> backRightSpeeds;
    public List<Long> cycleTime;

    public void init(List<Double>fls,List<Double>bls,List<Double>frs,List<Double>brs,List<Long> cycle) {
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
