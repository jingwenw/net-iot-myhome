/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhome.m2m;

import myhome.net.NetUtils;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author james
 */
public class HomeSubscriber extends MqttPubSub {

    private static final String clientId = NetUtils.getAddressByType(NetUtils.AddressType.MAC) + "-sub";
    
    public HomeSubscriber(String brokerUrl) {
        super(brokerUrl, clientId);
    }
    
    public void subcribe(String topic) throws MqttException {
        this.mqttClient.setCallback(this);
        this.connectToBroker();
        this.mqttClient.subscribe(topic);
    }
}
