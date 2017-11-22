package com.combitracker.Objetos;

/**
 * Created by juamp on 21/11/2017.
 */

public class Ruta {
    private String camino,ruta;

    public Ruta(String camino, String ruta) {
        this.camino = camino;
        this.ruta = ruta;
    }

    public Ruta() {
    }

    public String getCamino() {
        return camino;
    }

    public void setCamino(String camino) {
        this.camino = camino;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
}
