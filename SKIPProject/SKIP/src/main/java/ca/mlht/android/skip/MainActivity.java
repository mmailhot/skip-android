package ca.mlht.android.skip;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
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

public class MainActivity extends Activity {

    private SkipApiHandler apiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        apiHandler = new SkipApiHandler(this.getApplicationContext());

        apiHandler.checkIfDeviceRegistered(new AsyncReturn() {
            @Override
            public void callback(HashMap<String, String> results) {
                if(results.get("exists")=="true"){
                    TextView api_view = (TextView)findViewById(R.id.api_key_view);
                    api_view.setText(results.get("api_jey"));
                    Button unregister =  (Button)findViewById(R.id.unregister_button);
                    unregister.setEnabled(true);
                }else{
                    Button register =  (Button)findViewById(R.id.register_button);
                    register.setEnabled(true);
                }
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRegisterDeviceClicked (View v){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
