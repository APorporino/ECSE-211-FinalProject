package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.BLUE_MEAN;
import static ca.mcgill.ecse211.project.Resources.COLOUR_PERIOD;
import static ca.mcgill.ecse211.project.Resources.FRONT_COL_SENSOR;
import static ca.mcgill.ecse211.project.Resources.GREEN_MEAN;
import static ca.mcgill.ecse211.project.Resources.NUM_READINGS;
import static ca.mcgill.ecse211.project.Resources.ORANGE_MEAN;
import static ca.mcgill.ecse211.project.Resources.YELLOW_MEAN;
import static ca.mcgill.ecse211.project.Resources.YELLOW_THRESH;

/**
 * This class will be used to control colour detection and determine the colour 
 * of a ring given a sample.
 * @author Team06
 */
public class ColourDetector implements Runnable {

  //local variables for 
  public  float colourRed;
  public  float colourGreen;
  public  float colourBlue;

  /**
   * Buffer (array) to store light samples.
   */
  static float[] colourData = new float[3];

  /**
   * Constructor.
   */
  public ColourDetector(){
  }

  /**
   * Enumeration of all possible colour detections.
   */
  public enum Colour {
    BLUE, GREEN, YELLOW, ORANGE, NONE;
  }

  /**
   * Variable used to determine the colour detected by the colour detector.
   */
  public  Colour objectColour;

  /**
   * This run method continuously receives data RGB values from the front
   *  light sensor and stores it in static variables.
   */
  public void run() {
    long updateStart;
    long updateDuration;
    updateStart = System.currentTimeMillis();
    while (true) {
      FRONT_COL_SENSOR.getRGBMode().fetchSample(colourData, 0);
      colourRed = colourData[0];
      colourGreen = colourData[1];
      colourBlue = colourData[2];
      // this ensures that the light sensor runs once every 3 ms.
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < COLOUR_PERIOD) {
        sleepFor(COLOUR_PERIOD - updateDuration);
      }
    }
  }

  /**
   * This method will receive a fixed number of readings (NUM_READINGS) from the 
   * RGB colour sensor and return an average for each.
   * @return double[3] containing averages for RGB readings.
   */
  public static double[] getReadings() {
    double[] averages = new double[3];

    double[] redValues = new double[10];
    double[] blueValues = new double[10];
    double[] greenValues = new double[10];

    for (int i = 0; i < NUM_READINGS; i++) {
      FRONT_COL_SENSOR.getRGBMode().fetchSample(colourData, 0);
      redValues[i] = colourData[0];
      blueValues[i] = colourData[1];
      greenValues[i] = colourData[2];
    }
    
    double redSum = 0;
    double blueSum = 0;
    double greenSum = 0;
    for (int i = 0; i < NUM_READINGS; i++) {
      redSum +=  redValues[i];
      blueSum += blueValues[i];
      greenSum += greenValues[i];
    }
    averages[0] = redSum / 10.0;
    averages[1] = blueSum / 10.0;
    averages[2] = greenSum / 10.0;

    return averages;

  }

  /**
   * This method will return normailzed values of a sample RGB reading.
   * 
   * @return double[] containing RGB data normalized values
   */
  public  double[] normailze(double colourRed, double colourGreen, double colourBlue) {
    double[] normalizedColours = new double[3];
    double sumOfSquares = Math.pow(colourRed, 2) + Math.pow(colourGreen, 2) 
        + Math.pow(colourBlue, 2);
    double denominator = Math.pow(sumOfSquares, .5);

    normalizedColours[0] = colourRed / denominator;
    normalizedColours[1] = colourGreen / denominator;
    normalizedColours[2] = colourBlue / denominator;

    return normalizedColours;
  }

  /**
   * This method returns the euclidean distance of 2 points of 3 dimensions.
   * @param x1 first x pos
   * @param y1 first y pos
   * @param x2 second x pos
   * @param y2 second y pos
   * @param x3 third x pos
   * @param y3 third y pos
   * @return Euclidean distance
   */
  public static double euclideanDistance(double x1, double y1,double x2, double y2,
      double x3, double y3) {
    double sumOfSquaresBlue = Math.pow(x1 - y1, 2)
          +  Math.pow(x2 - y2, 2) + Math.pow(x3 - y3, 2);
    return Math.pow(sumOfSquaresBlue, .5);
  }

  /**
   * This method will update the ring colour variable given a sample by first normalizing t
   * he sample and then comparing euclidean distances.
   * @param colourRed red colour variable
   * @param colourGreen green colour variable
   * @param colourBlue blue colour variable
   * @return COLOUR representing the colour detected.
   */
  public  Colour updateColour(double colourRed, double colourGreen, double colourBlue) {
    this.objectColour = Colour.NONE;
    double minDistance;
    double[] normalizedColours = normailze(colourRed,colourGreen, colourBlue);

    //blue
    double distanceFromBlue = euclideanDistance(normalizedColours[0], BLUE_MEAN[0],
        normalizedColours[1], BLUE_MEAN[1],normalizedColours[2], BLUE_MEAN[2]);

    minDistance = distanceFromBlue;
    this.objectColour = Colour.BLUE;

    //green
    double distanceFromGreen = euclideanDistance(normalizedColours[0], GREEN_MEAN[0],
        normalizedColours[1], GREEN_MEAN[1], normalizedColours[2], GREEN_MEAN[2]);

    if (distanceFromGreen < minDistance) {
      minDistance = distanceFromGreen;
      this.objectColour = Colour.GREEN;
    }

    //yellow
    double distanceFromYellow = euclideanDistance(normalizedColours[0], YELLOW_MEAN[0],
        normalizedColours[1], YELLOW_MEAN[1],  normalizedColours[2], YELLOW_MEAN[2]);

    if ((distanceFromYellow < minDistance) & (distanceFromYellow < YELLOW_THRESH)) {
      minDistance = distanceFromYellow;
      this.objectColour = Colour.YELLOW;
    }

    //orange
    double distanceFromOrange = euclideanDistance(normalizedColours[0], ORANGE_MEAN[0],
        normalizedColours[1], ORANGE_MEAN[1], normalizedColours[2], ORANGE_MEAN[2]);

    if (distanceFromOrange < minDistance) {
      minDistance = distanceFromOrange;
      this.objectColour = Colour.ORANGE;
    }
    return this.objectColour;
  }
}