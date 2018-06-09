package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import java.util.HashMap;

public class PasswordActivity extends AppCompatActivity {

    private String origin;
    private String id;
    private String value;
    private String column;
    private String password1;
    private String password2;

    Button refreshPass;
    AutoCompleteTextView pass1;
    AutoCompleteTextView pass2;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private EditProfileActivity editAct = new EditProfileActivity();
    private InformationActivity infAct = new InformationActivity();

    private View mProgressView;
    private View mPassFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        refreshPass = findViewById(R.id.btnSavePassword);
        pass1 = findViewById(R.id.autoCompleteTextViewPass);
        pass2 = findViewById(R.id.autoCompleteTextViewPass2);

        mPassFormView = findViewById(R.id.password_form);
        mProgressView = findViewById(R.id.password_progress);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        Intent intent = getIntent();

            origin = intent.getStringExtra("origin");


            if(origin.equals("editProfileActivity")) {

                id = intent.getStringExtra("id");
                column = intent.getStringExtra("column");
                value =  intent.getStringExtra("value");

            }

            refreshPass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(validarPassword()){
                        showProgress(true);
                        actualizarPerfilAutomovilista(id, column.toLowerCase(), password1);
                    }
                }
            });



    }


    public boolean validarPassword(){

        password1 = pass1.getText().toString();
        password2 = pass2.getText().toString();

        //Verificamos si los dos son iguales
        if(TextUtils.isEmpty(password1)){
            pass1.setError("Se requiere el campo");
            return false;
        }

        if(TextUtils.isEmpty(password2)){
            pass2.setError("Se requiere el campo");
            return false;
        }

        if(password1.equals(password2)){
            return true;
        }else{
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "Las contraseñas no coinciden.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            snackbar.show();
            return false;
        }
    }

    public void actualizarPerfilAutomovilista(String id, String column, String value){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("id", id);
        parametros.put("column", column);
        parametros.put("value", value);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_UPDATE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        //Log.d("LoginActivity", response.toString());
                        if(response.getString("success").equals("1")){
                            showProgress(false);
                            //Si los datos se actualizaron correctamente, entonces cerramos el activity
                            //y/o cerramos el otro activity y lo reiniciamos o lo volvemos a abrir
                            dialogUpdateSuccess().show();

                        }else{
                            showProgress(false);
                            if(response.getString("success").equals("0")){
                                dialogUpdateFailed().show();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        dialogNoConnection().show();



                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogUpdateFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Error")
                .setMessage("Error al actualizar el perfil. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogUpdateSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Actualización exitosa")
                .setMessage("Se ha modificado el perfil exitosamente.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                editAct.editProfileActivity.finish();
                                infAct.informationActivity.finish();
                                startActivity(new Intent(PasswordActivity.this,InformationActivity.class));
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

            mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPassFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mPassFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Actualizar contraseña");
        }
    }
}
