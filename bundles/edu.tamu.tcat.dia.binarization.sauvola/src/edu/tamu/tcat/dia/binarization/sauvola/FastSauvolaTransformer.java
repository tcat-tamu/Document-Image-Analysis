package edu.tamu.tcat.dia.binarization.sauvola;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.dia.binarization.BinaryImage;

/**
 * Performs binarization of an source image using Sauvola's method. This implementation based an
 * optimized variant of the original algorithm that relies on integral images.
 * 
 * <p>
 * TODO add citations for relevant papers
 */
public class FastSauvolaTransformer implements TransformerFactory<IntegralImage, BinaryImage>
{
   public static final String EXTENSION_ID = "tcat.dia.binarizers.fastsauvola";
   
   private static final String PARAM_K = "k";
   private static final String PARAM_WINDOW_SIZE = "windowSize";
   
   private int windowSize = 0;
   private double k = 0.5;
   
   public FastSauvolaTransformer()
   {
   }

   public void setWindowSize(int windowSize)
   {
      this.windowSize = windowSize;
   }
   
   public int getWindowSize()
   {
      return windowSize;
      
   }
   
   public double getK()
   {
      return k;
   }

   public void setK(double k)
   {
      this.k = k;
   }
   
   private boolean hasValue(Map<String, Object> config, String key)
   {
      return config.containsKey(key) && config.get(key) != null;
   }

   private <X> X getValue(Map<String, Object> config, String key, Class<X> type) throws TransformerConfigurationException
   {
      if (!hasValue(config, key))
         throw new TransformerConfigurationException("No value is defined for key [" + key + "]");
      
      try 
      {
         return type.cast(config.get(key));
      }
      catch (ClassCastException cce)
      {
         String template = "Invalid value [{0}] for key [{1}]. Expected instance of [{2}]";
         String msg = MessageFormat.format(template, config.get(key), key, type.getName());
         throw new TransformerConfigurationException(msg, cce);
      }
   }

   @Override
   public Class<IntegralImage> getSourceType()
   {
      return IntegralImage.class;
   }
   
   @Override
   public Class<BinaryImage> getOutputType()
   {
      return BinaryImage.class;
   }
   
   @Override
   public void configure(Map<String, Object> config) throws TransformerConfigurationException
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
   
   @Override
   public Map<String, Object> getConfiguration()
   {
      Map<String, Object> config = new HashMap<>();
      config.put(PARAM_K, Double.valueOf(k));
      config.put(PARAM_WINDOW_SIZE, Integer.valueOf(windowSize));

      return config;
   }

   @Override
   public Runnable create(Supplier<? extends IntegralImage> source, Consumer<? super BinaryImage> sink)
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
