package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.*;

import java.text.DecimalFormat;

/**
 * This class is used to display content to the LCD screen.
 */
public class Display implements Runnable {

  private static final long DISPLAY_PERIOD = 30;

  /**
   * This thread will continuously display the robots reading of the USsensor and its min position 
   * and the left and right light sensor readings.
   */
  public void run() {
    long updateStart;
    long updateDuration;
    while (true) { // operates continuously
      updateStart = System.currentTimeMillis();
      TEXT_LCD.clear();

      // print last US reading
      TEXT_LCD.drawString("LEFT ID: " + LightLocalizer.colourIDLeft,0,0);
      TEXT_LCD.drawString("RIGHT ID: " + LightLocalizer.colourIDRight,0,2);
      TEXT_LCD.drawString("US Distance: " + ultrasonicLocalizer.readUsDistance(), 0, 5);
      TEXT_LCD.drawString("Min Distance: " + ultrasonicLocalizer.minDistance, 0, 6);
      //Main.sleepFor(SLEEP_TIME);
      updateDuration = System.currentTimeMillis() - updateStart;
      if (updateDuration < DISPLAY_PERIOD) {
        sleepFor(DISPLAY_PERIOD - updateDuration);
      }
    }
  }

  /**
   * Shows the text on the LCD, line by line.
   * 
   * @param strings comma-separated list of strings, one per line
   */
  public static void showText(String... strings) {
    TEXT_LCD.clear();
    for (int i = 0; i < strings.length; i++) {
      TEXT_LCD.drawString(strings[i], 0, i);
    }
  }

}
