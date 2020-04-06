package ca.mcgill.ecse211.project;

import ca.mcgill.ecse211.playingfield.Point;
import ca.mcgill.ecse211.playingfield.Region;
import ca.mcgill.ecse211.wificlient.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Integrate this carefully with your existing Resources class (See below for where to add your
 * code from your current Resources file). The order in which things are declared matters!
 * 
 * <p>When you're done, replace this javadoc comment with the one you have from your Resources
 * class.
 * 
 * @author Younes Boubekeur
 */
public class Resources {
  
  // Set these as appropriate for your team and current situation
  /**
   * The default server IP used by the profs and TA's.
   */
  public static final String DEFAULT_SERVER_IP = "192.168.2.3";
  
  /**
   * The IP address of the server that transmits data to the robot. For the beta demo and
   * competition, replace this line with
   * 
   * <p>{@code public static final String SERVER_IP = DEFAULT_SERVER_IP;}
   */
  public static final String SERVER_IP = "192.168.2.3"; // ours
  
  /**
   * Your team number.
   */
  public static final int TEAM_NUMBER = 6;
  
  /** 
   * Enables printing of debug info from the WiFi class. 
   */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
  
  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   */
  public static final boolean RECEIVE_WIFI_PARAMS = true;
  
  
  // ----------------------------- PREVIOUS RESOURCES HERE -----------------------------
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
   * Minimum value to reset the light localizer left and right data.
   */
  public static final long MIN_LIGHT_DATA = 60;

  /**
   * The speed at which the robot will rotate in degrees per second.
   */
  public static final int ROTATION_SPEED = 200;
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 120;

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
   * Sleep time for display.
   */
  public static final long SLEEP_TIME = 200;

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
  public static final int OBJECT_THRESHOLD = 9;
  
  /**
   * This constant will be used to determine when the robot should lower the 
   * front light sensor to detect the ring colour.
   */
  public static final int OBJECT_CLOSE = 4;
  
  /**
   * Ideal distance between the sensor and the wall (cm).
   */
  public static final int WALL_DIST = 35;
  
  /**
   * Width of the maximum tolerated deviation from the ideal {@code WALL_DIST}, also known as the
   * dead band. This is measured in cm.
   */
  public static final int WALL_DIST_ERR_THRESH = 3;
  
  /**
   * This variable is used to determine if the robot is way too close to a wall in the bang bang controller method.
   */
  public static final int WALL_TOO_CLOSE = 15;
  
  /**
   * Speed of slower rotating wheel (deg/sec).
   */
  public static final int MOTOR_LOW = 100;
  
  /**
   * Speed of the faster rotating wheel (deg/sec).
   */
  public static final int MOTOR_HIGH = 300;
  
  /**
   * Difference of motor speed constant for bang band controller
   */
  public static final int MOTOR_DIFF = 100;
  
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
   * Period for ultrasonic reading.
   */
  public static final long US_PERIOD = 300;
  

  /**
   * The odometer update period in ms.
   */
  public static final long ODOMETER_PERIOD = 25;

  /**
   * The value used as max reading of US sensor.
   */
  public static final int FILTER_MAX = 255;

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
   * The top motor which is used to lower the hook.
   */
  public static final EV3LargeRegulatedMotor topMotor = new EV3LargeRegulatedMotor(MotorPort.B);

  /**
   * The LCD.
   */
  public static final TextLCD TEXT_LCD = LocalEV3.get().getTextLCD();
  
  /**
   * The colorDetector. 
   */
  public static ColourDetector colorDetector = new ColourDetector();

  /**
   * The light localizer.
   */
  public static LightLocalizer lightLocalizer = new LightLocalizer();

  /**
   * The Ultrasonic Localizer.
   */
  public static UltrasonicLocalizer usLocalizer = UltrasonicLocalizer.getUltrasonicLocalizer();

  /**
   * The Odometer.
   */
  public static Odometer odo = Odometer.getOdometer();
  
  /**
   * The navigation.
   */
  public static Navigation navigation;
  
  /**
   * The distance of the tunnel in cm
   */
  public static final double TUNNEL_DISTANCE = 3*TILE_SIZE;
  
  // VARIABLES RELATED TO COLOUR DETECTION
  /**
   * Blue mean RGB values.
   */
  public static final double[] BLUE_MEAN = 
      {0.01725490279495716,0.09313725680112839,0.05137254968285561};
  
  /**
   * Blue standard deviation RGB values.
   */
  public static final double[] BLUE_SD = {0.000556516, 0.000506271,0.000473574};
  
