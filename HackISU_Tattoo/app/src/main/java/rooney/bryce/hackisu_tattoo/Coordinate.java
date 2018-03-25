package rooney.bryce.hackisu_tattoo;

/**
 * Created by Brycycle on 3/24/2018.
 */

public class Coordinate {

    public double degrees;
    public double distance;

    public Coordinate(double degrees, double distance){
        this.degrees = degrees;
        this.distance = distance;
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
