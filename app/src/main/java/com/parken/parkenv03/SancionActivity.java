package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SancionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public SancionAdapter adapterSancion;
    private List<Sancion> sancion_list;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;

    private View mProgressView;
    private View mSancionesFormView;

    private TextView txtError;

    private ShPref session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sancion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mSancionesFormView = findViewById(R.id.sancion_form);
        mProgressView = findViewById(R.id.sancion_progress);

        txtError = findViewById(R.id.textViewMessageSanciones);

        session = new ShPref(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        sancion_list  = new ArrayList<>();

        try {
            showProgress(true);
            obtenerSanciones(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //load_data_from_server(0);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapterSancion = new SancionAdapter(this, sancion_list, new SancionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Sancion item) {
                Intent pagarSancion = new Intent(SancionActivity.this, SancionPagoActivity.class);
                pagarSancion.putExtra("idSancion", item.getIdSancion());
                pagarSancion.putExtra("espacioParken", item.getIdEspacioParken());
                pagarSancion.putExtra("zonaParken", item.getNombreZonaParken());
                pagarSancion.putExtra("modeloVehiculo", item.getModeloVehiculo());
                //pagarSancion.putExtra("marcaVehiculo", item.getModeloVehiculo());
                pagarSancion.putExtra("placaVehiculo", item.getPlacaAutomovilista());
                pagarSancion.putExtra("tiempo", item.getTiempo() );
                pagarSancion.putExtra("monto", item.getMonto());
                pagarSancion.putExtra("origin", "SancionActivity");
                startActivity(pagarSancion);

                //Toast.makeText(, "Item Clicked", Toast.LENGTH_LONG).show();
                Log.d("PressSomething", item.getModeloVehiculo());

            }
        });
        recyclerView.setAdapter(adapterSancion);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == sancion_list.size()-1){
                    //try {
                        //obtenerSanciones(sancion_list.get(sancion_list.size()-1).getIdSancion());
                    //} catch (JSONException e) {
                      //  e.printStackTrace();
                    //}
                    //obtenerSanciones(sancion_list.get(sancion_list.size()-1).getIdSancion());
                    //load_data_from_server(data_list.get(data_list.size()-1).getId());
                }

            }
        });


    }

    private void obtenerSanciones(int idSancion) throws JSONException {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", session.infoId());

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_TICKETS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("ObtenerSanciones", response.toString());
                                showProgress(false);

                                parseSancionesJSON(response.getString("Sanciones"));

                            }else{
                                showProgress(false);
                                Log.d("ObtenerSanciones", response.toString());
                                //Mostrar dialog
                                txtError.setText("Error al cargar las sanciones. Intenta de nuevo.");
                                txtError.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            //Mostrar dialog
                            txtError.setText("Error al cargar las sanciones. Intenta de nuevo.");
                            txtError.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ObtenerSanciones", "Error Respuesta en JSON: " + error.getMessage());
                        //Mostrar dialog
                        txtError.setText("Error al cargar las sanciones. Intenta de nuevo.");
                        txtError.setVisibility(View.VISIBLE);
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    private void parseSancionesJSON(String sanciones) throws JSONException {
        //En este metodo obtenemos de una consulta el json con la información de las sanciones
        //Lo parseamos y creamos el objeto sancion
        JSONArray jsonSanciones = new JSONArray(sanciones);
        Sancion ticket;
        String latitud;
        String longitud;

        for (int i = 0; i < jsonSanciones.length(); i++) {

            JSONArray jsonSancionCentro = new JSONArray(jsonSanciones.getJSONObject(i).getString("Coordenadas"));
            latitud = jsonSancionCentro.getJSONObject(0).getString("latitud");
            longitud = jsonSancionCentro.getJSONObject(0).getString("longitud");

            ticket = new Sancion(Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idSancion")),
                    jsonSanciones.getJSONObject(i).getString("Fecha") + " - " + jsonSanciones.getJSONObject(i).getString("Hora"),
                    Float.valueOf(jsonSanciones.getJSONObject(i).getString("Monto")),
                    jsonSanciones.getJSONObject(i).getString("Observaciones"),
                    jsonSanciones.getJSONObject(i).getString("Estatus"),
                    jsonSanciones.getJSONObject(i).getString("FechaPago") + " - " + jsonSanciones.getJSONObject(i).getString("HoraPago"),
                    Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idAutomovilista")),
                    /*jsonSanciones.getJSONObject(i).getString("NombreAutomovilista"),
                    jsonSanciones.getJSONObject(i).getString("ApellidoAutomovilista"),
                    jsonSanciones.getJSONObject(i).getString("CorreoAutomovilista"),
                    jsonSanciones.getJSONObject(i).getString("CelAutomovilista"), */
                    Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idVehiculo")),
                    jsonSanciones.getJSONObject(i).getString("ModeloVehiculo"),
                    jsonSanciones.getJSONObject(i).getString("PlacaVehiculo"),
                    Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idSupervisor")),
                    Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idEspacioParken")),
                    Integer.parseInt(jsonSanciones.getJSONObject(i).getString("idZonaParken")),
                    jsonSanciones.getJSONObject(i).getString("NombreZonaParken"),
                    "",
                    //obtenerDireccionJson(latitud, longitud),
                    //jsonSanciones.getJSONObject(i).getString("DireccionEP"),
                    getStaticMapUrl(latitud, longitud));
                    //"https://maps.googleapis.com/maps/api/staticmap?center=19.453094,-99.147437&zoom=15&size=400x200&&markers=color:RED%7Clabel:P%7C19.453094,-99.147437&maptype=roadmap&key=AIzaSyAZNyEJvYRGwgeo0udMCeajMgeZXC1mAwg");

            sancion_list.add(ticket);

        }

        adapterSancion.notifyDataSetChanged();

    }

    public String obtenerDireccionJson(String lat, String lng){
        String peticion;
        final String[] dir = new String[1];
        peticion = Jeison.URL_GET_LATLNG.replaceAll("~LAT", lng).replaceAll("~LNG", lat);
        Log.d("URL", peticion);
        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                peticion,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            String formattedAddress = ((JSONArray)response.get("results")).getJSONObject(0)
                                    .getString("formatted_address");
                            Log.d("LoginActivity", response.toString());
                            //Aqui ya podemos modificar la etiqueta
                            dir[0] = formattedAddress;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dir[0] = "ERROR";
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ObtenerDireccion", "Error Respuesta en JSON: " + error.getMessage());
                        //showProgress(false);
                        dir[0] = "ERROR";

                    }
                }
        );

        fRequestQueue.add(jsArrayRequest);
        return dir[0];

    }


    private String getStaticMapUrl(String latPoint, String lngPoint) {

        String coordenadas =  latPoint +","+lngPoint;
        String center = "center=" + coordenadas;
        String zoom = "zoom=15";
        String size = "size=400x200";
        String markers = "markers=color:RED%7Clabel:P%7C" + coordenadas;
        String mapType = "maptype=roadmap";
        String key = "key="+getString(R.string.google_maps_directions_key);

        String parametros = center + "&" + zoom + "&"+ size +"&&"+ markers + "&" + mapType + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        //String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        String url = "https://maps.googleapis.com/maps/api/staticmap?"+parametros;
        Log.d("URL GoogleMapsStatic", url);

        return url;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSancionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSancionesFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSancionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSancionesFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSancionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
