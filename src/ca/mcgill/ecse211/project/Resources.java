package ca.mcgill.ecse211.project;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * List of coordinates for 4 x 4 grid Map1.
   */
  public static final int[][] MAP0 = {{3,1},{7,3},{7,1},{1,3}};

  /**
   * List of coordinates for 8 x 8 grid Map1.
   */
  public static final int[][] MAP1 = {{1,7},{3,4},{7,7},{7,4},{4,1}};

  /**
   * List of coordinates for 8 x 8 grid Map2.
   */
  public static final int[][] MAP2 = {{5,4},{1,7},{7,7},{7,4},{4,1}};

  /**
   * List of coordinates for 8 x 8 grid Map3.
   */
  public static final int[][] MAP3 = {{3,1},{7,4},{7,7},{1,7},{1,4}};

  /**
   * List of coordinates for 8 x 8 grid Map4.
   */
  public static final int[][] MAP4 = {{1,4},{3,7},{3,1},{7,4},{7,7}};

  /**
   * Variable for the threshold to determine if robot is traveling in straight line. 
   * Base = 0 or Height = 0.
   */
  public static final double STRAIGHT_LINE = 2.5;

  /**
   * The value used to rotate the robot in a full circle.
   */
  public static final int FULL_SPIN_DEG = 360;
  

  /**
   * The value used as max reading of US sensor.
   */
  public static final int FILTER_MAX = 255;
  
  /**
   * Period for ultrasonic reading.
   */
  public static final long US_PERIOD = 300;
  
  /**
   * Minimum value to reset the light localizer left and right data.
   */
  public static final long MIN_LIGHT_DATA = 60;

  /**
   * The speed at which the robot will rotate in degrees per second.
   */
  public static final int ROTATION_SPEED = 200;

  /**
   * The speed at which the robot will rotate in degrees per second.
   */
  public static final int LINE_DETECTION_SPEED = 100;

  /**
   * The distance from the sensor to the center of rotation in cm.
   */
  public static final double SENSOR_TO_CENTER = 10.5;

  /**
   * Distance from light to center of rotation in cm.
   */
  public static final double LIGHT_TO_CENTER = 4;
  
  /**
   * Distance used to back up the robot to localize using line detection.
   */
  public static final double BACKUP_DISTANCE = -8;

  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 300;

  /**
   * Wait time before positioning at min after finding the min distance.
   */
  public static final int WAIT_TIME = 7000;

  /**
   * Sleep time of 3 seconds to pause before functionality.
   */
  public static final int PAUSE_TIME = 3000;

  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double TILE_SIZE = 30.48;
  
  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static double LOC_BACKUP_DISTANCE = TILE_SIZE / 2;

  /**
   * The tile size in centimeters. Note that 30.48 cm = 1 ft.
   */
  public static final double BOARD_SIZE = 4 * 30.48;

  /**
   * Sleep time for display.
   */
  public static final long SLEEP_TIME = 200;
  
  /**
   * This will determine number of readings the colour detector should get.
   */
  public static final int NUM_READINGS = 10;

  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.133;

  /**
   * The robot width in centimeters. 11.1155
   */
  public static final double BASE_WIDTH = 12.2;
  
  /**
   * Rotate value in degrees to make robot center of rotation on y axis, 
   * knowing our left or right sensor is touching a line and current angle is 0.
   */
  public static final int ONE_LIGHT_ROTATION = (int) (BASE_WIDTH * 90 / WHEEL_RAD);

  /**
   * The limit of invalid samples that we read from the US sensor before assuming no obstacle.
   */
  public static final int INVALID_SAMPLE_LIMIT = 20;

  /**
   * This constant will be used to determine if the light is on top of a black line.
   */
  public static final int BLACK_LINE_THRESHOLD = 40;
  
  /**
   * This constant will be used to determine if the light is on top of a blue line.
   */
  public static final int BLUE_LINE_THRESHOLD = 25;
  
  /**
   * This constant will be used to determine if the light is on top of a blue line.
   */
  public static final int LINE_THRESHOLD = BLUE_LINE_THRESHOLD;

  /**
   * This constant will be used to determine if the robot should slow down if it sees a ring.
   */
  public static final int RING_THRESHOLD = 9;
  
  /**
   * This constant will be used to determine when the robot should lower the 
   * front light sensor to detect the ring colour.
   */
  public static final int RING_CLOSE = 4;
  
  /**
   * This constant will be used to set the speed at which the robot 
   * approaches the ring when it is close. 
   * Measured in degree's per second.
   */
  public static final int APPROACHING_SPEED = 100;
  
  /**
   * The colour updating/sampling period, 3ms, used in colour detector.
   */
  public static final long COLOUR_PERIOD = 300;
  
  /**
   * The light localizer colour updating/sampling period, 3ms, used in light localizer.
   */
  public static final long LIGHTLOCALIZER_PERIOD = 100;
  
  /**
   * The display period.
   */
  public static final long DISPLAY_PERIOD = 30;

  /**
   * The ultrasonic sensor.
   */
  public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S4);

  /**
   * The left light sensor. Used to detect lines and localize.
   */
  public static final EV3ColorSensor LEFT_COL_SENSOR = new EV3ColorSensor(SensorPort.S2);

  /**
   * The right light sensor. Used to detect lines and localize.
   */
  public static final EV3ColorSensor RIGHT_COL_SENSOR = new EV3ColorSensor(SensorPort.S3);

  /**
   * The front light sensor. Used to detect colours.
   */
  public static final EV3ColorSensor FRONT_COL_SENSOR = new EV3ColorSensor(SensorPort.S1);

  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.D);

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.A);

  /**
   * The top motor which is used to lower the colour sensor to 1 cm above the ring.
   */
  public static final EV3LargeRegulatedMotor topMotor = new EV3LargeRegulatedMotor(MotorPort.B);

  /**
   * The colorDetector. 
   */
  public static ColourDetector colorDetector = new ColourDetector();

  /**
   * The light localizer.
   */
  public static LightLocalizer lightLocalizer = new LightLocalizer();

  /**
   * The LCD.
   */
  public static final TextLCD TEXT_LCD = LocalEV3.get().getTextLCD();

  /**
   * The Ultrasonic Localizer.
   */
  public static UltrasonicLocalizer usLocalizer = UltrasonicLocalizer.getUltrasonicLocalizer();


  /**
   * The Odometer.
   */
  public static Odometer odo = Odometer.getOdometer();


}
