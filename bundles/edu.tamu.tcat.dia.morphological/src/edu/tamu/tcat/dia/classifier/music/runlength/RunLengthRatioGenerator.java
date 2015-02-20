package edu.tamu.tcat.dia.classifier.music.runlength;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.classifier.music.runlength.EM.Cluster;

public final class RunLengthRatioGenerator
{
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.rlgenerator");

 
//
//   public RunLengthRatioGenerator()
//   {
//   }
   
   /**
    * Calculates 
    * @param input
    * @param numberOfRuns
    * @return
    */
   public static Map<Integer, Set<Cluster>> findRunLengthRatios(BinaryImage input, int numberOfRuns)
   {
      Map<Integer, Set<Cluster>> outMap = new HashMap<>(numberOfRuns);
      
      Random randomGen = new Random();
      int numCols = input.getWidth();

      // for each run
      for (int i = 0; i < numberOfRuns; i++)
      {
         int colIx = selectColumn(randomGen, numCols, outMap.keySet());

         List<PixelRun> runs = findRuns(input, colIx);
         Set<Cluster> clusteredRatios = computeForegroundRatio(runs);
         
         outMap.put(Integer.valueOf(colIx), clusteredRatios);
      }

      return outMap;
   }

   private static Set<Cluster> computeForegroundRatio(List<PixelRun> runs)
   {
      // compute the average ratio of background (white) to foreground pixels)
      
      List<Double> ratios = new ArrayList<>();
      if (runs.isEmpty())
         return Collections.emptySet();
      
      int startIx = runs.get(0).foreground ? 1 : 0;
      for (int i = startIx; i < runs.size() - 1; i += 2)
      {
         double ratio = runs.get(i).ct / (double) runs.get(i + 1).ct;
         ratios.add(Double.valueOf(ratio));
      }
      
      EM estimator = new EM(ratios, 3);
      return estimator.estimate();
   }

   private static List<PixelRun> findRuns(BinaryImage input, int colIx)
   {
      int h = input.getHeight();
      List<PixelRun> runs = new ArrayList<>();
      boolean previous = input.isForeground(colIx, 0);
      PixelRun run = new PixelRun(previous);
      run.ct++;
      for (int j = 1; j < h; j++)
      {
         boolean current = input.isForeground(colIx, j);
         if (previous != current) {
            runs.add(run);
            run = new PixelRun(current);
            previous = current;
         }
         
         run.ct++;
      }
      return runs;
   }
   
   private static int selectColumn(Random randomGen, int max, Set<Integer> previous)
   {
      int colIx = -1;
      do {
         colIx = randomGen.nextInt(max);
         
      } while (previous.contains(Integer.valueOf(colIx)));
         
      logger.finest("Processing col " + colIx);
      return colIx;
   }

   private static class PixelRun
   {
      /** true if this is a run of foreground pixels. */
      private final boolean foreground;
      
      /** the number of pixels in the run. */
      int ct = 0;
      
      public PixelRun(boolean foreground)
      {
         this.foreground = foreground;
      }
   }

}
