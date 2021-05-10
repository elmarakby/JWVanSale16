package com.labs.jwvansale16.mdui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.labs.jwvansale16.R;

//import java.util.logging.Handler;
//import java.util.logging.LogRecord;
import android.os.Handler;

public class JasmineSplashActivity extends AppCompatActivity {
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    Handler handler;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        setContentView(R.layout.jasmine_splash);
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(JasmineSplashActivity.this, JasmineMainActivity.class);
                JasmineSplashActivity.this.startActivity(mainIntent);
                JasmineSplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

    }


}
