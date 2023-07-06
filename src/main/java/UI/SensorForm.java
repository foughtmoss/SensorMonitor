package UI;

import connections.WiFi;
import data.*;
import database.MyJDBC;
import email.EmailSender;
import file.SensorFile;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Represent the form of a sensor
 */

public class SensorForm extends JPanel implements Observer{
    double minValue;
    double maxValue;
    private JButton connectButton;
    private JButton disconnectButton;
    private JButton saveButton;
    private ChartPanel chartPanel;
    private JLabel highValueLabel;
    private JTextField highValueField;
    private JLabel lowValueLabel;
    private JTextField lowValueField;
    private JButton setAlarmButton;
    private Sensor sensor;
    private WiFi wiFi;
    private AlarmNotificator alarmNotificator;
    private JPanel messagePanel;
    private HashMap<Millisecond,Double> detectedData;
    private MyJDBC myJDBC;
    private boolean alarmIsOn=false;
    private boolean isConnected=false;


    public SensorForm(Sensor sensor) {

        this.sensor=sensor;
        wiFi=new WiFi(sensor);
        myJDBC=new MyJDBC();

        detectedData=new HashMap<>();

        setLayout(new BorderLayout());

        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        saveButton=new JButton("Save");
        connectionPanel.add(saveButton);

        add(connectionPanel, BorderLayout.NORTH);

        TimeSeries series = new TimeSeries(sensor.getType());
        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(" Sensor "+sensor.getIpAddress()+" Real-Time Chart", "Time", "Value", dataset);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        chartPanel = new ChartPanel(chart);
        JPanel chartPanelContainer = new JPanel(new BorderLayout());
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        add(chartPanelContainer, BorderLayout.CENTER);

        JPanel alarmsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        highValueLabel = new JLabel("High Value");
        highValueField = new JTextField(5);
        lowValueLabel = new JLabel("Low Value");
        lowValueField = new JTextField(5);
        setAlarmButton = new JButton("Set Alarm");
        alarmsPanel.add(highValueLabel);
        alarmsPanel.add(highValueField);
        alarmsPanel.add(lowValueLabel);
        alarmsPanel.add(lowValueField);
        alarmsPanel.add(setAlarmButton);
        add(alarmsPanel, BorderLayout.SOUTH);

        messagePanel = new JPanel();
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        messagePanel.setBorder(border);
        messagePanel.setAlignmentY(Component.TOP_ALIGNMENT);
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(400, getHeight()));

        add(scrollPane, BorderLayout.EAST);

        setAlarmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (highValueField.getText().isEmpty() || lowValueField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "If you want to set an alarm, you must insert all the critical values");
                } else {
                    double newMaxValue = Double.parseDouble(highValueField.getText());
                    double newMinValue = Double.parseDouble(lowValueField.getText());

                    if (alarmNotificator != null) {
                        wiFi.removeObserver(alarmNotificator);
                        alarmNotificator = null;
                    }

                    XYPlot plot = chart.getXYPlot();
                    plot.clearRangeMarkers();

                    Marker alarmMAXMarker = new ValueMarker(newMaxValue);
                    Marker alarmMINMarker = new ValueMarker(newMinValue);
                    alarmMAXMarker.setPaint(Color.BLUE);
                    alarmMINMarker.setPaint(Color.BLUE);
                    alarmMAXMarker.setStroke(new BasicStroke(2));
                    alarmMINMarker.setStroke(new BasicStroke(2));
                    plot.addRangeMarker(alarmMAXMarker);
                    plot.addRangeMarker(alarmMINMarker);

                    maxValue = newMaxValue;
                    minValue = newMinValue;

                    alarmNotificator = new AlarmNotificator(sensor, maxValue, minValue, SensorForm.this);
                    wiFi.addObserver(alarmNotificator);

                    alarmIsOn = true;

                    highValueField.setText("");
                    lowValueField.setText("");
                }
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    wiFi.addObserver(SensorForm.this);
                    wiFi.connect();
                    isConnected=wiFi.isConnected();
                }catch(Exception exception){
                    JOptionPane.showMessageDialog(null,"Problems occurred while trying to set a connection to the sensor");
                }

            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wiFi.disconnect();
                isConnected=wiFi.isConnected();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(!detectedData.isEmpty()){
                    myJDBC.setDBConnection();
                    myJDBC.storeData(detectedData,sensor);
                    myJDBC.closeDBConnection();

                    SensorFile sensorFile = new SensorFile();
                    sensorFile.createDirectory(sensor);
                    StringBuilder stringBuilder=createStringBuilder(alarmIsOn,minValue,maxValue);
                    sensorFile.saveToFile(sensor, stringBuilder);

                }

            }
        });

    }

    public StringBuilder createStringBuilder(boolean alarmIsOn, double minValue, double maxValue) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Detected data").append(";").append("Date of Detection").append(";").append("Alarm").append("\n");
        ArrayList<String> fileList = getSensorFiles(sensor);
        String lastFileMillisecond = null;
        if (!fileList.isEmpty()) {
            String lastFileName = fileList.get(fileList.size() - 1);
            lastFileName = lastFileName.substring(0, lastFileName.lastIndexOf('.'));
            lastFileName = lastFileName.substring(lastFileName.indexOf('_') + 1);
            SimpleDateFormat fileDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                Date lastFileDate = fileDateFormat.parse(lastFileName);
                lastFileMillisecond = dbDateFormat.format(lastFileDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        myJDBC.setDBConnection();
        LinkedHashMap<String, Double> linkedHashMap;
        if (lastFileMillisecond != null) {
            linkedHashMap = myJDBC.getDataFromDate(sensor, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), lastFileMillisecond);
        } else {
            linkedHashMap = myJDBC.getDataFromDate(sensor, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }

        for (String millisecondStr : linkedHashMap.keySet()) {
            double dataValue = linkedHashMap.get(millisecondStr);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date millisecondDate;
            try {
                millisecondDate = dateFormat.parse(millisecondStr);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            Millisecond millisecond = new Millisecond(millisecondDate);

            boolean isAlarm = false;
            if (alarmIsOn) {
                if (dataValue < minValue || dataValue > maxValue) {
                    isAlarm = true;
                }
            }

            stringBuilder.append(dataValue).append(";").append(millisecond).append(";").append(isAlarm ? "!" : "").append("\n");
        }

        myJDBC.closeDBConnection();
        return stringBuilder;
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

    public void update(SensorData sensorData) {
        System.out.println("ricevuto update "+sensorData.getPh());
        if(sensor.getType().equals("ph")){
            detectedData.put(new Millisecond(),sensorData.getPh());
            System.out.println(detectedData);
        }else{
            if(sensor.getType().equals("Temperature")){
                detectedData.put(new Millisecond(),sensorData.getTemperature());
                System.out.println(detectedData);
            }else{
                detectedData.put(new Millisecond(),sensorData.getChlorine());
                System.out.println(detectedData);
            }
        }

        RealTimeChartUpdater realTimeChartUpdater = new RealTimeChartUpdater(this.chartPanel, sensorData,sensor);
        SwingUtilities.invokeLater(realTimeChartUpdater);

    }

    public void updateAlarms(String message) {
        JLabel messageLabel = new JLabel(message);
        JCheckBox contactOperatorCheckBox = new JCheckBox("Contact operator");

        JPanel messageBox = new JPanel();
        messageBox.setLayout(new FlowLayout(FlowLayout.LEFT));
        Border border = BorderFactory.createLineBorder(Color.BLACK);
        messageBox.setPreferredSize(new Dimension(350, 50));
        messageBox.setOpaque(false);
        messageBox.setBorder(border);
        messageBox.add(contactOperatorCheckBox);
        messageBox.add(messageLabel);

        messageBox.setMaximumSize(messageBox.getPreferredSize());

        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        int verticalSpacing = 10;
        messagePanel.add(messageBox);
        messagePanel.add(Box.createVerticalStrut(verticalSpacing));

        contactOperatorCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (contactOperatorCheckBox.isSelected()) {
                    Thread operationThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new EmailSender(sensor.getOperatorEmail()).sendEmail("Alarm detected!",message);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    operationThread.start();
                }
            }
        });

        messagePanel.revalidate();
        messagePanel.repaint();
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public WiFi getWiFi() {
        return wiFi;
    }
}
