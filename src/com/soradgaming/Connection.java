package com.soradgaming;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Connection {
    private String m_strPassword_Normal = "12345678";
    private String m_strPassword_Default = "12345678";
    private String m_strSSID_Normal = "iMars-WiFi";
    private String m_strSSID_Default = "iMars-WiFi";
    private String m_strEncryptType_Normal = "none";
    private String m_strEncryptType_Default = "none";
    private int m_iNetMode_Backup;
    private String m_strWifiConfig_Default;
    private int m_iNetMode;
    private String m_strWifiConfig_Default_Backup;
    private String m_strSerialBaud = "9600";
    private String m_strNetworkMode;
    private String m_strRemoteIP;
    private String m_strRemotePort;
    private String m_strWifiConfig_Normal;
    private String m_strWifiConfig_Normal_Backup;
    private boolean m_bDoing;
    private String m_strVer;
    private String m_strUnknown;
    private String m_strModuleID;
    private String m_strResID;
    private int m_iHttpMethod;

    public Connection() {
        this.m_iNetMode = 0;

        String encoder = "UTF-8";

        Map<String, String> params = httpReadAll();

        String result = sendPostMessage(params, encoder);

        httpResult(result);

        Map<String, String> paramsDone = httpWriteAll();

        this.m_iNetMode = 0; // Maybe determines the Data State

        String finalResult = sendPostMessage(paramsDone, encoder);

        System.out.println(finalResult);

    }

    public void httpResult(String result) {
        //System.out.println("http" + result);
        this.m_bDoing = false;
        if (this.m_iHttpMethod == 0) {
            if (result == null || result.equals("")) {
                this.m_strVer = this.m_strUnknown;
                this.m_strSerialBaud = this.m_strUnknown;
                this.m_strNetworkMode = this.m_strUnknown;
                this.m_strRemoteIP = this.m_strUnknown;
                this.m_strRemotePort = this.m_strUnknown;
                this.m_strModuleID = this.m_strUnknown;
                this.m_strResID = this.m_strUnknown;
                this.m_strSSID_Default = this.m_strUnknown;
                this.m_strEncryptType_Default = this.m_strUnknown;
                this.m_strPassword_Default = this.m_strUnknown;
                this.m_strWifiConfig_Default = this.m_strUnknown;
                this.m_strWifiConfig_Default_Backup = this.m_strWifiConfig_Default;
                this.m_strSSID_Normal = this.m_strUnknown;
                this.m_strEncryptType_Normal = this.m_strUnknown;
                this.m_strPassword_Normal = this.m_strUnknown;
                this.m_strWifiConfig_Normal = this.m_strUnknown;
                this.m_iNetMode = 0;
                this.m_iNetMode_Backup = this.m_iNetMode;
                this.m_strWifiConfig_Normal_Backup = this.m_strWifiConfig_Normal;
//                showValue();
                return;
            }
            String[] list = result.split("\n");
            this.m_strVer = getValue(list, "at+ver");
            try {
                this.m_iNetMode = Integer.parseInt(getValue(list, "at+netmode"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                this.m_iNetMode = 0;
            }
            this.m_strNetworkMode = getValue(list, "at+mode");
            this.m_strRemoteIP = getValue(list, "at+remoteip");
            this.m_strRemotePort = getValue(list, "at+remoteport");
            this.m_strModuleID = getValue(list, "at+cusid0");
            this.m_strResID = getValue(list, "at+cusid1");
            String[] tmp = getValue(list, "at+uart").split(",");
            if (tmp.length == 4) {
                this.m_strSerialBaud = tmp[0];
            } else {
                this.m_strSerialBaud = "9600";
            }
            String[] tmp2 = getValue(list, "at+RapWIFI").split(",");
            if (tmp2.length == 3) {
                this.m_strSSID_Default = tmp2[0];
                this.m_strEncryptType_Default = tmp2[1];
                this.m_strPassword_Default = tmp2[2];
            } else if (tmp2.length == 2) {
                this.m_strSSID_Default = tmp2[0];
                this.m_strEncryptType_Default = tmp2[1];
            } else if (tmp2.length == 1) {
                this.m_strSSID_Default = tmp2[0];
            }
            String[] tmp3 = getValue(list, "at+wifi_conf").split(",");
            if (tmp3.length == 3) {
                this.m_strSSID_Normal = tmp3[0];
                this.m_strEncryptType_Normal = tmp3[1];
                this.m_strPassword_Normal = tmp3[2];
            } else if (tmp3.length == 2) {
                this.m_strSSID_Normal = tmp3[0];
                this.m_strEncryptType_Normal = tmp3[1];
            } else if (tmp3.length == 1) {
                this.m_strSSID_Normal = tmp3[0];
            }
            this.m_strWifiConfig_Default = this.m_strSSID_Default + "," + this.m_strEncryptType_Default + "," + this.m_strPassword_Default;
            this.m_strWifiConfig_Normal = this.m_strSSID_Normal + "," + this.m_strEncryptType_Normal + "," + this.m_strPassword_Normal;
            this.m_iNetMode_Backup = this.m_iNetMode;
            this.m_strWifiConfig_Default_Backup = this.m_strWifiConfig_Default;
            this.m_strWifiConfig_Normal_Backup = this.m_strWifiConfig_Normal;
//            showValue();
        } else if (this.m_iHttpMethod == 1) {
            try {
                httpReadAll();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else if (this.m_iHttpMethod == 2 && result != null && !result.equals("")) {
//            this.m_listRouter.clear();
//            getRouter(result.split("\n"), "at+wifi_Scan");
//            myDialogSingleChoice(this.m_context, 1);
            System.out.println("WTF BRO");
        }
    }

    public static String sendPostMessage(Map<String, String> params, String encode) {
        StringBuilder stringBuilder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    stringBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://192.168.16.254/goform/ser2netconfigAT").openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                byte[] myData = stringBuilder.toString().getBytes();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(myData.length));
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(myData, 0, myData.length);
                outputStream.close();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    System.out.println("responseCode: " + responseCode);
                    return changeInputStream(urlConnection.getInputStream(), encode);
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return "";
    }

    private static String changeInputStream(InputStream inputStream, String encode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        if (inputStream == null) {
            return null;
        }
        while (true) {
            try {
                int len = inputStream.read(data);
                System.out.println("Data: " + Arrays.toString(data));
                if (len == -1) {
                    return new String(byteArrayOutputStream.toByteArray(), encode);
                }
                byteArrayOutputStream.write(data, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String getValue(String[] list, String key) {
        String ret = "";
        for (String str : list) {
            if (str.indexOf(key) != -1) {
                System.out.println("WiFiSettingActivity" + str);
                ret = "";
                String[] tmp = str.split("[?]");
                if (tmp.length >= 2) {
                    String str2 = tmp[1];
                    int len = str2.length();
                    Boolean start = false;
                    int j = 0;
                    while (j < len && str2.charAt(j) != 13) {
                        if (start.booleanValue() || str2.charAt(j) != ' ') {
                            start = true;
                            ret = ret + str2.charAt(j);
                        }
                        j++;
                    }
                }
            }
        }
        return ret;
    }

    public static Map<String, String> httpReadAll() {
        Map<String, String> param = new HashMap<>();
        param.put("netmode", "?");
        param.put("wifi_conf", "?");
        param.put("RapWIFI", "?");
        param.put("uart", "?");
        param.put("mode", "?");
        param.put("remoteip", "?");
        param.put("remoteport", "?");
        param.put("cusid0", "?");
        param.put("cusid1", "?");
        param.put("ver", "?");
        return param;
    }

    public Map<String, String> httpWriteAll() {
        Map<String, String> param = new HashMap<>();
        if (this.m_iNetMode == 0) {
            if (this.m_iNetMode != this.m_iNetMode_Backup || !this.m_strWifiConfig_Default.equals(this.m_strWifiConfig_Default_Backup)) {
                param.put("netmode", Integer.toString(this.m_iNetMode));
                param.put("wifi_conf", this.m_strWifiConfig_Default);
                param.put("dhcpc", "1");
                param.put("dhcpd", "1");
                param.put("dhcpd_ip", "192.168.16.100,192.168.16.200,255.255.255.0,192.168.16.254");
                param.put("dhcpd_dns", "192.168.16.254,8.8.8.8");
                param.put("net_ip", "192.168.16.254,255.255.255.0,192.168.16.254");
                param.put("net_dns", "192.168.16.254,8.8.8.8");
                param.put("uart", this.m_strSerialBaud + ",8,n,1");
                param.put("mode", this.m_strNetworkMode);
                param.put("remoteip", this.m_strRemoteIP);
                param.put("remoteport", this.m_strRemotePort);
                param.put("RapWIFI", this.m_strWifiConfig_Default);
                param.put("RCommit", "1");
                param.put("net_commit", "1");
                param.put("reconn", "1");
            } else {
                param.put("netmode", Integer.toString(this.m_iNetMode));
                param.put("RapWIFI", this.m_strWifiConfig_Default);
                param.put("uart", this.m_strSerialBaud + ",8,n,1");
                param.put("mode", this.m_strNetworkMode);
                param.put("remoteip", this.m_strRemoteIP);
                param.put("remoteport", this.m_strRemotePort);
                param.put("save", "1");
            }
        }
        if (this.m_iNetMode != 0) {
            if (this.m_iNetMode == this.m_iNetMode_Backup && this.m_strWifiConfig_Normal.equals(this.m_strWifiConfig_Normal_Backup)) {
                param.put("netmode", Integer.toString(this.m_iNetMode));
                param.put("wifi_conf", this.m_strWifiConfig_Normal);
                param.put("uart", this.m_strSerialBaud + ",8,n,1");
                param.put("mode", this.m_strNetworkMode);
                param.put("remoteip", this.m_strRemoteIP);
                param.put("remoteport", this.m_strRemotePort);
                param.put("save", "1");
            } else if (this.m_iNetMode == 2) {
                param.put("netmode", Integer.toString(this.m_iNetMode));
                param.put("wifi_conf", this.m_strWifiConfig_Normal);
                param.put("dhcpc", "1");
                param.put("uart", this.m_strSerialBaud + ",8,n,1");
                param.put("mode", this.m_strNetworkMode);
                param.put("remoteip", this.m_strRemoteIP);
                param.put("remoteport", this.m_strRemotePort);
                param.put("net_commit", "1");
                param.put("reconn", "1");
            } else if (this.m_iNetMode == 3) {
                param.put("netmode", Integer.toString(this.m_iNetMode));
                param.put("wifi_conf", this.m_strWifiConfig_Normal);
                param.put("dhcpc", "1");
                param.put("dhcpd", "1");
                param.put("dhcpd_ip", "192.168.16.100,192.168.16.200,255.255.255.0,192.168.16.254");
                param.put("dhcpd_dns", "192.168.16.254,8.8.8.8");
                param.put("net_ip", "192.168.16.254,255.255.255.0,192.168.16.254");
                param.put("net_dns", "192.168.16.254,8.8.8.8");
                param.put("uart", this.m_strSerialBaud + ",8,n,1");
                param.put("mode", this.m_strNetworkMode);
                param.put("remoteip", this.m_strRemoteIP);
                param.put("remoteport", this.m_strRemotePort);
                param.put("net_commit", "1");
                param.put("reconn", "1");
            }
        }
        return param;
    }
}