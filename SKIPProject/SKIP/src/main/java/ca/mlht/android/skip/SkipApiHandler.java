package ca.mlht.android.skip;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

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
        device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //Returns a hash map containing two keys
    //exists -> "true" or "false" device exists on server
    //api_key -> if exists = "true", contains the api key for the device
    public void checkIfDeviceRegistered(final AsyncReturn returnMethod){
        final HashMap<String,String> results;
        results = new HashMap<String, String>();
        JsonArrayRequest deviceCheck = new JsonArrayRequest(url+"/devices?device_id="+device_id,new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response){
                if(response.length() != 0 ){
                    try{
                        String api_key = response.getJSONObject(0).getString("api_key");
                        String gcm_id = response.getJSONObject(0).getString("gcm_id");
                        results.put("exists","true");
                        Log.v("API KEY: ","api_key");
                        results.put("api_key",api_key);
                        results.put("gcm_id",gcm_id);
                        returnMethod.callback(results);
                    }catch(Exception e){}
                }else{
                    results.put("exists", "false");
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
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... null_params){

                String registration_id;

                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                //Attempt to register with GCM
                try {
                    return gcm.register("36373224589");
                } catch (IOException e) {
                    Log.e("GCM Registration Failure", "GCM Failed to Register");
                    e.printStackTrace();
                    return "";
                }


            }

            @Override
            protected void onPostExecute(String reg_id){
                final HashMap<String,String> results;
                results = new HashMap<String, String>();

                //Check for failure
                if(reg_id == ""){
                    results.put("success","false");
                    returnMethod.callback(results);
                    return;
                }

                //Construct Parameters

                JSONObject params = new JSONObject();
                try{
                    params.put("device_id",device_id);
                    params.put("gcm_id",reg_id);
                }catch(JSONException e){
                    results.put("success","false");
                    Log.e("JSON Failure","JSON Failure");
                    returnMethod.callback(results);
                    return;
                }

                Log.e("Progress","Sending Results");
                //Send registration to SKIP server
                JsonArrayRequestWithMethod registration_request = new JsonArrayRequestWithMethod(Request.Method.POST, url+"/devices",params,new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse (JSONArray response){
                        Log.v("Debug","Successful Network Response");
                        try{
                            results.put("success","true");
                            results.put("api_key",response.getJSONObject(0).getString("api_key"));
                            returnMethod.callback(results);
                        }catch (Exception e){
                            results.put("success","false");
                            Log.e("JSON Failure","JSON Failure");
                            returnMethod.callback(results);
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        results.put("success","false");
                        Log.e("Network Failure","Network Failure");
                        Log.e("Network Failure",error.getMessage());
                        returnMethod.callback(results);
                    }
                });
                queue.add(registration_request);
            }

        }.execute(null,null,null);
    }

    //Returns a hash map containing
    //success -> "true" or "false"
    public void unregisterDevice(String api_key,final AsyncReturn returnMethod){
        final HashMap<String,String> results;
        results = new HashMap<String, String>();

        //Construct Parameters
        final JSONObject params = new JSONObject();
        try
        {
            params.put("device_id",device_id);
        }catch(JSONException e){
            Log.e("JSON Error","JSON Error");
        }

        JsonObjectRequest unregister_request = new JsonObjectRequest(Request.Method.POST,url+"/devices/"+api_key+"/delete",params,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("Response from Delete Server","Success");
                try{
                    if(response.getBoolean("success")){
                        results.put("success","true");
                    }else{
                        results.put("success","false");
                    }

                }
                catch(JSONException e){
                    results.put("success","false");
                }
                returnMethod.callback(results);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.v("Response from Delete Server","Failure");
                Log.e("Volley Error",error.getMessage());
                results.put("success","false");
                returnMethod.callback(results);
            }

        });

        Log.e("Body",new String(unregister_request.getBody()));
        queue.add(unregister_request);


    }
}
