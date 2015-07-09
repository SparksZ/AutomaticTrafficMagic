import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

public class Simulator {
    public static void main(String[] args) {
        Dimension mapSize = new Dimension(500,500);
        RoadMap myMap = new RoadMap(mapSize);
        Grid theGrid = myMap.getGrid();
        JFrame simulatorWindow = new JFrame("Automatic Traffic Magic");
        MapPanel drawingPanel = new MapPanel(myMap);
        GridPanel gridPanel = new GridPanel(myMap.getGrid());
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Map", drawingPanel);
        tabbedPane.addTab("Grid", gridPanel);
        simulatorWindow.getContentPane().add(tabbedPane);
        Road road1 = myMap.makeNewRoad(new Coordinate(20, 101), -5);
        //Road road1 = myMap.makeNewRoad(new Coordinate(50, 50), -30);
        Road road2 = myMap.makeNewRoad(new Coordinate(30, 200), 10);
        Road road3 = myMap.makeNewRoad(new Coordinate(270, 400), -80);
        Road road4 = myMap.makeNewRoad(new Coordinate(400, 300), -120);
        myMap.makeNewRoad(new Coordinate(150, 350), -90);
        myMap.makeNewRoad(new Coordinate(150, 350), 0);



        drawingPanel.setPreferredSize(mapSize);
        gridPanel.setPreferredSize(mapSize);
        //simulatorWindow.setPreferredSize(mapSize);
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
