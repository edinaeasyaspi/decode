package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtifactCellManager {
    public enum CellState {
        IDLE,
        MOVING_TO_UP,
        UP,
        MOVING_TO_DOWN,
        DOWN
    }

    //For holding color data
    public enum CellColor {
        NONE,
        GREEN,
        PURPLE
    }

    public CellState leftCellState = CellState.IDLE;
    public CellState centerCellState = CellState.IDLE;
    public CellState rightCellState = CellState.IDLE;
    // Servos for cells
    private final ServoEx leftCellServo;
    private final ServoEx centerCellServo;
    private final ServoEx rightCellServo;

    //Color sensor
    private RevColorV3Manager revColorV3Manager;
    public static CellColor rightCellColor = CellColor.NONE;
    public static CellColor centerCellColor = CellColor.NONE;
    public static CellColor leftCellColor = CellColor.NONE;
    public static RevColorSensorV3 leftColorSensor;
    public static RevColorSensorV3 centerColorSensor;
    public static RevColorSensorV3 rightColorSensor;

    // Defaults to NONE, assuming the setCurrentMotif will be called from the OpModes.
    public static Motif currentMotif = Motif.NONE;

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
    public void setCurrentMotif(Motif motif1) {
        currentMotif = motif1;
    }

    public enum Cell {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }

    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellPositions;
    private final double[] cellDownPositions;
    private final double UP_WAIT_TIME = 0.4;
    private final double DOWN_WAIT_TIME = 0.1;
    public static final ElapsedTime timer = new ElapsedTime();

    public enum Motif {
        GPP,
        PGP,
        PPG,
        NONE
    }

    public void execute() {
        checkColors();
        leftCellState = processCell(Cell.LEFT, leftCellState, leftCellServo);
        centerCellState = processCell(Cell.CENTER, centerCellState, centerCellServo);
        rightCellState = processCell(Cell.RIGHT, rightCellState, rightCellServo);
    }

    // Opens the specified cell with designated wait times
    public void openCell(Cell cell) {
        switch (cell) {
            case LEFT:
                leftCellState = CellState.MOVING_TO_UP;
                break;
            case CENTER:
                centerCellState = CellState.MOVING_TO_UP;
                break;
            case RIGHT:
                rightCellState = CellState.MOVING_TO_UP;
                break;
        }
    }

    public CellColor checkColor(RevColorSensorV3 colorSensor) {
        if (ColorSensor.isGreen(colorSensor)) return CellColor.GREEN;
        if (ColorSensor.isPurple(colorSensor)) return CellColor.PURPLE;
        if (Math.random() > 0.5){
            return  CellColor.GREEN;
        } else {
            return CellColor.PURPLE;
        }
    }

    // Find out what colors are in each cell.
    private void checkColors() {
        leftCellColor = checkColor(leftColorSensor);
        centerCellColor = checkColor(centerColorSensor);
        rightCellColor = checkColor(rightColorSensor);
    }

    public static String colorToString(CellColor cellColor) {
        if (cellColor == CellColor.GREEN) {
            return "G";
        } else if (cellColor == CellColor.PURPLE) {
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
        end += "-" + ColorSensor.hue(leftColorSensor);
        end += "-" + ColorSensor.hue(centerColorSensor);
        end += "-" + ColorSensor.hue(rightColorSensor);
        return end;
    }

    /**
     * Determines the launch order of cells based on their colors and the current Motif.
     * <p>
     * The method initializes a list of cells (`launchOrder`) with default values (`Cell.None`)
     * and a list of cell colors (`cellColors`) representing the colors of the left, center,
     * and right cells. Based on the current Motif, it assigns the cells to specific positions
     * in the `launchOrder` list according to the color and Motif logic.
     *
     * @return A list of `Cell` objects representing the launch order of the cells.
     */
    public static List<Cell> noColorLaunch() {
        List<Cell> launchOrder = new ArrayList<>();
        launchOrder.add(Cell.LEFT);
        launchOrder.add(Cell.CENTER);
        launchOrder.add(Cell.RIGHT);
        return launchOrder;
    }
    public static List<Cell> launchOrder() {
        // Initialize launch order and cell colors
        List<Cell> launchOrder = new ArrayList<>();
        List<Cell> plainOrder = List.of(Cell.LEFT, Cell.CENTER, Cell.RIGHT);
        List<CellColor> cellColors = List.of(leftCellColor, centerCellColor, rightCellColor);

        if (Collections.frequency(cellColors, CellColor.PURPLE) == 2 && Collections.frequency(cellColors, CellColor.GREEN) == 1) {
            switch (currentMotif) {
                case GPP:
                    launchOrder.add(plainOrder.get(cellColors.indexOf(CellColor.GREEN)));
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.LEFT)) launchOrder.add(Cell.LEFT);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.CENTER)) launchOrder.add(Cell.CENTER);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.RIGHT)) launchOrder.add(Cell.RIGHT);
                    break;

                case PGP:
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.LEFT)) launchOrder.add(Cell.LEFT);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.CENTER)) launchOrder.add(Cell.CENTER);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.RIGHT)) launchOrder.add(Cell.RIGHT);
                    launchOrder.add(1,plainOrder.get(cellColors.indexOf(CellColor.GREEN)));
                    break;

                case PPG:
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.LEFT)) launchOrder.add(Cell.LEFT);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.CENTER)) launchOrder.add(Cell.CENTER);
                    if (!(plainOrder.get(cellColors.indexOf(CellColor.GREEN)) == Cell.RIGHT)) launchOrder.add(Cell.RIGHT);
                    launchOrder.add(plainOrder.get(cellColors.indexOf(CellColor.GREEN)));
                    break;
                case NONE:
                    launchOrder.add(Cell.NONE);
            }
        }

        return launchOrder;
    }

    // Processes the state of a cell and updates its servo position accordingly
    private CellState processCell(Cell cell, CellState state, ServoEx servo) {
        double servoUpPosition = cellPositions[cell.ordinal()];
        double servoDownPosition = cellDownPositions[cell.ordinal()];

        switch (state) {
            case IDLE:
                break;
            case MOVING_TO_UP:
                servo.set(servoUpPosition);
                timer.reset();
                state = CellState.UP;
                break;
            case UP:
                if (timer.seconds() > UP_WAIT_TIME) {
                    timer.reset();
                    state = CellState.MOVING_TO_DOWN;
                    break;
                }
                break;
            case MOVING_TO_DOWN:
                servo.set(servoDownPosition);
                timer.reset();
                state = CellState.DOWN;
                break;
            case DOWN:
                if (timer.seconds() > DOWN_WAIT_TIME) {
                    servo.set(servoDownPosition);
                    state = CellState.IDLE;
                    break;
                }
            default:
        }

        return state;
    }
}