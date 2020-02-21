package ca.mcgill.ecse211.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import lejos.hardware.sensor.SensorMode;

public class ColourCalibration implements Runnable{

  public void run() {
    SensorMode sensorMode = Resources.FRONT_COL_SENSOR.getRGBMode();
    float[] sample = new float[sensorMode.sampleSize()];

    String path = "/Users/gohar/git/lab5-ecse211_t06/result.csv";

    for(int i=0; i<10;i++) {
      sensorMode.fetchSample(sample, 0);
      FileWriter fileWriter = null;
      try {
        //BufferedWriter fileWriter = new BufferedWriter( new FileWriter( new File("result.csv")));
        fileWriter = new FileWriter (path,true);  
        //fileWriter.append("Red , Green , Blue");
        fileWriter.append("\n");
        fileWriter.append(sample[0]+","+sample[1]+","+sample[2]);
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          fileWriter.flush();
          fileWriter.close();
        } catch (Exception e) {
          e.printStackTrace();
        }  
      }

    }
    Main.sleepFor(100);
  }
}
