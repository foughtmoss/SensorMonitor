package data;

/**
 * Interpret data
 */

public class SensorData {

    double ph;
    double temperature;
    double chlorine;

    public double getPh() {
        return ph;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getChlorine() {
        return chlorine;
    }

    @Override
    public String toString() {
        return "SensorData{" + "ph=" + ph + ", temperature=" + temperature + ", chlorine=" + chlorine + '}';
    }
}
