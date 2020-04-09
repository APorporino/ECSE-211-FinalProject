package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Main.sleepFor;
import static ca.mcgill.ecse211.project.Resources.DISPLAY_PERIOD;
import static ca.mcgill.ecse211.project.Resources.TEXT_LCD;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

/**
 * This class is used to display content to the LCD screen.
 */
public class Display implements Runnable {

  /**
   * This thread will continuously display any information needed for debugging.
   */
  public void run() {
    long updateStart;
    long updateDuration;
    while (true) { // operates continuously
      updateStart = System.currentTimeMillis();
      TEXT_LCD.clear();

      
      TEXT_LCD.drawString("DEBUGGING_INFO",0,0);

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
