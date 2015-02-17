package edu.tamu.tcat.dia.segmentation.images.bloomberg;

import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;
import edu.tamu.tcat.dia.segmentation.cc.twopass.Finder;

/**
 * Generates a halftone mask image used to locat illustrations within a document image.
 * All connected components from a source binary image (a 16x1 sub-sampled version of the 
 * input image in the original Bloomberg algorithm) will be compared to a supplied seed 
 * image. All intersecting components will be passed on for subsequent processing.  
 */
public final class UnionOperator {

	/**
	 * Generates a halftone mask image used to locat illustrations within a document image.
	 * All connected components from a source binary image (a 16x1 sub-sampled version of the 
	 * input image in the original Bloomberg algorithm) will be compared to a supplied seed 
	 * image. All intersecting components will be passed on for subsequent processing.  
	 * 
	 * @param source The source image from which to generate the mask image.
	 * @param seed A seed image constructed to preserve portions of large elements of any 
	 *      illustrations in the input image (if any illustrations are present). 
	 * @return A preliminary halftone mask that (ideally) partially covers any illustration 
	 *      in the input image. This image will be further processed by subsequent steps in 
	 *      Bloomberg's algorithm.
	 */
	public static BinaryImage computeMask(BinaryImage source, BinaryImage seed)
   {
	   ConnectComponentSet sourceCCs = findComponents(source);
      ConnectComponentSet seedComponents = findComponents(seed);
      
      BooleanArrayBinaryImage result = new BooleanArrayBinaryImage(source.getWidth(), source.getHeight());
      if (seedComponents.listLabels().isEmpty())
         return result;    // this will be the case if the image has no illustrations.
      
      for (ConnectedComponent sourceCC : sourceCCs)
      {
         for (ConnectedComponent seedCC : seedComponents)    
         {
            if (seedCC.intersects(sourceCC))
               writeComponent(result, sourceCC);
         }
      }
      
      return result;
   }

   private static ConnectComponentSet findComponents(BinaryImage seed)
   {
      Finder ccFinder = new Finder(seed, 100_000);
      ConnectComponentSet seedComponents = ccFinder.call();
      return seedComponents;
   }

   private static void writeComponent(BooleanArrayBinaryImage result, ConnectedComponent cc)
   {
      int w = result.getWidth();
      for (Point p : cc.getPoints())
      {
         result.setForeground(p.getY() * w + p.getX());
      }
   }
}
