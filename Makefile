JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Rectangle.java \
	Block.java \
	GameBoard.java \
	Panel.java \
	NEWFillerClient.java \
	NEWFillerServer.java
	#Filler.java \
	#Player.java \
	#Playerable.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