  /**
   * Green mean RGB values.
   */
  public static final double[] GREEN_MEAN =
      {0.036666668206453326,0.09568627625703811,0.00862745139747858};
  
  /**
   * Green standard deviation RGB values.
   */
  public static final double[] GREEN_SD = {0.000661716,0.000620057,0.000556516};
  
  /**
   * Yellow mean RGB values.
   */
  public static final double[] YELLOW_MEAN =
      {0.08931372761726379,0.06960784643888474,0.00970588270574808};
  
  /**
   * Yellow standard deviation RGB values.
   */
  public static final double[] YELLOW_SD = {0.000556517, 0.000473573, 0.000661714};
  
  /**
   * Threshold distance of normalized sample from the mean to know the colour is yellow.
   */
  public static final double YELLOW_THRESH = 0.89;
  
  /**
   * Orange mean RGB values.
   */
  public static final double[] ORANGE_MEAN = 
      {0.06843137592077256, 0.027254902198910712, 0.003921568859368563};
  
  /**
   * Orange standard deviation RGB values.
   */
  public static final double[] ORANGE_SD = {0.000620055, 0.000473574, 0.000516712};

  
  //------------------------------END OF PREVIOUS RESOURCES
  
  //VARIABLES RELATED TO WIFI COORDINATES
  /**
   * Container for the Wi-Fi parameters.
   */
  public static Map<String, Object> wifiParameters;
 
  // This static initializer MUST be declared before any Wi-Fi parameters.
  static {
    receiveWifiParameters();
  }
  
  /** Red team number. */
  public static int redTeam = getWP("RedTeam");

  /** Red team's starting corner. */
  public static int redCorner = getWP("RedCorner");

  /** Green team number. */
  public static int greenTeam = getWP("GreenTeam");

  /** Green team's starting corner. */
  public static int greenCorner = getWP("GreenCorner");

  /** The Red Zone. */
  public static Region red = makeRegion("Red");

  /** The Green Zone. */
  public static Region green = makeRegion("Green");

  /** The Island. */
  public static Region island = makeRegion("Island");

  /** The red tunnel footprint. */
  public static Region tnr = makeRegion("TNR");

  /** The green tunnel footprint. */
  public static Region tng = makeRegion("TNG");

  /** The red search zone. */
  public static Region szr = makeRegion("SZR");

  /** The green search zone. */
  public static Region szg = makeRegion("SZG");
  
  
  //These COLOR_INDEPENDENT variable will be filled by either the green or red values depending on which we are.
  /** Team's starting corner. */
  public static int corner ;
  /** The Zone. */
  public static Region zone;
  /** The tunnel footprint. */
  public static Region tn;
  /** search zone */
  public static Region sz;
  
  
  /**
   * This method will fill the COLOR_INDEPENDENT fields with 
   * the appropriate values assuming we are the red team.
   */
  public static void weAreRed() {
    corner = redCorner;
    zone = red;
    tn = tnr;
    sz = szr;
  }
  
  /**
   * This method will fill the COLOR_INDEPENDENT fields with 
   * the appropriate values assuming we are the green team.
   */
  public static void weAreGreen() {
    corner = greenCorner;
    zone = green;
    tn = tng;
    sz = szg;
  }
  
  /**
   * Receives Wi-Fi parameters from the server program.
   */
  public static void receiveWifiParameters() {
    // Only initialize the parameters if needed
    if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
      return;
    }
    System.out.println("Waiting to receive Wi-Fi parameters.");

    // Connect to server and get the data, catching any errors that might occur
    try (WifiConnection conn =
        new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
      /*
       * getData() will connect to the server and wait until the user/TA presses the "Start" button
       * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
       * pressing the back/escape button on the EV3. getData() will throw exceptions if something
       * goes wrong.
       */
      wifiParameters = conn.getData();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
  
  /**
   * Returns the Wi-Fi parameter int value associated with the given key.
   * 
   * @param key the Wi-Fi parameter key
   * @return the Wi-Fi parameter int value associated with the given key
   */
  public static int getWP(String key) {
    if (wifiParameters != null) {
      return ((BigDecimal) wifiParameters.get(key)).intValue();
    } else {
      return 0;
    }
  }
  
  /** 
   * Makes a point given a Wi-Fi parameter prefix.
   */
  public static Point makePoint(String paramPrefix) {
    return new Point(getWP(paramPrefix + "_x"), getWP(paramPrefix + "_y"));
  }
  
  /**
   * Makes a region given a Wi-Fi parameter prefix.
   */
  public static Region makeRegion(String paramPrefix) {
    return new Region(makePoint(paramPrefix + "_LL"), makePoint(paramPrefix + "_UR"));
  }
  
}
