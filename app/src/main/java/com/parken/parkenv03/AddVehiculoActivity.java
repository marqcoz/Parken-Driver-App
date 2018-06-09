package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddVehiculoActivity extends AppCompatActivity {

    //AutoCompleteTextView
    AutoCompleteTextView marca;
    AutoCompleteTextView modelo;
    AutoCompleteTextView placa;

    //Buttons
    Button btnAddCar;

    //Strings
    private String marcaString, modeloString, placaString, origin, idvehiculo;

    //Objects
    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private View mProgressView;
    private View mAddNewCarFormView;
    private VehiculoActivity vehAct = new VehiculoActivity();
    private VehiculoInfoActivity vehInf = new VehiculoInfoActivity();

    AddVehiculoActivity activity;
    ShPref session;

    SesionParkenActivity seParkenAct = new SesionParkenActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehiculo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;

        Intent intent = getIntent();

        //AutocompleteTextView
        marca = findViewById(R.id.editTextMarca);
        modelo = findViewById(R.id.editTextModelo);
        placa = findViewById(R.id.editTextPlaca);

        //Buttons
        btnAddCar = findViewById(R.id.btnVehiculos);

        //Views
        mAddNewCarFormView = findViewById(R.id.add_new_car_formLL);
        mProgressView = findViewById(R.id.add_new_car_progress);


        //Volley
        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        //SharedPreferences
        session = new ShPref(activity);

        origin = intent.getStringExtra("origin");

            if (origin.equals("VehiculoInfoActivity")) {

                setupActionBar(true, "Editar vehículo", origin);

                btnAddCar.setText("Actualizar vehículo");
                marca.setText(intent.getStringExtra("marca"));
                modelo.setText(intent.getStringExtra("modelo"));
                placa.setText(intent.getStringExtra("placa"));

                idvehiculo = intent.getStringExtra("idvehiculo");

                placa.setEnabled(false);
            } else {
                setupActionBar(true, "Agregar vehículo", origin);
            }


            //Acción al presionar el botón "Agregar vehículo"
            btnAddCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addVehiculo();
                }
            });


    }
    private void addVehiculo(){

        // Reset errors.
        marca.setError(null);
        modelo.setError(null);
        placa.setError(null);

        //Obtenemos las variables
        // Store values at the time of the add.
        marcaString = marca.getText().toString().trim();
        modeloString = modelo.getText().toString().trim();
        placaString = placa.getText().toString().trim().replaceAll(" ","").replaceAll("-","").toUpperCase();

        boolean cancel = false;
        View focusView = null;

        //Validamos que todos los datos se hayan ingresado
        //Validate if it is empty the marca field
        if (TextUtils.isEmpty(marcaString)) {
            marca.setError(getString(R.string.error_addcar_field_required));
            focusView = marca;
            cancel = true;
        }

        //Validate if it is empty the modelo field
        if (TextUtils.isEmpty(modeloString)) {
            modelo.setError(getString(R.string.error_addcar_field_required));
            focusView = modelo;
            cancel = true;
        }


        //Validate if it is empty the placa field
        if (TextUtils.isEmpty(placaString)) {
            placa.setError(getString(R.string.error_addcar_field_required));
            focusView = placa;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //
            //Enviamos al server
            showProgress(true);
            //sendCar(session.infoId());
            if(origin.equals("VehiculoInfoActivity")){
                editarVehiculo(idvehiculo,marcaString, modeloString);
            }else{
                sendNewCar(session.infoId(),marcaString, modeloString, placaString);
            }

        }

    }


    public void sendNewCar(String automovilista, String marca, String modelo, String placa){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("marca", marca);
        parametros.put("modelo", modelo);
        parametros.put("placa", placa);
        parametros.put("idAutomovilista", automovilista);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_ADD_CAR,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        //Si se recibe un 1, se agregó exitosamente
                        if(response.getString("success").equals("1")){
                            //Log.d("LoginActivity", response.getString("success"));
                            showProgress(false);
                            dialogNewCarSuccess(response.getString("idVehiculo")).show();

                        }else{
                            showProgress(false);
                            if(response.getString("success").equals("2")){
                                dialogNewCarWarning().show();
                            }else{
                            if(response.getString("success").equals("3")){
                                dialogNewCarExists().show();
                            }else{
                                dialogNewCarFailed().show();
                            }
                            }


                            //Log.d("LoginActivity", response.getString("success"));
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogNewCarFailed().show();

                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }
    public void editarVehiculo(String vehiculo, String marca, String modelo){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idvehiculo", vehiculo);
        parametros.put("marca", marca);
        parametros.put("modelo", modelo);



        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_EDIT_CAR,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        showProgress(false);
                        if(response.getString("success").equals("1")) {

                            dialogNewCarSuccess("0").show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogNewCarFailed().show();

                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    private void setupActionBar(boolean estatus, String title, String origin) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
            actionBar.setTitle(title);
        }
    }

    public AlertDialog dialogNewCarSuccess(final String idvehiculo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Vehículo agregado")
                .setMessage("Se ha guardado la información de tu vehículo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if(origin.equals("VehiculoInfoActivity")){
                                    vehInf.activityVehiculoInfo.finish();
                                    vehAct.activityVehiculo.finish();
                                    startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                                    finish();
                                }else{

                                    if(origin.equals("SesionParkenActivity")){
                                        seParkenAct.activitySesionParken.textVehiculo.setText(modeloString+" - "+placaString);
                                        seParkenAct.activitySesionParken.idVehiculo= idvehiculo;
                                        finish();

                                    }else{

                                        startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                                        finish();
                                    }
                                }
                            }

                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if(origin.equals("VehiculoInfoActivity")){
                            vehInf.activityVehiculoInfo.finish();
                            vehAct.activityVehiculo.finish();
                            startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                            finish();
                        }else{

                            if(origin.equals("SesionParkenActivity")){
                                seParkenAct.activitySesionParken.textVehiculo.setText(modeloString+" - "+placaString);
                                seParkenAct.activitySesionParken.idVehiculo= idvehiculo;
                                finish();

                            }else{

                                startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                                finish();
                            }
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(origin.equals("VehiculoInfoActivity")){
                            vehInf.activityVehiculoInfo.finish();
                            vehAct.activityVehiculo.finish();
                            startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                            finish();
                        }else{

                            if(origin.equals("SesionParkenActivity")){
                                seParkenAct.activitySesionParken.textVehiculo.setText(modeloString+" - "+placaString);
                                seParkenAct.activitySesionParken.idVehiculo= idvehiculo;
                                finish();

                            }else{

                                startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                                finish();
                            }
                        }
                    }
                });

        return builder.create();
    }

    public AlertDialog dialogNewCarWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Vehículo agregado")
                .setMessage("Las placas del vehículo ya están registradas. Se agregó el vehiculo a su cuenta.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(AddVehiculoActivity.this, VehiculoActivity.class));
                                finish();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNewCarFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Error")
                .setMessage("No se puede agregar el vehículo. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNewCarExists() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Error")
                .setMessage("Este vehículo ya ha sido agregado a tu cuenta.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                            }
                        });

        return builder.create();
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

}
