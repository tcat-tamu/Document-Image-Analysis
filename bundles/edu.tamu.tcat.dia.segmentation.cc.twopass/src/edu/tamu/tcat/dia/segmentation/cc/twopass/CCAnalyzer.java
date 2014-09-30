package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;

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
public class CCAnalyzer implements TransformerFactory<BinaryImage, ConnectComponentSet>
{
   
   public final static String EXTENSION_ID = "tcat.dia.segmentation.cc.twopass"; 

   final static int MAX_LABELS = 100_000;

   private int maxLables = 100_000;
   
   @Override
   public Class<BinaryImage> getSourceType()
   {
      return BinaryImage.class;
   }

   @Override
   public Class<ConnectComponentSet> getOutputType()
   {
      return ConnectComponentSet.class;
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // no-op
      
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      return new HashMap<String, Object>();
   }

   @Override
   public Runnable create(final Supplier<? extends BinaryImage> source, 
                          final Consumer<? super ConnectComponentSet> sink)
   {
      // TODO make it so that Finder doesn't depend on Transform API.
      return new Finder(source, sink, maxLables);
   }

  
}