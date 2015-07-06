import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Intersection {
	private LinkedList<Car> queues;
    private ArrayList<Road> roads; /* 0 - 3 are in roads 4 - 7 are out. i % 4
                                      gives position of road wrt intersection.
                                      0 north, 1 east, 2 south, 3 west. null if
                                      road doesn't exist */
	
	/**
	 * Following is the constructor and 3 overloads
	 */
	public Intersection(ArrayList<Road> roadss){
        this.roads = roadss;
        queues = new LinkedList<>();
	}
	
//	public Intersection(Lights inLights){
//		roads = new LinkedList<Road>();
//		queues = new LinkedList<LinkedList<Car>>();
//		lights = inLights;
//	}

//	public Intersection(Road ... roads){
//		this.roads = roads;
//		queues = new LinkedList[roads.length];
//		for(LinkedList<Car> queue : queues){
//			queue = new LinkedList<Car>();
//		}
//	}

//	public Intersection(Lights inLights, Road ... roads){
//		this.roads = roads;
//		queues = new LinkedList[roads.length];
//		for(LinkedList<Car> queue : queues){
//			queue = new LinkedList<Car>();
//		}
//		lights = inLights;
//	}
	
	/**
	 * Override for equals method to see if two
	 * intersections are equal.
	 * 
	 * @param toCompareTo
	 * 				Intersection to compare to
	 * @return	True if the intersections are equal.
	 * 			False otherwise.
	 */
	public boolean equals(Object toCompareTo){
		Intersection comp = (Intersection) toCompareTo;
		for(Road road : roads){
			if(!comp.hasRoad(road)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the intersection is connected
	 * to a road.
	 * 
	 * @param road
	 * 			Road to check against
	 * @return	True if road and intersection are 
	 * 			connected. False otherwise.
	 */
	public boolean hasRoad(Road road){
		for(Road rue : roads){
			if(road.equals(rue)){
				return true;
			}
		}
		return false;
	}
	
//	/**
//	 * Adds a road connection to the intersection.
//	 *
//	 * @param road
//	 * 			Road to add to intersection
//	 */
//	public void addRoad(Road road){
//		Road[] newRoads = new Road[roads.length];
//		for(int i = 0; i < roads.length; i++){
//			newRoads[i] = roads[i];
//		}
//		newRoads[roads.length] = road;
//		roads = newRoads;
//	}
	
//	/**
//	 * Adds a car to the end of the queue from
//	 * a road.
//	 *
//	 * @param car
//	 * 			Car to add to the queue
//	 * @param road
//	 * 			Road the car is on
//	 * @return	True if the road exists. False
//	 * 			otherwise.
//	 */
//	public boolean addCarToQueue(Car car, Road road){
//		int index = -1;
//		for(int i = 0; i < roads.length && index == -1; i++){
//			if(roads[i].equals(road)){
//				index = i;
//			}
//		}
//		if(index == -1){
//			return false;
//		}
//
//		queues[index].add(car);
//		return true;
//	}
	
//	/**
//	 * Moves a car from the front of a queue
//	 * from a road.
//	 *
//	 * @param road
//	 * 			Road the car is on
//	 * @return	The car that was moved. Null
//	 * 			if the road doesn't exist or
//	 * 			it has no cars in its queue.
//	 */
//	public Car moveCarFromQueue(Road road){
//		int index = -1;
//		for(int i = 0; i < roads.length && index == -1; i++){
//			if(roads[i].equals(road)){
//				index = i;
//			}
//		}
//		if(index == -1){
//			return null;
//		}
//
//		return queues[index].pollFirst();
//	}
}
