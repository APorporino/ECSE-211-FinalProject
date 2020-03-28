package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.*;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The singleton odometer class keeps track of the robot's (x, y, theta) position.
 * 
 * @author TA
 * @author team 06
 */

public class Odometer implements Runnable {

  /**
   * The x-axis position in cm.
   */
  private volatile double x;

  /**
   * The y-axis position in cm.
   */
  private volatile double y;

  /**
   * The orientation (head angle) in degrees.
   */
  private volatile double theta; 

  // Thread control tools
  /**
   * Fair lock for concurrent writing.
   */
  private static Lock lock = new ReentrantLock(true);

  /**
   * Indicates if a thread is trying to reset any position parameters.
   */
  private volatile boolean isResetting = false;

  /**
   * Lets other threads know that a reset operation is over.
   */
  private Condition doneResetting = lock.newCondition();

  private static Odometer odo; // Returned as singleton

  // Motor-related variables
  private static int leftMotorTachoCount;
  private static int rightMotorTachoCount;

  private static int leftMotorLastTachoCount = leftMotor.getTachoCount();
  private static int rightMotorLastTachoCount = rightMotor.getTachoCount();

  /**
   * This is the default constructor of this class. It initiates all motors and variables once. It
   * cannot be accessed externally.
   */
  public Odometer() {
    setXyt(TILE_SIZE, TILE_SIZE, 0);
  }

  /**
   * Returns the Odometer Object. Use this method to obtain an instance of Odometer.
   * 
   * @return the Odometer Object
   */
  public static synchronized Odometer getOdometer() {
    if (odo == null) {
      odo = new Odometer();
    }
    return odo;
  }

  /**
   * This method is where the logic for the odometer will run.
   */
  public void run() {
    long updateStart;
    long updateDuration;
    double distL; 
    double distR;
    double deltaD;

    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();
    leftMotorLastTachoCount = leftMotor.getTachoCount();
    rightMotorLastTachoCount = rightMotor.getTachoCount();

    while (true) {
      updateStart = System.currentTimeMillis();

      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();


      // Calculate new robot position based on tachometer counts
      // compute L and R wheel displacements
      distL = Math.toRadians(WHEEL_RAD * (leftMotorTachoCount - leftMotorLastTachoCount));
      distR = Math.toRadians(WHEEL_RAD * (rightMotorTachoCount - rightMotorLastTachoCount));

      leftMotorLastTachoCount = leftMotorTachoCount;
      rightMotorLastTachoCount = rightMotorTachoCount;

      // Update odometer values with new calculated values using update()
      deltaD = (distL + distR) / 2;
      theta += Math.toDegrees((distL - distR) / BASE_WIDTH);         

      // calculate the x/y displacement
      x += deltaD * Math.sin(Math.toRadians(theta));
      y += deltaD * Math.cos(Math.toRadians(theta));

      // this ensures that the odometer only runs once every period
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < ODOMETER_PERIOD) {
        sleepFor(ODOMETER_PERIOD - updateDuration);
      }
    }
  }

  // IT IS NOT NECESSARY TO MODIFY ANYTHING BELOW THIS LINE
  /**
   * Returns the Odometer data.
   * 
   * <p>Writes the current position and orientation of the robot onto the odoData array.
   * {@code odoData[0] = x, odoData[1] = y; odoData[2] = theta;}
   * 
   * @return the odometer data.
   */
  public double[] getXyt() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }
      position[0] = x;
      position[1] = y;
      position[2] = theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
    return position;
  }

  /**
   * Adds dx, dy and dtheta to the current values of x, y and theta, respectively. Useful for
   * odometry.
   * 
   * @param dx the change in x
   * @param dy the change in y
   * @param dtheta the change in theta
   */
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isResetting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360; // keeps the updates within 360 degrees
      isResetting = false;
      doneResetting.signalAll(); // Let the other threads know we are done resetting
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overrides the values of x, y and theta. Use for odometry correction.
   * 
   * @param x the value of x
   * @param y the value of y
   * @param theta the value of theta in degrees
   */
  public void setXyt(double x, double y, double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites x. Use for odometry correction.
   * 
   * @param x the value of x
   */
  public void setX(double x) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites y. Use for odometry correction.
   * 
   * @param y the value of y
   */
  public void setY(double y) {
    lock.lock();
    isResetting = true;
    try {
      this.y = y;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites theta. Use for odometry correction.
   * 
   * @param theta the value of theta
   */
  public void setTheta(double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

}
