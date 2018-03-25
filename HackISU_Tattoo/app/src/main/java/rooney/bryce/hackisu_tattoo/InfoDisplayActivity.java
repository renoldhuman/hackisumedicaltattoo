package rooney.bryce.hackisu_tattoo;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class InfoDisplayActivity extends Activity {


    private ArrayList<String> diseasesList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_display);

        final ListView lv = (ListView) findViewById(R.id.lvDiseaseList);

        // Create an ArrayAdapter from List

        if(savedInstanceState != null){
            diseasesList = savedInstanceState.getStringArrayList("diseaseList");
        }

        diseasesList = getIntent().getStringArrayListExtra("diseaseList");


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, diseasesList);
        lv.setAdapter(arrayAdapter);

        arrayAdapter.notifyDataSetChanged();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent startMainCameraActivity = new Intent(InfoDisplayActivity.this, MainActivity.class);
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
