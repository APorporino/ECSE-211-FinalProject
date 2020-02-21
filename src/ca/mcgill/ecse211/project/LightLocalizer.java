package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;

import lejos.robotics.SampleProvider;

/**
 * This class will be used to read data from the left and right light sensors.
 * @author Team06
 *
 */
public class LightLocalizer {

  private static final long COLOUR_PERIOD = 100;

  public  float colourIDLeft;
  public  float colourIDRight;
  static SampleProvider colourValueLeft = LEFT_COL_SENSOR.getMode("Red"); 
  static SampleProvider colourValueRight = RIGHT_COL_SENSOR.getMode("Red"); 
  static float[] colourDataLeft = new float[colourValueLeft.sampleSize()];
  static float[] colourDataRight = new float[colourValueRight.sampleSize()];


  /**
   * This method will read values from the two light sensors and store them in instance variables.
   */
  public void readLightData() {
    (new Thread() {
      public void run() {

        long updateStart;
        long updateDuration;
        updateStart = System.currentTimeMillis();
        while (true) {
          LEFT_COL_SENSOR.getMode("Red").fetchSample(colourDataLeft, 0);
          colourIDLeft = colourDataLeft[0] * 100;

          RIGHT_COL_SENSOR.getMode("Red").fetchSample(colourDataRight, 0);
          colourIDRight = colourDataRight[0] * 100;

          // this ensures that the light sensor runs once every 3 ms.
          updateDuration = System.currentTimeMillis() - updateStart;
          if (updateDuration < COLOUR_PERIOD) {
            sleepFor(COLOUR_PERIOD - updateDuration);
          }

        }
      }
    }).start();
  }
  
}