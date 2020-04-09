package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.topMotor;

/**
 * This class will control the mechanism used to attach to the stranded vehicle.
 * 
 * The basic idea is to lower the hook with the motor after localizing the cart to be rescued.
 * With the cart attached with the hook, the cart will be lifted up for further actions.
 * 
 * PLEASE CONSULT THE SOFTWARE DOCUMENT FOR MORE DETAILED INFO
 * 
 * @author team 06
 *
 */
public class Tow {
  
  /**
   * This method will attach our robot to the stranded vehicle.
   */
  public static void attachToVehicle() {
    /*
     * Basic idea is to use the top motor to drop the hook down.
     * Once attached, bring the hook back up
     */
    topMotor.rotate(110);
    
    //New design team must see what other actions are required here
    
    topMotor.rotate(-110);
   
  }

}
