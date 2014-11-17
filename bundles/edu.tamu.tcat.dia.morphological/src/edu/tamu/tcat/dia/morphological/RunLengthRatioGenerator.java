package edu.tamu.tcat.dia.morphological;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import edu.tamu.tcat.dia.binarization.BinaryImage;

public final class RunLengthRatioGenerator
{

   private BinaryImage input;
   private int numberOfRuns;
   private Map<Integer, ArrayList<Float>> outMap;
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.rlgenerator");

   public static <T> List<T> mode(List<? extends T> coll)
   {
      Map<T, Integer> seen = new HashMap<T, Integer>();
      int max = 0;
      List<T> maxElems = new ArrayList<T>();
      for (T value : coll)
      {
         if (seen.containsKey(value))
            seen.put(value, seen.get(value) + 1);
         else
            seen.put(value, 1);
         if (seen.get(value) > max)
         {
            max = seen.get(value);
            maxElems.clear();
            maxElems.add(value);
         }
         else if (seen.get(value) == max)
         {
            maxElems.add(value);
         }
      }

      for (T e : maxElems)
      {
         logger.info("Value " + e + " occurs " + seen.get(e) + " times");
      }
      return maxElems;
   }

   public RunLengthRatioGenerator(BinaryImage source, int numRuns)
   {
      this.input = source;
      this.numberOfRuns = numRuns;
      this.outMap = new HashMap<Integer, ArrayList<Float>>(numRuns);
   }

   public Map<Integer, ArrayList<Float>> run()
   {
      Random randomGen = new Random();
      int numCols = input.getWidth();
      int randColIndex;
      int runLength = 0;
      ArrayList<Object> colList = new ArrayList<Object>();
      ArrayList<Float> ratioList = new ArrayList<Float>();

      for (int i = 0; i < numberOfRuns; i++)
      {
         // randomly pick a column, count number of black and white pixels in
         // a row and sum numbers up
         // calculate pairwise white:black ratios

         colList = new ArrayList<Object>();
         randColIndex = randomGen.nextInt(numCols);
         if (outMap.size() > 0)
         {
            while (outMap.containsKey(randColIndex))
            {
               randColIndex = randomGen.nextInt(numCols);
            }
         }
         logger.finest("Processing col " + randColIndex);

         for (int j = 0; j < input.getHeight(); j++)
         {
            runLength = 1;
            if (input.isForeground(randColIndex, j))
            {
               colList.add('B');
               while ((j + 1 < input.getHeight())
                     && ((input.isForeground(randColIndex, j) && input
                           .isForeground(randColIndex, j + 1))))
               {
                  runLength++;
                  j++;
               }
               colList.add(runLength);

            }
            else if (!input.isForeground(randColIndex, j))
            {
               colList.add('W');
               while ((j + 1 < input.getHeight())
                     && ((!input.isForeground(randColIndex, j) && !input
                           .isForeground(randColIndex, j + 1))))
               {
                  runLength++;
                  j++;
               }
               colList.add(runLength);

            }
         }

         // compute ratios in colLists
         ratioList = new ArrayList<Float>();
         if ((char)colList.get(0) == 'W')
         {
            for (int k = 1; (k + 2) < colList.size(); k += 4)
            {
               ratioList.add((float)((int)colList.get(k))
                     / (int)colList.get(k + 2));
            }
         }
         else if ((char)colList.get(0) == 'B')
         {
            // ignore the first black entries, start with white
            for (int k = 3; (k + 2) < colList.size(); k += 4)
            {
               ratioList.add((float)((int)colList.get(k))
                     / (int)colList.get(k + 2));
            }
         }

         logger.finest("ratio list length: " + ratioList.size() + ", "
               + ratioList.toString());
         if (ratioList.isEmpty())
         {
            outMap.put(randColIndex, new ArrayList<Float>());
            logger.finest("Adding empty list to output at index " + i);
         }
         else
         {
            System.out.println("Adding list to output at index " + i);
            outMap.put(randColIndex, ratioList);
         }

      }

      for (Integer e : outMap.keySet())
      {
         logger.fine("Mode for key " + e + " is " + mode(outMap.get(e)));
      }
      return outMap;
   }

}
