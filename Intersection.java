import java.util.Arrays;
import java.util.LinkedList;

public class Intersection {
	private LinkedList<Road> roads;
	private LinkedList<LinkedList<Car>> queues;
	private Lights lights;
	
	/**
	 * Following is the constructor and 3 overloads
	 */
	public Intersection(){
		roads = new LinkedList<Road>();
		queues = new LinkedList<LinkedList<Car>>();
		lights = new Lights();
	}
	
	public Intersection(Lights inLights){
		roads = new LinkedList<Road>();
		queues = new LinkedList<LinkedList<Car>>();
		lights = inLights;
	}

	public Intersection(Road ... inRoads){
		roads = new LinkedList<Road>(Arrays.asList(inRoads));
		queues = new LinkedList<LinkedList<Car>>();
		for(Road road : roads){
			queues.add(new LinkedList<Car>());
		}
		lights = new Lights();
	}

	public Intersection(Lights inLights, Road ... inRoads){
		roads = new LinkedList<Road>(Arrays.asList(inRoads));
		queues = new LinkedList<LinkedList<Car>>();
		for(Road road : roads){
			queues.add(new LinkedList<Car>());
		}
		lights = inLights;
	}
	
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
		return roads.contains(road);
	}
	
	/**
	 * Adds a road connection to the intersection.
	 * 
	 * @param road
	 * 			Road to add to intersection
	 */
	public void addRoad(Road road){
		roads.add(road);
		queues.add(new LinkedList<Cars>());
	}
	
	/**
	 * Adds a car to the end of the queue from
	 * a road.
	 * 
	 * @param car
	 * 			Car to add to the queue
	 * @param road
	 * 			Road the car is on
	 * @return	True if the road exists. False
	 * 			otherwise.
	 */
	public boolean addCarToQueue(Car car, Road road){
		int index = roads.indexOf(road);
		if(index == -1){
			return false;
		}
		
		queues.get(index).add(car);
		return true;
	}
	
	/**
	 * Moves a car from the front of a queue
	 * from a road.
	 * 
	 * @param road
	 * 			Road the car is on
	 * @return	The car that was moved. Null
	 * 			if the road doesn't exist or
	 * 			it has no cars in its queue.
	 */
	public Car moveCarFromQueue(Road road){
		int index = roads.indexOf(road);
		if(index == -1){
			return null;
		}
		
		return queues.get(index).pollFirst();
	}
}
