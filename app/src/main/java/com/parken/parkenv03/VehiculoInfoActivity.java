package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VehiculoInfoActivity extends AppCompatActivity {

    TextView brand;
    TextView model;
    TextView plate;

    ShPref session;

    private String marca;
    private String modelo;
    private String placa;
    private String id;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private View mProgressView;
    private View mVehiculoFormView;
    public static VehiculoInfoActivity activityVehiculoInfo;
    VehiculoActivity vehAct = new VehiculoActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);
        activityVehiculoInfo = this;

        session = new ShPref(this);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        Intent intent = getIntent();

        marca = intent.getStringExtra("marca");
        modelo = intent.getStringExtra("modelo");
        placa = intent.getStringExtra("placa");
        id = intent.getStringExtra("id");

        brand = findViewById(R.id.textViewBrand);
        model = findViewById(R.id.textViewModel);
        plate = findViewById(R.id.textViewPlate);

        mVehiculoFormView = findViewById(R.id.nested_form_vehiculo);
        mProgressView = findViewById(R.id.veh_progress);

        brand.setText(marca);
        model.setText(modelo);
        plate.setText(placa);



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_vehiculoinfo, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.vehiculoEditar:
                Intent intent = new Intent(VehiculoInfoActivity.this,AddVehiculoActivity.class);
                intent.putExtra("origin", "VehiculoInfoActivity");
                intent.putExtra("idvehiculo", id);
                intent.putExtra("marca", marca);
                intent.putExtra("modelo", modelo);
                intent.putExtra("placa", placa);
                startActivity(intent);

                return true;
            case R.id.vehiculoEliminar:
                dialogEliminarVehiculo().show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog dialogEliminarVehiculo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Eliminar vehículo")
                .setMessage("¿Desea eliminar el vehículo?")
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //session.setLoggedin(false);
                                //finish();
                                //startActivity(new Intent(ParkenActivity.this,LoginActivity.class));
                                eliminarVehiculo(id, session.infoId());
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogEliminarFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage("No se eliminó el vehículo. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogConnectionFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
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

    public void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
        }
    }

    public void eliminarVehiculo(String vehiculo, String automovilista){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idautomovilista", automovilista);
        parametros.put("idvehiculo", vehiculo);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_DELETE_CAR,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        showProgress(false);
                        if(response.getString("success").equals("1")){
                            vehAct.activityVehiculo.finish();
                            finish();
                            startActivity(new Intent(VehiculoInfoActivity.this, VehiculoActivity.class));


                        } else{
                            dialogEliminarFailed().show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogConnectionFailed().show();

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

            mVehiculoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVehiculoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVehiculoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mVehiculoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
