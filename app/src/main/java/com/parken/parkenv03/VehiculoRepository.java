package com.parken.parkenv03;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VehiculoRepository {
    /**
     * Repositorio ficticio de vehiculos
     */
        private static VehiculoRepository repository = new VehiculoRepository();
        private HashMap<String, Vehiculo> vehiculos = new HashMap<>();

        public static VehiculoRepository getInstance() {
            return repository;
        }

        private VehiculoRepository() {

            //Lo que debo hacer es sustituir los valores marca, modelo, placa por los del json
            //Se me ocurre leer solo el array vehiculos y pasar a esta clase todo e ir llenando
            saveLead(new Vehiculo("1","Alexander Pierrot", "CEO", "Insures S.O."));
            saveLead(new Vehiculo("1","Carlos Lopez", "Asistente", "Hospital Blue"));
            saveLead(new Vehiculo("1","Sara Bonz", "Directora de Marketing", "Electrical Parts ltd"));
            saveLead(new Vehiculo("1","Liliana Clarence", "Diseñadora de Producto", "Creativa App"));
            saveLead(new Vehiculo("1","Benito Peralta", "Supervisor de Ventas", "Neumáticos Press"));
            saveLead(new Vehiculo("1","Juan Jaramillo", "CEO", "Banco Nacional"));
            saveLead(new Vehiculo("1","Christian Steps", "CTO", "Cooperativa Verde"));
            saveLead(new Vehiculo("1","Alexa Giraldo", "Lead Programmer", "Frutisofy"));
            saveLead(new Vehiculo("1","Linda Murillo", "Directora de Marketing", "Seguros Boliver"));
            saveLead(new Vehiculo("1","Lizeth Astrada", "CEO", "Concesionario Motolox"));
        }

        private void saveLead(Vehiculo vehiculo) {
            vehiculos.put(vehiculo.getPlaca(), vehiculo);
        }

        public List<Vehiculo> getVehiculos() {
            return new ArrayList<>(vehiculos.values());
        }
    }

