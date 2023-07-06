package data;

/**
 * Represent a sensor
 */

public class Sensor {

    private final String ipAddress;
    private final int port;
    private final String type;
    private final String operatorEmail;
    private final String location;

    public Sensor(String ipAddress, int port, String type, String operatorEmail, String location) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.type = type;
        this.operatorEmail = operatorEmail;
        this.location = location;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getType() {
        return type;
    }

    public String getOperatorEmail() {
        return operatorEmail;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Sensor{" + "ipAddress='" + ipAddress + '\'' + ", port=" + port + ", type='" + type + '\'' + ", operatorEmail='" + operatorEmail + '\'' + ", location='" + location + '\'' + '}';
    }
}
