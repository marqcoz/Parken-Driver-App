package com.parken.parkenv03;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ParkenActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ResultCallback<Status>,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{

    //Nombre de variables-----------------------------------------------------------------------------
    private static final String ACTION_NAVIGATE_ON_THE_WAY ="com.parken.parkenv03.NAVEGAR_EN_CAMINO";
    private static final String ACTION_CANCEL_ON_THE_WAY ="com.parken.parkenv03.CANCELAR_EN_CAMINO";
    private static final int NOTIFICATION_ID = 0;
    private static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static int NAVIGATE_REQUEST_CODE = 2;
    private static final String NOTIFICATION_MSG = "NOTIFICATION MSG";
    public static final String VIEW_PARKEN = "PARKEN";
    private static final String VIEW_ON_THE_WAY = "ON_THE_WAY";
    public static final String METHOD_PARKEN_SPACE_BOOKED = "GEOFENCE_IN";
    public static final String METHOD_PARKEN_SPACE_CHECK = "GEOFENCE_OUT";
    public static final String VIEW_PARKEN_SPACE_BOOKED = "PARKEN_SPACE_BOOKED";
    private static final String VIEW_PARKEN_SESSION_ACTIVE = "PARKEN_SESSION_ACTIVE";
    private static final String VIEW_PARKEN_REPORT = "PARKEN_REPORT";
    private static final int LOAD =100;
    private static final int REFRESH =200;
    public static final int PARKEN_SPACE_BOOKED = 50;
    public static final int PARKEN_SPACE_FOUND = 60;
    private static final int NOTIFICATION_ID_ON_THE_WAY = 1;
    private static final int NOTIFICATION_ID_GEOFENCE_ON_THE_WAY = 98;

    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;

    public static final String ACTIVITY_PARKEN = "ParkenActivity";
    public static final String PARKEN_ONNEWINTENT = "onNewIntent";
    public static final String ACTIVITY_ZONA_PARKEN = "ZonaParkenActivity";
    public static final String ACTIVITY_SESION_PARKEN = "SesionParkenActivity";
    public static final String METHOD_ON_LOCATION_CHANGED = "onLocationChanged";
    public static final String INTENT_GEOFENCE_ON_THE_WAY = "GeofenceOnTheWay";
    public static final String INTENT_GEOFENCE_PARKEN_BOOKED = "GeofenceParkenBooked";

    public static final String GEOFENCE_ONTHEWAY = VIEW_ON_THE_WAY;
    public static final String GEOFENCE_PARKEN_BOOKED = VIEW_PARKEN_SPACE_BOOKED;


    public static final String MESSAGE_NO_EP= "No hay EP disponibles";
    public static final String MESSAGE_FAILED= "Error al buscar";
    public static final String MESSAGE_EP_BOOKED= "EP Reservado";
    public static final String MESSAGE_AUTOMOVILISTA_BOOKED= "EP ya reservado";
    public static final String MESSAGE_PAY_FAILED = "Fallo el pago";
    public static final String MESSAGE_PAY_SUCCESS = "Pago exitoso";

    private String STATE_ON_THE_WAY;
    private String STATE_PAY_PARKEN;
    private boolean STATE_SPACE_BOOKED;
    private boolean STATE_PARKEN_SESSION;
    private boolean STATE_PARKEN_REPORT;

    private static final int FINISH_BUTTON = 35;
    private static final int FINISH_TIME = 20;





    //----------------------------------------------------------------------------------------------
    private NotificationReceiver mReceiver = new NotificationReceiver();

    private TextView txtEmailDriver;
    private TextView txtNameDriver;
    private TextView txtEstatusEParken;
    private TextView txtDireccionEParken;
    private TextView txtIDEParken;
    private TextView txtNotaEParken;
    private TextView txtReloj;
    private TextView txtAlert;

    private LinearLayout alertLayout;

    private FloatingActionButton find;

    private Button espacioParken;
    private Button navegar;
    private Button cancelar;
    private Button renovar;
    private Button finalizar;

    private ConstraintLayout image;

    private View mProgressView;
    private View mParkenFormView;
    private View alertLay;
    public View infoLay;

    private double lat;
    private double lng;


    private boolean requestEspacioParken = false;
    private boolean alertOn = false;


    private String zonaParkenJson;
    private String espacioParkenJson;
    private String idSesionParken;
    private String fechaFinal;
    private String montoFinal;
    private Calendar fechaPago;
    private String idVehiculo;
    private String modeloVehiculo;
    private String placaVehiculo;

    private String idPayPal = "project@parken.com";
    private String drawerTitle;

    private int mapaReady = 0;
    private int mapaListo = 0;

    private GoogleMap mMap;
    private Marker m;
    private Polyline polyline;
    private VolleySingleton volley;
    private ShPref session;
    protected RequestQueue fRequestQueue;
    public JsonObjectRequest jsArrayRequest;

    TimerTask timerTask;

    private LatLng destino;

    private Location lastLocation;

    private LatLngBounds DF;
    private LatLngBounds EP;

    private AlertDialog _dialog;
    private AlertDialog dialogParken;

    public static ParkenActivity activityParken;
    private ZonaParkenActivity actZonaParken;

    private GeofencingClient mGeofencingClient;
    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;



    //Variables importantes que me permitiran saber la vista actual de la app-----------------------
    private String vista;
    private boolean notificationSPSent = false;
    private String notificacionTime;
    private int tiempoEnMinutos;
    private int minPausa = 0;
    private int segPausa = 59;
    private int minContador;
    private int segContador;
    public static double longitudDestino;
    public static double latitudDestino;
    public static double longitudDestinoFinal;
    public static double latitudDestinoFinal;
    public static double longitudEspacioParken;
    public static double latitudEspacioParken;
    private String nombreDestino;
    public static String idEspacioParken;
    public static String idZonaParken;

    public boolean reembolso = false;
    public boolean geofenceParkenExit = false;

    //----------------------------------------------------------------------------------------------

    //Metodos
    //peticiones al Servidor
    //Vistas en el mapa
    //MOdificaciones al mapa
    //Metodos útilies
    //Metodos sobreescritos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AppEstatus", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parken);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityParken = this;
        session = new ShPref(activityParken);
        mGeofencingClient = LocationServices.getGeofencingClient(this);



        //Verificamos la versión de la API
        //Si es >23 pedimos acceso a la ubicación
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            // Versiones con android 6.0 o superior
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activityParken, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
                Log.d("CheckPermission", "Request");

            } else {
                if(session.loggedin()){
                    Log.d("CheckPermission", "CargarMapa");
                    loadMap();
                }else {
                    finish();
                    startActivity(new Intent(ParkenActivity.this, LoginActivity.class));
                }

            }

        } else {
            //Versiones anteriores a android 6.0
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0) {
                dialogPermissionRequired().show();
                finish();

            } else {
                if(session.loggedin()){
                    Log.d("CheckPermission", "CargarMapa");
                    loadMap();
                }else {
                    finish();
                    startActivity(new Intent(ParkenActivity.this, LoginActivity.class));
                }

            }
        }


        if(!session.loggedin()) {
            finish();
        startActivity(new Intent(ParkenActivity.this, LoginActivity.class));
        }

        //dialogTimerPicker(0).show();

        //Initialize and register the notification receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NAVIGATE_ON_THE_WAY);
        intentFilter.addAction(ACTION_CANCEL_ON_THE_WAY);
        registerReceiver(mReceiver, intentFilter);

        find = findViewById(R.id.searchZonaParkenButton);

        navegar = findViewById(R.id.btnNavegar);
        cancelar = findViewById(R.id.btnCancelar);

        renovar = findViewById(R.id.btnRenovar);
        finalizar = findViewById(R.id.btnFinalizar);

        mParkenFormView = findViewById(R.id.map_form);
        mProgressView = findViewById(R.id.parken_progress);
        infoLay = findViewById(R.id.InfoLayout);
        //declareElements();

        alertLay = findViewById(R.id.AlertLayout);

        txtEstatusEParken = findViewById(R.id.textViewEstatusEspacioParken);
        txtDireccionEParken = findViewById(R.id.textViewDireccionEspacioParken);
        txtIDEParken = findViewById(R.id.textViewIDParken);
        txtNotaEParken = findViewById(R.id.textViewNota);
        txtReloj = findViewById(R.id.textViewRelojito);
        txtAlert = findViewById(R.id.textViewAlert);

        volley = VolleySingleton.getInstance(getApplicationContext());
        fRequestQueue = volley.getRequestQueue();


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerheView = navigationView.getHeaderView(0);

        txtEmailDriver = headerheView.findViewById(R.id.textViewMailDriver);
        txtEmailDriver.setText(session.infoEmail());
        txtNameDriver = headerheView.findViewById(R.id.textViewNameDriver);
        image = headerheView.findViewById(R.id.linearLayoutInfo);
        String nameHeader = session.infoNombre() + " " + session.infoApellido();
        txtNameDriver.setText(nameHeader);

        createGoogleApi();


        //Listener de todos los botones
        /*
        Botón Buscar
        Mostrar un fragment con la búsqueda de lugares
         */
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            //Bound de la zona metrópolitana para la busqueda de direcciones
                            .setBoundsBias(new LatLngBounds(new LatLng(19.287745, -99.340258), new LatLng(19.845833, -98.753581))).build(activityParken);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                } catch (GooglePlayServicesRepairableException e) {

                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });


        //Al presionar el boton de Cancelar
        //Mostrar la confirmación
        //Debemos limpiar las variables
        //Limpiar el mapa
        //Mostrar el botón de busqueda
        navegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrirGPSBrowser(session.getLatDestino(), session.getLngDestino(), session.getNombreDestino());
                abrirGPSBrowser(latitudDestino, longitudDestino, nombreDestino);

            }
        });

        //Al presionar el boton de Solicitar Espacio Parken
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirmEspacioParkenOut().show();
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkenActivity.this, InformationActivity.class));
            }
        });

        renovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Proceso de renovacion
                //Abrir SesionPagoActivity
                //No se pone el timer
                //No cambia el espacio parken
                //No se cambia el vehiculo
                //La fecha final debe ser mayor a la comprada anteriormente
                //Si exito, entonces se actualiza la sesion parken
                //Se modifica unicamente el monto se le suma lo anterior
                //Se actualiza el tiempo final
                //Se elimina la actividad que manda una notificación
                //Se crea una nueva actividad que envie la notificación a la hora final
                //Se actualiza el timer, con el nuevo tiempo comprado
                //Se actualiza la vista de SESSION_ACTIVE
                //Si falla, no pasa nada, se regresa.

                Intent payParken = new Intent(ParkenActivity.this, SesionParkenActivity.class);
                payParken.putExtra("Activity", SesionParkenActivity.ACTIVITY_SESION);
                payParken.putExtra("jsonEspacioParken", espacioParkenJson);
                payParken.putExtra("idSesionParken", idSesionParken);
                payParken.putExtra("paypal", idPayPal);
                payParken.putExtra("FechaFinal", fechaFinal);
                payParken.putExtra("Monto", montoFinal);
                payParken.putExtra("TiempoEnMinutos", tiempoEnMinutos);
                payParken.putExtra("idVehiculo", idVehiculo);
                payParken.putExtra("ModeloVehiculo", modeloVehiculo);
                payParken.putExtra("PlacaVehiculo", placaVehiculo);
                payParken.putExtra("selectedMin", fechaPago.get(Calendar.MINUTE));
                payParken.putExtra("selectedHour", fechaPago.get(Calendar.HOUR_OF_DAY));
                payParken.putExtra("selectedYear", fechaPago.get(Calendar.YEAR));
                payParken.putExtra("selectedMonth", fechaPago.get(Calendar.MONTH));
                payParken.putExtra("selectedDay", fechaPago.get(Calendar.DAY_OF_MONTH));
                startActivity(payParken);





            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Finalizar", "PressButton");
                dialogFinishParken().show();
                //Bienvenido a finalizar
                //StartHour = 11:02 PM Junio 7
                //checarFinSesionParken(FINISH_BUTTON);

            }
        });

    }

    /**
     * Preguntamos en que vista nos encontramos
     *1 - Parken
     *2 - En camino
     *3 - Espacio Parken
     *4 - Sesion Parken
     *5 - Sancionada
     *
     *Hacemos una petición al servidor
     *Dependiendo del resultado
     *Mostramos las pantallas necesarias
     *
     *1 - parken()
     *2 - onTheWay()
     *3 - parkenSpace()
     *4 - parkenSesion()
     *5 - report()
     */


    public String verificarStatus(){
        String status = "";
        Log.d("vVerificarEstatus", "True");

        if(session.getOnTheWay()){
            Log.d("TEST", "1");
            status = VIEW_ON_THE_WAY;
        }else {

            Log.d("TEST", "2");
            Intent intent = getIntent();
            if (null != intent) {
                /*
                Log.d("TEST", "3");
                requestEspacioParken = intent.getBooleanExtra("espacioParkenRequested", false);
                zonaParkenJson = intent.getStringExtra("zonasParkenJson");
                session.setZonasParken(zonaParkenJson);
                latitudDestino = intent.getDoubleExtra("latitudDestino", 1.1);
                session.setLatDestino(latitudDestino);
                longitudDestino = intent.getDoubleExtra("longitudDestino", 1.1);
                session.setLngDestino(longitudDestino);
                Log.d("Verificar Estatus", String.valueOf(requestEspacioParken));
                Log.d("Verificar Estatus", "Viene de ZonaParkenActivity");
                */
                if (requestEspacioParken && !session.getCancel()) {
                    Log.d("TEST", "3");
                    session.setOnTheWay(true);
                    status = VIEW_ON_THE_WAY;
                    nombreDestino = intent.getStringExtra("nombreDestino");
                    session.setLatDestino(latitudDestino);
                    session.setLngDestino(longitudDestino);
                    Log.d("Verificar Estatus", "Keeping data");
                } // else { session.setOnTheWay(false); } //Si esta en false, debemos verificar que

                else {
                    Log.d("TEST", "3");
                    Log.d("Verificar Estatus", "No hay Intent");
                    status = VIEW_PARKEN;
                    session.setOnTheWay(false);
                }
                //Lo que vamos a hacer en este método será
                //checar en que estado se encuentra el usuario
                //Por ahora supondremos que no esta en ningun lado,
                //Entonces checaremos el sharedPreference  para saber si
                //la vairable onTheWay, si es true, entonces regrearemos
                //String ON_THE_WAY
            }

        }
        Log.d("Ver vistas", status);
        return status;
    }

    public void selectView(String vista, String origin){

        if(origin.equals(PARKEN_ONNEWINTENT)){
            switch (vista){
                case VIEW_PARKEN:
                    Log.d("VistaVerificarStatus", vista);
                    parken();
                    break;
                case VIEW_ON_THE_WAY:
                    Log.d("SelectView", vista);

                    onTheWay(LOAD);
                    break;
                case VIEW_PARKEN_SPACE_BOOKED:
                    parkenSpaceBooked(LOAD);
                    break;
                case VIEW_PARKEN_SESSION_ACTIVE:
                    parkenSession(LOAD);
                    break;
                case VIEW_PARKEN_REPORT:
                    break;
                default:
                    break;
            }
        }

        if(origin.equals(METHOD_ON_LOCATION_CHANGED)) {
            switch (vista) {
                case VIEW_PARKEN:
                    //readyMap();
                    //parken();
                    break;
                case VIEW_ON_THE_WAY:
                    onTheWay(REFRESH);
                    //centrarMapa(EP);
                    break;
                case VIEW_PARKEN_SPACE_BOOKED:
                    //parkenSpaceBooked(1);
                    break;
                default:
                    break;
            }
        }

        if(origin.equals("onMapLoad")){
            switch (vista){
                case VIEW_PARKEN:
                    Log.d("VistaVerificarStatus", vista);
                    parken();
                    break;
                case VIEW_ON_THE_WAY:
                    Log.d("VistaTime1", vista);
                    onTheWay(1);
                    break;
                case VIEW_PARKEN_SPACE_BOOKED:
                    break;
                case VIEW_PARKEN_SESSION_ACTIVE:
                    break;
                case VIEW_PARKEN_REPORT:
                    break;
                default:
                    break;
            }
        }

        if(origin.equals("LocationChanged")) {
            switch (vista) {
                case VIEW_PARKEN:
                    //readyMap();
                    //parken();
                    break;
                case VIEW_ON_THE_WAY:
                    //onTheWay(0);
                    //centrarMapa(EP);
                    break;
                case VIEW_PARKEN_SPACE_BOOKED:
                    //parkenSpaceBooked(1);
                    break;
                default:
                    break;
            }
        }
    }

    public void parken() {

        readyMap();
        STATE_ON_THE_WAY = null;
        //mMap.clear();
        Log.d("Flag", "1");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Asignar el titulo del toolbar a Parken
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Parken");
        //Ocultar todos los botones
        turnDownButtons();
        //Mostrar el botón de búsqueda
        find.setVisibility(View.VISIBLE);
        infoLay.setVisibility(View.INVISIBLE);
        //Mostrar la ubicación actual centrada en el mapa
        //onMapReady();
        //if(mapaReady != 1){
          //  Log.d("Flag", "Entro");
            readyMap();
    //}else{
            //Log.d("Flag", "noEntro");

        //}

    }

    public void onTheWay(int time) {

        if(time == LOAD){
            Log.d("OnTheWay", "LOAD");
        }else{  //time = REFRESH
            Log.d("OnTheWay", "REFRESH");
        }


        if(time == LOAD){

            //Asignar el titulo del toolbar a Parken
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("En camino");

            //Ocultar todos los botones
            turnDownButtons();

            //Mantenemos la pantalla siempre encendida, durante el trayecto
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //Codigo para devolver la pantalla a la normalidad
            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //Mostrar los botones disponibles para "En camino"
            navegar.setVisibility(View.VISIBLE);
            cancelar.setVisibility(View.VISIBLE);

            //Asignamos las coordenadas del destino
            //destino = new LatLng(session.getLatDestino(), session.getLngDestino());
            destino = new LatLng(latitudDestino, longitudDestino);

            //Mostramos el marker con el punto del destino
            if (nombreDestino != null) Log.d("Nombre destino", nombreDestino);
            mMap.addMarker(new MarkerOptions().position(destino).title("Destino").snippet(nombreDestino).icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));

            //Creamos la geocerca
            try {
                //startGeofence();
                startGeofence(GEOFENCE_ONTHEWAY);
            }catch (JSONException e) {
             e.printStackTrace();
            }

        }

        //Obtner el bound centrar
        EP = obtenerBoundPrincipal();

        //Centrar el mapa
        centerMap(EP);

        //Asignar las coordenadas de la posicion actual
        //Log.d("OnTheWayOrigin", String.valueOf(lastLocation.getLatitude())+" - "+String.valueOf(lastLocation.getLongitude()));
        LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        //destino = new LatLng(latitudDestino, longitudDestino);


        if(isOnlineNet()){
            alertLay.setVisibility(View.GONE);
            //espacioParken(String.valueOf(latitudDestino), String.valueOf(longitudDestino));
            //buscarEspacioParken(String.valueOf(session.getLatDestino()), String.valueOf(session.getLngDestino()), METHOD_PARKEN_SPACE_CHECK);
            buscarEspacioParken(String.valueOf(latitudDestino), String.valueOf(longitudDestino), METHOD_PARKEN_SPACE_CHECK);
            drawRoute(origin, destino);


        }else {
            alertLay.setVisibility(View.VISIBLE);
            txtAlert.setText("Sin conexión a Internet");
        }
    }



    public void parkenSpaceBooked(int time){

        if(time == LOAD){
            Log.d("SpaceBooked", "LOAD");
        }else{  //time = REFRESH
            Log.d("SpaceBooked", "REFRESH");
        }


        //En esta vista es cuando eliminamos la geocerca anterior y creamos la nueva geocerca
        //Nueva geocerca:
        //Unicamente con el centro del espacioParken asignado
        if(time == LOAD) {
            try {
            //Limpiar el mapa
            mMap.clear();

            //Eliminamos la geocerca OnTheWay
            clearGeofence(GEOFENCE_ONTHEWAY);

            //Se cambian las coordendas destino por las del espacio parken
            //session.setLatDestino(session.getLatDestinoParken());
            //session.setLngDestino(session.getLngDestinoParken());
            //Cambiamos las coordenadas de
            //EspacioParken -> DestinoFinal

            JSONObject jsonEspacioParken = new JSONObject(espacioParkenJson);
            JSONArray jsonArrayCentro = new JSONArray(jsonEspacioParken.getString("coordenada"));

            latitudEspacioParken = Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("longitud"));
            longitudEspacioParken = Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("latitud"));
            latitudDestino = latitudEspacioParken;
            longitudDestino = longitudEspacioParken;
            idEspacioParken = jsonEspacioParken.getString("id");
            idZonaParken = jsonEspacioParken.getString("zona");
            Log.d("JSONEspacioParken",jsonEspacioParken.getString("id"));



            //Limpiamos todos los botones del mapa
            turnDownButtons();

            //Asignar el titulo del toolbar a Parken
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle("Espacio Parken reservado");

            //Mostrar los botones disponibles para Espacio Parken Reservado
            navegar.setVisibility(View.VISIBLE);
            cancelar.setVisibility(View.VISIBLE);

            //Mantenemos la pantalla siempre encendida, durante el trayecto
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            //Mostramos el banner con la informacion del espacio Parken Asignado
            //mostrarInfoEspacioParken(String.valueOf(session.getParkenSpace()), PARKEN_SPACE_BOOKED);
            mostrarInfoEspacioParken(espacioParkenJson, PARKEN_SPACE_BOOKED);

            //destino = new LatLng(session.getLatDestino(), session.getLngDestino());
            destino = new LatLng(latitudDestino, longitudDestino);
            if (nombreDestino != null) Log.d("Nombre destino", nombreDestino);
            getLastKnownLocation();

            //Colocamos el ID del espacio Parken al marker
            //mMap.addMarker(new MarkerOptions().position(destino).title("ID Espacio Parken").snippet(String.valueOf(session.getParkenSpace())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));
            mMap.addMarker(new MarkerOptions().position(destino).title("ID Espacio Parken").snippet(jsonEspacioParken.getString("id")).icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));

            //Agregamos la geocerca SpaceParkenBooked

            startGeofence(VIEW_PARKEN_SESSION_ACTIVE); //Se agrega las coordenadas actualizadas del espacio Parken asignado

            } catch (JSONException e) {
                e.printStackTrace();
            }

            timerTask = new TimerTask();
            timerTask.execute(0, 59);

            //-----------------------------------
            //WEBSOCKETS
            //En esta parte abrimos la conexion para comenzar a contar
        }

        //Obtenemos el bound del mapa
        EP = obtenerBoundPrincipal();

        //Centramos el mapa
        centerMap(EP);

        //obtenemos las coordenadas de la posicion
        LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        // Getting URL to the Google Directions API
        //String url = getDirectionsUrl(origin, destino);
        //Before clear the route
        //Log.d("ParkenBooked", "NuevoDestino");

        //Dibujamos la ruta
        drawRoute(origin, new LatLng(latitudDestino, longitudDestino));
    }

    public void parkenSession(int time){
        if(time == LOAD){
            Log.d("ParkenSession", "LOAD");
        }else{  //time = REFRESH
            Log.d("ParkenSession", "REFRESH");
        }


        LatLng markerEspacioParken = new LatLng(latitudEspacioParken, longitudEspacioParken);
        //En esta vista es cuando eliminamos la geocerca anterior y creamos la nueva geocerca
        //Nueva geocerca:
        //Unicamente con el centro del espacioParken asignado
        if(time == LOAD) {
            //try {
                //Limpiar el mapa
                mMap.clear();
                dialogParken.cancel();
                dialogParken.dismiss();

                //Eliminamos la geocerca OnTheWay
                //clearGeofence(GEOFENCE_PARKEN_BOOKED);

                //Se resetean las coordendas destino

                latitudDestino = 0.0;
                longitudDestino = 0.0;

                //Limpiamos todos los botones del mapa
                turnDownButtons();

                //Asignar el titulo del toolbar a Parken
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle("Sesión Parken");

                //Mostrar los botones disponibles para Espacio Parken Reservado
                renovar.setVisibility(View.VISIBLE);
                finalizar.setVisibility(View.VISIBLE);

                //Codigo para devolver la pantalla a la normalidad
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                //Mostramos el banner con la informacion de la sesión Parken
                //mostrarInfoEspacioParken(String.valueOf(session.getParkenSpace()), PARKEN_SPACE_BOOKED);
                mostrarInfoSesionParken(tiempoEnMinutos);
                //mostrarInfoEspacioParken(espacioParkenJson, PARKEN_SPACE_BOOKED);

                //destino = new LatLng(session.getLatDestino(), session.getLngDestino());
                markerEspacioParken = new LatLng(latitudEspacioParken, longitudEspacioParken);

                //Colocamos el ID del espacio Parken al marker
                //mMap.addMarker(new MarkerOptions().position(destino).title("ID Espacio Parken").snippet(String.valueOf(session.getParkenSpace())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));
                mMap.addMarker(new MarkerOptions().position(markerEspacioParken).title("ID Espacio Parken").snippet(idEspacioParken));

                //Agregamos la geocerca SpaceParkenBooked
                //startGeofence(VIEW_PARKEN_SESSION_ACTIVE); //Se agrega las coordenadas actualizadas del espacio Parken asignado

            //} catch (JSONException e) {
              //  e.printStackTrace();
            //}
            //if(timerTask == null){
                timerTask = new TimerTask();
            //}else{
                //timerTask.cancel(true);
            //}
            timerTask.execute(minContador, segContador);

            //-----------------------------------
            //WEBSOCKETS
            //En esta parte abrimos la conexion para comenzar a contar
        }

        //Centramos el mapa en el espacio Parken
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(markerEspacioParken, 15);
        mMap.animateCamera(miUbicacion);

        //obtenemos las coordenadas de la posicion
        //LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        // Getting URL to the Google Directions API
        //String url = getDirectionsUrl(origin, destino);
        //Before clear the route
        //Log.d("ParkenBooked", "NuevoDestino");

        //Dibujamos la ruta
        //drawRoute(origin, new LatLng(latitudDestino, longitudDestino));

    }

    public void parkenReport(){

    }


    public void checarFinSesionParken(int trigger){

        TimerCheckParkenFinished timerCheckParkenFinished = new TimerCheckParkenFinished();

        switch (trigger){
            case FINISH_BUTTON:
                Log.d("Finalizar", "Switch");
                //Sensamos por 1 minuto
                timerTask.cancel(true);
                timerCheckParkenFinished.execute(1);
                break;
            case FINISH_TIME:
                //Sensamos por 5 minutos
                timerCheckParkenFinished.execute(1);
                break;
                default:
                    break;
        }



    }



    /**
     * Maps
     */
    public void loadMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap = googleMap;
        float zoom = 10f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(19.428970, -99.133464), zoom);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(cameraUpdate);

        Log.d("OnMapParkenReady", "Recarga");

        readyMap();
        mapaReady = 1;
        mapaListo = 1;


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                if(lastLocation==null) {
                    vista = verificarStatus();
                    session.setVista(vista);
                    mapaListo = 0;
                }else{
                    vista = verificarStatus();
                    session.setVista(vista);
                    Log.d("OnMapLoad", "Cargo el mapa");
                    Log.d("VistaOnMapLoad", vista);
                    selectView(vista, "onMapLoad");
                }
                Log.d("LoadMapa", "Cargo el mapa");

                //dialogParken().show();


            }
        });

    }

    public void readyMap(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(mMap != null) {
            Log.d("readyMap", "Load");
            mMap.setMyLocationEnabled(true);
            mMap.clear();
            UiSettings uiSettings = mMap.getUiSettings();
            uiSettings.setMapToolbarEnabled(false);

            if(lastLocation != null){
                Log.d("readyMap", "LastLocaction");
                float zoom = 15f;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                        zoom);
                mMap.animateCamera(cameraUpdate);
            }
        }
    }

    public void centerMap(LatLngBounds bound){
        if(mMap != null){
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
                CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngBounds(bound, 15);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
            mMap.animateCamera(miUbicacion);
/*
            float zoom = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()),
                    zoom);
            mMap.animateCamera(cameraUpdate);
            */
        }
    }

    public void newMap(Double latitud, Double longitud, Boolean marker){
        mMap.clear();
        // Add a marker in Sydney and move the camera
        LatLng destination = new LatLng(latitud, longitud);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(destination, 16);
        if(marker) {
            mMap.addMarker(new MarkerOptions().position(destination).title("Latitud: " + latitud + " Longitud: " + longitud));
        }else{

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 16));
        mMap.animateCamera(miUbicacion);
    }

    /**
     *Geocercas
     *
     */

    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My home";
    private static String GEOFENCE_DESTINATION_ID = "EspacioParken";
    private static float GEOFENCE_RADIUS = 150f; // in meters

    private void startGeofence(String geofence) throws JSONException {

        /*
        Geofence geofence = createGeofence( new LatLng(latitudDestino, longitudDestino), GEOFENCE_RADIUS, GEOFENCE_REQ_ID );
        GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
        addGeofence( geofenceRequest );
        */
        Log.d("StartGeofence", "TRUE");

        //Creamos la lista
        List<Geofence> mGeofenceList = new ArrayList<Geofence>();

        if(geofence.equals(VIEW_ON_THE_WAY)){
            // Geocercas con centro en el destino y las zonas Parken
            GEOFENCE_DESTINATION_ID = "Destino";
            GEOFENCE_RADIUS = 500.0f;
            //Parseamos el string a Json
            //JSONArray jsonArray = new JSONArray(session.getZonasParken());
            JSONArray jsonArray = new JSONArray(zonaParkenJson);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONArray jsonArrayCentroide = new JSONArray(jsonArray.getJSONObject(i).getString("centro"));

                mGeofenceList.add(createGeofence(new LatLng(Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("latitud"))), Float.parseFloat(jsonArray.getJSONObject(i).getString("radio")), jsonArray.getJSONObject(i).getString("id")));
            }
        }
            //Log.d("GeofenceCentro", String.valueOf(session.getLatDestino())+" - "+ String.valueOf(session.getLngDestino()));
            Log.d("GeofenceCentro", String.valueOf(latitudDestino)+" - "+ String.valueOf(longitudDestino));
            Log.d("GeofenceCentro", GEOFENCE_DESTINATION_ID);
            mGeofenceList.add(createGeofence(
                    //new LatLng(session.getLatDestino(), session.getLngDestino()),
                    new LatLng(latitudDestino, longitudDestino),
                    GEOFENCE_RADIUS,
                    GEOFENCE_DESTINATION_ID));

            GeofencingRequest geofenceRequest = createGeofenceRequest(mGeofenceList);
            //addGeofence( geofenceRequest, session.getVista());
            addGeofence( geofenceRequest, vista);



        if(geofence.equals(VIEW_PARKEN_SPACE_BOOKED)){ //Geocerca unica con centro en el espacio Parken asignado

        }


        if(session.getVista().equals(VIEW_ON_THE_WAY)) {
            Log.d("IniciarGeofence", VIEW_ON_THE_WAY);

        }
    }

    private Geofence createGeofence(LatLng latLng, float radius, String id) {
        Log.d("CrearGeocerca", "createGeofence");
        return new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Clear Geofence
    private void clearGeofence(String geofence) {
        Log.d("ClearGeofence", "clearGeofence()");
        if(geofence.equals(GEOFENCE_ONTHEWAY)) {
            Log.d("ClearGeofence", "ClearOnTheWayGeofence");
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, createGeofenceOnTheWayPendingIntent()).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.d("clearGeofence", "createGeofenceOnTheWayPendingIntent" );
                        // remove drawing
                        //removeGeofenceDraw();
                    }
                }
            });
        }

        if(session.getVista().equals(VIEW_PARKEN_SPACE_BOOKED)) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, createGeofenceOnTheWayPendingIntent()).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Log.d("ClerGeofence", "Clear GeofenceOnTheWay");
                    }
                }
            });
        }

        if(session.getVista().equals(VIEW_PARKEN_SESSION_ACTIVE)){
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, createGeofenceParkenBookedPendingIntent()).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        // remove drawing
                        //removeGeofenceDraw();
                    }
                }
            });

        }
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( List<Geofence> geofences ) {
    //private GeofencingRequest createGeofenceRequest( Geofence geofences ) {
        Log.d("Crear request Geofence", "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofences( geofences )
                .build();
    }

    private PendingIntent geoFenceOnTheWayPendingIntent;
    private final int GEOFENCE_REQ_CODE = 0;
    private PendingIntent createGeofenceOnTheWayPendingIntent() {
        Log.d("Geofence", "createGeofenceOnTheWayPendingIntent");
        if ( geoFenceOnTheWayPendingIntent != null )
            return geoFenceOnTheWayPendingIntent;

        Intent intent = new Intent( this, GeofenceOnTheWayTransitionsIntentService.class);
        return PendingIntent.getService(
              this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

    }

    private PendingIntent geoFenceParkenBookedPendingIntent;
    private final int GEOFENCE_PARKEN_BOOKED_REQ_CODE = 2;
    private PendingIntent createGeofenceParkenBookedPendingIntent() {
        Log.d("Geofence", "createGeofenceBookedPendingIntent");
        if ( geoFenceParkenBookedPendingIntent != null )
            return geoFenceParkenBookedPendingIntent;

        Intent intent = new Intent( this, GeofenceParkenBookedTransitionsIntentService.class);
        return PendingIntent.getService(
                this, GEOFENCE_PARKEN_BOOKED_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );

    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request, String vista) {
        Log.d("Geofence", "addGeofence");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(vista.equals(VIEW_ON_THE_WAY)){
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofenceOnTheWayPendingIntent())
                    .setResultCallback(this);
        }
        if(vista.equals(VIEW_PARKEN_SPACE_BOOKED)){
            LocationServices.GeofencingApi.addGeofences(
                    googleApiClient,
                    request,
                    createGeofenceParkenBookedPendingIntent())
                    .setResultCallback(this);
        }


    }

    private Circle geoFenceLimits;
    private void drawGeofence(LatLng loc) {
        Log.d("Dibujar Geofence", "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center(loc)
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits = mMap.addCircle( circleOptions );
    }


    // Create GoogleApiClient instance
    private void createGoogleApi() {
        Log.d("Google Api", "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }



/*
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }
    */


    /**
     * Dibujos sobre el mapa
     */
    private void dibujarEspacioParken(String espacioParken){

        try{
            JSONArray jsonArray = new JSONArray(espacioParken);
            for(int i = 0; i < jsonArray.length(); i++){

                JSONArray jsonArrayCentroide = new JSONArray(jsonArray.getJSONObject(i).getString("coordenada"));
                LatLng destination = new LatLng(Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("latitud")));
                mMap.addMarker(new MarkerOptions().position(destination));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mostrarInfoEspacioParken(String espacioParken, int estatus){

        Log.d("InfoEspacioParken", espacioParken);

        try{
            JSONObject jsonEspacioParken = new JSONObject(espacioParken);
            Log.d("JSONEspacioParken",jsonEspacioParken.getString("id") );
            Log.d("JSONEspacioParkenShPr",String.valueOf(session.getParkenSpace()));


            JSONArray jsonArrayCentro = new JSONArray(jsonEspacioParken.getString("coordenada"));

            session.setLatDestinoParken(Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("longitud")));
            session.setLngDestinoParken(Double.parseDouble(jsonArrayCentro.getJSONObject(0).getString("latitud")));
            LatLng destinationEspacioParken = new LatLng(session.getLatDestinoParken(), session.getLngDestinoParken());

            String idEP = "";
            String idStatusEP = "";
            String nota = "";

            if(estatus == PARKEN_SPACE_FOUND){

                if(jsonEspacioParken.getString("id").equals(String.valueOf(session.getParkenSpace())) && infoLay.isShown()) {
                    return;
                }

                session.setParkenSpace(Integer.parseInt(jsonEspacioParken.getString("id")));
                idEP = "ID Espacio Parken: " + session.getParkenSpace();
                idStatusEP = "El espacio Parken maś cercano se encuentra en:";
                obtenerDireccionJson(jsonArrayCentro.getJSONObject(0).getString("latitud"), jsonArrayCentro.getJSONObject(0).getString("longitud"));
                nota = "Aún no se ha reservado el espacio Parken";

                if(m != null){
                    m.remove();
                }

            }

            if(estatus == PARKEN_SPACE_BOOKED){
                session.setParkenSpace(Integer.parseInt(jsonEspacioParken.getString("id")));
                idEP = "ID Espacio Parken: " + session.getParkenSpace();
                idStatusEP = "Espacio Parken asignado. Dirígite hacia:";
                obtenerDireccionJson(jsonArrayCentro.getJSONObject(0).getString("latitud"), jsonArrayCentro.getJSONObject(0).getString("longitud"));
                nota = "Tienes 5 minutos para ocupar el espacio";
            }

                //Mostrar en el layout la Info
                infoLay.setVisibility(View.VISIBLE);

                txtIDEParken.setText(idEP);
                txtEstatusEParken.setText(idStatusEP);
                txtNotaEParken.setText(nota);

                 m = mMap.addMarker(new MarkerOptions().position(destinationEspacioParken));


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mostrarInfoSesionParken(int timer){

        Log.d("InfoSesionParken", idEspacioParken);

                String idEP = "ID Espacio Parken: " + idEspacioParken;
                String idStatusEP = "La ubicación de tu vehiculo" + modeloVehiculo + "- " + placaVehiculo;
                //obtenerDireccionJson(jsonArrayCentro.getJSONObject(0).getString("latitud"), jsonArrayCentro.getJSONObject(0).getString("longitud"));

                String nota = "Timer: " + String.valueOf(timer);



                String relojHora = String.valueOf(fechaPago.get(Calendar.HOUR_OF_DAY));
                String relojMinuto = String.valueOf(fechaPago.get(Calendar.MINUTE));

                if(fechaPago.get(Calendar.HOUR_OF_DAY)<10){
                    relojHora = "0"+ String.valueOf(fechaPago.get(Calendar.HOUR_OF_DAY));
                }
                if(fechaPago.get(Calendar.MINUTE)<10){
                    relojMinuto = "0"+ String.valueOf(fechaPago.get(Calendar.MINUTE));
                }

                String reloj = "Hora de salida: " + relojHora + ":" + relojMinuto + " hrs";




            //Mostrar en el layout la Info
            infoLay.setVisibility(View.VISIBLE);
            txtReloj.setVisibility(View.VISIBLE);

            txtIDEParken.setText(idEP);
            txtEstatusEParken.setText(idStatusEP);
            txtNotaEParken.setText(nota);
            txtReloj.setText(reloj);

            //m = mMap.addMarker(new MarkerOptions().position(destinationEspacioParken));
    }

    public void mostrarInfoSesionParkenFinalizada(){

        //Tienes 5 minutos para desocupar el espacio Parken
        //Presiona Finalizar cuando hayas liberado el espacio
        //O serás acreedor a una sanción

        String idEP = "ID Espacio Parken: " + idEspacioParken;
        String idStatusEP = "La ubicación de tu vehiculo" + modeloVehiculo + "- " + placaVehiculo;
        String nota = "Presiona Finalizar cuando hayas liberado el espacio o serás acreedor a una sanción";
        String reloj = "Tienes 5 minutos para desocupar el espacio Parken";




        //Mostrar en el layout la Info
        infoLay.setVisibility(View.VISIBLE);
        txtReloj.setVisibility(View.VISIBLE);

        txtIDEParken.setText(idEP);
        txtEstatusEParken.setText(idStatusEP);
        txtNotaEParken.setText(nota);
        txtReloj.setText(reloj);

        //m = mMap.addMarker(new MarkerOptions().position(destinationEspacioParken));
    }


    private void drawRoute(LatLng origen, LatLng destino) {
        // Getting URL to the Google Directions API
        //String url = getDirectionsUrl(origin, destino);
        //Before clear the route
        //Log.d("OnTheWayURL", String.valueOf(session.getLatDestino())+" - "+String.valueOf(session.getLngDestino()));
        //String url = getDirectionsUrl(origin, new LatLng(session.getLatDestino(),session.getLngDestino()));
        //String url = getDirectionsUrl(origin, new LatLng(latitudDestino, longitudDestino));
        String url = getDirectionsUrl(origen, destino);
        ParkenActivity.DownloadTask downloadTask = new ParkenActivity.DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    /**
     * Funciones Parken
     */
    public void abrirGPSBrowser(Double latitud, Double longitud, String label){

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California"));
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(mapIntent, PackageManager.GET_META_DATA);

        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (!isIntentSafe) {
            Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "No tienes apps instaladas para navegar.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            snackbar.show();

            //Snackbar.make(this.getWindow().getDecorView().findViewById(android.R.id.content), "Debe aceptar los términos y condiciones.", Snackbar.LENGTH_LONG)
              //      .show();
        }else{

            Uri intentUri=null;

            switch (session.getGPS()){
                case "Waze":
                    intentUri = Uri.parse(String.format("waze://?ll=%f,%f&navigate=yes", latitud, longitud));
                    Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                    startActivity(intent);
                    break;

                case "Maps":
                    intentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=d",latitud,longitud));
                    mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                    break;


                    default:
                        String geoLabel = "geo:<lat>,<lon>?q=<lat>,<lon>(label)";
                        String zoom = "16";
                        //Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
                        intentUri = Uri.parse(geoLabel.replace("lat",String.valueOf(latitud)).replace("lon", String.valueOf(longitud)).replace("label", label));
                        intent = new Intent(Intent.ACTION_VIEW, intentUri);
                        startActivity(intent);
                        break;

            }
        }
    }

    public void setParkenSpaceBooked(int ep, String jsonEP){
        session = new ShPref(activityParken);
        session.setParkenSpace(ep);
        session.setJSONParkenSpace(jsonEP);
    }

    public void setLogIn(boolean login){
        session = new ShPref(activityParken);
        session.setLoggedin(true);
    }

    public void setViewParkenSpaceBooked(String vep){
        session = new ShPref(activityParken);
        session.setVista(vep);
        Log.d("GeofenceView", session.getVista());
    }

    public void actualizarInformacion(){
        //setContentView(R.layout.nav_header_parken);
        txtEmailDriver = findViewById(R.id.textViewMailDriver);
        txtEmailDriver.setText(session.infoEmail());
    }

    public void sesionParkenActiva(){
        //Ocultar los demas botones
        navegar.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        //Mostrar el botón de renovar
        renovar.setVisibility(View.VISIBLE);
        //Mostrar el botón de finalizar
        finalizar.setVisibility(View.VISIBLE);
        //Cambiar el titulo del toolbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Sesión Parken activa");
        //Mostrar el mapa con el marker del espacio Parken
        //Mostrar la información de la sesión
        //Dirección
        //Espacio Parken
        //Tiempo restante

    }

    public void turnDownButtons(){
        find.setVisibility(View.INVISIBLE);
        navegar.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        renovar.setVisibility(View.INVISIBLE);
        finalizar.setVisibility(View.INVISIBLE);
    }

    public void turnDownPreferences(){
        //Location Destino
        session.setLatDestino(0.0);
        session.setLngDestino(0.0);

        //Vista
        session.setOnTheWay(false);
        verificarStatus();

        //EspacioParkenAsignado
        session.setParkenSpace(0);
    }

    public void cancelarSolicitudEspacioParken(){
        turnDownPreferences();
        requestEspacioParken = false;
        latitudDestino = 0.0;
        longitudDestino = 0.0;
        mMap.clear();

        newMap(lastLocation.getLatitude(), lastLocation.getLongitude(), false);

        find.setVisibility(View.VISIBLE);
        navegar.setVisibility(View.INVISIBLE);
        cancelar.setVisibility(View.INVISIBLE);
        infoLay.setVisibility(View.INVISIBLE);
        session.setCancel(true);
        session.setOnTheWay(false);
        vista = VIEW_PARKEN;
        session.setVista(vista);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Parken");
        //cancelNotificationOnTheWay();

        ParkenActivity.DownloadTask downloadTask = new ParkenActivity.DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.cancel(true);
        parken();
    }



    /**
     * Location
     */

    /**
     * LocationListener
     * Métodos que registra que los cambios
     * en el GPS y la uicación del dispositivo
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged","TRUE");

        //Lo que haremos aqui es REFRESH actualizar la

        lastLocation = location;
        Log.d("onLocationChanged", String.valueOf(latitudDestino) +" - "+ String.valueOf(longitudDestino));
        //if(session.getVista() != null) {
        if(vista != null) {
            Log.d("LocationChangedViewShPr",session.getVista());
            Log.d("LocationChangedVista", vista);
            //selectView(session.getVista(), METHOD_ON_LOCATION_CHANGED);
            selectView(vista, METHOD_ON_LOCATION_CHANGED);
        }else{
            Log.d("LocationChangedVista", "NULL");
        }

    }

    // Start location Updates
    private void startLocationUpdates(){
        Log.d("StartLocation", "startLocationUpdates()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    // Get last known location
    private void getLastKnownLocation() {
        Log.d("GetLastKnownLocation", "getLastKnownLocation()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            alertNoGPS().show();
            alertLay.setVisibility(View.VISIBLE);
            txtAlert.setText("Buscando la señal del GPS");
        }else{
            alertLay.setVisibility(View.GONE);
        }

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.d("GetLastKnownLocation", "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                //writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w("GetLastKnownLocation", "No location retrieved yet");
                startLocationUpdates();
            }
    }


    /**
     * Bounds
     */
    public LatLngBounds obtenerBoundPrincipal(){

        int points = 2;
        double coordinatesNorthLat[] = new double[points];
        double coordinatesNorthLng[] = new double[points];
        double coordinatesSouthLat[] = new double[points];
        double coordinatesSouthLng[] = new double[points];

        LatLng destination = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        double radius = 200;

        LatLng targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
        LatLng targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

        coordinatesNorthLat[0] = targetNorthEast.latitude;
        coordinatesNorthLng[0] = targetNorthEast.longitude;
        coordinatesSouthLat[0] = targetSouthWest.latitude;
        coordinatesSouthLng[0] = targetSouthWest.longitude;

        //destination = new LatLng(session.getLatDestino(), session.getLngDestino());
        Log.d("obtenerBoundPrincipal", String.valueOf(latitudDestino) +" - "+ String.valueOf(longitudDestino));
        destination = new LatLng(latitudDestino, longitudDestino);
        //destination = destino;

        radius = 200;

        targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
        targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

        coordinatesNorthLat[1] = targetNorthEast.latitude;
        coordinatesNorthLng[1] = targetNorthEast.longitude;
        coordinatesSouthLat[1] = targetSouthWest.latitude;
        coordinatesSouthLng[1] = targetSouthWest.longitude;

        return obtenerBoundary(coordinatesNorthLat, coordinatesNorthLng, coordinatesSouthLat, coordinatesSouthLng);
    }

    public LatLngBounds obtenerBoundary(double[] northLat, double[] northLng, double [] southLat, double[] southLng){

        LatLng min = new LatLng(getMin(southLat),getMin(southLng));
        LatLng max = new LatLng(getMax(northLat),getMax(northLng));

        //Log.d("CoordenadaMin",getMin(southLat)+", "+getMin(southLng));
        //Log.d("CoordenadaMax",getMax(northLat)+", "+getMax(northLng));

        return new LatLngBounds(min, max);
    }


    /**
     * POST JSON
     * Peticiones al server
     *
     */
    public void obtenerCoordenadasJson(String peticion){
        peticion = Jeison.URL_GET_DIRECTION.replaceAll("~ADDRESS~", peticion);
        Log.d("URL", peticion);
        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                peticion,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            double longitud = ((JSONArray)response.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");
                            Log.d("LoginActivity", response.toString());

                            Log.d("Longitud:", String.valueOf(longitud));
                            double latitud = ((JSONArray)response.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            Log.d("LoginActivity", response.toString());

                            Log.d("Latitud:", String.valueOf(latitud));
                            //latitudDestino = latitud;
                            //longitudDestino = longitud;
                            latitudDestinoFinal = latitud;
                            longitudDestinoFinal = longitud;
                            latitudDestino = latitudDestinoFinal;
                            longitudDestino = longitudDestinoFinal;
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ParkenActivity.this);
                            String distancia = pref.getString("distance_zonaParken", "1000");
                            //Log.d("DistanciaZonaParken", distancia);
                            buscarZonaParken(String.valueOf(latitud), String.valueOf(longitud), distancia);

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogParkenZoneFailed().show();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        dialogParkenZoneFailed().show();
                        return;
                    }
                }
        );

        fRequestQueue.add(jsArrayRequest);

    }



    public void buscarZonaParken(final String lat, final String longi, String distancia) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("latitud", lat);
        parametros.put("longitud", longi);
        parametros.put("distancia", distancia);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_FIND_PARKEN_ZONE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("ZonaParken", response.toString());

                                //Dibujamos el marker del destino del usuario
                                //newMap(Double.parseDouble(lat), Double.parseDouble(longi), false);
                                //Dibujamos todas las zonas Parken

                                //dibujarZonasParken(response.getString("ZonasParken"));
                                showProgress(false);
                                find.setVisibility(View.INVISIBLE);

                                Intent zonaparken = new Intent(ParkenActivity.this,ZonaParkenActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                zonaparken.putExtra("zonaParkenJson", response.getString("ZonasParken"));
                                zonaparken.putExtra("latitudDestino", latitudDestinoFinal);
                                zonaparken.putExtra("longitudDestino",longitudDestinoFinal);
                                zonaparken.putExtra("direccionDestino",nombreDestino);
                                ZonaParkenActivity actZonaParken = new ZonaParkenActivity();
                                startActivity(zonaparken);

                                //finish();

                            }else{
                                showProgress(false);
                                dialogNoParkenZone().show();

                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        //onConnectionFailed(error.getMessage());
                        Log.d("ZonaParken", "Error Respuesta en JSON: " + error.getMessage());
                        dialogParkenZoneFailed().show();
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void obtenerDireccionJson(String lat, String lng){
        String peticion;
        peticion = Jeison.URL_GET_LATLNG.replaceAll("~LAT", lat).replaceAll("~LNG", lng);
        Log.d("URL", peticion);
        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                peticion,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            String formattedAddress = ((JSONArray)response.get("results")).getJSONObject(0)
                                    .getString("formatted_address");
                            Log.d("LoginActivity", response.toString());
                            //Aqui ya podemos modificar la etiqueta
                            txtDireccionEParken.setText(formattedAddress);



                            //latitudDestino = latitud;
                            //longitudDestino = longitud;
                            //buscarZonaParken(String.valueOf(latitud), String.valueOf(longitud), String.valueOf(100000));
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LoginActivity", "Error Respuesta en JSON: " + error.getMessage());
                        showProgress(false);
                        dialogParkenZoneFailed().show();
                        return;
                    }
                }
        );

        fRequestQueue.add(jsArrayRequest);

    }

    public void buscarEspacioParken(final String latitudDestino, final String longitudDestino, final String origen) {
    //public String[] buscarEspacioParken(final String latitudDestino, final String longitudDestino) {
        //final String[] espacioParken = new String[2];

        HashMap<String, String> parametros = new HashMap();
        parametros.put("latitud", latitudDestino);
        parametros.put("longitud", longitudDestino);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_FIND_PARKEN_SPACE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("BuscandoEspacioParken", response.toString());
                                mostrarInfoEspacioParken(response.toString(), PARKEN_SPACE_FOUND);
                                if(origen.equals(METHOD_PARKEN_SPACE_BOOKED)){
                                    //apartarEspacioParken(response.getString("id"), response.getString("zona"), session.infoId());
                                    apartarEspacioParken(response.toString(), session.infoId());

                                }else{
                                    mostrarInfoEspacioParken(response.toString(), PARKEN_SPACE_FOUND);
                                }
                                //espacioParken[0]= response.getString("id");
                                //espacioParken[1]= response.getString("zona");


                                return;

                            }else{
                                Log.d("BuscandoEspacioParken", "Espacios no disponibles");
                                return;

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("BuscandoEspacioParken", "Error Respuesta en JSON: " + error.getMessage());
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        //Ocultar la barra y mostrar la barra indicando error de conexión
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void obtenerEspacioParken(final String lat, final String longi) {
        HashMap<String, String> parametros = new HashMap();
        parametros.put("latitud", lat);
        parametros.put("longitud", longi);
        parametros.put("distancia", "10000");

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_PARKEN_SPACE,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("EspacioParken", response.toString());
                                dibujarEspacioParken(response.getString("EspaciosParken"));

                            }else{

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ZonaParken", "Error Respuesta en JSON: " + error.getMessage());
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    //Método que actualiza el estatus del espacio Parken asignado
    //public void apartarEspacioParken(final String idEspacio, final String zona, final String idAutomovilista) {
    public void apartarEspacioParken(String response, final String idAutomovilista) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idEspacioParken", jsonObject.getString("id"));
        parametros.put("idZonaParken",jsonObject.getString("zona"));
        parametros.put("idAutomovilista", idAutomovilista);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_PARKEN_SPACE_BOOK,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            if(response.getString("success").equals("1")){

                                Log.d("EspacioParken", response.toString());
                                //dibujarEspacioParken(response.getString("EspaciosParken"));
                                mostrarInfoEspacioParken(response.toString(), PARKEN_SPACE_BOOKED);
                                vista = VIEW_PARKEN_SPACE_BOOKED;
                                session.setVista(vista);
                                parkenSpaceBooked(1);

                            }else{
                                Log.d("EspacioParken", response.toString());
                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ZonaParken", "Error Respuesta en JSON: " + error.getMessage());
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    //Método que crea un reporte de la zona parken
    //public void apartarEspacioParken(final String idEspacio, final String zona, final String idAutomovilista) {
    public void crearReporte(String IdAutomovilista, String Estatus, String Tipo, String Observaciones, String IdEpacioParken, String IdZonaParken) throws JSONException {
        Log.d("CrearReporte", IdEpacioParken + " - " + IdZonaParken);
        HashMap<String, String> parametros = new HashMap();
        parametros.put("idAutomovilista", IdAutomovilista);
        parametros.put("Estatus", Estatus);
        parametros.put("Tipo", Tipo);
        parametros.put("Observaciones", Observaciones);
        parametros.put("idEspacioParken",IdEpacioParken);
        parametros.put("idZonaParken", IdZonaParken);


        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_CREATE_REPORT,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CrearReporte", response.toString());
                        try{
                            if(response.getString("success").equals("1")){
                                //Eliminar sesionParken
                                //idSesionParken
                                //eliminarSesionParken(idSesionParken);

                            }else{
                                showProgress(false);
                                //Algo salió mal
                                dialogFailed().show();

                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogFailed().show();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("CrearReporte", "Error Respuesta en JSON: " + error.getMessage());
                        dialogFailed().show();
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }

    //Método que crea un reporte de la zona parken
    //public void apartarEspacioParken(final String idEspacio, final String zona, final String idAutomovilista) {
    public void eliminarSesionParken(final String idSesionParken) {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSesionParken", idSesionParken);


        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_DELETE_SESSION,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EliminarSesion", response.toString());
                        try{
                            if(response.getString("success").equals("1")){
                                showProgress(false);
                                //Regresamos las coordendas originales
                                latitudDestino = latitudDestinoFinal;
                                longitudDestino = longitudDestinoFinal;
                                latitudEspacioParken = 0.0;
                                longitudEspacioParken = 0.0;
                                Log.d("LimpiarCoordenadasEP", String.valueOf(latitudEspacioParken) + " - " + String.valueOf(longitudEspacioParken));
                                vista = VIEW_ON_THE_WAY;
                                selectView(vista, PARKEN_ONNEWINTENT);


                            }else{
                                showProgress(false);
                                //Algo salió mal
                                dialogFailed().show();

                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogFailed().show();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("EliminarSesion", "Error Respuesta en JSON: " + error.getMessage());
                        dialogFailed().show();
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    //Método que crea un reporte de la zona parken
    //public void apartarEspacioParken(final String idEspacio, final String zona, final String idAutomovilista) {
    public void desactivarSesionParken(String estatus, String idSesionParken) {

        HashMap<String, String> parametros = new HashMap();
        parametros.put("idSesionParken", idSesionParken);
        parametros.put("Estatus", estatus);

        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                Request.Method.POST,
                Jeison.URL_DRIVER_DEACIVATE_SESSION_PARKEN,
                new JSONObject(parametros),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        Log.d("DesactivarSesionParken", response.toString());
                        try{
                            if(response.getString("success").equals("1")){

                            }else{
                                //Algo salió mal
                                dialogFailed().show();

                            }

                            return;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            showProgress(false);
                            dialogFailed().show();
                            return;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                        Log.d("DesactivarSesionParken", "Error Respuesta en JSON: " + error.getMessage());
                        dialogFailed().show();
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        return;
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }


    /**
     * Notifications
     */
    public void notificationOnTheWay(){
/*
        Intent snoozeIntent = new Intent(this, MyBroadcastReceiver.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);


*/

        Intent cancelIntent = new Intent(ACTION_CANCEL_ON_THE_WAY);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast
                (ParkenActivity.this, NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent navigateIntent = new Intent(ACTION_NAVIGATE_ON_THE_WAY);
        PendingIntent navigatePendingIntent = PendingIntent.getBroadcast
                (ParkenActivity.this, NOTIFICATION_ID, navigateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent i = new Intent(ParkenActivity.this,ParkenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,PendingIntent.FLAG_NO_CREATE);


        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        int icono = R.mipmap.ic_launcher;

        String message = "Dirígete hacia tu destino. "+"Estamos buscando el mejor espacio Parken para ti.";
        mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(icono)
                .setContentTitle("En camino")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .addAction(R.drawable.ic_menu_send,"Navegar", navigatePendingIntent)
                .addAction(R.drawable.places_ic_clear,"Cancelar", cancelPendingIntent)

                //.setVibrate(new long[]{100, 250, 100, 500})
                .setOngoing(true)
                .setAutoCancel(false);
        mNotifyMgr.notify(NOTIFICATION_ID_ON_THE_WAY, mBuilder.build());

    }

    public void cancelNotificationOnTheWay() {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
        nMgr.cancel(NOTIFICATION_ID_ON_THE_WAY);
    }

    private void notificationSPFinished(int minutos) {

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        Intent i = new Intent(getApplicationContext() ,ParkenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);

        String titulo = "Finalizando sesión Parken";
        String subtitulo = "Tu sesión finaliza en " + String.valueOf(minutos) + " minutos";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setColor(Color.RED)
                .setContentTitle(titulo)
                .setContentText(subtitulo)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_menu_send,"Renovar", pendingIntent)
                .addAction(R.drawable.places_ic_clear,"Finalizar", pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //.setFullScreenIntent(pendingIntent, false)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true);

        notificatioMng.notify(
                NOTIFICATION_ID_GEOFENCE_ON_THE_WAY, notificationBuilder.build());


    }


    /**
     * The broadcast receiver class for notifications. Responds to the update notification and
     * cancel notification pending intents actions.
     */
    public class NotificationReceiver extends BroadcastReceiver {

        /**
         * Gets the action from the incoming broadcast intent and responds accordingly
         * @param context Context of the app when the broadcast is received.
         * @param intent The broadcast intent containing the action.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_NAVIGATE_ON_THE_WAY:
                    startActivity(new Intent(ParkenActivity.this,ParkenActivity.class));
                    collapseStatusBar();
                    abrirGPSBrowser(session.getLatDestino(), session.getLngDestino(), session.getNombreDestino());


                    break;
                case ACTION_CANCEL_ON_THE_WAY:

                    startActivity(new Intent(ParkenActivity.this,ParkenActivity.class));
                    collapseStatusBar();
                    //parken();
                    //cancelNotificationOnTheWay();
                    dialogConfirmEspacioParkenOut().show();
                    //unregisterReceiver(mReceiver);
                    //cancelarSolicitudEspacioParken();

                    break;
            }
            return;

        }
    }


        /*
        LocationManager locationListener = new LocationManager() {

            @Override
            public void onLocationChanged(Location location) {
                actualizarUbicacion(location);
                Log.d("LocationChanged","ON");
                if(vista != null) {
                    Log.d("LocationChanged",vista);
                    selectView(vista, "LocationChanged");
                }
                //Aqui voy a enviar la solicitud al server de un espacio Parken
                //Siempre que el automovilista haya solicitado un espacio
                //buscarEspacioParken(String.valueOf(latitudDestino), String.valueOf(longitudDestino));
            }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if(alertOn){
                if(ParkenActivity.this._dialog!=null){
                    ParkenActivity.this._dialog.dismiss();
                }
            }
            readyMap();
            vista = "PARKEN";
            parken();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("GPS", "GPS Desactivado "+provider);
            if(provider.equals("gps")){
                if(actZonaParken.activityZonaParken != null){
                    actZonaParken.activityZonaParken.finish();
                }
                if(alertOn){
                    //alertNoGPS().dismiss();
                }else{
                    _dialog = alertNoGPS();
                    _dialog.show();
                }
                mMap.setMyLocationEnabled(false);
                find.setVisibility(View.INVISIBLE);
                }

            }
        };
        */

    /**
     * Result Methods
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                nombreDestino = place.getName().toString();
                Log.d("Place: ", place.getName().toString());
                String addressNoSpaces = place.getName().toString().replaceAll(" ","+");
                showProgress(true);
                obtenerCoordenadasJson(addressNoSpaces);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d("Place: ", status.getStatusMessage().toString());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if(requestCode == NAVIGATE_REQUEST_CODE){
            Log.d("NavigateActivity", "True");

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    Log.d("PermissionResult", "permission was granted, yay!");
                    if(session.loggedin()){
                        Log.d("PermissionResultLogIn", String.valueOf(session.loggedin()));
                        loadMap();
                    }
                    else {
                        Log.d("PermissionResultNotLog", String.valueOf(session.loggedin()));
                        finish();
                        startActivity(new Intent(ParkenActivity.this, LoginActivity.class));
                    }

                } else {
                    // permission denied, boo!
                    finish();
                }

            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    /**
     * Intents extraños
     */

    // Create a Intent send by the notification
    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, ParkenActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }


    /**
     * Override Methods
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        Log.d("AppEstatus", "onPause");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(verificarStatus().equals(VIEW_ON_THE_WAY)){
            notificationOnTheWay();
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("AppEstatus", "onStop");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //googleApiClient.disconnect();
        if(verificarStatus().equals(VIEW_ON_THE_WAY)){
            notificationOnTheWay();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("AppEstatus", "onDestroy");
        googleApiClient.disconnect();
        //En este método guardaremos la vista y los datos necesarios para regresar
        if(vista == null){
            Log.d("onDestroyVista","Vista=null");
        }else{
            Log.d("onDestroyVista",vista);



        switch(vista){
            case VIEW_PARKEN:
                //Clear everything
                break;
            case VIEW_ON_THE_WAY:

                break;
            case VIEW_PARKEN_SPACE_BOOKED:
                break;
            case VIEW_PARKEN_SESSION_ACTIVE:
                break;
            case VIEW_PARKEN_REPORT:
                break;
                default:
                    break;

        }
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //-----------------------------------------------------------------------------
    @Override
    protected void onResume() {
        googleApiClient.connect();
        Log.d("AppEstatus", "onResume");
        if(verificarStatus().equals(VIEW_ON_THE_WAY)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            cancelNotificationOnTheWay();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        Log.d("AppEstatus", "onStart");
        if(verificarStatus().equals(VIEW_ON_THE_WAY)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            cancelNotificationOnTheWay();
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        googleApiClient.connect();
        Log.d("AppEstatus", "onRestart");
        if(verificarStatus().equals(VIEW_ON_THE_WAY)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            cancelNotificationOnTheWay();
        }
        super.onRestart();
    }

    @Override
    protected void onNewIntent(Intent intent){
        //Recibe un nuevo Intent
        //Unicamente cuando recibe un intent
        //En esta parte se supone que recibiremos todas las actualizaciones que vienen de un nuevo intent
        Log.d("OnNewIntent", "TRUE");
        googleApiClient.connect();
        if(vista != null){
            Log.d("OnNewIntentVistaAnterior", vista);
        }

        if(intent == null){
            Log.d("OnNewIntent", "NULL");
        }else{
            if(intent.getStringExtra("Activity") != null) {
                Log.d("OnNewIntent", intent.getStringExtra("Activity"));
                switch (intent.getStringExtra("Activity")){
                    case ACTIVITY_ZONA_PARKEN:
                        vista = VIEW_ON_THE_WAY;
                        intent.getStringExtra("zonasParkenJson");
                        intent.getBooleanExtra("espacioParkenRequested",false);
                        latitudDestinoFinal = intent.getDoubleExtra("latitudDestino", 0.0);
                        longitudDestinoFinal =  intent.getDoubleExtra("longitudDestino", 0.0);
                        latitudDestino = latitudDestinoFinal;
                        longitudDestino = longitudDestinoFinal;
                        nombreDestino = intent.getStringExtra("nombreDestino");
                        zonaParkenJson = intent.getStringExtra("zonasParkenJson");
                        Log.d("OnNewIntent", String.valueOf(latitudDestino) +" - "+ String.valueOf(longitudDestino));
                        selectView(vista, PARKEN_ONNEWINTENT);
                        Log.d("OnNewIntent", String.valueOf(latitudDestino) +" - "+ String.valueOf(longitudDestino));
                        break;
                    case INTENT_GEOFENCE_ON_THE_WAY:
                        Log.d("OnNewIntentGeoOnTheWay", "TRUE");
                        if(STATE_ON_THE_WAY == null) {
                            switch (intent.getStringExtra("ActivityStatus")) {
                                case MESSAGE_EP_BOOKED:
                                    vista = VIEW_PARKEN_SPACE_BOOKED;
                                    Log.d("OnNewIntent", String.valueOf(latitudDestino) + " - " + String.valueOf(longitudDestino));
                                    Log.d("OnNewIntentBooked", intent.getStringExtra("idEspacioParkenAsignado"));
                                    espacioParkenJson = intent.getStringExtra("jsonEspacioParken");
                                    idSesionParken = intent.getStringExtra("idSesionParken");

                                    Log.d("OnNewIntentBooked", intent.getStringExtra("jsonEspacioParken"));
                                    selectView(vista, PARKEN_ONNEWINTENT);

                                    break;
                                case MESSAGE_NO_EP:
                                    vista = VIEW_PARKEN;
                                    dialogNoParkenSpaces().show();
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;
                                case MESSAGE_AUTOMOVILISTA_BOOKED:
                                    dialogBookedDone().show();
                                    vista = VIEW_PARKEN;
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;
                                case MESSAGE_FAILED:
                                    dialogFailed().show();
                                    vista = VIEW_PARKEN;
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;
                                default:
                                    vista = VIEW_PARKEN;
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;

                            }
                            STATE_ON_THE_WAY = "TRUE";
                        }
                        break;
                    case INTENT_GEOFENCE_PARKEN_BOOKED:
                        Log.d("OnNewIntentGeoPBoooked", "TRUE");
                        if(dialogParken == null){
                            dialogParken = dialogParken();
                            dialogParken.show();
                            Log.d("OnNewIntentDialog", "TRUE");
                        }

                        break;
                    case ACTIVITY_SESION_PARKEN:
                        Log.d("OnNewIntentSesionParken", "TRUE");
                        if(STATE_PAY_PARKEN == null) {
                            switch (intent.getStringExtra("ActivityStatus")) {
                                case MESSAGE_PAY_FAILED:
                                    vista = VIEW_PARKEN;
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;
                                case MESSAGE_PAY_SUCCESS:

                                    timerTask.cancel(true);
                                    if(intent.getBooleanExtra("clearPausa",true)){
                                        minPausa=0;
                                        segPausa = 59;
                                    }
                                    tiempoEnMinutos = intent.getIntExtra("TiempoEnMinutos", 0);
                                    idSesionParken = intent.getStringExtra("idSesionParken");
                                    fechaFinal = intent.getStringExtra("FechaFinal");
                                    montoFinal = intent.getStringExtra("Monto");
                                    idVehiculo = intent.getStringExtra("idVehiculo");
                                    modeloVehiculo = intent.getStringExtra("ModeloVehiculo");
                                    placaVehiculo = intent.getStringExtra("PlacaVehiculo");

                                    //Mostramos el dialog receipt
                                    double precioFinal = intent.getDoubleExtra("PrecioFinal", 0);
                                    float valorPuntos = intent.getFloatExtra("ValorPuntos", 0f);
                                    double puntosRestante = intent.getDoubleExtra("PuntosRestante", 0);
                                    long minutosParken = intent.getLongExtra("MinutosParken", 0);
                                    double puntosP = intent.getDoubleExtra("PuntosP", 0);


                                    if(fechaPago == null) {fechaPago = Calendar.getInstance();}
                                    fechaPago.add(Calendar.MINUTE, (int)minutosParken);
                                    int selectedHour = fechaPago.get(Calendar.HOUR_OF_DAY);
                                    int selectedMin = fechaPago.get(Calendar.MINUTE);
                                    int selectedDay = fechaPago.get(Calendar.DAY_OF_MONTH);
                                    int selectedMonth = fechaPago.get(Calendar.MONTH)+1;
                                    int selectedYear = fechaPago.get(Calendar.YEAR);

                                    Calendar dateRightNow = Calendar.getInstance();
                                    long restaMs = fechaPago.getTimeInMillis()-dateRightNow.getTimeInMillis();
                                    long segun = restaMs/1000;
                                    minContador = (int)(segun/60);
                                    if((int)(segun%60)==0){ segContador = 59; minContador=minContador-1; }
                                    else segContador = (int)(segun%60);
                                    Log.d("Contadores", String.valueOf(restaMs) +"-"+String.valueOf(segun) +"-"+String.valueOf(minContador) +"-"+String.valueOf(segContador));


                                    /*
                                    int selectedHour = intent.getIntExtra("SelectedHour", 0);
                                    int selectedMin = intent.getIntExtra("SelectedMin", 0);
                                    */

                                    dialogPayWithPoints(puntosP,
                                            precioFinal,
                                            valorPuntos,
                                            puntosRestante,
                                            minutosParken,
                                            selectedHour,
                                            selectedMin,
                                            selectedDay,
                                            selectedMonth,
                                            selectedYear
                                    )
                                            .show();

                                    vista = VIEW_PARKEN_SESSION_ACTIVE;
                                    selectView(vista, PARKEN_ONNEWINTENT);
                                    break;
                                    default:
                                        break;
                            }
                        }
                        break;

                        default:
                            vista = VIEW_PARKEN;
                            break;
                }
                Log.d("OnNewIntentVista", vista);
            }
        }



    }

    // GoogleApiClient.ConnectionCallbacks connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Connected", "onConnected()");
        getLastKnownLocation();
    }

    // GoogleApiClient.ConnectionCallbacks suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.w("ConnectionSuspended", "onConnectionSuspended()");
    }

    // GoogleApiClient.OnConnectionFailedListener fail
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w("ConnectionFailed", "onConnectionFailed()");
    }

    @Override
    public void onResult(@NonNull Status status) {
        Log.d("Tag", "onResult: " + status);
        if ( status.isSuccess() ) {
            //saveGeofence();
            //drawGeofence(new LatLng(session.getLatDestino(),session.getLngDestino()));
            drawGeofence(new LatLng(latitudDestino, longitudDestino));
        } else {
            // inform about fail
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //Log.d(TAG, "onMapClick("+latLng +")");
        //markerForGeofence(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable();
            //actionBar.setTitle("Crear cuenta");
            //actionBar
        }
    }

    /**
     * Navigation Bar
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.parken, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //dialogConfirmLogOut();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_pay_sanction) {
            finish();
            startActivity(new Intent(ParkenActivity.this,SesionParkenActivity.class));
        } else if (id == R.id.nav_cars) {
            startActivity(new Intent(ParkenActivity.this,VehiculoActivity.class));
        } else if (id == R.id.nav_pays) {


        } else if (id == R.id.nav_session) {
            startActivity(new Intent(ParkenActivity.this,SesionActivity.class));

        } else if (id == R.id.nav_sanction) {
            startActivity(new Intent(ParkenActivity.this,SancionActivity.class));

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(ParkenActivity.this,SettingsActivity.class));

        } else if (id == R.id.log_out) {
            dialogConfirmLogOut().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * AlertDialogs
     * Shows and defines the AlertDialogs.
     */
    public AlertDialog dialogParken() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        LayoutInflater inflater = activityParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_parken, null);
        //clearGeofence();
        builder.setView(v);
        builder.setCancelable(false);


        Button btnPagar = v.findViewById(R.id.buttonPagar);
        Button btnNEP = v.findViewById(R.id.buttonNoEP);

        btnPagar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Crear Cuenta...
                        //dismiss();
                        //Cancelamos el timer
                        timerTask.cancel(true);
                        Intent payParken = new Intent(ParkenActivity.this, SesionParkenActivity.class);
                        payParken.putExtra("Activity", SesionParkenActivity.ACTIVITY_PARKEN);
                        payParken.putExtra("jsonEspacioParken", espacioParkenJson);
                        payParken.putExtra("idSesionParken", idSesionParken);
                        payParken.putExtra("paypal", idPayPal);
                        startActivity(payParken);
                    }
                }
        );

        btnNEP.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            Log.d("CrearReporte", idEspacioParken + " - " + idZonaParken);
                            crearReporte(session.infoId(),"PENDIENTE","OCUPADO", "" , idEspacioParken, idZonaParken);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

        );


        return builder.create();
    }

    public AlertDialog dialogPayWithPoints(double puntosP,
                                           double precioFinal,
                                           float valorPuntos,
                                           double puntosRestante,
                                           long minutosParken,
                                           int selectedHour,
                                           int selectedMin,
                                           int selectedDay,
                                           int selectedMonth,
                                           int selectedYear) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        LayoutInflater inflater = activityParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_receipt, null);
        builder.setView(v);

        LinearLayout puntos = v.findViewById(R.id.linearLayoutPuntos);

        TextView txtMonto = v.findViewById(R.id.textViewMontoCobrado);
        TextView txtPuntosUsados = v.findViewById(R.id.textViewPuntosUsado);
        TextView txtPuntosActualizados = v.findViewById(R.id.textViewPuntosActuales);
        TextView txtTiempo = v.findViewById(R.id.textViewTiempo);
        TextView txtRecordatorio = v.findViewById(R.id.textViewRecordatorio);
        Button btnAceptar = (Button) v.findViewById(R.id.btnAceptar);

        if(puntosP == 0.0){
            puntos.setVisibility(View.GONE);
        }


        String mon = "$ " + String.valueOf(precioFinal) + "0 " + SesionParkenActivity.CURRENCY;
        txtMonto.setText(mon);
        String punUs = String.valueOf(((puntosP*valorPuntos)-puntosRestante));
        txtPuntosUsados.setText(punUs);
        String punAct = String.valueOf(puntosRestante);
        txtPuntosActualizados.setText(punAct);
        String tiem = String.valueOf(minutosParken) + " minutos";
        txtTiempo.setText(tiem);
        String reco;
        String fecha;

        Calendar hoy = Calendar.getInstance();

        if(hoy.get(Calendar.YEAR) == selectedYear &&
        hoy.get(Calendar.MONTH)+1 == selectedMonth &&
        hoy.get(Calendar.DAY_OF_MONTH) == selectedDay){
            fecha = "";
        }else{
            fecha = String.valueOf(selectedDay) + " - " + String.valueOf(selectedMonth) +" - " +String.valueOf(selectedYear) + " - ";
        }


        if(selectedMin < 10){
            reco = fecha + selectedHour + ":0" + selectedMin + " hrs";
        }else{
            reco = fecha + selectedHour + ":" + selectedMin + " hrs";
        }
        txtRecordatorio.setText(reco);


        final AlertDialog dialog = builder.create();

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });



        return dialog;
    }


    public AlertDialog dialogTimerPicker(final int minutosParken) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        LayoutInflater inflater = activityParken.getLayoutInflater();

        View v = inflater.inflate(R.layout.alertdialog_time, null);
        builder.setView(v);

        final EditText h = v.findViewById(R.id.editTextHoras);
        final EditText m = v.findViewById(R.id.editTextMinutos);

        Button establecer = v.findViewById(R.id.btnEstablecerTiempo);

        String horas = String.valueOf(minutosParken/60);
        String minutos;
        if((minutosParken)%60 == 0){
            minutos = "5";
        } else{
            minutos = String.valueOf((minutosParken)%60);
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

                if(m.getText().toString().equals("0") && h.getText().toString().equals("0")){
                    m.setText("5");
                }

                int min = ( Integer.parseInt( h.getText().toString() )*60 ) + Integer.parseInt(m.getText().toString());
                Log.d("MyTimerPicker", String.valueOf(min));
            }
        });

        return dialog;
    }


    public AlertDialog dialogEPTimeOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        builder.setTitle("Tiempo excedido")
                .setMessage("Se ha liberado el espacio Parken. Intenta de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogBookedDone() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Error 407")
                .setMessage("El automovilista ya tiene un espacio Parken reservado")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogFailed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Error")
                .setMessage("No se puede realizar la conexión con el servidor. Intenta de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogNoParkenSpaces() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Espacios Parken no disponibles")
                .setMessage("En este momento todos los espacios Parken están ocupados. Intenta más tarde.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return builder.create();
    }

    public AlertDialog alertNoGPS() {
        alertOn = true;
        final AlertDialog.Builder builderGPS = new AlertDialog.Builder(this);
        builderGPS.setTitle("GPS desactivado")
                .setMessage("Habilita la localización del dispositivo para poder utilizar Parken.")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        _dialog = null;
                        alertOn = false;
                    }
                })
                .setPositiveButton("Habilitar",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                _dialog = null;
                                alertOn = false;
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        alertOn = false;
                        _dialog = null;
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        alertOn = false;
                        _dialog = null;
                    }
                });
        return builderGPS.create();
    }

    private AlertDialog dialogPermissionRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Parken requiere tener acceso a tu ubicación").setMessage("Ingresa a la configuración de aplicaciones para habilitar el acceso a la ubicación de tu dispositivo.").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;

            }
        });

        return builder.create();
    }

    public AlertDialog dialogConfirmEspacioParkenOut() {
        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Cancelar")
                .setMessage("¿Desea cancelar la solicitud de un espacio Parken?")
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelarSolicitudEspacioParken();
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogFinishParken() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Cancelar sesión")
                .setMessage("¿Deseas finalizar la sesión Parken? Presiona OK únicamente cuando estes listo para mover tu vehículo. De lo contrario serás acreedor a una sanción")
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Finalizar", "DialogOK");
                                checarFinSesionParken(FINISH_BUTTON);
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogConfirmLogOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar la sesión?")
                .setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.setLoggedin(false);
                                getSharedPreferences("parken", Context.MODE_PRIVATE).edit().clear().commit();
                                session.clearAll();
                                finish();
                                startActivity(new Intent(ParkenActivity.this,LoginActivity.class));
                            }
                        });

        return builder.create();
    }

    public AlertDialog dialogNoParkenZone() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);
        builder.setTitle("Lo sentimos")
                .setMessage("No hay zonas Parken cercanas a tu destino. Estamos trabajando para estar en mas puntos de la ciudad.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return builder.create();
    }

    public AlertDialog dialogParkenZoneFailed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activityParken);

        builder.setTitle("Error al buscar zonas Parken")
                .setMessage("No se puede realizar la conexión con el servidor. Intente de nuevo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
        return builder.create();
    }

    /**
     * AsynkTask
     * Código con las tareas asincronas para mostrar la ruta en la app
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParkenActivity.ParserTask parserTask = new ParkenActivity.ParserTask();
            parserTask.execute(result);

        }

        @Override
        protected void onCancelled(){
            //ParkenActivity.ParserTask parserTask = new ParkenActivity.ParserTask();
            //parserTask.cancel(true);
            super.onCancelled();
        }

    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected void onPreExecute(){
            if(polyline!=null){
                //polyline.get
                //polylineAux = mMap.add
            }
            super.onPreExecute();
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            points = new ArrayList();
            lineOptions = new PolylineOptions();
            if (result == null) {
                cancel(true);
            }else {

                for (int i = 0; i < result.size(); i++) {


                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    lineOptions.addAll(points);
                    lineOptions.width(12);
                    lineOptions.color(Color.RED);
                    lineOptions.geodesic(true);
                }

                // Drawing polyline in the Google Map for the i-th route
                if (points.size() != 0) {
                    if (polyline != null) {
                        polyline.remove();
                    }
                    polyline = mMap.addPolyline(lineOptions);
                }
            }
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
        }

    }

    /**
     * Timer Asynk para manejar los tiempos
     */
    public class TimerTask extends AsyncTask<Integer, Integer, Boolean> {

        String msj2;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //txtTimer.setText("Tiene 5:00 minutos para pagar");

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            String seg;
            //Validación para el reembolso
            if(values[1] > 5) reembolso = true;
                    else reembolso = false;

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
                msj2 = " segundos "; // para pagar";
            } else {
                msj2 = " minutos "; // para pagar";
            }
            String msj1 = "Tienes ";


            //Aqui es donde indicamos en donde vamos a mostrarlo
            if(vista.equals(VIEW_PARKEN_SPACE_BOOKED)){
                String msj3 = "para llegar al espacio asignado";
                txtNotaEParken.setText(msj1 + min +":"+ seg +  msj2 + msj3);
            }
            if(vista.equals(VIEW_PARKEN_SESSION_ACTIVE)) {
                String msj3 = "";
                msj1 = "Tiempo restante: ";
                //txtNotaEParken.setText(msj1 + min + ":" + seg + msj2 + msj3);
                txtNotaEParken.setText("Tiempo restante: " + SesionParkenActivity.obtenerTiempoString((long) values[1]));
                //txtNotaEParken.setText(msj1 + min + ":" + seg + msj2 + msj3);


                //Aqui tambien checamos las notificaciones
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ParkenActivity.this);
                boolean noti = pref.getBoolean("notify_on", true);
                String notiTime = pref.getString("notify_time", "5");
                if(notificacionTime != null){
                    if(!notificacionTime.equals(notiTime)){
                        notificationSPSent = false;
                    }
                }
                notificacionTime = notiTime;

                if (noti) {

                    //if(Integer.parseInt(notiTime) > values[2] && values[1] == values[2]){
                    if (Integer.parseInt(notiTime) > values[2] && !notificationSPSent) {
                        notificationSPFinished(values[1]);
                        notificationSPSent = true;
                    } else {
                        if (Integer.parseInt(notiTime) == values[1] && values[0] == 0 && !notificationSPSent) {
                            notificationSPFinished(values[1]);
                            notificationSPSent = true;
                        }
                    }
                }
            }


        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            try {
                int aux;
                for(int j = params[0]; j >= 0 ; j--){
                    if(j == params[0]){ //Si es al inicio entonces
                        aux = params[1];
                    }else{
                        aux = 59;
                    }
                    //for(int i = 59; i >=0; i--){
                    for(int i = aux; i >=0; i--){
                        //for(int i = 3; i >=0; i--){



                        Thread.currentThread();
                        Thread.sleep(1000);
                        if(!isCancelled()){
                            publishProgress(i,j, params[0]);
                        }
                        else {
                            minPausa = j;
                            segPausa = i;
                            break;
                        }
                    }

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.d("TimerEnd", "TRUE");
            if(dialogParken != null){
                Log.d("TimerEnd", "!=Null");
                dialogParken.cancel();
            }
            dialogEPTimeOut().show();
            if(idSesionParken !=null){
               // eliminarSesionParken(idSesionParken);
            }

            if(vista.equals(VIEW_PARKEN_SESSION_ACTIVE)){
                //Si terminó el tiempo y no renovó ni finalizó
                //Entonces, deshabilitamos el botón RENOVAR
                renovar.setEnabled(false);
                renovar.setBackgroundColor(Color.parseColor("#FF34495E"));

                //Actualizar el banner SESION PARKEN FINALIZADA
                mostrarInfoSesionParkenFinalizada();

                //Estoy en duda de si mandar o no a la vista parken
                //vista = VIEW_PARKEN;
                //selectView(vista,PARKEN_ONNEWINTENT);

                checarFinSesionParken(FINISH_TIME);

            }




        }

        @Override
        protected void onCancelled() {
            //cancel(true);
            super.onCancelled();

        }
    }


    public class TimerCheckParkenFinished extends AsyncTask<Integer, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            Log.d("Finalizar", "StartTask");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("TimerProgressUpdate","TRUE");
            // geofenceParkenExit
            if(geofenceParkenExit){ //El automovilista salió del espacio Parken
                desactivarSesionParken("FINALIZADA", idSesionParken); //status = "FINALIZADA"
                //cancelar el timer
                //Si es cancelado entonces
                vista = VIEW_PARKEN;
                selectView(vista, PARKEN_ONNEWINTENT);
                onCancelled();
                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
                //Crear una lista con todos los errores
                //Ordenar el server
                //Ordenar los print en nodejs
                //Ordenar la app.
                //THis is a test for cehcking git
                //PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP
            }
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.d("Finalizar", "StartTask");
            //Contador
            try {
                for(int j = params[0]-1; j >= 0 ; j--){
                    for(int i = 59; i >=0; i--){
                        Thread.currentThread();
                        Thread.sleep(1000);
                        if(!isCancelled()){
                            publishProgress(i,j);
                        }
                        else {
                            break;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            try {
                Log.d("TimerPost","TRUE");
                desactivarSesionParken("REPORTADA", idSesionParken);
                crearReporte(session.infoId(),"PENDIENTE","TIMEOUT","",idEspacioParken,idZonaParken);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //vista = VIEW_PARKEN;
            //selectView(vista, PARKEN_ONNEWINTENT);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }



    /**
     *Métodos con funciones útiles
     */
    public void collapseStatusBar(){

        Object service  = getSystemService("statusbar");
        Class<?> statusbarManager = null;

        try {

            statusbarManager = Class.forName("android.app.StatusBarManager");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method collapse = null;

        try {

            if (Build.VERSION.SDK_INT >= 17) {
                collapse = statusbarManager.getMethod("collapsePanels");
            }
            else {
                collapse = statusbarManager.getMethod("collapse");
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {

            collapse.invoke(service);

        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (InvocationTargetException e) {
            e.printStackTrace();

        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+getString(R.string.google_maps_directions_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;


        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    public boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Method for getting the maximum value
    public static double getMax(double[] inputArray){
        double maxValue = inputArray[0];
        for(int i=1;i < inputArray.length;i++){
            if(inputArray[i] > maxValue){
                maxValue = inputArray[i];
            }
        }
        return maxValue;
    }

    // Method for getting the minimum value
    public static double getMin(double[] inputArray){
        double minValue = inputArray[0];
        for(int i=1;i<inputArray.length;i++){
            if(inputArray[i] < minValue){
                minValue = inputArray[i];
            }
        }
        return minValue;
    }

    /**
     * ShowProgress
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mParkenFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mParkenFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
