package edu.tamu.tcat.dia.segmentation.images.bloomberg.datatrax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.ThresholdReducer;

public class ThresholdReducerTransformer implements Transformer
{

   public static final String BINARY_IMAGE_PIN = "binary";
   
   public static final String EXTENSION_ID = "tcat.dia.morphological.thresholdreducer";
   
   /** The number of times this reduction should be performed. */
   public static final String PARAM_T = "t";
   
   /** The number of times this reduction should be performed. */
   public static final String PARAM_ITERATIONS = "iterations";

   private int t = 1;
   private int iterations = 1;

   public ThresholdReducerTransformer()
   {
   }

   public void setThreshold(int t)
   {
      this.t = t;
   }
   
   public void setNumberOfIterations(int iterations) throws TransformerConfigurationException
   {
      if (iterations < 1)
         throw new TransformerConfigurationException("Invalid number of iterations. Must be at least 1.");
   
      this.iterations = iterations;
   }
   
   @Override
   public void configure(Map<String, Object> config) throws TransformerConfigurationException
   {
      if (config.containsKey(PARAM_T))
      {
         Integer value = Transformer.getValue(config, PARAM_T, Integer.class);
         setThreshold(value.intValue());
      }
      
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
      config.put(PARAM_T, Integer.valueOf(t));
      config.put(PARAM_ITERATIONS, Integer.valueOf(iterations));

      return config;
   }

   @Override
   public Callable<?> create(TransformerContext ctx)
   {
      final BinaryImage source = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
      return () -> {
         BinaryImage output = source;
         for (int i = 0; i < iterations; i++) 
         {
            output = ThresholdReducer.threshold(output, t);
         }
         
         return output;
      };
   }

}
