package ca.mcgill.ecse211.project;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.project.Resources.*;
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
  //Assuming we start somewhere on 45 degree line in the first square
  //--------------------------------------
    
    localizeToStartingPosition();       //Get to point (1,1)
    
   //--------------------------------------
    
    int buttonChoice = chooseMap();

    new Thread(odo).start();
    new Thread(new Display()).start();
    if (buttonChoice == Button.ID_LEFT) {
      Navigation.drive(MAP1);            //call Navigation.drive to run through specific map
    } else if (buttonChoice == Button.ID_RIGHT) {
      Navigation.drive(MAP2);           
    }
    
    //--------------------------------------
    
    //hitting escape button will stop the program 
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing
    
    System.exit(0);

  }
  
  /**
   * This method will localize the robot to position 1,1.
   */
  public static void localizeToStartingPosition() {
        int[] positions = new int[2];    //Array used to store the x and y position of the robot from the wall.
        int buttonChoice;
        
        //--------------------------------------
        buttonChoice = chooseLocalize();
        if (buttonChoice == Button.ID_LEFT | buttonChoice == Button.ID_RIGHT) {
          positions = localizeToZeroDeg();  //Position of robot now stored
         
        }

        //Distance from center of robot to position (1,1)
        double distanceFromPoint = calculateDistanceFromPosition(positions[0], positions[1]);
        
        Button.waitForAnyPress();   //wait for press so TA has time to measure the angle
        
        //Move to position (1,1)
        Navigation.turnBy(FULL_SPIN_DEG / 8);
        Navigation.moveStraightFor(distanceFromPoint);
        Navigation.turnBy(-(FULL_SPIN_DEG / 8));
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
    SpinDriver.rotate();
    //continuously locate the current position of robot and store minimum position. Thread 
    new Thread(ultrasonicLocalizer).start(); 
    
    sleepFor(WAIT_TIME);    //Must sleep to give it time to do a full circle
    
    //Now it will position itself at the minimum distance
      SpinDriver.rotate();
      while (true) {
        if (ultrasonicLocalizer.currentDistance <= ultrasonicLocalizer.minDistance) {
          SpinDriver.stopMotorsInstantaneously();
          positions[0] = ultrasonicLocalizer.minDistance; //store minimum distance. Either x (height) or y (length)
          break;
        }
    }
      
     //We are now pointing at the closest wall.
     SpinDriver.turnBy(FULL_SPIN_DEG / 4);
     if (ultrasonicLocalizer.currentDistance <= TILE_SIZE) {   //still facing a wall
       positions[1] = ultrasonicLocalizer.currentDistance;  //record distance x (length) from side wall
       SpinDriver.turnBy(FULL_SPIN_DEG / 4);   //face the 0 degree direction
     }else {    //facing 0 degree direction
       SpinDriver.turnBy(FULL_SPIN_DEG / 2);  
       positions[1] = ultrasonicLocalizer.currentDistance;  //record distance y (height) from bottom wall
       SpinDriver.turnBy(FULL_SPIN_DEG / 2);  //face 0 degrees again
     }
     return positions;
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
   * This method will position the robot at 0 degrees without recording any positions.
   */
  public static void goToZero() {
    ultrasonicLocalizer.minDistance = 1500;         //reset the min value
   
    SpinDriver.rotate();   //make the robot rotate 360 degrees once. 
    // it will continuously locate the current position of robot and store minimum position. 
 
    sleepFor(WAIT_TIME);    //Must sleep to give it time to do a full circle
    
      SpinDriver.rotate();
      while (true) {
        if (ultrasonicLocalizer.currentDistance <= ultrasonicLocalizer.minDistance) {
          SpinDriver.stopMotorsInstantaneously();
          break;
        }
      }
      
      //now facing minimum distance to a wall
      SpinDriver.turnBy(FULL_SPIN_DEG / 4);
      if (ultrasonicLocalizer.currentDistance <= BOARD_SIZE) {   //still facing a wall
        SpinDriver.turnBy(FULL_SPIN_DEG / 4);   //face the 0 degree direction
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
                     " Localize | Localize  ",
                     "the robot | the robot ",
                     "To position (1,1)");
    
    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }
  
  /**
   * This method will present the user with the option to localize the robot.
   * @return
   */
  private static int chooseMap() {
    int buttonChoice;
    Display.showText("< Left | Right >",
                     "       |        ",
                     " Map 1 | Map 2  ");
    
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
