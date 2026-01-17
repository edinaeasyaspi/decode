package org.firstinspires.ftc.teamcode;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
This class reads or writes AutonomousOptions objects to a file.
 It is intended for use with the AutonomousConfiguration class.
 */
public class MovementPatternRetrieve {
    private String fileName = "movementPaths.txt";
    private Context context;

    public MovementPatternRetrieve(Context context) {
        this.context = context;
    }

    // Check to see if options file exists.
    public boolean optionsAreSaved() {
        boolean result = true;
        try {
            context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            result = false;
        }
        return result;
    }

    public void storeObject(MovementPattern autonomousSpeeds, String fileNameTarget) {
        fileName = fileNameTarget;
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            objectOutputStream.writeObject(autonomousSpeeds);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MovementPattern getObject(String fileNameTarget) {
        fileName = fileNameTarget;
        ObjectInputStream objectInputStream;
        MovementPattern autonomousSpeeds = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
//            File filesDir = context.getFilesDir();
//            String string = filesDir.toString();
            objectInputStream = new ObjectInputStream(fileInputStream);
            autonomousSpeeds = (MovementPattern) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return autonomousSpeeds;
    }
}
