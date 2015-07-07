import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Intersection {
	private CopyOnWriteArrayList<Moveable> queues;
    private CopyOnWriteArrayList<Road> roads; /* 0 - 3 are in roads 4 - 7 are out. i % 4
                                      gives position of road wrt intersection.
                                      0 north, 1 east, 2 south, 3 west. null if
                                      road doesn't exist */
    public static final double length = 100; // length of intersection (m)
    private double northStart;
    private double northEnd;
    private double lengthOfCars;
    private boolean state; // true if N/S is green, false if E/W is green
    private double nSLightLength = 15;
    private double eWLightLength = 15;
	private double lastLightStart;

	/**
	 * Following is the constructor and 3 overloads
	 */
	public Intersection(CopyOnWriteArrayList<Road> roads){
        this.roads = roads;
        queues = new CopyOnWriteArrayList<>();
        lengthOfCars = 0;
        lastLightStart = 0; // 0 is the start of the simulation
        state = true;
        queues.add(new DummyCar(length + 1000)); // 1000 for green (state == true)
        northStart = roads.get(0).getRoadEnd();
        northEnd = northStart + length;
	}

    /**
     * Adds car from road to Intersection.  If the intersection is backed up,
     * it will return false, which means the road should start backing up.
     * @param newCar the car object to add
     * @return if the car was added.
     */
    public boolean addCar(Moveable newCar) {
        if (newCar.getLength() + Car.minimumGap > length) {
            return false;
        }

        queues.add(newCar);
        if (!roads.get(0).getCars().isEmpty()) {
            roads.get(0).getCars().get(0).setLeadingCar(newCar);
        }
        lengthOfCars += newCar.getLength() + Car.cLength;
        return true;
    }

    public void update() {
        Road northRoad = roads.get(0);
        CopyOnWriteArrayList<Moveable> northCars = northRoad.getCars();
        updateRoadDummy(northRoad, northCars);

        for (int i = 1; i < queues.size(); i++) {
            queues.get(i).update();
            if (queues.get(i).getPosition() > northEnd) {
                roads.get(4).addCar(removeCar());
            }
        }

    }

    private Moveable removeCar() {
        if (queues.size() > 2) {
            queues.get(2).setLeadingCar(queues.get(0));
        }
        return queues.remove(1); /* the first car (0) is a dummy to correct
                                  behavior of the cars approaching traffic
                                  lights/intersections */
    }

    /**
     * Updates the dummy cars that act as lights
     * @param road the incoming road
     * @param cars the list of cars on the incoming road
     */
    private void updateRoadDummy(Road road, CopyOnWriteArrayList<Moveable> cars) {
        if (state == true && (Driver.getTimeElapsed() - lastLightStart) ==
                nSLightLength) { // NS light needs to change to red
            queues.set(0, new DummyCar(northEnd));
            if (!cars.isEmpty()) {
                cars.get(0).setLeadingCar(getLast());
            }
            state = false;
            lastLightStart = Driver.getTimeElapsed();
        }

        if (state == false && (Driver.getTimeElapsed() - lastLightStart) ==
                eWLightLength) { // NS light needs to change to green
            queues.set(0, new DummyCar(northEnd + 1000)); // 1000 for green light

            if (!cars.isEmpty()) {
                cars.get(0).setLeadingCar(getLast());
            }

            if (roads.get(4).getCars().size() > 1 && queues.size() > 1) {
                queues.get(1).setLeadingCar(roads.get(4).getLast());
            }
            state = true;
            lastLightStart = Driver.getTimeElapsed();
        }

//        if (queues.size() < 1) {
//            cars.get(1).setLeadingCar(queues.get(0));
//        } else {
//            cars.get(1).setLeadingCar(queues.get(queues.size() - 1));
//        }
    }
	
	/**
	 * Override for equals method to see if two
	 * intersections are equal.
	 * 
	 * @param toCompareTo
	 * 				Intersection to compare to
	 * @return	True if the intersections are equal.
	 * 			False otherwise.
	 */
	public boolean equals(Object toCompareTo){
		Intersection comp = (Intersection) toCompareTo;
		for(Road road : roads){
			if(!comp.hasRoad(road)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the intersection is connected
	 * to a road.
	 * 
	 * @param road
	 * 			Road to check against
	 * @return	True if road and intersection are 
	 * 			connected. False otherwise.
	 */
	public boolean hasRoad(Road road){
		for(Road rue : roads){
			if(road.equals(rue)){
				return true;
			}
		}
		return false;
	}

    public boolean getState() {
        return state;
    }

    public double getLength() {
        return length;
    }

    public Moveable getLast() {
        return queues.get(queues.size() - 1);
    }
}
