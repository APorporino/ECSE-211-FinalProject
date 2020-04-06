package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.receiveWifiParameters;

/**
 * This class will be used to receive and store the game time parameters in the resources class.
 * This class is independent since it will start a new thread and Resources should not implement Runnable.
 * @author Team06
 */
public class WiFi implements Runnable{

  @Override
  public void run() {
    receiveWifiParameters();
  }

  public WiFi(){
    
  }

}
