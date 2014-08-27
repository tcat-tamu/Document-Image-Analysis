package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.image.binary.BinaryImage;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;
import edu.tamu.tcat.visualpage.dataproc.basic.DataProcessingTaskFactory;

/**
 * Use row-by-row labeling algorithm to label connected components
 * The algorithm makes two passes over the image: one pass to record
 * equivalences and assign temporary labels and the second to replace each
 * temporary label by the label of its equivalence class.
 * 
 * [Reference]
 * Linda G. Shapiro, Computer Vision: Theory and Applications.  (3.4 Connected
 * Components Labeling) Rosenfeld and Pfaltz (1966)
 *
 * From https://www.cs.washington.edu/education/courses/576/02au/homework/hw3/ConnectComponent.java
 */
public class CCAnalyzerFactory implements DataProcessingTaskFactory<BinaryImage, ConnectedComponent>
{
   final static int MAX_LABELS = 100_000;

   @Override
   public Runnable create(Supplier<BinaryImage> input, Consumer<ConnectedComponent> out)
   {
      return new Finder(input, out);
   }

   private static class Finder implements Runnable
   {
      private final int w;
      private final int h;

      private int[] result;

      private final UnionFind uf;

      private Consumer<ConnectedComponent> sink;
      private BinaryImage image;

      public Finder(Supplier<BinaryImage> input, Consumer<ConnectedComponent> sink) {
         this.image = input.get();
         this.sink = sink;
         this.w = image.getWidth();
         this.h = image.getHeight();

         result = new int[w * h];
         uf = new UnionFind(MAX_LABELS);
      }

      public void run() {
         firstPass();
         secondPass();

         int sequence = 0;
         int offset = 0;
         Map<Integer, SimpleCC> components = new HashMap<>();
         for (int r = 0; r < h; r++)
         {
            for (int c = 0; c < w; c++)
            {
               int label = result[offset + c];
               if (label == 0)
                  continue;

               Integer l = Integer.valueOf(label);
               if (!components.containsKey(l))
               {
                  SimpleCC cc = new SimpleCC();
                  cc.setSequence(sequence++);
                  components.put(l, cc);
               }

               components.get(l).add(c, r);
            }

            offset += w;
         }

         for (ConnectedComponent cc : components.values())
         {
            sink.accept(cc);
         }
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

               if (connectedLeft) 
                  k = result[ix - 1];

               if (connectedUp && (!connectedLeft || result[prexIx] < k )) 
                  k = result[prexIx];

               if (connectedLeft || connectedUp)
                  k = uf.increment();

               if (k >= MAX_LABELS)
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
   }
}