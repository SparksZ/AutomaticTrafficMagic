
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Zack on 7/4/2015.
 */
public class Driver {

    private static Road road1;
    private static Road road2;
    private static Intersection inters1;
    private static double timeElapsed;

    // CONSTANTS
    public static final double frameRate = 0.5; // seconds

//    public synchronized static void main(String[] args) {
//
//        // Create Intersections (Y x Y)
//
//
//        road1 = new Road(44, 600, null, 0);
//        road2 = new Road(44, 50000, null, road1.getRoadEnd() + Intersection.length);
//
//        CopyOnWriteArrayList<Road> roads = new CopyOnWriteArrayList<Road>();
//        roads.add(road1);
//        roads.add(null);
//        roads.add(null);
//        roads.add(null);
//        roads.add(4, road2);
//
//        inters1 = new Intersection(roads);
//        road1.setIntersection(inters1);
//
//        addCars(8);
//
//        timeElapsed = 0;
//
//        while (timeElapsed < 7200) {
//            if (timeElapsed % 45 == 0) {
//                System.out.println("HERE!");
//            }
//            List<Moveable> cars = Collections.synchronizedList(road1.getCars());
//            inters1.update();
//            for (Road r : roads) {
//                if (r != null) {
//                    r.update();
//                }
//            }
//            timeElapsed += (frameRate);
//        }
//
//        timeElapsed++;
//    }
//
//    private static void createIntersections(int y) {
//
//    }
//
//    private static void addCars(int num) {
//        double firstPos = num * (Car.cLength + Car.minimumGap);
//
//        //road1.addCar(new DummyCar(road1.getRoadLength() + inters1.getLength() + 1000));
//        road1.addCar(new Car(firstPos - 0 * (Car.cLength +
//                Car.minimumGap), 0, road1, inters1.getLast()));
//
//        for (int i = 1; i < num; i++) {
//            road1.addCar(new Car(firstPos - i * (Car.cLength +
//                    Car.minimumGap), 0, road1, road1.getLast()));
//        }
//    }

    public static double getTimeElapsed() {
        return timeElapsed;
    }
}
