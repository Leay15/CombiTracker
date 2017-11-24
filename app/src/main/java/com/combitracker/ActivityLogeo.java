package com.combitracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.combitracker.Objetos.cooki;
import com.combitracker.Objetos.redStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityLogeo extends AppCompatActivity  {

    EditText txUsuario,txContraseña;
    ImageView btnIngresar;
    private TextInputLayout inUser,inPass;

    private cooki sesion;

    DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private DatabaseReference ref;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logeo);

        txUsuario=findViewById(R.id.txUsuario);
        txContraseña=findViewById(R.id.txContraseña);
        inUser=findViewById(R.id.inUserLog);
        inPass=findViewById(R.id.inPassLog);

        auth=FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);

        btnIngresar=findViewById(R.id.btnIngresar);
        redStatus aux= new redStatus();
        if(aux.redStatus()){
            Log.i("TAGKK","SI");
        }else{
            Log.i("TAGKK","No");

        }

        ref=FirebaseDatabase.getInstance().getReference().child("Rutas");
        sesion=new cooki(this);



        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cajasVacias()){
                    logear();
                }
            }
        });

    }

    private boolean cajasVacias() {
        boolean valid=true;

        if(txUsuario.getText().toString().isEmpty()){
            txUsuario.setError("Ingresa tu correo");
            valid=false;
        }


        if(txContraseña.getText().toString().isEmpty()){
            txContraseña.setError("Ingresa tu contraseña");
            valid=false;
        }

        return valid;
    }

    private void logear() {
        progressDialog.setMessage("Iniciado sesión");
        progressDialog.show();



        final String email=txUsuario.getText().toString();
        final String pass=txContraseña.getText().toString();

        if(email.isEmpty()||pass.isEmpty()){
            //mostrar mensaje
        }else{
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (task.isSuccessful()) {

                        sesion.limpiarCooki();
                        sesion.setUserEmail(email);
                        sesion.setUserPass(txContraseña.getText().toString());

                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snap) {
                                for(DataSnapshot ds:snap.getChildren()){
                                    Log.i("TAGF", ds.child("Administrador").getValue().toString());
                                    Log.i("TAGF", ds.getKey());

                                    if(ds.child("Administrador").getValue().toString().contains(email)){
                                        sesion.setUserRuta( ds.getKey());
                                        break;
                                    }
                                }

                                ref.removeEventListener(this);
                                Intent mainA=new Intent(ActivityLogeo.this,MainActivity.class);
                                startActivity(mainA);
                                progressDialog.dismiss();
                                ActivityLogeo.this.finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                    }else{

                        progressDialog.dismiss();
                        if(task.getException().getMessage().contains("network")){
                            Toast.makeText(ActivityLogeo.this, "Verifica tu conexion a internet", Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(ActivityLogeo.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            });
        }


    }

}
