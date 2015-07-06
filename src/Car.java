import java.text.DecimalFormat;
import java.util.Random;

/**
 * The Car object for Automatic Traffic Magic's Simulation
 * @author Zack Sparks
 * @version 1.0
 */
public class Car {
    private double distanceTravelled;
    private double position, velocity, acceleration, deceleration,
            aggressiveFactor, desiredV;
    private Road road;
    private Car leadingCar;
    private final boolean aggressive;
    private Random r = new Random();


    // CONSTANTS
    public static final double ACCEL = 1.25; // m/s^2 typical number
    public static final double cLength = 4; // meters
    public static final double frameRate = 0.5; // seconds
    public static final double timeHeadway = 1.5; // seconds between cars desired (might need to be lower for city driving)
    public static final double minimumGap = 2.0; // meters between cars at standstill

    /**
     * Constructs a new Car object
     * @param position the initial position of the car (simply the number of
     *                 meters along the road from the road's origin)
     * @param velocity the initial velocity of the car (m/sec)
     * @param road road spawned on
     * @param leadingCar car that is in front of this car
     */
    public Car(double position, double velocity, Road road, Car leadingCar) {
        this.distanceTravelled = 0;
        this.position = position;
        this.velocity = velocity;
        this.road = road;
        this.leadingCar = leadingCar;

        if (r.nextDouble() < .75) {
            aggressive = false;
            aggressiveFactor = 1.0;

        } else {
            aggressive = true;
            aggressiveFactor = 1.15;
        }

        acceleration = aggressiveFactor * ACCEL;
        deceleration = aggressiveFactor * ACCEL;
        desiredV = aggressiveFactor * road.getSpeedLimit();
    }

    /**
     * Updates the cars velocity and position based on the frameRate and acceleration/velocity (respectively)
     */
    public void update() {
        updateVelocity();
        updatePosition();

        if (position > road.getRoadLength()) {
            position = position % road.getRoadLength();
        }
    }

    /**
     * Updates the Velocity from acceleration and frameRate
     */
    private void updateVelocity() {
        velocity = velocity + dVdT() * frameRate;
    }

    /**
     * IDM dv/dt equation
     * @return the acceleration of this car based on the car in front of it
     */
    private double dVdT() {
        return acceleration * (1 - Math.pow((velocity / desiredV), 4) -
                (sStar() / leadingCarGap()));
    }

    /**
     * @return Desired dynamical distance
     */
    private double sStar() {
        return minimumGap + Math.max(0, velocity * timeHeadway + (velocity *
                approachV()) / (2 * Math.sqrt(acceleration * deceleration)));
    }

    /**
     * @return approach velocity of this car and the leading car
     */
    private double approachV() {
        return velocity - leadingCar.getVelocity();
    }

    public double leadingCarGap() {

        if (leadingCar.getPosition() < position) {
            double result = (4000 - (position - leadingCar.getPosition() - cLength));

            if (result < 0) {
                result = 0;
            }
            return result;
        }
        return Math.abs(leadingCar.getPosition() - cLength - position);
    }

    /**
     * Updates the position from velocity and frameRate
     */
    private void updatePosition() {
        distanceTravelled += velocity * frameRate;
        position = position + velocity * frameRate;
    }

    /**
     * @return the position (ft) of the car
     */
    public double getPosition() {
        return position;
    }

    /**
     * @return the velocity (ft/sec) of the car
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @return the acceleration (ft/sec/sec) of the car
     */
    public double getAcceleration() {
        return acceleration;
    }

    /**
     * @return the total distance travelled by this car
     */
    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    /**
     * Sets the position of the car
     * @param position the position (ft) of the car along the road it is on
     */
    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * Sets the velocity of the car
     * @param velocity the velocity (ft/sec) of the car
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    /**
     * Sets the acceleration of the car
     * @param acceleration the acceleration (ft/sec/sec) of the car
     */
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * @return this car's leading car
     */
    public Car getLeadingCar() {
        return leadingCar;
    }

    /**
     * Sets this car's leading car
     * @param leadingCar the new leading car
     */
    public void setLeadingCar(Car leadingCar) {
        this.leadingCar = leadingCar;
    }

    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.0");
        return "P:" + f.format(position) + " V:" + f.format(velocity) + " travelled:" + f.format(distanceTravelled) + " A:" + aggressive + " gap:" + f.format(leadingCarGap());
    }
}
