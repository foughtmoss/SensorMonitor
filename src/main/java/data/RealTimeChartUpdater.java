package data;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class RealTimeChartUpdater implements Runnable{
    private ChartPanel chartPanel;
    private SensorData sensorData;
    private Sensor sensor;

    public RealTimeChartUpdater(ChartPanel chartPanel, SensorData sensorData,Sensor sensor) {
        this.chartPanel = chartPanel;
        this.sensorData = sensorData;
        this.sensor=sensor;
    }
    @Override
    public void run() {
        XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
        TimeSeriesCollection dataset = (TimeSeriesCollection) plot.getDataset();
        TimeSeries series = dataset.getSeries(sensor.getType());
        if(sensor.getType().equals("ph")){
            series.addOrUpdate(new Millisecond(), sensorData.getPh());
        }else{
            if(sensor.getType().equals("Temperature")){
                series.addOrUpdate(new Millisecond(), sensorData.getTemperature());
            }else{
                series.addOrUpdate(new Millisecond(), sensorData.getChlorine());
            }
        }

        chartPanel.repaint();
    }
}
