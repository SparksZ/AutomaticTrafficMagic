/**
 * Created by Zack on 7/8/2015.
 */
public interface CarContainer {
    /**
     * Abstracts addCar for CarSink and Intersection
     * @param whichRoad This is only for Intersection, CarSink doesn't use it
     * @param car Car to be added
     */
    boolean addCar(int whichRoad, Moveable car);
}
