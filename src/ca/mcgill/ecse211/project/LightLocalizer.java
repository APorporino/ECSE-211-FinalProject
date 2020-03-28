package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.FRONT_COL_SENSOR;
import static ca.mcgill.ecse211.project.Resources.FULL_SPIN_DEG;
import static ca.mcgill.ecse211.project.Resources.LEFT_COL_SENSOR;
import static ca.mcgill.ecse211.project.Resources.LIGHTLOCALIZER_PERIOD;
import static ca.mcgill.ecse211.project.Resources.LIGHT_TO_CENTER;
import static ca.mcgill.ecse211.project.Resources.LINE_DETECTION_SPEED;
import static ca.mcgill.ecse211.project.Resources.LINE_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.LOC_BACKUP_DISTANCE;
import static ca.mcgill.ecse211.project.Resources.MIN_LIGHT_DATA;
import static ca.mcgill.ecse211.project.Resources.ONE_LIGHT_ROTATION;
import static ca.mcgill.ecse211.project.Resources.RIGHT_COL_SENSOR;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.lightLocalizer;
import static ca.mcgill.ecse211.project.Resources.odo;
import static ca.mcgill.ecse211.project.Resources.rightMotor;

import lejos.robotics.SampleProvider;

/**
 * This class is used to control and receive readings from multiple light sensors.
 * @author Team06
 *
 */
public class LightLocalizer implements Runnable {

  // class variables used to store the color values from the light sensor
  public static float colourIDLeft;
  public static float colourIDRight;
  public static float colourIDFront;

  /**
   * This variable will store the minimum light value detected for the left sensor.
   */
  public static float minLeft;
  /**
   * This variable will store the minimum light value detected for the right sensor.
   */
  public static float minRight;

  /**
   * This variable will store how many times the left light sensor detected a line.
   */
  public static float counterLeft;

  /**
   * This variable will store how many times the left right sensor detected a line.
   */
  public static float counterRight;

  // buffers to store the data from the light sensors
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
      if (colourIDLeft < LINE_THRESHOLD) {
        counterLeft++;
      }
      if (colourIDRight < LINE_THRESHOLD) {
        counterRight++;
      }
      RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
      colourIDRight = colourDataRight[0] * 100;

      if (colourIDRight < minRight) {
        minRight = colourIDRight;
      }

