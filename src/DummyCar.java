/**
 * Created by Zack on 7/6/2015.
 */
public class DummyCar implements Moveable {

    private double xPosition;
    private double yPosition;

    public DummyCar(double xPosition, double yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    @Override
    public void setXPosition(double position) {
        this.xPosition = position;
    }

    @Override
    public void setYPosition(double position) {
        this.yPosition = position;
    }

    @Override
    public double getXPosition() {
        return xPosition;
    }

    @Override
    public double getYPosition() {
        return yPosition;
    }

    @Override
    public double getVelocity() {
        return 0;
    }

    @Override
    public void update(int timeElapsed) {
    }

    @Override
    public double getLength() {
        return 0;
    }

    @Override
    public void setLeadingCar(Moveable leadingCar) {

    }

    @Override
    public void setRoad(Road r) {

    }

    public String toString() {
        return "P: (" + xPosition + ", " + yPosition + ")";
    }

    public int route(){
    	return 0;
    }
}
