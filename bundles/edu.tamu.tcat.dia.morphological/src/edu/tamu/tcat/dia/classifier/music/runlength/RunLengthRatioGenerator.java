package edu.tamu.tcat.dia.classifier.music.runlength;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import edu.tamu.tcat.dia.binarization.BinaryImage;

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
   public static List<RunLengthStruct> findRunLengthRatios(BinaryImage input, int numberOfRuns)
   {
      Random randomGen = new Random();
      int numCols = input.getWidth();

      // for each run
      Set<Integer> previous = new HashSet<>();
      List<RunLengthStruct> results = new ArrayList<>();
      for (int i = 0; i < numberOfRuns; i++)
      {
         RunLengthStruct runLengths = new RunLengthStruct();
         runLengths.colIx = selectColumn(randomGen, numCols, previous);
         previous.add(Integer.valueOf(runLengths.colIx));
         
         runLengths.runs = findRuns(input, runLengths.colIx);
         runLengths.ratios = computeForegroundRatio(runLengths.runs);
         runLengths.mode = computeMode(runLengths.ratios);
         
         results.add(runLengths);
      }

      return results;
   }

   public static class RunLengthStruct 
   {
      int colIx;
      List<PixelRun> runs;
      List<Double> ratios;
      int mode;
   }
   
   private static List<Double> computeForegroundRatio(List<PixelRun> runs)
   {
      // compute the average ratio of background (white) to foreground pixels)
      int startIx = runs.get(0).foreground ? 1 : 0;
      List<Double> ratios = new ArrayList<>();
      for (int i = startIx; i < runs.size() - 1; i += 2)
      {
         double ratio = runs.get(i).ct / (double) runs.get(i + 1).ct;
         ratios.add(Double.valueOf(ratio));
      }
      
      return ratios;
   }
   
   private static int computeMode(List<Double> runLengthRatios) 
   {
      // TODO find the mode
      Map<Integer, AtomicInteger> accumulator = new HashMap<>();
      for (double v : runLengthRatios)
      {
         Integer ix = Integer.valueOf((int)Math.round(v));
         if (!accumulator.containsKey(ix))
            accumulator.put(ix, new AtomicInteger());
         
         accumulator.get(ix).incrementAndGet();
      }
      
      int mode = 0;
      int maxValue = Integer.MIN_VALUE;
      for (Integer ratio : accumulator.keySet())
      {
         int ct = accumulator.get(ratio).get();
         if (ct > maxValue)
         {
            maxValue = ct;
            mode = ratio.intValue();
         }
      };
      
      return mode;
      
//      // find the mode of ratios
//      EM estimator = new EM(ratios, 3);
//      estimator.
//      return estimator.estimate();
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
