package UI;

import data.Sensor;
import database.MyJDBC;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class HistoricalForm extends JPanel {
    private Sensor sensor;
    private ArrayList<String> fileList;
    private JPanel fileListPanel;
    private JPanel infoPanel;
    private JPanel contentPanel;
    private MyJDBC myJDBC;

    public HistoricalForm(Sensor sensor, ArrayList<String> fileList) {
        this.sensor = sensor;
        this.fileList = fileList;

        myJDBC = new MyJDBC();

        setLayout(new BorderLayout());

        JPanel specificationPanel = createSpecificationPanel();

        fileListPanel = createFilePanel();
        infoPanel = createChartPanel();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        JButton sensorDirectoryButton = createIconButton("images/directory.png");
        JButton sensorOldButton = createIconButton("images/previous.png");

        sensorDirectoryButton.addActionListener(e -> showFileListPanel());
        sensorOldButton.addActionListener(e -> showInfoPanel());

        JPanel buttonPanel = createButtonPanel(sensorDirectoryButton, sensorOldButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(specificationPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSpecificationPanel() {
        JPanel specificationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;
        constraints.gridy = 0;

        Font labelFont = new Font("Arial", Font.BOLD, 20);

        addSpecificationLabel(specificationPanel, "IP Address: " + sensor.getIpAddress(), labelFont,constraints);
        constraints.gridy = 1;
        addSpecificationLabel(specificationPanel, "Port: " + sensor.getPort(), labelFont,constraints);
        constraints.gridy = 2;
        addSpecificationLabel(specificationPanel, "Type: " + sensor.getType(), labelFont,constraints);
        constraints.gridy = 3;
        addSpecificationLabel(specificationPanel, "Operator Email: " + sensor.getOperatorEmail(), labelFont,constraints);
        constraints.gridy = 4;
        addSpecificationLabel(specificationPanel, "Location: " + sensor.getLocation(), labelFont,constraints);

        return specificationPanel;
    }

    private void addSpecificationLabel(JPanel specificationPanel, String text, Font font,GridBagConstraints constraints) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        specificationPanel.add(label,constraints);
    }

    private JButton createIconButton(String imagePath) {
        JButton button = new JButton();
        button.setBackground(Color.WHITE);
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(image));
        button.setBorderPainted(true);
        button.setContentAreaFilled(false);
        button.setFocusPainted(true);
        button.setPreferredSize(new Dimension(50, 50));
        return button;
    }

    private JPanel createButtonPanel(JButton sensorDirectoryButton, JButton sensorOldButton) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(sensorDirectoryButton);
        buttonPanel.add(sensorOldButton);
        return buttonPanel;
    }

    private JPanel createFilePanel() {
        JPanel filePanel = new JPanel(new BorderLayout());

        // Create a wrapper panel for the table
        JPanel tableWrapperPanel = new JPanel(new BorderLayout());
        tableWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("File Name");
        tableModel.addColumn("Date of Detection");

        JTable fileListTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }

            public Component prepareRenderer(DefaultTableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);

                if (component instanceof JLabel) {
                    JLabel label = (JLabel) component;
                    label.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo delle celle
                }

                return component;
            }
        };
        fileListTable.setFont(new Font("Arial", Font.BOLD, 14));

        fileList.forEach(fileName -> {
            int startIndex = fileName.indexOf("_") + 1; // Trova l'indice del primo carattere dopo il primo underscore
            int endIndex = fileName.indexOf("_", startIndex); // Trova l'indice del secondo underscore a partire dall'indice precedente
            String dateOfDetectionStr = fileName.substring(startIndex, endIndex); // Estrai la porzione del nome del file contenente la data di rilevazione

            // Formatta la data nel formato desiderato
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDateOfDetection = "";
            try {
                Date dateOfDetection = inputDateFormat.parse(dateOfDetectionStr);
                formattedDateOfDetection = outputDateFormat.format(dateOfDetection);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            tableModel.addRow(new Object[]{fileName, formattedDateOfDetection});
        });

        JScrollPane scrollPane = new JScrollPane(fileListTable);
        tableWrapperPanel.add(scrollPane, BorderLayout.CENTER);

        // Personalizza l'aspetto dell'intestazione della tabella
        JTableHeader header = fileListTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.BLACK);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY));

        fileListTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER); // Centra il testo delle celle
                label.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY)); // Aggiunge una linea inferiore a ogni cella
                label.setBackground(row % 2 == 0 ? Color.WHITE : UIManager.getColor("Table.background")); // Alterna il colore di sfondo delle righe

                if (isSelected) {
                    label.setBackground(Color.LIGHT_GRAY); // Imposta il colore di sfondo per la riga selezionata
                }

                return label;
            }
        });

        fileListTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int selectedRow = fileListTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String selectedFile = (String) tableModel.getValueAt(selectedRow, 0);
                        openFile(selectedFile);
                    }
                }
            }
        });

        filePanel.add(tableWrapperPanel, BorderLayout.CENTER);

        return filePanel;
    }

    private JPanel createChartPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        JComboBox<String> chartTypeComboBox = new JComboBox<>();
        chartTypeComboBox.addItem("Line Chart");
        chartTypeComboBox.addItem("Bar Chart");

        JComboBox<String> dateComboBox = new JComboBox<>();
        myJDBC.setDBConnection();
        LinkedHashSet<java.sql.Date>dateLinkedHashSet =myJDBC.getDateList(sensor);
        myJDBC.closeDBConnection();

        for(java.sql.Date date:dateLinkedHashSet){
            dateComboBox.addItem(String.valueOf(date));
        }

        JButton showChartButton = new JButton("Show Chart");

        inputPanel.add(chartTypeComboBox);
        inputPanel.add(dateComboBox);
        inputPanel.add(showChartButton);

        infoPanel.add(inputPanel, BorderLayout.NORTH);

        showChartButton.addActionListener(e -> {
            String selectedChartType = (String) chartTypeComboBox.getSelectedItem();
            String selectedDate = (String) dateComboBox.getSelectedItem();
            myJDBC.setDBConnection();
            LinkedHashMap<String, Double> map = myJDBC.getDataFromDate(sensor, selectedDate);
            JPanel chartPanelContainer = createChart(map, selectedChartType);
            infoPanel.removeAll();
            infoPanel.add(inputPanel, BorderLayout.NORTH);
            infoPanel.add(chartPanelContainer, BorderLayout.CENTER);
            infoPanel.revalidate();
            infoPanel.repaint();
            myJDBC.closeDBConnection();
        });

        return infoPanel;
    }

    private void showFileListPanel() {
        contentPanel.removeAll();
        contentPanel.add(fileListPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showInfoPanel() {
        contentPanel.removeAll();
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void openFile(String fileName) {
        String filePath = "sensors/" + sensor.getIpAddress() + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "File not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel createChart(LinkedHashMap<String, Double> dataMap, String selectedChartType) {
        JFreeChart chart = null;

        if (selectedChartType.equals("Line Chart")) {
            // Crea un dataset utilizzando la serie
            TimeSeriesCollection dataset = new TimeSeriesCollection();
            TimeSeries series = new TimeSeries(sensor.getType());

            // Aggiungi i dati alla serie
            for (String millisecondStr : dataMap.keySet()) {
                double dataValue = dataMap.get(millisecondStr);

                // Converti la stringa del millisecondo in oggetto Date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date millisecondDate;
                try {
                    millisecondDate = dateFormat.parse(millisecondStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue; // Salta l'iterazione corrente in caso di errore di parsing
                }

                // Converti l'oggetto Date in oggetto Millisecond
                Millisecond millisecond = new Millisecond(millisecondDate);

                // Aggiungi il valore alla serie
                series.add(millisecond, dataValue);
            }

            dataset.addSeries(series);
            // Crea il grafico a linea
            chart = ChartFactory.createTimeSeriesChart(
                    "Line chart",  // Titolo del grafico
                    "Millisecond",  // Etichetta asse x
                    "Value",  // Etichetta asse y
                    dataset,  // Dataset
                    true,  // Legenda
                    true,  // Tooltips
                    false  // URL
            );
        } else if (selectedChartType.equals("Bar Chart")) {
            // Crea il dataset di categorie
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            // Aggiungi i dati al dataset
            int counter = 1;
            for (String millisecondStr : dataMap.keySet()) {
                double dataValue = dataMap.get(millisecondStr);
                // Aggiungi il valore alla categoria "Valore" associata al millisecondo
                dataset.addValue(dataValue, sensor.getType(), String.valueOf(counter));
                counter++;
            }

            // Crea il grafico a barre
            chart = ChartFactory.createBarChart(
                    "Bar chart",  // Titolo del grafico
                    "Detection",  // Etichetta asse y
                    "Value",  // Etichetta asse x
                    dataset,  // Dataset di categorie
                    PlotOrientation.VERTICAL,  // Orientamento delle barre (verticale)
                    true,  // Legenda
                    true,  // Tooltips
                    false  // URL
            );
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        JPanel chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);

        return chartPanelContainer;
    }
}
