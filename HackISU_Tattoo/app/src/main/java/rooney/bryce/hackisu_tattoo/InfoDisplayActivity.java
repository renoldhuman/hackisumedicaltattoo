package rooney.bryce.hackisu_tattoo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class InfoDisplayActivity extends Activity {


    private ArrayList<String> diseasesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        if(savedInstanceState != null){
            diseasesList = savedInstanceState.getStringArrayList("diseaseList");
        }

        diseasesList = getIntent().getStringArrayListExtra("diseaseList");


        addToDiseaseList();

    }


    private void addToDiseaseList(){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startMainCameraActivity = new Intent(InfoDisplayActivity.this, MainCameraActivity.class);
        startActivity(startMainCameraActivity);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("diseaseList", diseasesList);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }
}
