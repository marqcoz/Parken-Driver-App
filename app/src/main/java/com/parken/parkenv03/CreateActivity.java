package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
//import android.icu.text.SimpleDateFormat;


public class CreateActivity extends AppCompatActivity {


    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private View mCreateFormView;
    private View mProgressView;
    public static CreateActivity activityCreate;


    //Botones
    Button sign;
    CheckBox terms;

    //TextView
    AutoCompleteTextView name, lastname, mail, pass, pass2, cel;

    String nombre, apellido, correo, password, password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();
        activityCreate = this;
        //session = new ShPref(activity);

        mCreateFormView = findViewById(R.id.linear_lay);
        mProgressView = findViewById(R.id.login_progress);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        sign = findViewById(R.id.btnSign2);
        name = findViewById(R.id.editTextName);
        lastname = findViewById(R.id.editTextApe);
        mail = findViewById(R.id.editTextMail);
        pass = findViewById(R.id.editTextPass);
        pass2 = findViewById(R.id.editTextPass2);
        cel = findViewById(R.id.editTextCel);
        terms = findViewById(R.id.checkBoxTerminos);


        //Metodo para esconder el teclado
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(sign.getWindowToken(), 0);


        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  attemptSigin();

            }
        });
    }

    //Método para registrar la información del automovilista
    private void attemptSigin() {

        // Reset errors.
        name.setError(null);
        lastname.setError(null);
        mail.setError(null);
        pass.setError(null);
        pass2.setError(null);


        // Store values at the time of the login attempt.

        //Enviar los datos al servidor para guardarlos en la base de datos
        nombre = name.getText().toString().trim();
        apellido = lastname.getText().toString().trim();
        correo = mail.getText().toString().trim();
        password = pass.getText().toString();
        password2 = pass2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nombre)) {
            name.setError(getString(R.string.error_field_required_sigin));
            focusView = name;
            cancel = true;
        }
        if (TextUtils.isEmpty(apellido)) {
            lastname.setError(getString(R.string.error_field_required_sigin));
            focusView = lastname;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(correo)) {
            mail.setError(getString(R.string.error_field_required_sigin));
            focusView = mail;
            cancel = true;
        } else if (!isEmailValid(correo)) {
            mail.setError(getString(R.string.error_invalid_email_sigin));
            focusView = mail;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            pass.setError(getString(R.string.error_field_required_sigin));
            //pass2.setError(getString(R.string.error_invalid_password));
            focusView = pass;
            cancel = true;

        }else{
            if (!isPasswordValid(password)) {
                pass.setError(getString(R.string.error_invalid_password_sigin));
                //pass2.setError(getString(R.string.error_invalid_password));
                focusView = pass;
                cancel = true;
            }else{
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password2)) {
                    pass2.setError(getString(R.string.error_field_required_sigin));
                    focusView = pass2;
                    cancel = true;
                }else{

                    if (!password2.equals(password)) {
                        pass2.setError(getString(R.string.error_invalid_matchpassword_sigin));
                        focusView = pass2;
                        cancel = true;
                    }

                }
            }

        }
        if(!terms.isChecked()){
            Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Debe aceptar los términos y condiciones.", Snackbar.LENGTH_LONG)
                    .show();
            focusView = name;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //Verify the mail doesn't exist in the database
            showProgress(true);
            verificarCredencial("1", correo);
        }
    }

    //Método para validar si ya esta registrado el correo en la base de datos
    public void verificarCredencial(String tipo, String mail){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("tipo", tipo);
        parametros.put("credencial", mail);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_VERYFY_ID,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {

                        if(response.getString("success").equals("1")){
                            //Si success:1 ya existe el correo.
                            showProgress(false);
                            dialogExistMail().show();
                            return;

                        }else{
                            //Si success:2 no existe el correo, pasamos a verificar el celular

                            showProgress(false);
                            Intent verify =  new Intent(CreateActivity.this, VerifyActivity.class);
                            verify.putExtra("nombre", nombre);
                            verify.putExtra("apellido", apellido);
                            verify.putExtra("correo", correo);
                            verify.putExtra("password", password);
                            verify.putExtra("origin","createActivity");
                            startActivity(verify);

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




    /*
    Métodos para validar los datos del automovilista
     */

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }


    private boolean isPasswordValid(String password) {
        //return password.length() > 4;
        return true;
    }


    /*
    AlertDialogs
     */
    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityCreate);

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

    public AlertDialog dialogExistMail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityCreate);

        builder.setTitle("Correo electrónico registrado")
                .setMessage("Este correo electrónico ya esta registrado en Parken.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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

            mCreateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
