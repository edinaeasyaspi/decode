package org.firstinspires.ftc.teamcode.mechanisms;

import static com.qualcomm.robotcore.eventloop.opmode.OpMode.blackboard;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.drivebase.MecanumDrive;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.AutonomousConfiguration;
import org.firstinspires.ftc.teamcode.AutonomousOptions;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AprilTagManager is a class for managing AprilTag detection and processing.
 * Future implementations may include methods for initializing the camera, detecting tags,
 * and providing tag information to other parts of the robot code.
 */
public class AprilTagManager {
    private WebcamName webcamName;
    private MecanumDrive mecanumDrive;
    private OpMode opMode;
    private AutonomousConfiguration autonomousConfiguration = new AutonomousConfiguration();
    private AutonomousOptions.AllianceColor allianceColor;
    private int exposure;
    private int gain;
    private int BLUE_GOAL_ID = 20;
    private int RED_GOAL_ID = 24;
    private int OBELISK_GPP_ID = 21;
    private int OBELISK_PGP_ID = 22;
    private int OBELISK_PPG_ID = 23;
    private ArtifactCellManager.Motif currentMotif;
    private double currentRange = 0.0;
    public static double BEARING_TOLERANCE = 2.5f;

    public AprilTagManager(OpMode opMode, WebcamName webcamName, MecanumDrive mecanumDrive) {
        this.webcamName = webcamName;
        this.mecanumDrive = mecanumDrive;
        autonomousConfiguration.init(opMode.gamepad1, opMode.telemetry, opMode.hardwareMap.appContext);
        this.opMode = opMode;
        allianceColor = autonomousConfiguration.getAlliance();
        exposure = allianceColor == AutonomousOptions.AllianceColor.Red ?
                autonomousConfiguration.getExposureRed() : autonomousConfiguration.getExposureBlue();
        gain = allianceColor == AutonomousOptions.AllianceColor.Red ?
                autonomousConfiguration.getGainRed() : autonomousConfiguration.getGainBlue();

        initAprilTag();
        setManualExposure(exposure, gain);
        // Get the Motif from the dashboard, default to NONE
        Object dashboardCurrentMotif = blackboard.getOrDefault(CURRENT_MOTIF_KEY, ArtifactCellManager.Motif.NONE);
        currentMotif = (ArtifactCellManager.Motif) dashboardCurrentMotif;
    }

    /**
     * Variables to store the position and orientation of the camera on the robot. Setting these
     * values requires a definition of the axes of the camera and robot:
     * <p>
     * Camera axes:
     * Origin location: Center of the lens
     * Axes orientation: +x right, +y down, +z forward (from camera's perspective)
     * <p>
     * Robot axes (this is typical, but you can define this however you want):
     * Origin location: Center of the robot at field height
     * Axes orientation: +x right, +y forward, +z upward
     * <p>
     * Position:
     * If all values are zero (no translation), that implies the camera is at the center of the
     * robot. Suppose your camera is positioned 5 inches to the left, 7 inches forward, and 12
     * inches above the ground - you would need to set the position to (-5, 7, 12).
     * <p>
     * Orientation:
     * If all values are zero (no rotation), that implies the camera is pointing straight up. In
     * most cases, you'll need to set the pitch to -90 degrees (rotation about the x-axis), meaning
     * the camera is horizontal. Use a yaw of 0 if the camera is pointing forwards, +90 degrees if
     * it's pointing straight left, -90 degrees for straight right, etc. You can also set the roll
     * to +/-90 degrees if it's vertical, or 180 degrees if it's upside-down.
     */
    private Position cameraPosition = new Position(DistanceUnit.INCH,
            0, 9, 12, 0);

    private YawPitchRollAngles cameraOrientation = new YawPitchRollAngles(AngleUnit.DEGREES,
            0, -90, 0, 0);

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    private ElapsedTime cameraDelayTimer = new ElapsedTime();
    //TODO: Define desired distances for short and long shots.
    // This is only used for auto-alignment if range is implemented.
    final double DESIRED_SHORT_DISTANCE = 24.0;
    final double DESIRED_LONG_DISTANCE = 48.0;
    // Store the current Motif in the opmode dashboard for retrieval by tele OpModes.
    private String CURRENT_MOTIF_KEY = "currentmotif";

