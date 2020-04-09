package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.APPROACHING_SPEED;
import static ca.mcgill.ecse211.project.Resources.BASE_WIDTH;
import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.FULL_SPIN_DEG;
import static ca.mcgill.ecse211.project.Resources.LINE_DETECTION_SPEED;
import static ca.mcgill.ecse211.project.Resources.MOTOR_DIFF;
import static ca.mcgill.ecse211.project.Resources.MOTOR_HIGH;
import static ca.mcgill.ecse211.project.Resources.OBJECT_CLOSE;
import static ca.mcgill.ecse211.project.Resources.OBJECT_THRESHOLD;
import static ca.mcgill.ecse211.project.Resources.SLEEP_TIME;
import static ca.mcgill.ecse211.project.Resources.ROTATION_SPEED;
import static ca.mcgill.ecse211.project.Resources.TEXT_LCD;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.WALL_DIST;
import static ca.mcgill.ecse211.project.Resources.WALL_DIST_ERR_THRESH;
import static ca.mcgill.ecse211.project.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.project.Resources.odo;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.topMotor;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;
import ca.mcgill.ecse211.project.ColourDetector.Colour;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class will be used to navigate the robot to certain positions.
 * It contains all functionality dealing with moving the robot.
 * It also implements obstacle avoidance.
 * @author Team06
 *
 */
public class Navigation {

  /**
   * Constructor.
   */
  public Navigation() {
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
   * Process a movement based on the US distance passed in (BANG-BANG style).
   * 
   * @param distance the distance in cm
   * @return 
   */
  public static void bangBangController(int distance) {
    if (Math.abs(distance - WALL_DIST) < WALL_DIST_ERR_THRESH){
      leftMotor.setSpeed(MOTOR_HIGH);
      rightMotor.setSpeed(MOTOR_HIGH);
      leftMotor.forward();
      rightMotor.forward();

    }
    else if (distance - WALL_DIST < -Resources.WALL_TOO_CLOSE) {
      //way too close to the wall so go backwards
      leftMotor.setSpeed(2*MOTOR_HIGH);
      rightMotor.setSpeed(2*MOTOR_HIGH);
      leftMotor.backward();
      rightMotor.backward();
    }
    else if ((distance - WALL_DIST) < 0) {
      //Too close to wall so speed up left motor
      leftMotor.setSpeed(MOTOR_HIGH + MOTOR_DIFF);
      rightMotor.setSpeed(MOTOR_HIGH - MOTOR_DIFF);
      leftMotor.forward();
      rightMotor.forward();
    }
  }

  /**
   * This method causes the robot to travel to the  field location (x,y), specified in tile points.
   *  It also ensures it does not hit an object.
   *  If an obstacle is avoided, the final position will not be (x,y)
   * @param x x position 
   * @param y y position
   */
  public static void travelTo(double x, double y) {
    double[] odoValuesBefore = odo.getXyt();

    double dx = x - odo.getXyt()[0];
    double dy = y - odo.getXyt()[1];
    double dt = Math.atan2(dx, dy);

    turnTo(dt);

    // Minimum distance that the robot will travel, always the hypotenuse of the triangle.
    double hypotenus = Math.hypot(dx, dy);

    setSpeed(FORWARD_SPEED);
    leftMotor.forward();
    rightMotor.forward();
  
    double distanceTravelled = 0;
    //    //This section of code will make sure the robot avoids obstacles
    while(distanceTravelled < hypotenus) {
      distanceTravelled = euclidDistance(odoValuesBefore[0], odoValuesBefore[1], 
          odo.getXyt()[0], odo.getXyt()[1]);
      bangBangController(usLocalizer.currentDistance);
      Main.sleepFor(SLEEP_TIME );
    }
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
   * Stops both motors.
   */
  public static void stopMotors() {
    leftMotor.stop();
    rightMotor.stop();
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
   * Returns a thread that will rotate the robot once.
   * @return Thread that rotates the robot once.
   */
  public static Thread rotate() {
    // spawn a new Thread to avoid this method blocking
    Thread rotateThread = new Thread() {
      public void run() {
        setSpeed(ROTATION_SPEED);
        turnBy(FULL_SPIN_DEG);
      }
    };
    return rotateThread;
  }

  /**
   * Will drive the robot continuously straight at LINE_DETECTION_SPEED speed.
   */
  public static void drive() {
    setSpeed(LINE_DETECTION_SPEED);
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
   * Calls wait method on both left and right motor threads.
   */
  public static void waitMotors() {
    try {
      leftMotor.wait();
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
    try {
      rightMotor.wait();
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }

  /**
   * Stops both motors by first setting their speed to 0.
   */
  public static void stopMotorsInstantaneously() {
    setSpeeds(0,0);
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
