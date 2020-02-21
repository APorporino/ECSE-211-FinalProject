package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import static ca.mcgill.ecse211.project.Driver.*;
import static ca.mcgill.ecse211.project.Main.*;

import lejos.robotics.SampleProvider;
import lejos.hardware.Sound;

public class LightLocalizer {
  
      private static final long COLOUR_PERIOD = 100;
  
      public static float colourIDLeft;
      public static float colourIDRight;
      public static float colourIDFront;
      static SampleProvider colourValueLeft = LEFT_COL_SENSOR.getMode("Red"); 
      static SampleProvider colourValueRight = RIGHT_COL_SENSOR.getMode("Red"); 
      static SampleProvider colourValueFront = FRONT_COL_SENSOR.getMode("Red"); 
      static float[] colourDataLeft = new float[colourValueLeft.sampleSize()];
      static float[] colourDataRight = new float[colourValueRight.sampleSize()];
      static float[] colourDataFront = new float[colourValueFront.sampleSize()];
    
      /**
       * Get the colorID from the left color sensor.
       * @return colorIDLeft  variable
       */
      public static float getColourIDLeft() {
        return colourIDLeft;
      }
      
      /**
       * Get the colorID from the right color sensor.
       * @return colorIDRight  variable
       */
      public static float getColourIDRight() {
        return colourIDRight;
      }
      
      /**
       * Get the colorID from the front color sensor.
       * @return colorIDFront  variable
       */
      public static float getColourIDFront() {
        return colourIDFront;
      }
    
      /**
       * This method will localize the robot using light sensor, start the thread.
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
              
              //Display.showText("Left: " + colourIDLeft, "Right: " + colourIDRight);
              // this ensures that the light sensor runs once every 3 ms.
              updateDuration = System.currentTimeMillis() - updateStart;
              if (updateDuration < COLOUR_PERIOD) {
                sleepFor(COLOUR_PERIOD - updateDuration);
              }
              
            }
          }
        }).start();
      }
      
      /**
       * This method will read data from the top light sensor
       */
      public static double readFrontLightData() {
        FRONT_COL_SENSOR.getMode("Red").fetchSample(colourDataFront, 0);
        colourIDFront = colourDataFront[0]*100;
        return colourIDFront;
      }
      
      /**
       * Localizing method using the light sensor to get closer to a specified grid position.
       * 
       * @param x xPosition to localize to in grid coordinates
       * @param y yPosition to localize to in grid coordinates
       */
      public static void lightLocToPoint(int x, int y) {
        //double currentAngle = odo.getXyt()[2];
        double lineAngles[] = new double[4];
        int counter = 0;
        
        topMotor.setSpeed(40);
        topMotor.rotate(110);
        
        //rotate 360 degrees to detect black lines
        rotate();
        
        //store thetas when black lines are read from light sensor
        while (counter != 4) {
          while (readFrontLightData() > BLACK_LINE_THRESHOLD) {
          }

          lineAngles[counter] = odo.getXyt()[2]; // Record the angle at which we detected the line.
          
          counter++;

          sleepThread(1f); // wait for a second to avoid multiple detections of the same line.
      }
        
        // calculate the x and y position relative to the grid point that we inputted
        double xPos = -SENSOR_TO_CENTER * Math.cos((lineAngles[2] - lineAngles[0]) / 2);
        double yPos = -SENSOR_TO_CENTER * Math.cos((lineAngles[3] - lineAngles[1]) / 2);
        double dTheta = -0.5 * Math.abs(lineAngles[1] - lineAngles[3]) + Math.PI - lineAngles[3];
        
        //odo.setTheta(odo.getXyt()[2] + dTheta);

        //correct the y position similar to when we localized to (1,1)
        if (yPos < 0) {
          Navigation.turnTo(0);
          Driver.moveStraightFor(-2*LIGHT_TO_CENTER);
          localizeToOneOne();
          Driver.moveStraightFor(LIGHT_TO_CENTER);
        } else if (yPos > 0) {
          Navigation.turnTo(180);
          Driver.moveStraightFor(-2*LIGHT_TO_CENTER);
          localizeToOneOne();
          Driver.moveStraightFor(LIGHT_TO_CENTER);
        }
        
        //correct the x position similar to when we localized to (1,1)
        if (xPos < 0) {
          Navigation.turnTo(90);
          Driver.moveStraightFor(-2*LIGHT_TO_CENTER);
          localizeToOneOne();
          Driver.moveStraightFor(LIGHT_TO_CENTER);
          odo.setTheta(90);
        } else if (xPos > 0) {
          Navigation.turnTo(270);
          Driver.moveStraightFor(-2*LIGHT_TO_CENTER);
          localizeToOneOne();
          Driver.moveStraightFor(LIGHT_TO_CENTER);
          odo.setTheta(270);
        }
        
        //correct odometer values
        odo.setX(x*TILE_SIZE);
        odo.setY(y*TILE_SIZE);
        
        topMotor.rotate(-110);
      }
    
      /**
       * This method move the robot to find the line.
       * The robot moves forward until it detect a line, then it turns to another direction and repeat.
       */
//      public static void movement() {
//        Driver.setSpeed(FORWARD_SPEED);
//        (new Thread() {
//          public void run() {
//            while (true) {
//              while ((getcolorID() < colorID_constant)) {
//                leftMotor.forward();
//                rightMotor.forward();
//              }
//              Driver.turnBy(90.0);
//    
//              while ((getcolorID() < colorID_constant)) {
//                leftMotor.forward();
//                rightMotor.forward();
//              }
//              // corrections for light sensor to make sure it orientated itself perfectly
//              leftMotor.rotate(Driver.convertDistance(3), true);
//              rightMotor.rotate(Driver.convertDistance(3), false);
//              Driver.turnBy(-88);
//              leftMotor.rotate(Driver.convertDistance(8), true);
//              rightMotor.rotate(Driver.convertDistance(8), false);
//              break;
//            }
//          }
//        }).start();
//      }
      
      private static void sleepThread(float seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (Exception e) {
        }
    }
}