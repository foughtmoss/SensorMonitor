package data;

import com.google.gson.Gson;
import java.util.List;

/**
 * Interpret data
 */
public class JSONDataInterpreter implements DataInterpreter{
    private List<Observer> observers;

    public JSONDataInterpreter(List<Observer> observers) {
        this.observers = observers;
    }

    @Override
    public void interpretData(String data) {

        Gson gson = new Gson();
        SensorData sensorData = gson.fromJson(data, SensorData.class);

        for (Observer observer : this.observers) {
            observer.update(sensorData);
        }
    }
}
