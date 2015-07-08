import java.util.concurrent.CopyOnWriteArrayList;

public class Intersection implements Updateable, CarContainer {
    private final int sinkScenario;
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Moveable>> queues;
    private CopyOnWriteArrayList<Road> roads; /* 0 - 3 are in roads 4 - 7 are out. i % 4
                                      gives position of road wrt intersection.
                                      0 north, 1 east, 2 south, 3 west. null if
                                      road doesn't exist */
    private double northStart;
    private double northEnd;
    private CopyOnWriteArrayList<Double> lengthOfCars;
    private boolean state; // true if N/S is green, false if E/W is green
	private double lastLightStart;
    private double xPos; // eastern most point of the intersection
    private double yPos; // southern most point of the intersection
    private CopyOnWriteArrayList<CarSink> sinks = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CarFactory> factories =
            new CopyOnWriteArrayList<>();


    // CONSTANTS
    private final double nSLightLength = 15;
    private final double eWLightLength = 15;
    private final double speedLimit = 16;
    private final int secondsPerCar = 5;
    public static final double length = 75; // length of intersection (m)
    public static final double roadLength = 200;
    public static final double roadWidth = 15;

    /**
     * Instantiates a new intersection.  This creates all roads north and west
     * of the intersection. The east and south roads will be connected by the
     * Driver of the simulation.  For now all are started with NS green EW red.
     * @param x The southern most limit of the intersection
     * @param y The eastern most limit of the intersection
     * @param sinkScenario -1...8 to specify what roads need to be connected to
     *                     sinks.
     *                     -1: Middle Intersection - no sinks
     *                     0: Northwest Corner
     *                     1: North side of grid
     *                     2: Northeast Corner
     *                     3: East side of grid
     *                     4: Southeast Corner
     *                     5: South side of grid
     *                     6: Southwest Corner
     *                     7: West side of grid
     */
    public Intersection(double x, double y, int sinkScenario) {
        xPos = x;
        yPos = y;
        this.sinkScenario = sinkScenario;

        setUpRoads();

        // Construct Queues of cars. Place dummy cars (Lights)
        queues = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 4; i++) {
            queues.add(new CopyOnWriteArrayList<>());
        }

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

    public Road getRoad(int i) {
        return roads.get(i);
    }

    /**
     * Sets the intersection of the road at whichRoad to container
     * @param whichRoad the road in the collection of roads to modifiy
     * @param r the road to be hooked up to the intersection
     */
    public void setCarContainer(int whichRoad, Road r) {
        roads.set(whichRoad, r);
        roads.get(whichRoad).setIntersection(this);
    }

    /**
     * Crazy function evaluates which sink scenario and hooks up sinks or
     * or factories if appropriate
     */
    private void setUpRoads() {
        /* Create North and West Roads (Driver will connect others after all
           instantiated) unless factory or sink */
        roads = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 8; i++) {
            roads.add(null);
        }

        // Checks if North out/in needs to connect to a sink/factory
        if (sinkScenario == 0 || sinkScenario == 1 || sinkScenario == 2) {
            // in road
            Road in = new Road(speedLimit, roadLength, this, xPos, yPos -
                    length - roadLength, true, true, false); // North In

            CarFactory f = new CarFactory(in, secondsPerCar);
            factories.add(f);
            roads.set(0, in);

            // out road
            CarSink s = new CarSink(null);
            Road out = new Road(speedLimit, roadLength, s, xPos, yPos -
                    length - roadLength, true, false, true);

            s.setRoad(out);
            sinks.add(s);
            roads.set(4, out);
        } else {
            roads.set(0, new Road(speedLimit, roadLength, this, xPos, yPos -
                    length - roadLength, true, true, false));

            roads.set(4, new Road(speedLimit, roadLength, null, xPos, yPos -
                    length - roadLength, true, false, false));
        }

        // Checks if East out/in needs to connect to a sink/factory
        if (sinkScenario == 2 || sinkScenario == 3 || sinkScenario == 4 ) {
            // in road
            Road in = new Road(speedLimit, roadLength, this, xPos + length,
                    yPos, false, false, false);

            CarFactory f = new CarFactory(in, secondsPerCar);
            factories.add(f);
            roads.set(1, in);

            // out road
            CarSink s = new CarSink(null);
            Road r = new Road(speedLimit, roadLength, s, xPos + length, yPos,
                    false, true, true);
            s.setRoad(r);
            sinks.add(s);
            roads.set(5, r);
        } else {
            roads.set(1, null);
            roads.set(5, null);
        }

        // Checks if South out/in needs to connect to sink/factory
        if (sinkScenario == 4 || sinkScenario == 5 || sinkScenario == 6 ) {
            // in road
            Road in = new Road(speedLimit, roadLength, this, xPos, yPos +
                    length, true, false, false);

            CarFactory f = new CarFactory(in, secondsPerCar);
            factories.add(f);
            roads.set(2, in);

            // out road
            CarSink s = new CarSink(null);
            Road r = new Road(speedLimit, roadLength, s, xPos, yPos + length,
                    true, true, true);
            s.setRoad(r);
            sinks.add(s);
            roads.set(6, r);
        } else {
            roads.set(2, null);
            roads.set(6, null);
        }

        // Checks if West out/in needs to connect to sink/factory
        if (sinkScenario == 6 || sinkScenario == 7 || sinkScenario == 0 ) {
            // in road
            Road in = new Road(speedLimit, roadLength, this, xPos - length -
                    roadLength, yPos, false, true, false);

            CarFactory f = new CarFactory(in, secondsPerCar);
            factories.add(f);
            roads.set(3, in);

            // out road
            CarSink s = new CarSink(null);
            Road r = new Road(speedLimit, roadLength, s, xPos - length -
                    roadLength, yPos, false, false, true);
            s.setRoad(r);
            sinks.add(s);
            roads.set(7, r);
        } else {
            roads.set(3, new Road(speedLimit, roadLength, this, xPos - length -
                    roadLength, yPos, false, true, false));

            roads.set(7, new Road(speedLimit, roadLength, null, xPos - length -
                    roadLength, yPos, false, false, false));
        }
    }

    public int getSinkScenario() {
        return sinkScenario;
    }
}
