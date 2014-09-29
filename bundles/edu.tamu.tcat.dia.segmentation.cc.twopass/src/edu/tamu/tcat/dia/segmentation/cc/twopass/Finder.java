package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.dia.binarization.BinaryImage;

public class Finder implements Runnable
{
   
   private final Supplier<? extends BinaryImage> input;
   private final Consumer<? super SimpleCCSet> sink;
   private final int maxLabels;
   
   private BinaryImage image;
   private int w;
   private int h;

   private int[] result;

   private UnionFind uf;


   public Finder(Supplier<? extends BinaryImage> input, Consumer<? super SimpleCCSet> sink, int maxLabels) {
      this.input = input;
      this.sink = sink;
      this.maxLabels = maxLabels;
            
   }
   
   public void run() {
      initialize();
      firstPass();
      secondPass();
      SimpleCCSet ccSet = createCCSet();
      
      sink.accept(ccSet);
   }

   private void initialize()
   {
      this.image = input.get();
      this.w = image.getWidth();
      this.h = image.getHeight();
   
      result = new int[w * h];
      uf = new UnionFind(maxLabels);
   }

   private void firstPass() {
      int yOffset = -w;
      for (int y = 0; y < h; ++y)
      {
         yOffset += w;		// first element of the current row.
         for (int x = 0; x < w; ++x)
         {
            int ix = yOffset + x;		// index for x,y
            if (!image.isForeground(ix))
               continue; 		// don't label background pixels

            int k = 0;
            int prexIx = (y - 1) * w + x;
            boolean connectedLeft = (x > 0 && image.isForeground(ix - 1));
            boolean connectedUp =  (y > 0 && image.isForeground(prexIx));

            // HACK seems like these first two if statements are overwritten by the third
            if (connectedLeft) 
               k = result[ix - 1];

            if (connectedUp && (!connectedLeft || result[prexIx] < k )) 
               k = result[prexIx];

            if (!connectedLeft && !connectedUp)
               k = uf.makeSet();

            if (k >= maxLabels)
               throw new IllegalStateException("maximum number of labels reached. increase MAX_LABELS and recompile.");

            result[ix] = k;
            // if connected, but with different label, then do union
            if (connectedLeft && result[ix - 1] != k)
               uf.union(k, result[ix - 1]);
            if (connectedUp && result[prexIx] != k)
               uf.union(k, result[prexIx]);
         }
      }
   }

   private void secondPass() {
      for (int i = 0; i < w * h; i++ ) 
      {
         if (image.isForeground(i)) 
            result[i] = uf.find(result[i]);
      }
   }

   private SimpleCCSet createCCSet()
   {
      int sequence = 0;
      int offset = 0;
      Map<Integer, SimpleCC> components = new HashMap<>();
      for (int r = 0; r < h; r++)
      {
         for (int c = 0; c < w; c++)
         {
            if (result[offset + c] == 0)
               continue;
   
            Integer ccLabel = Integer.valueOf(result[offset + c]);
            if (!components.containsKey(ccLabel))
            {
               SimpleCC cc = new SimpleCC();
               cc.setSequence(sequence++);
               components.put(ccLabel, cc);
            }
   
            components.get(ccLabel).add(c, r);
         }
   
         offset += w;
      }
   
      SimpleCCSet ccSet = new SimpleCCSet(w, h, components);
      return ccSet;
   }
}