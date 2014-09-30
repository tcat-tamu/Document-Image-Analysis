package edu.tamu.tcat.analytics.image.integral.datatrax;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;

public class BufferedImageAdapter implements TransformerFactory<BufferedImage, IntegralImage>
{
   
   public final static String EXTENSION_ID = "tcat.dia.images.adapters.buffered.integral"; 

   public BufferedImageAdapter()
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
   
   public IntegralImage adapt(BufferedImage image)
   {
      Objects.requireNonNull(image, "Source image was null");
      
      Raster data = image.getData();
      if (data.getNumBands() > 1)
      {
         // TODO convert to grayscale.
      }
      
      return IntegralImageImpl.create(data);
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
            sink.accept(adapt(src));
         }
      };
   }

}
