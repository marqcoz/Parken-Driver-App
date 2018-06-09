package com.parken.parkenv03;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by marcos on 21/03/18.
 */

public class Jeison {

    //public static String URL_BASE_POST = "http://192.168.1.70:3000/automovilista/sigIn";
    public static final String YOUR_API_KEY = "AIzaSyA15TKW11TMeC60Kmoq4cqgRYryvRsGDQI";
    //public static final String IP = "192.168.1.75";
    public static final String IP = "192.168.70.198";
  //  public static final String IP = "192.168.1.111";

      //public static final String IP = "10.4.132.31";


    //public static final String IP = "192.168.43.188";

    public static final String PORT = ":3000";
    public static final String URL_BASE = "http://" + IP + PORT;
    public static final String URL_LOGIN = URL_BASE + "/login";
    public static final String URL_DRIVER_SIGIN = URL_BASE + "/automovilista/sigIn";
    public static final String URL_DRIVER_DATA = URL_BASE + "/automovilista/data";
    public static final String URL_DRIVER_ADD_CAR = URL_BASE + "/automovilista/addCar";
    public static final String URL_DRIVER_CARS = URL_BASE + "/automovilista/cars";
    public static final String URL_DRIVER_CAR_AVAILABLE = URL_BASE + "/automovilista/verificarEstatusVehiculo";
    public static final String URL_DRIVER_POINTS = URL_BASE + "/automovilista/obtenerPuntosParken";
    public static final String URL_DRIVER_FIND_PARKEN_ZONE = URL_BASE + "/automovilista/buscarZonaParken";
    public static final String URL_DRIVER_FIND_PARKEN_SPACE = URL_BASE + "/automovilista/buscarEspacioParken";
    public static final String URL_DRIVER_PARKEN_SPACE = URL_BASE + "/automovilista/mostrarEspaciosParken";
    public static final String URL_DRIVER_VERYFY_ID = URL_BASE +  "/automovilista/verificarDatos";
    public static final String URL_DRIVER_UPDATE = URL_BASE +  "/automovilista/actualizarDatos";
    public static final String URL_DRIVER_DELETE_CAR = URL_BASE +  "/automovilista/eliminarVehiculo";
    public static final String URL_DRIVER_EDIT_CAR = URL_BASE +  "/automovilista/actualizarVehiculo";
    public static final String URL_DRIVER_PARKEN_SPACE_BOOK = URL_BASE + "/automovilista/apartarEspacioParken";
    public static final String URL_DRIVER_TICKETS = URL_BASE + "/automovilista/obtenerSanciones";
    public static final String URL_DRIVER_UPDATE_TICKET = URL_BASE + "/automovilista/pagarSancion";
    public static final String URL_DRIVER_PARKEN_SESSION = URL_BASE + "/automovilista/obtenerSesionesParken";
    public static final String URL_DRIVER_ACIVATE_SESSION_PARKEN = URL_BASE + "/automovilista/activarSesionParken";
    public static final String URL_DRIVER_DEACIVATE_SESSION_PARKEN = URL_BASE + "/automovilista/desactivarSesionParken";
    public static final String URL_DRIVER_CREATE_REPORT = URL_BASE + "/automovilista/crearReporte";
    public static final String URL_DRIVER_DELETE_SESSION = URL_BASE + "/automovilista/eliminarSesionParken";
    public static String URL_BASE_POST = "http://192.168.1.70:3000/login";
    public static final String URL_GET_DIRECTION = "https://maps.googleapis.com/maps/api/geocode/json?address=~ADDRESS~&key="+YOUR_API_KEY;
    //https://maps.googleapis.com/maps/api/geocode/json?address=NEPTUNO&key=AIzaSyA15TKW11TMeC60Kmoq4cqgRYryvRsGDQI
    public static final String URL_GET_LATLNG = "https://maps.googleapis.com/maps/api/geocode/json?latlng=~LNG,~LAT&key="+ YOUR_API_KEY;
    protected RequestQueue fRequestQueue;
    public String obj;
    public String resp;

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public void sendJson(HashMap parametros, VolleySingleton vs) {

        fRequestQueue = vs.getRequestQueue();
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_BASE_POST,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //onConnectionFinished();

                        Log.d("LoginActivity", response.toString());
                        //setResp(response.toString());
                        //resp = response.toString();
                        resp = "Holi";

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //onConnectionFailed(error.getMessage());
                        Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                       //resp = (error.getMessage());
                        resp = "Holi2";


                    }
                });

            //this.setResp("putos");

        fRequestQueue.add(jsArrayRequest);

        //onPreStartConnection();
        //Log.d("Jeison",resp);
    }

//    public Jeison() {
//        this.resp = "Test";
    //}
}

