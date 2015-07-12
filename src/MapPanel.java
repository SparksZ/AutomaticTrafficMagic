import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.util.List;

public class MapPanel extends JPanel {
    private int sideLength;
    private int panelSideLength = 500;
    private double scalingFactorX, scalingFactorY;
    private List<Intersection> intersections;
    private static final int INTERSECTION_OVAL_DIAM = 5;
    private static int numIntersectionsPerSide;

    public MapPanel(int sideLength, List<Intersection> intersections) {
        numIntersectionsPerSide = (int)Math.round(Math.sqrt(intersections.size()));
        this.sideLength = sideLength;
        this.numIntersectionsPerSide = numIntersectionsPerSide;
        this.intersections = intersections;
        setPreferredSize(new Dimension(panelSideLength, panelSideLength));
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        scalingFactorX = (double)getWidth()/sideLength;
        scalingFactorY = (double)getHeight()/sideLength;
        for (int index = 0; index < intersections.size(); index++) {
            g2.setColor(Color.BLACK);
            Intersection i = intersections.get(index);
            g2.fillOval((int)(i.getX()*scalingFactorX) - 4, (int)(i.getY()*scalingFactorY) - 4, 9, 9);
            List<Road> intersectionRoads = i.getRoads();
            g2.setColor(Color.GREEN);
            if(i.getSinkScenario() != 0 && i.getSinkScenario() != 6 && i.getSinkScenario() != 7) {
                // paint road to the west
                Intersection otherIntersection = intersections.get(index - 1);
                g2.drawLine((int)(i.getX()*scalingFactorX), (int)(i.getY()*scalingFactorY), (int)(otherIntersection.getX()*scalingFactorX), (int)(otherIntersection.getY()*scalingFactorY));
            }
            if (i.getSinkScenario() != 0 && i.getSinkScenario() != 1 && i.getSinkScenario() != 2) {
                // paint road to the north
                Intersection otherIntersection = intersections.get(index - numIntersectionsPerSide);
                g2.drawLine((int)(i.getX()*scalingFactorX), (int)(i.getY()*scalingFactorY), (int)(otherIntersection.getX()*scalingFactorX), (int)(otherIntersection.getY()*scalingFactorY));
            }
        }

    }
}
