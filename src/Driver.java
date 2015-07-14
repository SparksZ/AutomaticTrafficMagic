
import javax.swing.JFrame;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Zack on 7/4/2015.
 */
public class Driver {

    private static CopyOnWriteArrayList<Intersection> intersections;
    private static double timeElapsed;
    private static CopyOnWriteArrayList<Double> results;
    private static double simulationTime;
    private static int numIntersectionsPerSide = 3;
    private static int finalMapSize = (int)((numIntersectionsPerSide + 1)*(Intersection.length + Intersection.roadLength) + Intersection.length*(numIntersectionsPerSide - 1));
    private static Simulation[] sims;
    private static ExecutorService executor;

    public static int carID;
    // CONSTANTS
    public static final double frameRate = .5; // seconds
    public static final int paintRate = 10; // milliseconds
    public static final int PERIOD = 64;
    public static final int POPULATION_SIZE = 20;
    public static final int NUM_GENERATIONS = 50;
    public static final int SIMULATION_TIME = 7200;

    public static void main(String[] args) {
        // start with random chromosomes
        Random prng = new Random();
        executor = Executors.newFixedThreadPool(4);
        byte[][][] lightData = new byte[POPULATION_SIZE][(int)Math.pow(numIntersectionsPerSide, 2)][PERIOD];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < Math.pow(numIntersectionsPerSide, 2); j++) {
                for (int k = 0; k < PERIOD; k++) {
                    lightData[i][j][k] = (byte)prng.nextInt(4);
                }
            }
        }
        sims = new Simulation[POPULATION_SIZE];
        // run simulation with each chromosome
        for (int i = 0; i < sims.length; i++) {
            System.out.println("Queuing simulation for individual " + (i + 1) + "...");
            sims[i] = new Simulation(numIntersectionsPerSide, SIMULATION_TIME, lightData[i]);
            executor.execute(sims[i]);
        }
        executor.shutdown();
        while (!executor.isTerminated()) { }
        // iterate over generations:
        for (int gen = 0; gen < 50; gen++) {
            // pick best few
            // perform crossover
            // perform mutation
            // also, clone 2 of the best
            // run simulation
        }
    }

    public static void oldMain(String[] args) {
        Random prng = new Random();
        byte[][] lightData = new byte[(int)Math.pow(numIntersectionsPerSide, 2)][PERIOD];
        for (int j = 0; j < Math.pow(numIntersectionsPerSide, 2); j++) {
            for (int k = 0; k < PERIOD; k++) {
                lightData[j][k] = (byte)prng.nextInt(4);
            }
        }
        Simulation sim1 = new Simulation(numIntersectionsPerSide, SIMULATION_TIME, lightData);
        JFrame simulatorWindow = new JFrame("Automatic Traffic Magic");
        SimulationPanel drawingPanel = new SimulationPanel(sim1, 500);
        simulatorWindow.add(drawingPanel);
        simulatorWindow.pack();
        simulatorWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulatorWindow.setVisible(true);
    }

}
