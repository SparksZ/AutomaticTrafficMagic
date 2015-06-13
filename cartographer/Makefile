files = Car.java Coordinate.java Grid.java MapPanel.java Road.java RoadNotifier.java RoadMap.java SimpleRoad.java Simulator.java
bytecode = $(addsuffix .class,$(basename $(files)))

all: $(files)
	javac $(files)

safe: $(files)
	javac -Xlint:all $(files)

clean:
	rm $(bytecode)
