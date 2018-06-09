package com.parken.parkenv03;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by marcos on 21/03/18.
 */

public class Automovilista {

    private int idAutomovilista;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private String celular;
    private Double puntosParken;


    public Automovilista(String nombre, String apellido, String email, String contrasena, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.celular = celular;
        this.puntosParken = 0.0;
    }

    public int getIdAutomovilista() {
        return idAutomovilista;
    }

    public void setIdAutomovilista(int idAutomovilista) {
        this.idAutomovilista = idAutomovilista;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Double getPuntosParken() {
        return puntosParken;
    }

    public void setPuntosParken(Double puntosParken) {
        this.puntosParken = puntosParken;
    }

}
