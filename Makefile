JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Rectangle.java \
	Block.java \
	Grid.java \
	GameBoard.java \
	#Panel.java \
	#Filler.java \
	#Player.java \
	#Playerable.java \
	#FillerClient.java \
	#FillerServer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
