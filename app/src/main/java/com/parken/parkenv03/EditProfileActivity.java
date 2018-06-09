package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    TextView info;
    AutoCompleteTextView data;
    Button refresh;

    private String id;
    private String column;
    private String value;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private View mProgressView;
    private View mEditInfoFormView;

    static EditProfileActivity editProfileActivity;
    private InformationActivity infAct = new InformationActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        column = intent.getStringExtra("column");
        value = intent.getStringExtra("value");


        info = findViewById(R.id.textViewInformacion);
        data = findViewById(R.id.autoCompleteTextViewDat);

        mEditInfoFormView = findViewById(R.id.edit_form);
        mProgressView = findViewById(R.id.edit_progress);

        refresh = findViewById(R.id.btnSaveProfile);

        if(column.equals("Contrasena")){
            info.setText(column.replace("na","ña"));
            data.setHint("Por tu seguridad, ingresa tu contaseña actual");
            data.setTextSize(15);
            data.setTransformationMethod(PasswordTransformationMethod.getInstance());
            refresh.setText("Verificar contraseña");

        }else{
            info.setText(column);
            data.setText(value);
            data.setSelection(data.length());
        }

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(column.equals("Contrasena")){
                    if(data.getText().toString().equals(value)){
                        Intent intent =  new Intent(EditProfileActivity.this, PasswordActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("column", column);
                        intent.putExtra("value", value);
                        intent.putExtra("origin","editProfileActivity");
                        startActivity(intent);

                    }else{
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "La contraseña es incorrecta.", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                    }

                }else{

                    value = data.getText().toString().trim();
                    column = column.toLowerCase();
                    showProgress(true);
                    actualizarPerfilAutomovilista(id, column, value);
                    //Ejecutar un Update a la base de datos
                }
            }
        });

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
                                infAct.informationActivity.finish();
                                startActivity(new Intent(EditProfileActivity.this,InformationActivity.class));
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

            mEditInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditInfoFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEditInfoFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");

        }
    }
}
