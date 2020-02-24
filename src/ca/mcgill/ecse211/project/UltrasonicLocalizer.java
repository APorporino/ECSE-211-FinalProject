package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Main.sleepFor;

/**
 * This class will be used to localize the robot.
 * @author Team06
 *
 */
public class UltrasonicLocalizer implements Runnable{



  /**
   * The distance remembered by the {@code filter()} method.
   */
  public int currentDistance;

  /**
   * The minimum distance seen will be recorded.
   */
  public int minDistance = 1500;

  /**
   * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
   * array each time {@code readUsSample()} is called.
   */
  private float[] usData = new float[usSensor.sampleSize()];

  private static UltrasonicLocalizer ultraLoc; // Returned as singleton

  /**
   * Constructor.
   */
  private UltrasonicLocalizer(){
  }

  /**
   * This method will either return the single instance of the class
   * Or make a new instance.
   * @return Instance of UltrasonicLocalizer
   */
  public static synchronized UltrasonicLocalizer getUltrasonicLocalizer() {
    if (ultraLoc == null) {
      ultraLoc = new UltrasonicLocalizer();
    }
    return ultraLoc;
  }


  @Override
  /**
   * Will continuously take readings and keep track of the min distance found.
   */
  public void run() {

    while(true) {
      long updateStart;
      long updateDuration;
      updateStart = System.currentTimeMillis();

      currentDistance = readUsDistance();
      if (currentDistance < minDistance) {
        minDistance = currentDistance;
      }
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < US_PERIOD) {
        sleepFor(US_PERIOD - updateDuration);
      }

    }   
  }

  /**
   * Localizing method to get closer to a specified grid position.
   * 
   * @param x xPosition to localize to in grid coordinates
   * @param y yPosition to localize to in grid coordinates
   */
  public void localizeToPoint(int x, int y) {
    double currentAngle = odo.getXyt()[2];

    Navigation.turnTo(0);

    sleepFor(PAUSE_TIME);
    //get accurate x position
    Driver.turnBy(FULL_SPIN_DEG / 2);
    double yPos = getAccurateReading();
    //get accurate y position
    Driver.turnBy(FULL_SPIN_DEG / 4);
    double xPos = getAccurateReading();

    //reset odometer values to known x, y and theta values.
    odo.setX(xPos);
    odo.setY(yPos);
    odo.setTheta(FULL_SPIN_DEG * 3/4);

    sleepFor(PAUSE_TIME);
    
    Navigation.travelTo(x,y);

    sleepFor(PAUSE_TIME);
    Navigation.turnTo(currentAngle);

  }

  /**
   * This method will take 20 readings from ultrasonic sensor and return the average reading.
   */
  public double getAccurateReading() {
    double readings[] = new double[20];
    double sum = 0;
    double average;
    for (int i = 0; i < 20; i++) {

      readings[i] = readUsDistance();
      sum += readings[i];
    }
    average = sum / 20;

    return average + SENSOR_TO_CENTER;
  }
  
  /**
   * Returns the filtered distance between the US sensor and an obstacle in cm.
   * 
   * @return the filtered distance between the US sensor and an obstacle in cm
   */
  public int readUsDistance() {
    usSensor.fetchSample(usData, 0);
    // extract from buffer, convert to cm, cast to int, and filter
    return filter((int) (usData[0] * 100.0));
  }

  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * 
   * @param distance raw distance measured by the sensor in cm
   * @return the filtered distance in cm
   */
  int filter(int distance) {
    if(distance >= FILTER_MAX) {
      return FILTER_MAX;
    }
    return distance;
  }


}
