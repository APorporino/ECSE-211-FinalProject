package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;
import lejos.robotics.SampleProvider;

public class ColourDetecter {
      
  
        private static final long COLOUR_PERIOD = 300;
        private long timeout = Long.MAX_VALUE;
        
        //local variables for 
        private static float colourRed;
        private static float colourGreen;
        private static float colourBlue;
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
        
        /**
         * Get the colorID from the color sensor.
         * @return
         */
        public static float getcolourRed() {
          return colourRed;
        }
        /**
         * Get the colorID from the color sensor.
         * @return
         */
        public static float getcolourGreen() {
          return colourGreen;
        }
        /**
         * Get the colorID from the color sensor.
         * @return
         */
        public static float getcolourBlue() {
          return colourBlue;
        }
        
        /**
         * This method will localize the robot using light sensor, start the thread.
         */
        public void readColourData() {
          (new Thread() {
              public void run() {
                  long updateStart;
                  long updateDuration;
                  updateStart = System.currentTimeMillis();
                  while (true) {
                      FRONT_COL_SENSOR.getRGBMode().fetchSample(colourData, 0);
                      colourRed = colourData[0];
                      colourGreen = colourData[1];
                      colourBlue = colourData[2];
                      //Display.showText("Red: " + colourRed, "Green: " + colourGreen, "Blue: " + colourBlue);
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
