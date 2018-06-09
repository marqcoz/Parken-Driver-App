package com.parken.parkenv03;

/**
 * Created by marcos on 21/03/18.
 */

public class ZonaParken {

    private int idZonaParken;
    private String nombre;
    private String ubicacion;
    private String estatus;

    public ZonaParken(int idZonaParken, String nombre, String ubicacion, String estatus) {
        this.idZonaParken = idZonaParken;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.estatus = estatus;
    }

    public int getIdZonaParken() {
        return idZonaParken;
    }

    public void setIdZonaParken(int idZonaParken) {
        this.idZonaParken = idZonaParken;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }


    //Debe devolver un poligono (un chingo de coordendas)
    //Debe devolver un nombre
    //Debe devolver estatus
    //Debe devolver el id
    public void searchZone(){


    }
}
