package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

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
    public static CELL_STATE rightCellState = CELL_STATE.Idle;
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

    public ArtifactCellManager(double[] cellPositions, double[] cellDownPositions,
                               ColorSensor csOne, ColorSensor csTwo, ColorSensor csThree,
                               ServoEx leftServo, ServoEx centerServo, ServoEx rightServo) {
        this.cellPositions = cellPositions;
        this.cellDownPositions = cellDownPositions;
        //Put in sensors
        this.leftColorSensor = csOne;
        this.centerColorSensor = csTwo;
        this.rightColorSensor = csThree;
        //def servos
        this.leftCellServo = leftServo;
        this.centerCellServo = centerServo;
        this.rightCellServo = rightServo;
    }

    public enum CELL {
        Left,
        Center,
        Right
    }

    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellPositions;
    private final double[] cellDownPositions;
    private final double UP_WAIT_TIME = 0.1;
    private final double DOWN_WAIT_TIME = 0.1;
    public static final ElapsedTime timer = new ElapsedTime();


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

    private void directSwitch(CELL cell, CELL_STATE state) {
        switch (cell) {
            case Left:
                leftCellState = state;
            case Center:
                centerCellState = state;
            case Right:
                rightCellState = state;
        }
    }
    private void passiveCell(CELL cell, CELL_STATE state, ServoEx servo) {
        double servoUpPosition = cellPositions[cell.ordinal()];
        double servoDownPosition = cellDownPositions[cell.ordinal()];

        switch (state) {
            case MovingToUp:
                servo.set(servoUpPosition);
                timer.reset();
                directSwitch(cell, CELL_STATE.Up);
                break;
            case Up:
                if (timer.seconds() > 1) {
                    directSwitch(cell, CELL_STATE.MovingToDown);
                    servo.set(servoDownPosition);
                    timer.reset();
                    break;
                }
            case MovingToDown:
                if (timer.seconds() > 5) {
                    directSwitch(cell, CELL_STATE.Down);
                }
            case Down:
                directSwitch(cell, CELL_STATE.Idle);
                break;
            default:
        }
        switch (cell) {
            case Left:
                leftCellState = state;
                break;
            case Center:
                centerCellState = state;
                break;
            case Right:
                rightCellState = state;
                break;
        }
    }

    //TODO: Is this needed?
    public void passiveProccessAll(ServoEx servoOne, ServoEx servoTwo, ServoEx servoThree) {
        passiveCell(CELL.Left, leftCellState, servoOne);
        passiveCell(CELL.Center, rightCellState, servoTwo);
        passiveCell(CELL.Right, rightCellState, servoThree);
    }
}