package com.srapp.print.newprint;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Administrator
 *
 * @author 猿史森林
 *         Date: 2017/11/30
 *         Class description:
 */
public class Utils {

    private static Toast toast;

    public static UsbDevice getUsbDeviceFromName(Context context, String usbName) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> usbDeviceList = usbManager.getDeviceList();
        return usbDeviceList.get(usbName);
    }

    public static void toast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
    /**
     * Byte类型转十六进制String类型
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * 分包发送
     * @param bytes  总的数据大小
     * @param counts 每包字节数
     * @return
     */
    public static List<byte[]> getListByteArray(byte[] bytes, int counts) {
        List<byte[]> lists = new ArrayList();
        int f = bytes.length / counts;
        int length = 0;

        for(int i = 0; i < f; ++i) {
            byte[] bbb = new byte[counts];

            for(int j = 0; j < counts; ++j) {
                bbb[j] = bytes[j + i * counts];
            }

            length += bbb.length;
            lists.add(bbb);
        }

        if (length < bytes.length) {
            byte[] a = new byte[bytes.length - length];

            for(int i = 0; i < bytes.length - length; ++i) {
                a[i] = bytes[length + i];
            }

            lists.add(a);
        }

        return lists;
    }

    /**
     * Vector [] 转 byte
     * @param data
     * @return
     */
    public static byte[] convertVectorByteToBytes(Vector<Byte> data) {
        byte[] sendData = new byte[data.size()];
        if (data.size() > 0) {
            for(int i = 0; i < data.size(); ++i) {
                sendData[i] = (Byte)data.get(i);
            }
        }

        return sendData;
    }

    /**
     * byte [] 转 Vector
     * @param data
     * @return
     */
    public static Vector<Byte> sendbyteDataImmediately(final byte [] data) {
            Vector<Byte> datas=new Vector<Byte>();
            for(int i = 0; i < data.length; ++i) {
                datas.add(Byte.valueOf(data[i]));
            }
            return datas;

    }
}
