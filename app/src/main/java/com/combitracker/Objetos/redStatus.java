package com.combitracker.Objetos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.combitracker.ActivityLogeo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by juamp on 23/11/2017.
 */

public class redStatus extends AppCompatActivity{
    private static boolean valid;
   public boolean redStatus(){

       FirebaseAuth auth;
       FirebaseDatabase BD=FirebaseDatabase.getInstance();
       auth=FirebaseAuth.getInstance();

       auth.signInWithEmailAndPassword("mail@mail.com","123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                    valid=true;
               }else{
                   if(task.getException().getMessage().contains("network")){
                        valid=false;
                   }else{
                        valid=true;
                   }
               }

           }
       });

       return valid;
   }
}
