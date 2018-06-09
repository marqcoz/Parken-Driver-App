package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerifyActivity extends AppCompatActivity{

    AutoCompleteTextView countryCode, cel;
    Button verify;
    String celular, codigoPais;
    String nombre, apellido, correo, password, origin;
    String id, column, value;
    View form;

    private FirebaseAuth mAuth;
    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    FirebaseAuth.AuthStateListener mAuthListener;
    String phoneNumber, phoneNumberFormatted, code;
    public static VerifyActivity activityVerify;
    private View mProgressView;
    private View mVerifyFormView;
    private ShPref session;
    private LoginActivity loginAct = new LoginActivity();

    @Override
    public  void onBackPressed(){
        if(session != null){
            if(session.getVerifying()){ } else{ super.onBackPressed(); }
        }else{
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(true);


        Intent intent = getIntent();
        origin = intent.getStringExtra("origin");
        if(origin.equals("createActivity")){
            nombre = intent.getStringExtra("nombre");
            apellido = intent.getStringExtra("apellido");
            correo = intent.getStringExtra("correo");
            password = intent.getStringExtra("password");
        }else{
            id = intent.getStringExtra("id");
            column = intent.getStringExtra("column");
            value = intent.getStringExtra("value");
        }




        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();
        activityVerify = this;
        session = new ShPref(activityVerify);
        session.setVerifying(false);
        mVerifyFormView = findViewById(R.id.nestedScrollForm);
        mProgressView = findViewById(R.id.verifiy_progress);

        VerifyFragment verifyFragment = (VerifyFragment)
                getSupportFragmentManager().findFragmentById(R.id.nestedScrollForm);

        if (verifyFragment == null) {
            verifyFragment = VerifyFragment.newInstance();

            Bundle arguments = new Bundle();
            arguments.putString("origin",origin);

            if(origin.equals("createActivity")){
                arguments.putString("nombre", nombre);
                arguments.putString("apellido", apellido);
                arguments.putString("correo", correo);
                arguments.putString("password", password);


            }else{
                arguments.putString("id", id);
                arguments.putString("column", column);
                arguments.putString("value", value);

            }


            verifyFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.nestedScrollForm, verifyFragment)
                    .commit();
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

