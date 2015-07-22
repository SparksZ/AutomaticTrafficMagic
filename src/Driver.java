
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Zack on 7/4/2015.
 */
public class Driver {

    private static CopyOnWriteArrayList<Intersection> intersections;
    private static double timeElapsed;
    private static CopyOnWriteArrayList<Double> results;
    private static double simulationTime;
    private static int numIntersectionsPerSide = 3;
    private static int numIntersections = (int)Math.pow(numIntersectionsPerSide, 2);
    private static int finalMapSize = (int)((numIntersectionsPerSide + 1)*(Intersection.length + Intersection.roadLength) + Intersection.length*(numIntersectionsPerSide - 1));
    private static Simulation[] sims;
    private static ArrayList<Results> generationResults;
    private static ArrayList<Future<Results>> futureGenResults;
    private static ExecutorService executor;
    private static int generationNum;
    private static byte[][] chromosomes;
    private static double mutationProb;

    public static int carID;
    // CONSTANTS
    public static final double frameRate = .5; // seconds
    public static final int paintRate = 10; // milliseconds
    public static final int PERIOD = 64;
    public static final int POPULATION_SIZE = 5; // 20;
    public static final int NUM_GENERATIONS = 1;
    public static final int SURVIVORS_PER_GENERATION = 2;
    public static final int INDIVIDUALS_TO_CLONE = 2;
    public static final int SIMULATION_TIME = 7200;
    // public static final double INITIAL_MUTATION_P = 0.75;
    public static final double INITIAL_MUTATION_P = 0.2;
    public static final double MUTATION_DECAY_RATE = 0.982;

