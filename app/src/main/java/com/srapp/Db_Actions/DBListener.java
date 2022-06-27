package com.srapp.Db_Actions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public interface DBListener {
    public void    OnLocalDBdataRetrive(String json) throws JSONException;
    public void  OnLocalDBdataRetrive(ArrayList<HashMap<String, String>> arrayList);
    public void  OnLocalDBdataRetrive(HashMap<String, String> hasmap);
}
