package ca.mlht.android.skip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marc on 05/08/13.
 */
public class NotificationReceiver extends BroadcastReceiver {
    Context ctx;

    @Override
    public void onReceive(Context context, Intent intent){
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        ctx = context;
        String messageType = gcm.getMessageType(intent);
        if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
            sendPebbleNotification(intent.getExtras().getString("title","SKIP Notification"),intent.getExtras().getString("body","<<No Body>>"));
        }
        setResultCode(Activity.RESULT_OK);
    }

    private void sendPebbleNotification(String title, String body){
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title",title);
        data.put("body",body);
        final JSONObject jsonData = new JSONObject(data);
        final String notifiactionData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType","PEBBLE_ALERT");
        i.putExtra("sender","SKIP for Android");
        i.putExtra("notificationData",notifiactionData);

        Log.d("SKIP", "About to send a modal alert to pebble");
        ctx.sendBroadcast(i);
    }

}
