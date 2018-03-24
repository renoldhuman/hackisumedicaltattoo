package rooney.bryce.hackisu_tattoo;

/**
 * Created by mcrgw_y1ejz4l on 3/23/2018.
 */

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;
import org.opencv.core.Point;
//import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;


public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0,0,0,255);
    private Scalar mUpperBound = new Scalar(25,50,50,255);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mBlurred = new Mat();
    Mat mThreshed = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();
    Mat mCircles = new Mat();
    Mat mCircled = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    public void setHsvColor(Scalar hsvColor) {
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }

    public void process(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }

    public void simpleprocess(Mat rgbaImage) {
        Log.d("CHECK","In simple process");
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2GRAY);

        //Imgproc.GaussianBlur(mHsvMat, mBlurred, new Size(5,5), 0);

        //Imgproc.threshold(mBlurred, mThreshed, 127,255,Imgproc.THRESH_BINARY);
        Imgproc.adaptiveThreshold(mHsvMat,mThreshed,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY_INV,11,2);

//        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
//        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Imgproc.findContours(mThreshed, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//        // Find max contour area
//        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
//        while (each.hasNext()) {
//            MatOfPoint wrapper = each.next();
//            double area = Imgproc.contourArea(wrapper);
//            if (area > maxArea)
//                maxArea = area;
//        }
//
//        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
//            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
//                Core.multiply(contour, new Scalar(4,4), contour);
//                mContours.add(contour);
//            }
            mContours.add(contour);
        }
    }

    public void houghsimpleprocess(Mat rgbaImage) {
        Log.d("CHECK","In hough simple process");
//        Imgproc.pyrDown(rgbaImage, mPyrDownMat);
//        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);
        ArrayList<Point> pointList = new ArrayList<Point>();

        Imgproc.cvtColor(rgbaImage, mHsvMat, Imgproc.COLOR_RGB2GRAY);

        Imgproc.medianBlur(mHsvMat, mHsvMat, 5);

        Imgproc.HoughCircles(mHsvMat, mCircles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)mHsvMat.rows()/16,100.0,30.0,30, 50);

        for (int x = 0; x < mCircles.cols(); x++) {
            double[] c = mCircles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(rgbaImage, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(rgbaImage, center, radius, new Scalar(255,0,255), 3, 8, 0 );
            pointList.add(center);
            if(x > 0) {
                Log.d("DISTANCE", "" + euclideanDist(pointList.get(0), pointList.get(1)));
            }
        }




        mCircled = rgbaImage;

        //Imgproc.threshold(mBlurred, mThreshed, 127,255,Imgproc.THRESH_BINARY);
//        Imgproc.adaptiveThreshold(mHsvMat,mThreshed,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//                Imgproc.THRESH_BINARY_INV,11,2);

//        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
//        Imgproc.dilate(mMask, mDilatedMask, new Mat());

//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//
//        Imgproc.findContours(mThreshed, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//        // Find max contour area
//        double maxArea = 0;
//        Iterator<MatOfPoint> each = contours.iterator();
//        while (each.hasNext()) {
//            MatOfPoint wrapper = each.next();
//            double area = Imgproc.contourArea(wrapper);
//            if (area > maxArea)
//                maxArea = area;
//        }
//
//        // Filter contours by area and resize to fit the original image size
//        mContours.clear();
//        each = contours.iterator();
//        while (each.hasNext()) {
//            MatOfPoint contour = each.next();
////            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
////                Core.multiply(contour, new Scalar(4,4), contour);
////                mContours.add(contour);
////            }
//            mContours.add(contour);
//        }
    }

    public List<MatOfPoint> getContours() {
        return mContours;
    }

    public Mat getmCircled(){
        return mCircled;
    }

    private double euclideanDist(Point p, Point q) {
        Point diff = new Point((p.x - q.x),(p.y - q.y));
        return Math.sqrt(diff.x*diff.x + diff.y*diff.y);
    }
}
