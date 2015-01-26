package edu.tamu.tcat.dia.segmentation.cc;

import java.util.Set;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;

/**
 * Represents as set of foreground pixels that are adjacent to each other. Identification of 
 * connected components is a common low-level step in image analysis in a variety of domains. 
 * Connected components may, for example, be thought of as approximating glyphs in a document 
 * image, bacteria cultures in an image of a Pitri dish, or spots in a biological image scan 
 * data. 
 *  
 * 
 * <p>See {@linkplain http://en.wikipedia.org/wiki/Connected-component_labeling} for an 
 * overview of connected component analysis. 
 *
 */
public interface ConnectedComponent
{
   /**
    * @return The bounding box of this connected component.
    */
   BoundingBox getBounds();
   
   /**
    * @return The set of points that participate in this connected component. Note that 
    *       bounding boxes of distinct connected components may overlap so the bounding box
    *       is not in itself a reliable discriminator for participation in the component. 
    */
   Set<Point> getPoints();

   /**
    * @return The number of pixels contained within this connected component.
    */
   int getNumberOfPixels();

   /**
    * @return The sequence identifier for this component. This is determined by the application 
    *       that extracted the component. This is used as a label for the component and may or
    *       may not refer to some natural ordering of components such as reading order of 
    *       glyphs on a page. 
    */
   int getSequence();
   
   boolean intersects(ConnectedComponent cc);

}
