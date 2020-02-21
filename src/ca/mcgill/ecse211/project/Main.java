package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;

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
    //--------------------------------------
    int buttonChoice;
    buttonChoice = chooseLocalize();
    if (buttonChoice == Button.ID_RIGHT ) {      //2nd part of demo
      //new Thread(new Display()).start();
      new Thread(ultrasonicLocalizer).start(); 
      //Assuming we start somewhere on 45 degree line in the first square
      localizeToStartingPosition();
      new Thread(odo).start();
      Navigation.drive(MAP0);
    }else if (buttonChoice == Button.ID_LEFT) {    //1st part of demo
      detectColours();
    }
    
    //hitting escape button will stop the program 
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing

    System.exit(0);

  }

  public static void detectColours() {
    while (true) {
      if (Button.waitForAnyPress() == Button.ID_ESCAPE) {
        break;
      }
      TEXT_LCD.drawString("Object Detected", 0,1);
      Button.waitForAnyPress();
      Thread colour = new Thread(colorDetector);
      colour.start();
      System.out.println("RED: " + colorDetector.colourRed + "\nGREEN" + colorDetector.colourGreen + "\nBLUE" + colorDetector.colourBlue);
      colorDetector.updateRingColour(colorDetector.colourRed, colorDetector.colourGreen, colorDetector.colourBlue);
      TEXT_LCD.clear();
      TEXT_LCD.drawString("COLOUR: " + colorDetector.ringColour, 0, 2);
      colour.interrupt();
    }
    System.exit(0);
  }

  /**
   * This method will localize the robot to position 1,1.
   */
  public static void localizeToStartingPosition() {
    int[] positions = new int[2];    //Array used to store the x and y position of the robot from the wall.

    positions = localizeToZeroDeg();  //Position of robot now stored

    lightLocalizer.readLightData();
    lineAdjustment();
    Driver.moveStraightFor(LIGHT_TO_CENTER);
    Driver.turnBy(90);
    lineAdjustment();
    Driver.moveStraightFor(LIGHT_TO_CENTER);
    Driver.turnBy(-90);


    Driver.moveStraightFor(BACKUP_DISTANCE);
    lineAdjustment();
    Driver.moveStraightFor(LIGHT_TO_CENTER);
  }

  /**
   * This method will position the robot at 0 degrees.
   * 
   * @return int[] Integer array containing the x and y position of the robot from the two walls.
   * 
   */
  public static int[] localizeToZeroDeg() {
    ultrasonicLocalizer.minDistance = 1500;
    int[] positions = new int[2];

    //make the robot rotate 360 degrees once. Thread
    Driver.rotate();
    //continuously locate the current position of robot and store minimum position. Thread 

    sleepFor(WAIT_TIME);    //Must sleep to give it time to do a full circle

    //Now it will position itself at the minimum distance
    Driver.rotate();
    while (true) {
      if (ultrasonicLocalizer.currentDistance <= ultrasonicLocalizer.minDistance) {
        Driver.stopMotorsInstantaneously();
        positions[0] = ultrasonicLocalizer.minDistance; //store minimum distance. Either x (height) or y (length)

        break;
      }
    }
    //We are now pointing at the closest wall.
    Driver.turnBy(FULL_SPIN_DEG / 4);
    if (ultrasonicLocalizer.currentDistance <= TILE_SIZE) {   //still facing a wall
      positions[1] = ultrasonicLocalizer.currentDistance;  //record distance x (length) from side wall
      Driver.turnBy(FULL_SPIN_DEG / 4);   //face the 0 degree direction
    }
    return positions;
  }

  /**
   * This method will move the robot to point 1, 1.
   * Assumptions are it is facing zero degrees somewhere in the first grid (0,0).
   */
  public static void lineAdjustment() {
    boolean leftLineNotDetected = true;
    boolean rightLineNotDetected = true;

    //Drive robot straight
    Driver.drive();
    while (leftLineNotDetected & rightLineNotDetected) {

      if (lightLocalizer.colourIDLeft < BLUE_LINE_THRESHOLD) {
        Driver.stopMotorsInstantaneously();
        leftLineNotDetected = false;
      }

      if (lightLocalizer.colourIDRight < BLUE_LINE_THRESHOLD) {
        Driver.stopMotorsInstantaneously();
        rightLineNotDetected = false;
      }
    }

    if (rightLineNotDetected) {
      Driver.setSpeeds(0, 20);
      rightMotor.forward();
      while (rightLineNotDetected) {
        if (lightLocalizer.colourIDRight < BLUE_LINE_THRESHOLD) {
          Driver.stopMotorsInstantaneously();
          rightLineNotDetected = false;
        }
      }
    }else {
      Driver.setSpeeds(20, 0);
      leftMotor.forward();
      while (leftLineNotDetected) {
        if (lightLocalizer.colourIDLeft < BLUE_LINE_THRESHOLD) {
          Driver.stopMotorsInstantaneously();
          leftLineNotDetected = false;
        }
      }
    }
    Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);

  }

  /**
   * This method will return the minimum distance to point (1,1) given starting positions. 
   * @param x Starting x distance from wall
   * @param y Starting y distance from wall
   * @return Minimum distance to point (1,1)
   */
  public static double calculateDistanceFromPosition(int x, int y) {
    double base = TILE_SIZE - x;   
    double height = TILE_SIZE - y;
    double hypotenus = Math.pow((Math.pow(base, 2) + Math.pow(height, 2)), .5);
    return hypotenus - SENSOR_TO_CENTER;
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
