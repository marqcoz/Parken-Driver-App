package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SesionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    public SesionAdapter adapterSesion;
    private List<Sesion> sesion_list;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;

    private View mProgressView;
    private View mSesionesFormView;

    private TextView txtError;

    private ShPref session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mSesionesFormView = findViewById(R.id.sesion_form);
        mProgressView = findViewById(R.id.sesion_progress);

        txtError = findViewById(R.id.textViewMessageSesiones);

        session = new ShPref(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        sesion_list  = new ArrayList<>();

        try {
            showProgress(true);
            obtenerSesionesParken();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //load_data_from_server(0);

        gridLayoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapterSesion = new SesionAdapter(this, sesion_list, new SesionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Sesion item, View view) {

                String tag = view.getTag().toString();

                //Toast.makeText(, "Item Clicked", Toast.LENGTH_LONG).show();
                Log.d("PressSomething", tag +" - "+ item.getModeloVehiculo());

                switch (tag){
                    case "pagar":
                        Intent pagarSesion = new Intent(SesionActivity.this, SancionPagoActivity.class);
                        pagarSesion.putExtra("idSancion", item.getIdSancion());
                        pagarSesion.putExtra("espacioParken", item.getIdEspacioParken());
                        pagarSesion.putExtra("zonaParken", item.getNombreZonaParken());
                        pagarSesion.putExtra("modeloVehiculo", item.getModeloVehiculo());
                        //pagarSancion.putExtra("marcaVehiculo", item.getModeloVehiculo());
                        pagarSesion.putExtra("placaVehiculo", item.getPlacaVehiculo());
                        pagarSesion.putExtra("tiempo", item.getHoraFinal());
                        pagarSesion.putExtra("monto", item.getMontoSancion());
                        pagarSesion.putExtra("origin", "SesionActivity");
                        startActivity(pagarSesion);
                        break;
                    case "renovar":
                        break;
                    case "finalizar":
                        break;
                        default:
                            break;

                }



            }
        });
        recyclerView.setAdapter(adapterSesion);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if(gridLayoutManager.findLastCompletelyVisibleItemPosition() == sesion_list.size()-1){
                }

            }
        });
    }

    private void obtenerSesionesParken() throws JSONException {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", session.infoId());

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_PARKEN_SESSION,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("ObtenerSesiones", response.toString());
                                showProgress(false);

                                parseSesionesJSON(response.getString("Sesiones"));

                            }else{
                                showProgress(false);
                                Log.d("ObtenerSesiones", response.toString());
                                //Mostrar dialog
                                txtError.setText("Error al cargar las sesiones. Intenta de nuevo.");
                                txtError.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            //Mostrar dialog
                            txtError.setText("Error al cargar las sesiones. Intenta de nuevo.");
                            txtError.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ObtenerSesiones", "Error Respuesta en JSON: " + error.getMessage());
                        //Mostrar dialog
                        txtError.setText("Error al cargar las sesiones. Intenta de nuevo.");
                        txtError.setVisibility(View.VISIBLE);
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    private void parseSesionesJSON(String sesiones) throws JSONException {
        //En este metodo obtenemos de una consulta el json con la información de las sanciones
        //Lo parseamos y creamos el objeto sancion
        JSONArray jsonSesiones = new JSONArray(sesiones);
        Sesion sesion;
        String latitud;
        String longitud;

        for (int i = 0; i < jsonSesiones.length(); i++) {

            JSONArray jsonSancionCentro = new JSONArray(jsonSesiones.getJSONObject(i).getString("Coordenadas"));
            latitud = jsonSancionCentro.getJSONObject(0).getString("latitud");
            longitud = jsonSancionCentro.getJSONObject(0).getString("longitud");


            sesion = new Sesion(Integer.parseInt(jsonSesiones.getJSONObject(i).getString("idSesion")),
                    jsonSesiones.getJSONObject(i).getString("FechaInicio"),
                    jsonSesiones.getJSONObject(i).getString("FechaFinal"),
                    jsonSesiones.getJSONObject(i).getString("HoraInicio"),
                    jsonSesiones.getJSONObject(i).getString("HoraFinal"),
                    jsonSesiones.getJSONObject(i).getInt("idSancion"),
                    Float.valueOf(jsonSesiones.getJSONObject(i).getString("MontoSancion")),
                    Float.valueOf(jsonSesiones.getJSONObject(i).getString("Monto")),
                    //jsonSesiones.getJSONObject(i).getInt("Monto"),
                    jsonSesiones.getJSONObject(i).getString("Tiempo"),
                    jsonSesiones.getJSONObject(i).getString("Estatus"),
                    jsonSesiones.getJSONObject(i).getInt("idVehiculo"),
                    jsonSesiones.getJSONObject(i).getString("MarcaVehiculo"),
                    jsonSesiones.getJSONObject(i).getString("ModeloVehiculo"),
                    jsonSesiones.getJSONObject(i).getString("PlacaVehiculo"),
                    jsonSesiones.getJSONObject(i).getInt("idEspacioParken"),
                    jsonSesiones.getJSONObject(i).getString("DireccionZonaParken"),
                    jsonSesiones.getJSONObject(i).getInt("idZonaParken"),
                    jsonSesiones.getJSONObject(i).getString("NombreZonaParken"),
                    getStaticMapUrl(latitud, longitud));

            sesion_list.add(sesion);

        }

        adapterSesion.notifyDataSetChanged();

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

            mSesionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSesionesFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSesionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSesionesFormView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSesionesFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
