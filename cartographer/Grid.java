import java.util.LinkedList;
import java.util.ArrayList;

public class Grid {
    public final int numRows, numCols;
    // can't use normal arrays because of the implementation of generics in java
    // (I receive "error: generic array creation")
    // http://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java
    public ArrayList<ArrayList<LinkedList<Integer>>> roads;

    public Grid(int edgeLength) {
        this(edgeLength, edgeLength);
    }

    public Grid(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        roads = new ArrayList<ArrayList<LinkedList<Integer>>>(numRows);
        for(int row = 0; row < numRows; row++) {
            roads.add(new ArrayList<LinkedList<Integer>>(numCols));
            for(int col = 0; col < numCols; col++) {
                roads.get(row).add(new LinkedList<Integer>());
            }
        }
    }

    public boolean addRoadToGridLocation(int row, int col, int roadID) {
        for(Integer currID : roads.get(row).get(col)) {
            if(currID == roadID) {
                return false;
            }
        }
        roads.get(row).get(col).addLast(roadID);
        return true;
    }
}
