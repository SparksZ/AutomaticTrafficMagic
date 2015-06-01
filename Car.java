/**
 * The Car object for Automatic Traffic Magic's Simulation
 * @author Zack Sparks
 * @version 1.0
 */
public class Car {
    private final double cLength = 13.7;
    private double velocity, acceleration, position;
    private final int frameRate = 1; //seconds

    /**
     * Constructs a new Car object
     * @param position the initial position of the car (simply the number of feet along the road from the road's origin)
     * @param velocity the initial velocity of the car (ft/sec)
     * @param acceleration the initial acceleration of the car (ft/sec/sec)
     */
    public Car(double position, double velocity, double acceleration) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
    }

    /**
     * Updates the cars velocity and position based on the frameRate and acceleration/velocity (respectively)
     */
    public void update() {
        updateVelocity();
        updatePosition();
    }

    /**
     * Updates the Velocity from acceleration and frameRate
     */
    private void updateVelocity() {
        velocity = velocity + acceleration * frameRate;
    }

    /**
     * Updates the position from velocity and frameRate
     */
    private void updatePosition() {
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
}
