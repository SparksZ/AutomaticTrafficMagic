public class TimedCoordinate extends Coordinate {

    private double time;

    public TimedCoordinate(double t, int x, int y) {
        super(x, y);
        time = t;
    }

    public TimedCoordinate(double t, Coordinate c) {
        super(c.getX(), c.getY());
        time = t;
    }

    public double getTime() {
        return time;
    }
}
