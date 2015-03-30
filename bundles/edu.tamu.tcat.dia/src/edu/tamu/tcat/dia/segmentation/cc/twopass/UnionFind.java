package edu.tamu.tcat.dia.segmentation.cc.twopass;

/**
 * A data structure to keep track of a set of elements partitioned into a number of disjoint
 * (nonoverlapping) subsets. This structure provides three distinct operations
 * 
 *   <ul>
 *     <li>{@link #find(int)}: Determine which sub-set a particular element is in. Find returns
 *         an item from this set that serves as its "representative"; by comparing the result of
 *         two {@code #find(int)} operations, one can determine whether two elements are in the 
 *         same subset.</li>
 *     <li>{@link #union(int, int)}: Joins two subsets into a single subset</li>
 *     <li>{@link #makeSet()}: Creates a new set containing a single element.</li>
 *   </ul>
 *   
 * <p>
 * With these three operations, many practical partitioning problems can be solved. For more 
 * information see {@link http://en.wikipedia.org/wiki/Disjoint-set_data_structure}.
 *     
 *
 */
public class UnionFind {
   // NOTE this data structure relies on creating labeled sub-sets with integer-valued labels 
   //      and no associated data. This simplicity supports performance and a minimal memory 
   //      footprint. For a more general-purpose implementation, we would need to provide a 
   //      more generic model of the contained items and sets and supply an interface with
   //      (possibly) multiple implementations
   
    private final int[] id;    // id[i] = parent of i
    private final int[] sz;    // sz[i] = number of objects in subtree rooted at i
    private int count = 0;     // number of components
    private int label = 0;

    // Create an empty union find data structure with N isolated sets.
    public UnionFind(int maxSize) {
        id = new int[maxSize];
        sz = new int[maxSize];
    }

    /**
     * @return The number of disjoint sets.
     */
    public int count() {
        return count;
    }

   /**
     * Create a new element within its own singleton set, and return the id for that element. 
     * Note that, initially, this the returned element id will also serve as the label for the 
     * set this element belongs to. That label will be updated as the created element is 
     * involved in {@link #union(int, int)} operations.  
     * 
     * @return The id for the newly created element.
     */
    public int makeSet()
    {
 	   label++;
 	   count++;
 	   id[label] = label;
 	   sz[label] = label;

 	   return label;
    }

    /**
     * 
     * @param p
     * @return
     */
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
    /**
     * Replace sets containing p and q with their union.
     * @param p
     * @param q
     */
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