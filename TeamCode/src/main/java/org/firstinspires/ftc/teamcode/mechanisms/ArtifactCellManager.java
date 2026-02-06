package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
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
    private final ServoEx leftCellServo;
    private final ServoEx centerCellServo;
    private final ServoEx rightCellServo;

    //Color sensor
    private RevColorV3Manager revColorV3Manager;
    public static CELL_COLOR rightCellColor = CELL_COLOR.None;
    public static CELL_COLOR centerCellColor = CELL_COLOR.None;
    public static CELL_COLOR leftCellColor = CELL_COLOR.None;
    public RevColorSensorV3 leftColorSensor;
    public RevColorSensorV3 centerColorSensor;
    public RevColorSensorV3 rightColorSensor;

    // Defaults to NONE, assuming the setCurrentMotif will be called from the OpModes.
    public static motif currentMotif = motif.NONE;

    public ArtifactCellManager(double[] cellPositions, double[] cellDownPositions,
                               RevColorSensorV3 csOne, RevColorSensorV3 csTwo, RevColorSensorV3 csThree,
                               ServoEx leftServo, ServoEx centerServo, ServoEx rightServo) {
        this.cellPositions = cellPositions;
        this.cellDownPositions = cellDownPositions;
        //Put in sensors
        this.leftColorSensor = csOne;
        this.centerColorSensor = csTwo;
        this.rightColorSensor = csThree;
        //TODO Choose RGB (default) or HSV for color detection.
        revColorV3Manager = new RevColorV3Manager(true);
        //Define servos
        this.leftCellServo = leftServo;
        this.centerCellServo = centerServo;
        this.rightCellServo = rightServo;
    }

    //  This can be set from auto if it finds the AprilTag or manually from teleop.
    public void setCurrentMotif(motif motif1) {
        currentMotif = motif1;
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
        PPG,
        NONE
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

    public CELL_COLOR checkColor(RevColorSensorV3 colorSensor) {
        if (ColorSensor.isGreen(colorSensor)) return CELL_COLOR.Green;
        if (ColorSensor.isPurple(colorSensor)) return CELL_COLOR.Purple;
        return CELL_COLOR.None;
    }

    // Find out what colors are in each cell.
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

    /**
     * Determines the launch order of cells based on their colors and the current motif.
     * <p>
     * The method initializes a list of cells (`launchOrder`) with default values (`CELL.None`)
     * and a list of cell colors (`cellColors`) representing the colors of the left, center,
     * and right cells. Based on the current motif, it assigns the cells to specific positions
     * in the `launchOrder` list according to the color and motif logic.
     *
     * @return A list of `CELL` objects representing the launch order of the cells.
     */
    public static List<CELL> noColorLaunch() {
        List<CELL> launchOrder = new ArrayList<>();
        launchOrder.add(CELL.Left);
        launchOrder.add(CELL.Center);
        launchOrder.add(CELL.Right);
        return launchOrder;
    }
    public static List<CELL> launchOrder() {
        // Initialize launch order and cell colors
        List<CELL> launchOrder = new ArrayList<>(Collections.nCopies(3, CELL.None));
        List<CELL_COLOR> cellColors = List.of(leftCellColor, centerCellColor, rightCellColor);

        int index = 0;
        switch (currentMotif) {
            case GPP:
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Green) {
                        launchOrder.set(0, CELL.values()[i]);
                        break;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Purple) {
                        launchOrder.set(++index, CELL.values()[i]);
                    }
                }
                break;

            case PGP:
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Purple) {
                        launchOrder.set(0, CELL.values()[i]);
                        break;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Green) {
                        launchOrder.set(++index, CELL.values()[i]);
                        break;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Purple) {
                        launchOrder.set(2, CELL.values()[i]);
                    }
                }
                break;

            case PPG:
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Purple) {
                        launchOrder.set(0, CELL.values()[i]);
                        break;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Purple) {
                        launchOrder.set(++index, CELL.values()[i]);
                        break;
                    }
                }
                for (int i = 0; i < 3; i++) {
                    if (cellColors.get(i) == CELL_COLOR.Green) {
                        launchOrder.set(++index, CELL.values()[i]);
                    }
                }
                break;
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