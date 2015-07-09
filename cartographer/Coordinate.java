public class Coordinate {
    public int x, y;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public double distanceToCoordinate(Coordinate otherCoordinate) {
        return Math.sqrt((double)Math.pow(x - otherCoordinate.getX(), 2) + (double)Math.pow(y - otherCoordinate.getY(), 2));
    }
}
