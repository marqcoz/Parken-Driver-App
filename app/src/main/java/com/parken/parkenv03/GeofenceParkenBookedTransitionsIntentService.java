package com.parken.parkenv03;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceParkenBookedTransitionsIntentService extends IntentService {

    private static final String TAG = GeofenceParkenBookedTransitionsIntentService.class.getSimpleName();

    public static final int GEOFENCE_PARKEN_BOOKED_NOTIFICATION_ID = 420;

    public GeofenceParkenBookedTransitionsIntentService() {
        super(TAG);
    }

    private ParkenActivity actParken;

    //private ShPref session = new ShPref(this);

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
                //actParken = new ParkenActivity();
                Intent dialogIntent = new Intent(getApplicationContext(), ParkenActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra("Activity", ParkenActivity.INTENT_GEOFENCE_PARKEN_BOOKED);
                startActivity(dialogIntent);



                /*
                actParken.buscarEspacioParken(String.valueOf(session.getLatDestino()),
                        String.valueOf(session.getLngDestino()),
                        ParkenActivity.METHOD_PARKEN_SPACE_BOOKED);*/
                //actParken.dialogFailed().show();
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
        Log.i(TAG, "sendNotificationBooked: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = ParkenActivity.makeNotificationIntent(
                getApplicationContext(), msg
        );

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ParkenActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(2, PendingIntent.FLAG_UPDATE_CURRENT);


        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_PARKEN_BOOKED_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }

    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Geofence Parken Boooked Notification!")
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
}
