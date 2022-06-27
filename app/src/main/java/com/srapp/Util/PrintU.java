package com.srapp.Util;

import static com.srapp.print.newprint.Constant.CONN_STATE_DISCONN;
import static com.srapp.print.newprint.Constant.Connect_cuccess;
import static com.srapp.print.newprint.Constant.Connect_fail;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gprinter.io.PortManager;
import com.gprinter.io.UsbPort;
import com.srapp.print.PrintActivity;
import com.srapp.print.newprint.Constant;
import com.srapp.print.newprint.PrintContent;
import com.srapp.print.newprint.ThreadPool;

import java.io.IOException;

/**
 * Creadted BY tanvir3488 on 6/27/2022.
 */
public class PrintU {

    PortManager portManager=null;
     UsbDevice mUsbDevice =null;
    private ThreadPool threadPool =ThreadPool.getInstantiation();
    Bitmap bitmap;
    Context context;


    public PrintU(UsbDevice mUsbDevice, Bitmap bitmap, Context context) {
        this.mUsbDevice = mUsbDevice;
        this.context = context;
        connect();
    }

    public void connect (){
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                portManager=new UsbPort(context, mUsbDevice);//实例化对象
                boolean result=portManager.openPort();//连接端口 成功返回true 失败返回 false
                mHandler.obtainMessage(result?Constant.Connect_cuccess:Constant.Connect_fail).sendToTarget();
            }
        });

    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        btnPhotoTest();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN:

                    break;
                case Connect_cuccess:
                    Log.e("status","Connect");
                  //  btnPhotoTest();
                    break;
                case Connect_fail:
                    Log.e("status","Connect_fail");
                    break;
                case Constant.tip:

                    break;
                case Constant.PRINTER_STATUS:

                    break;
                case Constant.MESSAGE_UPDATE_PARAMETER://wifi或以太网连接

                    break;




            }
        }
    };


    public void btnPhotoTest() {
        if (portManager== null) {
            boolean bool = portManager==null;
            // Utils.toast(this, getString(R.string.str_cann_printer));
            Log.e("portManager",String.valueOf(bool));
            return;
        }
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success=portManager.writeDataImmediately(PrintContent.getPhoto(bitmap));
                   if (success)
                    btnCutTest();

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                        }
                    },1000);
                } catch (IOException e) {
                    mHandler.obtainMessage(Constant.CONN_STATE_DISCONN).sendToTarget();
                }
            }
        });
    }

    public void btnCutTest() {
        if (portManager== null) {
            // Utils.toast(this, getString(R.string.str_cann_printer));
            return;
        }
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success=portManager.writeDataImmediately(PrintContent.openCut());
                  /*  Log.e(TAG,"发送内容：\r\n"+Utils.bytesToHexString(Utils.convertVectorByteToBytes(PrintContent.openCut())));//成功返回true  失败返回false
                    Log.e(TAG,"发送结果："+success);*///成功返回true  失败返回false
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                        }
                    },100);
                } catch (IOException e) {
                    mHandler.obtainMessage(Constant.CONN_STATE_DISCONN).sendToTarget();
                }
            }
        });
    }



}
