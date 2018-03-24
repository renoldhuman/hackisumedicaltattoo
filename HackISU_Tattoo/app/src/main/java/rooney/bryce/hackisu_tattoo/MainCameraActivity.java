package rooney.bryce.hackisu_tattoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class MainCameraActivity extends Activity {

    private Button takePicButton;
    private ArrayList<Coordinate> coordinateList;
    //   alec was here

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        if(savedInstanceState != null){

        }

        takePicButton = (Button) findViewById(R.id.bTest);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //take and get pic. pass to threshold function...

                //Threshhold functioin here. pass info to...


                Intent startInfoDisplayActivity = new Intent(MainCameraActivity.this, InfoDisplayActivity.class);
                startInfoDisplayActivity.putStringArrayListExtra("diseaseList", extrapolateDiseaseData(coordinateList));
                startActivity(startInfoDisplayActivity);
            }
        });


    }

//    private locationDataPackage thresholdPicFunction(input pic){
//
//        return locationDataPackage;
//    }


    private ArrayList<String> extrapolateDiseaseData(ArrayList<Coordinate> coordinateList){
        ArrayList<String> diseaseList = new ArrayList<String>();



        return diseaseList;
    }
}
