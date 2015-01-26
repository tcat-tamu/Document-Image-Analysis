package edu.tamu.tcat.dia.morphological.test;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;

public class ThresholdReducerTests
{

   private BufferedImage image;
   private BinaryImage binImage;
   private BinaryImage binReducedImage;
   private Path dataDir;

   public ThresholdReducerTests()
   {
      // TODO Auto-generated constructor stub
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
   
   @SuppressWarnings("unused")
   @Before
   public void setup() throws IOException
   {
      dataDir = Paths.get("C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res");
      Path imagePath = dataDir.resolve("00000009.jp2");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      Iterator<ImageReader> imageReadersBySuffix = ImageIO.getImageReadersBySuffix("jp2");
      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      
   }

   @Test
   public void testThresholdReducer() throws IOException
   {

//      Path outputPath = dataDir.resolve("output/00000009-bin.png");
//      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
//      {
//         ImageIO.write(toImage((BinaryImage)binImage, image), "png", out);
//         out.flush();
//      }
//      
//      ThresholdReducer reducer = new ThresholdReducer(binImage, 4, 1);
//      binReducedImage = reducer.run();
//      
//      outputPath = dataDir.resolve("output/00000009-bin-reduced.png");
//      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
//      {
//         ImageIO.write(toImage((BinaryImage)binReducedImage, image), "png", out);
//         out.flush();
//      }
//      
//      assertTrue("", true);
   }
}
