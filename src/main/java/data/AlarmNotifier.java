package data;

import UI_operations.SensorForm;

/**
 * Create alarm messages
 */

public class AlarmNotifier implements Observer {
    private final double maxValue;
    private final double minValue;
    private final Sensor sensor;
    private final SensorForm sensorForm;

    public AlarmNotifier(Sensor sensor, double maxValue, double minValue, SensorForm sensorForm) {
        this.sensor = sensor;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.sensorForm = sensorForm;
    }

    @Override
    public void update(SensorData sensorData) {
        if (sensor.getType().equals("ph")) {
            if (sensorData.getPh() > maxValue) {
                String message = "Alarm triggered: detected value " + sensorData.getPh();
                sensorForm.updateAlarms(message);
            } else if (sensorData.getPh() < minValue) {
                String message = "Alarm triggered: detected value " + sensorData.getPh();
                sensorForm.updateAlarms(message);
            }
        } else {
            if (sensor.getType().equals("Temperature")) {
                if (sensorData.getTemperature() > maxValue) {
                    String message = "Alarm triggered: detected value " + sensorData.getTemperature();
                    sensorForm.updateAlarms(message);
                } else if (sensorData.getTemperature() < minValue) {
                    String message = "Alarm triggered: detected value " + sensorData.getTemperature();
                    sensorForm.updateAlarms(message);
                }
            } else {
                if (sensorData.getChlorine() > maxValue) {
                    String message = "Alarm triggered: detected value " + sensorData.getChlorine();
                    sensorForm.updateAlarms(message);
                } else if (sensorData.getChlorine() < minValue) {
                    String message = "Alarm triggered: detected value " + sensorData.getChlorine();
                    sensorForm.updateAlarms(message);
                }
            }
        }
    }
}
