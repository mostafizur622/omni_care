package com.srapp.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreference {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AuthPreference(Context context){
        sharedPreferences = context.getSharedPreferences(ConstantUtils.AdminPreference.FILE_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoginStatus(int value){
        editor.putInt(ConstantUtils.AdminPreference.STATUS_KEY,value);
        editor.commit();
    }

    public void setLoginStatusValue(boolean value){
        editor.putBoolean("stausValue",value);
        editor.commit();
    }

    public int isAdminLoggedIn(){
        return sharedPreferences.getInt(ConstantUtils.AdminPreference.STATUS_KEY,0);
    }

    public boolean getStatusValue(){
        return sharedPreferences.getBoolean("stausValue",false);
    }

    public void setCurrentDate(String value){
        editor.putString(ConstantUtils.AdminPreference.CurrentDate,value);
        editor.commit();
    }
    public String getCheckInDate(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.CurrentDate,"");
    }


    public void setCheckInStatus(String value){
        editor.putString(ConstantUtils.AdminPreference.AttendanceInStatus,value);
        editor.commit();
    }
    public void setInTime(String value){
        editor.putString(ConstantUtils.AdminPreference.InTime,value);
        editor.commit();
    }
    public void setOutTime(String value){
        editor.putString(ConstantUtils.AdminPreference.OutTime,value);
        editor.commit();
    }

    public String getInTime(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.InTime,"");
    }
    public String getOutTime(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.OutTime,"");
    }
    public void setCheckOutStatus(String value){
        editor.putString(ConstantUtils.AdminPreference.AttendanceOutStatus,value);
        editor.commit();
    }
    public String getCheckInStatus(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.AttendanceInStatus,"");
    }
    public String getCheckOutStatus(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.AttendanceOutStatus,"");
    }
    public void setInLat(String value){
        editor.putString(ConstantUtils.AdminPreference.inLat,value);
        editor.commit();
    }
    public void setInLong(String value){
        editor.putString(ConstantUtils.AdminPreference.inLong,value);
        editor.commit();
    }

    public void setOutLat(String value){
        editor.putString(ConstantUtils.AdminPreference.outLat,value);
        editor.commit();
    }
    public void setOutLong(String value){
        editor.putString(ConstantUtils.AdminPreference.outLong,value);
        editor.commit();
    }

    public String getInLat(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.inLat,"");
    }
    public String getInLong(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.inLong,"");
    }
    public String getOutLat(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.outLat,"");
    }
    public String getOutLong(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.outLong,"");
    }
    public void setDeviceTimeChange(String value){
        editor.putString(ConstantUtils.AdminPreference.deviceTimeChange,value);
        editor.commit();
    }
    public String getDeviceTimeChange(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.deviceTimeChange,"");
    }
    public void setPendingAttendance(String value){
        editor.putString(ConstantUtils.AdminPreference.pendingAttendance,value);
        editor.commit();
    }
    public String getPendingAttendance(){
        return sharedPreferences.getString(ConstantUtils.AdminPreference.pendingAttendance,"");
    }
    public void clearAllPreferences() {
        editor.clear(); // Clears all stored preferences
        editor.commit(); // Apply changes
    }
}
