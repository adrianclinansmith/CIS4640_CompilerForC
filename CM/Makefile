JAVA=java
JAVAC=javac
JFLEX=jflex

### Linux setup

# CLASSPATH=-cp /usr/share/java/cup.jar:.
# CUP=cup

### Professor's mac setup

#JFLEX=~/Projects/jflex/bin/jflex
#CLASSPATH=-cp ~/Projects/java-cup-11b.jar:.
#CUP=$(JAVA) $(CLASSPATH) java_cup.Main

### My setup

CLASSPATH=-cp /Users/adrianclinansmith/Documents/University_of_Guelph/12_2022_Winter/CIS4640_Compilers/Assignments/CupFiles/java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

### Compile commands

all: tm CM.class

CM.class: absyn/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java CM.java

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: ScannerGenerator.flex
	$(JFLEX) ScannerGenerator.flex

parser.java: ParserGenerator.cup
	#$(CUP) -dump -expect 3 ParserGenerator.cup
	$(CUP) -expect 3 ParserGenerator.cup

### Generate code

tm0:
	java $(CLASSPATH) CM testfiles/0.cm -c code.tm
tm1:
	java $(CLASSPATH) CM testfiles/1.cm -c code.tm
tm2:
	java $(CLASSPATH) CM testfiles/2.cm -c code.tm
tm3:
	java $(CLASSPATH) CM testfiles/3.cm -c code.tm
tm4:
	java $(CLASSPATH) CM testfiles/4.cm -c code.tm
tm5:
	java $(CLASSPATH) CM testfiles/5.cm -c code.tm
tm6:
	java $(CLASSPATH) CM testfiles/6.cm -c code.tm
tm7:
	java $(CLASSPATH) CM testfiles/7.cm -c code.tm
tm8:
	java $(CLASSPATH) CM testfiles/8.cm -c code.tm
tm9:
	java $(CLASSPATH) CM testfiles/9.cm -c  code.tm

tmfac:
	java $(CLASSPATH) CM testfiles/fac.cm -c code.tm
tmgcd:
	java $(CLASSPATH) CM testfiles/gcd.cm -c code.tm
tmsort:
	java $(CLASSPATH) CM testfiles/sort.cm -c code.tm

### TM

CC = gcc

CFLAGS = 

tm: tm.c
	$(CC) $(CFLAGS) tm.c -o tm

### Run

run:
	./tm code.tm

### Cleanup

clean:
	rm -f tm code.tm parser.java Lexer.java sym.java *.class absyn/*.class *~
