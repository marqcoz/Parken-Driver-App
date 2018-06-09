package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class VerifyFragment extends Fragment {

    AutoCompleteTextView countryCode, cel;
    Button verify;
    String celular, codigoPais;
    View form;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private String phoneNumber, phoneNumberFormatted, code, origin;

    VerifyFragment fragmentVerify;
    String nombre, apellido, correo, password;
    String id, column, value;
    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;
    private ShPref session;
    private LoginActivity loginAct = new LoginActivity();
    private CreateActivity createActivity = new CreateActivity();
    private VerifyActivity verifyActivity = new VerifyActivity();

    private View mProgressView;
    private View mVerifyFormView;

    private InformationActivity infAct = new InformationActivity();

    public VerifyFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VerifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyFragment newInstance() {
        VerifyFragment fragment = new VerifyFragment();
        // Setup parámetros
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            origin = getArguments().getString("origin");
            if(origin.equals("createActivity")){
                nombre = getArguments().getString("nombre");
                apellido = getArguments().getString("apellido");
                correo = getArguments().getString("correo");
                password = getArguments().getString("password");

            }else{
                id = getArguments().getString("id");
                column = getArguments().getString("column");
                value = getArguments().getString("value");

            }


        }

        volley = VolleySingleton.getInstance(getContext());
        fRequestQueue = volley.getRequestQueue();
        fragmentVerify = this;
        session = new ShPref(fragmentVerify.getActivity());


        ((VerifyActivity) getActivity())
                .setupActionBar(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_verify, container, false);
        cel = root.findViewById(R.id.editTextCel);
        countryCode = root.findViewById(R.id.editTextCountryCode);
        verify = root.findViewById(R.id.btnVerificar);
        fragmentVerify = this;
        form = root.findViewById(R.id.verify_form);

        mVerifyFormView = getActivity().findViewById(R.id.nestedScrollForm);
        mProgressView = getActivity().findViewById(R.id.verifiy_progress);

        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        mAuth.getApp();


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(attemptSigin()){
                    dialogVerificationConfirm(phoneNumberFormatted).show();
                }
            }
        });


        return root;


    }

    public boolean attemptSigin(){

        Boolean cancel = false;

        celular = cel.getText().toString().trim();
        codigoPais = countryCode.getText().toString().trim();//.replaceAll(" ","").replaceAll(".","").replaceAll("#","").replaceAll("*","").replaceAll("-","");


        if (TextUtils.isEmpty(celular) | !isNumberValid(celular)) {
            Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "El número celular no es válido.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            snackbar.show();
            cancel = true;
        }

        if (TextUtils.isEmpty(codigoPais) | !isCountryCodeValid(codigoPais)) {
            Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), "El código del país no es válido.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            snackbar.show();
            cancel = true;
        }



        if(cancel){
            phoneNumber = "";
            return false;
        }else{
            phoneNumber = codigoPais+celular;
            phoneNumberFormatted = codigoPais + " " + celular.substring(0,2) + " " + celular.substring(2,6) + " " + celular.substring(6,10);
            return true;

        }
    }

    private boolean isCountryCodeValid(String countryCode) {
        if(countryCode.equals("+52")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isNumberValid(String number) {

        if(number.length() == 10){
            if(number.contains(".")){
                return false;
            }
            try {
                Float.parseFloat(number);
                return true;
            } catch (NumberFormatException nfe){
                return false;
            }

        }else{
            return false;
        }

    }

    public AlertDialog dialogVerificationFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Error")
                .setMessage("No se puede completar la verificación del número celular. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogVerificationConfirm(String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage(Html.fromHtml("Verificaremos el número celular: <br><br><b><big>"+number+"</big></b><br><br>¿Es correcto, o desea editar el número?"))
                .setNegativeButton("Editar",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No hacer nada
                        return;
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgress(true);
                                verificarCredencial("2", phoneNumber);

                            }

                        });

        return builder.create();
    }

    public AlertDialog dialogSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Mucho exito")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                //verificarCelular();
                            }

                        });

        return builder.create();
    }

    public void verificarCelular(){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        showProgress(false);
                        dialogVerificationFailed().show();
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("FAILED", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        code = s;
                        showProgress(false);
                        abrirVerifyCode(code);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                    }
                }

        );

    }



    public void abrirVerifyCode(String code){

        VerifyCodeFragment vcf = new VerifyCodeFragment();
            Bundle arg = new Bundle();
            if(origin.equals("createActivity")){
                arg.putString("veriId", code);
                arg.putString("phone", phoneNumber);
                arg.putString("phoneFormatted", phoneNumberFormatted);
                arg.putString("nombre", nombre);
                arg.putString("apellido", apellido);
                arg.putString("correo", correo);
                arg.putString("password", password);
                arg.putString("origin", origin);
            }else{
                arg.putString("id", id);
                arg.putString("veriId", code);
                arg.putString("phone", phoneNumber);
                arg.putString("phoneFormatted", phoneNumberFormatted);
                arg.putString("column", column);
                arg.putString("value", value);
                arg.putString("origin", origin);
            }
            vcf.setArguments(arg);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nestedScrollForm, vcf)
                    .addToBackStack(null)
                    .commit();
            session.setVerifying(true);
            showProgress(false);

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mVerifyFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mVerifyFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Esta da el acceso
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            showProgress(false);
                            if(origin.equals("createActivity")){
                                enviarJsonSign(phoneNumber);
                            }else{
                                actualizarPerfilAutomovilista(id,column,phoneNumber);
                            }

                            //dialogSuccess().show();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.d("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                showProgress(false);
                                dialogVerificationFailed().show();
                            }
                        }
                    }
                });
    }


    /*
    Método para verificar si el número celular ya se encuentra registrado
     */
    public void verificarCredencial(String tipo, String celular){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("tipo", tipo);
        parametros.put("credencial", celular);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_VERYFY_ID,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {

                        if(response.getString("success").equals("1")){
                            //Si success:1 ya existe el celular.
                            showProgress(false);
                            dialogExistCel().show();
                            return;

                        }else{
                            //Si success:2 no existe el celular, pasamos a verificar el celular
                            verificarCelular();


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


    public void enviarJsonSign(final String phone){
        HashMap<String, String> parametros = new HashMap();
        parametros.put("nombre", nombre);
        parametros.put("apellido", apellido);
        parametros.put("correo", correo);
        parametros.put("contrasena", password);
        parametros.put("celular", phone);
        //parametros.put("sexo", sexo);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_SIGIN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        //Log.d("LoginActivity", response.toString());
                        if(response.getString("success").equals("1")){

                            //Guardar los datos en la memoria del teléfono
                            //guardarDatosAutomovilista(response.getString(), nombre, apellido, correo, password, phone, "0.0");
                            session.setInfo(response.getString("id"),nombre, apellido, correo, password, phone, "0.0");
                            session.setLoggedin(true);
                            showProgress(false);
                            //dialogWelcome().show();

                            if(origin.equals("createActivity")){
                                startActivity(new Intent(getActivity(), ParkenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                getActivity().finish();
                                loginAct.activityLogin.finish();
                                createActivity.activityCreate.finish();
                                verifyActivity.activityVerify.finish();
                            }else{
                                getActivity().finish();
                            }

                            //addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK)

                        }else{
                            showProgress(false);
                            if(response.getString("success").equals("2")){
                                dialogNoUser(2).show();
                            }
                            if(response.getString("success").equals("3")){
                                dialogNoUser(3).show();
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


    /*
    AlertsDialog
     */

    public AlertDialog dialogUpdateFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Actualización exitosa")
                .setMessage("Se ha modificado el perfil exitosamente.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                infAct.informationActivity.finish();
                                startActivity(new Intent(getActivity(),InformationActivity.class));
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoUser(int info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = "";
        if(info == 2){
            message = "correo electrónico";
        }
        if(info == 3){
            message = "número celular";
        }

        builder.setTitle("No se pudo crear la cuenta")
                .setMessage("El "+ message + " ya está registrado.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogExistCel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Número celular registrado")
                .setMessage("Este número celular ya esta registrado en Parken.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogWelcome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Bienvenido")
                .setMessage("Registro exitoso.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listener.onPossitiveButtonClick();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Error")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo")
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
