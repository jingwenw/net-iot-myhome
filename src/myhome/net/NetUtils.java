/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myhome.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * The general networking utility functions and constants, most are static ones.
 * 
 * @author james
 */
public class NetUtils {

    public static enum AddressType {
        IPV4, IPV6, MAC
    };

    /**
     * Retrieve the IP or Mac address,
     *
     * @param addressType, 2 types supported so far: "ip" or "mac"
     * @return the address string
     */
    public static String getAddressByType(AddressType addressType) {
        String address = "";
        InetAddress lanIp = null;
        try {

            String ipAddress = null;
            Enumeration<NetworkInterface> net = null;
            net = NetworkInterface.getNetworkInterfaces();

            while (net.hasMoreElements()) {
                NetworkInterface element = net.nextElement();
                Enumeration<InetAddress> addresses = element.getInetAddresses();

                while (addresses.hasMoreElements() && !isVMMac(element.getHardwareAddress())) {
                    InetAddress ip = addresses.nextElement();
                    if (ip instanceof Inet4Address) {

                        if (ip.isSiteLocalAddress()) {
                            ipAddress = ip.getHostAddress();
                            lanIp = InetAddress.getByName(ipAddress);
                            break;
                        }

                    }

                }
            }

            if (lanIp == null) {
                return null;
            }

            switch (addressType) {
                case IPV4:
                    address = lanIp.toString().replaceAll("^/+", "");
                    break;
                case MAC:
                    address = getMacAddressByIp(lanIp);
                    break;
                default:
                    throw new Exception("Specify \"ipv4\" or \"mac\"");
            }

        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return address;

    }

    /**
     * To retrieve the mac address of localhost
     *
     * @param ip: the ip address to which the mac belongs
     * @return String: the mac address string.
     */
    public static final String getMacAddressByIp(InetAddress ip) {
        String macString = null;
        try {
            System.out.println("The IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();
            System.out.print("The associated MAC address : ");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            macString = sb.toString();
            System.out.println(sb.toString());

        } catch (SocketException e) {
            System.err.println("Caught exceptions: " + e.getMessage());
        }
        return macString;
    }

    private static boolean isVMMac(byte[] mac) {
        if (null == mac) {
            return false;
        }
        byte invalidMacs[][] = {
            {0x00, 0x05, 0x69}, //VMWare
            {0x00, 0x1C, 0x14}, //VMWare
            {0x00, 0x0C, 0x29}, //VMWare
            {0x00, 0x50, 0x56}, //VMWare
            {0x08, 0x00, 0x27}, //Virtualbox
            {0x0A, 0x00, 0x27}, //Virtualbox
            {0x00, 0x03, (byte) 0xFF}, //Virtual-PC
            {0x00, 0x15, 0x5D} //Hyper-V
        };

        for (byte[] invalid : invalidMacs) {
            if (invalid[0] == mac[0] && invalid[1] == mac[1] && invalid[2] == mac[2]) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Print out the message with thread name and timestamp preceded.
     * @param message 
     */
    public static final void threadMessage(String message) {
        String tname = Thread.currentThread().getName();
        String tstamp = new SimpleDateFormat("HH:mm:ss MMM.dd").format(new Date());
        System.out.format("[%s %s]:: %s%n", tstamp, tname, message);
    }
    
}
