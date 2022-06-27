package com.srapp.print.newprint;

/**
 * Created by Administrator
 *
 * @author 猿史森林
 *         Date: 2017/10/14
 *         Class description:
 */
public class Constant {
    public static final String SERIALPORTPATH = "SerialPortPath";
    public static final String SERIALPORTBAUDRATE = "SerialPortBaudrate";
    public static final String WIFI_CONFIG_IP = "wifi config ip";
    public static final String WIFI_CONFIG_PORT = "wifi config port";
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final int BLUETOOTH_REQUEST_CODE = 0x001;
    public static final int USB_REQUEST_CODE = 0x002;
    public static final int WIFI_REQUEST_CODE = 0x003;
    public static final int SERIALPORT_REQUEST_CODE = 0x006;
    public static final int CONN_STATE_DISCONN = 0x007;
    public static final int MESSAGE_UPDATE_PARAMETER = 0x009;
    public static final int tip=0x010;
    public static final int abnormal_Disconnection=0x011;//异常断开
    public static final int PRINTER_STATUS=0x13;//打印机状态
    public static final int Connect_cuccess=0x14;//连接成功
    public static final int Connect_fail=0x15;//连接失败
    /**
     * wifi 默认ip
     */
    public static final String WIFI_DEFAULT_IP = "192.168.123.100";

    /**
     * wifi 默认端口号
     */
    public static final int WIFI_DEFAULT_PORT = 9100;
    public static  boolean encryptedConnection=true;//使用加密连接，蓝牙连接返回时自动回复秘钥
}