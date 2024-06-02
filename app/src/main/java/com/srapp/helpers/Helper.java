package com.srapp.helpers;

import android.content.Context;

import com.srapp.Db_Actions.Data_Source;

/******
 **** Created By  TANVIR3488 AT 1/6/24 12:06 AM
 ******/


public class Helper {
    Data_Source db;
    Context context;
    public Helper(Context context) {
        this.db = new Data_Source(context);
        this.context = context;
    }
}
