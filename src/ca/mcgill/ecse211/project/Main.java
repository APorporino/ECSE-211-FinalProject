package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.BACKUP_DISTANCE;
import static ca.mcgill.ecse211.project.Resources.FULL_SPIN_DEG;
import static ca.mcgill.ecse211.project.Resources.LIGHT_TO_CENTER;
import static ca.mcgill.ecse211.project.Resources.TEXT_LCD;
import static ca.mcgill.ecse211.project.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.project.Resources.WAIT_TIME;
import static ca.mcgill.ecse211.project.Resources.leftMotor;
import static ca.mcgill.ecse211.project.Resources.lightLocalizer;
import static ca.mcgill.ecse211.project.Resources.navigation;
import static ca.mcgill.ecse211.project.Resources.odo;
import static ca.mcgill.ecse211.project.Resources.rightMotor;
import static ca.mcgill.ecse211.project.Resources.usLocalizer;

import lejos.hardware.Button;

/**
 * The main driver class for the lab.
 */
public class Main {

  /**
   * The main entry point.
   * 
   * @param args not used
   */
  public static void main(String[] args) {
    
    navigation = new Navigation(odo, leftMotor, rightMotor);

    int buttonChoice;
    buttonChoice = chooseLocalize();
    if (buttonChoice == Button.ID_RIGHT) {
      //Assuming we start somewhere on 45 degree line in the first square
      TEXT_LCD.clear();
      TEXT_LCD.drawString("Localizing to 1,1", 0, 1);
      localizeToStartingPosition();
      new Thread(odo).start();
      new Thread(navigation).start();
    } else if (buttonChoice == Button.ID_LEFT) { 
      detectColours();
    }
    
    //hitting escape button will stop the program 
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing

    System.exit(0);
  }

  /**
   * This method will localize the robot to position 1,1.
   */
  public static void localizeToStartingPosition() {
    
    //new Ultrasonic thread
    Thread usThread = new Thread(usLocalizer);
    usThread.start();
    localizeToZeroDeg();  
    usThread.interrupt();
    sleepFor(500);
    //new Light thread (left and right light sensors)
    Thread lightLocThread = new Thread(lightLocalizer);
    lightLocThread.start();
    LightLocalizer.lineAdjustment();
    Navigation.moveStraightFor(LIGHT_TO_CENTER);
    Navigation.turnBy(FULL_SPIN_DEG / 4);
    LightLocalizer.lineAdjustment();
    Navigation.moveStraightFor(LIGHT_TO_CENTER);
    Navigation.turnBy(-FULL_SPIN_DEG / 4);

    Navigation.moveStraightFor(BACKUP_DISTANCE);
    LightLocalizer.lineAdjustment();
    Navigation.moveStraightFor(LIGHT_TO_CENTER);
    lightLocThread.interrupt();
  }

  /**
   * This method will position the robot at 0 degrees.
   * 
   * @return 
   */
  public static void localizeToZeroDeg() {
    usLocalizer.minDistance = 1500;

    //make the robot rotate 360 degrees once. Thread
    Navigation.rotate().start();
    //continuously locate the current position of robot and store minimum position.
    sleepFor(WAIT_TIME);    //Must sleep to give it time to do a full circle

    //Now it will position itself at the minimum distance
    Thread rotateThread  = Navigation.rotate();
    rotateThread.start();
    while (true) {
      if (usLocalizer.currentDistance <= usLocalizer.minDistance) {
        rotateThread.interrupt();
        break;
      }
    }
    //We are now pointing at the closest wall.
    Navigation.turnBy(FULL_SPIN_DEG / 4);
    // still facing the wall
    if (usLocalizer.currentDistance <= TILE_SIZE) { 
      Navigation.turnBy(FULL_SPIN_DEG / 4);   //face the 0 degree direction
    }
  }

  /**
   * This method will present the user with the option to localize the robot.
   * @return
   */
  private static int chooseLocalize() {
    int buttonChoice;
    Display.showText("< Left | Right >",
        "       |        ",
        " Detect | Run through  ",
        "Colours | map");

    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }
  
  /**
   * This method will present the user with the option to detect colours.
   * @return
   */
  public static void detectColours() {
    while (true) {
      TEXT_LCD.clear();
      Display.showText("Hit any |  Hit >",
          "    button  |   back   ",
          " to detect | button to ",
          "colour. | leave.");
      if (Button.waitForAnyPress() == Button.ID_ESCAPE) {
        break;
      }
      TEXT_LCD.clear();
      Navigation.detectRing();
    }
    System.exit(0);
  }

  /**
   * This method will make the robot sleep for a certain time.
   * @param duration How in milliseconds it should sleep for
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
}
