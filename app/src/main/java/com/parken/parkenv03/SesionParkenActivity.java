package com.parken.parkenv03;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SesionParkenActivity extends AppCompatActivity {

    private static final String PAYPAL_CLIENT = "Ae5YnMlrCaIo7Gl622Cvyb7r8eOEhQweVjxuvvxxVhJCbfZgtH4LLDOshoDQe_LO2iClRoMQID_YcOwW";
    private static final int PAYPAL_REQUEST_CODE = 7171;
    public static final String CURRENCY = "USD";
    //public static final String CURRENCY = "MXN";
    public static final String ACTIVITY_PARKEN = "ParkenActivity";
    public static final String ACTIVITY_SESION = "SesionParkenActivity";



    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PAYPAL_CLIENT);

    private AlertDialog _dialog;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    private TimerTask timerTask = new TimerTask();

    private View mSesionParkenFormView;
    private View mProgressView;


    ConstraintLayout vehiculo;
    ConstraintLayout fecha;
    ConstraintLayout tiempo;
    ConstraintLayout hora;
    ConstraintLayout puntosParken;
    ConstraintLayout monto;
    ConstraintLayout montoTotal;
    ConstraintLayout paypal;

    LinearLayout timer;

    TextView txtEspacioParken;
    TextView textVehiculo;
    TextView txtFecha;
    TextView txtTiempo;
    TextView txtHora;
    TextView txtPuntos;
    TextView txtMonto;
    TextView txtMonto2;
    TextView txtTotal;
    TextView txtPaypal;
    TextView txtTimer;

    Button pay;
    Button cancel;

    ShPref session;

    Intent parken;

    private CharSequence[] vehiculos;
    private CharSequence[] vehiculosId;
    private CharSequence[] vehiculosMarca;
    private CharSequence[] vehiculosModelo;
    private CharSequence[] vehiculosPlaca;

    private String jsonVehiculos;

    private  int mYear;
    private  int mMonth;
    private  int mDay;
    private  int mMin;
    private  int mHour;
    private int mSeg;
    private String opc;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMin;

    Double precioParken = 5.0;
    Float valorPuntos = 1f;

    boolean horaReinicida = false;

    Calendar calendarFechaFinal;
    Calendar calendarFechaFinalFija;


    Double precioFinal;
    Double montoPrevio;
    long minutosParken;
    long tiempoPrevio;
    Double puntosRestante;
    String strDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String idEspacioParken;
    private String idSesionParken;
    private String espacioParkenJson;
    public CharSequence idVehiculo;
    public CharSequence marcaVehiculo;
    public CharSequence modeloVehiculo;
    public CharSequence placaVehiculo;

    private double puntosP;
    private String cuentaPayPal;
    private String origin;
    private String carro;

    public static SesionParkenActivity activitySesionParken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_parken);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar(false);

        activitySesionParken = this;
