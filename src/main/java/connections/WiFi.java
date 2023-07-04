package connections;

import data.DataInterpreter;
import data.JSONDataInterpreter;
import data.Observer;
import data.Sensor;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Establish a Wi-Fi connection and operate on it
 */
public class WiFi implements MyConnection,Runnable {

    private Socket socket;
    private Sensor sensor;
    private boolean isConnected=false;
    private List<Observer> observers = new ArrayList<>();

    public WiFi(Sensor sensor) {
        this.sensor=sensor;
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
            isConnected=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data;

            while (isConnected && (data = bufferedReader.readLine()) != null) {
                DataInterpreter dataInterpreter = new JSONDataInterpreter(observers);
                dataInterpreter.interpretData(data);
            }

        } catch (IOException e) {
            if (!socket.isClosed()) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void connect() {
        try {
            socket=new Socket(sensor.getIpAddress(), sensor.getPort());
            isConnected=true;
            Thread t = new Thread(this);
            t.start();

        } catch (IOException e) {
            String errorMessage = "An error occurred while trying to establish a connection: " + e.getMessage()+ ". Try to reconnect the sensor to the network";
            JOptionPane.showMessageDialog(null, errorMessage, "Connection Error", JOptionPane.ERROR_MESSAGE);
            isConnected = false;
        }
    }

    @Override
    public void write() {

    }
    @Override
    public void run() {
        read();
    }

    public void addObserver(Observer channel) {
        this.observers.add(channel);
    }
    public void removeObserver(Observer channel){ this.observers.remove(channel);}
    public boolean isConnected() {
        return isConnected;
    }
}
