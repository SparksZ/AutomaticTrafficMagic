import java.util.Arrays;
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
    private int timeElapsed;
    private byte currGreenLight; // 0 = north, 1 = east, 2 = south, 3 = west
    public static final byte NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
    private final byte[] lightList;
    private int lightIndex = 0;
    private double timeSinceStateChange = 0;
    private double xPos; // eastern most point of the intersection
    private double yPos; // southern most point of the intersection
    private CopyOnWriteArrayList<CarSink> sinks = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<CarFactory> factories =
            new CopyOnWriteArrayList<>();


    // CONSTANTS
    private final double timeBetweenStates = 400;
    private final double speedLimit = 16;
    private final int secondsPerCar = 5;
    public static final double length = 75; // length of intersection (m)
    public static final double roadLength = 200;

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
    public Intersection(double x, double y, int sinkScenario, byte[] lightList) {
        xPos = x;
        yPos = y;
        this.sinkScenario = sinkScenario;
        this.lightList = lightList;

        setUpRoads();

        // Construct Queues of cars. Place dummy cars (Lights)
        queues = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 4; i++) {
            queues.add(new CopyOnWriteArrayList<Moveable>());
        }
        queues.get(0).add(new DummyCar(xPos, yPos));
        queues.get(1).add(new DummyCar(xPos, yPos));
        queues.get(2).add(new DummyCar(xPos, yPos));
        queues.get(3).add(new DummyCar(xPos, yPos));

        // set the initial green light
        currGreenLight = lightList[0];
        queues.get(currGreenLight).set(0, new DummyCar(100000,100000));
    }

    /**
     * Adds car from road to Intersection.  If the intersection is backed up,
     * it will return false, which means the road should start backing up.
     * @param whichRoad which road is passing the car (0-3 corresponding to N/S/E/W)
     * @param newCar the car object to add
     * @return if the car was added.
     */
    public boolean addCar(int whichRoad, Moveable newCar) {
        queues.get(whichRoad).add(newCar);
        return true;
    }

    /**
     * Updates all the lights and car positions that are in the queues
     */
    public void update(int timeElapsed) {
        this.timeElapsed = timeElapsed;

        // Changes the lights if necessary
        updateRoadDummy();
        advanceCarsInQueue(timeElapsed);
        updateRoads(timeElapsed);
        updateFactories(timeElapsed);
    }

    /**
     * Updates the factories if applicable
     */
    private void updateFactories(int timeElapsed) {
        if (!factories.isEmpty()) {
           for (CarFactory f : factories) {
               f.update(timeElapsed);
           }
        }
    }

    /**
     * Updates all out roads going to other intersections and roads that are
     * connected to factories/sinks
     */
    private void updateRoads(int timeElapsed) {
        // Update all out roads
        for (int i = 4; i < 8; i++) {
            roads.get(i).update(timeElapsed);
        }

        /* ************************************************
         * Special cases where roads connect to factories *
         ************************************************ */
        // North Intersections connected to factories
        if (Arrays.asList(0, 1, 2).contains(sinkScenario)) {
            roads.get(0).update(timeElapsed);
        }

        // East roads connected to factories
        if (Arrays.asList(2, 3, 4).contains(sinkScenario)) {
            roads.get(1).update(timeElapsed);
        }

        // South roads connected to factories
        if (Arrays.asList(4, 5, 6).contains(sinkScenario)) {
            roads.get(2).update(timeElapsed);
        }

        // West roads connected to factories
        if (Arrays.asList(6, 7, 0).contains(sinkScenario)) {
            roads.get(3).update(timeElapsed);
        }
    }

    /**
     * Updates the dummy cars that act as lights
     */
    private void updateRoadDummy() {
        boolean changed = false;
        timeSinceStateChange += .5;
        if (timeSinceStateChange >= timeBetweenStates) {
            byte prevGreenLight = currGreenLight;
            lightIndex++;
            lightIndex %= lightList.length;
            currGreenLight = lightList[lightIndex];
            if (prevGreenLight != currGreenLight) {
                queues.get(prevGreenLight).set(0, new DummyCar(xPos, yPos));
                queues.get(currGreenLight).set(0, new DummyCar(100000,100000));
                changed = true;
            }
            timeSinceStateChange = 0;
        }

        if (changed) {
            for (int i = 0; i < 4; i++) {
                CopyOnWriteArrayList<Moveable> q = queues.get(i);

                // Updates lead cars in the Queues

                if (q.size() > 1) {
                    q.get(1).setLeadingCar(q.get(0));
                }

                // Updates lead cars in roads to last in the Queues

                CopyOnWriteArrayList<Moveable> cars = roads.get(i).getCars();

                if (!cars.isEmpty()) {
                    cars.get(0).setLeadingCar(getLast(i));
                }
            }
        }
    }

    private void advanceCarsInQueue(int timeElapsed) {
        // Advances Cars in Queues
        for (int i = 0; i < 4; i++) { // loops through queues
            CopyOnWriteArrayList<Moveable> q = queues.get(i);
            if (q.size() > 1) {
                for (int j = 1; j < q.size(); j++) { // loops through cars in queue
                    Moveable car = q.get(j);
                    car.update(timeElapsed);

                    Road next = roads.get(i); // ugly instantiation...
                    boolean removed = false;

                    // Removes cars from the intersection when they reach the end
                    int route = car.route();
                    /*
                    if(((Car)car).getID() == 96){
                    	System.out.println(route);
                    }
                    */
                    switch (i) {
                        case 0: // North in queue
                            if (car.getYPosition() > yPos) {
                                // Gets the road the car is newly on
                            	if (route == 0) {
                            		next = roads.get(6);
                            	} else if (route == 1) {
                            		next = roads.get(5);
                            	} else {
                            		next = roads.get(7);
                            	}
                                removed = true;
                            }
                            break;
                        case 1: // East in queue
                            if (car.getXPosition() < xPos) {
                                // Gets the road the car is newly on
                                if (route == 0) {
                            		next = roads.get(7);
                            	} else if (route == 1) {
                            		next = roads.get(6);
                            	} else {
                            		next = roads.get(4);
                            	}
                                removed = true;
                            }
                            break;
                        case 2: // South in queue
                            if (car.getYPosition() < yPos) {
                                if (route == 0) {
                            		next = roads.get(4);
                            	} else if (route == 1) {
                            		next = roads.get(5);
                            	} else {
                            		next = roads.get(7);
                            	}
                                removed = true;
                            }
                            break;
                        case 3: // West in queue
                            if (car.getXPosition() > xPos) {
                                if (route == 0) {
                            		next = roads.get(5);
                            	} else if (route == 1) {
                            		next = roads.get(6);
                            	} else {
                            		next = roads.get(4);
                            	}
                                removed = true;
                            }
                            break;
                    }

                    if (removed) {
                        // removes car from q and add it to next road
                        car.setLeadingCar(next.getLast());
                        next.addCar(car);
                        q.remove(car);

                        car.setXPosition(next.getXStart());
                        car.setYPosition(next.getYStart());

                        // Sets next lead car's lead car to the dummy
                        if (q.size() > 1) {
                            q.get(1).setLeadingCar(q.get(0));
                        } else {
                            /* If the preceding road is empty don't need to set
                               the last lead's car
                             */
                            if (!roads.get(i).getCars().isEmpty()) {
                                roads.get(i).getFirst().
                                        setLeadingCar(q.get(0));
                            }
                        }
                    }
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

    public byte getGreenDirection() {
        return currGreenLight;
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
     * @param whichRoad the road in the collection of roads to modify
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
            s.setDummy();
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
            s.setDummy();
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
            s.setDummy();
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
            s.setDummy();
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

    public String toString() {
        String green;
        switch (currGreenLight) {
            case 0: green = "north"; break;
            case 1: green = "east"; break;
            case 2: green = "south"; break;
            case 3: green = "west"; break;
            default: green = "INVALID STATE";
        }
        // should include more about the state in a toString() method
        return green;
    }

    /**
     * Loops over all sinks and provides car data from sinks
     * @return a vector of (Total Distance, Total Time, # of cars)
     */
    public CopyOnWriteArrayList<Double> getSinkData() {
        CopyOnWriteArrayList<Double> result = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 3; i++) {
            result.add(0.0);
        }

        if (sinks != null) {
            for (CarSink s : sinks) {
                CopyOnWriteArrayList<Double> data = s.distanceDurationSize();

                for (int i = 0; i < 3; i++) {
                    result.set(i, result.get(i) + data.get(i));
                }
            }

            return result;
        } else {
            return null;
        }
    }

    public int getX() {
        return (int)xPos;
    }

    public int getY() {
        return (int)yPos;
    }

    public CopyOnWriteArrayList<Road> getRoads() {
        return roads;
    }

    public CopyOnWriteArrayList<CopyOnWriteArrayList<Moveable>> getQueues() {
        return queues;
    }
}