    // Execute the AprilTag auto alignment process for launching.
    // Return true if aligned, false if not. This method can be called repeatedly in a loop until alignment is achieved.
    public boolean execute(boolean shootFar) {
        return isAprilTagAligned(shootFar);
    }

    // Find the obelisk Motif AprilTag.
    // Returns Motif.NONE if no tag is found.
    public ArtifactCellManager.Motif findMotif() {
        this.currentMotif = getMotifApriltag();
        // Store the current Motif in the blackboard for retrieval by tele OpModes.
        blackboard.put(CURRENT_MOTIF_KEY, currentMotif);
        return this.currentMotif;
    }

    // For use by teleop when auto does not find the Motif.
    public void setCurrentMotif(ArtifactCellManager.Motif motif) {
        this.currentMotif = motif;
        blackboard.put(CURRENT_MOTIF_KEY, currentMotif);
    }

    /* Getters for current Motif and range, to adjust behavior based on the detected AprilTag information.
     */
    public ArtifactCellManager.Motif getCurrentMotif() {
        return currentMotif;
    }

    public double getCurrentRange() {
        return currentRange;
    }

    /**
     * Initialize the AprilTag processor.
     */
    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        //TODO: Adjust the camera position and orientation values to match your robot
        aprilTag = new AprilTagProcessor.Builder()
                .setCameraPose(cameraPosition, cameraOrientation)
                .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // e.g. Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        // Here we are using a webcam.
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .addProcessor(aprilTag)
                .build();
    }


    /*
     Manually set the camera gain and exposure.
     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
    */
    private void setManualExposure(int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            while ((visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                cameraDelayTimer.reset();
                while (cameraDelayTimer.milliseconds() < 20) {
                }
            }
        }

        // Set camera controls
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            cameraDelayTimer.reset();
            while (cameraDelayTimer.milliseconds() < 50) {
            }
        }

        exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
        cameraDelayTimer.reset();
        while (cameraDelayTimer.milliseconds() < 20) {
        }

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
        cameraDelayTimer.reset();
        while (cameraDelayTimer.milliseconds() < 20) {
        }
    }

    /**
     * Check to see if we are aligned to the desired AprilTag.
     *
     * @return true if aligned.
     */
    private boolean isAprilTagAligned(boolean shootFar) {
        boolean aligned = false;
        //TODO Adjust this to account for launcher offset from robot center if needed.
        double COMPENSATION_ANGLE_DEGREES = 5.0;
        // Assume there are 2 launch distances: short and long
        double desiredRange = shootFar ? DESIRED_LONG_DISTANCE : DESIRED_SHORT_DISTANCE;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if (detection.id == 20 || detection.id == 24) {
                    // Save so launching can be dynamically adjusted to account for
                    // distance to the goal.
                    currentRange = detection.ftcPose.range;
                    // Check if the tag is within alignment tolerances
                    // Use static variable for dashboard tuning
                    double bearing = detection.ftcPose.bearing;

                    // Only correct for bearing.
                    if (Math.abs(bearing + COMPENSATION_ANGLE_DEGREES) <= BEARING_TOLERANCE) {
                        aligned = true;
                        mecanumDrive.driveRobotCentric(0, 0, 0);
                    } else {
                        // The parameters are set to only center. You may want to add range control as well.
                        mecanumDrive.driveRobotCentric(0,
                                0,
                                scale(-bearing, -45, 45, -1., 1));
                    }

                    break; // No need to check further tags
                }
            }
        }

        return aligned;
    }

    private ArtifactCellManager.Motif getMotifApriltag() {
        ArtifactCellManager.Motif motif = ArtifactCellManager.Motif.NONE;
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                // Only correct for bearing for now
                if (detection.id == OBELISK_GPP_ID) {
                    motif = ArtifactCellManager.Motif.GPP;
                } else if (detection.id == OBELISK_PGP_ID) {
                    motif = ArtifactCellManager.Motif.PGP;
                } else if (detection.id == OBELISK_PPG_ID) {
                    motif = ArtifactCellManager.Motif.PPG;
                }
            }
        }
        return motif;
    }

    // Scale a value from one range to another.
    private static double scale(double value, double inMin, double inMax, double outMin,
                                double outMax) {
        double result = (value - inMin) * (outMax - outMin) / (inMax - inMin) + outMin;

        if (result < outMin) {
            return outMin;
        } else if (result > outMax) {
            return outMax;
        }
        return result;
    }
}

