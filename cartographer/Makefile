files = $(wildcard *.java)
bytecode = $(addsuffix .class,$(basename $(files)))

all: $(files)
	javac $(files)

safe: $(files)
	javac -Xlint:all $(files)

clean:
	rm $(bytecode)
