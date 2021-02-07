Akshay Narula (anarul3@uic.edu)

Input - <filename>.txt
javac cpath.java
java cpath <apsolute file name> <source> <destination> <budget>

Example: using file g2.txt

javac cpath.java
java cpath g2.txt 0 6 30

All paths in the given budget of $30
0 to 6 in 9 unit time costs $7
0 to 6 in 6 unit time costs $10
0 to 6 in 4 unit time costs $13
0 to 6 in 5 unit time costs $14
0 to 6 in 7 unit time costs $17
---------Shortest time in the given budget----------
0 to 6 in 4 unit time costs $13
Path: [0, 4, 5, 6]


Instructions:

1. Keeping the input file in the same folder will allow the program to run in without the apsolte path.
2. The assumption is that the input files will have an empty first line, similar to the provided inputs.
3. The numbering of the vertices starts from 0. That is, if the number of vertices is 7 then the vertices are 0,1,2,3,4,5,6
4. The arguments of the program should consider point 3 and for the path from the first to last node in the seven vertex scenario would be
	cpath <file> 0 6 <budget> as there are 7 vertices.
