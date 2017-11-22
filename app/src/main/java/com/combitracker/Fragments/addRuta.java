package com.combitracker.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.combitracker.Objetos.Combi;
import com.combitracker.Objetos.Ruta;
import com.combitracker.Objetos.cooki;
import com.combitracker.Objetos.rutaAdapter;
import com.combitracker.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class addRuta extends Fragment {


    private OnFragmentInteractionListener mListener;

    private ArrayList<Ruta> rutas;
    public FirebaseDatabase BD;
    private DatabaseReference ref;

    private ListView elementos;
    private cooki sesion;

    public addRuta() {
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
        View v= inflater.inflate(R.layout.fragment_add_ruta, container, false);

        elementos=v.findViewById(R.id.listaRutas);
        rutas= new ArrayList<>();
        sesion=new cooki(getContext());


        BD=FirebaseDatabase.getInstance();
        try{
            BD.setPersistenceEnabled(true);
        }catch(Exception e){}


        ref=BD.getReference().child("Rutas");
        inflarRutas();


        elementos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {


                Ruta aux=rutas.get(i);
                mensajeEmergente("¿Qué opcion desea relizar a la ruta?");
                return false;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mListener.agregarRuta();

            }
        });


        return v;    }

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

        void agregarRuta();
    }

    private void mensajeEmergente(String title){
        AlertDialog.Builder dialog= new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setPositiveButton("ELIMINAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setNegativeButton("MODIFICAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.create();
        dialog.show();
    }
    private void inflarRutas() {
        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                for(DataSnapshot ds:snap.getChildren()){
                    rutas.add(ds.getValue(Ruta.class));
                }

                rutaAdapter cAdapter = new rutaAdapter(getContext(),rutas);
                elementos.setAdapter(cAdapter);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
