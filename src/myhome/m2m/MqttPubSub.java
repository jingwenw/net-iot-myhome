/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhome.m2m;

import myhome.net.NetUtils;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author james
 */
public class MqttPubSub implements MqttCallback{

    public static final String BROKER_URL_MQTTDASHBOARD = "tcp://broker.mqttdashboard.com:1883";
    public static final String BROKER_URL_LOCALHOST = "tcp://192.168.201.1:1883";

    protected MqttClient mqttClient;

    private MqttConnectOptions mqttOptions = new MqttConnectOptions();
    private String brokerUrl = null;
    private String clientid = null;
    protected boolean isConnected = false;

    public MqttPubSub(String brokerUrl, String clientId) {
        this(brokerUrl, clientId, "droid", "droid");
    }
    
    public MqttPubSub(String brokerUrl, String clientId, String user, String passwd) {
        this.brokerUrl = brokerUrl;
        this.clientid = clientId;
        this.isConnected = false;
        try {
            mqttClient = new MqttClient(brokerUrl, clientId);
            mqttOptions.setCleanSession(false);
            mqttOptions.setUserName(user);
            mqttOptions.setPassword(passwd.toCharArray());
            mqttOptions.setWill(mqttClient.getTopic("home/LWT"), "I'm gone".getBytes(), 2, true);
        } catch (MqttException e) {
            NetUtils.threadMessage("Caught exceptions: " + e.getMessage());
            System.exit(1);
        }

    }

    /**
     * To connect the MQTT broker, supported by this app.
     *
     * @throws MqttException
     */
    public void connectToBroker() throws MqttException {
        if (!this.isConnected) {
            mqttClient.connect(mqttOptions);
            NetUtils.threadMessage("Connected to " + BROKER_URL_LOCALHOST);
            this.isConnected = true;
        }
    }

    @Override
    public void connectionLost(Throwable thrwbl) {
        try {
            NetUtils.threadMessage("Broker connection lost! Will try to connect again...");
            this.isConnected = false;
            this.connectToBroker();
        } catch (MqttException ex) {
            NetUtils.threadMessage("Failed in re-connection, so exit");
            System.exit(1);
        }
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage mm) throws Exception {
        NetUtils.threadMessage("Message arrived with topic: " + topic + " message: " + mm);
    }
    
    @Override
    public void deliveryComplete(IMqttDeliveryToken imdt) {
        NetUtils.threadMessage("All messages received!");
    }
    

}
