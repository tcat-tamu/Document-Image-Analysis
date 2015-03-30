package edu.tamu.tcat.dia.classifier.music.runlength;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.classifier.music.runlength.RunLengthRatioGenerator.RunLengthStruct;

public class RunLengthRatioTransformer implements Transformer
{
   public static final String EXTENSION_ID = "tcat.dia.music.runlength"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";

   /** The number of randomly selected columns for which to calculate run lengths */
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
   public Callable<List<RunLengthStruct>> create(TransformerContext ctx)
   {
      return () -> {
         BinaryImage input = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
         List<RunLengthStruct> runLengthRatios = RunLengthRatioGenerator.findRunLengthRatios(input, iterations);
         return runLengthRatios;
      };
   }
}
