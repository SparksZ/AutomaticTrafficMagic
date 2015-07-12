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

    public MapPanel(int sideLength, List<Intersection> intersections) {
        this.sideLength = sideLength;
        this.intersections = intersections;
        setPreferredSize(new Dimension(panelSideLength, panelSideLength));
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        scalingFactorX = (double)getWidth()/sideLength;
        scalingFactorY = (double)getHeight()/sideLength;
        System.out.println("Scaling factor: " + scalingFactorX);
        for (Intersection i : intersections) {
            System.out.println("Painting intersection dot at (" + ((int)(i.getX()*scalingFactorX) - 2) + ", " + ((int)(i.getY()*scalingFactorY) - 2) + ").");
            g2.fillOval((int)(i.getX()*scalingFactorX) - 2, (int)(i.getY()*scalingFactorY) - 2, 5, 5);
        }

    }
}
