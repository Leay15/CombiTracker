package com.combitracker.Objetos;

/**
 * Created by juamp on 21/11/2017.
 */

public class Combi {
    private String Contraseña,Lat,Lon,Numero,RutaAsignada,Usuario;


    public Combi() {
    }


    public String getContraseña() {
        return Contraseña;
    }

    public void setContraseña(String contraseña) {
        Contraseña = contraseña;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getLon() {
        return Lon;
    }

    public void setLon(String lon) {
        Lon = lon;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getRutaAsignada() {
        return RutaAsignada;
    }

    public void setRutaAsignada(String rutaAsignada) {
        RutaAsignada = rutaAsignada;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }
}
