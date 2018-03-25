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
    private static final int REFERENCE_DISTANCE = 27;
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1;
    private int centerArea = 0;
    private int referenceArea = 0;

    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    private List<Point> lineHorizontal = new ArrayList<Point>();
    private List<Point> lineVertical = new ArrayList<Point>();
    private int lineCount;

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
                (double)mHsvMat.rows()/16,100.0,30.0,10, 300);

        for (int x = 0; x < mCircles.cols(); x++) {
            double[] c = mCircles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(rgbaImage, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            int area = (int)Math.PI*(radius*radius);
            if(area >= centerArea || area >= (centerArea - centerArea * .15)){
                Log.d("FIRST",""+area);
                if(area >= centerArea){
                    centerArea = area;
                }
                Imgproc.circle(rgbaImage, center, radius, new Scalar(255,0,0), 3, 8, 0 );
            }
            else if(area >= referenceArea || area >= (referenceArea - referenceArea *.5)){
                Log.d("SECOND",""+area);
                if (area >= referenceArea) {
                    referenceArea = area;
                }
                Imgproc.circle(rgbaImage, center, radius, new Scalar(0,0,255), 3, 8, 0 );
            }
            else{
                Log.d("DISEASE",""+area);
                Imgproc.circle(rgbaImage, center, radius, new Scalar(0,255,0), 3, 8, 0 );
            }
            pointList.add(center);
            if(x > 0) {
                Log.d("DISTANCE", "" + euclideanDist(pointList.get(0), pointList.get(1)));
                Imgproc.line(rgbaImage,pointList.get(0),pointList.get(1),new Scalar(0,255,0,255),3,8,0);
            }
        }




        mCircled = rgbaImage;

    }

//    public void houghlineprocess(Mat rgbImage){
//        Mat dst = new Mat();
//       // Mat cdst = new Mat();
//        lineCount++;
//
//        Imgproc.Canny(rgbImage,dst,50,200);
//
//      //  Imgproc.cvtColor(dst,cdst,Imgproc.COLOR_RGB2GRAY);
//
//        Mat lines = new Mat();
//        //Imgproc.HoughLines(dst,lines,1,Math.PI/180,100,0,0,0.0,360.0);
//
//        Imgproc.HoughLinesP(dst, lines, 3, Math.PI/180, 50,
//                5, 10);
//
//        for (int x = 0; x < lines.rows(); x++)
//        {
//            double[] vec = lines.get(x, 0);
//            double x1 = vec[0],
//                    y1 = vec[1],
//                    x2 = vec[2],
//                    y2 = vec[3];
//            Point start = new Point(x1, y1);
//            Point end = new Point(x2, y2);
//            double dx = x1 - x2;
//            double dy = y1 - y2;
//
//            double dist = Math.sqrt (dx*dx + dy*dy);
//
//            if(dist>100.d)  // show those lines that have length greater than 300
//                Imgproc.line(rgbImage, start, end, new Scalar(0,255, 0, 255),5);// here initimg is the original image.
//
//        }
//        mCircled = rgbImage;
//    }

    /**
     * Gets the coordinate information given the center, reference and disease marker points
     * @param center The center point
     * @param ref The reference point
     * @param disease One of the disease point
     * @return A Coordinate instance with radius (in mm) and angle (in degrees) information
     */
    public static Coordinate getDiseaseCoordinate(Point center, Point ref, Point disease) {

        // Coordinate value to return
        Coordinate coordinate = new Coordinate();

        // distance from center point to disease point in pixels
        double distanceToDisease, normalizationDistance, mmDistance;
        double theAngle, angleToRef, angleToDisease;

        normalizationDistance = Math.sqrt(Math.pow(ref.x - center.x, 2) + Math.pow(ref.y - center.y, 2));
        distanceToDisease = Math.sqrt(Math.pow(center.x - disease.x, 2) + Math.pow(center.y - disease.y, 2));

        mmDistance = distanceToDisease / normalizationDistance * REFERENCE_DISTANCE;

        double ref_dx = ref.x - center.x;
        double ref_dy = ref.y - center.y;

        double disease_dx = disease.x - center.x;
        double disease_dy = disease.y - center.y;

        // angle comes back in radius
        angleToRef = Math.atan2(ref_dy, ref_dx);
        angleToDisease = Math.atan2(disease_dy, disease_dx);

        theAngle = angleToRef - angleToDisease;

        // convert angle to degrees
        theAngle = theAngle * 180.0 / Math.PI;

        // we need to adjust the angle that the math function gives us because Bryce is silly and
        // defined the angle to be zero when vertical and move clockwise instead of zero at
        // horizontal and move counter-clockwise
        if (theAngle <= 90)
            theAngle = 90 - theAngle;
        else // theAngle > 90
            theAngle = 450 - theAngle;

        coordinate.setDistance(mmDistance);
        coordinate.setDegrees(theAngle);

        return coordinate;
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

    private double calculateLineSlope(Point p, Point q){
        double larger = (p.x > q.x) ? p.x : q.x;
        double smaller = (larger == p.x) ? q.x : p.x;

        return 0.0;
    }
}
