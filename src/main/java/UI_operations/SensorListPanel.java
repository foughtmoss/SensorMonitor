package UI_operations;

import data.Sensor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Manage sensors you are working with
 */

public class SensorListPanel extends JPanel {
    Font font = new Font("Poppins", Font.BOLD, 12);
    private final ArrayList<Sensor> sensorList;
    private final JPanel sensorPanel;
    private final JPanel sensorFormPanel;

    public SensorListPanel(JPanel sensorFormPanel) {

        this.sensorFormPanel = sensorFormPanel;

        sensorList = new ArrayList<>();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(200, 500));

        JLabel titleLabel = new JLabel("Sensor List");
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titleLabel.setFont(font);
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        sensorPanel = new JPanel();
        sensorPanel.setLayout(new BoxLayout(sensorPanel, BoxLayout.Y_AXIS));
        sensorPanel.setBackground(Color.DARK_GRAY);
        sensorPanel.setOpaque(true);
        scrollPane.setViewportView(sensorPanel);
    }

    public void addSensor(Sensor sensor) {
        sensorList.add(sensor);

        JLabel sensorLabel = new JLabel(sensor.getIpAddress());
        sensorLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        sensorLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sensorLabel.setForeground(Color.WHITE);
        sensorLabel.setFont(font);

        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("View");
        viewItem.addActionListener(e -> {
            viewSensorSpecification(sensor);
        });

        contextMenu.add(viewItem);

        sensorLabel.addMouseListener(new SensorLabelMouseListener(sensor, sensorFormPanel, sensorPanel, contextMenu, sensorLabel));

        sensorPanel.add(sensorLabel);

        sensorPanel.revalidate();
        sensorPanel.repaint();
    }

    public void viewSensorSpecification(Sensor sensor) {
        sensorFormPanel.removeAll();
        sensorFormPanel.add(new HistoryForm(sensor, getSensorFiles(sensor)));
        sensorFormPanel.revalidate();
        sensorFormPanel.repaint();
    }

    private ArrayList<String> getSensorFiles(Sensor sensor) {
        ArrayList<String> fileList = new ArrayList<>();

        String sensorDirectoryPath = "sensors/" + sensor.getIpAddress();

        File sensorDirectory = new File(sensorDirectoryPath);

        if (sensorDirectory.exists() && sensorDirectory.isDirectory()) {
            File[] files = sensorDirectory.listFiles();

            for (File file : files) {
                fileList.add(file.getName());
            }
        }
        return fileList;
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
}

