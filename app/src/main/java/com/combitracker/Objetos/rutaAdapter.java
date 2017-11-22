package com.combitracker.Objetos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.combitracker.R;

import java.util.ArrayList;

/**
 * Created by juamp on 21/11/2017.
 */

public class rutaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Ruta> lista;

    public rutaAdapter(Context context, ArrayList<Ruta> lista) {
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

        TextView ruta;

        LayoutInflater vista=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item=vista.inflate(R.layout.list_item_ruta,parent,false);



        ruta=item.findViewById(R.id.rutaName);
        ruta.setText("RUTA:    "+lista.get(position).getRuta());


        return item;
    }
}
