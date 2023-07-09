# Sensor_Monitor
<img src="https://github.com/foughtmoss/SensorMonitor/assets/136918608/02f2bd83-c5b0-486f-89f0-e89b7bac6058" alt="App Icon" width="100" height="100">

A simple desktop application for monitoring Wi-Fi sensors.

# Features
* Add new sensors 
* Select a sensor and establish a connection via the application
* Examine the real-time chart and set up new alarms
* Contact automatically the designated operator via email
* Save detected data and check them later via line charts, bar charts or .csv files
* Operate on an inventory in tabular form
* And others more...
 
# Core Idea
1) The application connects to the sensor;
2) The sensor starts sending data to the application;
3) The application interprets the data;
4) The applicatin update the chart;
5) The connection is closed;

Operations 2,3,4 continue until operation 5 is performed

![flow drawio](https://github.com/foughtmoss/SensorMonitor/assets/136918608/e4ebd118-0044-4d4f-9c72-c506749fb419)

# Screenshots
Navigate the inventory and select the needed sensor by clicking on it. You can open its Sensor Form by double clicking the selected row, or you can remove it by right clicking the selected row and then choosing the remove option.
You can search a sensor by entering its IP address, or sort all the sensors by type or location.

![inventory](https://github.com/foughtmoss/SensorMonitor/assets/136918608/931a8cd5-b53f-4409-8589-a07c6966b6ef)

Establish a Wi-Fi connection with the sensor, set up new alarms and delete the old ones from the chart, contact automatically the designated operator via email, keep track of the sensors you are working with during the session, reload the Sensor Form by clicking on its label in the SensorList panel, switch Form, and save data.

![total](https://github.com/foughtmoss/SensorMonitor/assets/136918608/5158aff2-50d1-4f36-8892-2b190f456ca1)

Every time the application is connected to a sensor via Wi-Fi you must close the connection before switching to another Form or reloading the current one.

![errormessage](https://github.com/foughtmoss/SensorMonitor/assets/136918608/23ebc740-935c-4059-9d60-cca9139abddd)

Work with previously saved .csv files.

![historypanelWithFiles](https://github.com/foughtmoss/SensorMonitor/assets/136918608/aaf489de-8054-4620-b960-c4ad14b80ed1)

Analyze previous charts selecting the detection date and the type of chart.

![historypanelWithLineChart](https://github.com/foughtmoss/SensorMonitor/assets/136918608/062bd386-7042-4f7a-8992-2fcc83377bf7)

![barchart](https://github.com/foughtmoss/SensorMonitor/assets/136918608/f41d6298-7ade-407d-abfa-8b6ae38a70a6)

Add new sensors and save their details in the database. Currently you can select one of three different type of sensors (ph sensor, Temperature sensor, Chlorine sensor).

![addSensor](https://github.com/foughtmoss/SensorMonitor/assets/136918608/067c917d-2494-4315-94eb-37829310ab01)

# Suorces
* Java API
* GMail API
* JFreeChart API

# License
This software is licensed under MIT License.

# Contributors
Any contributors are welcome!
