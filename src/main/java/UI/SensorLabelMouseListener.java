package UI;

import data.Sensor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
            // Ora puoi accedere ai metodi e alle proprietà di SensorForm tramite la variabile sensorForm
            if (sensorForm.isConnected()) {
                // La connessione WiFi è ancora aperta
                // Informa l'utente e chiedi se vuole chiuderla

                int option = JOptionPane.showConfirmDialog(
                        sensorForm,
                        "The WiFi connection is still open. Do you want to close it and proceed with this operation?",
                        "WiFi connection still open",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    // L'utente ha scelto di chiudere la connessione WiFi
                    sensorForm.getWiFi().disconnect();

                    // Esegui le altre operazioni qui
                }else{
                    return;
                }
            } else {
                // La connessione è chiusa
            }
        } else {
            // Il componente ottenuto non è di tipo SensorForm
        }

        // Highlight the selected sensor
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

            // Show the sensor form panel for the selected sensor
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
        // Change the background color of the label when the mouse enters
        ((JLabel) e.getSource()).setBackground(new Color(230, 230, 230));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Change the background color of the label when the mouse exits
        ((JLabel) e.getSource()).setBackground(Color.WHITE);
    }

}
