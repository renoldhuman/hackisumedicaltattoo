package rooney.bryce.hackisu_tattoo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class InfoDisplayActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startMainCameraActivity = new Intent(InfoDisplayActivity.this, MainCameraActivity.class);
        startActivity(startMainCameraActivity);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }
}
