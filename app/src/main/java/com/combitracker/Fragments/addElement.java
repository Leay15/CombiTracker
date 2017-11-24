package com.combitracker.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.style.IconMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.combitracker.MainActivity;
import com.combitracker.Objetos.Combi;
import com.combitracker.Objetos.Ruta;
import com.combitracker.Objetos.combiAdapter;
import com.combitracker.Objetos.cooki;
import com.combitracker.Objetos.rutaAdapter;
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


public class addElement extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ListView elementos;
    private cooki sesion;

    private ArrayList<Combi> listaCombis;
    private String opcion;


    public FirebaseDatabase BD;
    private DatabaseReference ref;
    protected String id;
    private TextView title;

    private combiAdapter cAdapter;
    private ImageView add;

    public addElement() {
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
        View v= inflater.inflate(R.layout.fragment_add_element, container, false);

        MainActivity.titulo.setText("Combis");

        elementos=v.findViewById(R.id.lstElementos);
        add=v.findViewById(R.id.fabC);
        sesion= new cooki(getContext());



        listaCombis= new ArrayList<>();

        BD=FirebaseDatabase.getInstance();

        cAdapter = new combiAdapter(getContext(),listaCombis);
        elementos.setAdapter(cAdapter);


        try{
            BD.setPersistenceEnabled(true);
        }catch(Exception e){}
        ref=BD.getReference().child("Rutas");
        inflarCombis();

        elementos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {


                    Combi aux=listaCombis.get(i);
                    mensajeEmergente("¿Qué opcion desea relizar a la combi?",i);

                return false;
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.agregarCombi();
            }
        });

        return v;
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

        void agregarCombi();
        void modificarCombi(Combi aux);

    }

    private void mensajeEmergente(String title,final int position){


        final AlertDialog dialog= new AlertDialog.Builder(getContext()).create();

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.mensaje_emergente, null);
        dialog.setView(dialogView);

        final TextView titulo =dialogView.findViewById(R.id.title_mensaje);
        final ImageButton cancel=dialogView.findViewById(R.id.btnSalir);
        final ImageButton modif=dialogView.findViewById(R.id.btnModificar);
        final ImageButton elim=dialogView.findViewById(R.id.btnEliminar);
        dialog.setCancelable(true);

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
                    id=listaCombis.get(position).getUsuario();
                    id=id.substring(id.lastIndexOf("#")+1);
                    ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Combis").child(id);

                    ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                listaCombis.remove(position);
                                cAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Elemento eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getContext(), "Ocurrio un error al eliminar el elemento", Toast.LENGTH_SHORT).show();

                            }
                            dialog.cancel();

                        }
                    });
                }
            });

               modif.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       mListener.modificarCombi(listaCombis.get(position));
                       dialog.cancel();

                   }
               });

        dialog.show();
    }


    private void inflarCombis() {
        ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Combis");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snap) {
                Combi aux;

                for(DataSnapshot ds:snap.getChildren()){
                    aux=ds.getValue(Combi.class);
                    aux.setUsuario(aux.getUsuario()+"#"+ds.getKey());
                    listaCombis.add(aux);
                }

               cAdapter.notifyDataSetChanged();
                ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
