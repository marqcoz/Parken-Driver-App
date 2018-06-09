package com.parken.parkenv03;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class ZonaParkenActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener{


    private double latitudDestino;
    private double longitudDestino;
    private double currentLat;
    private double currentLng;

    private String direccionDestino;
    private String zonaParkenJson;

    private int mapaListo = 0;

    private Button espacioParken;

    private GoogleMap mMap;

    private LatLngBounds ZONAS;
    private LatLngBounds DF;

    private ShPref session;

    private ParkenActivity actParken = new ParkenActivity();
    public static ZonaParkenActivity activityZonaParken;

    private GoogleApiClient googleApiClient;

    private Location lastLocation;

    private LocationRequest locationRequest;

    private Polyline polyline;

    private final int UPDATE_INTERVAL =  1000;
    private final int FASTEST_INTERVAL = 900;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zona_parken);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        activityZonaParken = this;
        createGoogleApi();
        session = new ShPref(activityZonaParken);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Zonas Parken");


        //Verificamos la versión de la API
        //Si es >23 pedimos acceso a la ubicación
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
            // Versiones con android 6.0 o superior
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activityZonaParken, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1);
            }
            else{
                cargarMapa();
            }

        } else{
            //Versiones anteriores a android 6.0
            if(PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 0){
                dialogPermissionRequired().show();
                actParken.activityParken.finish();
                finish();
            }else{
                cargarMapa();
            }
        }



        espacioParken = findViewById(R.id.btnEspacioParken);


        espacioParken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actParken.activityParken.finish();
                Intent parken = new Intent(ZonaParkenActivity.this,ParkenActivity.class);
                parken.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                parken.putExtra("Activity", "ZonaParkenActivity");
                parken.putExtra("zonasParkenJson", zonaParkenJson);
                parken.putExtra("espacioParkenRequested",true);
                parken.putExtra("latitudDestino", latitudDestino);
                parken.putExtra("longitudDestino", longitudDestino);
                parken.putExtra("nombreDestino", direccionDestino);
                session.setCancel(false);
                ParserTask parserTask = new ParserTask();
                parserTask.cancel(true);
                startActivity(parken);
                finish();

            }
        });



        //cargarMapa();
    }

    /**
     * Google Maps
     * Métodos para manipular la ubicación y los mapas
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mMap = googleMap;
        mMap.clear();

        Log.d("MapZona", "Recarga");
        //Centrar el mapa en Ciudad de México
        float zoom = 10f;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                new LatLng(19.428970, -99.133464), zoom);
        mMap.animateCamera(cameraUpdate);
        //mMap.moveCamera(cameraUpdate);
        espacioParken.setVisibility(View.INVISIBLE);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if(lastLocation!=null){
                    cargarMapaZonasParken();
                }
                mapaListo = 1;
            }
        });

    }

    //Método que muestra todas las zonas parken, el destino y centra el mapa
    public void cargarMapaZonasParken(){
        mMap.clear();
        mMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);

        //Obtenemos las variables de ParkenActivity para dibujar las zonas Parken en el mapa
        Intent intent = getIntent();

        zonaParkenJson = intent.getStringExtra("zonaParkenJson");
        direccionDestino = intent.getStringExtra("direccionDestino");
        latitudDestino = intent.getDoubleExtra("latitudDestino", 0.0);
        longitudDestino = intent.getDoubleExtra("longitudDestino", 0.0);

        dibujarZonasParken(zonaParkenJson);

        LatLng destino = new LatLng(latitudDestino,longitudDestino);
        mMap.addMarker(new MarkerOptions().position(destino)
                .title("Destino")
                .snippet(direccionDestino)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish)));

        centrarMapa(ZONAS);

        drawRoute();

        espacioParken.setVisibility(View.VISIBLE);



    }

    //Método que llama el fragment del mapa Google Maps
    public void cargarMapa(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    //Método para dibujar las zonas parken cercanas y centra el mapa dentro de todas las zonas
    private void dibujarZonasParken(String zonaParken){

        try{

            JSONArray jsonArray = new JSONArray(zonaParken);
                int nz = jsonArray.length();
                int n = 2;
            double coordinatesNorthLat[] = new double[nz+n];
            double coordinatesNorthLng[] = new double[nz+n];
            double coordinatesSouthLat[] = new double[nz+n];
            double coordinatesSouthLng[] = new double[nz+n];

            for(int i = 0; i < jsonArray.length(); i++){
                JSONArray jsonArrayPolygon = new JSONArray(jsonArray.getJSONObject(i).getString("coordenadas"));


                PolygonOptions rectOptions = new PolygonOptions();

                for(int j = 0; j < jsonArrayPolygon.length(); j++){
                    LatLng coord= new LatLng(Double.parseDouble(jsonArrayPolygon.getJSONObject(j).getString("longitud")), Double.parseDouble(jsonArrayPolygon.getJSONObject(j).getString("latitud")));
                    rectOptions.add(coord);
                }

                Polygon polygon = mMap.addPolygon(rectOptions);
                polygon.setFillColor(Color.argb(50, 244, 67, 54));
                polygon.setStrokeWidth(6);
                polygon.setStrokeColor(Color.rgb(244, 67, 54));


                JSONArray jsonArrayCentroide = new JSONArray(jsonArray.getJSONObject(i).getString("centro"));
                LatLng destination = new LatLng(Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("longitud")), Double.parseDouble(jsonArrayCentroide.getJSONObject(0).getString("latitud")));

                double radius = Double.parseDouble(jsonArray.getJSONObject(i).getString("radio")) + 200;

                LatLng targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
                LatLng targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

                coordinatesNorthLat[i] = targetNorthEast.latitude;
                coordinatesNorthLng[i] = targetNorthEast.longitude;
                coordinatesSouthLat[i] = targetSouthWest.latitude;
                coordinatesSouthLng[i] = targetSouthWest.longitude;
                //Log.d("Coordenadas"+i,String.valueOf(targetNorthEast.latitude)+", "+String.valueOf(targetNorthEast.longitude));
                //Log.d("Coordenadas"+i,String.valueOf(targetSouthWest.latitude)+", "+String.valueOf(targetSouthWest.longitude));


                mMap.addMarker(new MarkerOptions().position(destination).title(jsonArray.getJSONObject(i).getString("nombre"))
                        .snippet("Precio: "+"Cargando..."))
                        //"$"+jsonArray.getJSONObject(i).getString("precio")+"MXN"))
                        .setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_parken));
            }

            LatLng destination = new LatLng(latitudDestino, longitudDestino);

            double radius = 200;

            LatLng targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
            LatLng targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

            coordinatesNorthLat[nz] = targetNorthEast.latitude;
            coordinatesNorthLng[nz] = targetNorthEast.longitude;
            coordinatesSouthLat[nz] = targetSouthWest.latitude;
            coordinatesSouthLng[nz] = targetSouthWest.longitude;

            destination = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());

            radius = 200;

            targetNorthEast = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 45);
            targetSouthWest = SphericalUtil.computeOffset(destination, radius * Math.sqrt(2), 225);

            coordinatesNorthLat[nz+1] = targetNorthEast.latitude;
            coordinatesNorthLng[nz+1] = targetNorthEast.longitude;
            coordinatesSouthLat[nz+1] = targetSouthWest.latitude;
            coordinatesSouthLng[nz+1] = targetSouthWest.longitude;

            obtenerBoundary(coordinatesNorthLat, coordinatesNorthLng, coordinatesSouthLat, coordinatesSouthLng);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void drawRoute(){
        LatLng origin = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        LatLng destino = new LatLng(latitudDestino, longitudDestino);
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, destino);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    //Método que obtiene el Bound utilizando un arreglo de todas las coordenadas a centrar
    public void obtenerBoundary(double[] northLat, double[] northLng, double [] southLat, double[] southLng){

        LatLng min = new LatLng(getMin(southLat),getMin(southLng));
        LatLng max = new LatLng(getMax(northLat),getMax(northLng));

        //Log.d("CoordenadaMin",getMin(southLat)+", "+getMin(southLng));
        //Log.d("CoordenadaMax",getMax(northLat)+", "+getMax(northLng));

        ZONAS = new LatLngBounds(min, max);

    }

    //Método que centra el mapa con base en dos coordendas (Bound)
    public void centrarMapa(LatLngBounds bound){

        if(mMap != null){
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 0));
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngBounds(bound, 0);
        mMap.animateCamera(miUbicacion);
        }
    }

    /**
     * Location
     */
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


    //Método que obtiene la ubicación actual del dispositivo
    /*
    private Location miUbicacion() {

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location;

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
          //alertNoGPS().show();
          location = null;

        }else {
            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if(location == null){ location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER); }
            actualizarUbicacion(location);
        }
        Log.d("MapaListo", String.valueOf(mapaListo));
        if(mapaListo!=1) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }



        return location;

    }*/

    //Método que actualiza el valor de las coordenadas de la posición actual
    private void actualizarUbicacion(Location location) {
        if (location != null) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
        }
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

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        drawRoute();
        Log.d("LocationChanged","ON");
        Log.d("LocationChanged", "onLocationChanged ["+location+"]");

        //Aqui voy a enviar la solicitud al server de un espacio Parken
        //Siempre que el automovilista haya solicitado un espacio
        //buscarEspacioParken(String.valueOf(latitudDestino), String.valueOf(longitudDestino));
    }

    /*
        LocationListener locationListener= new LocationListener()
    {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    };
    */


    /**
     * Override methods
     */
    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
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
    public void onBackPressed() {
        Intent parken = new Intent(ZonaParkenActivity.this,ParkenActivity.class);
        parken.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        parken.putExtra("Activity", "ZonaParkenActivity");
        parken.putExtra("espacioParkenRequested",false);
        finish();
        //actParken.activityParken.finish();
        startActivity(parken);
        //super.onBackPressed();
    }



    /**
     * AsynkTask
     * Código con las tareas asincronas
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

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

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
            } else {

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
                        Log.d("DrawRoute", "ClearRoute");
                    }
                    polyline = mMap.addPolyline(lineOptions);
                    Log.d("DrawRoute", "Route");
                }
            }
        }
    }



    /**
     * AlertDialogs
     * Código con todos los AlertDialog de ZonaParkenActivity
     */
    public AlertDialog alertNoGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityZonaParken);
        builder.setTitle("GPS desactivado")
                .setMessage("Habilita la localización del dispositivo para poder utilizar Parken.")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                        actParken.activityParken.finish();
                    }
                })
                .setPositiveButton("Habilitar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                            }
                        });

        return builder.create();
    }

    private AlertDialog dialogPermissionRequired() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityZonaParken);
        builder.setTitle("Parken requiere tener acceso a tu ubicación")
                .setMessage("Ingresa a la configuración de aplicaciones para habilitar el acceso a la ubicación de tu dispositivo.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;

                            }
                        });

        return builder.create();
    }

    /**
     * Método que muestra el mapa dependiendo el resultado
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    cargarMapa();

                } else {
                    // permission denied, boo!
                    actParken.activityParken.finish();
                    finish();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Funciones útiles
     */
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

        Log.d("RequestDirections",url);
        return url;
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
        Log.d("RequestDirections",data);
        return data;
    }

}
