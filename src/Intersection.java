import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Intersection {
	private CopyOnWriteArrayList<CopyOnWriteArrayList<Moveable>> queues;
    private CopyOnWriteArrayList<Road> roads; /* 0 - 3 are in roads 4 - 7 are out. i % 4
                                      gives position of road wrt intersection.
                                      0 north, 1 east, 2 south, 3 west. null if
                                      road doesn't exist */
    public static final double length = 75; // length of intersection (m)
    public static final double roadLength = 200;
    public static final double roadWidth = 15;
    private double northStart;
    private double northEnd;
    private CopyOnWriteArrayList<Double> lengthOfCars;
    private boolean state; // true if N/S is green, false if E/W is green
    private double nSLightLength = 15;
    private double eWLightLength = 15;
	private double lastLightStart;
    private double xPos; // eastern most point of the intersection
    private double yPos; // southern most point of the intersection
    private final double speedLimit = 16;

    /**
     * Instantiates a new intersection.  This creates all roads north and west
     * of the intersection. The east and south roads will be connected by the
     * Driver of the simulation.  For now all are started with NS green EW red.
     * @param x
     * @param y
     */
    public Intersection(double x, double y) {
        xPos = x;
        yPos = y;


        /* Create North and West Roads (Driver will connect others after all
           instantiated */
        roads = new CopyOnWriteArrayList<>();
        roads.add(new Road(speedLimit, roadLength, this, xPos, yPos - length -
                roadLength, true, true)); // North In
        roads.add(null); // East In
        roads.add(null); // South In
        roads.add(new Road(speedLimit, roadLength, this, xPos - length -
                roadLength, yPos, false, true)); // West In
        roads.add(new Road(speedLimit, roadLength, this, xPos, yPos - length -
                roadLength, true, false)); // North Out
        roads.add(null); // East Out
        roads.add(null); // South Out
        roads.add(new Road(speedLimit, roadLength, this, xPos - length -
                roadLength, yPos, false, false)); // West Out

        // Construct Queues of cars. Place dummy cars (Lights)
        queues = new CopyOnWriteArrayList<>();
        queues.get(0).add(new DummyCar(xPos, yPos + 1000)); // North in green
        queues.get(1).add(new DummyCar(xPos - roadWidth, yPos)); // East in red
        queues.get(2).add(new DummyCar(xPos, yPos - 1000)); // South in green
        queues.get(3).add(new DummyCar(xPos - roadWidth, yPos)); // West in red


        // instantiate with no cars in queue
        lengthOfCars = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 8; i++) {
            lengthOfCars.add(0.0);
        }

        lastLightStart = 0; // 0 is the start of the simulation

    }

    /**
     * Adds car from road to Intersection.  If the intersection is backed up,
     * it will return false, which means the road should start backing up.
     * @param whichRoad which road is passing the car (0-3 corresponding to N/S/E/W)
     * @param newCar the car object to add
     * @return if the car was added.
     */
    public boolean addCar(int whichRoad, Moveable newCar) {
        double lOC = lengthOfCars.get(whichRoad);
        if (lOC + newCar.getLength() + Car.minimumGap >
                length) {
            return false;
        }

        queues.get(whichRoad).add(newCar);

//        // Sets the new lead car's (in the incoming road)
//        if (!roads.get(whichRoad).getCars().isEmpty()) {
//            roads.get(whichRoad).getCars().get(0).setLeadingCar(newCar);
//        }

        lengthOfCars.set(whichRoad, lOC + newCar.getLength() + Car.minimumGap);
        return true;
    }

    /**
     * Updates all the lights and car positions that are in the queues
     */
    public void update() {

        updateRoadDummy();

        for (int i = 0; i < 4; i++) {
            CopyOnWriteArrayList<Moveable> q = queues.get(i);
            for (int j = 1; j < q.size(); j++) {
                Moveable car = q.get(j);
                car.update();

                // Removes cars from the intersection when they reach the end
                switch(i) {
                    case 0:
                        if (car.getYPosition() > yPos) {
                            roads.get(4).addCar(removeCar(i));
                        }
                        break;
                    case 1:
                        if (car.getXPosition() < xPos) {
                            roads.get(5).addCar(removeCar(i));
                        }
                        break;
                    case 2:
                        if (car.getYPosition() < yPos) {
                            roads.get(6).addCar(removeCar(i));
                        }
                        break;
                    case 3:
                        if (car.getXPosition() > xPos) {
                            roads.get(7).addCar(removeCar(i));
                        }
                        break;
                }
            }
        }
    }

    /**
     * Removes the lead car in the specified queue
     * @param i the queue to remove the car from
     * @return returns the car removed from the queue
     */
    private Moveable removeCar(int i) {
        if (queues.get(i).size() > 2) {
            queues.get(i).get(2).setLeadingCar(queues.get(i).get(0));
        }
        return queues.get(i).remove(1); /* the first car (0) is a dummy to correct
                                  behavior of the cars approaching traffic
                                  lights/intersections */
    }

    /**
     * Updates the dummy cars that act as lights
     */
    private void updateRoadDummy() {
        if (state == true && (Driver.getTimeElapsed() - lastLightStart) ==
                nSLightLength) { // NS light needs to change to red

            // Updating Dummies
            queues.get(0).set(0, new DummyCar(xPos, yPos - roadWidth)); // North In
            queues.get(1).set(0, new DummyCar(xPos - 1000, yPos)); // East In
            queues.get(2).set(0, new DummyCar(xPos, yPos + roadWidth)); // South In
            queues.get(3).set(0, new DummyCar(xPos + 1000, yPos)); // West In

            state = false;
            lastLightStart = Driver.getTimeElapsed();
        }

        if (state == false && (Driver.getTimeElapsed() - lastLightStart) ==
                eWLightLength) { // NS light needs to change to green

            // Updating Dummies
            queues = new CopyOnWriteArrayList<>();
            queues.get(0).add(new DummyCar(xPos, yPos + 1000)); // North in green
            queues.get(1).add(new DummyCar(xPos - roadWidth, yPos)); // East in red
            queues.get(2).add(new DummyCar(xPos, yPos - 1000)); // South in green
            queues.get(3).add(new DummyCar(xPos - roadWidth, yPos)); // West in red

            state = true;
            lastLightStart = Driver.getTimeElapsed();
        }

        // Updates all in roads' lead car's leading car :)
        for (int i = 0; i < 4; i++) {
            CopyOnWriteArrayList<Moveable> cars = roads.get(i).getCars();

            if (!cars.isEmpty()) {
                cars.get(0).setLeadingCar(getLast(i));
            }
        }
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

    /**
     * @param i the queue to retrieve car from
     * @return the last car in the specified queue
     */
    public Moveable getLast(int i) {
        return queues.get(i).get(queues.get(i).size() - 1);
    }

    /**
     * Connects up roads to the intersection
     * @param i the road index to hook the passed road to.
     *          0 - 3 are in roads 4 - 7 are out. i % 4 gives position of road
     *          wrt intersection. 0 north, 1 east, 2 south, 3 west, null if road
     *          doesn't exist
     * @param road the road to hook up to the intersection
     */
    public void setRoad(int i, Road road) {
        roads.set(i, road);
    }
}
