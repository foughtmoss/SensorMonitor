package database;

import data.Sensor;
import org.jfree.data.time.Millisecond;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Establish a connection with the database and operate on it
 */
public class MyJDBC {

    private static Connection connection;

    public MyJDBC() {
    }

    public void setDBConnection(){
        try {
            connection= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/sensordb","root","XYZ.sensormonitorproject178");
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void getSensorInformation(Sensor sensor){
        try {
            Statement statement= connection.createStatement();
            ResultSet resultSet=statement.executeQuery("");
            while(resultSet.next()){
                System.out.println(resultSet.getString("ipAddress"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LinkedHashMap<String,Double> getDataFromDate(Sensor sensor, String date) {
        LinkedHashMap<String,Double> map=new LinkedHashMap<>();
        try {
            String query = "SELECT millisecond, dataValue FROM detections WHERE ipAddress = ? AND detectionDate = ?  ORDER BY millisecond";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sensor.getIpAddress());
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("millisecond");
                Millisecond millisecond = new Millisecond(timestamp);
                double dataValue = resultSet.getDouble("dataValue");

                // Formattazione del millisecondo
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String formattedMillisecond = dateFormat.format(millisecond.getStart());

                // Utilizzare i dati come richiesto
                System.out.println("Millisecond: " + formattedMillisecond);
                System.out.println("Data Value: " + dataValue);

                //aggiungere alla map
                map.put(formattedMillisecond,dataValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public LinkedHashMap<String, Double> getDataFromDate(Sensor sensor, String date, String fileMillisecond) {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        try {
            String query = "SELECT millisecond, dataValue FROM detections WHERE ipAddress = ? AND detectionDate = ? AND millisecond >= ? ORDER BY millisecond";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sensor.getIpAddress());
            preparedStatement.setString(2, date);
            preparedStatement.setString(3, fileMillisecond); // Aggiungi il millisecondo del file come parametro
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("millisecond");
                Millisecond millisecond = new Millisecond(timestamp);
                double dataValue = resultSet.getDouble("dataValue");

                // Formattazione del millisecondo
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String formattedMillisecond = dateFormat.format(millisecond.getStart());

                // Utilizzare i dati come richiesto
                System.out.println("Millisecond: " + formattedMillisecond);
                System.out.println("Data Value: " + dataValue);

                // Aggiungi alla mappa solo se il millisecondo è maggiore o uguale a quello del file
                if (formattedMillisecond.compareTo(fileMillisecond) >= 0) {
                    map.put(formattedMillisecond, dataValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public ArrayList<Sensor> getSensors(){
        ArrayList<Sensor> sensors=new ArrayList<>();
        try {
            Statement statement= connection.createStatement();
            ResultSet resultSet=statement.executeQuery("select * from sensor");
            while(resultSet.next()){
                String ipAddress=resultSet.getString("ipAddress");
                int port=resultSet.getInt("sensorPort");
                String type=resultSet.getString("sensorType");
                String operatorEmail=resultSet.getString("operatorEmail");
                String location=resultSet.getString("Location");
                Sensor sensor=new Sensor(ipAddress,port,type,operatorEmail,location);
                sensors.add(sensor);
                for(Sensor s:sensors){
                    s.toString();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensors;
    }

    public void addSensor(Sensor sensor){
        try {
            String insertSensorQuery = "INSERT INTO Sensor (ipAddress, sensorPort, sensorType,Location, operatorEmail) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSensorQuery);
            preparedStatement.setString(1, sensor.getIpAddress());
            preparedStatement.setInt(2, sensor.getPort());
            preparedStatement.setString(3, sensor.getType());
            preparedStatement.setString(4, sensor.getLocation());
            preparedStatement.setString(5, sensor.getOperatorEmail());
            preparedStatement.executeUpdate();
            System.out.println("Sensor added to db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void closeDBConnection() {
        try {
            connection.close();
            System.out.println("connection closed");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Sensor> groupBy(String groupBy) {
        ArrayList<Sensor> sensors = new ArrayList<>();
        try {
            String query = "SELECT ipAddress, sensorPort, sensorType, operatorEmail, Location FROM sensor ORDER BY " + groupBy;
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                String ipAddress = resultSet.getString("ipAddress");
                int port = resultSet.getInt("sensorPort");
                String type = resultSet.getString("sensorType");
                String operatorEmail = resultSet.getString("operatorEmail");
                String location = resultSet.getString("Location");
                Sensor sensor = new Sensor(ipAddress, port, type, operatorEmail, location);
                sensors.add(sensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensors;
    }
    public ArrayList<Sensor> searchSensor(String ip) {
        ArrayList<Sensor> sensors = new ArrayList<>();
        try {
            String query = "SELECT ipAddress, sensorPort, sensorType, operatorEmail, Location FROM sensor WHERE ipAddress = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String ipAddress = resultSet.getString("ipAddress");
                int port = resultSet.getInt("sensorPort");
                String type = resultSet.getString("sensorType");
                String operatorEmail = resultSet.getString("operatorEmail");
                String location = resultSet.getString("Location");
                Sensor sensor = new Sensor(ipAddress, port, type, operatorEmail, location);
                sensors.add(sensor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sensors;
    }
    public void removeFromDatabase(String ip) {
        try {
            String query = "DELETE FROM sensor WHERE ipAddress = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeAssociatedDetections(String ip){
        try {
            String query = "DELETE FROM detections WHERE ipAddress = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ip);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeData(HashMap<Millisecond, Double> detectedData, Sensor sensor) {
        String ipAddress = sensor.getIpAddress();

        try {
            String insertDataQuery = "INSERT INTO Detections (ipAddress, detectionDate, millisecond, dataValue) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertDataQuery);

            for (Map.Entry<Millisecond, Double> entry : detectedData.entrySet()) {
                Millisecond millisecond = entry.getKey();
                Double dataValue = entry.getValue();

                // Convert Millisecond to java.sql.Timestamp
                long timeInMillis = millisecond.getFirstMillisecond();
                Timestamp timestamp = new Timestamp(timeInMillis);

                // Set the parameter values
                preparedStatement.setString(1, ipAddress);
                preparedStatement.setDate(2, new Date(timestamp.getTime()));
                preparedStatement.setTimestamp(3, timestamp);
                preparedStatement.setDouble(4, dataValue);

                // Execute the query
                preparedStatement.executeUpdate();
            }

            System.out.println("Data stored in the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public LinkedHashSet<Date> getDateList(Sensor sensor){
        LinkedHashSet<Date> dateLinkedHashSet = new LinkedHashSet<>();
        try {
            String query = "SELECT detectionDate FROM detections WHERE ipAddress = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sensor.getIpAddress());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Date date = resultSet.getDate("detectionDate");
                dateLinkedHashSet.add(date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dateLinkedHashSet;
    }
}
