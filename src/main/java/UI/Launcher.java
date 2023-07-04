package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Launcher {

    //crea il pannello per la lista dei sensori
    private SensorListPanel sensorListPanel;
    private JPanel sensorFormPanel;
    private JPanel mainPanel;
    private JFrame frame;
    private JMenuBar menuBar;
    private JMenu add;
    private JMenu inventory;
    private JMenuItem addSensorItem;
    private JMenuItem seeInventory;

    public Launcher() {

        //set frame principale
        this.frame=new JFrame("WaterQuality");
        this.frame.setResizable(true);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //crea il pannello principale
        this.mainPanel=new JPanel(new BorderLayout());

        //crea il sensorFormPanel
        sensorFormPanel=new JPanel();
        sensorFormPanel.setLayout(new BorderLayout());
        sensorFormPanel.setPreferredSize(new Dimension(200, 500));

        //crea il sensorListPanel
        sensorListPanel= new SensorListPanel(sensorFormPanel);

        sensorFormPanel.add(new Inventory(sensorFormPanel,sensorListPanel));

        //crea menu
        this.menuBar=new JMenuBar();
        this.add=new JMenu("Add");
        this.addSensorItem=new JMenuItem("Sensor");
        this.inventory=new JMenu("Inventory");
        this.seeInventory=new JMenuItem("see Inventory");
        this.add.add(addSensorItem);
        this.inventory.add(seeInventory);
        this.menuBar.add(add);
        this.menuBar.add(inventory);
        this.frame.setJMenuBar(menuBar);

        //aggiungi sensorListPanel e sensorFormPanel al main panel
        this.mainPanel.add(this.sensorListPanel,BorderLayout.WEST);
        this.mainPanel.add(this.sensorFormPanel,BorderLayout.CENTER);

        seeInventory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                sensorFormPanel.removeAll();
                sensorFormPanel.add(new Inventory(sensorFormPanel,sensorListPanel));
                sensorFormPanel.revalidate();
                sensorFormPanel.repaint();
            }
        });

        addSensorItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
                sensorFormPanel.removeAll();
                sensorFormPanel.add(new AddSensorPanel(sensorListPanel));
                sensorFormPanel.revalidate();
                sensorFormPanel.repaint();
            }
        });


        frame.getContentPane().add(this.mainPanel);
        frame.pack();
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Launcher());
    }
}
