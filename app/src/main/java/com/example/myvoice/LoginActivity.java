package com.example.myvoice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * this is the first screen in the app here you can log in to the user or create a new user.
 * if the user wanted to stay logged in the user will be signed in automatically
 * create an argument that will be always active and check if the phone has Wifi
 * and start a sticky service and BCR
 */
public class LoginActivity extends AppCompatActivity {
    private Button btnSignIn,btnSignUp;
    private TextView tvWelcome;
    /**
     * connect between the code and the layout,
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText("ברוך הבא, מה תרצה לעשות?");
        SharedPreferences prefs = getSharedPreferences("myTable",MODE_PRIVATE);

        if (prefs.getBoolean("save",false)) {
            Gson gson = new Gson();
            String json = prefs.getString("User", "");
            User u = gson.fromJson(json, User.class);
            Intent i = new Intent(LoginActivity.this,MainPageActivity.class);
            i.putExtra("User",u);
            finish();
            startActivity(i);
        }

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,SignInActivity.class);
                startActivity(i);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });
        /*
        curently having problems with the service so the code not working
        Intent svc = new Intent(LoginActivity.this, MySensorService.class);
        startService(svc);
        
         */

        requestPermissions(
                new String[] { android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_CONTACTS,  android.Manifest.permission.CALL_PHONE, android.Manifest.permission.SEND_SMS},
                123);
    }
    /**
     * check if your phone has internet/wifi connection
     * will be active as long as the app in running
     */
    public static boolean checkWifi(Context context){
        ConnectivityManager connMngr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMngr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected =(wifi!=null && wifi.isConnectedOrConnecting()) || (mobile!=null && mobile.isConnectedOrConnecting());
        return isConnected;


    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}


