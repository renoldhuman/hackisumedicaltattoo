package rooney.bryce.hackisu_tattoo;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Button;

public class MainActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";


    private boolean              mIsColorSelected   = false;
    private boolean              mGatherData        = false;
    private boolean              firstFrameCaptured = false;
    private int                  touchCount       = 0;

    private Mat                  mRgba;
    private Scalar               mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private Mat                  mSpectrum;
    private Size                 SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    private String               mShapeName;
    private ColorBlobDetector    mDetector;
    private ArrayList<Coordinate> diseaseCoordinateList = new ArrayList<Coordinate>();

    private CameraBridgeViewBase mOpenCvCameraView;

    // difference in angle allowed in degrees
    private static final double ANGLE_BUFFER = 8.0;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);



    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        //mRgba creates a new matrix with height and width and four color channels
        //in the range 0 to 255
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        touchCount++;


        if(touchCount == 1){
            mIsColorSelected = true;
        }

        //mGatherData = true;


        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Mat circled;


        if(firstFrameCaptured){
            // once the first frame has been captured and the image data processed, we know have
            // access to the center coordinates for the circles that are detected on the screen
            Point centerPoint = mDetector.getCenterPoint();
            Point referencePoint = mDetector.getReferencePoint();
            List<Point> diseasePoints = mDetector.getDiseasePoints();

            // create a new ArrayList to store the diseaseCoordinates that are created by the
            // getDiseaseCoordinate method
            for (Point p : diseasePoints) {
                Coordinate diseaseCoord = ColorBlobDetector.getDiseaseCoordinate(centerPoint, referencePoint, p);
                diseaseCoordinateList.add(diseaseCoord);
            }

            // now that we have the information, start the Patient Screen
            startPatientIntent();
            mIsColorSelected = false;
        }

        if (mIsColorSelected) {

            mDetector.houghsimpleprocess(mRgba);

            circled = mDetector.getmCircled();

            firstFrameCaptured = true;
            return circled;
        }
        else{
            mDetector.houghcircleprocess(mRgba);

            circled = mDetector.getmCircled();

            return circled;

        }
    }




    private ArrayList<String> extrapolateDiseaseData(ArrayList<Coordinate> coordinateList){
        ArrayList<String> diseaseList = new ArrayList<String>();

        for(int i = 0; i<coordinateList.size(); i++) {

            // Inner (9mm)
            if (coordinateList.get(i).getDistance() < 11 && coordinateList.get(i).getDistance() > 7) {
                if (coordinateList.get(i).getDegrees() < Definitions.DNR_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.DNR_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.DNR_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.DONOR_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.DONOR_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.DONOR_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.NIV_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.NIV_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.NIV_STRING);

                }
            }
                // Middle (18mm)
            else if (coordinateList.get(i).getDistance() < 20 && coordinateList.get(i).getDistance() > 16) {
                if (coordinateList.get(i).getDegrees() < Definitions.PENICILLIN_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.PENICILLIN_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.PENICILLIN_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.ANTIBIOTICS_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.ANTIBIOTICS_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.ANTIBIOTICS_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.ASPIRIN_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.ASPIRIN_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.ASPIRIN_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.NSAIDS_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.NSAIDS_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.NSAIDS_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.FOOD_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.FOOD_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.FOOD_STRING);
                }
            }

                // Outer (27mm)
            else if (coordinateList.get(i).getDistance() < 29 && coordinateList.get(i).getDistance() > 25) {
                if (coordinateList.get(i).getDegrees() < Definitions.DIABETES1_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.DIABETES1_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.DIABETES1_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.DIABETES2_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.DIABETES2_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.DIABETES2_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.HYPOGLYCEMIA_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.HYPOGLYCEMIA_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.HYPOGLYCEMIA_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.HYPOTENSION_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.HYPOTENSION_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.HYPOTENSION_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.HYPERGLYCEMIA_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.HYPERGLYCEMIA_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.HYPERGLYCEMIA_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.HYPERTENSION_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.HYPERTENSION_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.HYPERTENSION_STRING);
                } else if (coordinateList.get(i).getDegrees() < Definitions.AIDS_DEGREES + ANGLE_BUFFER && coordinateList.get(i).getDegrees() > Definitions.AIDS_DEGREES - ANGLE_BUFFER) {
                    diseaseList.add(Definitions.AIDS_STRING);
                }
            }


            }

        return diseaseList;
    }

    private void startPatientIntent(){
        Intent startInfoDisplayActivity = new Intent(MainActivity.this, InfoDisplayActivity.class);
        startInfoDisplayActivity.putExtra("diseaseList", extrapolateDiseaseData(diseaseCoordinateList));
        startActivity(startInfoDisplayActivity);
    }
}
