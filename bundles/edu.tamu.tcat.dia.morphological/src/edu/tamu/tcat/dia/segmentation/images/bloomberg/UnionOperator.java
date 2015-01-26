package edu.tamu.tcat.dia.segmentation.images.bloomberg;

import java.util.HashSet;
import java.util.Set;

import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;
import edu.tamu.tcat.dia.segmentation.cc.twopass.Finder;

public final class UnionOperator {

	public static BinaryImage findOverlap(BinaryImage a, BinaryImage b)
	{
	   throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param initialReduction
	 * @param intermediate
	 * @return
	 */
	public static BinaryImage computeIntersection(BinaryImage initialReduction, BinaryImage intermediate)
   {
      Finder ccFinder = new Finder(intermediate, 1000);
      ConnectComponentSet seedComponents = ccFinder.call();
      ccFinder = new Finder(initialReduction, 10000);
      ConnectComponentSet sourceCCs = ccFinder.call();
      
//      System.out.println(seedComponents.listLabels().size());
//      System.out.println(sourceCCs.listLabels().size());
      Set<ConnectedComponent> union = new HashSet<>();
//      int ct = 0;
      for (ConnectedComponent sourceCC : sourceCCs)
      {
         if (sourceCC.getNumberOfPixels() < 5)
            continue;
//         ct++;
         for (ConnectedComponent seedCC : seedComponents)    
         {
            if (seedCC.intersects(sourceCC))
               union.add(sourceCC);
         }
      }
      
      int w = initialReduction.getWidth();
      int h = initialReduction.getHeight();
      BooleanArrayBinaryImage result = new BooleanArrayBinaryImage(w, h);
      for (ConnectedComponent cc : union)
      {
         for (Point p : cc.getPoints())
         {
            result.setForeground(p.getY() * w + p.getX());
         }
      }
      
      return result;

   }
}
