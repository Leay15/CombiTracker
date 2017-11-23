package com.combitracker.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.combitracker.Objetos.Combi;
import com.combitracker.Objetos.Ruta;
import com.combitracker.Objetos.cooki;
import com.combitracker.Objetos.rutaAdapter;
import com.combitracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    protected String id;
    private rutaAdapter rAdapter;


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


        rAdapter = new rutaAdapter(getContext(),rutas);
        elementos.setAdapter(rAdapter);

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
                mensajeEmergente("¿Qué opcion desea relizar a la ruta?",i);
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
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.botones)));



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

        void modificarRuta(Ruta ruta);
    }

    private void mensajeEmergente(String title,final int position){
        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.mensaje_emergente_ruta, null);
        dialog.setView(dialogView);

        final TextView titulo =dialogView.findViewById(R.id.title_mensaje);
        final ImageButton cancel=dialogView.findViewById(R.id.btnSalir);
        final ImageButton modif=dialogView.findViewById(R.id.btnModificar);
        final ImageButton elim=dialogView.findViewById(R.id.btnEliminar);

        titulo.setText(title);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });

        elim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=rutas.get(position).getRuta();
                id=id.substring(id.lastIndexOf("#")+1);
                ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas").child(id);

                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            rutas.remove(position);
                            rAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), "Registro eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getContext(), "Ocurrio un error al eliminar el registro", Toast.LENGTH_SHORT).show();

                        }
                        rAdapter.notifyDataSetChanged();

                        dialog.cancel();

                    }
                });
            }
        });

        modif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.modificarRuta(rutas.get(position));
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private void inflarRutas() {
        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Ruta aux;
                for(DataSnapshot ds:snap.getChildren()){
                    aux=ds.getValue(Ruta.class);
                    aux.setRuta(aux.getRuta()+"#"+ds.getKey());
                    rutas.add(aux);
                }

                rutaAdapter rAdapter = new rutaAdapter(getContext(),rutas);
                elementos.setAdapter(rAdapter);
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
