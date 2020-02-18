package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import java.text.DecimalFormat;

public class LightLocalizer implements Runnable{
      
  
        public static boolean goingStraight = true;
        
        private static final long COLOUR_PERIOD = 300;
        private long timeout = Long.MAX_VALUE;
        long updateStart;
        long updateEnd;
      
        long startTime = System.currentTimeMillis();
                
  
        public static int currentReading;
        /**
         * Buffer (array) to store US samples. Declared as an instance variable to avoid creating a new
         * array each time {@code readUsSample()} is called.
         */
        private float[] lightData = new float[COLOUR_SENSOR.sampleSize()];
        
        //private SampleProvider lightValue = LIGHT_SENSOR.getRedMode();         // provides samples from this instance
        
        
        
        private static LightLocalizer lightLoc; // Returned as singleton
        
        /**
         * Constructor.
         */
        private LightLocalizer(){
        }
        
        /**
         * This method will either return the single instance of the class
         * Or make a new instance.
         * @return Instance of UltrasonicLocalizer
         */
        public static synchronized LightLocalizer getLightLocalizer() {
          if (lightLoc == null) {
            lightLoc = new LightLocalizer();
          }
          return lightLoc;
        }
        
        
        
        @Override
        public void run() {
          if (goingStraight) {
               while (currentReading < 755) {
                  Navigation.turnBy(turnBySmallAmount());
                }
          }
          
        }
        
        public void reposition() {
            int count = 1;
            while ((currentReading < 755)&&count<20) {
              currentReading = readLightData();
              Navigation.turnBy(turnBySmallAmount());
              count++;
            }
        }
        
        /**
         * 
         */
        public static double turnBySmallAmount() {
            int tmp = (int) ( Math.random() * 2 + 1); // will return either 1 or 2
            if (tmp == 1) {
              return 2;
            }else {
              return -2;
            }
        }
        
        
        
        
        /**
         * Returns the filtered distance between the US sensor and an obstacle in cm.
         * 
         * @return the filtered distance between the US sensor and an obstacle in cm
         */
        public int readLightData() {
          COLOUR_SENSOR.fetchSample(lightData, 0);
          // extract from buffer, convert to cm, cast to int, and filter
          return (int) (lightData[0] * 100.0);
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

        public static void setGoingStraight(boolean b) {
              goingStraight = b;
          
        }
}
