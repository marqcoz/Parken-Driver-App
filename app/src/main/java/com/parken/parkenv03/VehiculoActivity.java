package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VehiculoActivity extends AppCompatActivity {

    //JSON con todos los vehiculos
    public String jsonVehiculos;

    //Objects
    public static VehiculoActivity activityVehiculo;
    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    ShPref session;
    private View mProgressView;
    private View mAddNewCarFormView;
    TextView txtViewMessageCars;
    TextView aux;
    ImageView imgInfo;



    public String getJsonVehiculos() {
        return jsonVehiculos;
    }

    public void setJsonVehiculos(String jsonVehiculos) {
        this.jsonVehiculos = jsonVehiculos;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        mAddNewCarFormView = findViewById(R.id.car_form);
        mProgressView = findViewById(R.id.car_progress);
        imgInfo = findViewById(R.id.imageViewMessageCars);
        txtViewMessageCars = findViewById(R.id.textViewMessageCars);
        aux = findViewById(R.id.textView2);

        //Volley
        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
        activityVehiculo = this;
        session = new ShPref(activityVehiculo);
        showProgress(true);
        sendCar(session.infoId());
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAddCar);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehiculoActivity.this, AddVehiculoActivity.class);
                intent.putExtra("origin", "VehiculoActivity");
                startActivity(intent);
            }
        });


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void sendCar(String automovilista){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", automovilista);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_CARS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        showProgress(false);
                        if(response.getString("success").equals("1")){
                            aux.setVisibility(View.GONE);
                            txtViewMessageCars.setVisibility(View.GONE);
                            imgInfo.setVisibility(View.GONE);
                            Log.d("AddCarActivity", response.toString());
                            Log.d("AddCarActivity", response.getString("Vehiculos"));
                            jsonVehiculos = response.getString("Vehiculos");
                            setJsonVehiculos(response.getString("Vehiculos"));
                            session.setVehiculos(jsonVehiculos);

                            VehiculoFragment vehiculoFragment = (VehiculoFragment)
                                    getSupportFragmentManager().findFragmentById(R.id.vehiculos_container);

                            if (vehiculoFragment == null) {
                                vehiculoFragment = VehiculoFragment.newInstance();

                                //Bundle arguments = new Bundle();
                                //arguments.putString("Vehiculos", jsonVehiculos);

                                //vehiculoFragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.vehiculos_container, vehiculoFragment)
                                        .commit();
                            }

                        } else{
                            //No hay vehiculos registrados, hay que mostrar el mensaje en el contenedor:
                            //Aún no tienes vehículos registrados.
                            //Agrega uno ->
                            session.setVehiculos("");
                            aux.setVisibility(View.VISIBLE);
                            imgInfo.setVisibility(View.VISIBLE);
                            txtViewMessageCars.setVisibility(View.VISIBLE);
                            imgInfo.setImageResource(R.drawable.ic_add_blue);
                            txtViewMessageCars.setText("Aún no tienes vehículos registrados. Agrega uno.");

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogCarsFailed().show();
                        //No se puede realizar la conexión, hay que escribir en el contenedor:
                        //Ocurrió un problem al cargar tus vehículos.
                        aux.setVisibility(View.VISIBLE);
                        imgInfo.setVisibility(View.VISIBLE);
                        txtViewMessageCars.setVisibility(View.VISIBLE);
                        imgInfo.setImageResource(R.drawable.ic_no_connection);
                        txtViewMessageCars.setText("Error al cargar tus vehículos.");
                        Log.d("AddCarActivity", error.toString());
                    }
                });

        fRequestQueue.add(jsArrayRequest);
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

            mAddNewCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mAddNewCarFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAddNewCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mAddNewCarFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public AlertDialog dialogCarsFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityVehiculo);

        builder.setTitle("Error al mostrar los vehículos")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                            }
                        });
        return builder.create();

    }
}
