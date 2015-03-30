package edu.tamu.tcat.dia.segmentation.images.bloomberg.datatrax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.ExpansionOperator;

public class ExpansionTransformer implements Transformer
{
   public final static String EXTENSION_ID = "tcat.dia.morphological.expansion"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";

   /** The number of times this reduction should be performed. */
   public static final String PARAM_ITERATIONS = "iterations";
   
   private int iterations = 1;
   
   public void setNumberOfIterations(int iterations) throws TransformerConfigurationException
   {
      if (iterations < 1)
         throw new TransformerConfigurationException("Invalid number of iterations. Must be at least 1.");
   
      this.iterations = iterations;
   }
   
   @Override
   public void configure(Map<String, Object> config) throws TransformerConfigurationException
   {
      if (config.containsKey(PARAM_ITERATIONS))
      {
         Integer value = Transformer.getValue(config, PARAM_ITERATIONS, Integer.class);
         setNumberOfIterations(value.intValue());
      }
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      Map<String, Object> config = new HashMap<>();
      config.put(PARAM_ITERATIONS, Integer.valueOf(iterations));

      return config;
   }


   @Override
   public Callable<BinaryImage> create(TransformerContext ctx)
   {
      final BinaryImage source = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
      
      return () -> {
         BinaryImage image = source;
         for (int i = 0; i < iterations; i++)
         {
            image = ExpansionOperator.expand(image);
         }
         
         return image;
      };
   }
}
