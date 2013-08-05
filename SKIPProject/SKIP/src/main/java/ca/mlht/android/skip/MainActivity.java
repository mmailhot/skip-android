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
import android.app.ProgressDialog;


import java.util.HashMap;

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
                    api_view.setText(results.get("api_key"));
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
        final Button register =  (Button)findViewById(R.id.register_button);
        final Button unregister =  (Button)findViewById(R.id.unregister_button);
        final ProgressDialog dialog = new ProgressDialog(this);
        final TextView api_view = (TextView)findViewById(R.id.api_key_view);
        dialog.setCancelable(false);
        dialog.show();
        register.setEnabled(false);

        apiHandler.registerDevice(new AsyncReturn() {
            @Override
            public void callback(HashMap<String, String> results) {
                dialog.cancel();
                if(results.get("success")!="true"){
                    Toast.makeText(getApplicationContext(),"Failed to Register",Toast.LENGTH_LONG).show();
                    register.setEnabled(true);
                }else{
                    api_view.setText(results.get("api_key"));
                    unregister.setEnabled(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
