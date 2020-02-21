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


  public  COLOUR updateRingColour(double colourRed, double colourGreen, double colourBlue) {
    this.ringColour = COLOUR.NONE;
    
    //double[] normalizedColours = normalize(colourRed,colourGreen, colourBlue);

    if ((colourBlue >= B_MEAN_BLUE - 100*B_SD_BLUE) & (colourBlue <= B_MEAN_BLUE + 100*B_SD_BLUE)) {
      System.out.println("b1\n");
      if ((colourRed >= B_MEAN_RED - 100*B_SD_RED) & (colourRed <= B_MEAN_RED + 100*B_SD_RED)) {
        System.out.println("b2\n");
        if ((colourGreen >= B_MEAN_GREEN - 100*B_SD_GREEN) & (colourGreen <= B_MEAN_GREEN + 100*B_SD_GREEN)) {
          System.out.println("b3\n");
          this.ringColour = COLOUR.BLUE;
          return this.ringColour;
        }
      }
    } 
    if ((colourBlue >= G_MEAN_BLUE - 100*G_SD_BLUE) & (colourBlue <= G_MEAN_BLUE + 100*G_SD_BLUE)) {
      System.out.println("g1\n");
      if ((colourRed >= G_MEAN_RED - 100*G_SD_RED) & (colourRed <= G_MEAN_RED + 100*G_SD_RED)) {
        System.out.println("g2\n");
        if ((colourGreen >= G_MEAN_GREEN - 100*G_SD_GREEN) & (colourGreen <= G_MEAN_GREEN + 100*G_SD_GREEN)) {
          System.out.println("g3\n");
          this.ringColour = COLOUR.GREEN;
          return this.ringColour;
        }
      }
    }  

    if ((colourBlue >= Y_MEAN_BLUE - 100*Y_SD_BLUE) & (colourBlue <= Y_MEAN_BLUE + 100*Y_SD_BLUE)) {
      System.out.println("y1\n");
      if ((colourRed >= Y_MEAN_RED - 100*Y_SD_RED) & (colourRed <= Y_MEAN_RED + 100*Y_SD_RED)) {
        System.out.println("y2\n");
        if ((colourGreen >= Y_MEAN_GREEN - 100*Y_SD_GREEN) & (colourGreen <= Y_MEAN_GREEN + 100*Y_SD_GREEN)) {
          System.out.println("y3\n");
          this.ringColour = COLOUR.YELLOW;
          return this.ringColour;
        }
      }

    }  
    if ((colourBlue >= O_MEAN_BLUE - 100*O_SD_BLUE) & (colourBlue <= O_MEAN_BLUE + 100*O_SD_BLUE)) {
      System.out.println("o1\n");
      if ((colourRed >= O_MEAN_RED - 100*O_SD_RED) & (colourRed <= O_MEAN_RED + 100*O_SD_RED)) {
        System.out.println("o2\n");
        if ((colourGreen >= O_MEAN_GREEN - 100*O_SD_GREEN) & (colourGreen <= O_MEAN_GREEN + 100*O_SD_GREEN)) {
          System.out.println("o3\n");
          this.ringColour = COLOUR.ORANGE;
          return this.ringColour;
        }
      }
    }

    return this.ringColour;

  }
  
//  /**
//   * This method will return normailzed values of a sample RGB reading.
//   */
//  public static double[3] normailze(double colourRed, double colourGreen, double colourBlue) {
//    
//  }


}
