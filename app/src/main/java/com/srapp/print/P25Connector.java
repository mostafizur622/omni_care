package com.srapp.print;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import com.srapp.TempData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;

/**
 * P25 printer connection class.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */
public class P25Connector {
	private BluetoothSocket mSocket;	
	private OutputStream mOutputStream;
	
	private ConnectTask mConnectTask;
	private P25ConnectionListener mListener;
	
	private boolean mIsConnecting = false;
	
	private static final String TAG = "P25";	
	private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
	public P25Connector(P25ConnectionListener listener) {
		mListener = listener;
	}
	
	public boolean isConnecting() {
		return mIsConnecting;
	}
	
	public boolean isConnected() {
		return (mSocket == null) ? false : true;
	}
	
	public void connect(BluetoothDevice device) throws P25ConnectionException {
		if (mIsConnecting && mConnectTask != null) {
			throw new P25ConnectionException("Connection in progress");
		}
		
		if (mSocket != null) {
			throw new P25ConnectionException("Socket already connected");
		}
		
		(mConnectTask = new ConnectTask(device)).execute();
	}
	
	public void disconnect() throws P25ConnectionException {
		if (mSocket == null) {
			throw new P25ConnectionException("Socket is not connected");
		}
		
		try {
			mSocket.close();
			
			mSocket = null;
			
			mListener.onDisconnected();
		} catch (IOException e) {
			throw new P25ConnectionException(e.getMessage());
		}
	}
	
	public void cancel() throws P25ConnectionException {
		if (mIsConnecting && mConnectTask != null) {
			mConnectTask.cancel(true);
		} else {
			throw new P25ConnectionException("No connection is in progress");
		}
	}
	
