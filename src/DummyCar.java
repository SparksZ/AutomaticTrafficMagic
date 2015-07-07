/**
 * Created by Zack on 7/6/2015.
 */
public class DummyCar implements Moveable {

    private double position;

    public DummyCar(double position) {
        this.position = position;
    }

    @Override
    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public double getPosition() {
        return position;
    }

    @Override
    public double getVelocity() {
        return 0;
    }

    @Override
    public void update() {

    }

    @Override
    public double getLength() {
        return 0;
    }

    @Override
    public void setLeadingCar(Moveable leadingCar) {

    }

    public String toString() {
        return "P:" + position;
    }
}
