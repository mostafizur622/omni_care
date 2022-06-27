package com.srapp.print;

import android.util.Log;


public class Printer {
	
	public Printer(){}
	
	public static byte[] printfont (String content,byte fonttype,byte fontalign,byte linespace,byte language){
		
		if (content != null && content.length() > 0) {
			
			content = content + "\n";
			byte[] temp = null;
			Log.e("language", ""+language);
			Log.e("fonttype", ""+fonttype);
			Log.e("fontalign", ""+fontalign);
			Log.e("linespace", ""+linespace);
			temp = PocketPos.convertPrintData(content, 0,content.length(), language, fonttype,fontalign,linespace);
			
			return temp;
		}else{
			return null;
		}
	}
	
}