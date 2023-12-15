package com.srapp;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JsonParse {
	static String  JSONResponse=""; 
	static InputStream inputStream = null;
	String result = "";


	public static String makeServiceCall(String url, String MyJson) {

		try
		{

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			StringEntity se = new StringEntity(MyJson);
			httpPost.setEntity(se);


			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null)
			{
				JSONResponse = convertInputStreamToString(inputStream);
			}

			else
				JSONResponse = "Did not work!";


			//Log.e("base_msg",url+" \n "+MyJson+" \n "+JSONResponse);
		}
		catch (Exception e) 
		{
			Log.d("InputStream", e.getLocalizedMessage());
		}

		Log.e("ApiDetails","url"+url+"\n Json="+MyJson+"\n response="+JSONResponse);
		return JSONResponse;

	}

	
	public static String makeServiceCall(String url) {

		try
		{

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
//			StringEntity se = new StringEntity();
			httpPost.setEntity(null);


			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");


			HttpResponse httpResponse = httpclient.execute(httpPost);
			inputStream = httpResponse.getEntity().getContent();

			if(inputStream != null)
			{
				JSONResponse = convertInputStreamToString(inputStream);
			}

			else
				JSONResponse = "Did not work!";

		}
		catch (Exception e) 
		{
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return JSONResponse;

	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}   

public static String fetchJSONByUrl(String url) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("Content-type", "application/json");
		
		Log.e("Url", url);
			
		try
		{
		 HttpResponse response = httpclient.execute(httppost);
			 
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
		}catch(Exception e){}
		
		return "NO RESPONSE";
	}


	public static String fetchJSON(String url){   

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);

			}
		} catch (ClientProtocolException e) {  } 
		catch (IOException e) { }

		return "";
	}
}
