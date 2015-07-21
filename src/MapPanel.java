import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.RenderingHints;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapPanel extends JPanel {
    private int sideLength;
    private double scalingFactorX, scalingFactorY;
    private List<Intersection> intersections;
    private Simulation sim;

    // constants
    private static final int INTERSECTION_OVAL_DIAM = 5;
    private static final int PADDING = 20; // not pixels, but proportional to pixels
    private static final int vizRoadWidth = 16;
    private static final Color greenLightColor = new Color(0,150,0),
            redLightColor = new Color(150, 0, 0),
            streetColor = Color.GRAY,
            roadCarColor = Color.BLACK,
            intersectionCarColor = new Color(200, 200, 255);

    public MapPanel(Simulation sim, int preferred_panel_length, int sideLength) {
        this.sideLength = sideLength;
        this.intersections = sim.getIntersections();
        this.sim = sim;
        setPreferredSize(new java.awt.Dimension(preferred_panel_length, preferred_panel_length));
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        scalingFactorX = (double)getWidth()/(2*PADDING + sideLength);
        scalingFactorY = (double)getHeight()/(2*PADDING + sideLength);
        for (int index = 0; index < intersections.size(); index++) {
            Intersection i = intersections.get(index);
            // paint intersections, with corresponding light colors
            g2.setColor(redLightColor);
            g2.fillRect(panelX(i.getX() - 75), panelY(i.getY() - vizRoadWidth),
                    (int)(150*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
            g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - 75),
                    (int)(vizRoadWidth*2*scalingFactorX), (int)(150*scalingFactorY));
            g2.setColor(Color.GRAY);
            g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - vizRoadWidth),
                    (int)(vizRoadWidth*2*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
            byte currLightState = i.getGreenDirection();
            g2.setColor(greenLightColor);
            if (currLightState == Intersection.NORTH) {
                g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() - 75),
                        (int)(vizRoadWidth*2*scalingFactorX), (int)((75 - vizRoadWidth)*scalingFactorY));
            }
            else if (currLightState == Intersection.EAST) {
                g2.fillRect(panelX(i.getX() + vizRoadWidth), panelY(i.getY() - vizRoadWidth),
                        (int)((75 - vizRoadWidth)*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
            }
            else if (currLightState == Intersection.SOUTH) {
                g2.fillRect(panelX(i.getX() - vizRoadWidth), panelY(i.getY() + vizRoadWidth),
                        (int)(vizRoadWidth*2*scalingFactorX), (int)((75 - vizRoadWidth)*scalingFactorY));
            }
            else if (currLightState == Intersection.WEST) {
                g2.fillRect(panelX(i.getX() - 75), panelY(i.getY() - vizRoadWidth),
                        (int)((75 - vizRoadWidth)*scalingFactorX), (int)(vizRoadWidth*2*scalingFactorY));
            }


            List<Road> interRoads = i.getRoads();
            g2.setColor(streetColor);
            Road currRoad;
            // southbound roads are on the left, eastbound roads are on top
            // north-south inbound top
            currRoad = interRoads.get(0);
            g2.fillRect(panelX(currRoad.getXWestPos() - vizRoadWidth), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south outbound bottom
            currRoad = interRoads.get(6);
            g2.fillRect(panelX(currRoad.getXWestPos() - vizRoadWidth), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south outbound top
            currRoad = interRoads.get(4);
            g2.fillRect(panelX(currRoad.getXWestPos() + vizRoadWidth/2), panelY(currRoad.getYNorthPos()),
                    (int)(vizRoadWidth/2*scalingFactorX), (int)((currRoad.getYSouthPos() - currRoad.getYNorthPos())*scalingFactorY));
            // north-south inbound bottom
            currRoad = interRoads.get(2);
            g2.fillRect(panelX(currRoad.getXWestPos() + vizRoadWidth/2), panelY(currRoad.getYNorthPos()),
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

            // paint sinks
            g2.setColor(Color.BLACK);
            for (Road r : interRoads) {
                if (r.getEndRoad()) {
                    if (r.getNS()) {
                        if (r.getPF()) {
                            g2.fillOval(panelX(r.roadEndX + vizRoadWidth/4), panelY(r.roadEndY - vizRoadWidth/2),
                                    (int)(vizRoadWidth*scalingFactorX), (int)(vizRoadWidth*scalingFactorY));
                        }
                        else {
                            g2.fillOval(panelX(r.roadEndX - 5*vizRoadWidth/4), panelY(r.roadEndY - vizRoadWidth/2),
                                    (int)(vizRoadWidth*scalingFactorX), (int)(vizRoadWidth*scalingFactorY));
                        }
                    }
                    else {
                        if (r.getPF()) {
                            g2.fillOval(panelX(r.roadEndX - vizRoadWidth/2), panelY(r.roadEndY - 5*vizRoadWidth/4),
                                    (int)(vizRoadWidth*scalingFactorX), (int)(vizRoadWidth*scalingFactorY));
                        }
                        else {
                            g2.fillOval(panelX(r.roadEndX - vizRoadWidth/2), panelY(r.roadEndY + vizRoadWidth/4),
                                    (int)(vizRoadWidth*scalingFactorX), (int)(vizRoadWidth*scalingFactorY));
                        }
                    }
                }
            }

            // paint cars on roads
            g2.setColor(roadCarColor);
            for (Road r : interRoads) {
                for (Moveable m : r.getCars()) {
                    Car c = (Car)m;
                    if (c.getNS()) {
                        if (c.getDirection() > 0) {
                            g2.fillOval(panelX(c.getXPosition() - (3 * vizRoadWidth / 4)) - 1, panelY(c.getYPosition()) - 1, 3, 3);
                        }
                        else {
                            g2.fillOval(panelX(c.getXPosition() + (3 * vizRoadWidth / 4)) - 1, panelY(c.getYPosition()) - 1, 3, 3);
                        }
                    }
                    else {
                        if (c.getDirection() > 0) {
                            g2.fillOval(panelX(c.getXPosition()) - 1, panelY(c.getYPosition() + (3 * vizRoadWidth / 4)) - 1, 3, 3);
                        }
                        else {
                            g2.fillOval(panelX(c.getXPosition()) - 1, panelY(c.getYPosition() - (3 * vizRoadWidth / 4)) - 1, 3, 3);
                        }
                    }
                }
            }

            // paint cars in intersection queues
            g2.setColor(intersectionCarColor);
            CopyOnWriteArrayList<CopyOnWriteArrayList<Moveable>> interQueues = i.getQueues();
            for (CopyOnWriteArrayList<Moveable> queue : interQueues) {
                for (Moveable m : queue) {
                    if (!(m instanceof Car)) {
                        continue;
                    }
                    Car c = (Car)m;
                    if (c.getNS()) {
                        if (c.getDirection() > 0) {
                            g2.fillOval(panelX(c.getXPosition() - (3 * vizRoadWidth / 4)) - 1, panelY(c.getYPosition()) - 1, 3, 3);
                        }
                        else {
                            g2.fillOval(panelX(c.getXPosition() + (3 * vizRoadWidth / 4)) - 1, panelY(c.getYPosition()) - 1, 3, 3);
                        }
                    }
                    else {
                        if (c.getDirection() > 0) {
                            g2.fillOval(panelX(c.getXPosition()) - 1, panelY(c.getYPosition() + (3 * vizRoadWidth / 4)) - 1, 3, 3);
                        }
                        else {
                            g2.fillOval(panelX(c.getXPosition()) - 1, panelY(c.getYPosition() - (3 * vizRoadWidth / 4)) - 1, 3, 3);
                        }
                    }
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
                Intersection otherIntersection = intersections.get(index - sim.getNumIntersectionsPerSide());
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
