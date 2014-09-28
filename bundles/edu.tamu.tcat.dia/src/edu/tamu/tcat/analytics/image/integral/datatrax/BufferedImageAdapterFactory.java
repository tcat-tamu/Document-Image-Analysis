package edu.tamu.tcat.analytics.image.integral.datatrax;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;

public class BufferedImageAdapterFactory implements Transformer<BufferedImage, IntegralImage>
{

   public BufferedImageAdapterFactory()
   {
   }

   @Override
   public Class<BufferedImage> getSourceType()
   {
      return BufferedImage.class;
   }

   @Override
   public Class<IntegralImage> getOutputType()
   {
      return IntegralImage.class;
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // no-op
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      return new HashMap<>();
   }

   @Override
   public Runnable create(Supplier<? extends BufferedImage> source, Consumer<? super IntegralImage> sink)
   {
      return new Runnable()
      {
         
         @Override
         public void run()
         {
            BufferedImage src = source.get();
            Objects.requireNonNull(src, "Source image was null");
            
            if (src.getData().getNumBands() > 1)
            {
               // TODO convert to grayscale.
            }
            
            
            sink.accept(IntegralImageImpl.create(src.getData()));
         }
      };
   }

}
