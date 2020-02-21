package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.ColourResources.*;
import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;
import lejos.robotics.SampleProvider;

/**
 * This class will be used to read values from the front light sensor.
 * It will also determine the colour of a ring from a given reading.
 * @author Team06
 *
 */
public class ColourDetecter implements Runnable{


  private static final long COLOUR_PERIOD = 300;
  private long timeout = Long.MAX_VALUE;

  //local variables for 
  public  float colourRed;
  public  float colourGreen;
  public  float colourBlue;
  //for some reason this sampleProvider does not work.
  //private static SampleProvider colourValue = FRONT_COL_SENSOR.getRGBMode(); 
  /**
   * Buffer (array) to store light samples.
   */
  static float[] colourData = new float[3];

  /**
   * Constructor.
   */
  public ColourDetecter(){
  }


  public enum COLOUR {
    BLUE, GREEN, YELLOW, ORANGE, NONE;
  }

  public  COLOUR ringColour;

  /**
   * This method will localize the robot using light sensor, start the thread.
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
 * This method returns a COLOUR enumeration object given a sample reading.
 * @param colourRed Sample red RGB value
 * @param colourGreen Sample green RGB value
 * @param colourBlue Sample blue RGB value
 * @return COLOUR enumeration which corresponds to the colour detected.
 */
  public  COLOUR updateRingColour(double colourRed, double colourGreen, double colourBlue) {
    this.ringColour = COLOUR.NONE;
    double minDistance;
    double[] normalizedColours = normailze(colourRed,colourGreen, colourBlue);
    

    //blue
    double sumOfSquaresBlue = Math.pow(normalizedColours[0] - B_MEAN_RED, 2) + 
                                                    Math.pow(normalizedColours[1] - B_MEAN_GREEN, 2) + 
                                                    Math.pow(normalizedColours[2] - B_MEAN_BLUE, 2);
    double distanceFromBlue = Math.pow(sumOfSquaresBlue, .5);
    minDistance = distanceFromBlue;
    this.ringColour = COLOUR.BLUE;
    
    //green
    double sumOfSquaresGreen = Math.pow(normalizedColours[0] - G_MEAN_RED, 2) + 
                                                   Math.pow(normalizedColours[1] - G_MEAN_GREEN, 2) + 
                                                   Math.pow(normalizedColours[2] - G_MEAN_BLUE, 2);
    double distanceFromGreen = Math.pow(sumOfSquaresGreen, .5);
    if (distanceFromGreen < minDistance) {
      minDistance = distanceFromGreen;
      this.ringColour = COLOUR.GREEN;
    }
    //yellow
    double sumOfSquaresYellow = Math.pow(normalizedColours[0] - Y_MEAN_RED, 2) + 
                                                    Math.pow(normalizedColours[1] - Y_MEAN_GREEN, 2) + 
                                                    Math.pow(normalizedColours[2] - Y_MEAN_BLUE, 2);
    double distanceFromYellow = Math.pow(sumOfSquaresYellow, .5);
    if (distanceFromYellow < minDistance) {
      minDistance = distanceFromYellow;
      this.ringColour = COLOUR.YELLOW;
    }
    
    //orange
    double sumOfSquaresOrange = Math.pow(normalizedColours[0] - O_MEAN_RED, 2) + 
                                                    Math.pow(normalizedColours[1] - O_MEAN_GREEN, 2) + 
                                                    Math.pow(normalizedColours[2] - O_MEAN_BLUE, 2);
    double distanceFromOrange = Math.pow(sumOfSquaresOrange, .5);
    if (distanceFromOrange < minDistance) {
      minDistance = distanceFromOrange;
      this.ringColour = COLOUR.ORANGE;
    }
    System.out.println("Min distance: " +minDistance );
    return this.ringColour;

  }

  /**
   * This method will return normailzed values of a sample RGB reading.
   */
  public static double[] normailze(double colourRed, double colourGreen, double colourBlue) {
    double[] normalizedColours = new double[3];
    double sumOfSquares = Math.pow(colourRed, 2) + Math.pow(colourGreen, 2) + Math.pow(colourBlue, 2);
    double denominator = Math.pow(sumOfSquares, .5);

    normalizedColours[0] = colourRed / denominator;
    normalizedColours[1] = colourGreen / denominator;
    normalizedColours[2] = colourBlue / denominator;

    return normalizedColours;
  }


}
