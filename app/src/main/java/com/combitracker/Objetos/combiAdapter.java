package com.combitracker.Objetos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.combitracker.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by juamp on 21/11/2017.
 */

public class combiAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Combi> lista;
    protected  String num;

    public combiAdapter(Context context, ArrayList<Combi> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView number,rta,user,pass;

        LayoutInflater vista=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=vista.inflate(R.layout.list_item,parent,false);


        number=item.findViewById(R.id.combiNo);
        rta=item.findViewById(R.id.combiRta);
        user=item.findViewById(R.id.combiUser);
        pass=item.findViewById(R.id.combiPass);


        num=lista.get(position).getUsuario();
        num=num.substring(0,num.lastIndexOf("#"));
        number.setText("NÚMERO:     "+lista.get(position).getNumero());
        rta.setText("RUTA:       "+lista.get(position).getRutaAsignada());
        user.setText("USUARIO:    "+num);
        pass.setText("CONTRASEÑA: "+lista.get(position).getContraseña());

        return item;
    }
}
