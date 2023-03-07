package com.srapp.Util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


import androidx.core.content.FileProvider;

import com.srapp.Db_Actions.Tables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import static com.srapp.Db_Actions.Tables.SR_ID;
import static com.srapp.Db_Actions.URL.EMAIL;

public class DatabaseBackManager {

    public static void setBackUp(Context context) {
        try {
            // File sd = Environment.getDataDirectory();
            File data = Environment.getDataDirectory();
            //File file = new File(context.getFilesDir(), filename);
//		    if (sd.mkdirs()) {
            String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + Tables.DATABASE_NAME;
            String backupDBPath = Tables.DATABASE_NAME;
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(context.getCacheDir().getPath(), backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
            // Toast.makeText(context, backupDB.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();
            setEmail(backupDB, context);
//		    }
        } catch (Exception e) {
            Log.e("log", e.getMessage());
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void setEmail(File backupDB, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, EMAIL);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SMC SR APP database backup");
        emailIntent.putExtra(Intent.EXTRA_TEXT,  "SPID : "+ prefs.getString(SR_ID, "NO PREFERENCE")
                +"\n"+"Name : "+prefs.getString("sr_name","0")
                +"\n"+"Username : "+prefs.getString("sr_uname","0")
                +"\n"+"password : "+prefs.getString("password","0")
        );
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + Tables.DATABASE_NAME));
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
    }


}
