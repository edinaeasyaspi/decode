package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

import java.util.ArrayList;
import java.util.List;

public class ArtifactCellManager {
    public enum Cell {
        LEFT,
        CENTER,
        RIGHT,
        NONE
    }

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

    public enum Motif {
        GPP,
        PGP,
        PPG,
        NONE
    }

    // These variables hold the current state of each cell, which is used in the state machine in
    // processCell() to determine how to move the servos in the execute() method.
    private CellState leftCellState = CellState.IDLE;
    private CellState centerCellState = CellState.IDLE;
    private CellState rightCellState = CellState.IDLE;
    // Servos for cells
    private final ServoEx leftCellServo;
    private final ServoEx centerCellServo;
    private final ServoEx rightCellServo;
    //Color sensor
    private RevColorV3Manager revColorV3Manager;
    private CellColor rightCellColor = CellColor.NONE;
    private CellColor centerCellColor = CellColor.NONE;
    private CellColor leftCellColor = CellColor.NONE;
    private final RevColorSensorV3 leftColorSensor;
    private final RevColorSensorV3 centerColorSensor;
    private final RevColorSensorV3 rightColorSensor;

    // Defaults to NONE, assuming the setCurrentMotif will be called from the OpModes.
    private Motif currentMotif = Motif.NONE;

    public ArtifactCellManager(double[] cellOpenPositions, double[] cellClosedPositions,
                               RevColorSensorV3 csOne, RevColorSensorV3 csTwo, RevColorSensorV3 csThree,
                               ServoEx leftServo, ServoEx centerServo, ServoEx rightServo) {
        this.cellOpenPositions = cellOpenPositions;
        this.cellClosedPositions = cellClosedPositions;
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
    public void setCurrentMotif(Motif motif) {
        currentMotif = motif;
    }

    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellOpenPositions;
    private final double[] cellClosedPositions;
    // How long should we wait for the servo to open the cell.
    private final double UP_WAIT_TIME = 0.4;
    // How long should we wait for the servo to close the cell.
    private final double DOWN_WAIT_TIME = 0.1;

    private ElapsedTime timer = new ElapsedTime();

    public void execute() {
        // Get the colors of the cells every loop, so that we can determine the launch order.
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

    private CellColor checkColor(RevColorSensorV3 colorSensor) {
        return revColorV3Manager.getCellColor(colorSensor);
    }

    // Find out what colors are in each cell.
    private void checkColors() {
        leftCellColor = checkColor(leftColorSensor);
        centerCellColor = checkColor(centerColorSensor);
        rightCellColor = checkColor(rightColorSensor);
    }

    public String colors() {
        String end;
        end = leftCellColor.toString();
        end += centerCellColor.toString();
        end += rightCellColor.toString();
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

    /* Determines the launch order of cells based on their colors and the current Motif.
     * The method initializes a list of cells (launchOrder) with default values (Cell.None)
     * and a list of cell colors (cellColors) representing the colors of the left, center,
     * and right cells. Based on the current Motif, it assigns the cells to specific positions
     * in the launchOrder list according to the color and Motif logic.
     */
    public List<Cell> launchOrder() {
        // Initialize launch order and cell colors
        List<Cell> launchOrder = new ArrayList<>();
        List<Cell> cellList = List.of(Cell.LEFT, Cell.CENTER, Cell.RIGHT);
        List<CellColor> cellColors = List.of(leftCellColor, centerCellColor, rightCellColor);
        int colorIndex;

        switch (currentMotif) {
            case GPP:
                colorIndex = cellColors.indexOf(CellColor.GREEN);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }
                break;

            case PGP:
                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.GREEN);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }
                break;

            case PPG:
                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.PURPLE);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }

                colorIndex = cellColors.indexOf(CellColor.GREEN);
                if (colorIndex != -1) {
                    launchOrder.add(cellList.get(colorIndex));
                } else {
                    launchOrder.add(Cell.NONE);
                }
                break;
            case NONE:
                launchOrder.add(Cell.NONE);

        }
        // If there are any remaining cells that haven't been added to the launch order,
        // add them.
        for (Cell cell : cellList) {
            if (!launchOrder.contains(cell)) {
                launchOrder.add(cell);
            }
        }

        return launchOrder;
    }

    //TODO figure out how to sequence the launches in the execute loop.
    // We want to be able to open the cells in the correct order, but we also need to wait for the
    // servos to move before we can open the next cell. We also need to make sure that we don't
    // try to open a cell that is already open.
    private void processLaunch() {
        List<Cell> launchOrder = launchOrder();
        for (Cell cell : launchOrder) {
            switch (cell) {
                case LEFT:
                    processCell(cell, leftCellState, leftCellServo);
                    break;
                case CENTER:
                    processCell(cell, centerCellState, centerCellServo);
                    break;
                case RIGHT:
                    processCell(cell, rightCellState, rightCellServo);
                    break;
            }
        }
    }

    /* Processes the state of a cell and updates its servo position accordingly.
        This actually launches artifacts.
        Returns the new state of the cell after processing.
     */
    private CellState processCell(Cell cell, CellState state, ServoEx servo) {
        // Get the servo positions for the current cell.
        double servoUpPosition = cellOpenPositions[cell.ordinal()];
        double servoDownPosition = cellClosedPositions[cell.ordinal()];

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