package ca.mlht.android.skip;

import android.content.Context;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by marc on 04/08/13.
 */
public class SkipApiHandler {
    private RequestQueue queue;
    private Context context;
    private String url = "http://skipapebble.herokuapp.com";
    private String device_id;


    public SkipApiHandler(Context context){
        this.context = context;
        queue = Volley.newRequestQueue(context);

    }

    //Returns a hash map containing two keys
    //exists -> "true" or "false" device exists on server
    //api_key -> if exists = "true", contains the api key for the device
    public void checkIfDeviceRegistered(final AsyncReturn returnMethod){
        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        final HashMap<String,String> results;
        results = new HashMap<String, String>();
        JsonArrayRequest deviceCheck = new JsonArrayRequest(url+"/devices?device_id="+device_id,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                if(response.length() != 0 ){
                    try{
                        String api_key = response.getJSONObject(0).getString("api_key");
                        results.put("exists","true");
                        results.put("api_key",api_key);
                        returnMethod.callback(results);
                    }catch(Exception e){}
                }else{
                    results.put("exists","false");
                    returnMethod.callback(results);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
                Toast.makeText(context, "Error reaching SKIP server", Toast.LENGTH_SHORT).show();
                return;
            }
        });
        queue.add(deviceCheck);
    }

    //Returns a hash map containing
    //success -> "true" or "false"
    //api_key -> the api key (if successful)
    public void registerDevice(final AsyncReturn returnMethod){
        
    }
}
