public class Coordinate {
    public double x, y;
    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double distanceToCoordinate(Coordinate otherCoordinate) {
        return Math.sqrt(Math.pow(x - otherCoordinate.getX(), 2) + Math.pow(y - otherCoordinate.getY(), 2));
    }
}
