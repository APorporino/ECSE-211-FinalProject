package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.project.Resources.colorID_constant;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.lightSensor;
import static ca.mcgill.ecse211.project.Resources.rightMotor;

import lejos.robotics.SampleProvider;

/**
 * This class handles the localization using the light sensor.
 * Code from lab 3 - Localization, LightLocalizer.java class.
 * 
 * @author Menglin He
 * @author Gohar Saqib Fazal
 *
 */
public class LightLocalizer {
  private static int colorID;
  static SampleProvider colorValue = lightSensor.getMode("Red"); 
  static float[] colorData = new float[colorValue.sampleSize()];

  /**
   * Get the colorID from the color sensor.
   * @return
   */
  public static int getcolorID() {
    return colorID;
  }

  /**
   * This method will localize the robot using light sensor, start the thread.
   */
  public static void lightlocalize() {
    (new Thread() {
      public void run() {
        while (true) {
          lightSensor.fetchSample(colorData, 0);
          colorID = (int) colorData[0];
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            // do  nothing
          }
        }
      }
    }).start();
  }

  /**
   * This method move the robot to find the line.
   * The robot moves forward until it detect a line, then it turns to another direction and repeat.
   */
  public static void movement() {
    Driver.setSpeed(FORWARD_SPEED);
    (new Thread() {
      public void run() {
        while (true) {
          while ((getcolorID() < colorID_constant)) {
            leftMotor.forward();
            rightMotor.forward();
          }
          Driver.turnBy(90.0);

          while ((getcolorID() < colorID_constant)) {
            leftMotor.forward();
            rightMotor.forward();
          }
          // corrections for light sensor to make sure it orientated itself perfectly
          leftMotor.rotate(Driver.convertDistance(3), true);
          rightMotor.rotate(Driver.convertDistance(3), false);
          Driver.turnBy(-88);
          leftMotor.rotate(Driver.convertDistance(8), true);
          rightMotor.rotate(Driver.convertDistance(8), false);
          break;
        }
      }
    }).start();
  }
}