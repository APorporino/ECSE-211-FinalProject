package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.APPROACHING_SPEED;
import static ca.mcgill.ecse211.project.Resources.FULL_SPIN_DEG;
import static ca.mcgill.ecse211.project.Resources.LIGHT_TO_CENTER;
import static ca.mcgill.ecse211.project.Resources.LINE_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.LOC_BACKUP_DISTANCE;
import static ca.mcgill.ecse211.project.Resources.MIN_LIGHT_DATA;
import static ca.mcgill.ecse211.project.Resources.ONE_LIGHT_ROTATION;
import static ca.mcgill.ecse211.project.Resources.RING_CLOSE;
import static ca.mcgill.ecse211.project.Resources.RING_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.ROTATION_SPEED;
import static ca.mcgill.ecse211.project.Resources.STRAIGHT_LINE;
import static ca.mcgill.ecse211.project.Resources.TEXT_LCD;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.colorDetector;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.lightLocalizer;
import static ca.mcgill.ecse211.project.Resources.odo;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.topMotor;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

import ca.mcgill.ecse211.project.ColourDetector.Colour;
import lejos.hardware.Sound;

/**
 * This class will be used to navigate the robot to certain positions.
 * @author Team06
 *
 */
public class Navigation {

  public static Colour[] colours = new Colour[5];
  public static int numberRingsDetected = 0;

  /**
   * This method starts a loops through all grid positions in the map and goes to each one.
   * At each point it will localize.
   */
  public static void drive(final int[][] map) {
    for (int[] elem: map) {
      TEXT_LCD.clear();
      TEXT_LCD.drawString("Moving to: " + elem[0] + "," + elem[1], 0, 1);

      Thread usThread = new Thread(usLocalizer);
      usThread.start();


      //update backup distance if we are close to wall.
      //makes sure we do not collide with a wall.
      if ((elem[0] == 1) & (elem[1] == 7)) {
        LOC_BACKUP_DISTANCE = TILE_SIZE / 4;
      }else if ((elem[0] == 7) & (elem[1] == 1)) {
        LOC_BACKUP_DISTANCE = TILE_SIZE / 4;
      }else {
        LOC_BACKUP_DISTANCE = TILE_SIZE / 2;
      }

      travelTo(elem[0], elem[1]);
      usThread.interrupt();

      LightLocalizer.localizeToPoint(elem[0], elem[1]);
    }
    finalWaypoint();
  }

  /**
   * This method will perform the requested actions after reaching the final waypoint.
   * Actions: beep three times, display number of rings detected and colour of rings in order.
   */
  public static void finalWaypoint() {
    Sound.beep();
    Sound.beep();
    Sound.beep();
    TEXT_LCD.clear();
    TEXT_LCD.drawString("Number rings: " + numberRingsDetected, 0, 1);

    for (int i = 0; i < numberRingsDetected; i++) {
      TEXT_LCD.drawString("Colour" + i + 1 + ": " +  colours[i], 0, i + 2);
    }
  }

  /**
   * This method returns the euclidian distance between two points in cm.
   * @param x1 first x pos 
   * @param y1 first y pos
   * @param x2 second x pos
   * @param y2 second y pos
   * @return Euclidian distance in cm.
   */
  public static double euclidDistance(double x1, double y1, double x2, double y2) {
    double sum = Math.pow((x1 - x2),2) + Math.pow((y1 - y2),2);
    return Math.pow(sum, .5);
  }

  /**
   * This method will make the robot travel to a specified x and y grid position.
   * @param x X grid position
   * @param y Y grid position
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
      if (usLocalizer.currentDistance <= RING_THRESHOLD) {
        Driver.setSpeeds(APPROACHING_SPEED, APPROACHING_SPEED);
        if (usLocalizer.currentDistance <= RING_CLOSE) {
          //Driver.stopMotors();
          Driver.turnBy(1);
          Driver.stopMotorsInstantaneously();
          farFromRing = false;

          topMotor.setSpeed(40);
          topMotor.rotate(110);
          detectRing();
          Sound.beep();
          Sound.beep();
          topMotor.rotate(-110);
          Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
        }
      }
      //Make sure that we still stop once we reach the waypoint
      double distanceTravelled = euclidDistance(odoValuesBefore[0], odoValuesBefore[1], 
          odo.getXyt()[0], odo.getXyt()[1]);
      if (distanceTravelled >= angleAndDistance[0]) {
        farFromRing = false;
      }
    }

    //Determine how much more distance we need to cover to get to waypoint
    double[] odoValuesAfter = odo.getXyt();
    double distanceTravelled = euclidDistance(odoValuesBefore[0], odoValuesBefore[1], 
        odoValuesAfter[0], odoValuesAfter[1]);

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
    double base = x * TILE_SIZE - positions[0];   
    double height = y * TILE_SIZE - positions[1];
    double hypotenus = Math.pow((Math.pow(base, 2) + Math.pow(height, 2)), .5);
    returnValue[0] = hypotenus;

    //if its traveling in a straight line
    if (base >= -STRAIGHT_LINE && base <= STRAIGHT_LINE) {
      if (height > 0) {
        angleToTurnTo = 0;
      } else {
        angleToTurnTo = FULL_SPIN_DEG / 2;
      }
    } else if (height >= -STRAIGHT_LINE && height <= STRAIGHT_LINE) {
      if (base > 0) {
        angleToTurnTo = FULL_SPIN_DEG / 4;
      } else {
        angleToTurnTo = FULL_SPIN_DEG * 3 / 4;
      }
    } else { //not straight line
      angleToTurnTo = Math.atan(Math.abs(height / base));
      angleToTurnTo = Math.abs(Math.toDegrees(angleToTurnTo));

      //logic to figure out which angle we want to turn by
      if (height < 0 && base < 0) {
        //Quadrant 3
        angleToTurnTo = FULL_SPIN_DEG * 3 / 4 - angleToTurnTo;
      } else if (height < 0) {
        //Quadrant 4
        angleToTurnTo = FULL_SPIN_DEG / 4 + angleToTurnTo;
      } else if (base < 0) {
        //Quadrant 2
        angleToTurnTo = FULL_SPIN_DEG * 3 / 4 + angleToTurnTo; 
      } else {
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
      } else {
        difference = difference - FULL_SPIN_DEG;
        Driver.turnBy(difference);
      }
    } else {
      Driver.turnBy(difference);
    }
  }

  /**
   * This method will lower the front light sensor start a new thread to read the 
   * colour value of the ring and attempt to detect the colour.
   */
  public static void detectRing() {
    TEXT_LCD.clear();
    TEXT_LCD.drawString("Object Detected", 0,1);

    //Will get NUM_READINGS amount of readings from the colour detector and return the average.
    double[] averageReadings = ColourDetector.getReadings();
    colorDetector.updateRingColour(averageReadings[0], averageReadings[1],averageReadings[2]);
    TEXT_LCD.drawString("COLOUR: " + colorDetector.ringColour, 0, 2);
    sleepFor(5000);
    colours[numberRingsDetected] = colorDetector.ringColour;
    numberRingsDetected++;
  }

}
