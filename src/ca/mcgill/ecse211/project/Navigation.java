package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.project.Main.sleepFor;


public class Navigation {

  /**
   * This method starts a loops through all grid positions in the map and goes to each one.
   */
  public static void drive(final int[][] map) {
        for (int[] elem: map) {
          travelTo(elem[0], elem[1]);

          sleepFor(PAUSE_TIME);

          LightLocalizer.lightLocToPoint(elem[0], elem[1]);
          //localizeToPoint(elem[0], elem[1]);
          //ultrasonicLocalizer.localizeToPoint(elem[0], elem[1]);
        }
        // int lastElement = map.length -1;

        // ultrasonicLocalizer.localizeToPoint(map[lastElement][0], map[lastElement][1]);      //localize at the last point
      
  }

  /**
   * This method will use the two side light sensors to adjust to robots position.
   */
  public static void localizeToPoint(int x, int y) {
    double currentAngle = odo.getXyt()[2];
    turnTo(90);
    Driver.moveStraightFor(BACKUP_DISTANCE);
    LightLocalizer.lineAdjustment();
    turnTo(currentAngle);

  }

  /**
   * This method will make the robot travel to a specified x and y grid position.
   * @param x
   * @param y
   */
  public static void travelTo(int x, int y) {

    //Distance from center of robot to position (1,1)
    double[] angleAndDistance = getAngleAndDistance(x, y);

    turnTo(angleAndDistance[1]);

    double[] odoValuesBefore = odo.getXyt();

    Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
    leftMotor.forward();
    rightMotor.forward();
    boolean farFromRing = true;

    //This section of code will make sure the robot stops if a ring is nearby
    while (farFromRing) {
      //If we detect a ring logic
      if (ultrasonicLocalizer.currentDistance <= RING_THRESHOLD) {
        Driver.setSpeeds(APPROACHING_SPEED, APPROACHING_SPEED);
        if (ultrasonicLocalizer.currentDistance <= RING_CLOSE) {
          Driver.stopMotors();
          farFromRing = false;
          Sound.twoBeeps();
          topMotor.setSpeed(40);
          topMotor.rotate(110);
          detectRing();
          topMotor.rotate(-110);
          Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
        }
      }
      //Make sure that we still stop once we reach the waypoint
      double deltaX = Math.pow((odoValuesBefore[0] - odo.getXyt()[0]),2);      
      double deltaY = Math.pow((odoValuesBefore[1] - odo.getXyt()[1]),2);
      double distanceTravelled = Math.pow(deltaX+deltaY, .5);
      if (distanceTravelled >= angleAndDistance[0]) {
        farFromRing = false;
      }
    }

    //Determine how much more distance we need to cover to get to waypoint
    double[] odoValuesAfter = odo.getXyt();
    double deltaX = Math.pow((odoValuesBefore[0] - odoValuesAfter[0]),2);      
    double deltaY = Math.pow((odoValuesBefore[1] - odoValuesAfter[1]),2);
    double distanceTravelled = Math.pow(deltaX+deltaY, .5);

    Driver.moveStraightFor(angleAndDistance[0] - distanceTravelled);
  }

  /**
   * This method will return the distance and angle needed to move to grid position (x y).
   * @param x Starting x distance from wall
   * @param y Starting y distance from wall
   * @return Array of doubles, first element distance to position, second is angle needed to turn.
   */
  public static double[] getAngleAndDistance(int x, int y) {
    double[] positions = odo.getXyt();
    double[] returnValue = new double[2];
    double angleToTurnTo;
    double base = x*TILE_SIZE - positions[0];   
    double height = y*TILE_SIZE - positions[1];
    double hypotenus = Math.pow((Math.pow(base, 2) + Math.pow(height, 2)), .5);
    returnValue[0] = hypotenus;

    //if its traveling in a straight line
    if (base >= -STRAIGHT_LINE && base <= STRAIGHT_LINE) {
      if (height > 0) {
        angleToTurnTo =0;
      }else {
        angleToTurnTo = FULL_SPIN_DEG / 2;
      }
    } else if (height >= -STRAIGHT_LINE && height <= STRAIGHT_LINE) {
      if (base > 0) {
        angleToTurnTo = FULL_SPIN_DEG / 4;
      }
      else {
        angleToTurnTo = FULL_SPIN_DEG * 3/4;
      }
    }//not straight line
    else{
      angleToTurnTo = Math.atan(Math.abs(height/base));
      angleToTurnTo = Math.abs(Math.toDegrees(angleToTurnTo));

      //logic to figure out which angle we want to turn by
      if (height < 0 && base < 0) {
        //Quadrant 3
        angleToTurnTo = FULL_SPIN_DEG * 3/4 - angleToTurnTo;

      }
      else if (height < 0) {
        //Quadrant 4
        angleToTurnTo = FULL_SPIN_DEG / 4 + angleToTurnTo;

      }else if (base < 0) {
        //Quadrant 2
        angleToTurnTo = FULL_SPIN_DEG * 3/4 + angleToTurnTo;

      }else {
        //Quadrant 1
        angleToTurnTo = FULL_SPIN_DEG / 4 - angleToTurnTo;
      }
    }
    returnValue[1] = angleToTurnTo;
    return returnValue;
  }

  /**
   * This method will turn the robot to a specified degree based of current angle it is at.
   * @param theta the angle to end up at
   */
  public static void turnTo(double theta) {
    double currentAngle = odo.getXyt()[2];
    double difference = theta - currentAngle;

    //If turning angle greater than 180 turn the other way
    if (Math.abs(difference) > FULL_SPIN_DEG / 2) {
      if (difference < 0) {
        difference = FULL_SPIN_DEG  + difference;
        Driver.turnBy(difference);
      }else {
        difference = difference - FULL_SPIN_DEG;
        Driver.turnBy(difference);
      }
    }else {
      Driver.turnBy(difference);
    }

  }
  /**
   * This method will lower the front light sensor start a new thread to read the colour value of the ring and attempt to detect the colour.
   */
  public static void detectRing() {
    TEXT_LCD.clear();
    TEXT_LCD.drawString("Object Detected", 0,1);
//    Thread colour = new Thread(colorDetector);
//    colour.start();
    
    //Will get NUM_READINGS amount of readings from the colour detector and return the average.
   double[] averageReadings = ColourDetector.getReadings();
//    System.out.println("RED average: " + averageReadings[0]);
//    System.out.println("\nBLUE average: " + averageReadings[1]);
//    System.out.println("\nGREEN average: " + averageReadings[2]);
    sleepFor(1000);
    //colorDetector.updateRingColour(colorDetector.colourRed, colorDetector.colourGreen, colorDetector.colourBlue);
    colorDetector.updateRingColour(averageReadings[0], averageReadings[1],averageReadings[2]);
    TEXT_LCD.drawString("COLOUR: " + colorDetector.ringColour, 0, 2);
    //colour.interrupt();
  }

}
