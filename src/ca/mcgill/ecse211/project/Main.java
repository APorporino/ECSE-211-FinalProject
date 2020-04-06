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
import lejos.hardware.Sound;

/**
 * The main driver class for the lab.
 */
public class Main {

  /**
   * The main entry point. 
   * Running main will make the robot reach across the bridge, ready searching and scanning.
   * IMPORTANT: THIS CODE HAS NOT BEEN RUN ON THE ROBOT AND MUST BE REVIEWED.
   * 
   * @param args not used
   */
  public static void main(String[] args) {

    //First step is to get WIFI parameters. 
    //This will start a thread to receive the wifi paramters
    Thread wifiThread = new Thread(new WiFi());
    wifiThread.start();

    //Concurrently with receiving the paramters we will localize to not waste any time
    //Assuming we start somewhere on 45 degree line in the first square
    TEXT_LCD.clear();
    TEXT_LCD.drawString("Localizing to 1,1", 0, 1);
    localizeToStartingPosition();
    new Thread(odo).start();
    
    //beep 3 times to signal we are done localizing
    Sound.beep();
    Sound.twoBeeps();
    
    //Here we must wait until the wifi thread is finished and we have received all paramters
    while (Resources.wifiParameters == null) {
    }// do nothing
    //Now the variables in resources are populated
    
    
    //fill the COLOUR_INDEPENDENT fields with appropriate data
    if (Resources.getWP("RedTeam") == 6) {
      Resources.weAreRed();
    }else {
      Resources.weAreGreen();
    }
    
    switch(Resources.corner) {
      case(1):
        //bottom right corner
        odo.setXyt(1, 1, 90);
      case(2):
        //bottom left corner
        odo.setXyt(14, 1, 270);
      case(3):
        //top left corner
        odo.setXyt(1, 8, 90);
    }

    //Now we can begin travelling to the entrance of bridge. We have to be careful not to hit the bridge
    Navigation.travelTo(Resources.tn.ll.x - 1, Resources.tn.ll.y -1);

    //localize before crossing bridge
    LightLocalizer.localizeToPoint((int) Resources.tn.ll.x - 1, (int) Resources.tn.ll.y -1);

    //we must move to be precisely at the entrance of the tunnel 
    if (Resources.tn.ll.x != Resources.tn.ur.x) { //if tunnel is horizontal
      Navigation.moveStraightFor(1.5*TILE_SIZE);
      Navigation.turnBy(90);
    }else { //tunnel is vertical
      Navigation.moveStraightFor(.5*TILE_SIZE);
    }

    Navigation.moveStraightFor(Resources.TUNNEL_DISTANCE);
    
    //beep 3 times to signal we have reached the island
    Sound.beep();
    Sound.twoBeeps();


    //Move to search zone while avoiding obstacles  
    //Using travelTo method to navigate to this point will incorporate obstacle avoidance
    while (notInSearchZone()) {
      Navigation.travelTo(Resources.zone.ll.x, Resources.zone.ll.y);
    }
    

    //HERE IS WHERE WE WILL START SEARCHING AND SCANNING
    //NEW DESIGN TEAM SHOULD CONSULT THE SOFTWARE DOCUMENT FOR A BASIC IDEA

    //hitting escape button will stop the program 
    while (Button.waitForAnyPress() != Button.ID_ESCAPE) {
    } // do nothing

    System.exit(0);
  }

  /**
   * This method returns true if the robot is not in the search zone and false if it is.
   * @return
   */
  private static boolean notInSearchZone() {
    if (odo.getXyt()[0] <=  Resources.sz.ur.x && odo.getXyt()[0] > Resources.sz.ll.x) {
      if (odo.getXyt()[1] <=  Resources.sz.ur.y && odo.getXyt()[1] > Resources.sz.ll.y) {
        return false;
      }
    }
    return true;
  }

  /**
   * This method will localize the robot to position 1,1.
   */
  public static void localizeToStartingPosition() {

    //new Ultrasonic thread to localize robot to zero degrees
    Thread usThread = new Thread(usLocalizer);
    usThread.start();
    localizeToZeroDeg();  
    usThread.interrupt();
    sleepFor(500);

    //new Light thread (left and right light sensors) to position robot at (1,1)
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
