package com.blogspot.waptell.tafakari.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.blogspot.waptell.tafakari.R;

public class SharedPreferenceConfig {

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceConfig(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_preference), context.MODE_PRIVATE);
    }


    public void write_welcome_status(Boolean status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.app_welcome_preference), status);
        editor.commit();
    }

    public boolean readWelcomeStatus() {
        boolean status = false;
        status = sharedPreferences.getBoolean(context.getResources().getString(R.string.app_welcome_preference), false);
        return status;
    }

    public void addVerfInf(String phonenumber, String verfication_id){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phonenumber",phonenumber );
        editor.putString("verf_id", verfication_id);
        editor.commit();
    }

    public String readPhoneNumber(){

        String phonenumber = "";
        phonenumber = sharedPreferences.getString("phonenumber","");
        return phonenumber;
    }

    public String readTokenId(){

        String verf_id = "";
        verf_id = sharedPreferences.getString("verf_id","");
        return verf_id;
    }

    public void insertVerfiedUserPhonenumber(String phonenumber){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("verf_phonenumber",phonenumber );
        editor.commit();
    }

    public String verfiedUserPhonenumber(){
        String phonenumber = "";
        phonenumber = sharedPreferences.getString("verf_phonenumber","");
        return phonenumber;
    }

    public void insertUserDisplayName(String displayName){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("display_name",displayName );
        editor.commit();
    }

    public String getDisplayName(){
        String displayname = "";
        displayname = sharedPreferences.getString("display_name","");
        return displayname;
    }

    public void insertUserProfile(String userProfile){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_profile_image",userProfile );
        editor.commit();
    }

    public String getUserProfileUri(){
        String image_uri = "";
        image_uri = sharedPreferences.getString("User_profile_image","");
        return image_uri;
    }



}
