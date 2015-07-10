/**
 * Created by Zack on 7/6/2015.
 */
public interface Moveable extends Updateable {
    void setXPosition(double position);
    double getXPosition();
    void setYPosition(double position);
    double getYPosition();
    double getVelocity();
    String toString();
    void update();
    double getLength();
    void setLeadingCar(Moveable leadingCar);
    void setRoad(Road r);
}
