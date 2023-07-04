package UI;

import data.Sensor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * Manage some user actions
 */
public class SensorLabelMouseListener extends MouseAdapter {

    private Sensor sensor;
    private JPanel sensorFormPanel;
    private JPanel sensorPanel;
    private JPopupMenu contextMenu;
    private JLabel sensorLabel;

    public SensorLabelMouseListener(Sensor sensor, JPanel sensorFormPanel, JPanel sensorPanel,JPopupMenu contextMenu,JLabel sensorLabel) {
        this.sensor = sensor;
        this.sensorFormPanel = sensorFormPanel;
        this.sensorPanel = sensorPanel;
        this.contextMenu=contextMenu;
        this.sensorLabel=sensorLabel;
    }


    @Override
    public void mousePressed(MouseEvent e) {

        Component component1 = sensorFormPanel.getComponent(0);
        if (component1 instanceof SensorForm) {
            SensorForm sensorForm = (SensorForm) component1;

            if (sensorForm.isConnected()) {
                int option = JOptionPane.showConfirmDialog(
                        sensorForm,
                        "The WiFi connection is still open. Do you want to close it and proceed with this operation?",
                        "WiFi connection still open",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    sensorForm.getWiFi().disconnect();

                }else{
                    return;
                }
            } else {
            }
        } else {
        }

        if(SwingUtilities.isLeftMouseButton(e)) {
            Component[] components = sensorPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    if (label.getText().equals(sensor.getIpAddress())) {
                        label.setBackground(new Color(135, 206, 250));
                    } else {
                        label.setBackground(Color.WHITE);
                    }
                }
            }

            sensorFormPanel.removeAll();
            sensorFormPanel.add(new SensorForm(sensor));
            sensorFormPanel.revalidate();
            sensorFormPanel.repaint();
        }else{
            if(SwingUtilities.isRightMouseButton(e)){
                contextMenu.show(sensorLabel, e.getX(), e.getY());
            }
        }

    }
    @Override
    public void mouseEntered(MouseEvent e) {
        ((JLabel) e.getSource()).setBackground(Color.CYAN);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ((JLabel) e.getSource()).setBackground(Color.WHITE);
    }

}
