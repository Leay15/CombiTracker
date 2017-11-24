package com.combitracker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.combitracker.Objetos.cooki;

public class SplashActivity extends AppCompatActivity {

        cooki sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sesion=new cooki(this);

        Log.i("tgy",sesion.getUserEmail());
        if(!sesion.getUserEmail().equalsIgnoreCase("NA")){
            Intent mainA=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(mainA);
            Log.i("tgy","si");

            SplashActivity.this.finish();
        }else{
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Log.i("tgy","no");


                    startActivity(new Intent(SplashActivity.this, ActivityLogeo.class));

                    finish();
                }
            }, 3000);
        }



    }
}
