import java.util.ArrayList;
import java.util.Collection;
import java.util.Observable;
import java.util.Random;
import java.awt.Dimension;

public class RoadMap extends Observable {
    private ArrayList<Road> roads;
    private int numRoads = 0;
    private Random prng;
    private Grid mapGrid;
    private Dimension mapSize;

    public static final int GRID_RESOLUTION = 10;

    /**
     * Make a new RoadMap!
     * @param mapSize the size of the map, as a Dimension. This is unitless and only affects computation time.
     */
    public RoadMap(Dimension mapSize) {
        mapGrid = new Grid(GRID_RESOLUTION);
        roads = new ArrayList<Road>();
        prng = new Random();
        this.mapSize = mapSize;
    }

    /**
     * For testing, allow random number generator to be given a particular seed.
     * @param mapSize the size of the map, as a Dimension. This is unitless and only affects computation time.
     * @param prngSeed a seed to pass to the random number generator
     */
    public RoadMap(Dimension mapSize, int prngSeed) {
        super(mapSize);
        prng = new Random(prngSeed);
    }

    /**
     * make a new road
     * TODO: add support for other road types
     * @param origin
     * @param direction
     * @return a new Road
     */
    public Road makeNewRoad(Coordinate origin, double direction) {
        // in final code, allow the type of road to be passed as a parameter
        int roadID = getNextRoadID();
        Road newRoad = new SimpleRoad(mapGrid, origin, direction, roadID);
        roads.add(newRoad);
        setChanged();
        notifyObservers(roads);
        return newRoad;
    }

    /**
     * @return a new road id
     */
    private int getNextRoadID() {
        return numRoads++;
    }

    /**
     * @param id the integer id describing a road
     * @return the road associated with the given ID
     */
    public Road getRoadByID(int id) {
        return roads.get(id);
    }

    /**
     * @return the collection of all roads
     */
    public Collection<Road> getAllRoads() {
        return roads;
    }

    /**
     * @return the grid used in this RoadMap to determine intersection information
     */
    public Grid getGrid() {
        return mapGrid;
    }
}
