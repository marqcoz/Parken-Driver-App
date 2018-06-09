package com.parken.parkenv03;

import java.util.Date;

public class Sancion {

    private int idSancion;
    private String tiempo;
    private float monto;
    private String observacion;
    private String estatus;
    private String tiempoPago;
    private int idAutomovilista;
    private String nombreAutomovilista;
    private String apellidoAutomovilista;
    private String correoAutomovilista;
    private String celAutomovilista;
    private int idVehiculo;
    private String modeloVehiculo;
    private String placaAutomovilista;
    private int idSupervisor;
    private int idEspacioParken;
    private int idZonaParken;
    private String nombreZonaParken;
    private String direccionEspacioParken;

    private String imgMapLink;

    public Sancion(int idSancion, String tiempo, float monto, String observacion,String estatus,
                   String tiempoPago, int idAutomovilista,
                   /*String nombreAutomovilista,
                   String apellidoAutomovilista, String correoAutomovilista,
                   String celAutomovilista,*/
                   int idVehiculo, String modeloVehiculo,
                   String placaAutomovilista, int idSupervisor, int idEspacioParken,
                   int idZonaParken, String nombreZonaParken, String direccionEspacioParken,
                   String imgMapLink) {

        this.idSancion = idSancion;
        this.tiempo = tiempo;
        this.monto = monto;
        this.observacion = observacion;
        this.estatus = estatus;
        this.tiempoPago = tiempoPago;
        this.idAutomovilista = idAutomovilista;
        /*this.nombreAutomovilista = nombreAutomovilista;
        this.apellidoAutomovilista = apellidoAutomovilista;
        this.correoAutomovilista = correoAutomovilista;
        this.celAutomovilista = celAutomovilista;*/
        this.idVehiculo = idVehiculo;
        this.modeloVehiculo = modeloVehiculo;
        this.placaAutomovilista = placaAutomovilista;
        this.idSupervisor = idSupervisor;
        this.idEspacioParken = idEspacioParken;
        this.idZonaParken = idZonaParken;
        this.nombreZonaParken = nombreZonaParken;
        this.direccionEspacioParken = direccionEspacioParken;
        this.imgMapLink = imgMapLink;
    }

    public String getNombreAutomovilista() {
        return nombreAutomovilista;
    }

    public void setNombreAutomovilista(String nombreAutomovilista) {
        this.nombreAutomovilista = nombreAutomovilista;
    }

    public String getApellidoAutomovilista() {
        return apellidoAutomovilista;
    }

    public void setApellidoAutomovilista(String apellidoAutomovilista) {
        this.apellidoAutomovilista = apellidoAutomovilista;
    }

    public String getCorreoAutomovilista() {
        return correoAutomovilista;
    }

    public void setCorreoAutomovilista(String correoAutomovilista) {
        this.correoAutomovilista = correoAutomovilista;
    }

    public String getCelAutomovilista() {
        return celAutomovilista;
    }

    public void setCelAutomovilista(String celAutomovilista) {
        this.celAutomovilista = celAutomovilista;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getModeloVehiculo() {
        return modeloVehiculo;
    }

    public void setModeloVehiculo(String modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }

    public String getPlacaAutomovilista() {
        return placaAutomovilista;
    }

    public void setPlacaAutomovilista(String placaAutomovilista) {
        this.placaAutomovilista = placaAutomovilista;
    }

    public int getIdEspacioParken() {
        return idEspacioParken;
    }

    public void setIdEspacioParken(int idEspacioParken) {
        this.idEspacioParken = idEspacioParken;
    }

    public int getIdZonaParken() {
        return idZonaParken;
    }

    public void setIdZonaParken(int idZonaParken) {
        this.idZonaParken = idZonaParken;
    }

    public String getNombreZonaParken() {
        return nombreZonaParken;
    }

    public void setNombreZonaParken(String nombreZonaParken) {
        this.nombreZonaParken = nombreZonaParken;
    }

    public String getDireccionEspacioParken() {
        return direccionEspacioParken;
    }

    public void setDireccionEspacioParken(String direccionEspacioParken) {
        this.direccionEspacioParken = direccionEspacioParken;
    }

    public int getIdSancion() {
        return idSancion;
    }

    public void setIdSancion(int idSancion) {
        this.idSancion = idSancion;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getTiempoPago() {
        return tiempoPago;
    }

    public void setTiempoPago(String tiempoPago) {
        this.tiempoPago = tiempoPago;
    }

    public int getIdAutomovilista() {
        return idAutomovilista;
    }

    public void setIdAutomovilista(int idAutomovilista) {
        this.idAutomovilista = idAutomovilista;
    }

    public int getIdSupervisor() {
        return idSupervisor;
    }

    public void setIdSupervisor(int idSupervisor) {
        this.idSupervisor = idSupervisor;
    }

    public String getImgMapLink() {
        return imgMapLink;
    }

    public void setImgMapLink(String imgMapLink) {
        this.imgMapLink = imgMapLink;
    }
}
