package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.hardware.servos.ServoEx;

public class ArtifactCellManager {
    public enum CELL_STATE {
        Idle,
        Up,
        Down
    }

    public CELL_STATE leftCellState = CELL_STATE.Idle;
    public CELL_STATE centerCellState = CELL_STATE.Idle;
    public CELL_STATE rightCellState = CELL_STATE.Idle;

    public ArtifactCellManager(double[] cellPositions, double[] cellDownPositions) {
        this.cellPositions = cellPositions;
        this.cellDownPositions = cellDownPositions;
    }

    public enum CELL {
        Left,
        Center,
        Right
    }

    // Servo positions for each cell, [0] = left, [1] = center, [2] = right
    private final double[] cellPositions;
    private final double[] cellDownPositions;
    private final double UP_WAIT_TIME = 0.5;
    private final double DOWN_WAIT_TIME = 0.5;
    private final ElapsedTime timer = new ElapsedTime();

    public void execute(CELL cell, ServoEx servo) {
        switch (cell) {
            case Left:
                processCell(cell, leftCellState, servo);
                break;
            case Center:
                processCell(cell, centerCellState, servo);
                break;
            case Right:
                processCell(cell, rightCellState, servo);
                break;
        }
    }

    private void processCell(CELL cell, CELL_STATE state, ServoEx servo) {
        double servoUpPosition = cellPositions[cell.ordinal()];
        double servoDownPosition = cellDownPositions[cell.ordinal()];

        switch (state) {
            case Idle:
                servo.set(servoUpPosition);
                timer.reset();
                state = CELL_STATE.Up;
                break;
            case Up:
                if (timer.seconds() > UP_WAIT_TIME) {
                    timer.reset();
                    state = CELL_STATE.Down;
                }
                break;
            case Down:
                if (timer.seconds() > DOWN_WAIT_TIME) {
                    servo.set(servoDownPosition);
                    state = CELL_STATE.Idle;
                    break;
                }
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
}