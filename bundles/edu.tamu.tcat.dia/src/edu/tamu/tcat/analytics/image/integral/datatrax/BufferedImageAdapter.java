package edu.tamu.tcat.analytics.image.integral.datatrax;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;

public class BufferedImageAdapter implements Transformer
{
   
   public final static String EXTENSION_ID = "tcat.dia.images.adapters.buffered.integral"; 
   public final static String IMAGE_PIN = "image"; 

   public BufferedImageAdapter()
   {
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
      
      // HACK prevent 'java.awt.color.CMMException: LCMS error 13: Couldn't link the profiles' exceptions
      //      See: http://stackoverflow.com/questions/26535842/multithreaded-jpeg-image-processing-in-java
      try {
         Class.forName("java.awt.color.ICC_ColorSpace");
         Class.forName("sun.java2d.cmm.lcms.LCMS");
      } catch (Exception ex) {
         throw new IllegalStateException("Cannot load color models");
      }
      
      Raster data = image.getData();
      if (data.getNumBands() > 1)
      {
         // convert to grayscale.
         ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
         ColorConvertOp op = new ColorConvertOp(colorSpace, null);
         image = op.filter(image, null);
      }
      
      return IntegralImageImpl.create(data);
   }

   @Override
   public Callable<IntegralImage> create(TransformerContext ctx)
   {
      return () -> {
    	  BufferedImage image = (BufferedImage)ctx.getValue("image");
    	  return adapt(image);
      };
   }
}
