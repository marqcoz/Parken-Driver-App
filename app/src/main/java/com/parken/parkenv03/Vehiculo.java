package com.parken.parkenv03;

public class Vehiculo {
    private String id;
    private String marca;
    private String modelo;
    private String placa;

    public Vehiculo(String id, String marca, String modelo, String placa) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.placa = placa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String toString(){
        return "Vehiculos{" +
                "id='" + id + '\'' +
                ", Marca='" + marca + '\'' +
                ", Modelo='" + modelo + '\'' +
                ", Placa='" + placa + '\'' +
                '}';
    }
}
