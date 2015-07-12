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
    private static final int PADDING = 50; // not pixels, but proportional to pixels

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
            g2.setColor(Color.GRAY);
            Intersection i = intersections.get(index);
            g2.fillOval(panelX(i.getX()) - 4, panelY(i.getY()) - 4, 9, 9);
            List<Road> intersectionRoads = i.getRoads();
            g2.setColor(Color.GREEN);
            if(i.getSinkScenario() != 0 && i.getSinkScenario() != 6 && i.getSinkScenario() != 7) {
                // paint road to the west
                Intersection otherIntersection = intersections.get(index - 1);
                g2.drawLine(panelX(i.getX()), panelY(i.getY() - Intersection.roadWidth),
                        panelX(otherIntersection.getX()), panelY(otherIntersection.getY() - Intersection.roadWidth));
                g2.drawLine(panelX(i.getX()), panelY(i.getY() + Intersection.roadWidth),
                        panelX(otherIntersection.getX()), panelY(otherIntersection.getY() + Intersection.roadWidth));
            }
            if (i.getSinkScenario() != 0 && i.getSinkScenario() != 1 && i.getSinkScenario() != 2) {
                // paint road to the north
                Intersection otherIntersection = intersections.get(index - numIntersectionsPerSide);
                g2.drawLine(panelX(i.getX() - Intersection.roadWidth), panelY(i.getY()),
                        panelX(otherIntersection.getX() - Intersection.roadWidth), panelY(otherIntersection.getY()));
                g2.drawLine(panelX(i.getX() + Intersection.roadWidth), panelY(i.getY()),
                        panelX(otherIntersection.getX() + Intersection.roadWidth), panelY(otherIntersection.getY()));
            }
            for (int rNum = 0; rNum < 4; rNum++) {
                for (Moveable c : intersectionRoads.get(rNum).getCars()) {
                    //stuff;
                }
            }
        }

    }

    private int panelX(double mapX) {
        scalingFactorX = (double)getWidth()/(2*PADDING + sideLength);
        return (int)((mapX - 1000 + PADDING)*scalingFactorX);
    }

    private int panelY(double mapY) {
        scalingFactorY = (double)getHeight()/(2*PADDING + sideLength);
        return (int)((mapY - 1000 + PADDING)*scalingFactorY);
    }
}
