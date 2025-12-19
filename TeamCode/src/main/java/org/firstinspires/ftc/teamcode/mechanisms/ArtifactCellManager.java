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

    public static CELL_STATE leftCellState = CELL_STATE.Idle;
    public static CELL_STATE centerCellState = CELL_STATE.Idle;
    public static CELL_STATE rightCellState = CELL_STATE.Idle;
    // Servos for cells
    private static ServoEx leftCellServo;
    private static ServoEx centerCellServo;
    private static ServoEx rightCellServo;

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
        //Define servos
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
    private static double[] cellPositions = new double[0];
    private static double[] cellDownPositions = new double[0];
    private static final double UP_WAIT_TIME = 0.4;
    private static final double DOWN_WAIT_TIME = 0.1;
    public static final ElapsedTime timer = new ElapsedTime();

    public static void execute() {
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

    // Processes the state of a cell and updates its servo position accordingly
    private static CELL_STATE processCell(CELL cell, CELL_STATE state, ServoEx servo) {
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

    private void passiveCell(CELL cell, CELL_STATE state, ServoEx servo) {
        double servoUpPosition = cellPositions[cell.ordinal()];
        double servoDownPosition = cellDownPositions[cell.ordinal()];

        switch (state) {
            case Up:
                timer.reset();
                state = CELL_STATE.Down;
                break;
            case Down:
                servo.set(servoDownPosition);
                state = CELL_STATE.Idle;
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