package rooney.bryce.hackisu_tattoo;

import android.util.Log;

import org.junit.Test;
import org.opencv.core.Point;

/**
 * Created by john on 3/24/2018.
 */

public class TestGetDisease {

    private Point center, ref, disease;

    public TestGetDisease() {
    }

    @Test
    public void TestGetDiseaseMethod() throws Exception {
        Point center, ref, disease;
        center = new Point(0, 0);
        ref = new Point(10, 3);
        disease = new Point(8, -4);

        Coordinate coordinate = ColorBlobDetector.getDiseaseCoordinate(center, ref, disease);
        System.out.println("" + coordinate.getDegrees() + " " + coordinate.getDistance());

        center = new Point(3.6, -0.2);
        ref = new Point(6.5, -0.3);
        disease = new Point(0, 6);
        coordinate = ColorBlobDetector.getDiseaseCoordinate(center, ref, disease);
        System.out.println("" + coordinate.getDegrees() + " " + coordinate.getDistance());
    }
}
