package UI_operations;

import data.Sensor;
import database.MyJDBC;
import file.SensorFile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddSensorPanel extends JPanel {
    private JLabel ipAddressLabel;
    private JLabel portLabel;
    private JLabel typeLabel;
    private JLabel operatorEmailLabel;
    private JLabel locationLabel;
    private JTextField ipAddressField;
    private JTextField portField;
    private JComboBox<String> typeField;
    private JTextField operatorEmailField;
    private JTextField locationField;
    private JButton saveButton;
    private SensorListPanel sensorListPanel;

    /**
     * Panel for adding new sensors
    */

    public AddSensorPanel(SensorListPanel sensorListPanel) {
        this.sensorListPanel = sensorListPanel;
        setLayout(new BorderLayout());

        ipAddressLabel = new JLabel("IP Address:");
        portLabel = new JLabel("Port:");
        typeLabel = new JLabel("Sensor Type:");
        operatorEmailLabel = new JLabel("Operator's email:");
        locationLabel = new JLabel("Location:");

        ipAddressField = new JTextField(20);
        portField = new JTextField(20);
        typeField = new JComboBox<>();
        typeField.addItem("ph");
        typeField.addItem("Temperature");
        typeField.addItem("Chlorine");
        operatorEmailField = new JTextField(20);
        locationField = new JTextField(20);

        saveButton = new JButton("Add");
        saveButton.setPreferredSize(new Dimension(80, saveButton.getPreferredSize().height));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (ipAddressField.getText().isEmpty() || portField.getText().isEmpty() ||
                        operatorEmailField.getText().isEmpty() || locationField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(AddSensorPanel.this,
                            "Please fill in all the fields",
                            "Missing Fields",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String ipAddress = ipAddressField.getText();
                int port;
                try{
                    port = Integer.parseInt(portField.getText());
                }catch(NumberFormatException exception){
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(AddSensorPanel.this,
                                    "Invalid port number",
                                    "Invalid Input",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    return;
                }

                String type = String.valueOf(typeField.getSelectedItem());
                String operatorEmail = operatorEmailField.getText();
                String location = locationField.getText();

                Sensor sensor = new Sensor(ipAddress, port, type, operatorEmail, location);

                sensorListPanel.addSensor(sensor);
                MyJDBC myJDBC = new MyJDBC();
                myJDBC.setDBConnection();
                myJDBC.addSensor(sensor);
                myJDBC.closeDBConnection();

                new SensorFile().createDirectory(sensor);

                //pulisci i campi
                ipAddressField.setText("");
                portField.setText("");
                operatorEmailField.setText("");
                locationField.setText("");
            }
        });

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        inputPanel.add(ipAddressLabel, gbc);
        inputPanel.add(ipAddressField, gbc);
        inputPanel.add(portLabel, gbc);
        inputPanel.add(portField, gbc);
        inputPanel.add(typeLabel, gbc);
        inputPanel.add(typeField, gbc);
        inputPanel.add(operatorEmailLabel, gbc);
        inputPanel.add(operatorEmailField, gbc);
        inputPanel.add(locationLabel, gbc);
        inputPanel.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0);
        inputPanel.add(saveButton, gbc);

        add(inputPanel, BorderLayout.CENTER);
    }
}

