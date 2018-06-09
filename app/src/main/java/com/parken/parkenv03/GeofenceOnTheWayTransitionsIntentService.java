package com.parken.parkenv03;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GeofenceOnTheWayTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceOnTheWayTransitionsIntentService.class.getSimpleName();

    public static final int GEOFENCE_ON_THE_WAY_NOTIFICATION_ID = 406;

    public GeofenceOnTheWayTransitionsIntentService() {
        super(TAG);
    }

    private ParkenActivity actParken;

    private ShPref session;

    private VolleySingleton volley;
    protected RequestQueue fRequestQueue;

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }



        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );

            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );

            if(geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
                actParken = new ParkenActivity();
                session = new ShPref(ParkenActivity.activityParken);
                volley = VolleySingleton.getInstance(getApplicationContext());
                fRequestQueue = volley.getRequestQueue();

                /*
                buscarEspacioParken(String.valueOf(session.getLatDestino()),
                        String.valueOf(session.getLngDestino()),
                        ParkenActivity.METHOD_PARKEN_SPACE_BOOKED);
                        */
                buscarEspacioParken(String.valueOf(ParkenActivity.latitudDestino),
                        String.valueOf(ParkenActivity.longitudDestino),
                        ParkenActivity.METHOD_PARKEN_SPACE_BOOKED);

                //actParken.dialogParken().show();
            }

            //Cuando entre a cualquier geocerca
            //Este es el proceso
            //Se obtiene el espacioParken mas cercano
            //Se aparta un espacio Parken apartarEspacioParken()
            //Se crea una nueva geocerca
            //startGeofence() -> centro=espacioParkenAsignado radius=150 metros
            //Cuando se entre a esa geocerca
            //Se muestra el super alertdialog (Pagar espacioParken ocupado)
            //Si paga y toodo esta chido, entonces se creará la geocerca o se mantendrá la misma geocerca
            //Con un tiempo de 10 minutos mas a lo que pagó
            //Habra otro intent para saber cuando entra a la geocerca
        }
    }


    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        String status = null;
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER )
            status = "Entering ";
        else if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT )
            status = "Exiting ";
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }

    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = ParkenActivity.makeNotificationIntent(
                getApplicationContext(), msg);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ParkenActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_ON_THE_WAY_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Entrando a la geocerca")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    public void buscarEspacioParken(final String latitudDestino, final String longitudDestino, final String origen) {
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

                                if(origen.equals(ParkenActivity.METHOD_PARKEN_SPACE_BOOKED)){
                                    apartarEspacioParken(response.toString(), session.infoId());
                                }
                                return;

                            }else{
                                Log.d("BuscandoEspacioParken", "Espacios no disponibles");
                                //actParken.dialogNoParkenSpaces().show();
                                //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                                Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);
                                dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_NO_EP);
                                startActivity(dialogIntent);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //actParken.dialogFailed().show();
                            //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                            Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);
                            dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_FAILED);
                            startActivity(dialogIntent);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("BuscandoEspacioParken", "Error Respuesta en JSON: " + error.getMessage());
                        //actParken.dialogFailed().show();
                        //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                        Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);
                        dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_FAILED);
                        startActivity(dialogIntent);
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                        //Ocultar la barra y mostrar la barra indicando error de conexión
                    }
                });

        fRequestQueue.add(jsArrayRequest);

    }

    public void apartarEspacioParken(final String responseJson, final String idAutomovilista) throws JSONException {

        final JSONObject jsonObject = new JSONObject(responseJson);

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

                        Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);

                        try{

                            if(response.getString("success").equals("1")){

                                Log.d("EspacioParken", response.toString());

                                //actParken.setParkenSpaceBooked(Integer.parseInt(jsonObject.getString("id")), jsonObject.toString());
                                sendNotification("Espacio Parken Reservado!");

                                dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_EP_BOOKED);
                                dialogIntent.putExtra("idEspacioParkenAsignado", jsonObject.getString("id"));
                                dialogIntent.putExtra("idSesionParken", response.getString("idSesionParken"));
                                dialogIntent.putExtra("jsonEspacioParken", jsonObject.toString());
                                Log.d("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);
                                startActivity(dialogIntent);
                                //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN_SPACE_BOOKED);

                            }else{
                                Log.d("EspacioParken", response.toString());

                                if(response.getString("success").equals("2")){
                                    //actParken.dialogBookedDone().show();
                                    //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN_SPACE_BOOKED);
                                    //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                                    dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_AUTOMOVILISTA_BOOKED);
                                }else{
                                    //actParken.dialogFailed().show();
                                    //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                                    dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_FAILED);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            //actParken.dialogFailed().show();
                            //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                            dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_FAILED);
                        }

                        startActivity(dialogIntent);
                        return;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("EspacioParken", "Error Respuesta en JSON: " + error.getMessage());
                        //actParken.dialogFailed().show();
                        //actParken.setViewParkenSpaceBooked(ParkenActivity.VIEW_PARKEN);
                        Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_ON_THE_WAY);
                        dialogIntent.putExtra("ActivityStatus", ParkenActivity.MESSAGE_FAILED);
                        startActivity(dialogIntent);
                        return;
                        //Mostrar el mensaje cuando haya un error en la pantalla del usuario
                    }
                });

        fRequestQueue.add(jsArrayRequest);
    }




}

