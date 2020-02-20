package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import static ca.mcgill.ecse211.project.Main.sleepFor;


public class Navigation {
  
      /**
       * This method starts a thread that loops through all grid positions in the map and goes to each one.
       */
      public static void drive(final int[][] map) {
        (new Thread() {
          public void run() {
                for (int[] elem: map) {
                  travelTo(elem[0], elem[1]);
                  
                  sleepFor(PAUSE_TIME);
                }
                int lastElement = map.length -1;
               
                //ultrasonicLocalizer.localizeToPoint(map[lastElement][0], map[lastElement][1]);      //localize at the last point
          }
        }).start();
      }
      
      /**
       * This method will make the robot travel to a specified x and y grid position.
       * @param x
       * @param y
       */
      public static void travelTo(int x, int y) {
              
            //Distance from center of robot to position (1,1)
            double[] angleAndDistance = getAngleAndDistance(x, y);
            
            turnTo(angleAndDistance[1]);
            
            Driver.setSpeeds(ROTATION_SPEED, ROTATION_SPEED);
            Thread ringThread = new Thread(RING_ODO);
            ringThread.start();
            leftMotor.forward();
            rightMotor.forward();
            boolean farFromRing = true;
            
            while (farFromRing) {
               if (ultrasonicLocalizer.currentDistance <= RING_THRESHOLD) {
                   Driver.stopMotorsInstantaneously();
                   farFromRing = false;
                   Sound.twoBeeps();
                   Button.waitForAnyPress();
               }
            }
            Driver.moveStraightFor(angleAndDistance[0] - RING_ODO.getXyt()[1] );
            ringThread.interrupt();
      }
      
      /**
       * This method will return the distance and angle needed to move to grid position (x y).
       * @param x Starting x distance from wall
       * @param y Starting y distance from wall
       * @return Array of doubles, first element distance to position, second is angle needed to turn.
       */
     public static double[] getAngleAndDistance(int x, int y) {
          double[] positions = odo.getXyt();
          double[] returnValue = new double[2];
          double angleToTurnTo;
          double base = x*TILE_SIZE - positions[0];   
          double height = y*TILE_SIZE - positions[1];
          double hypotenus = Math.pow((Math.pow(base, 2) + Math.pow(height, 2)), .5);
          returnValue[0] = hypotenus;
          
          //if its traveling in a straight line
          if (base >= -STRAIGHT_LINE && base <= STRAIGHT_LINE) {
                if (height > 0) {
                  angleToTurnTo =0;
                }else {
                  angleToTurnTo = FULL_SPIN_DEG / 2;
                }
          } else if (height >= -STRAIGHT_LINE && height <= STRAIGHT_LINE) {
                if (base > 0) {
                  angleToTurnTo = FULL_SPIN_DEG / 4;
                }
                else {
                  angleToTurnTo = FULL_SPIN_DEG * 3/4;
                }
          }//not straight line
          else{
            angleToTurnTo = Math.atan(Math.abs(height/base));
            angleToTurnTo = Math.abs(Math.toDegrees(angleToTurnTo));
                    
                    //logic to figure out which angle we want to turn by
                    if (height < 0 && base < 0) {
                      //Quadrant 3
                      angleToTurnTo = FULL_SPIN_DEG * 3/4 - angleToTurnTo;
                      
                    }
                    else if (height < 0) {
                      //Quadrant 4
                      angleToTurnTo = FULL_SPIN_DEG / 4 + angleToTurnTo;
                      
                    }else if (base < 0) {
                      //Quadrant 2
                      angleToTurnTo = FULL_SPIN_DEG * 3/4 + angleToTurnTo;
                      
                    }else {
                      //Quadrant 1
                      angleToTurnTo = FULL_SPIN_DEG / 4 - angleToTurnTo;
                    }
          }
          returnValue[1] = angleToTurnTo;
          return returnValue;
      }
      
      /**
       * This method will turn the robot to a specified degree based of current angle it is at.
       * @param theta the angle to end up at
       */
      public static void turnTo(double theta) {
              double currentAngle = odo.getXyt()[2];
              double difference = theta - currentAngle;
              
             //If turning angle greater than 180 turn the other way
             if (Math.abs(difference) > FULL_SPIN_DEG / 2) {
               if (difference < 0) {
                 difference = FULL_SPIN_DEG  + difference;
                 Driver.turnBy(difference);
               }else {
                 difference = difference - FULL_SPIN_DEG;
                 Driver.turnBy(difference);
               }
             }else {
                 Driver.turnBy(difference);
             }
        
      }
      
}
