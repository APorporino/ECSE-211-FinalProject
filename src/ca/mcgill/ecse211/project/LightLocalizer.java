package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.robotics.SampleProvider;

public class LightLocalizer {

  private static final long COLOUR_PERIOD = 100;

  public  float colourIDLeft;
  public  float colourIDRight;
  static SampleProvider colourValueLeft = LEFT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueRight = RIGHT_COL_SENSOR.getMode("Red"); 
  static float[] colourDataLeft = new float[colourValueLeft.sampleSize()];
  static float[] colourDataRight = new float[colourValueRight.sampleSize()];


  /**
   * This method will localize the robot using light sensor, start the thread.
   */
  public void readLightData() {
    (new Thread() {
      public void run() {

        long updateStart;
        long updateDuration;
        updateStart = System.currentTimeMillis();
        while (true) {
          LEFT_COL_SENSOR.getMode("Red").fetchSample(colourDataLeft, 0);
          colourIDLeft = colourDataLeft[0] * 100;

          RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
          colourIDRight = colourDataRight[0] * 100;

          //Display.showText("Left: " + colourIDLeft, "Right: " + colourIDRight);
          // this ensures that the light sensor runs once every 3 ms.
          updateDuration = System.currentTimeMillis() - updateStart;
          if (updateDuration < COLOUR_PERIOD) {
            sleepFor(COLOUR_PERIOD - updateDuration);
          }

        }
      }
    }).start();
  }

  /**
   * This method move the robot to find the line.
   * The robot moves forward until it detect a line, then it turns to another direction and repeat.
   */
  //      public static void movement() {
  //        Driver.setSpeed(FORWARD_SPEED);
  //        (new Thread() {
  //          public void run() {
  //            while (true) {
  //              while ((getcolorID() < colorID_constant)) {
  //                leftMotor.forward();
  //                rightMotor.forward();
  //              }
  //              Driver.turnBy(90.0);
  //    
  //              while ((getcolorID() < colorID_constant)) {
  //                leftMotor.forward();
  //                rightMotor.forward();
  //              }
  //              // corrections for light sensor to make sure it orientated itself perfectly
  //              leftMotor.rotate(Driver.convertDistance(3), true);
  //              rightMotor.rotate(Driver.convertDistance(3), false);
  //              Driver.turnBy(-88);
  //              leftMotor.rotate(Driver.convertDistance(8), true);
  //              rightMotor.rotate(Driver.convertDistance(8), false);
  //              break;
  //            }
  //          }
  //        }).start();
  //      }
}