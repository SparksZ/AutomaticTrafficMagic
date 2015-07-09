
import java.util.ArrayList;

/**
 *
 * @author Tyler Durkota
 */
public class RoadSegment {
    private final int roadID;
    private ArrayList<TimedCoordinate> waypoints;
    
    public RoadSegment(int roadID, ArrayList<TimedCoordinate> waypoints) {
        this.roadID = roadID;
        this.waypoints = waypoints;
    }
    
    public ArrayList<TimedCoordinate> getWaypoints() {
        return waypoints;
    }
    
    public void setWaypoints(ArrayList<TimedCoordinate> newWaypoints) {
        this.waypoints = newWaypoints;
    }
    
    public int getRoadID() {
        return roadID;
    }
}
