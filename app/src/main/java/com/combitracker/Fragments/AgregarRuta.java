package com.combitracker.Fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.combitracker.Objetos.DirectionsJSONParser;
import com.combitracker.Objetos.Ruta;
import com.combitracker.Objetos.cooki;
import com.combitracker.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgregarRuta extends Fragment implements GoogleMap.OnMarkerDragListener{

    private OnFragmentInteractionListener mListener;
    MapView mMapView;
    private GoogleMap googleMap;

    ArrayList<LatLng> coordenadas = new ArrayList<>();
    ArrayList<Marker> marcadores = new ArrayList<>();
    LatLng markInicial=null,markFinal=null;
    private cooki sesion;
    private String rutaFinal;
    private EditText nombre;
    private Polyline lineAux;

    private String ruta,id,camino;
    private String color="";

    public AgregarRuta() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragme0nt
        View v= inflater.inflate(R.layout.fragment_agregar_ruta, container, false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);



        sesion= new cooki(getContext());
        nombre=v.findViewById(R.id.txtNombreRuta);


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if(getArguments()!=null){
                    ruta=getArguments().getString("ruta");
                    id=getArguments().getString("id");
                    camino=getArguments().getString("camino");

                    Log.i("tag3",ruta+"/"+id+"/"+camino);

                    nombre.setText(ruta);
                    pintarRuta(camino.split(","));
                }
                googleMap.setOnMarkerDragListener(AgregarRuta.this);
                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        googleMap.clear();
                        if(markFinal==null){
                            markFinal=latLng;
                            Marker m = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
                            coordenadas.add(latLng);
                        }else{
                            markInicial=markFinal;
                            markFinal=latLng;
                            String url = obtenerDireccionesURL(markInicial,markFinal);
                            DownloadTask downloadTask = new DownloadTask();
                            downloadTask.execute(url);
                        }
                    }
                });


                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(19.6872175, -100.5586839);

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });


        FloatingActionButton agregar = (FloatingActionButton) v.findViewById(R.id.fabAdd);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validRuta()){
                       if(getArguments()!=null){
                           actualizarRuta();
                       }else{
                           guardarRuta();
                       }
                }

            }
        });
        agregar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(100,200,100)));


        FloatingActionButton cancelar = (FloatingActionButton) v.findViewById(R.id.fabCancel);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cerrarFragmrnt();

            }
        });

        return v;
    }

    private void actualizarRuta() {
        FirebaseDatabase BD =FirebaseDatabase.getInstance();
        DatabaseReference ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas").child(id);

        rutaFinal="";
        for(int y=0;y<coordenadas.size();y++){
            rutaFinal+=coordenadas.get(y).latitude+","+coordenadas.get(y).longitude+",";
        }

        Ruta route= new Ruta();
        route.setRuta(nombre.getText().toString());
        route.setCamino(rutaFinal.substring(0,rutaFinal.length()-2));

        ref.setValue(route).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Ruta "+nombre.getText()+" modifocada exitosamente", Toast.LENGTH_SHORT).show();
                    mListener.cerrarFragmrnt();

                }else{
                    Toast.makeText(getContext(), "Ocurrio un error al modificar la ruta", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void pintarRuta(String[] camino) {


        limpiarRutaAnterior();

        PolylineOptions line= new PolylineOptions();

            for (int x = 1; x < camino.length; x +=2) {

                if(x<camino.length){
                    double lat = Double.parseDouble(camino[x - 1]);
                    double lng = Double.parseDouble(camino[x]);

                    LatLng position = new LatLng(lat, lng);
                    coordenadas.add(position);
                    Log.i("points",position.toString());
                }


            }




        line.addAll(coordenadas);
        line.width(10);
        line.color(Color.rgb(0,0,255));


        if(line!=null){
            agregarMarcadoresUpdate();
            lineAux=googleMap.addPolyline(line);
        }






    }

    private void limpiarRutaAnterior() {

        if(lineAux!=null){

            lineAux.remove();
        }
        coordenadas.clear();



    }

    private void guardarRuta() {
        FirebaseDatabase BD =FirebaseDatabase.getInstance();
        DatabaseReference ref=BD.getReference().child("Rutas").child(sesion.getUserRuta()).child("Subrutas");


        Ruta route= new Ruta();
        route.setRuta(nombre.getText().toString());
        route.setCamino(rutaFinal.substring(0,rutaFinal.length()-2));

        ref.push().setValue(route).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(), "Ruta "+nombre.getText()+" guardada exitosamente", Toast.LENGTH_SHORT).show();
                    mListener.cerrarFragmrnt();

                }else{
                    Toast.makeText(getContext(), "Ocurrio un error al guardar la ruta", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean validRuta() {
        boolean valida=true;
        rutaFinal="";
        if(coordenadas.size()==0){
            valida=false;
        }else{
            if(nombre.getText().toString().isEmpty()){
                valida=false;
                nombre.setError("Campo obligatorio");
            }else{
                for(int x=0;x<coordenadas.size();x++){
                    rutaFinal+=coordenadas.get(x).latitude+","+coordenadas.get(x).longitude+",";
                }
            }

        }



        return valida;
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

    @Override
    public void onMarkerDragStart(Marker marker) {
        cordAux= marker;

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }
    Marker cordAux;
    @Override
    public void onMarkerDragEnd(Marker marker) {

        LatLng corUpdt = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        if(markInicial!=null) {
            int pos = marcadores.indexOf(marker);
            coordenadas.set(pos, corUpdt);

            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(coordenadas);
            lineOptions.color(Color.rgb(0,0,255));
            lineOptions.width(10);
            googleMap.clear();
            agregarMarcadores();
            googleMap.addPolyline(lineOptions);
            Log.i("tagv","DRAGG-si");

        }else{
            markFinal=corUpdt;
            Log.i("tagv","DRAGG-no");

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void subirRuta();
        void cerrarFragmrnt();
    }


    private String obtenerDireccionesURL(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    private void agregarMarcadoresUpdate() {
        marcadores.clear();
        for (LatLng l:coordenadas) {
            Marker m = googleMap.addMarker(new MarkerOptions().position(l).draggable(true));
            marcadores.add(m);
        }
        markInicial= marcadores.get(0).getPosition();
        markFinal=marcadores.get(marcadores.size()-1).getPosition();
    }

    private void agregarMarcadores() {
        marcadores.clear();
        for (LatLng l:coordenadas) {
            Marker m = googleMap.addMarker(new MarkerOptions().position(l).draggable(true));
            marcadores.add(m);
        }
    }

    //Descarga de Datos de la API por proceso
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //Leer el JSON que nos regresa el API
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    coordenadas.add(position);
                }

                lineOptions.addAll(coordenadas);
                lineOptions.width(10);
                lineOptions.color(Color.rgb(0,0,255));
            }
            if(lineOptions!=null) {
                agregarMarcadores();
                googleMap.addPolyline(lineOptions);
            }
        }
    }
}
