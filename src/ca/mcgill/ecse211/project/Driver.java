package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.*;

/**
 * This class is used to control the robots movement.
 */
public class Driver {

  /**
   * Rotates the robot once.
   */
  public static void rotate() {
    // spawn a new Thread to avoid this method blocking
    (new Thread() {
      public void run() {

        // reset the motors
        stopMotors();
        setSpeeds(ROTATION_SPEED,ROTATION_SPEED);
        turnBy(FULL_SPIN_DEG);

      }
    }).start();
  }

/**
 * Will drive the robot straight.
 */
  public static void drive() {
    stopMotors(); 
    setSpeeds(LINE_DETECTION_SPEED,LINE_DETECTION_SPEED);
    leftMotor.forward();
    rightMotor.forward();
  }

  /**
   * Turns the robot by a specified angle. Note that this method is different from
   * {@code Navigation.turnTo()}. For example, if the robot is facing 90 degrees, calling
   * {@code turnBy(90)} will make the robot turn to 180 degrees, but calling
   * {@code Navigation.turnTo(90)} should do nothing (since the robot is already at 90 degrees).
   * 
   * @param angle the angle by which to turn, in degrees
   */
  public static void turnBy(double angle) {
    leftMotor.rotate(convertAngle(angle), true);
    rightMotor.rotate(-convertAngle(angle), false);
  }

  /**
   * Stops both motors.
   */
  public static void stopMotors() {
    //setSpeeds(0, 0);
    leftMotor.stop();
    rightMotor.stop();
  }

  /**
   * Stops both motors.
   */
  public static void stopMotorsInstantaneously() {

    int speedL = leftMotor.getSpeed();
    int speedR = rightMotor.getSpeed();
    setSpeeds(0,0);
    leftMotor.stop();
    rightMotor.stop();
    setSpeeds(speedL, speedR);
  }

  /**
   * Stops the left motor instantaneously.
   */
  public static void leftMotorStop() {
    int speedL = leftMotor.getSpeed();
    int speedR = rightMotor.getSpeed();
    setSpeeds(0,speedR);
    leftMotor.stop();
    setSpeeds(speedL, speedR);
  }

  /**
   * Stops the right motor instantaneously.
   */
  public static void rightMotorStop() {
    int speedL = leftMotor.getSpeed();
    int speedR = rightMotor.getSpeed();
    setSpeeds(speedL,0);
    rightMotor.stop();
    setSpeeds(speedL, speedR);
  }

  /**
   * Sets the speed of both motors to the same values.
   * 
   * @param speed the speed in degrees per second
   */
  public static void setSpeed(int speed) {
    setSpeeds(speed, speed);
  }

  /**
   * Sets the speed of both motors to different values.
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
   * 
   * @param acceleration the acceleration in degrees per second squared
   */
  public static void setAcceleration(int acceleration) {
    leftMotor.setAcceleration(acceleration);
    rightMotor.setAcceleration(acceleration);
  }

  /**
   * Moves the robot straight for the given distance.
   * 
   * @param distance in feet (tile sizes), may be negative
   */
  public static void moveStraightFor(double distance) {
    leftMotor.rotate(convertDistance(distance), true);
    rightMotor.rotate(convertDistance(distance), false);
  }

  /**
   * Converts input distance to the total rotation of each wheel needed to cover that distance.
   * 
   * @param distance the input distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((FULL_SPIN_DEG / 2 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
   * angle.
   * 
   * @param angle the input angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * BASE_WIDTH * angle / FULL_SPIN_DEG);
  }


}
