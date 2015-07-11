import java.text.DecimalFormat;
import java.util.Random;

/**
 * The Car object for Automatic Traffic Magic's Simulation
 * @author Zack Sparks
 * @version 1.0
 */
public class Car implements Moveable {
    private double distanceTravelled;
    private double xPosition, yPosition, velocity, acceleration, deceleration,
            aggressiveFactor, desiredV, startTime, endTime;
    private Road road;
    private Moveable leadingCar;
    private final boolean aggressive;
    private boolean nS; // specifies if the car is traveling north/south or not
                        // (east/west)
    int direction; // -1 for north/west +1 for south/east
    private Random r = new Random(1);


    // CONSTANTS
    public static final double ACCEL = 1.25; // m/s^2 typical number
    public static final double cLength = 4; // meters
    public static final double timeHeadway = 1.5; // seconds between cars
                                                  // desired (might need to be
                                                  // lower for city driving)
    public static final double minimumGap = 2.0; // meters between cars at
                                                 // standstill

    /**
     * Constructs a new Car object
     * @param xPosition the initial x position of the car (simply the number of
     *                 meters along the road from the road's origin)
     * @param yPosition the initial y position of the car (simply the number of
     *                 meters along the road from the road's origin)
     * @param velocity the initial velocity of the car (m/sec)
     * @param road road spawned on
     * @param leadingCar car that is in front of this car
     * @param nS whether the car is going north/south or not (e/w)
     * @param direction whether the car is moving in positive direction (s/e)
     *                  or not (n/w)
     * @param startTime the time the car was instantiated
     */
    public Car(double xPosition, double yPosition, double velocity, Road road,
               Moveable leadingCar, boolean nS, int direction,
               double startTime) {
        this.distanceTravelled = 0;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.velocity = velocity;
        this.road = road;
        this.leadingCar = leadingCar;
        this.nS = nS;
        this.direction = direction;
        this.startTime = startTime;

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
     * Updates the cars velocity and position based on the frameRate and
     * acceleration/velocity (respectively)
     */
    public void update() {
//        if (leadingCarGap() > 2000) {
//            System.out.println("HERE!");
//        }

        updateVelocity();
        updatePosition();
    }

    /**
     * Updates the Velocity from acceleration and frameRate
     */
    private void updateVelocity() {
        velocity = velocity + dVdT() * Driver.frameRate;
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

        if (nS) {
            return Math.abs(leadingCar.getYPosition() - yPosition) - cLength ;
        } else {
            return Math.abs(leadingCar.getXPosition() - xPosition) - cLength ;
        }
    }

    /**
     * Updates the position from velocity and frameRate
     */
    private void updatePosition() {
        distanceTravelled += velocity * Driver.frameRate;

        if (nS) {
            yPosition = yPosition + velocity * Driver.frameRate * direction;
        } else {
            xPosition = xPosition + velocity * Driver.frameRate * direction;
        }

        if (leadingCarGap() < 2) {
            velocity = 0;
        }
    }

    @Override
    public void setXPosition(double position) {
        xPosition = position;
    }

    @Override
    public double getXPosition() {
        return xPosition;
    }

    @Override
    public void setYPosition(double position) {
        yPosition = position;
    }

    @Override
    public double getYPosition() {
        return yPosition;
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
    public Moveable getLeadingCar() {
        return leadingCar;
    }

    /**
     * Sets this car's leading car
     * @param leadingCar the new leading car
     */
    public void setLeadingCar(Moveable leadingCar) {
        this.leadingCar = leadingCar;
    }

    public double getLength() {
        return cLength;
    }

    public String toString() {
        DecimalFormat f = new DecimalFormat("#0.0");
        return "P: (" + f.format(xPosition) + ", " + f.format(yPosition) +
                ") V:" + f.format(velocity) +
                " gap:" + f.format(leadingCarGap());
    }

    /**
     * Sets the end time of the car when it leaves the map
     * @param endTime the end time of the car
     */
    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the lifetime of the car
     */
    public double getLifetime() {
        return endTime - startTime;
    }

    public void setRoad(Road r) {
        road = r;
    }

    /**
     * Checks if another car is equal to this car based on position
     * @param o object to compare equality to this
     * @return whether or not the passed object is equal to this object
     */
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Car)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        Car that = (Car) o;
        return (that.getXPosition() == this.xPosition) && (that.getYPosition()
                == this.yPosition);
    }
}
