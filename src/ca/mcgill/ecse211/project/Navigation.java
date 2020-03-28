package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.BASE_WIDTH;
import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.ROTATION_SPEED;
import static ca.mcgill.ecse211.project.Resources.TEXT_LCD;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.project.Resources.colorDetector;
import ca.mcgill.ecse211.project.ColourDetector.Colour;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class will be used to navigate the robot to certain positions.
 * @author Team06
 *
 */
public class Navigation implements Runnable {

  public static int numberRingsDetected = 0;
  public static Colour[] colours = new Colour[5];

  
  private static EV3LargeRegulatedMotor leftMotor;
  private static EV3LargeRegulatedMotor rightMotor; 
  private static Odometer odo;
  
  /**
   * Constructor.
   * 
   * @param odo odometer
   * @param leftMotor left motor of the robot
   * @param rightMotor right motor of the robot
   */
  public Navigation(Odometer odo, EV3LargeRegulatedMotor leftMotor, 
      EV3LargeRegulatedMotor rightMotor) {
    Navigation.odo = Odometer.getOdometer();
    Navigation.leftMotor = leftMotor;
    Navigation.rightMotor = rightMotor;
  }

  /**
   * Maps of the navigation.
   */
  // TODO: this need to be integrated with the array representation of the map
  public void run() {
    travelTo(2 * TILE_SIZE, 1 * TILE_SIZE);
    travelTo(3 * TILE_SIZE, 2 * TILE_SIZE);
    travelTo(3 * TILE_SIZE, 3 * TILE_SIZE);
    travelTo(1 * TILE_SIZE, 3 * TILE_SIZE);
    travelTo(2 * TILE_SIZE, 2 * TILE_SIZE);
  }

  /**
   * This method causes the robot to travel to the  field location (x,y), specified in tile points.
   * This method should continuously call turnTo(double theta), set the motor speed to forward.
   * This will make sure that your heading is updated until you reach your exact goal. 
   * This method will use the odometer.
   * @param x x position 
   * @param y y position
   */
  // TODO: need to react if an obstacle is nearby
  public static void travelTo(double x, double y) {
    double dx = x - odo.getXyt()[0];
    double dy = y - odo.getXyt()[1];
    double dt = Math.atan2(dx, dy);

    turnTo(dt);

    // Minimum distance that the robot will travel, always the hypotenuse of the triangle.
    double dist = Math.hypot(dx, dy);

    setSpeed(FORWARD_SPEED);
    leftMotor.rotate(convertDistance(dist), true);
    rightMotor.rotate(convertDistance(dist), false);
    stopMotors();
  }

  /**
   * This method causes the robot to turn (on point) to the absolute heading theta. 
   * This method should turn a MINIMAL angle to its target.
   * @param theta angle theta in radians
   */
  public static void turnTo(double theta) {

    setSpeed(ROTATION_SPEED);
    // get the minimum turning angle 
    double turningAngle = getMinAngle(theta - Math.toRadians(odo.getXyt()[2]));

    leftMotor.rotate(convertAngle(Math.toDegrees(turningAngle)), true);
    rightMotor.rotate(-convertAngle(Math.toDegrees(turningAngle)), false);
  }

  /**
   * This method calculate the minimum turning angle to suit the lab description.
   * @param theta angle in radians
   * @return
   */
  public static double getMinAngle(double theta) {
    // angle > 180, decrease by 360
    if (theta > Math.PI) {
      theta -= 2 * Math.PI;
    }
    // angle < 180, increase by 360
    if (theta < - Math.PI) {
      theta += 2 * Math.PI;
    }
    return theta;
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * Code from lab 2 Odometer, SquareDriver.java class.
   * 
   * @param distance the input distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * Code from lab 2 Odometer, SquareDriver.java class.
   * 
   * @param angle the input angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * BASE_WIDTH * angle / 360.0);
  }

  /**
   * Stops both motors.
   * Code from lab 2 Odometer, SquareDriver.java class.
   */
  public static void stopMotors() {
    leftMotor.stop();
    rightMotor.stop();
  }

  /**
   * Sets the speed of both motors to the same values.
   * Code from lab 2 Odometer, SquareDriver.java class.
   * 
   * @param speed the speed in degrees per second
   */
  public static void setSpeed(int speed) {
    setSpeeds(speed, speed);
  }

  /**
   * Sets the speed of both motors to different values.
   * Code from lab 2 Odometer, SquareDriver.java class.
   * 
   * @param leftSpeed the speed of the left motor in degrees per second
   * @param rightSpeed the speed of the right motor in degrees per second
   */
  public static void setSpeeds(int leftSpeed, int rightSpeed) {
    leftMotor.setSpeed(leftSpeed);
    rightMotor.setSpeed(rightSpeed);
  }

  /**
   * Sets the acceleration of both motors.
   * Code from lab 2 Odometer, SquareDriver.java class.
   * 
   * @param acceleration the acceleration in degrees per second squared
   */
  public static void setAcceleration(int acceleration) {
    leftMotor.setAcceleration(acceleration);
    rightMotor.setAcceleration(acceleration);
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
