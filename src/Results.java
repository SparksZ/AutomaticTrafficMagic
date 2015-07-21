/* represents a struct holding the results of a Simulation execution
 */
public class Results {
    public final double AVERAGE_SPEED;
    public final double TOTAL_DISTANCE;
    public final double TOTAL_DURATION;
    public final double NUM_CARS;
    public final int SIMULATION_ID;

    public Results(double distance, double duration, double size, int simulationID) {
        this.TOTAL_DISTANCE = distance;
        this.TOTAL_DURATION = duration;
        this.NUM_CARS = size;
        AVERAGE_SPEED = distance/duration;
        this.SIMULATION_ID = simulationID;
    }
}


