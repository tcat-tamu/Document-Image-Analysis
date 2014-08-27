package edu.tamu.tcat.dia.segmentation.cc.twopass;

/***************************************************************************
 *  Compilation:  javac WeightedQuickUnionUF.java
 *  Execution:  java WeightedQuickUnionUF < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *
 *  Weighted quick-union (without path compression).
 *
 ****************************************************************************/

public class UnionFind {
    private final int[] id;    // id[i] = parent of i
    private final int[] sz;    // sz[i] = number of objects in subtree rooted at i
    private int count = 0;     // number of components
    private int label = 0;

    // Create an empty union find data structure with N isolated sets.
    public UnionFind(int maxSize) {
        id = new int[maxSize];
        sz = new int[maxSize];
    }

    int increment()
    {
 	   label++;
 	   count++;
 	   id[label] = label;
 	   sz[label] = label;

 	   return label;
    }

    // Return the number of disjoint sets.
    public int count() {
        return count;
    }

    // Return component identifier for component containing p
    public int find(int p) {
        while (p != id[p])
            p = id[p];
        return p;
    }

   // Are objects p and q in the same set?
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }


    // Replace sets containing p and q with their union.
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);
        if (i == j) return;

        // make smaller root point to larger one
        if   (sz[i] < sz[j]) {
     	   id[i] = j;
     	   sz[j] += sz[i];
 	   } else {
 		   id[j] = i;
 		   sz[i] += sz[j];
 	   }

        count--;
    }
}