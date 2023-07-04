package file;

import data.Sensor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Operate on directory and files
 */

public class SensorFile {
    public void createDirectory(Sensor sensor){
        File sensorFolder=new File("sensors");
        if(!sensorFolder.exists()){
            sensorFolder.mkdir();
            System.out.println("folder is created");
        }else{
            System.out.println("folder already exists");
        }

        File sensorSubdirectory = new File("sensors/"+sensor.getIpAddress());
        if(!sensorSubdirectory.exists()){
            sensorSubdirectory.mkdir();
            System.out.println("Subdirectory is created inside 'sensors'");
        } else {
            System.out.println("Subdirectory already exists inside 'sensors'");
        }
    }
    public void saveToFile(Sensor sensor, StringBuilder stringBuilder) {
        createDirectory(sensor);

        String sensorDirectoryPath = "sensors/" + sensor.getIpAddress();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "data_" + timeStamp + ".csv";
        String filePath = sensorDirectoryPath + "/" + fileName;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(stringBuilder.toString());
            System.out.println("File saved successfully");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error saving file");
        }
    }
}
