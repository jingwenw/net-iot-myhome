/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhome;

import myhome.m2m.HomeSubscriber;
import myhome.m2m.MqttPubSub;
import myhome.m2m.TempPublisher;
import myhome.net.NetUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author james
 */
public class MyHome {

    private static void myHome() throws InterruptedException {
        TempPublisher publisher;
        long patience = 1000 * 60; //1 minute

        publisher = new TempPublisher(MqttPubSub.BROKER_URL_LOCALHOST);
        Thread t = new Thread(publisher);
        long startTime = System.currentTimeMillis();
        t.start();

        // the main loop
        while (t.isAlive()) {
            NetUtils.threadMessage("Waiting...");
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {
                NetUtils.threadMessage("Tired of waiting - will interrupt...");
                t.interrupt();
                t.join();
                NetUtils.threadMessage("Good, children exited.");
                break;
            }
        }
        NetUtils.threadMessage("Done - going to exit");
        System.exit(0);

    }

    private static void myHomeCenter() throws MqttException, InterruptedException {
        HomeSubscriber sub = new HomeSubscriber(MqttPubSub.BROKER_URL_LOCALHOST);

        NetUtils.threadMessage("Going to subscribe temp...");
        sub.subcribe(TempPublisher.TOPIC_TEMPERATURE);
        NetUtils.threadMessage("Subscribed topic of " + TempPublisher.TOPIC_TEMPERATURE);
        sub.subcribe(TempPublisher.TOPIC_BRIGHTNESS);
        NetUtils.threadMessage("Subscribed topic of " + TempPublisher.TOPIC_BRIGHTNESS);

        // Waiting in main loop
        while (true) {
            NetUtils.threadMessage("Waiting for subscribed messages...");
            Thread.sleep(TempPublisher.INTERVAL);
        }

    }

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException, MqttException {
        Options options = new Options();

        Option mode = new Option("s", "subscribe", false, "subscribe the topics so acts as the home center");
        mode.setRequired(false);
        options.addOption(mode);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("MyHome IoT", options);

            System.exit(1);
            return;
        }

        if (cmd.hasOption('s')) {
            myHomeCenter();
        } else {
            myHome();
        }
    }

}
