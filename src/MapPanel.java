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
    private static final int PADDING = 20; // not pixels, but proportional to pixels
    private static final int vizRoadWidth = 10;

    public MapPanel(int sideLength, List<Intersection> intersections) {
        numIntersectionsPerSide = (int)Math.round(Math.sqrt(intersections.size()));
        this.sideLength = sideLength;
        this.numIntersectionsPerSide = numIntersectionsPerSide;
        this.intersections = intersections;
        setPreferredSize(new Dimension(panelSideLength, panelSideLength));
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        scalingFactorX = (double)getWidth()/(2*PADDING + sideLength);
        scalingFactorY = (double)getHeight()/(2*PADDING + sideLength);
        for (int index = 0; index < intersections.size(); index++) {
            //g2.setColor(Color.GRAY);
            Intersection i = intersections.get(index);
            boolean currLightState = i.getState();
            // east-west
            if (currLightState) {
                g2.setColor(Color.RED);
                g2.fillRect(panelX(i.getX() - 75), panelY(i.getY() - vizRoadWidth),
                        (int)(150*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
                g2.setColor(Color.GREEN);
                g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - 75),
                        (int)(vizRoadWidth*2*scalingFactorX), (int)(150*scalingFactorY));
            }
            else {
                g2.setColor(Color.RED);
                g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - 75),
                        (int)(vizRoadWidth*2*scalingFactorX), (int)(150*scalingFactorY));
                g2.setColor(Color.GREEN);
                g2.fillRect(panelX(i.getX() - 75), panelY(i.getY() - vizRoadWidth),
                        (int)(150*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
            }

            // north-south
            if (currLightState) {
                g2.setColor(Color.GREEN);
            }
            else {
                g2.setColor(Color.RED);
            }
            g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - 75),
                    (int)(vizRoadWidth*2*scalingFactorX), (int)(150*scalingFactorY));
            List<Road> interRoads = i.getRoads();
            g2.setColor(Color.GRAY);
            Road currRoad;
            // north-south inbound top
            currRoad = interRoads.get(0);
            g2.fillRect(panelX(currRoad.getXWestPos() + vizRoadWidth/2), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south outbound bottom
            currRoad = interRoads.get(6);
            g2.fillRect(panelX(currRoad.getXWestPos() + vizRoadWidth/2), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south outbound top
            currRoad = interRoads.get(4);
            g2.fillRect(panelX(currRoad.getXWestPos() - vizRoadWidth), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south inbound bottom
            currRoad = interRoads.get(2);
            g2.fillRect(panelX(currRoad.getXWestPos() - vizRoadWidth), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // east-west inbound left
            currRoad = interRoads.get(3);
            g2.fillRect(panelX(currRoad.getXWestPos()), panelY(currRoad.getYNorthPos() - vizRoadWidth),
                        (int)((currRoad.getXEastPos() - currRoad.getXWestPos())*scalingFactorX), (int)(vizRoadWidth/2*scalingFactorY));
            // east-west outbound right
            currRoad = interRoads.get(5);
            g2.fillRect(panelX(currRoad.getXWestPos()), panelY(currRoad.getYNorthPos() - vizRoadWidth),
                        (int)((currRoad.getXEastPos() - currRoad.getXWestPos())*scalingFactorX), (int)(vizRoadWidth/2*scalingFactorY));
            // east-west outbound left
            currRoad = interRoads.get(7);
            g2.fillRect(panelX(currRoad.getXWestPos()), panelY(currRoad.getYNorthPos() + vizRoadWidth/2),
                        (int)((currRoad.getXEastPos() - currRoad.getXWestPos())*scalingFactorX), (int)(vizRoadWidth/2*scalingFactorY));
            // east-west inbound right
            currRoad = interRoads.get(1);
            g2.fillRect(panelX(currRoad.getXWestPos()), panelY(currRoad.getYNorthPos() + vizRoadWidth/2),
                        (int)((currRoad.getXEastPos() - currRoad.getXWestPos())*scalingFactorX), (int)(vizRoadWidth/2*scalingFactorY));


            for (int rNum = 0; rNum < interRoads.size(); rNum++) { // iterate over incoming roads
                for (Moveable c : interRoads.get(rNum).getCars()) {
                    //stuff;
                }
            }
            g2.setColor(Color.BLACK);
            if(i.getSinkScenario() != 0 && i.getSinkScenario() != 6 && i.getSinkScenario() != 7) {
                // paint roads to the west
                Intersection otherIntersection = intersections.get(index - 1);
                g2.drawLine(panelX(i.getX() - 75), panelY(i.getY() - vizRoadWidth/2),
                        panelX(otherIntersection.getX() + 75), panelY(otherIntersection.getY() - vizRoadWidth/2));
                g2.drawLine(panelX(i.getX() - 75), panelY(i.getY() + vizRoadWidth/2),
                        panelX(otherIntersection.getX() + 75), panelY(otherIntersection.getY() + vizRoadWidth/2));
            }
            if (i.getSinkScenario() != 0 && i.getSinkScenario() != 1 && i.getSinkScenario() != 2) {
                // paint roads to the north
                Intersection otherIntersection = intersections.get(index - numIntersectionsPerSide);
                g2.drawLine(panelX(i.getX() - vizRoadWidth/2), panelY(i.getY() - 75),
                        panelX(otherIntersection.getX() - vizRoadWidth/2), panelY(otherIntersection.getY() + 75));
                g2.drawLine(panelX(i.getX() + vizRoadWidth/2), panelY(i.getY() - 75),
                        panelX(otherIntersection.getX() + vizRoadWidth/2), panelY(otherIntersection.getY() + 75));
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
