package com.parken.parkenv03;

public class Sesion {

    private int idSesion;
    private String fechaInicio;
    private String horaInicio;
    private String horaFinal;
    private String fechaFinal;
    private int idSancion;
    private float montoSancion;
    private float monto;
    private String tiempo;
    private String estatus;
    private int idVehiculo;
    private String marcaVehiculo;
    private String modeloVehiculo;
    private String placaVehiculo;
    private int idEspacioParken;
    private String direccionEspacioParken;
    private int idZonaParken;
    private String nombreZonaParken;
    private String imgMapLink;

    public Sesion(int idSesion, String fechaInicio, String fechaFinal,
                  String horaInicio, String horaFinal, int idSancion, float montoSancion,
                  float monto, String tiempo, String estatus, int idVehiculo,
                  String marcaVehiculo, String modeloVehiculo, String placaVehiculo,
                  int idEspacioParken, String direccionEspacioParken, int idZonaParken,
                  String nombreZonaParken, String imgMapLink) {

        this.idSesion = idSesion;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.horaInicio = horaInicio;
        this.horaFinal = horaFinal;
        this.idSancion = idSancion;
        this.montoSancion = montoSancion;
        this.monto = monto;
        this.tiempo = tiempo;
        this.estatus = estatus;
        this.idVehiculo = idVehiculo;
        this.marcaVehiculo = marcaVehiculo;
        this.modeloVehiculo = modeloVehiculo;
        this.placaVehiculo = placaVehiculo;
        this.idEspacioParken = idEspacioParken;
        this.direccionEspacioParken = direccionEspacioParken;
        this.idZonaParken = idZonaParken;
        this.nombreZonaParken = nombreZonaParken;
        this.imgMapLink = imgMapLink;
    }

    public int getIdSesion() {
        return idSesion;
    }

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public float getMontoSancion() {
        return montoSancion;
    }

    public void setMontoSancion(float montoSancion) {
        this.montoSancion = montoSancion;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String esttatus) {
        this.estatus = esttatus;
    }

    public int getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getMarcaVehiculo() {
        return marcaVehiculo;
    }

    public void setMarcaVehiculo(String marcaVehiculo) {
        this.marcaVehiculo = marcaVehiculo;
    }

    public String getModeloVehiculo() {
        return modeloVehiculo;
    }

    public void setModeloVehiculo(String modeloVehiculo) {
        this.modeloVehiculo = modeloVehiculo;
    }

    public String getPlacaVehiculo() {
        return placaVehiculo;
    }

    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }

    public int getIdEspacioParken() {
        return idEspacioParken;
    }

    public void setIdEspacioParken(int idEspacioParken) {
        this.idEspacioParken = idEspacioParken;
    }

    public String getDireccionEspacioParken() {
        return direccionEspacioParken;
    }

    public void setDireccionEspacioParken(String direccionEspacioParken) {
        this.direccionEspacioParken = direccionEspacioParken;
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

    public int getIdSancion() {
        return idSancion;
    }

    public void setIdSancion(int idSancion) {
        this.idSancion = idSancion;
    }

    public String getImgMapLink() {
        return imgMapLink;
    }

    public void setImgMapLink(String imgMapLink) {
        this.imgMapLink = imgMapLink;
    }
}
