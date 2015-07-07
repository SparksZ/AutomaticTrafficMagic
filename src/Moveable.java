/**
 * Created by Zack on 7/6/2015.
 */
public interface Moveable {
    void setPosition(double position);
    double getPosition();
    double getVelocity();
    String toString();
    void update();
    double getLength();
    void setLeadingCar(Moveable leadingCar);
}
