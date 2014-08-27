package edu.tamu.tcat.dia.binarization.sauvola;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.InvalidTransformerConfiguration;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.analytics.image.binary.BinaryImage;
import edu.tamu.tcat.analytics.image.binary.IntegralImage;

public class FastSauvolaFactory implements TransformerFactory<IntegralImage, BinaryImage>
{
   private static final String PARAM_K = "k";
   private static final String PARAM_WINDOW_SIZE = "windowSize";
   
   private int windowSize = 0;
   private double k = 0.5;
   
   public FastSauvolaFactory()
   {
   }

   public void setWindowSize(int windowSize)
   {
      this.windowSize = windowSize;
   }
   
   public void setK(double k)
   {
      this.k = k;
   }
   
   @Override
   public void configure(Map<String, Object> config) throws InvalidTransformerConfiguration
   {
      if (hasValue(config, PARAM_K))
      {
         this.k = getValue(config, PARAM_K, Double.class).doubleValue();
      }
      
      if (config.containsKey(PARAM_WINDOW_SIZE))
      {
         this.windowSize = getValue(config, PARAM_WINDOW_SIZE, Integer.class).intValue();
      }
   }
   
   private boolean hasValue(Map<String, Object> config, String key)
   {
      return config.containsKey(key) && config.get(key) != null;
   }

   private <X> X getValue(Map<String, Object> config, String key, Class<X> type) throws InvalidTransformerConfiguration
   {
      if (!hasValue(config, key))
         throw new InvalidTransformerConfiguration("No value is defined for key [" + key + "]");
      
      try 
      {
         return type.cast(config.get(key));
      }
      catch (ClassCastException cce)
      {
         String template = "Invalid value [{0}] for key [{1}]. Expected instance of [{2}]";
         String msg = MessageFormat.format(template, config.get(key), key, type.getName());
         throw new InvalidTransformerConfiguration(msg, cce);
      }
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      Map<String, Object> config = new HashMap<>();
      config.put(PARAM_K, Double.valueOf(k));
      config.put(PARAM_WINDOW_SIZE, Integer.valueOf(windowSize));

      return config;
   }

   @Override
   public Runnable create(Supplier<IntegralImage> source, Consumer<BinaryImage> sink)
   {
      return new Runnable()
      {
         @Override
         public void run()
         {
            IntegralImage input = source.get();
            int window = (windowSize > 0) ? windowSize : input.getWidth() / 15;
               
            FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(input, window, k);
            BinaryImage result = binarizer.run();
            sink.accept(result);
         }
      };
   }

}
