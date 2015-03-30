package edu.tamu.tcat.dia.binarization.datatrax;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.ImageWriterUtils;

/**
 * Pass through data transformer that writes {@link BinaryImage}s to a file while passing 
 * the input image unchanged to the provided sink. 
 */
@Deprecated
public class BinaryImageWriter implements Transformer
{
   private static final String IMAGE_PIN = "image";

   public static final String EXTENSION_ID = "tcat.dia.images.adapters.binary.writer";

   private Path path;
   private String format = "jpg";
   private byte[] model;
   
   public BinaryImageWriter()
   {
      // TODO Auto-generated constructor stub
   }

   public void setPath(Path path) throws TransformerConfigurationException
   {
      try
      {
         this.path = ImageWriterUtils.processOutputImagePath(path);
      }
      catch (IOException e)
      {
         throw new TransformerConfigurationException(e);
      }
   }
   
   public void setFormat(String format) throws TransformerConfigurationException
   {
      try
      {
         this.format = ImageWriterUtils.checkFormat(format);
      }
      catch (IOException e)
      {
         throw new TransformerConfigurationException(e);
      }
   }

   public void setModel(BufferedImage model)
   {
      this.model = ImageWriterUtils.getModelData(model);
   }
   
   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      try
      {
         this.path = ImageWriterUtils.processOutputImagePath((Path)data.get("path"));
         this.format = ImageWriterUtils.checkFormat((String)data.get("format"));
      }
      catch (IOException e)
      {
         throw new TransformerConfigurationException(e);
      }
      
      Object model = data.get("model");
      if (model instanceof BufferedImage)
         this.model = ImageWriterUtils.getModelData((BufferedImage)model);
      else if (model instanceof byte[])
         this.model = (byte[])model;
      else
         throw new TransformerConfigurationException("Invalid type for model data. Expected BufferedImage or byte[] but found " + model.getClass());
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> config = new HashMap<String, Object>();
      config.put("path", path);
      config.put("format", format);
      config.put("model", model);
      
      return config;
   }

   @Override
   public Callable<?> create(TransformerContext ctx)
   {
      final BinaryImage image = (BinaryImage)ctx.getValue(IMAGE_PIN);
      return new Callable<BinaryImage>()
      {
         @Override
         public BinaryImage call() throws Exception
         {
            ImageWriterUtils.writeImage(image, path, format, model);
            return image;
         }
      };
   }
}
