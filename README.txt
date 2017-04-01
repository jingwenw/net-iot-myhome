/*
 *     Copyright (c) 2017
 *     Author: James Wang.
 *     All rights reserved.
 *
 */

This is a demo using Apache Paho Java api communicate with a MQTT broker:

- The M2M broker is running on VM1 (open source Mosquitto)

- The MQTT subscriber was running on VM2 (written in Java with Paho lib, myhome.MyHome), subscribed the topics of home/temperature and home/lightness, acts as the Home Control Center or Commander

- The MQtt publisher was running on VM3(written in Java with Paho lib, myhome.MyHome), publishing messages under topics of home/temperature and home/lightness.

How To Use it:

- Make sure a MQTT broker, e.g. Mosquitto, is running and reachable.
- Build it by running "ant" under the directory checked out.
- On a terminal or a VM/computer running the subscriber via "java -cp dist/MyHome.jar myhome.MyHome"
- On another terminal or a VM/computer running the publisher via "java -jar dist/MyHome.jar"


