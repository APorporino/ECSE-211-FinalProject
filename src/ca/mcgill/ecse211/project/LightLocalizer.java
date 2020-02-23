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
  static SampleProvider colourValueLeft = LEFT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueRight = RIGHT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueFront = FRONT_COL_SENSOR.getMode("Red"); 
  static float[] colourDataLeft = new float[colourValueLeft.sampleSize()];
  static float[] colourDataRight = new float[colourValueRight.sampleSize()];
  static float[] colourDataFront = new float[colourValueFront.sampleSize()];

  /**
   * This method will localize the robot using light sensor, start the thread.
   */
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
    while (leftLineNotDetected & rightLineNotDetected) {

      if (LightLocalizer.colourIDLeft < BLUE_LINE_THRESHOLD) {
        Driver.stopMotors();
        //Driver.waitMotors();
        leftLineNotDetected = false;
      }

      if (LightLocalizer.colourIDRight < BLUE_LINE_THRESHOLD) {
        Driver.stopMotors();
      //Driver.waitMotors();
        rightLineNotDetected = false;
      }
    }

    //Untill both light sensors see a line adjust one of them
    if (rightLineNotDetected) {
      Driver.setSpeeds(0, 20);
      rightMotor.forward();
      while (rightLineNotDetected) {
        if (LightLocalizer.colourIDRight < BLUE_LINE_THRESHOLD) {
          Driver.stopMotors();
        //Driver.waitMotors();
          rightLineNotDetected = false;
        }
      }
    }else {
      Driver.setSpeeds(20, 0);
      leftMotor.forward();
      while (leftLineNotDetected) {
        if (LightLocalizer.colourIDLeft < BLUE_LINE_THRESHOLD) {
          Driver.stopMotors();
          leftLineNotDetected = false;
        }
      }
    }
    Driver.setSpeeds(LINE_DETECTION_SPEED,LINE_DETECTION_SPEED);
   // Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
  }

  /**
   * Localizing method using the light sensor to get closer to a specified grid position.
   * 
   * @param x xPosition to localize to in grid coordinates
   * @param y yPosition to localize to in grid coordinates
   */
  public static void lightLocToPoint(int x, int y) {
    double lineAngles[] = new double[4];
    int counter = 0;
    Thread lightLocThread = new Thread(lightLocalizer);
    lightLocThread.start();
    topMotor.setSpeed(40);
    topMotor.rotate(110);

    //rotate 360 degrees to detect black lines
    rotate();

    //store thetas when black lines are read from light sensor
    while (counter != 4) {
      while (readFrontLightData() > BLUE_LINE_THRESHOLD) {
      }

      lineAngles[counter] = odo.getXyt()[2]; // Record the angle at which we detected the line.

      counter++;

      Main.sleepFor(1000); // wait for a second to avoid multiple detections of the same line.
    }

    // calculate the x and y position relative to the grid point that we inputted
    double xPos = -SENSOR_TO_CENTER * Math.cos((lineAngles[2] - lineAngles[0]) / 2);
    double yPos = -SENSOR_TO_CENTER * Math.cos((lineAngles[3] - lineAngles[1]) / 2);
    double dTheta = -0.5 * Math.abs(lineAngles[1] - lineAngles[3]) + Math.PI - lineAngles[3];

    //odo.setTheta(odo.getXyt()[2] + dTheta);

    //checks if left or right sensor is on a black line
    //if so, turn to the right/left depending on xPos
    if(colourIDLeft <= BLUE_LINE_THRESHOLD || colourIDRight <= BLUE_LINE_THRESHOLD) {
      if(xPos<0) {
        Navigation.turnTo(90);
        Driver.moveStraightFor(BACKUP_DISTANCE);
      } else if(xPos>0) {
        Navigation.turnTo(270);
        Driver.moveStraightFor(BACKUP_DISTANCE);
      }
    }
    //correct the y position similar to when we localized to (1,1)
    if (yPos < 0) {
      Navigation.turnTo(0);
      Driver.moveStraightFor(BACKUP_DISTANCE);
      lineAdjustment();
      Driver.moveStraightFor(LIGHT_TO_CENTER);
    } else if (yPos > 0) {
      Navigation.turnTo(180);
      Driver.moveStraightFor(BACKUP_DISTANCE);
      lineAdjustment();
      Driver.moveStraightFor(LIGHT_TO_CENTER);
    }

    //correct the x position similar to when we localized to (1,1)
    if (xPos < 0) {
      Navigation.turnTo(90);
      Driver.moveStraightFor(BACKUP_DISTANCE);
      lineAdjustment();
      Driver.moveStraightFor(LIGHT_TO_CENTER);
      odo.setTheta(90);
    } else if (xPos > 0) {
      Navigation.turnTo(270);
      Driver.moveStraightFor(BACKUP_DISTANCE);
      lineAdjustment();
      Driver.moveStraightFor(LIGHT_TO_CENTER);
      odo.setTheta(270);
    }

    //correct odometer values
    odo.setX(x*TILE_SIZE);
    odo.setY(y*TILE_SIZE);
    lightLocThread.interrupt();
    //Navigation.turnTo(currentAngle);
    topMotor.rotate(-110);
  }

}