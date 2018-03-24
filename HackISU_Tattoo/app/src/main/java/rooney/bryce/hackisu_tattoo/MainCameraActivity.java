package rooney.bryce.hackisu_tattoo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class MainCameraActivity extends Activity {

    private Button takePicButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_camera);

        if(savedInstanceState != null){

        }

//        takePicButton = (Button) findViewById(R.id.bTakePic);
        takePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Threshhold functioin here. pass info to...

                //Location Data to Readable data function here. Store in intent to send to InfoDisplayActivity


                Intent startInfoDisplayActivity = new Intent(MainCameraActivity.this, InfoDisplayActivity.class);
                //put display info into intent to pass
                startActivity(startInfoDisplayActivity);
            }
        });


    }

//    private locationDataPackage thresholdPicFunction(input pic){
//
//        return locationDataPackage;
//    }

//    private infoToDisplayPackage extrapolateInfo(input locationDataPackage){
//
//        return infoToDisplayPackage;
//    }
}
