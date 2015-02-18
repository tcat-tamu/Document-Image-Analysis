package edu.tamu.tcat.dia.morphological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import edu.tamu.tcat.dia.binarization.BinaryImage;

public final class RunLengthRatioGenerator
{
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.rlgenerator");

//   public static <T> List<T> mode(List<? extends T> coll)
//   {
//      Map<T, Integer> seen = new HashMap<T, Integer>();
//      int max = 0;
//      List<T> maxElems = new ArrayList<T>();
//      for (T value : coll)
//      {
//         if (seen.containsKey(value))
//            seen.put(value, seen.get(value) + 1);
//         else
//            seen.put(value, 1);
//         if (seen.get(value) > max)
//         {
//            max = seen.get(value);
//            maxElems.clear();
//            maxElems.add(value);
//         }
//         else if (seen.get(value) == max)
//         {
//            maxElems.add(value);
//         }
//      }
//
//      for (T e : maxElems)
//      {
//         logger.info("Value " + e + " occurs " + seen.get(e) + " times");
//      }
//      return maxElems;
//   }
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
   public static Map<Integer, Double> findRunLengthRatios(BinaryImage input, int numberOfRuns)
   {
      Map<Integer, Double> outMap = new HashMap<>(numberOfRuns);
      
      Random randomGen = new Random();
      int numCols = input.getWidth();

      // for each run
      for (int i = 0; i < numberOfRuns; i++)
      {
         int colIx = selectColumn(randomGen, numCols, outMap.keySet());

         List<PixelRun> runs = findRuns(input, colIx);
         double foregroundRatio = computeForegroundRatio(runs);
         
         outMap.put(Integer.valueOf(colIx), Double.valueOf(foregroundRatio));
      }

      return outMap;
   }

   private static double computeForegroundRatio(List<PixelRun> runs)
   {
      // compute the average ratio of background (white) to foreground pixels)
      
      List<Double> ratios = new ArrayList<>();
      int startIx = runs.get(0).foreground ? 1 : 0;
      for (int i = startIx; i < runs.size() - 1; i += 2)
      {
         double ratio = runs.get(i).ct / (double) runs.get(i + 1).ct;
         ratios.add(Double.valueOf(ratio));
      }
      
      // TODO for now, just use the average, eventually should through out abnormal values 
      //      and/or find groupings of related values
      double sum =  ratios.parallelStream()
                     .reduce(Double::sum)
                     .orElse(Double.valueOf(0)); 
      return sum / ratios.size();
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
