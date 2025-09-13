package org.firstinspires.ftc.teamcode.inputsys;

import com.qualcomm.robotcore.hardware.Gamepad;

public class Input {
  private byte[] states;
  private Gamepad pad;

  public Input(Gamepad gamepad){
    this.pad = gamepad;
    states = new byte[KeyCode.END.ordinal()];
  }


  public void Update(){
    boolean[] currState = LoadCurrState(); 
  }

  public boolean GetKeyDown(KeyCode code){
    return states[code.ordinal()] == 1;
  }
  
  private boolean[] LoadCurrState(){
    boolean[] currState = new boolean[KeyCode.END.ordinal()];
    currState[KeyCode.a.ordinal()] = pad.a;
    currState[KeyCode.b.ordinal()] = pad.b;
    currState[KeyCode.x.ordinal()] = pad.x;
    currState[KeyCode.y.ordinal()] = pad.y;
    currState[KeyCode.back.ordinal()] = pad.back;
    currState[KeyCode.circle.ordinal()] = pad.circle;
    currState[KeyCode.cross.ordinal()] = pad.cross;
    currState[KeyCode.down.ordinal()] = pad.dpad_down;
    currState[KeyCode.left.ordinal()] = pad.dpad_left;
    currState[KeyCode.right.ordinal()] = pad.dpad_right;
    currState[KeyCode.up.ordinal()] = pad.dpad_up;
    currState[KeyCode.lb.ordinal()] = pad.left_bumper;
    currState[KeyCode.rb.ordinal()] = pad.right_bumper;
    currState[KeyCode.lt.ordinal()] = pad.left_trigger > 0.5;
    currState[KeyCode.rt.ordinal()] = pad.right_trigger > 0.5;
    currState[KeyCode.guide.ordinal()] = pad.guide;
    currState[KeyCode.start.ordinal()] = pad.start;

    return currState;
  } 
}
