import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;

public class Simulation implements Runnable {

    private CopyOnWriteArrayList<Intersection> intersections;
    private CopyOnWriteArrayList<Double> results;
    private int numIntersectionsPerSide;
    private static int carID;
    private int timeElapsed = 0;
    private final double simulationTime;
    private final double TIME_STEP = 1;
    private byte[][] lightTimingData;


    public Simulation (int numIntersectionsPerSide, int simulationLength, byte[][] lightTimingData) {
        this.numIntersectionsPerSide = numIntersectionsPerSide;
        this.simulationTime = simulationLength;
        this.lightTimingData = lightTimingData;

        // init variables
        intersections = new CopyOnWriteArrayList<Intersection>();
        results = new CopyOnWriteArrayList<Double>();

        // create intersections
        createIntersections(numIntersectionsPerSide, lightTimingData);
        connectIntersections();
    }

    public double runSim() {
        while (timeElapsed < simulationTime) {
            updateSim();
            // int checkpointTimeStep = (int)(simulationTime/20);
            // if (timeElapsed % checkpointTimeStep == 0) {
            //     System.out.println(timeElapsed / simulationTime * 100 + "% \r");
            // }
        }
        return averageSpeed();
        //System.out.println("The average speed of the cars was: " +
        //        averageSpeed() + " m/s. averaging over " + results.get(2) + " cars");
    }

    public void run() {
        StringBuilder sb = new StringBuilder(2 + numIntersectionsPerSide*numIntersectionsPerSide*lightTimingData[0].length);
        sb.append("[");
        for (byte[] intersectionNums : lightTimingData) {
            for (byte greenLight : intersectionNums) {
                sb.append(greenLight);
            }
        }
        sb.append("]");
        System.out.println(Thread.currentThread().getName() + " Start.");
        System.out.println("Input vector: " + sb.toString());
        double averageSpeed = runSim();
        System.out.println(Thread.currentThread().getName() + " End. avgSpeed = " + averageSpeed);
    }

    public void updateSim() {
        for (Intersection i : intersections) {
            i.update(timeElapsed);
        }
        timeElapsed += TIME_STEP;
    }

    public void createIntersections(int y, byte[][] lightTimingData) {
        double totalLength = Intersection.length + Intersection.roadLength;

        int currLightNum = 0;
        for (int i = 0; i < y; i++) { // Rows of intersections
            for (int j = 0; j < y; j++) { // Columns of intersections

                int sinkScenario = getSinkScenario(y, i, j);

                double xCoordinate = 1000 + totalLength * (j + 1) + Intersection.length*j;
                double yCoordinate = 1000 + totalLength * (i + 1) + Intersection.length*i;

                Intersection inter = new Intersection(xCoordinate, yCoordinate,
                        sinkScenario, lightTimingData[currLightNum]);
                intersections.add(inter);
                currLightNum++;
            }
        }
    }

    public void connectIntersections() {
        int y = numIntersectionsPerSide;

        for (int i = 0; i < y; i++) { // Rows of intersections
            for (int j = 0; j < y; j++) { // Columns of intersections
                Intersection inter = intersections.get(i * y + j); // Gets relevant intersection
                int sinkScenario = inter.getSinkScenario();

                /* the starting intersection in the second lines of each if
                   block is the one that doesn't own the road to be connected.
                   Currently the intersections are created owning the roads to
                   the north and west of them. Proper implementation would be
                   that the intersections are created owning roads that go out.
                                            O__O
                 */

                // Connects all North out roads that need North Intersection
                if (Arrays.asList(7, -1, 3, 6, 5, 4).contains(sinkScenario)) {
                    Intersection remote = intersections.get((i - 1) * y + j);
                    remote.setCarContainer(2, inter.getRoad(4));
                    //remote.setCarContainer(6, inter.getRoad(0));
                }

                // Connects all East Out roads that need East Intersection
                if (Arrays.asList(0, 1, 7 , -1, 6 , 5).contains(sinkScenario)) {
                    Intersection remote = intersections.get(i * y + j + 1);
                    inter.setRoad(5, remote.getRoad(3));
                    //remote.setCarContainer(3, inter.getRoad(5));
                }

                // Connect all South Out roads that need South Intersection
                if (Arrays.asList(0, 1, 2, 7, -1, 3).contains(sinkScenario)) {
                    Intersection remote = intersections.get((i + 1) * y + j);
                    inter.setRoad(6, remote.getRoad(0));
                    //inter.setCarContainer(6, remote.getRoad(0));
                }

                // Connects all West Out roads that need West Intersection
                if (Arrays.asList(1, 2, -1, 3, 5, 4).contains(sinkScenario)) {
                    Intersection remote = intersections.get(i * y + j - 1);
                    remote.setCarContainer(1, inter.getRoad(7));
                    //remote.setCarContainer(5, inter.getRoad(3));
                }
            }
        }
    }

    private int getSinkScenario(int y, int i, int j) {
        int sinkScenario;
                /* **********************************
                 *             Top Row              *
                 ********************************** */
        if (i == 0) {

            // Determine sinkScenario
            if (j % y == 0) {
                sinkScenario = 0;
            } else if (j % y == y - 1) {
                sinkScenario = 2;
            } else {
                sinkScenario = 1;
            }

                /* **********************************
                 *             Bottom Row           *
                 ********************************** */
        } else if (i == y - 1) {

            // Determine sinkScenario
            if (j % y == 0) {
                sinkScenario = 6;
            } else if (j % y == y - 1) {
                sinkScenario = 4;
            } else {
                sinkScenario = 5;
            }

                /* **********************************
                 *            Middle Rows           *
                 ********************************** */
        } else {

            //Determine sinkScenarios
            if (j % y == 0) {
                sinkScenario = 7;
            } else if (j % y == y - 1) {
                sinkScenario = 3;
            } else {
                sinkScenario = -1;
            }
        }

        return sinkScenario;
    }

    public double averageSpeed() {
        results = new CopyOnWriteArrayList<Double>();
        for (int i = 0; i < 3; i++) {
            results.add(0.0);
        }

        for (Intersection inter : intersections) {
            CopyOnWriteArrayList<Double> data = inter.getSinkData();

            for (int i = 0; i < 3; i++) {
                results.set(i, results.get(i) + data.get(i));
            }
        }
        return results.get(0) / results.get(1);
    }

    public int getNumIntersectionsPerSide() {
        return numIntersectionsPerSide;
    }

    public CopyOnWriteArrayList<Intersection> getIntersections() {
        return intersections;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    //TODO: make non-static
    public static int getCarID() {
        return carID++;
    }
}
