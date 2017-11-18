package com.combitracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityLogeo extends MainActivity {

    EditText txUsuario,txContraseña;
    Button btnCancelar,btnIngresar;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logeo);

        txUsuario=findViewById(R.id.txUsuario);
        txContraseña=findViewById(R.id.txContraseña);

        btnCancelar=findViewById(R.id.btnCancelar);
        btnIngresar=findViewById(R.id.btnIngresar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logear();
            }
        });

        databaseReference=firebaseDatabase.getReference("Rutas");
    }

    private void logear() {
        String usuario,contraseña;

        usuario=txUsuario.getText().toString();
        contraseña=txContraseña.getText().toString();

        if(!usuario.isEmpty() || !contraseña.isEmpty()){
            databaseReference.orderByChild("Administrador").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        String ruta = ds.getKey();
                        DataSnapshot credenciales = ds.child("Administrador");
                        String usuario = credenciales.child("Usuario").getValue().toString();
                        String contraseña = credenciales.child("Contraseña").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            if(usuario.isEmpty()){
                txUsuario.setError("Ingrese Usuario");
            }
            if(contraseña.isEmpty()){
                txContraseña.setError("Ingrese contraseña");
            }
        }
    }
}
