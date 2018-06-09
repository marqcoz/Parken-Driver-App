package com.parken.parkenv03;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.w3c.dom.Text;

public class ShPref {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public ShPref(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("parken", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void clearAll(){
        editor.clear();
        editor.commit();
    }

    public void setVista(String vista){
        editor.putString("vista", vista);
        editor.commit();
    }

    public void setCancel(boolean cancel){
            editor.putBoolean("onCancel", cancel);
            editor.commit();
    }

    public void setZonasParken(String zonas){
        editor.putString("zonasParken", zonas);
        editor.commit();

    }

    public void setParkenSpace(int id){
        editor.putInt("espacioParkenAsignado", id);
        editor.commit();
    }

    public void setJSONParkenSpace(String ep){
        editor.putString("espacioParkenJSON", ep);
        editor.commit();
    }

    public void setGPS(String gps){
        editor.putString("appGPS", gps);
        editor.commit();

    }

    public void setNombreDestino(String destino){
        editor.putString("nombreDestino", destino);
        editor.commit();

    }

    public void setLatDestinoParken(double latitud){
        editor.putString("latitudDestinoParken",String.valueOf(latitud));
        editor.commit();
    }

    public void setLngDestinoParken(double longitud){
        editor.putString("longitudDestinoParken", String.valueOf(longitud));
        editor.commit();
    }

    public void setLatDestinoAutomovilista(double latitud){
        editor.putString("latitudDestinoAutomovilista",String.valueOf(latitud));
        editor.commit();
    }

    public void setLngDestinoAutomovilista(double longitud){
        editor.putString("longitudDestinoAutomovilista", String.valueOf(longitud));
        editor.commit();
    }

    public void setLatDestino(double latitud){
        editor.putString("latitudDestino",String.valueOf(latitud));
        editor.commit();
    }

    public void setLngDestino(double longitud){
        editor.putString("longitudDestino", String.valueOf(longitud));
        editor.commit();
    }

    public void setOnTheWay(boolean way){
        editor.putBoolean("onTheWay", way);
        editor.commit();
    }

    public void setLoggedin(boolean logggedin){
        editor.putBoolean("loggedInmode",logggedin);
        editor.commit();
    }

    public void setVehiculos(String vehiculos){
        editor.putString("vehiculos",vehiculos);
        editor.commit();
    }

    public void setVerifying(boolean verifying){
        editor.putBoolean("verifying", verifying);
        editor.commit();
    }

    public void setInfo(String id, String nombre, String apellido, String email, String pass, String celular, String puntos){
        editor.putString("id", id);
        editor.commit();
        editor.putString("nombre", nombre);
        editor.commit();
        editor.putString("apellido", apellido);
        editor.commit();
        editor.putString("email", email);
        editor.commit();
        editor.putString("password", pass);
        editor.commit();
        editor.putString("celular", celular);
        editor.commit();
        editor.putString("puntos",puntos);
        editor.commit();
    }

    public String getVista(){ return prefs.getString("vista", ParkenActivity.VIEW_PARKEN);}

    public boolean getCancel(){ return prefs.getBoolean("onCancel", true); }

    public String getZonasParken(){ return prefs.getString("zonasParken", "{}");}

    public String getNombreDestino() {return prefs.getString("nombreDestino", ""); }

    public double getLngDestinoParken(){
        double longitud = Double.parseDouble(prefs.getString("longitudDestinoParken","0.0"));
        return longitud;
    }

    public double getLatDestinoParken(){
        double latitud = Double.parseDouble(prefs.getString("latitudDestinoParken","0.0"));
        return latitud;
    }

    public double getLngDestinoAutomovilista(){
        double longitud = Double.parseDouble(prefs.getString("longitudDestinoAutomovilista","0.0"));
        return longitud;
    }

    public double getLatDestinoAutomovilista(){
        double latitud = Double.parseDouble(prefs.getString("latitudDestinoAutomovilista","0.0"));
        return latitud;
    }

    public double getLngDestino(){
        double longitud = Double.parseDouble(prefs.getString("longitudDestino","0.0"));
        return longitud;
    }

    public double getLatDestino(){
        double latitud = Double.parseDouble(prefs.getString("latitudDestino","0.0"));
        return latitud;
    }

    public int getParkenSpace(){
        return prefs.getInt("espacioParkenAsignado", 0);
    }

    public String getJSONParkenSpace(){
        return prefs.getString("espacioParkenJSON", "{}");
    }

    public String getGPS(){ return prefs.getString("appGPS", "Maps"); }

    public boolean getOnTheWay(){ return prefs.getBoolean("onTheWay", false); }

    public boolean loggedin(){
        return prefs.getBoolean("loggedInmode", false);
    }

    public String infoId(){
        return prefs.getString("id", null);
    }

    public String infoNombre(){
        return prefs.getString("nombre", null);
    }

    public String infoApellido(){
        return prefs.getString("apellido", null);
    }

    public String infoEmail(){
        return prefs.getString("email", null);
    }

    public String infoPass(){
        return prefs.getString("password", null);
    }

    public String infoCelular(){
        return prefs.getString("celular", null);
    }

    public String infoPuntos(){
        return prefs.getString("puntos", "");
    }

    public String getVehiculos(){ return prefs.getString("vehiculos","[{\"id\":130,\"marca\":\"Nissan\",\"modelo\":\"Versa\",\"placa\":\"D56DF\"},{\"id\":170,\"marca\":\"Nissan\",\"modelo\":\"Versa\",\"placa\":\"YHU098\"},{\"id\":171,\"marca\":\"Nissan\",\"modelo\":\"Versa\",\"placa\":\"HU098\"},{\"id\":174,\"marca\":\"Nisan\",\"modelo\":\"Versa\",\"placa\":\"HU09\"},{\"id\":187,\"marca\":\"Jffj\",\"modelo\":\"Nccj\",\"placa\":\"QW\"},{\"id\":188,\"marca\":\"Jffj\",\"modelo\":\"Nccj\",\"placa\":\"QT\"},{\"id\":189,\"marca\":\"Mavis\",\"modelo\":\"Ert\",\"placa\":\"QWERT\"},{\"id\":190,\"marca\":\"Mavis\",\"modelo\":\"Ert\",\"placa\":\"QWERTY\"},{\"id\":191,\"marca\":\"Mavis\",\"modelo\":\"Ert\",\"placa\":\"QWERTYR\"}]");}

    public boolean getVerifying(){ return prefs.getBoolean("verifying",false);}
}
