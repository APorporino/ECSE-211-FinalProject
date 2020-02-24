package ca.mcgill.ecse211.project;

/**
 * This class will be used to store all variables related to colour detection.
 * @author Team06
 */
public class ColourResources {
  
  /**
   * Blue mean RGB values.
   */
  public static final double[] BLUE_MEAN = 
      {0.01725490279495716,0.09313725680112839,0.05137254968285561};
  
  /**
   * Blue standard deviation RGB values.
   */
  public static final double[] BLUE_SD = {0.000556516, 0.000506271,0.000473574};
  
  /**
   * Green mean RGB values.
   */
  public static final double[] GREEN_MEAN =
      {0.036666668206453326,0.09568627625703811,0.00862745139747858};
  
  /**
   * Green standard deviation RGB values.
   */
  public static final double[] GREEN_SD = {0.000661716,0.000620057,0.000556516};
  
  /**
   * Yellow mean RGB values.
   */
  public static final double[] YELLOW_MEAN =
      {0.08931372761726379,0.06960784643888474,0.00970588270574808};
  
  /**
   * Yellow standard deviation RGB values.
   */
  public static final double[] YELLOW_SD = {0.000556517, 0.000473573, 0.000661714};
  
  /**
   * Threshold distance of normalized sample from the mean to know the colour is yellow.
   */
  public static final double YELLOW_THRESH = 0.89;
  
  /**
   * Orange mean RGB values.
   */
  public static final double[] ORANGE_MEAN = 
      {0.06843137592077256, 0.027254902198910712, 0.003921568859368563};
  
  /**
   * Orange standard deviation RGB values.
   */
  public static final double[] ORANGE_SD = {0.000620055, 0.000473574, 0.000516712};
}
