package com.combitracker.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.combitracker.Objetos.Combi;
import com.combitracker.Objetos.Ruta;
import com.combitracker.Objetos.cooki;
import com.combitracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AgregarCombi extends Fragment {


    private OnFragmentInteractionListener mListener;
    private TextInputLayout inUser,inNumero,inPass;
    private EditText user,number,pass;
    private Spinner spnRutas;
    private ImageButton save,cancel;
    private ArrayList<String> rutasSpinner= new ArrayList<>();

    private DatabaseReference ref;
    private cooki sesion;
    private FirebaseDatabase BD;

    private boolean add=true;
    protected String idUpdate;

    public AgregarCombi() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_agregar_combi, container, false);

        sesion= new cooki(getContext());

        inNumero=v.findViewById(R.id.ti_combi_numero);
        inUser=v.findViewById(R.id.ti_combi_usuario);
        inPass=v.findViewById(R.id.ti_combi_pass);

        user=v.findViewById(R.id.txt_combi_usuario);
        number=v.findViewById(R.id.txt_combi_numero);
        pass=v.findViewById(R.id.txt_combi_pass);

        save=v.findViewById(R.id.saveCombi);
        cancel=v.findViewById(R.id.cancelCombi);

        spnRutas=v.findViewById(R.id.spnRutas);

        BD=FirebaseDatabase.getInstance();
        try{
            BD.setPersistenceEnabled(true);
        }catch(Exception e){}
        ref=BD.getReference().child("Rutas");

        llenarSpinner();

        if(getArguments()==null){
                //AGREGAR
        }else{

            idUpdate=getArguments().getString("user");
            number.setText(getArguments().getString("number"));
            user.setText(idUpdate.substring(0,idUpdate.lastIndexOf("#")));
            idUpdate=idUpdate.substring(idUpdate.lastIndexOf("#")+1);
            pass.setText(getArguments().getString("pass"));
        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarCajas()){
                    if(getArguments()!=null){
                        actualizarCombi();
                    }else{
                        guardarCombi();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cerrarFragmrnt();
            }
        });


        return v;
    }

    private void actualizarCombi() {
        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Combis").child(idUpdate);

        Combi aux= new Combi();
        aux.setLat("0");
        aux.setLon("0");
        aux.setUsuario(user.getText().toString());
        aux.setContraseña(pass.getText().toString());
        aux.setNumero(number.getText().toString());
        aux.setRutaAsignada(spnRutas.getSelectedItem().toString());

        ref.setValue(aux).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Registro modificado exitosamente", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Ocurrio un error al modificar el registro", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void llenarSpinner() {

        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Ruta aux;
                for(DataSnapshot ds:snap.getChildren()){
                    aux=ds.getValue(Ruta.class);

                    rutasSpinner.add(aux.getRuta());

                }

                ArrayAdapter<String> mAdapter= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,rutasSpinner);
                spnRutas.setAdapter(mAdapter);

                for(int x=0;x<spnRutas.getCount();x++){
                    if(getArguments()!=null){
                        if(getArguments().getString("route").equalsIgnoreCase(spnRutas.getItemAtPosition(x).toString())){
                            spnRutas.setSelection(x);
                            break;
                        }
                    }else{
                        break;
                    }
                }
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void guardarCombi() {


        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Combis");


        Combi aux= new Combi();
        aux.setLat("0");
        aux.setLon("0");
        aux.setUsuario(user.getText().toString());
        aux.setContraseña(pass.getText().toString());
        aux.setNumero(number.getText().toString());
        aux.setRutaAsignada(spnRutas.getSelectedItem().toString());


        Log.i("TAGE",aux.toString());
        ref.push().setValue(aux).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Combi guardada exitosamente", Toast.LENGTH_SHORT).show();
                    limpiarCajas();

                }else{
                    Toast.makeText(getContext(), "Ocurrio un error al guardar los datos", Toast.LENGTH_SHORT).show();

                }
            }
        });




    }

    private void limpiarCajas() {
            user.setText("");
            number.setText("");
            pass.setText("");
    }

    private boolean validarCajas() {
        boolean valid=true;
        inUser.setErrorEnabled(false);
        inPass.setErrorEnabled(false);
        inNumero.setErrorEnabled(false);

        inUser.setError(null);
        inPass.setError(null);
        inNumero.setError(null);

        if(user.getText().toString().isEmpty()){
            valid=false;
            inUser.setError("Ingresa el usuario");
        }

        if(pass.getText().toString().isEmpty()){
            valid=false;
            inPass.setError("Ingresa la contraseña");
        }

        if(number.getText().toString().isEmpty()){
            valid=false;
            inNumero.setError("Ingresa el numero de la unidad");
        }

        return valid;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void cerrarFragmrnt();
    }


}
