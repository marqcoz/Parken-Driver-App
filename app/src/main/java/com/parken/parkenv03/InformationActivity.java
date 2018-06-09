package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class InformationActivity extends AppCompatActivity {

    private ConstraintLayout nombre;
    private ConstraintLayout apellido;
    private ConstraintLayout correo;
    private ConstraintLayout celular;
    private ConstraintLayout contrasena;

    private TextView name;
    private TextView last;
    private TextView mail;
    private TextView pass;
    private TextView cel;

    private LinearLayout linear;

    ShPref session;
    static InformationActivity informationActivity;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private View mProgressView;
    private View mInfoFormView;
    private NestedScrollView nested;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);

        session = new ShPref(this);
        informationActivity = this;

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mInfoFormView = findViewById(R.id.nested_form_profile);
        mProgressView = findViewById(R.id.info_progress);
        nested = findViewById(R.id.nested_form_profile);

        linear = findViewById(R.id.linear_profile);

        nombre = findViewById(R.id.constraintNombre);
        apellido = findViewById(R.id.constraintApellido);
        correo = findViewById(R.id.constraintCorreo);
        celular = findViewById(R.id.constraintCelular);
        contrasena = findViewById(R.id.constraintContrasena);

        name = findViewById(R.id.textViewName);
        last = findViewById(R.id.textViewLast);
        mail = findViewById(R.id.textViewMail);
        cel = findViewById(R.id.textViewCel);
        pass = findViewById(R.id.textViewPass);

        //session.setInfo(response.getString("id"),nombre, apellido, correo, password, phone, "0.0");
        showProgress(true);
        obtenerPerfilAutomovilista(session.infoId());


        nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
              intent.putExtra("id", session.infoId());
              intent.putExtra("column", "Nombre");
              intent.putExtra("value", name.getText().toString());
              startActivity(intent);
            }
        });

        apellido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Apellido");
                intent.putExtra("value", last.getText().toString());
                startActivity(intent);
            }
        });

        correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "email");
                intent.putExtra("value", mail.getText().toString());
                startActivity(intent);
                */
                Snackbar snackbar = Snackbar.make(view, "El correo electrónico no se puede cambiar.", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();

            }
        });

        celular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(InformationActivity.this, VerifyActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Celular");
                intent.putExtra("value", cel.getText().toString());
                intent.putExtra("nombre", name.getText().toString());

                /*
                intent.putExtra("apellido", last.getText().toString());
                intent.putExtra("correo", mail.getText().toString());
                intent.putExtra("password", pass.getText().toString());
                */
                intent.putExtra("origin","informationActivity");
                startActivity(intent);
            }
        });

        contrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InformationActivity.this, EditProfileActivity.class);
                intent.putExtra("id", session.infoId());
                intent.putExtra("column", "Contrasena");
                intent.putExtra("value", pass.getText().toString());
                startActivity(intent);

            }
        });
    }

    public void obtenerPerfilAutomovilista(String idAutomovilista) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", idAutomovilista);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_DATA,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getString("success").equals("1")){
                                showProgress(false);
                                session.setInfo(response.getString("id"), response.getString("nombre"), response.getString("apellido"),response.getString("email"), response.getString("password"), response.getString("celular"), response.getString("puntos"));
                                //session.setVehiculos("");
                                name.setText(response.getString("nombre"));
                                last.setText(response.getString("apellido"));
                                mail.setText(response.getString("email"));
                                cel.setText(response.getString("celular"));
                                pass.setText(response.getString("password"));

                            }else{
                                showProgress(false);
                                messageFailed();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            messageFailed();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        messageFailed();
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void messageFailed(){
        Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "No se puede realizar la conexión con el servidor.", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        snackbar.show();
        nombre.setEnabled(false);
        name.setText("");
        apellido.setEnabled(false);
        last.setText("");
        correo.setEnabled(false);
        mail.setText("");
        celular.setEnabled(false);
        cel.setText("");
        contrasena.setEnabled(false);
        pass.setText("");
        //nombre.setVisibility(View.INVISIBLE);


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

            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mInfoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
        }
    }
}
