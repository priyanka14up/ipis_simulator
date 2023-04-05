package com.innobit.simulator.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Util {
    public static String decimalToHex1(byte param) {

        // Integer.toHexString(param & 0xFF);
        String hex = Integer.toHexString(param & 0xFF).toUpperCase();
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return "0x" + hex;
    }

    public static String decimalToHex2(String param) {

        return "0x" + Integer.toHexString(Util.stringToInt(param));

    }

    public static String decimalToHex3(int param) {

        return "0x" + Integer.toHexString(param);

    }

    public static int stringToInt(String param) {

        return Integer.parseInt(param);

    }

    public static int hexToInt(String param) {

        return Integer.decode(param);

    }


    public static String getStatusForHexCode(String hex) {
        String packetHeaderForHexCode = "";
        if (hex.equals("0x01"))  packetHeaderForHexCode = "ARunning Right Time";
        else if (hex.equals("0x02"))  packetHeaderForHexCode = "AWill Arrive Shortly";
        else if (hex.equals("0x03"))  packetHeaderForHexCode = "AIs Arriving On";
        else if (hex.equals("0x04"))  packetHeaderForHexCode = "AHas Arrived On";
        else if (hex.equals("0x05"))  packetHeaderForHexCode = "ARunning Late";
        else if (hex.equals("0x06"))  packetHeaderForHexCode = "ACancelled";
        else if (hex.equals("0x07"))  packetHeaderForHexCode = "AIndefinite Late";
        else if (hex.equals("0x08"))  packetHeaderForHexCode = "ATerminated";
        else if (hex.equals("0x0A"))  packetHeaderForHexCode = "APlatform Change";
        else if (hex.equals("0x0B"))  packetHeaderForHexCode = "DRunning Right Time";
        else if (hex.equals("0x0C"))  packetHeaderForHexCode = "DCancelled";
        else if (hex.equals("0x0D"))  packetHeaderForHexCode = "DIs Ready To Leave";
        else if (hex.equals("0x0E"))  packetHeaderForHexCode = "DIs On Platform";
        else if (hex.equals("0x0F"))  packetHeaderForHexCode = "DHas Left";
        else if (hex.equals("0x10"))  packetHeaderForHexCode = "DRescheduled";
        else if (hex.equals("0x11"))  packetHeaderForHexCode = "DDiverted";
        else if (hex.equals("0x12"))  packetHeaderForHexCode = "DScheduled departure";
        else if (hex.equals("0x13"))  packetHeaderForHexCode = "DPlatform change";
        
        return packetHeaderForHexCode;
    }

    public static String getEffectForHexCode(String hex) {
        String effectForHexCode = "";
        if (hex.equals("0x00"))  effectForHexCode = "Reserved";
        else if(hex.equals("0x01"))  effectForHexCode = "Curtain Left to Right";
        else if (hex.equals("0x02"))  effectForHexCode = "Curtain Top to Bottom";
        else if (hex.equals("0x03"))  effectForHexCode = "Curtain Bottom to Top";
        else if (hex.equals("0x04"))  effectForHexCode = "Typing Left to Right";
        else if (hex.equals("0x05"))  effectForHexCode = "Running Right to Left";
        else if (hex.equals("0x06"))  effectForHexCode = "Running Top to Bottom";
        else if (hex.equals("0x07"))  effectForHexCode = "Running Bottom to Top";
        else if (hex.equals("0x08"))  effectForHexCode = "Flashing";
        else if (hex.equals("0x09"))  effectForHexCode = "Stable";
        
        return effectForHexCode;
    }
    public static String getSpeedForHexCode(String hex) {
        String effectForHexCode = "";
        if (hex.equals("0x00"))  effectForHexCode = "Lowest";
        else if(hex.equals("0x01"))  effectForHexCode = "Low";
        else if (hex.equals("0x02"))  effectForHexCode = "Medium";
        else if (hex.equals("0x03"))  effectForHexCode = "High";
        else if (hex.equals("0x04"))  effectForHexCode = "Highest";
        
        return effectForHexCode;
    }

    public static byte crc16ccitt(byte[] bytes, String resultType) {
        int crc = 0xFFFF;          // initial value
        final int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12)
        byte res = 0;

        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) {
                    crc ^= polynomial;
                   
                }
            }
        }
        System.out.println("crc"+crc);
        crc &= 0xffff; // 16 bits only
        final byte[] ret = new byte[2];
        ret[0] = (byte) (crc & 0xff);
        ret[1] = (byte) (crc >> 8 & 0xff); // little endian
        if (resultType.equals("MSB")) {
            res = ret[1];
        } else if (resultType.equals("LSB")) {
            res = ret[0];
        }

        return res;
    }

    public static int getSAMSB() {

        int temp = 0;

        try {
            InetAddress ip;
            String hostname;

            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            String ipstr = ip.toString();
            System.out.println("ip adresssssss" + ipstr);
            String ipstrarr[] = ipstr.split("/");
            System.out.println(ipstrarr);
            // ipADD=ipstrarr[1];

            String[] ipArray = ipstrarr[1].split("[, . ']+");

            temp = Util.hexToInt(decimalToHex2(ipArray[2]));

            System.out.println("third octet   =" + temp);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(temp + "temp 90");
        return temp;
    }

    public static int getSALSB() {

        int temp = 0;

        try {
            InetAddress ip;
            String hostname;

            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            String ipstr = ip.toString();
            System.out.println("ip adresssssss" + ipstr);
            String ipstrarr[] = ipstr.split("/");
            System.out.println(ipstrarr);
            // ipADD=ipstrarr[1];

            String[] ipArray = ipstrarr[1].split("[, . ']+");

            temp = Util.hexToInt(decimalToHex2(ipArray[3]));

            Util.hexToInt(decimalToHex2(ipArray[3]));
            System.out.println("fourth octet   =" + temp);

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(temp + "temp 148");

        return temp;

    }
}
