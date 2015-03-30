package edu.tamu.tcat.dia.segmentation.cc;

import java.util.Collection;
import java.util.Set;

/**
 * Represents a set of connected components extracted from a common source such as a page
 * image.
 */
public interface ConnectComponentSet extends Iterable<ConnectedComponent>
{

   /**
    * @return The labels used to identify the components in the set.
    * @see #get(int)
    */
   Collection<Integer> listLabels();

   /**
    * @param label The labels whose associated component should be retrieved.
    * @return The identified connected component
    */
   ConnectedComponent get(int label);
   
   /**
    * @return The components as a {@link Set}.
    */
   Set<ConnectedComponent> asSet();
   
   /**
    * @return The width of the source image this connected component set is rendered on.
    */
   int getWidth();
   
   /**
    * @return The height of the source image this connected component set is rendered on.
    */
   int getHeight();

}
