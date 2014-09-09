package edu.tamu.tcat.dia.binarization.sauvola.test;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import edu.tamu.tcat.analytics.datatrax.InvalidTransformerConfiguration;
import edu.tamu.tcat.analytics.image.binary.BinaryImage;
import edu.tamu.tcat.analytics.image.binary.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaFactory;


public class TestSauvola
{
   // TODO test configuration serialization
   // TODO 
   
   public TestSauvola()
   {
      
   }

   public static BufferedImage toImage(BinaryImage im, BufferedImage model)
   {
      int offset = 0;
      int width = im.getWidth();
      int height = im.getHeight();

      ColorModel colorModel = model.getColorModel();
      WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
      int numBands = raster.getNumBands();
      for (int r = 0; r < height; r++)
      {
         for (int c = 0; c < width; c++)
         {
            int value = im.isForeground(offset + c) ? 0 : 255;
            for (int b = 0; b < numBands; b++)
               raster.setSample(c, r, b, value);
         }

         offset += width;
      }

      return new BufferedImage(colorModel, raster, true, new Hashtable<>());
   }
   
   @Test
   public void thresholdTestImage() throws IOException
   {
      Path dataDir = Paths.get("C:\\dev\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res");
      
      FastSauvolaFactory thresholder = new FastSauvolaFactory();
      thresholder.setK(0.3);
      Path imagePath = dataDir.resolve("shipbuilding-treatise.jpg");
      Path outputPath = dataDir.resolve("output/shipbuilding-treatise.png");
      
      final BufferedImage image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));

      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      Runnable runnable = thresholder.create(() -> { return iImage; }, (im) -> {
         try
         {
            try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
            {
               ImageIO.write(toImage(im, image), "png", out);
               out.flush();
            }
         }
         catch (Exception e)
         {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }); 
      
      runnable.run();
   }
   
   private void checkConfig(FastSauvolaFactory thresholder, double k, int window)
   {
      assertEquals("Values for K do not match", k, thresholder.getK(), 0.00001);
      assertEquals("Values for window size do not match", window, thresholder.getWindowSize());
   }
   
   private void checkConfig(FastSauvolaFactory expected, FastSauvolaFactory actual)
   {
      assertEquals("Values for K do not match", expected.getK(), actual.getK(), 0.00001);
      assertEquals("Values for window size do not match", expected.getWindowSize(), actual.getWindowSize());
   }
   
   @Test
   public void testConfiguration() throws InvalidTransformerConfiguration
   {
      FastSauvolaFactory thresholder = new FastSauvolaFactory();
      // supply valid values
      double k = 0.3;
      int window = 6;
      thresholder.setK(k);
      thresholder.setWindowSize(window);
      
      checkConfig(thresholder, k, window);
      
      // TODO supply invalid values
      
      // TODO test configuration
      FastSauvolaFactory thresholder2 = new FastSauvolaFactory();
      thresholder2.configure(thresholder.getConfiguration());
   
      checkConfig(thresholder, thresholder2);
   }
   
   public void testConfigurationSerialization() throws InvalidTransformerConfiguration
   {
      throw new UnsupportedOperationException();
   }
}