/*
        Intent intent = getIntent();
        espacioParken = intent.getStringExtra("espacioParken");
        precioParken = intent.getStringExtra("precioParken");

*/

        session = new ShPref(this);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();

        mSesionParkenFormView = findViewById(R.id.sesion_scroll);
        mProgressView = findViewById(R.id.sesion_progress);

        vehiculo = findViewById(R.id.constraintVehiculo);
        fecha = findViewById(R.id.constraintFechaFin);
        hora = findViewById(R.id.constraintHora);
        tiempo = findViewById(R.id.constraintTiempo);
        puntosParken = findViewById(R.id.constraintPuntosParken);
        monto = findViewById(R.id.constraintMonto);
        montoTotal = findViewById(R.id.constraintMontoTotal);
        paypal = findViewById(R.id.constraintPayPal);

        timer = findViewById(R.id.linearLayoutTimer);

        txtEspacioParken = findViewById(R.id.textViewEP);
        textVehiculo = findViewById(R.id.textViewCar);
        txtFecha = findViewById(R.id.textViewDateEnd);
        txtTiempo = findViewById(R.id.textViewTime);
        txtHora = findViewById(R.id.textViewHour);
        txtPuntos = findViewById(R.id.textViewPointsP);
        txtMonto = findViewById(R.id.textViewAmount);
        txtMonto2 = findViewById(R.id.textViewAmount2);
        txtTotal = findViewById(R.id.textViewAmountTotal);
        txtPaypal = findViewById(R.id.textViewPay);
        txtTimer = findViewById(R.id.textViewTimerPago);

        pay = findViewById(R.id.btnPayPal);
        cancel = findViewById(R.id.btnCancelarPago);

        //Iniciar PayPal Service
        Intent intent = new Intent(SesionParkenActivity.this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        cargarDatos();
        //iniciarTimer();

        //sendCar(session.infoId());

        vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                sendCar(session.infoId());
            }
        });

        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(origin.equals(ACTIVITY_SESION)){

                    mYear = selectedYear;
                    mMonth = selectedMonth;
                    mDay = selectedDay;

                }else{
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                }




                Log.d("Fecha", "TRUE");
                DatePickerDialog datePickerDialog = new DatePickerDialog(activitySesionParken,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Log.d("onDataSet", "TRUE");
                                String dat = (dayOfMonth + " " + obtenerMesNombre(monthOfYear + 1))+ ", " + year;
                                int segu;
                                Calendar d;
                                Log.d("onDataSet", dat);
                                if(txtHora.getText().toString().equals("Selecciona...")){
                                    Log.d("onDataSet", "NoFecha");
                                    d = new GregorianCalendar(year,monthOfYear,dayOfMonth);
                                    segu = 2;
                                }else{
                                    Log.d("onDataSet", "Si fecha");
                                    d = new GregorianCalendar(year,monthOfYear,dayOfMonth, selectedHour, selectedMin);
                                    segu = 1;
                                    //selectedHour = c.get(Calendar.HOUR_OF_DAY);
                                    //selectedMin = c.get(Calendar.MINUTE);
                                }
                                //Validación fecha menor a la actual
                                if (obtenerFechaNow(segu).compareTo(d)==1){
                                    //La fecha seleccionada ya pasó
                                    dialogWrongCalendar(1).show();
                                }else{
                                    txtFecha.setText(dat);
                                    selectedDay = dayOfMonth;
                                    selectedMonth = monthOfYear;
                                    selectedYear = year;
                                    //Validación hora seleccionada
                                    if(txtHora.getText().toString().equals("Selecciona...") || textVehiculo.getText().toString().equals("Selecciona un vehículo...")){
                                    }else{
                                        activarOpciones(1);
                                    }
                                }
                                //txtFecha.setText(String.valueOf(obtenerFecha().compareTo(d)));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time

                if(origin.equals(ACTIVITY_SESION)){

                    mYear = selectedYear;
                    mMonth = selectedMonth;
                    mDay = selectedDay;
                    mMin = selectedMin;
                    mHour = selectedHour;

                }else {

                    final Calendar c = Calendar.getInstance();
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    mMonth = c.get(Calendar.MONTH);
                    mYear = c.get(Calendar.YEAR);
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMin = c.get(Calendar.MINUTE);
                }


                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(activitySesionParken,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String dat;
                                if(minute >= 0 && minute < 10){
                                    String sMin = "0" + String.valueOf(minute);
                                    dat = hourOfDay + ":" + sMin + " hrs";
                                }else{
                                    dat = hourOfDay + ":" + minute + " hrs";
                                }


                                //Debo crear el d con base al que esta en Fecha
                                //Eso implica guardar nuevas variables de fechas
                                Calendar d = new GregorianCalendar(selectedYear,selectedMonth, selectedDay, hourOfDay, minute);
                                //Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth-1,selectedDay,selectedHour,selectedMin);
                                Date date = d.getTime();
                                strDate = sdf.format(date);
                                Log.d("CalendarHora", strDate);
                                //Validación hora menor a la actual
                                //if(origin.equals()
                                if (obtenerFechaNow(3).compareTo(d)==1){
                                    //La fecha seleccionada ya pasó
                                    dialogWrongCalendar(2).show();
                                }else{
                                    Date date2 = calendarFechaFinalFija.getTime();
                                    String strDate2 = sdf.format(date2);
                                    Log.d("CalendarFija", strDate2);
                                    if(calendarFechaFinalFija.compareTo(d)==1 && origin.equals(ACTIVITY_SESION)){
                                        dialogWrongCalendar(2).show();
                                    }else {
                                        txtHora.setText(dat);
                                        selectedHour = hourOfDay;
                                        selectedMin = minute;
                                        //Validación hora seleccionada
                                        if (textVehiculo.getText().toString().equals("Selecciona un vehículo...")) {
                                        } else {
                                            activarOpciones(1);
                                        }
                                    }
                                }

                            }
                        }, mHour, mMin, false);
                timePickerDialog.show();

            }
        });

        tiempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             dialogTimerPicker(0).show();

            }
        });

        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin.equals(ACTIVITY_PARKEN)){
                    //Verificar que el vehiculo seleccionado este disponible
                    verificarDisponiblidadVehiculo(String.valueOf(idVehiculo));
                }else{
                    payAttempt();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin.equals(ACTIVITY_PARKEN)){
                    dialogCancelPago().show();
                }else {
                    finish();
                }

            }
        });


    }

    private void processPayment() {
        PayPalPayment paypalPayment = new PayPalPayment(new BigDecimal(precioFinal),
                CURRENCY, "Parken Test", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, paypalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    private void payAttempt(){

        //Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
        Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
        Date date = afterFecha.getTime();
        strDate = sdf.format(date);

        Log.d("FechaFinal", strDate);

        if(precioFinal == 0){
            activarSesionParken(session.infoId(), idSesionParken, String.valueOf(precioFinal+montoPrevio), String.valueOf(minutosParken+tiempoPrevio), String.valueOf(idVehiculo), strDate, String.valueOf(puntosRestante), opc);

        }else{
            processPayment();
        }
    }

    public void obtenerVehiculos(String jsonVehiculos, String numVehiculo ){

        try{

            if(!numVehiculo.equals("0")){
                JSONArray jsonArray = new JSONArray(jsonVehiculos);

                vehiculos = new CharSequence[jsonArray.length() + 1];
                vehiculosId = new CharSequence[jsonArray.length() + 1];
                vehiculosMarca = new CharSequence[jsonArray.length() + 1];
                vehiculosModelo = new CharSequence[jsonArray.length() + 1];
                vehiculosPlaca = new CharSequence[jsonArray.length() + 1];

                for(int i = 0; i < jsonArray.length(); i++){
                    vehiculos[i] = jsonArray.getJSONObject(i).getString("modelo") + " - " + jsonArray.getJSONObject(i).getString("placa");
                    vehiculosId[i] = jsonArray.getJSONObject(i).getString("id");
                    vehiculosMarca[i] = jsonArray.getJSONObject(i).getString("marca");
                    vehiculosModelo[i] = jsonArray.getJSONObject(i).getString("modelo");
                    vehiculosPlaca[i] = jsonArray.getJSONObject(i).getString("placa");

                }
                vehiculos[jsonArray.length()] = "Agregar un vehículo";
                vehiculosId[jsonArray.length()] = "0";
                vehiculosMarca[jsonArray.length()] = "";
                vehiculosModelo[jsonArray.length()] = "";
                vehiculosPlaca[jsonArray.length()] = "";

            }else{

                vehiculos = new CharSequence[1];
                vehiculosId = new CharSequence[1];
                vehiculosPlaca = new CharSequence[1];
                vehiculosModelo = new CharSequence[1];
                vehiculosMarca = new CharSequence[1];
                vehiculosId[0] = "0";
            }

            //if(txtHora.getText().toString().equals("Selecciona...")){
            if(txtTiempo.getText().toString().equals("Selecciona...")){
            }else{
                activarOpciones(1);
            }


        }catch (Exception e){
            e.printStackTrace();
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

                            jsonVehiculos = response.getString("Vehiculos");
                            session.setVehiculos(jsonVehiculos);

                            obtenerVehiculos(jsonVehiculos, response.getString("vehiculosNumber"));


                        } else{

                            jsonVehiculos = "";
                            obtenerVehiculos(jsonVehiculos,"0");
                            session.setVehiculos("");
                        }
                        dialogVehiculosList().show();
                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ConsultarVehiculos", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    public void verificarDisponiblidadVehiculo(String idVehiculo){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idVehiculo", idVehiculo);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_CAR_AVAILABLE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        showProgress(false);
                        Log.d("VerificarDisponibilidad", response.toString());
                        if(response.getString("success").equals("1") && response.getString("Disponibilidad").equals("DISPONIBLE")){
                            //Si esta disponible entonces procedemos con el pago, si no, le decimos al coño que cambie de vehiculo
                            payAttempt();
                        } else{
                            dialogCarBusy().show();
                            textVehiculo.setText("Selecciona un vehículo...");
                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("VerificarDisponibilidad", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    public void activarSesionParken(final String automovilista, final String idSesionParken, final String monto, final String tiempo, final String idVehiculo, final String fechaFinal, final String puntosParken, String opc){

        final HashMap<String, String> parametros = new HashMap();

        parametros.put("idAutomovilista", automovilista);
        parametros.put("idSesionParken", idSesionParken);
        parametros.put("FechaFinal", fechaFinal);
        parametros.put("Monto", monto);
        parametros.put("Tiempo", tiempo);
        parametros.put("idVehiculo", idVehiculo);
        parametros.put("puntosParken", puntosParken);
        parametros.put("opc", opc);


/*
        parametros.put("idSesionParken", automovilista);
        parametros.put("FechaFinal", automovilista);
        parametros.put("Monto", automovilista);//Sumar al monto final
        parametros.put("Tiempo", automovilista);//Sumar al tiempo final
        parametros.put("Estatus", automovilista);

        parametros.put("idAutomovilista", automovilista);
        parametros.put("idAutomovilista", automovilista);
        parametros.put("idEspacioParken", automovilista);
        parametros.put("idZonaParken", automovilista);
*/

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_ACIVATE_SESSION_PARKEN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        Log.d("ActivarSesion", response.toString());
                        showProgress(false);
                        if(response.getString("success").equals("1")){
                            timerTask.cancel(true);
                                parken = new Intent(SesionParkenActivity.this, ParkenActivity.class);
                                parken.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                parken.putExtra("Activity", ParkenActivity.ACTIVITY_SESION_PARKEN);
                                parken.putExtra("ActivityStatus", ParkenActivity.MESSAGE_PAY_SUCCESS);
                                parken.putExtra("idSesionParken", idSesionParken);
                                parken.putExtra("FechaFinal", fechaFinal);
                                parken.putExtra("Monto", monto);
                                parken.putExtra("TiempoEnMinutos", Integer.parseInt(tiempo));
                                parken.putExtra("idVehiculo", idVehiculo);
                                parken.putExtra("ModeloVehiculo", modeloVehiculo);
                                parken.putExtra("PlacaVehiculo", placaVehiculo);

                                //Información para el receipt
                                parken.putExtra("PuntosP", puntosP);
                                parken.putExtra("PrecioFinal", precioFinal);
                                parken.putExtra("ValorPuntos", valorPuntos);
                                parken.putExtra("PuntosRestante", puntosRestante);
                                parken.putExtra("MinutosParken",minutosParken);
                                parken.putExtra("SelectedMin", selectedMin);
                                parken.putExtra("SelectedHour", selectedHour);

                                if(origin.equals(ACTIVITY_PARKEN)){
                                    parken.putExtra("clearPausa", true);
                                }else{
                                    parken.putExtra("clearPausa", false);
                                }

                                startActivity(parken);
                                finish();
                                //dialogPayWithPoints().show();

                        } else{
                                dialogError().show();
                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("ActivarSesion", error.toString());
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }



    public void obtenerPuntosParken(String automovilista){

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", automovilista);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_POINTS,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        showProgress(false);
                        if(response.getString("success").equals("1")){
                            puntosP = response.getDouble("PuntosParken");
                            activarOpciones(2);
                            //txtPuntos.setText(String.valueOf(puntosP)+"0");

                        } else{

                        }

                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "No hay conexión con el servidor", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }



    public AlertDialog dialogVehiculosList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Selecciona tu vehículo: ")
                .setItems(vehiculos,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(vehiculos[which].equals("Agregar un vehículo")){
                            Intent intent = new Intent(SesionParkenActivity.this, AddVehiculoActivity.class);
                            intent.putExtra("origin", "SesionParkenActivity");
                            startActivity(intent);
                            textVehiculo.setText("Selecciona un vehículo...");

                        }else  {
                            textVehiculo.setText(vehiculos[which]);
                            idVehiculo = vehiculosId[which];
                            marcaVehiculo = vehiculosMarca[which];
                            modeloVehiculo = vehiculosModelo[which];
                            placaVehiculo = vehiculosPlaca[which];

                        }

                    }
                });

        return builder.create();
    }

    public AlertDialog dialogTimeList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence [] times = new CharSequence[20];
        times[0]="5 minutos";
        times[1]="10 minutos";
        times[2]="15 minutos";
        times[3]="20 minutos";
        times[4]="25 minutos";
        times[5]="30 minutos";
        times[6]="35 minutos";
        times[7]="40 minutos";
        times[8]="45 minutos";
        times[9]="50 minutos";
        times[10]="55 minutos";
        times[11]="1 hora";
        times[12]="1 hora 5 minutos";
        times[13]="20 minutos";
        times[14]="25 minutos";
        times[15]="30 minutos";
        times[16]="35 minutos";
        times[17]="40 minutos";
        times[18]="45 minutos";
        times[19]="50 minutos";

        builder.setTitle("Selecciona los minutos: ")
                .setSingleChoiceItems(times,0,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
/*
                        if(times[which].equals("Agregar un vehículo")){
                            Intent intent = new Intent(SesionParkenActivity.this, AddVehiculoActivity.class);
                            intent.putExtra("origin", "SesionParkenActivity");
                            startActivity(intent);
                        }*/
                        txtTiempo.setText(times[which]);
                        dialog.cancel();

                    }
                });

        return builder.create();
    }


    public AlertDialog dialogTimerPicker(final int minParken) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activitySesionParken);

        LayoutInflater inflater = activitySesionParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_time, null);
        builder.setView(v);

        final EditText h = v.findViewById(R.id.editTextHoras);
        final EditText m = v.findViewById(R.id.editTextMinutos);

        Button establecer = v.findViewById(R.id.btnEstablecerTiempo);

        String horas = String.valueOf(minParken/60);
        String minutos;
        if((minParken)%60 == 0){
            minutos = "5";
        } else{
            minutos = String.valueOf((minParken)%60);
        }

        h.setText(horas);
        m.setText(minutos);

        final AlertDialog dialog = builder.create();

        m.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int min;
                int hora;

                if(m.getText().toString().equals("")){
                    min = 0;
                }else{
                    min =Integer.parseInt(m.getText().toString());
                }

                if(h.getText().toString().equals("")) {
                    hora = 0;
                }else{
                    hora = Integer.parseInt(h.getText().toString());
                }


                if(min > 59){
                    //Se suma una hora
                    hora = hora + 1;
                    h.setText(String.valueOf(hora));
                    //Se actualizan los minutos
                    min = min - 60;
                    m.setText(String.valueOf(min));

                }


            }
        });

        establecer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(h.getText().toString().equals("")){
                    h.setText("0");
                }
                if(m.getText().toString().equals("")){
                    m.setText("0");
                }

                int auxInt = Integer.parseInt(m.getText().toString());

                if(auxInt < 5 && h.getText().toString().equals("0")){
                    m.setText("5");
                }

                int min = ( Integer.parseInt( h.getText().toString() )*60 ) + Integer.parseInt(m.getText().toString());
                //min

                minutosParken = min;

                txtTiempo.setText(obtenerTiempoString(min));

                if(!textVehiculo.getText().toString().equals("Selecciona...")){
                    activarOpciones(1);
                }
                Log.d("MyTimerPicker", String.valueOf(min));

                dialog.cancel();
            }
        });

        return dialog;
    }


    public AlertDialog dialogCarBusy() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vehículo no disponible")
                .setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogCancelPago() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancelar Pago")
                .setMessage("¿Deseas cancelar el pago de tu sesión Parken? Tu espacio asignado se liberará.")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogSuccessParkenSession() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EXITO")
                //.setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogError() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ERROR")
                //.setMessage("El vehículo seleccionado se encuentra en una sesión Parken. Selecciona otro.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogTimeOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Tiempo excedido")
                .setMessage("No efectuaste tu pago a tiempo. Por favor desaloja el espacio Parken.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.putExtra("Activity", ParkenActivity.ACTIVITY_SESION_PARKEN);
                                dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_PAY_FAILED);
                                Log.d("Activity", ParkenActivity.MESSAGE_PAY_FAILED);
                                startActivity(dialogIntent);
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                        Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Activity", ParkenActivity.ACTIVITY_SESION_PARKEN);
                        dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_PAY_FAILED);
                        Log.d("Activity", ParkenActivity.MESSAGE_PAY_FAILED);
                        startActivity(dialogIntent);

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Activity", ParkenActivity.ACTIVITY_SESION_PARKEN);
                        dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_PAY_FAILED);
                        Log.d("Activity", ParkenActivity.MESSAGE_PAY_FAILED);
                        startActivity(dialogIntent);
                    }
                });

        return builder.create();
    }

    public AlertDialog dialogWrongCalendar(int opc) {
        String m;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(opc==1){ m = "Fecha"; }else{ m = "Hora"; }

        builder.setTitle(m+ " no válida")
                .setMessage("La " + m.toLowerCase() + " seleccionada no es válida.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogTimeAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("¡Atención!")
                .setMessage("Tiene 5 minutos para efectuar su pago, de lo contrario deberá liberar el espacio Parken o será acreedor a una sanción.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                _dialog = null;
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        _dialog = null;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        _dialog = null;
                    }
                });

        return builder.create();
    }

    public AlertDialog dialogPayWithPoints() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activitySesionParken);

        LayoutInflater inflater = activitySesionParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_receipt, null);
        builder.setView(v);

        LinearLayout puntos = v.findViewById(R.id.linearLayoutPuntos);

        TextView txtMonto = v.findViewById(R.id.textViewMontoCobrado);
        TextView txtPuntosUsados = v.findViewById(R.id.textViewPuntosUsado);
        TextView txtPuntosActualizados = v.findViewById(R.id.textViewPuntosActuales);
        TextView txtTiempo = v.findViewById(R.id.textViewTiempo);
        TextView txtRecordatorio = v.findViewById(R.id.textViewRecordatorio);

        if(puntosP == 0.0){
            puntos.setVisibility(View.GONE);
        }

        Button btnAceptar = v.findViewById(R.id.btnAceptar);
        String mon = "$ " + String.valueOf(precioFinal) + "0 " + CURRENCY;
        txtMonto.setText(mon);
        String punUs = String.valueOf(((puntosP*valorPuntos)-puntosRestante));
        txtPuntosUsados.setText(punUs);
        String punAct = String.valueOf(puntosRestante);
        txtPuntosActualizados.setText(punAct);
        String tiem = String.valueOf(minutosParken) + " minutos";
        txtTiempo.setText(tiem);
        String reco;
        if(selectedMin < 10){
            reco = selectedHour + ":0" + selectedMin + " hrs";
        }else{
            reco = selectedHour + ":" + selectedMin + " hrs";
        }
        txtRecordatorio.setText(reco);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(parken);
                finish();
            }
        });
        return builder.create();
    }


    private String obtenerMesNombre(int mes){
        switch (mes){
            case 1:
                return "Enero";

            case 2:
                return "Febrero";

            case 3:
                return "Marzo";

            case 4:
                return "Abril";

            case 5:
                return "Mayo";

            case 6:
                return "Junio";

            case 7:
                return "Julio";

            case 8:
                return "Agosto";

            case 9:
                return "Septiembre";

            case 10:
                return "Octubre";

            case 11:
                return "Noviembre";

            case 12:
                return "Diciembre";

                default:
                    break;

        }
        return "ERROR";

    }

    public long obtenerTiempo(Calendar now, Calendar after) {

        long d = after.getTimeInMillis() - now.getTimeInMillis() ;
        long segundos = d / 1000;
        long minutos = segundos/60;

        return minutos;

    }

    public static String obtenerTiempoString(long minutos){

        long horas = minutos/60;

        String min;
        String hour;
        String h;
        String m;
        String fin;

        if(horas >0){

            hour = String.valueOf(horas);
            min = String.valueOf(minutos -(horas*60));

            if(horas ==1){
                h = " hora ";
            }else{
                h = " horas ";
            }
            if(minutos -(horas*60) == 1){
                m = " minuto";
            }else{
                if(minutos -(horas*60)== 0){
                    m = "";
                    min = "";
                }else{
                    m = " minutos";
                }

            }

            fin = hour + h + min + m;


        }else{
            min = String.valueOf(minutos);

            if(minutos ==1){
                m = " minuto";
            }else{
                m = " minutos";
            }

            fin = min + m;
        }

//      1:45 horas
//      45 minutos
//      1 hora 45 minutos
//      return hour + h + min + m;
//      return min + m;
        return fin;
    }

    public Calendar obtenerFechaNow(int seg){

        int limiteMinutos = 5;

        final Calendar c = Calendar.getInstance();
        Calendar d;
        Log.d("onDataSet", "FechaNow:"+seg);
        switch(seg){
            case 1:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                break;
            case 2:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                break;
            case 3:
                d = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)+limiteMinutos);
                Date date = d.getTime();
                strDate = sdf.format(date);
                Log.d("CalendarObtenerNow", strDate);
                break;
                default:
                    d = null;
                    break;
        }
        //Log.d("onDataSet", d);
        return d;
    }

    public void activarOpciones(int opc){
        if(opc == 1){
            tiempo.setVisibility(View.VISIBLE);

            Calendar now;

            if(origin.equals(ACTIVITY_SESION)){
                if(!horaReinicida) {
                    calendarFechaFinal.add(Calendar.MINUTE, -5);
                    horaReinicida = true;
                }
                now = calendarFechaFinal;
            }else{
                now = obtenerFechaNow(1);
            }

            Calendar afterFecha = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
            //La nueva forma de obtener el tiempo es:
            //Crear un calendario con la fecha de hoy
            //añadirle los nuevos minutos
            //obtener la fecha
            //Mostrarla
            //nuevosMinutosParken
            final Calendar c;
            if(origin.equals(ACTIVITY_SESION)){
                //Creamos el calendario con la fecha final
                //y le agragamos los minutosParken
                c = new GregorianCalendar(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMin);
                //para este instante ya tenemos los minutos parken
                c.add(Calendar.MINUTE, (int)minutosParken);

            } else{

                c = Calendar.getInstance();
                //para este instante ya tenemos los minutos parken
                c.add(Calendar.MINUTE, (int)minutosParken);
            }



            tiempo.setVisibility(View.VISIBLE);
            fecha.setVisibility(View.VISIBLE);
            hora.setVisibility(View.VISIBLE);

            //Mostrar a fecha en txtFecha
            String dat = (c.get(Calendar.DAY_OF_MONTH) + " " + obtenerMesNombre(c.get(Calendar.MONTH) + 1))+ ", " + c.get(Calendar.YEAR);
            txtFecha.setText(dat);
            //Mostrar la hora en txtHora

            if(c.get(Calendar.MINUTE) >= 0 && c.get(Calendar.MINUTE) < 10){
                String sMin = "0" + String.valueOf(c.get(Calendar.MINUTE));
                dat = c.get(Calendar.HOUR_OF_DAY) + ":" + sMin + " hrs";
            }else{
                dat = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + " hrs";
            }
            txtHora.setText(dat);



            //minutosParken = obtenerTiempo(now,afterFecha);
            txtTiempo.setText(String.valueOf(obtenerTiempoString(minutosParken)));
            obtenerPuntosParken(session.infoId());


        }
        if(opc == 2){

            puntosParken.setVisibility(View.VISIBLE);
            String pp = String.valueOf(puntosP) + "0";
            txtPuntos.setText(pp);

            monto.setVisibility(View.VISIBLE);
            Double subtotal = minutosParken*precioParken;
            String total = String.valueOf(subtotal);

            txtMonto.setText("$ "+total+" "+CURRENCY);

            //Actualizar el valor Puntos
            String leyend = "1 punto = $ " + String.valueOf(valorPuntos) + " " +CURRENCY;
            txtMonto2.setText(leyend);

            montoTotal.setVisibility(View.VISIBLE);
            precioFinal = subtotal - (puntosP*valorPuntos);
            if(precioFinal < 0){
                puntosRestante = (precioFinal*(-1));
                precioFinal = 0.0;

            }else{
                puntosRestante = 0.0;
            }

            String montoFinal = "$ " + String.valueOf(precioFinal) + "0. " + CURRENCY;
            txtTotal.setText(montoFinal);
            activarOpciones(3);
        }
        if(opc == 3){
            paypal.setVisibility(View.VISIBLE);
            //obtenerPaypal();
            txtPaypal.setText("marqc.oz@test.com");
            pay.setEnabled(true);
            pay.setBackgroundColor(Color.parseColor("#FF34495E"));
        }

    }

    public  void cargarDatos(){

        Intent intent = getIntent();
        if (null != intent) {
            origin = intent.getStringExtra("Activity");
            if(origin!=null)
            Log.d("CargarDatos", origin);
            //idEspacioParken = intent.getStringExtra("jsonEspacioParken");

            espacioParkenJson = intent.getStringExtra("jsonEspacioParken");
            if(espacioParkenJson!=null)
            Log.d("CargarDatos", espacioParkenJson);
            idSesionParken = intent.getStringExtra("idSesionParken");
            if(idSesionParken!=null)
            Log.d("CargarDatos", idSesionParken);
            cuentaPayPal = intent.getStringExtra("paypal");
            if(cuentaPayPal!=null)
            Log.d("CargarDatos", cuentaPayPal);



            if(espacioParkenJson!=null){
            try {
                JSONObject jsonArray = new JSONObject(espacioParkenJson);
                idEspacioParken = jsonArray.getString("id");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            }

        }

        fecha.setEnabled(false);
        hora.setEnabled(false);

        //origin = ACTIVITY_SESION;
        if(origin.equals(ACTIVITY_PARKEN)){
            timerTask.execute();
            _dialog = dialogTimeAlert();
            _dialog.show();
            //txtEspacioParken.setText(String.valueOf(session.getParkenSpace()));
            txtEspacioParken.setText(idEspacioParken);
            textVehiculo.setText("Selecciona un vehículo...");
            //Obtener la fecha actual
            selectedYear = obtenerFechaNow(2).get(Calendar.YEAR);
            selectedMonth = obtenerFechaNow(2).get(Calendar.MONTH);
            selectedDay= obtenerFechaNow(2).get(Calendar.DAY_OF_MONTH);

            txtTiempo.setText("Selecciona...");

            //txtFecha.setText(selectedDay+" "+obtenerMesNombre(selectedMonth+1)+", "+selectedYear);
            calendarFechaFinalFija = new GregorianCalendar(selectedYear,selectedMonth,selectedDay, 0,0);
            //txtHora.setText("Selecciona...");
            pay.setEnabled(false);
            pay.setBackgroundColor(Color.parseColor("#757575"));
            //pay.setVisibility(View.INVISIBLE);;
            montoPrevio = 0.0;
            tiempoPrevio = 0;
            opc = "1";

        }


        if(origin.equals(ACTIVITY_SESION)){
            if(intent.getStringExtra("Monto") != null)
            montoPrevio = Double.parseDouble(intent.getStringExtra("Monto"));

            tiempoPrevio = (intent.getIntExtra("TiempoEnMinutos", -1));
            ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle("Renovar sesión Parken");
            timer.setVisibility(View.GONE);
            txtEspacioParken.setText(idEspacioParken);
            vehiculo.setEnabled(false);
            if(intent.getStringExtra("idVehiculo") != null)
            idVehiculo = intent.getStringExtra("idVehiculo");
            if(intent.getStringExtra("MarcaVehiculo") != null)
            marcaVehiculo = intent.getStringExtra("MarcaVehiculo");
            if(intent.getStringExtra("ModeloVehiculo") != null)
            modeloVehiculo = intent.getStringExtra("ModeloVehiculo");
            if(intent.getStringExtra("PlacaVehiculo") != null)
            placaVehiculo = intent.getStringExtra("PlacaVehiculo");
            carro = modeloVehiculo + " - " + placaVehiculo;
            textVehiculo.setText(carro);

            calendarFechaFinal = Calendar.getInstance();
            calendarFechaFinalFija = Calendar.getInstance();
            try {
                if(intent.getStringExtra("FechaFinal") != null)
                calendarFechaFinal.setTime(sdf.parse(intent.getStringExtra("FechaFinal")));
                calendarFechaFinalFija.clear();
                calendarFechaFinalFija.setTime(sdf.parse(intent.getStringExtra("FechaFinal")));
                calendarFechaFinalFija.add(Calendar.MINUTE,5);
            } catch (ParseException e) {
                e.printStackTrace();

                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
                //Agregar una bandera para que el server sepa que solo va a actualizar el monto,
                //los puntos, la fechafinal y el tiempo
                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
            }

            calendarFechaFinal.add(Calendar.MINUTE, 5);


            //Obtener la fecha final de la sesion pasada
            /*
            selectedYear = calendarFechaFinal.get(Calendar.YEAR);
            selectedMonth = calendarFechaFinal.get(Calendar.MONTH);
            selectedDay= calendarFechaFinal.get(Calendar.DAY_OF_MONTH);
            */
            selectedYear = intent.getIntExtra("selectedYear", 0);
            selectedMonth = intent.getIntExtra("selectedMonth", 0);
            selectedDay= intent.getIntExtra("selectedDay", 0);
            selectedMin =  intent.getIntExtra("selectedMin", 0);
            selectedHour = intent.getIntExtra("selectedHour", 0);
            //txtFecha.setText(selectedDay+" "+obtenerMesNombre(selectedMonth+1)+", "+selectedYear);

            //selectedHour = calendarFechaFinal.get(Calendar.HOUR_OF_DAY);
            //selectedMin = calendarFechaFinal.get(Calendar.MINUTE);
            String min;

            if (selectedMin < 10) min = "0"+ String.valueOf(selectedMin);
            else min =String.valueOf(selectedMin);

            String lastHour = String.valueOf(selectedHour) + ":" + min + " hrs";

            //txtHora.setText("Selecciona...");
            txtTiempo.setText("Selecciona...");
            pay.setEnabled(false);
            pay.setText("Renovar sesión Parken");
            pay.setBackgroundColor(Color.parseColor("#757575"));
            //pay.setVisibility(View.INVISIBLE);;
            opc = "2";



        }





    }

    private void setupActionBar(boolean estatus) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(estatus);
            actionBar.setTitle("Pagar sesión Parken");
        }
    }

    @Override
    public void onBackPressed(){
        if(origin.equals(ACTIVITY_SESION)){
            super.onBackPressed();
        }

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

            mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSesionParkenFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mSesionParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class TimerTask extends AsyncTask<Integer, Integer, Boolean> {

        String msj2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtTimer.setText("Tiene 5:00 minutos para pagar");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String seg;

            if(values[1] == 4 && values[0] == 45 && _dialog!=null){
                _dialog.cancel();
            }
            if (values[0] >= 0 && values[0] < 10) {
                seg = "0" + Integer.toString(values[0]);
            } else {
                seg = Integer.toString(values[0]);
            }

            String min = Integer.toString(values[1]);

            if (values[1] == 0) {
                msj2 = " segundos para pagar";
            } else {
                msj2 = " minutos para pagar";
            }
            String msj1 = "Tiene ";


            //que solo pase cuando el tiempo seleccionado son 5 minutos menos
/*
            if(values[0]==0){
            if (selectedMin == 59) {
                selectedMin = 0;
                if (selectedHour == 23) {
                    selectedHour = 0;
                } else {
                    selectedHour = selectedHour + 1;
                }
            } else {
                selectedMin = selectedMin + 1;
            }

            String dat;
            if (selectedMin >= 0 && selectedMin < 10) {
                String sMin = "0" + String.valueOf(selectedMin);
                dat = selectedHour + ":" + sMin + " hrs";
            } else {
                dat = selectedHour + ":" + selectedMin + " hrs";
            }


            txtHora.setText(dat);
            }
*/


            txtTimer.setText(msj1 + min +":"+ seg +  msj2);

        }

        @Override
        protected Boolean doInBackground(Integer... params) {

            try {

                for(int j = 4; j >= 0 ; j--){
                for(int i = 59; i >=0; i--){
                    //for(int i = 3; i >=0; i--){



                    Thread.currentThread();
                    Thread.sleep(1000);
                    if(!isCancelled())
                        publishProgress(i,j);
                    else break;


                }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            dialogTimeOut().show();
        }

        @Override
        protected void onCancelled() {
            cancel(true);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null){
                    try {

                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "PAGO EXITOSO", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snackbar.show();
                        activarSesionParken(session.infoId(), idSesionParken, String.valueOf(precioFinal+montoPrevio), String.valueOf(minutosParken+tiempoPrevio), String.valueOf(idVehiculo), strDate, String.valueOf(puntosRestante), opc);
                        //Intent intent = new Intent(SesionParkenActivity.this, ParkenActivity.class);
                        //startActivity(intent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "ERROR", Snackbar.LENGTH_LONG);
                View sbView = snackbar.getView();
                sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snackbar.show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), "INVALIDO", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        snackbar.show();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

}
