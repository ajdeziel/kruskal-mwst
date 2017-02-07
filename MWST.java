/* MWST.java
   Author: AJ Po-Deziel

   Template originally created by Bill Bird, 08/02/2014.
   
   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java MWST

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java MWST file.txt
   where file.txt is replaced by the name of the text file.

   The input consists of a series of graphs in the following format:

    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>

   Entry A[i][j] of the adjacency matrix gives the weight of the edge from
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that A[i][j]
   is always equal to A[j][i].

   An input file can contain an unlimited number of graphs; each will be
   processed separately.
*/

import java.util.PriorityQueue;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;


//Union-find class implementation inspired from Algorithms, 4th Ed.
//by Sedgewick & Wayne (pg. 228)
class WeightedUF {
	private int[] id;
	private int[] size;
	private int count;
	
	//Weighted Union Find constructor
	public WeightedUF(int n) {
		count = n;
		id = new int[n];

		for (int i = 0; i < n; i++) {
			id[i] = i;
		}

		size = new int[n];

		for (int i = 0; i < n; i++) {
			size[i] = 1;
		}
	}

	public int count() {
		return count;
	}

	//Are vertex p, q connected? 
	public boolean connected(int p, int q) {
		return find(p) == find(q);
	}

	public int find(int p) {
		while (p != id[p]) {
			p = id[p];
		}

		return p;
	}

	public void union(int p, int q) {
		int i = find(p);
		int j = find(q);

		if (i == j) {
			return;
		}
		
		if (size[i] < size[j]) {
			id[i] = j;
			size[j] += size[i];
		} else {
			id[j] = i;
			size[i] += size[j];
		}

		count--;
	}
}

//Weighted edge data type implementation inspired from Algorithms, 4th Ed.
//by Sedgewick & Wayne (pg. 610)
class Edge implements Comparable<Edge> {
	private final int v;
	private final int w;
	private final double weight;

	//Edge constructor
	public Edge(int v, int w, double weight) {
		this.v = v;
		this.w = w;
		this.weight = weight;
	}
	
	//Get weight of edge
	public double weight() {
		return weight;
	}

	public int either() {
		return v;
	}

	//Is there another vertex that knows v?
	public int other(int vertex) {
		if (vertex == v) {
			return w;
		} else if (vertex == w) {
			return v;
		} else {
			throw new RuntimeException("Inconsistent edge");
		}
	}

	//Compare edge weights
	public int compareTo(Edge that) {
		if (this.weight() < that.weight()) {
			return -1;
		} else if (this.weight() > that.weight()) {
			return 1;
		} else {
			return 0;
		}
	}
}//end class Edge


//Do not change the name of the MWST class
public class MWST{
	
	private static PriorityQueue<Edge> mst;
	
	/* mwst(G)
		Given an adjacency matrix for graph G, return the total weight
		of all edges in a minimum weight spanning tree.

		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/

	//MWST method implementation uses Kruskal's Algorithm, with code inspired
	//from Algorithms, 4th Ed. by Sedgewick and Wayne (pg. 627)
	static int MWST(int[][] G){
		int numVerts = G.length;
		mst = new PriorityQueue<Edge>();
		
		//Read adjacency matrix and pass to MST priority queue
		for (int i = 0; i < numVerts; i++) {
			for (int j = i; j < numVerts; j++) {
				if (G[i][j] != 0) {
					Edge e = new Edge(i, j, G[i][j]);
					mst.add(e);
				}//end if/else statement
			}//end for
		}//end for
		
		WeightedUF wUF = new WeightedUF(numVerts);
		int totalWeight = 0;
		
		//Pull and get total weight of MST by examining edges in Priority Queue
		while (mst.size() != 0) {
			Edge f = mst.poll(); //Remove from PQ and store min edge in f
			int v = f.either();
			int w = f.other(v);
			
			if (wUF.connected(v, w) == true) {
				continue;
			} else {
				wUF.union(v, w);
				mst.add(f);
				totalWeight += f.weight(); //Add min weight edge to totalWeight
			}
			
		}		

		return totalWeight;
	}


	/* main()
	   Contains code to test the MWST function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}

		int graphNum = 0;
		double totalTimeSeconds = 0;

		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();

			int totalWeight = MWST(G);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;

			System.out.printf("Graph %d: Total weight is %d\n",graphNum,totalWeight);
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}
