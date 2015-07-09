
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class GridPanel extends JPanel {

    private Grid theGrid;
    private Dimension mapSize;

    public GridPanel(Grid g) {
        this.theGrid = g;
        this.mapSize = theGrid.getMapSize();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (int row : theGrid.getRowEnds()) {
            g2.drawLine(0, row, mapSize.width, row);
        }
        for (int col : theGrid.getColEnds()) {
            g2.drawLine(col, 0, col, mapSize.height);
        }
        for (int gridRow = 0; gridRow < theGrid.numRows; gridRow++) {
            for (int gridCol = 0; gridCol < theGrid.numCols; gridCol++) {
                GridSquare currSquare = theGrid.getSquares()[gridRow][gridCol];
                for (RoadSegment s : currSquare.getSegments()) {
                    ArrayList<TimedCoordinate> waypoints = s.getWaypoints();
                    g2.fillOval(waypoints.get(0).x-2, mapSize.height - waypoints.get(0).y-2, 5, 5);
                    for (int coordinateIndex = 0; coordinateIndex < waypoints.size() - 1; coordinateIndex++) {
                        g2.setColor(Color.red);
                        g2.fillOval(waypoints.get(coordinateIndex + 1).x-2, mapSize.height - waypoints.get(coordinateIndex + 1).y-2, 5, 5);
                        g2.setColor(Color.black);
                        g2.drawLine(waypoints.get(coordinateIndex).x, mapSize.height - waypoints.get(coordinateIndex).y,
                                waypoints.get(coordinateIndex + 1).x, mapSize.height - waypoints.get(coordinateIndex + 1).y);
                    }
                }
            }
        }
    }
}
