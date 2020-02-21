package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;

import java.text.DecimalFormat;

/**
 * This class is used to display the content of the odometer variables (x, y, theta).
 */
public class Display implements Runnable {

  private double[] position;
  private static final long DISPLAY_PERIOD = 30;
  private long timeout = Long.MAX_VALUE;

  /**
   * This thread will continuously display the robots reading of the USsensor and its min position.
   */
  public void run() {
    while (true) { // operates continuously
      TEXT_LCD.clear();
      //TEXT_LCD.drawString("Distance from wall... ", 0, 0);

      // print last US reading
            TEXT_LCD.drawString("LEFT ID: " + lightLocalizer.colourIDLeft,0,0);
            TEXT_LCD.drawString("RIGHT ID: " + lightLocalizer.colourIDRight,0,2);
      TEXT_LCD.drawString("US Distance: " + ultrasonicLocalizer.readUsDistance(), 0, 5);
      TEXT_LCD.drawString("Min Distance: " + ultrasonicLocalizer.minDistance, 0, 6);
      Main.sleepFor(SLEEP_TIME);
    }
  }

  /**
   * Sets the timeout in ms.
   * 
   * @param timeout the timeout in milliseconds
   */
  public void setTimeout(long timeout) {
    this.timeout = timeout;
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