    public static void main(String[] args) {
        // start with random chromosomes
        Random prng = new Random();
        mutationProb = INITIAL_MUTATION_P;
        executor = Executors.newFixedThreadPool(4);
        byte[][][] lightData = new byte[POPULATION_SIZE][numIntersections][PERIOD];
        byte[][] chromosomes = new byte[POPULATION_SIZE][numIntersections*PERIOD];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < numIntersections; j++) {
                for (int k = 0; k < PERIOD; k++) {
                    lightData[i][j][k] = (byte)prng.nextInt(4);
                    chromosomes[i][j*k] = lightData[i][j][k];
                }
            }
        }
        sims = new Simulation[POPULATION_SIZE];
        generationResults = new ArrayList<Results>(POPULATION_SIZE);
        futureGenResults = new ArrayList<Future<Results>>(POPULATION_SIZE);
        // run simulation with each chromosome
        for (int i = 0; i < sims.length; i++) {
            System.out.println("Queuing simulation for individual " + (i + 1) + "...");
            sims[i] = new Simulation(numIntersectionsPerSide, SIMULATION_TIME, lightData[i], i);
            futureGenResults.add(executor.submit(sims[i]));
        }
        System.out.println("Waiting to finish tasks...");
        for (Future<Results> fr : futureGenResults) {
            Results r = null;
            try {
                r = fr.get();
                generationResults.add(r);
                System.out.println(r.AVERAGE_SPEED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        boolean allFinished = false;
        /*try {
            allFinished = executor.awaitTermination(2, java.util.concurrent.TimeUnit.MINUTES);
        } catch (java.lang.InterruptedException e) { e.printStackTrace(); }
        if (!allFinished) {
            System.out.println("Unable to finish queued tasks before timeout!");
        }
        else {
            System.out.println("Whew. All done!");
        }
        */
        // iterate over generations:
        for (int gen = 0; gen < NUM_GENERATIONS; gen++) {
            System.out.println("Starting generation " + gen + ", mutation rate = " + mutationProb);
            // pick the best few
            int[] indicesOfSurvivors = findBestNResults(generationResults, SURVIVORS_PER_GENERATION);
            byte[][] survivorChromosomes = new byte[SURVIVORS_PER_GENERATION][lightData[indicesOfSurvivors[0]].length*PERIOD];
            for (int i = 0; i < survivorChromosomes.length; i++) {
                survivorChromosomes[i] = chromosomes[indicesOfSurvivors[i]];
                System.out.println("Survivor " + (i + 1) + ": " + lightDataToString(lightData[i]));
            }
            // System.out.println("best: " + indicesOfBestIndividuals[0]);
            // System.out.println("second best: " + indicesOfBestIndividuals[1]);
            // perform crossover
            // perform mutation
            byte[][][] nextGeneration = new byte[POPULATION_SIZE][numIntersections][PERIOD];
            // randomly sample and do crossovers over random ranges
            for (int childNum = 0; childNum < nextGeneration.length - INDIVIDUALS_TO_CLONE; childNum++) {
                System.out.println("generating child " + childNum);
                int parent1Index, parent2Index;
                parent1Index = prng.nextInt(survivorChromosomes.length);
                do {
                    parent2Index = prng.nextInt(survivorChromosomes.length);
                } while (parent1Index == parent2Index);
                byte[] parent1Chromosome = survivorChromosomes[parent1Index];
                byte[] parent2Chromosome = survivorChromosomes[parent2Index];
                int crossoverStart = prng.nextInt(parent1Chromosome.length - 1); // -1 to make sure the last index isn't picked
                int crossoverEnd = crossoverStart + 1 + prng.nextInt(parent1Chromosome.length - crossoverStart - 1);
                System.out.println("child crossoverStart = " + crossoverStart);
                System.out.println("child crossoverEnd = " + crossoverEnd);
                byte[] child = new byte[parent1Chromosome.length];
                for (int i = 0; i < crossoverStart; i++) {
                    if (prng.nextDouble() < mutationProb) {
                        child[i] = (byte)prng.nextInt(4);
                    }
                    else {
                        child[i] = parent1Chromosome[i];
                    }
                    nextGeneration[childNum][i/PERIOD][i % PERIOD] = child[i];
                }
                for (int i = crossoverStart; i <= crossoverEnd; i++) {
                    if (prng.nextDouble() < mutationProb) {
                        child[i] = (byte)prng.nextInt(4);
                    }
                    else {
                        child[i] = parent2Chromosome[i];
                    }
                    nextGeneration[childNum][i/PERIOD][i % PERIOD] = child[i];
                }
                for (int i = crossoverEnd + 1; i < child.length; i++) {
                    if (prng.nextDouble() < mutationProb) {
                        child[i] = (byte)prng.nextInt(4);
                    }
                    else {
                        child[i] = parent1Chromosome[i];
                    }
                    nextGeneration[childNum][i/PERIOD][i % PERIOD] = child[i];
                }
                System.out.println("Child " + (childNum + 1) + ": " + lightDataToString(nextGeneration[childNum]));
            }

            // also, clone 2 of the best
            int[] indicesOfBestIndividuals = findBestNResults(generationResults, INDIVIDUALS_TO_CLONE);
            byte[][][] bestIndividuals = new byte[INDIVIDUALS_TO_CLONE][lightData[indicesOfBestIndividuals[0]].length][PERIOD];
            for (int i = 0; i < bestIndividuals.length; i++) {
                bestIndividuals[i] = lightData[indicesOfBestIndividuals[i]];
                int newIndex = nextGeneration.length - INDIVIDUALS_TO_CLONE + i;
                nextGeneration[newIndex] = bestIndividuals[i];
                System.out.println("Clone " + (i + 1) + "(index " + newIndex +  "): " + lightDataToString(bestIndividuals[i]));
            }
            System.exit(0);
            // run simulation
            // executor.submit(sims[i]);
            mutationProb *= MUTATION_DECAY_RATE;
        }
        executor.shutdown();
    }

    public static void oldMain(String[] args) {
        Random prng = new Random();
        byte[][] lightData = new byte[numIntersections][PERIOD];
        for (int j = 0; j < numIntersections; j++) {
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

    private static int[] findBestNResults(ArrayList<Results> allResults, int n) {
        int[] bestResults = new int[n];
        ArrayList<Results> resultsCopy = (ArrayList<Results>)allResults.clone();
        for (int m = 0; m < n; m++) {
            double bestAvgSpeed = 0;
            int bestIndex = -1;
            for (int i = 0; i < resultsCopy.size(); i++) {
                Results currResult = resultsCopy.get(i);
                if (currResult.AVERAGE_SPEED > bestAvgSpeed) {
                    bestAvgSpeed = currResult.AVERAGE_SPEED;
                    bestIndex = i;
                }
            }
            // can't remove because that would change the indices on future iterations over m
            resultsCopy.set(bestIndex, new Results(0, 1, 0, -1)); // set to a new value that will never be "best"
            bestResults[m] = bestIndex;
        }
        return bestResults;
    }

    private static String lightDataToString(byte[][] b) {
        StringBuilder sb = new StringBuilder(2 + b.length*b[0].length);
        sb.append("[");
        for (byte[] intersectionNums : b) {
            for (byte greenLight : intersectionNums) {
                sb.append(greenLight);
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
