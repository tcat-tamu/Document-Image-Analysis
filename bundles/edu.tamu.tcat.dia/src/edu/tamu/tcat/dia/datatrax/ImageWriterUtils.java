package edu.tamu.tcat.dia.datatrax;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import javax.imageio.ImageIO;

import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.dia.binarization.BinaryImage;

/**
 * Utility methods that support adapters whose purpose is to write data to an image file. 
 */
public final class ImageWriterUtils
{
   // HACK: Should not throw TranformerConfigurationException
   
   /**
    * Ensures that the path to an output image file exists and can be written.
    * 
    * @param path The path that the image will be written to
    * @return The supplied path
    * @throws TransformerConfigurationException If the path is not suitable or cannot be made
    *       available for writing.
    */
   public static Path processOutputImagePath(Path path) throws TransformerConfigurationException
   {
      Path parent = path.getParent();
      if (!Files.exists(parent))
      {
         try
         {
            Files.createDirectories(parent);
         }
         catch (IOException e)
         {
            throw new TransformerConfigurationException("Invalid output path. Cannot create parent directory [" + parent + "]");
         }
      }
      
      if (!Files.isDirectory(parent) || !Files.isWritable(parent))
      {
         throw new TransformerConfigurationException("Output path parent [" + parent + "] must be a writeable diretory ");
      }
      
      if (Files.exists(path))
      {
         try
         {
            Files.delete(path);
         }
         catch (IOException e)
         {
            throw new TransformerConfigurationException("Invalid output path. Cannot delete existing file [" + path + "]");
         }
      }
         
      return path;
   }

   /**
    * 
    * @param format The candidate image format.
    * @return The supplied format if and only if it is a valid image output format.
    * @throws TransformerConfigurationException If {@link BufferedImage}s cannot be written 
    *       using the supplied format. 
    * 
    */
   public static String checkFormat(String format) throws TransformerConfigurationException
   {
      for (String fmt : ImageIO.getWriterFormatNames())
      {
         if (format.equalsIgnoreCase(fmt));
         {
            return format;
         }
      }
      
      throw new TransformerConfigurationException("Invalid image format [" + format + "]");
   }

   
   /**
    * Converts the supplied {@link BufferedImage} to a format that is appropriate to use as 
    * a model for an output image. In general, this means cropping the image to ensure that 
    * only minimal image data is stored in the configuration for a 
    * <p>
    * One of the easiest ways to construct an image is to use an existing image as the template
    * to determine the correct number bands, data packing strategies, color model, etc. This
    * method is useful to convert an existing image that will serve as a  
    * 
    * @param model
    * @return
    */
   private static BufferedImage prepareModel(BufferedImage model)
   {
      // HACK using a buffered image as a model is a hack for reconstructing a new buffered image. 
      //      should improve the toImage method on BinaryImage
      if (model.getWidth() > 2 || model.getHeight() > 2)
      {
         // TODO trim to minimal image
         return model.getSubimage(0, 0, 2, 2); // discard image data
      }
      
      return model;
   }
   
   public static BufferedImage restoreModel(byte[] image) throws IOException
   {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(image))
      {
         return ImageIO.read(bais);
      }
   }
   
   public static byte[] getModelData(BufferedImage image)
   {
      BufferedImage model = prepareModel(image);
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ImageIO.write(model, "png", baos);
         return baos.toByteArray();
      }
      catch (IOException e)
      {
         throw new IllegalStateException("Failed to serialize model image", e);
      }
   }
   
   public static void writeImage(BinaryImage image, Path path, String format, byte[] model) throws IOException
   {
      try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         BufferedImage im = BinaryImage.toBufferedImage(image);
         ImageIO.write(im, format, out);
         out.flush();
      }
   }
   public static void writeImage(BufferedImage image, Path path, String format) throws IOException
   {
      try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(image, format, out);
         out.flush();
      }
   }
}
