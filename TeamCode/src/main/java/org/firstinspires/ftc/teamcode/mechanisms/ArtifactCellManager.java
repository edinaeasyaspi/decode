package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtifactCellManager {
    public enum CELL_STATE {
        Idle,
        MovingToUp,
        Up,
        MovingToDown,
        Down
    }

    //For holding color data
    public enum CELL_COLOR {
        None,
        Green,
        Purple
    }

    public CELL_STATE leftCellState = CELL_STATE.Idle;
    public CELL_STATE centerCellState = CELL_STATE.Idle;
    public CELL_STATE rightCellState = CELL_STATE.Idle;
    // Servos for cells
    private ServoEx leftCellServo;
    private ServoEx centerCellServo;
    private ServoEx rightCellServo;

    //Color sensor
    public static CELL_COLOR rightCellColor = CELL_COLOR.None;
    public static CELL_COLOR centerCellColor = CELL_COLOR.None;
    public static CELL_COLOR leftCellColor = CELL_COLOR.None;
    public ColorSensor leftColorSensor;
    public ColorSensor centerColorSensor;
    public ColorSensor rightColorSensor;

    public static motif currentMotif;

    public ArtifactCellManager(double[] cellPositions, double[] cellDownPositions,
                               ColorSensor csOne, ColorSensor csTwo, ColorSensor csThree,
                               ServoEx leftServo, ServoEx centerServo, ServoEx rightServo, motif motif1) {
        this.cellPositions = cellPositions;
        this.cellDownPositions = cellDownPositions;
        //Put in sensors
        this.leftColorSensor = csOne;
        this.centerColorSensor = csTwo;
        this.rightColorSensor = csThree;
        //Define servos
        this.leftCellServo = leftServo;
        this.centerCellServo = centerServo;
        this.rightCellServo = rightServo;

        this.currentMotif = motif.GPP;
    }

    public void setCurrentMotif(motif motif1) {
        this.currentMotif = motif1;
    }

    public enum CELL {
        Left,
        Center,
        Right,
        None
    }

    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellPositions;
    private final double[] cellDownPositions;
    private final double UP_WAIT_TIME = 0.4;
    private final double DOWN_WAIT_TIME = 0.1;
    public static final ElapsedTime timer = new ElapsedTime();

    public enum motif {
        GPP,
        PGP,
        PPG
    }

    public void execute() {
        leftCellState = processCell(CELL.Left, leftCellState, leftCellServo);
        centerCellState = processCell(CELL.Center, centerCellState, centerCellServo);
        rightCellState = processCell(CELL.Right, rightCellState, rightCellServo);
    }

    // Opens the specified cell with designated wait times
    public void openCell(CELL cell) {
        switch (cell) {
            case Left:
                leftCellState = CELL_STATE.MovingToUp;
                break;
            case Center:
                centerCellState = CELL_STATE.MovingToUp;
                break;
            case Right:
                rightCellState = CELL_STATE.MovingToUp;
                break;
        }
    }

    public CELL_COLOR checkColor(ColorSensor colorSensor) {
        boolean green = colorSensor.isGreen();
        boolean purple = colorSensor.isPurple();
        if (green) {
            return CELL_COLOR.Green;
        } else if (purple) {
            return CELL_COLOR.Purple;
        } else {
            return CELL_COLOR.None;
        }
    }

    public void checkColors() {
        leftCellColor = checkColor(leftColorSensor);
        centerCellColor = checkColor(centerColorSensor);
        rightCellColor = checkColor(rightColorSensor);
    }

    public static String colorToString(CELL_COLOR cellColor) {
        if (cellColor == CELL_COLOR.Green) {
            return "G";
        } else if (cellColor == CELL_COLOR.Purple) {
            return "P";
        } else {
            return "N";
        }
    }

    public static String colors() {
        String end;
        end = colorToString(leftCellColor);
        end += colorToString(centerCellColor);
        end += colorToString(rightCellColor);
        return end;
    }

    private static CELL_COLOR specificMotifColor(int num) {
        if (currentMotif == motif.GPP) {
            if (num == 1) return CELL_COLOR.Green;
            if (num == 2) return CELL_COLOR.Purple;
            if (num == 3) return CELL_COLOR.Purple;
        } else if (currentMotif == motif.PGP) {
            if (num == 1) return CELL_COLOR.Purple;
            if (num == 2) return CELL_COLOR.Green;
            if (num == 3) return CELL_COLOR.Purple;
        } else if (currentMotif == motif.PPG) {
            if (num == 1) return CELL_COLOR.Purple;
            if (num == 2) return CELL_COLOR.Purple;
            if (num == 3) return CELL_COLOR.Green;
        }
        return CELL_COLOR.Purple;
    }

    public static List<CELL> launchOrder() {

        List<CELL> launchOrder = new ArrayList<>();
        launchOrder.add(CELL.None);
        launchOrder.add(CELL.None);
        launchOrder.add(CELL.None);
        List<CELL_COLOR> balls = new ArrayList<>();
        balls.add(leftCellColor);
        balls.add(centerCellColor);
        balls.add(rightCellColor);
        /// I know its inefficient, but I know how to do this and this is simple
        if (Collections.frequency(balls, CELL_COLOR.Purple) == 2 && Collections.frequency(balls, CELL_COLOR.Green) == 1) {
            if ((balls.indexOf(CELL_COLOR.Green) == 0 && currentMotif == motif.GPP) || (balls.indexOf(CELL_COLOR.Green) == 1 && currentMotif == motif.PGP) || ((balls.indexOf(CELL_COLOR.Green) == 2 && currentMotif == motif.PPG))) {
                launchOrder.set(0, CELL.Left);
                launchOrder.set(1, CELL.Center);
                launchOrder.set(2, CELL.Right);
            } else if ((balls.indexOf(CELL_COLOR.Green) == 1 && currentMotif == motif.GPP) || (balls.indexOf(CELL_COLOR.Green) == 0 && currentMotif == motif.PGP) || (balls.indexOf(CELL_COLOR.Green) == 2 && currentMotif == motif.PPG)) {
                launchOrder.set(0, CELL.Center);
                launchOrder.set(1, CELL.Left);
                launchOrder.set(2, CELL.Right);
            } else if ((balls.indexOf(CELL_COLOR.Green) == 2 && currentMotif == motif.GPP) || (balls.indexOf(CELL_COLOR.Green) == 0 && currentMotif == motif.PGP) || (balls.indexOf(CELL_COLOR.Green) == 1 && currentMotif == motif.PPG)) {
                launchOrder.set(0, CELL.Right);
                launchOrder.set(1, CELL.Left);
                launchOrder.set(2, CELL.Center);
            } else {
                launchOrder.set(0, CELL.Left);
                launchOrder.set(1, CELL.Right);
                launchOrder.set(2, CELL.Center);
            }
        } else {
            if (Collections.frequency(balls, CELL_COLOR.None) == 0) {
                launchOrder.set(0, CELL.Left);
                launchOrder.set(1, CELL.Center);
                launchOrder.set(2, CELL.Right);
            } else if (Collections.frequency(balls, CELL_COLOR.None) == 1) {
                if (balls.get(0) == CELL_COLOR.None) {
                    launchOrder.set(0, CELL.Center);
                    launchOrder.set(1, CELL.Right);
                } else if (balls.get(1) == CELL_COLOR.None) {
                    launchOrder.set(0, CELL.Left);
                    launchOrder.set(1, CELL.Right);
                } else if (balls.get(2) == CELL_COLOR.None) {
                    launchOrder.set(0, CELL.Left);
                    launchOrder.set(1, CELL.Center);
                }
            } else if (Collections.frequency(balls, CELL_COLOR.None) == 2) {
                if (balls.get(0) != CELL_COLOR.None) launchOrder.set(0, CELL.Left);
                if (balls.get(1) != CELL_COLOR.None) launchOrder.set(0, CELL.Center);
                if (balls.get(2) != CELL_COLOR.None) launchOrder.set(0, CELL.Right);
            }
        }
        return launchOrder;
    }

    // Processes the state of a cell and updates its servo position accordingly
    private CELL_STATE processCell(CELL cell, CELL_STATE state, ServoEx servo) {
        double servoUpPosition = cellPositions[cell.ordinal()];
        double servoDownPosition = cellDownPositions[cell.ordinal()];

        switch (state) {
            case Idle:
                break;
            case MovingToUp:
                servo.set(servoUpPosition);
                timer.reset();
                state = CELL_STATE.Up;
                break;
            case Up:
                if (timer.seconds() > UP_WAIT_TIME) {
                    timer.reset();
                    state = CELL_STATE.MovingToDown;
                    break;
                }
                break;
            case MovingToDown:
                servo.set(servoDownPosition);
                timer.reset();
                state = CELL_STATE.Down;
                break;
            case Down:
                if (timer.seconds() > DOWN_WAIT_TIME) {
                    servo.set(servoDownPosition);
                    state = CELL_STATE.Idle;
                    break;
                }
            default:
        }

        return state;
    }
}