	public void sendData(byte[] msg) throws P25ConnectionException {
		if (mSocket == null) {
			throw new P25ConnectionException("Socket is not connected, try to call connect() first");
		}

		if(TempData.AllprintName.equals("AllprintName"))
		{
			try {
				Log.e("FINAL Byte:", ""+msg);
				Log.e("FINAL STRING:", new String(msg));
				byte[] format = { 27, 33, 0 };
				byte[] arrayOfByte1 = { 27, 33, 0 };


				format[2] = ((byte)(0x10 | arrayOfByte1[2]));
				format[2] = ((byte) (0x31 | arrayOfByte1[2]));
				format[2] = ((byte)(0x1 | arrayOfByte1[2]));

				String str	=TempData.AllprintValue;
				mOutputStream.write(format);
				mOutputStream.write(str.getBytes(),0,str.getBytes().length);
				mOutputStream.flush();

				Log.e("final Data", new String(msg));
			} catch(Exception e) {
				throw new P25ConnectionException(e.getMessage());
			}
		}
		else
		{
			try {
				Log.e("FINAL Byte:", ""+msg);
				Log.e("FINAL STRING:", new String(msg));
				byte[] format = { 27, 33, 0 };
				byte[] arrayOfByte1 = { 27, 33, 0 };

				format[2] = ((byte)(0x10 | arrayOfByte1[2]));
				format[2] = ((byte) (0x31 | arrayOfByte1[2]));
				format[2] = ((byte)(0x1 | arrayOfByte1[2]));


				String str56="";
				if(TempData.printName.equals("memo"))
					str56=TempData.printBlankContent;


//			String str	=TempData.printTopContent;
				mOutputStream.write(format);
				mOutputStream.write(str56.getBytes(),0,str56.getBytes().length);
				mOutputStream.flush();


				String str="";
				if(TempData.printName.equals("memo")) {
					str = TempData.printTopContent;

					mOutputStream.write(format);
					mOutputStream.write(str.getBytes(),0,str.getBytes().length);
					mOutputStream.flush();
					byte[] format5 = { 27, 33, 0 };
					byte[] arrayOfByte5 = { 27, 33, 0 };
					format5[2] = ((byte) (0x8 | arrayOfByte5[2]));
					format5[2] = ((byte)(0x10 | arrayOfByte5[2]));
					format5[2] = ((byte) (0x31 | arrayOfByte5[2]));
					format5[2] = ((byte) (0x1 | arrayOfByte5[2]));
					format5[2] = ((byte) (0x8 | arrayOfByte5[2]));
					String str2=TempData.PrintOutlate;
					Log.e("PrintOutlate",TempData.PrintOutlate);
					mOutputStream.write(format5);
					mOutputStream.write(str2.getBytes(),0,str2.getBytes().length);
					mOutputStream.flush();
					byte[] format6 = { 27, 33, 0 };
					byte[] arrayOfByte6 = { 27, 33, 0 };
					format6[2] = ((byte)(0x10 | arrayOfByte6[2]));
					format6[2] = ((byte) (0x31 | arrayOfByte6[2]));
					format6[2] = ((byte)(0x1 | arrayOfByte6[2]));
					String str3=TempData.PrintBody ;
					Log.e("PrintBody",TempData.PrintBody);
					mOutputStream.write(format6);
					mOutputStream.write(str3.getBytes(),0,str3.getBytes().length);
					mOutputStream.flush();


				}
				else {
					str = TempData.printTopContentProductReceive;
					mOutputStream.write(format);
					mOutputStream.write(str.getBytes(),0,str.getBytes().length);
					mOutputStream.flush();
				}

//			String str	=TempData.printTopContent;


				byte[] format1 = { 27, 33, 0 };
				byte[] arrayOfByte2 = { 27, 33, 0 };
				if(TempData.printName.equals("memo")) {
					//format1[2] = ((byte) (0x8 | format1[2]));
					//format1[2] = ((byte)(0x10 | arrayOfByte2[2]));
					//format1[2] = ((byte) (0x31 | arrayOfByte2[2]));
					//format1[2] = ((byte) (0x1 | format1[2]));

					/*format1[2] = ((byte)(0x10 | arrayOfByte2[2]));
					format1[2] = ((byte) (0x31 | arrayOfByte2[2]));
					format1[2] = ((byte)(0x02 | arrayOfByte2[2]));*/
					format1[0] = ((byte)(0x9 | arrayOfByte2[0]));

				}
				    else {
					format1[2] = ((byte)(0x10 | arrayOfByte2[2]));
					format1[2] = ((byte) (0x31 | arrayOfByte2[2]));
					format1[2] = ((byte)(0x1 | arrayOfByte2[2]));
				}

				String str1="";
				if(TempData.printName.equals("memo"))
					str1=TempData.printBottomContent;

				else if (TempData.printName.equalsIgnoreCase("so_stock")){
					str1=TempData.SO_Stock_print;
				}
				else
					str1=TempData.printBottonText;


//			String str1=TempData.printBottomContent;
				mOutputStream.write(format1);
				mOutputStream.write(str1.getBytes(),0,str1.getBytes().length);
				mOutputStream.flush();

                if(TempData.printboldthank.equals("thanku")&&TempData.printName.equals("memo")){
                    byte[] format3 = { 27, 33, 0 };
                    byte[] arrayOfByte3 = { 27, 33, 0 };
                    format3[2] = ((byte)(0x10 | arrayOfByte3[2]));
                    format3[2] = ((byte) (0x31 | arrayOfByte3[2]));
                    format3[2] = ((byte)(0x1 | arrayOfByte3[2]));

                     String  str3 = TempData.printThanku;

                    mOutputStream.write(format3);
                    mOutputStream.write(str3.getBytes(),0,str3.getBytes().length);
                    mOutputStream.flush();

                }


				Log.e("final Data", new String(msg));
			} catch(Exception e) {
				throw new P25ConnectionException(e.getMessage());
			}
		}

	}
	
	public interface P25ConnectionListener {
		public abstract void onStartConnecting();
		public abstract void onConnectionCancelled();
		public abstract void onConnectionSuccess();
		public abstract void onConnectionFailed(String error);
		public abstract void onDisconnected();
	}
	
	public class ConnectTask extends AsyncTask<URL, Integer, Long> {
		BluetoothDevice device;
		String error = "";
		
		public ConnectTask(BluetoothDevice device) {
			this.device = device;
		}
		
		protected void onCancelled() {
			mIsConnecting = false;
			
			mListener.onConnectionCancelled();
		}
		
    	protected void onPreExecute() {
    		mListener.onStartConnecting();
    		
    		mIsConnecting = true;
    	}
    
        protected Long doInBackground(URL... urls) {         
            long result = 0;
            
            try {
            	mSocket			= device.createRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID));
            	
            	mSocket.connect(); 
            	
            	mOutputStream	= mSocket.getOutputStream();
            	
            	result = 1;
            } catch (IOException e) { 
            	e.printStackTrace();
            	
            	error = e.getMessage();
            }
            
            return result;
        }

        protected void onProgressUpdate(Integer... progress) {              	
        }

        protected void onPostExecute(Long result) {        	
        	mIsConnecting = false;
        	
        	if (mSocket != null && result == 1) {
        		mListener.onConnectionSuccess();
        	} else {
        		mListener.onConnectionFailed("Connection failed " + error);
        	}
        }
    }
}