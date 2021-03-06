import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

public class Simulator {
    public static void main(String[] args) {
        RoadMap myMap = new RoadMap(new Dimension(500,500));
        Grid theGrid = myMap.getGrid();
        JFrame simulatorWindow = new JFrame("Automatic Traffic Magic");
        MapPanel drawingPanel = new MapPanel(myMap);
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Map", drawingPanel);
        simulatorWindow.getContentPane().add(tabbedPane);
        myMap.makeNewRoad(new Coordinate(50, 50), -30);
        myMap.makeNewRoad(new Coordinate(30, 100), 10);

        drawingPanel.setMinimumSize(new Dimension(300,300));
        //simulatorWindow.setSize(500,500);
        simulatorWindow.pack();
        simulatorWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        simulatorWindow.setVisible(true);
        //simulatorWindow.setMinimumSize(new Dimension(300, 300));
        /*try {
            Thread.sleep(2000);
        } catch (java.lang.InterruptedException e) { }
        myMap.makeNewRoad(new Coordinate(25,10), 5);
        */
    }
}
