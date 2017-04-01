/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhome.m2m;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import myhome.net.NetUtils;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

/**
 *
 * @author james
 */
public class TempPublisher extends MqttPubSub implements Runnable {

    public static final String TOPIC_TEMPERATURE = "home/temperature";
    public static final String TOPIC_BRIGHTNESS = "home/brightness";
    
    public static final int INTERVAL = 1000;
    private static final String clientId = NetUtils.getAddressByType(NetUtils.AddressType.MAC) + "-pub";

    public TempPublisher(String brokerUrl) {
        super(brokerUrl, clientId);
    }

    
    @Override
    public void run() {
        try {
            this.connectToBroker();
            boolean shouldStop = false;
            while (!shouldStop) {
                try {
                    publishBrightness();
                    Thread.sleep(INTERVAL);
                    publishTemperature();
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    NetUtils.threadMessage(e.getMessage());
                    shouldStop = true;
                } catch (MqttException e) {
                    NetUtils.threadMessage(e.getMessage());
                    shouldStop = true;
                }
            }
        } catch (MqttException ex) {
            NetUtils.threadMessage("Connection to the broker failed: " + ex.getMessage());
        }
    }

    private void publishBrightness() throws MqttException {
        final MqttTopic brightnessTopic = mqttClient.getTopic(TOPIC_BRIGHTNESS);

        final int brightnessNumber = ThreadLocalRandom.current().nextInt(20, 30);
        final String brightness = brightnessNumber + "W";

        brightnessTopic.publish(new MqttMessage(brightness.getBytes()));
        NetUtils.threadMessage("Published Topic: " + TOPIC_BRIGHTNESS + " brightness = " + brightness);
    }

    private void publishTemperature() throws MqttException {
        final MqttTopic temperatureTopic = mqttClient.getTopic(TOPIC_TEMPERATURE);

        final int temperatureNumber = ThreadLocalRandom.current().nextInt(20, 30);
        final String temperature = temperatureNumber + "°C";

        temperatureTopic.publish(new MqttMessage(temperature.getBytes()));
        NetUtils.threadMessage("Published Topic: " + TOPIC_TEMPERATURE + " temp = " + temperature);
    }
}
