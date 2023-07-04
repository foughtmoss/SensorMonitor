package UI;

import data.Sensor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class SensorListPanel extends JPanel {
    private ArrayList<Sensor> sensorList;
    private JPanel sensorPanel;
    private JPanel sensorFormPanel;

    //font
    Font font=new Font("Poppins", Font.BOLD,12);


    public SensorListPanel(JPanel sensorFormPanel) {



        //passo a sensorListPanel il sensorFormPanel
        this.sensorFormPanel=sensorFormPanel;

        sensorList = new ArrayList<>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 500));

        // Create title label
        JLabel titleLabel = new JLabel("Sensor List");
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titleLabel.setFont(font);
        add(titleLabel, BorderLayout.NORTH);

        // Create scrollable panel for sensor list
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Create panel to hold sensors
        sensorPanel = new JPanel();
        sensorPanel.setLayout(new BoxLayout(sensorPanel, BoxLayout.Y_AXIS));
        sensorPanel.setBackground(Color.DARK_GRAY);
        sensorPanel.setOpaque(true);
        scrollPane.setViewportView(sensorPanel);
    }

    public void addSensor(Sensor sensor) {
        sensorList.add(sensor);

        // Create sensor label and add it to sensorPanel
        JLabel sensorLabel = new JLabel(sensor.getIpAddress());
        sensorLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        sensorLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sensorLabel.setForeground(Color.WHITE);
        sensorLabel.setFont(font);


        // Create context menu for sensor label
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View");
        viewItem.addActionListener(e -> {
            // Action to perform when "View" is clicked
            viewSensorSpecification(sensor);

        });

        contextMenu.add(viewItem);

        sensorLabel.addMouseListener(new SensorLabelMouseListener(sensor,sensorFormPanel, sensorPanel,contextMenu,sensorLabel));

        sensorPanel.add(sensorLabel);

        // Repaint panel to reflect changes
        sensorPanel.revalidate();
        sensorPanel.repaint();
    }

    public void removeSensor(Sensor sensor) {
        String ipAddressToRemove = sensor.getIpAddress();
        JLabel labelToRemove = null;

        for (Component component : sensorPanel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel sensorLabel = (JLabel) component;
                if (sensorLabel.getText().equals(ipAddressToRemove)) {
                    labelToRemove = sensorLabel;
                    break;
                }
            }
        }

        if (labelToRemove != null) {
            sensorPanel.remove(labelToRemove);
            sensorList.remove(sensor);
            sensorPanel.revalidate();
            sensorPanel.repaint();
            System.out.println("Sensor label with IP address " + ipAddressToRemove + " removed.");
        } else {
            System.out.println("Sensor label with IP address " + ipAddressToRemove + " not found.");
        }
    }

    public void viewSensorSpecification(Sensor sensor){
        sensorFormPanel.removeAll();
        sensorFormPanel.add(new HistoricalForm(sensor,getSensorFiles(sensor)));
        sensorFormPanel.revalidate();
        sensorFormPanel.repaint();
    }

    public boolean isSensorDisplayed(Sensor sensor) {
        for (Component component : sensorPanel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel sensorLabel = (JLabel) component;
                if (sensorLabel.getText().equals(sensor.getIpAddress())) {
                    return true;
                }
            }
        }
        return false;
    }
    private ArrayList<String> getSensorFiles(Sensor sensor) {
        ArrayList<String> fileList = new ArrayList<>();

        // Percorso della directory del sensore
        String sensorDirectoryPath = "sensors/" + sensor.getIpAddress(); // Assumi che il percorso sia "sensors/IP_ADDRESS"

        // Creazione dell'oggetto File per la directory del sensore
        File sensorDirectory = new File(sensorDirectoryPath);

        // Verifica se la directory esiste ed è una directory
        if (sensorDirectory.exists() && sensorDirectory.isDirectory()) {
            // Recupera l'elenco dei file nella directory del sensore
            File[] files = sensorDirectory.listFiles();

            // Aggiungi i nomi dei file alla lista
            for (File file : files) {
                fileList.add(file.getName());
            }
        }

        return fileList;
    }

}

