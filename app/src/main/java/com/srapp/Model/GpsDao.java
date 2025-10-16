package com.srapp.Model;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.srapp.Db_Actions.DB_Helper;

public class GpsDao {

    private final SQLiteDatabase db;

    public GpsDao(Context ctx) {
        // Always application context
        Context app = ctx.getApplicationContext();
        DB_Helper helper = new DB_Helper(app);
        db = helper.getWritableDatabase();
    }

    public SQLiteDatabase db() {
        return db;
    }

    public void exec(String sql) {
        db.execSQL(sql);
    }

    public Cursor raw(String sql) {
        return db.rawQuery(sql, null);
    }
}
