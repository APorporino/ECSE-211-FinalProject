package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Driver.*;
import static ca.mcgill.ecse211.project.Main.*;

import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;

/**
 * This class is used to control and receive readings from multiple light sensors.
 * @author Team06
 *
 */
public class LightLocalizer implements Runnable{

  private static final long COLOUR_PERIOD = 100;

  public static float colourIDLeft;
  public static float colourIDRight;
  public static float colourIDFront;
  
  public static float minLeft;
  public static float minRight;
  
  static SampleProvider colourValueLeft = LEFT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueRight = RIGHT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueFront = FRONT_COL_SENSOR.getMode("Red"); 
  static float[] colourDataLeft = new float[colourValueLeft.sampleSize()];
  static float[] colourDataRight = new float[colourValueRight.sampleSize()];
  static float[] colourDataFront = new float[colourValueFront.sampleSize()];

  /**
   * This method is used to continuously read data from the left and right light sensors..
   */
  public void run() {

    long updateStart;
    long updateDuration;
    updateStart = System.currentTimeMillis();
    while (true) {
      LEFT_COL_SENSOR.getMode("Red").fetchSample(colourDataLeft, 0);
      colourIDLeft = colourDataLeft[0] * 100;
      if (colourIDLeft < minLeft) {
        minLeft = colourIDLeft;
      }
      RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
      colourIDRight = colourDataRight[0] * 100;
      if (colourIDRight < minRight) {
        minRight = colourIDRight;
      }
      //Display.showText("Left: " + colourIDLeft, "Right: " + colourIDRight);
      // this ensures that the light sensor runs once every 3 ms.
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < COLOUR_PERIOD) {
        sleepFor(COLOUR_PERIOD - updateDuration);
      }

    }
  }

  /**
   * This method will read data from the top light sensor
   */
  public static double readFrontLightData() {
    FRONT_COL_SENSOR.getMode("Red").fetchSample(colourDataFront, 0);
    colourIDFront = colourDataFront[0]*100;
    return colourIDFront;
  }
  
  /**
   * This method will read data from the left light sensor
   */
  public static double readLeftLightData() {
    LEFT_COL_SENSOR.getMode("Red").fetchSample(colourDataLeft, 0);
    colourIDFront = colourDataFront[0]*100;
    return colourIDFront;
  }
  
  /**
   * This method will read data from the right light sensor
   */
  public static double readRightLightData() {
    RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
    colourIDFront = colourDataFront[0]*100;
    return colourIDFront;
  }
  
  /**
   * This method will adjust the robot's position to have both sensor directly on a line.
   */
  public static void lineAdjustment() {
    
    boolean leftLineNotDetected = true;
    boolean rightLineNotDetected = true;

    //Drive robot straight until it sees a line
    Driver.drive();
    LightLocalizer.colourIDLeft = 50;
    LightLocalizer.colourIDRight = 50;
    while (leftLineNotDetected & rightLineNotDetected) {

      if (LightLocalizer.colourIDLeft < LINE_THRESHOLD) {
        //Driver.stopMotors();
        //Driver.turnBy(1);
        Driver.stopMotorsInstantaneously();
        leftLineNotDetected = false;
      }

      if (LightLocalizer.colourIDRight < LINE_THRESHOLD) {
        //Driver.stopMotors();
        //Driver.turnBy(1);
      Driver.stopMotorsInstantaneously();
        rightLineNotDetected = false;
      }
    }

    //Untill both light sensors see a line adjust one of them
    if (rightLineNotDetected) {
      Driver.setSpeeds(0, 20);
      rightMotor.forward();
      while (rightLineNotDetected) {
        if (LightLocalizer.colourIDRight < LINE_THRESHOLD) {
          //Driver.stopMotors();
          //Driver.turnBy(1);
          Driver.stopMotorsInstantaneously();
          rightLineNotDetected = false;
        }
      }
    }else {
      Driver.setSpeeds(20, 0);
      leftMotor.forward();
      while (leftLineNotDetected) {
        if (LightLocalizer.colourIDLeft < LINE_THRESHOLD) {
          //Driver.stopMotors();
          //Driver.turnBy(1);
          Driver.stopMotorsInstantaneously();
          leftLineNotDetected = false;
        }
      }
    }
    Driver.setSpeeds(LINE_DETECTION_SPEED,LINE_DETECTION_SPEED);
   // Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
  }
}