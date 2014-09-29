package edu.tamu.tcat.dia.binarization.datatrax;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.datatrax.ImageWriterUtils;

/**
 * Pass through data transformer that writes {@link BinaryImage}s to a file while passing 
 * the input image unchanged to the provided sink. 
 */
public class BinaryImageWriter implements Transformer<BinaryImage, BinaryImage>
{
   public static final String EXTENSION_ID = "tcat.dia.images.adapters.binary.writer";

   private Path path;
   private String format = "jpg";
   private byte[] model;
   
   public BinaryImageWriter()
   {
      // TODO Auto-generated constructor stub
   }

   @Override
   public Class<BinaryImage> getSourceType()
   {
      return BinaryImage.class;
   }

   @Override
   public Class<BinaryImage> getOutputType()
   {
      return BinaryImage.class;
   }
   
   public void setPath(Path path) throws TransformerConfigurationException
   {
      this.path = ImageWriterUtils.processOutputImagePath(path);
   }
   
   public void setFormat(String format) throws TransformerConfigurationException
   {
      this.format = ImageWriterUtils.checkFormat(format);
   }

   public void setModel(BufferedImage model)
   {
      this.model = ImageWriterUtils.getModelData(model);
   }
   
   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      this.path = ImageWriterUtils.processOutputImagePath((Path)data.get("path"));
      this.format = ImageWriterUtils.checkFormat((String)data.get("format"));
      
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
   public Runnable create(Supplier<? extends BinaryImage> source, Consumer<? super BinaryImage> sink)
   {
      return new Runnable() {
         
         @Override
         public void run()
         {
            try 
            {
               BinaryImage image = source.get();
               ImageWriterUtils.writeImage(image, path, format, model);
               sink.accept(image);
            }
            catch (IOException ioe)
            {
               throw new IllegalStateException("", ioe);
            }
         }
      };
   }

}
