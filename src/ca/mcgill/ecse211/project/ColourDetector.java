package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.ColourResources.*;
import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;
import lejos.robotics.SampleProvider;

public class ColourDetector implements Runnable{


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
  public ColourDetector(){
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


  public  COLOUR updateRingColour(double colourRed, double colourGreen, double colourBlue) {
    this.ringColour = COLOUR.NONE;
    
    if ((colourBlue >= BLUE_MEAN[2] - 100*BLUE_SD[2]) & (colourBlue <= BLUE_MEAN[2] + 100*BLUE_SD[2])) {
      System.out.println("b1\n");
      if ((colourRed >= BLUE_MEAN[0] - 100*BLUE_SD[0]) & (colourRed <= BLUE_MEAN[0] + 100*BLUE_SD[0])) {
        System.out.println("b2\n");
        if ((colourGreen >= BLUE_MEAN[1] - 100*BLUE_SD[1]) & (colourGreen <= BLUE_MEAN[1] + 100*BLUE_SD[1])) {
          System.out.println("b3\n");
          this.ringColour = COLOUR.BLUE;
          return this.ringColour;
        }
      }
    } 
    if ((colourBlue >= GREEN_MEAN[2] - 100*GREEN_SD[2]) & (colourBlue <= GREEN_MEAN[2] + 100*GREEN_SD[2])) {
      System.out.println("g1\n");
      if ((colourRed >= GREEN_MEAN[0] - 100*GREEN_SD[0]) & (colourRed <= GREEN_MEAN[0] + 100*GREEN_SD[0])) {
        System.out.println("g2\n");
        if ((colourGreen >= GREEN_MEAN[1] - 100*GREEN_SD[1]) & (colourGreen <= GREEN_MEAN[1] + 100*GREEN_SD[1])) {
          System.out.println("g3\n");
          this.ringColour = COLOUR.GREEN;
          return this.ringColour;
        }
      }
    } 

    if ((colourBlue >= YELLOW_MEAN[2] - 100*YELLOW_SD[2]) & (colourBlue <= YELLOW_MEAN[2] + 100*YELLOW_SD[2])) {
      System.out.println("y1\n");
      if ((colourRed >= YELLOW_MEAN[0] - 100*YELLOW_SD[0]) & (colourRed <= YELLOW_MEAN[0] + 100*YELLOW_SD[0])) {
        System.out.println("y2\n");
        if ((colourGreen >= YELLOW_MEAN[1] - 100*YELLOW_SD[1]) & (colourGreen <= YELLOW_MEAN[1] + 100*YELLOW_SD[1])) {
          System.out.println("y3\n");
          this.ringColour = COLOUR.YELLOW;
          return this.ringColour;
        }
      }

    }  
    if ((colourBlue >= ORANGE_MEAN[2] - 100*ORANGE_SD[2]) & (colourBlue <= ORANGE_MEAN[2] + 100*ORANGE_SD[2])) {
      System.out.println("o1\n");
      if ((colourRed >= ORANGE_MEAN[0] - 100*ORANGE_SD[0]) & (colourRed <= ORANGE_MEAN[0] + 100*ORANGE_SD[0])) {
        System.out.println("o2\n");
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