      // this ensures that the light sensor runs once every 3 ms.
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < LIGHTLOCALIZER_PERIOD) {
        sleepFor(LIGHTLOCALIZER_PERIOD - updateDuration);
      }
    }
  }

  /**
   * This method will read data from the top light sensor.
   */
  public static double readFrontLightData() {
    FRONT_COL_SENSOR.getMode("Red").fetchSample(colourDataFront, 0);
    colourIDFront = colourDataFront[0] * 100;
    return colourIDFront;
  }

  /**
   * This method will read data from the left light sensor.
   */
  public static double readLeftLightData() {
    LEFT_COL_SENSOR.getMode("Red").fetchSample(colourDataLeft, 0);
    colourIDFront = colourDataFront[0] * 100;
    return colourIDFront;
  }

  /**
   * This method will read data from the right light sensor.
   */
  public static double readRightLightData() {
    RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
    colourIDFront = colourDataFront[0] * 100;
    return colourIDFront;
  }

  /**
   * This method will adjust the robot's position to have both sensor directly on a line.
   */
  public static void lineAdjustment() {

    // boolean variables which control the motion of the motors while doing line detecting
    boolean leftLineNotDetected;
    boolean rightLineNotDetected;
    leftLineNotDetected = true;
    rightLineNotDetected = true;

    //Drive robot straight until it sees a line
    Driver.drive();
    LightLocalizer.colourIDLeft = 50;
    LightLocalizer.colourIDRight = 50;
    while (leftLineNotDetected & rightLineNotDetected) {

      if (LightLocalizer.colourIDLeft < LINE_THRESHOLD) {
        Driver.stopMotorsInstantaneously();
        leftLineNotDetected = false;
      }

      if (LightLocalizer.colourIDRight < LINE_THRESHOLD) {
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
          Driver.stopMotorsInstantaneously();
          rightLineNotDetected = false;
        }
      }
    } else {
      Driver.setSpeeds(20, 0);
      leftMotor.forward();
      while (leftLineNotDetected) {
        if (LightLocalizer.colourIDLeft < LINE_THRESHOLD) {
          Driver.stopMotorsInstantaneously();
          leftLineNotDetected = false;
        }
      }
    }
    Driver.setSpeeds(LINE_DETECTION_SPEED,LINE_DETECTION_SPEED);
  }

  /**
   * This method will use the two side light sensors to adjust to robots position 
   * closer to the waypoint.
   * @param x x position
   * @param y y position
   */
  public static void localizeToPoint(int x,int y) {

    //otherwise fix distance using light localization
    Navigation.turnTo(0);
    Thread lightThread = new Thread(lightLocalizer);
    lightThread.start();

    if ((LightLocalizer.colourIDLeft < LINE_THRESHOLD) 
        & (LightLocalizer.colourIDRight < LINE_THRESHOLD)) {
      bothSensorsOnLine();
    } else if (LightLocalizer.colourIDLeft < LINE_THRESHOLD) {
      leftSensorOnLine();
    } else if (LightLocalizer.colourIDRight < LINE_THRESHOLD) {
      rightSensorOnLine();
    } else {
      noSensorOnLine();
    }

    //reset the odometers to a more accurate values
    odo.setX(x * TILE_SIZE);
    odo.setY(y * TILE_SIZE);
    odo.setTheta(90);

    lightThread.interrupt();
  }

  /**
   * This method will localize the robot to waypoint knowing that 
   * only the left light sensors is currently touching a line.
   */
  public static void leftSensorOnLine() {
    rightMotor.rotate(-ONE_LIGHT_ROTATION);     //make center of rotation on y axis
    Driver.turnBy(-FULL_SPIN_DEG / 4);                          //turn back to zero degrees
    LightLocalizer.minLeft = MIN_LIGHT_DATA;        //reset the minimum to high value
    LightLocalizer.minRight = MIN_LIGHT_DATA;

    //adjust to make both sensors on the x axis line.
    Driver.moveStraightFor(-LOC_BACKUP_DISTANCE);       //backup
    if ((LightLocalizer.minLeft < LINE_THRESHOLD) & (LightLocalizer.minRight < LINE_THRESHOLD)) {
      //we passed the line
      LightLocalizer.lineAdjustment();
    } else {
      //the line is in front of us
      Driver.moveStraightFor(LOC_BACKUP_DISTANCE);
      LightLocalizer.lineAdjustment();
    }
    Driver.moveStraightFor(LIGHT_TO_CENTER);
  }

  /**
   * This method will localize the robot to waypoint knowing that only the 
   * right light sensors is currently touching a line.
   */
  public static void rightSensorOnLine() {
    leftMotor.rotate(-ONE_LIGHT_ROTATION);          //make center of rotation on y axis
    Driver.turnBy(FULL_SPIN_DEG / 4);                             //turn back to zero degrees
    LightLocalizer.minLeft = MIN_LIGHT_DATA;
    LightLocalizer.minRight = MIN_LIGHT_DATA;

    //adjust to make both sensors on the x axis line.
    Driver.moveStraightFor(-LOC_BACKUP_DISTANCE);
    if ((LightLocalizer.minLeft < LINE_THRESHOLD) & (LightLocalizer.minRight < LINE_THRESHOLD)) {
      //we passed the line
      LightLocalizer.lineAdjustment();
    } else {
      //the line is in front of us
      Driver.moveStraightFor(LOC_BACKUP_DISTANCE);
      LightLocalizer.lineAdjustment();
    }
    Driver.moveStraightFor(LIGHT_TO_CENTER);
  }

  /**
   * This method will localize the robot to waypoint knowing that
   *  both light sensors are currently touching a line.
   */
  public static void bothSensorsOnLine() {
    Driver.moveStraightFor(LIGHT_TO_CENTER);
    Driver.turnBy(FULL_SPIN_DEG / 4);
    LightLocalizer.minLeft = MIN_LIGHT_DATA;
    LightLocalizer.minRight = MIN_LIGHT_DATA;

    //adjust to make both sensors on the x axis line.
    Driver.moveStraightFor(-LOC_BACKUP_DISTANCE);
    if ((LightLocalizer.minLeft < LINE_THRESHOLD) & (LightLocalizer.minRight < LINE_THRESHOLD)) {
      //we passed the line
      LightLocalizer.lineAdjustment();
    } else {
      //the line is in front of us
      Driver.moveStraightFor(LOC_BACKUP_DISTANCE);
      LightLocalizer.lineAdjustment();
    }
    Driver.moveStraightFor(LIGHT_TO_CENTER);
  }

  /**
   * This method will localize the robot to waypoint knowing that 
   * the light sensors aren't currently touching a line.
   */
  public static void noSensorOnLine() {
    //resets the min light data to high value
    LightLocalizer.minLeft = MIN_LIGHT_DATA;
    LightLocalizer.minRight = MIN_LIGHT_DATA;

    //adjust to make both sensors on the y axis line.
    Driver.moveStraightFor(-LOC_BACKUP_DISTANCE);
    if ((LightLocalizer.minLeft < LINE_THRESHOLD) & (LightLocalizer.minRight < LINE_THRESHOLD)) {
      //Now we know we passed the line
      LightLocalizer.lineAdjustment();
    } else {
      //the line is in front of us
      Driver.moveStraightFor(LOC_BACKUP_DISTANCE);
      LightLocalizer.lineAdjustment();
    }
    Driver.moveStraightFor(LIGHT_TO_CENTER);

    Driver.turnBy(FULL_SPIN_DEG / 4);
    LightLocalizer.minLeft = MIN_LIGHT_DATA;
    LightLocalizer.minRight = MIN_LIGHT_DATA;

    //adjust to make both sensors on the x axis line.
    Driver.moveStraightFor(-LOC_BACKUP_DISTANCE);
    if ((LightLocalizer.minLeft < LINE_THRESHOLD) & (LightLocalizer.minRight < LINE_THRESHOLD)) {
      //Now we know we passed the line
      LightLocalizer.lineAdjustment();
    } else {
      //the line is in front of us
      Driver.moveStraightFor(LOC_BACKUP_DISTANCE);
      LightLocalizer.lineAdjustment();
    }
    Driver.moveStraightFor(LIGHT_TO_CENTER);
  }
}