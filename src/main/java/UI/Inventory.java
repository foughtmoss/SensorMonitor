package UI;

import data.Sensor;
import database.MyJDBC;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

public class Inventory extends JPanel {
    private ArrayList<Sensor> sensors;
    private MyJDBC myJDBC;
    private JPanel sensorFormPanel;
    private SensorListPanel sensorListPanel;
    private Object[][] data;
    private DefaultTableModel model;
    private JTable table;
    private JScrollPane scrollPane;
    private JComboBox<String> sortByComboBox;
    private JTextField searchField;
    private JButton searchButton;

    public Inventory(JPanel sensorFormPanel, SensorListPanel sensorListPanel) {
        this.sensorFormPanel = sensorFormPanel;
        this.sensorListPanel = sensorListPanel;
        setLayout(new BorderLayout());

        sensors = new ArrayList<>();

        myJDBC = new MyJDBC();
        myJDBC.setDBConnection();
        sensors = myJDBC.getSensors();
        myJDBC.closeDBConnection();

        data = getDataForTable(sensors);
        model = createModel(data);
        table = createTable(model);
        scrollPane = createScrollPane(table);

        addToPanel(scrollPane);
    }

    private Object[][] getDataForTable(ArrayList<Sensor> sensors) {
        Object[][] data = new Object[sensors.size()][5];
        for (int i = 0; i < sensors.size(); i++) {
            Sensor s = sensors.get(i);
            data[i][0] = s.getIpAddress();
            data[i][1] = s.getPort();
            data[i][2] = s.getType();
            data[i][3] = s.getLocation();
            data[i][4] = s.getOperatorEmail();
        }
        return data;
    }

    private DefaultTableModel createModel(Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, new String[]{"IP Address", "Port", "Type", "Location", "Operator Email"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return model;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        table.setRowHeight(40);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int row = target.getSelectedRow();

                if (row != -1) {
                    // Ottenere l'indirizzo IP selezionato
                    String ipAddress = (String) target.getValueAt(row, 0);

                    // Trova il sensore corrispondente all'indirizzo IP selezionato
                    Sensor selectedSensor = null;
                    for (Sensor sensor : sensors) {
                        if (sensor.getIpAddress().equals(ipAddress)) {
                            selectedSensor = sensor;
                            break;
                        }
                    }

                    if (e.getClickCount() == 2 && ipAddress != null) {  // Controllo per un doppio clic
                        // Aprire il SensorForm con il sensore selezionato
                        sensorFormPanel.removeAll();
                        sensorFormPanel.add(new SensorForm(selectedSensor));
                        sensorFormPanel.revalidate();
                        sensorFormPanel.repaint();

                        boolean isSensorAlreadyDisplayed = sensorListPanel.isSensorDisplayed(selectedSensor);

                        // Aggiungi il sensore solo se non è già visualizzato
                        if (!isSensorAlreadyDisplayed) {
                            sensorListPanel.addSensor(selectedSensor);
                            sensorListPanel.revalidate();
                            sensorListPanel.repaint();
                        }

                    } else if (SwingUtilities.isRightMouseButton(e) && ipAddress != null) {
                        int selectedRow = table.getSelectedRow();
                        if (selectedRow != -1) {
                            table.setRowSelectionInterval(selectedRow, selectedRow);
                            JPopupMenu contextMenu = new JPopupMenu();
                            JMenuItem removeItem = new JMenuItem("Remove");
                            contextMenu.add(removeItem);

                            removeItem.addActionListener(e2 -> {
                                int choice = JOptionPane.showConfirmDialog(null, "Do you want to remove the sensor from inventory?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                    String ip = (String) table.getValueAt(selectedRow, 0);

                                    myJDBC.setDBConnection();
                                    myJDBC.removeAssociatedDetections(ip);
                                    myJDBC.removeFromDatabase(ip);
                                    deleteSelectedSensorFiles(ip);
                                    myJDBC.closeDBConnection();

                                    Sensor removedSensor = null;
                                    for (Sensor sensor : sensors) {
                                        if (sensor.getIpAddress().equals(ip)) {
                                            removedSensor = sensor;
                                            break;
                                        }
                                    }
                                    if (removedSensor != null) {
                                        sensors.remove(removedSensor);
                                        sensorListPanel.removeSensor(removedSensor);//appena aggiunta
                                    }

                                    model.removeRow(selectedRow);
                                    addToPanel(scrollPane);
                                }
                            });

                            contextMenu.show(table, e.getX(), e.getY());
                        }
                    }
                }
            }
        });

        return table;
    }

    private JScrollPane createScrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel sortByLabel = new JLabel("Sort By:");
        sortByComboBox = new JComboBox<>();
        sortByComboBox.addItem("Type");
        sortByComboBox.addItem("Location");
        sortByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) sortByComboBox.getSelectedItem();
                if(selectedOption.equals("Type")){
                    sortBy("sensorType");
                }else if(selectedOption.equals("Location")){
                    sortBy("Location");
                }
            }
        });

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                searchSensor(searchText);
            }
        });

        controlPanel.add(sortByLabel);
        controlPanel.add(sortByComboBox);
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchButton);

        return controlPanel;
    }

    private void addToPanel(Component component) {
        removeAll();
        add(createControlPanel(), BorderLayout.NORTH);
        add(component, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void sortBy(String sortBy) {
        myJDBC.setDBConnection();
        sensors = myJDBC.groupBy(sortBy);
        myJDBC.closeDBConnection();

        data = getDataForTable(sensors);
        model.setDataVector(data, new String[]{"IP Address", "Port", "Type", "Location", "Operator Email"});

        addToPanel(scrollPane);
    }

    private void searchSensor(String searchText) {
        myJDBC.setDBConnection();
        sensors = myJDBC.searchSensor(searchText);
        myJDBC.closeDBConnection();

        data = getDataForTable(sensors);
        model.setDataVector(data, new String[]{"IP Address", "Port", "Type", "Location", "Operator Email"});

        addToPanel(scrollPane);
    }
    private void deleteSelectedSensorFiles(String ipAddress){

        File[] files;

        // Percorso della directory del sensore
        String sensorDirectoryPath = "sensors/"+ipAddress;

        // Creazione dell'oggetto File per la directory del sensore
        File sensorDirectory = new File(sensorDirectoryPath);

        // Verifica se la directory esiste ed è una directory
        if (sensorDirectory.exists() && sensorDirectory.isDirectory()) {
            // Recupera l'elenco dei file nella directory del sensore
            files = sensorDirectory.listFiles();

            // Aggiungi i nomi dei file alla lista
            for (File file : files) {
                if(file.delete()){
                    System.out.println("file deleted successfully");
                }else{
                    System.out.println("file could not be delete");
                }
            }
            if(sensorDirectory.delete()){
                System.out.println("directory deleted successfully");
            }else{
                System.out.println("directory could not be delete");
            }
        }
    }
}
