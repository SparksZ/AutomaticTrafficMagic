import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.ArrayList;
import java.awt.RenderingHints;
import java.util.Observer;
import java.util.Observable;

public class MapPanel extends JPanel implements Observer {

    private Collection<Road> roads;
    private RoadMap theMap;

    public MapPanel(RoadMap aMap) {
        roads = new ArrayList<Road>();
        theMap = aMap;
        theMap.addObserver(this);
    }

    /**
     * The automatic option
     * Using the observer pattern prevents the need for polling for change
     * or copying the entire collection on every painting
     * @param o the observable notifier
     * @param newRoads the new collection of roads to paint
     */
    @SuppressWarnings("unchecked")
    public void update(Observable o, Object newRoads) {
        System.out.println("Updating list of roads...");
        updateRoadsCollection((Collection<Road>)newRoads);
    }

    /**
     * The manual option
     * @param newRoads the new collection of roads to paint
     */
    public void updateRoadsCollection(Collection<Road> newRoads) {
        roads = newRoads;
    }

    /**
     * This gets called automatically
     * @param g the graphics context
     */
    public void paint (Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(Road r: roads) {
            // TODO: recognize that the coordinate systems are not necessarily scaled the same
            g2.fillOval((int)r.getOrigin().getX() - 3, getSize().height - ((int)r.getOrigin().getY()) - 3, 7, 7);
        }
    }
}
