package ca.mcgill.ecse211.project;

import static ca.mcgill.ecse211.project.Resources.*;
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
               
                ultrasonicLocalizer.localizeToPoint(map[lastElement][0], map[lastElement][1]);      //localize at the last point
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
            
            moveStraightFor(angleAndDistance[0]);
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
          double angleToTurnBy;
          double base = x*TILE_SIZE - positions[0];   
          double height = y*TILE_SIZE - positions[1];
          double hypotenus = Math.pow((Math.pow(base, 2) + Math.pow(height, 2)), .5);
          returnValue[0] = hypotenus;
          
          //if its traveling in a straight line
          if (base >= -STRAIGHT_LINE && base <= STRAIGHT_LINE) {
                if (height > 0) {
                  angleToTurnBy =0;
                }else {
                  angleToTurnBy = FULL_SPIN_DEG / 2;
                }
          } else if (height >= -STRAIGHT_LINE && height <= STRAIGHT_LINE) {
                if (base > 0) {
                  angleToTurnBy = FULL_SPIN_DEG / 4;
                }
                else {
                  angleToTurnBy = FULL_SPIN_DEG * 3/4;
                }
          }//not straight line
          else{
                    angleToTurnBy = Math.atan(Math.abs(height/base));
                    angleToTurnBy = Math.abs(Math.toDegrees(angleToTurnBy));
                    
                    //logic to figure out which angle we want to turn by
                    if (height < 0 && base < 0) {
                      //Quadrant 3
                      angleToTurnBy = FULL_SPIN_DEG * 3/4 - angleToTurnBy;
                      
                    }
                    else if (height < 0) {
                      //Quadrant 4
                      angleToTurnBy = FULL_SPIN_DEG / 4 + angleToTurnBy;
                      
                    }else if (base < 0) {
                      //Quadrant 2
                      angleToTurnBy = FULL_SPIN_DEG * 3/4 + angleToTurnBy;
                      
                    }else {
                      //Quadrant 1
                      angleToTurnBy = FULL_SPIN_DEG / 4 - angleToTurnBy;
                    }
          }
          returnValue[1] = angleToTurnBy;
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
                 turnBy(difference);
               }else {
                 difference = difference - FULL_SPIN_DEG;
                 turnBy(difference);
               }
             }else {
              turnBy(difference);
             }
        
      }
      
      /**
       * Turns the robot by a specified angle. Note that this method is different from
       * {@code Navigation.turnTo()}. For example, if the robot is facing 90 degrees, calling
       * {@code turnBy(90)} will make the robot turn to 180 degrees, but calling
       * {@code Navigation.turnTo(90)} should do nothing (since the robot is already at 90 degrees).
       * 
       * @param angle the angle by which to turn, in degrees
       */
      public static void turnBy(double angle) {
        leftMotor.rotate(convertAngle(angle), true);
        rightMotor.rotate(-convertAngle(angle), false);
      }
     
      /**
       * Moves the robot straight for the given distance.
       * 
       * @param distance in feet (tile sizes), may be negative
       */
      public static void moveStraightFor(double distance) {
        leftMotor.rotate(convertDistance(distance), true);
        rightMotor.rotate(convertDistance(distance), false);
      }
      
      /**
       * Converts input distance to the total rotation of each wheel needed to cover that distance.
       * 
       * @param distance the input distance
       * @return the wheel rotations necessary to cover the distance
       */
      public static int convertDistance(double distance) {
        return (int) ((FULL_SPIN_DEG / 2 * distance) / (Math.PI * WHEEL_RAD));
      }
      
      /**
       * Converts input angle to the total rotation of each wheel needed to rotate the robot by that
       * angle.
       * 
       * @param angle the input angle
       * @return the wheel rotations necessary to rotate the robot by the angle
       */
      public static int convertAngle(double angle) {
        return convertDistance(Math.PI * BASE_WIDTH * angle / FULL_SPIN_DEG);
      }
      
      /**
       * Stops both motors.
       */
      public static void stopMotors() {
        leftMotor.stop();
        rightMotor.stop();
      }
      
      /**
       * Sets the speed of both motors to the same values.
       * 
       * @param speed the speed in degrees per second
       */
      public static void setSpeed(int speed) {
        setSpeeds(speed, speed);
      }
      
      /**
       * Sets the speed of both motors to different values.
       * 
       * @param leftSpeed the speed of the left motor in degrees per second
       * @param rightSpeed the speed of the right motor in degrees per second
       */
      public static void setSpeeds(int leftSpeed, int rightSpeed) {
        leftMotor.setSpeed(leftSpeed);
        rightMotor.setSpeed(rightSpeed);
      }
      
      /**
       * Sets the acceleration of both motors.
       * 
       * @param acceleration the acceleration in degrees per second squared
       */
      public static void setAcceleration(int acceleration) {
        leftMotor.setAcceleration(acceleration);
        rightMotor.setAcceleration(acceleration);
      }
      
      
}
