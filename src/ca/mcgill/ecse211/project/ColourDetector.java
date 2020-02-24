package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.ColourResources.*;
import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;
import lejos.robotics.SampleProvider;

/**
 * This class will be used to control colour detection and determine the colour 
 * of a ring given a sample.
 * @author Team06
 *
 */
public class ColourDetector implements Runnable{


  private static final long COLOUR_PERIOD = 300;

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
  public ColourDetector(){
  }

  /**
   * Enumeration of all possible colour detections.
   */
  public enum COLOUR {
    BLUE, GREEN, YELLOW, ORANGE, NONE;
  }

  /**
   * Variable used to determine the colour detected by the colour detector.
   */
  public  COLOUR ringColour;

  /**
   * This run method continuously receives data RGB values from the front light sensor and stores it in static variables.
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
   * This method will receive a fixed number of readings (NUM_READINGS) from the RGB colour sensor and return an average for each.
   * @return double[3] containing averages for RGB readings.
   */
  public static double[] getReadings(){
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
    
    averages[0] = redSum/10.0;
    averages[1] = blueSum/10.0;
    averages[2] = greenSum/10.0;
    
    
    return averages;

  }

/**
 * This unique method is used to guess the colour of the ring given the sample RGB values.
 * The method will check if each RGB sample is within 100 standard deviations of the mean.
 * This seems way too high however since the standard deviation is extremely low to actually achieve 
 * it for all three RGB sample is difficult unless it is the correct colour.
 * Method has been tested and works consistently.
 * 
 * @param colourRed Red colour sample
 * @param colourGreen Green colour sample
 * @param colourBlue Blue colour sample
 * @return COLOUR representing the colour detected
 */
  public  COLOUR updateRingColour(double colourRed, double colourGreen, double colourBlue) {
    this.ringColour = COLOUR.NONE;
    
    if ((colourBlue >= BLUE_MEAN[2] - 100*BLUE_SD[2]) & (colourBlue <= BLUE_MEAN[2] + 100*BLUE_SD[2])) {
      System.out.println("b1\n");
      if ( this.ringColour == COLOUR.NONE) {
        this.ringColour = COLOUR.BLUE;
      }
      if ((colourRed >= BLUE_MEAN[0] - 100*BLUE_SD[0]) & (colourRed <= BLUE_MEAN[0] + 100*BLUE_SD[0])) {
        System.out.println("b2\n");
        this.ringColour = COLOUR.BLUE;
        if ((colourGreen >= BLUE_MEAN[1] - 100*BLUE_SD[1]) & (colourGreen <= BLUE_MEAN[1] + 100*BLUE_SD[1])) {
          System.out.println("b3\n");
          this.ringColour = COLOUR.BLUE;
          return this.ringColour;
        }
      }
    } 
    if ((colourBlue >= GREEN_MEAN[2] - 100*GREEN_SD[2]) & (colourBlue <= GREEN_MEAN[2] + 100*GREEN_SD[2])) {
      System.out.println("g1\n");
      if ( this.ringColour == COLOUR.NONE) {
        this.ringColour = COLOUR.GREEN;
      }
      if ((colourRed >= GREEN_MEAN[0] - 100*GREEN_SD[0]) & (colourRed <= GREEN_MEAN[0] + 100*GREEN_SD[0])) {
        System.out.println("g2\n");
        this.ringColour = COLOUR.GREEN;
        if ((colourGreen >= GREEN_MEAN[1] - 100*GREEN_SD[1]) & (colourGreen <= GREEN_MEAN[1] + 100*GREEN_SD[1])) {
          System.out.println("g3\n");
          this.ringColour = COLOUR.GREEN;
          return this.ringColour;
        }
      }
    } 

    if ((colourBlue >= YELLOW_MEAN[2] - 100*YELLOW_SD[2]) & (colourBlue <= YELLOW_MEAN[2] + 100*YELLOW_SD[2])) {
      System.out.println("y1\n");
      if ( this.ringColour == COLOUR.NONE) {
        this.ringColour = COLOUR.YELLOW;
      }
      if ((colourRed >= YELLOW_MEAN[0] - 100*YELLOW_SD[0]) & (colourRed <= YELLOW_MEAN[0] + 100*YELLOW_SD[0])) {
        System.out.println("y2\n");
        this.ringColour = COLOUR.YELLOW;
        if ((colourGreen >= YELLOW_MEAN[1] - 100*YELLOW_SD[1]) & (colourGreen <= YELLOW_MEAN[1] + 100*YELLOW_SD[1])) {
          System.out.println("y3\n");
          this.ringColour = COLOUR.YELLOW;
          return this.ringColour;
        }
      }

    }  
    if ((colourBlue >= ORANGE_MEAN[2] - 100*ORANGE_SD[2]) & (colourBlue <= ORANGE_MEAN[2] + 100*ORANGE_SD[2])) {
      System.out.println("o1\n");
      if ( this.ringColour == COLOUR.NONE) {
        this.ringColour = COLOUR.ORANGE;
      }
      if ((colourRed >= ORANGE_MEAN[0] - 100*ORANGE_SD[0]) & (colourRed <= ORANGE_MEAN[0] + 100*ORANGE_SD[0])) {
        System.out.println("o2\n");
        this.ringColour = COLOUR.ORANGE;
        if ((colourGreen >= ORANGE_MEAN[1] - 100*ORANGE_SD[1]) & (colourGreen <= ORANGE_MEAN[1] + 100*ORANGE_SD[1])) {
          System.out.println("o3\n");
          this.ringColour = COLOUR.ORANGE;
          return this.ringColour;
        }
      }
    }

    return this.ringColour;

  }


}