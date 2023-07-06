package data;

import UI_operations.SensorForm;

/**
 * Create alarm messages
 */

public class AlarmNotificator implements Observer{
    double maxValue;
    double minValue;
    Sensor sensor;
    private SensorForm sensorForm;

    public AlarmNotificator(Sensor sensor, double maxValue, double minValue,SensorForm sensorForm) {
        this.sensor=sensor;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.sensorForm=sensorForm;
    }

    @Override
    public void update(SensorData sensorData) {
        if(sensor.getType().equals("ph")){
            if(sensorData.getPh()>maxValue){
                System.out.println("High value alarm triggered!");
                String message="Alarm triggered: detected value "+sensorData.getPh();
                sensorForm.updateAlarms(message);

            }else if(sensorData.getPh()<minValue){
                System.out.println("Low value alarm triggered!");
                String message="Alarm triggered: detected value "+sensorData.getPh();
                sensorForm.updateAlarms(message);
            }
        }else{
            if(sensor.getType().equals("Temperature")){
                if(sensorData.getTemperature()>maxValue){
                    System.out.println("High value alarm triggered!");
                    String message="Alarm triggered: detected value "+sensorData.getTemperature();
                    sensorForm.updateAlarms(message);

                }else if(sensorData.getTemperature()<minValue){
                    System.out.println("Low value alarm triggered!");
                    String message="Alarm triggered: detected value "+sensorData.getTemperature();
                    sensorForm.updateAlarms(message);
                }
            }else{
                if(sensorData.getChlorine()>maxValue){
                    System.out.println("High value alarm triggered!");
                    String message="Alarm triggered: detected value "+sensorData.getChlorine();
                    sensorForm.updateAlarms(message);

                }else if(sensorData.getChlorine()<minValue){
                    System.out.println("Low value alarm triggered!");
                    String message="Alarm triggered: detected value "+sensorData.getChlorine();
                    sensorForm.updateAlarms(message);
                }
            }
        }

    }
}
