package com.combitracker.Objetos;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by juamp on 21/11/2017.
 */

public class cooki {
    private final String SHARED_PREFS_FILE = "NTAUser";
    private final String KEY_EMAIL = "KEY_EMAIL";
    private final String KEY_PASS = "KEY_PASS";
    private final String KEY_RUTA = "KEY_RUTA";
    private Context mContext;


    public cooki(Context mContext) {
        this.mContext = mContext;
    }

    private SharedPreferences getCookis(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }


    public String getUserEmail(){
        String email=getCookis().getString(KEY_EMAIL, "NA");
        return email;
    }

    public String getUserPass(){
        String pass=getCookis().getString(KEY_PASS, "NA");
        return pass;
    }


    public void setUserEmail(String email){
        SharedPreferences.Editor editor = getCookis().edit();
        editor.putString(KEY_EMAIL, email );
        editor.commit();
    }

    public void setUserPass(String pass){
        SharedPreferences.Editor editor = getCookis().edit();
        editor.putString(KEY_PASS,pass );
        editor.commit();
    }

    public String getUserRuta(){
        String ruta=getCookis().getString(KEY_RUTA, "NA");
        return ruta;
    }


    public void setUserRuta(String ruta){
        SharedPreferences.Editor editor = getCookis().edit();
        editor.putString(KEY_RUTA, ruta );
        editor.commit();
    }

    public void limpiarCooki(){
        SharedPreferences.Editor editor = getCookis().edit();
        editor.putString(KEY_EMAIL,"NA");
        editor.putString(KEY_PASS,"NA");
        editor.putString(KEY_RUTA,"NA");

        editor.commit();
    }

}
