import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Zack on 7/10/2015.
 */
public class Visualization extends Application {

    private static CopyOnWriteArrayList<Intersection> intersections;
    public static double timeElapsed;
    private static CopyOnWriteArrayList<Double> results;
    private static double simulationTime;
    public static CopyOnWriteArrayList<Car> cars;

    // CONSTANTS
    public static final double frameRate = .5; // seconds

    private Group root;
    private Scene scene;

    // will represent car
    public static Pane carLayer;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new Group();
        carLayer = new Pane();

        primaryStage.setTitle("Traffic Simulator!");
        primaryStage.setResizable(false);

        scene = new Scene(root, 1000, 1000);

        root.getChildren().add(carLayer);

        primaryStage.setScene(scene);
        primaryStage.show();

        loadgame();

        AnimationTimer simLoop = new AnimationTimer() {

            @Override
            public void handle(long now) {
                intersections.forEach(Intersection::update);
                timeElapsed += frameRate;

                cars.forEach(Car::updateUI);
            }
        };
        simLoop.start();
    }

    private void loadgame() {
        intersections = new CopyOnWriteArrayList<>();

        createIntersections(2);
        connectIntersections();
        simulationTime = 7200;
        cars = new CopyOnWriteArrayList<>();

        timeElapsed = 0;
    }

    public static void connectIntersections() {
        int y = (int) Math.sqrt(intersections.size());

        for (int i = 0; i < y; i++) { // Rows of intersections
            for (int j = 0; j < y; j++) { // Columns of intersections
                Intersection inter = intersections.get(i * y + j); // Gets relevant intersection
                int sinkScenario = inter.getSinkScenario();



                /* the starting intersection in the second lines of each if
                   block is the one that doesn't own the road to be connected.
                   Currently the intersections are created owning the roads to
                   the north and west of them. Proper implementation would be
                   that the intersections are created owning roads that go out.
                                            O__O
                 */

                // Connects all North out roads that need North Intersection
                if (Arrays.asList(7, -1, 3, 6, 5, 4).contains(sinkScenario)) {
                    Intersection remote = intersections.get((i - 1) * y + j);
                    remote.setCarContainer(2, inter.getRoad(4));
                }

                // Connects all East Out roads that need East Intersection
                if (Arrays.asList(0, 1, 7 , -1, 6 , 5).contains(sinkScenario)) {
                    Intersection remote = intersections.get(i * y + j + 1);
                    inter.setRoad(5, remote.getRoad(3));
                    //remote.setCarContainer(3, inter.getRoad(5));
                }

                // Connect all South Out roads that need South Intersection
                if (Arrays.asList(0, 1, 2, 7, -1, 3).contains(sinkScenario)) {
                    Intersection remote = intersections.get((i + 1) * y + j);
                    inter.setRoad(6, remote.getRoad(0));
                    //inter.setCarContainer(6, remote.getRoad(0));
                }

                // Connects all West Out roads that need West Intersection
                if (Arrays.asList(1, 2, -1, 3, 5, 4).contains(sinkScenario)) {
                    Intersection remote = intersections.get(i * y + j - 1);
                    remote.setCarContainer(1, inter.getRoad(7));
                }
            }
        }
    }

    public static void createIntersections(int y) {
        double totalLength = Intersection.length + Intersection.roadLength;

        for (int i = 0; i < y; i++) { // Rows of intersections
            for (int j = 0; j < y; j++) { // Columns of intersections
                int sinkScenario = getSinkScenario(y, i, j);

                Intersection inter = new Intersection(1000 + totalLength *
                        (j + 1), 1000 + totalLength * (i + 1), sinkScenario);

                intersections.add(inter);
            }
        }
    }

    public static double getTimeElapsed() {
        return timeElapsed;
    }

    private static int getSinkScenario(int y, int i, int j) {
        int sinkScenario;
                /* **********************************
                 *             Top Row              *
                 ********************************** */
        if (i == 0) {

            // Determine sinkScenario
            if (j % y == 0) {
                sinkScenario = 0;
            } else if (j % y == y - 1) {
                sinkScenario = 2;
            } else {
                sinkScenario = 1;
            }

                /* **********************************
                 *             Bottom Row           *
                 ********************************** */
        } else if (i == y - 1) {

            // Determine sinkScenario
            if (j % y == 0) {
                sinkScenario = 6;
            } else if (j % y == y - 1) {
                sinkScenario = 4;
            } else {
                sinkScenario = 5;
            }

                /* **********************************
                 *            Middle Rows           *
                 ********************************** */
        } else {

            //Determine sinkScenarios
            if (j % y == 0) {
                sinkScenario = 7;
            } else if (j % y == y - 1) {
                sinkScenario = 3;
            } else {
                sinkScenario = -1;
            }
        }

        return sinkScenario;
    }

    private static double averageSpeed() {
        CopyOnWriteArrayList<Double> result = new CopyOnWriteArrayList<>();
        results = result;
        for (int i = 0; i < 3; i++) {
            result.add(0.0);
        }

        for (Intersection inter : intersections) {
            CopyOnWriteArrayList<Double> data = inter.getSinkData();

            for (int i = 0; i < 3; i++) {
                result.set(i, result.get(i) + data.get(i));
            }
        }

        return result.get(0) / result.get(1);
    }
}
