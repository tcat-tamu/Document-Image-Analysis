package edu.tamu.tcat.dia.morphological.test;

import static org.junit.Assert.assertTrue;

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
import java.util.Iterator;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;
import edu.tamu.tcat.dia.morphological.DilationOperator;
import edu.tamu.tcat.dia.morphological.ErosionOperator;
import edu.tamu.tcat.dia.morphological.ThresholdReducer;

public class DilationTests
{

   private BufferedImage image;
   private BinaryImage binImage;
   private BinaryImage binErodedImage;
   private BinaryImage binDilatedImage;
   private Path dataDir;
   private String filename, outFilename, outFilename2;

   public DilationTests()
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
      Path imagePath = dataDir.resolve("00000009-bin.png");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");
      filename = imagePath.toString();
      outFilename = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\00000009-dilated.png";
      outFilename2 = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\00000009-eroded.png";
      
      Iterator<ImageReader> imageReadersBySuffix = ImageIO.getImageReadersBySuffix("png");
      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      

   }

   @Test
   public void testDilation() throws IOException
   {	
      //DilationOperator dilator = new DilationOperator(filename, outFilename, null,null);
      
	  //DilationOperator dilator = new DilationOperator(binImage, null,null);
      //binDilatedImage = dilator.run();
      
      
      
      ErosionOperator eroder = new ErosionOperator(binImage, null, null);
      binErodedImage = eroder.run();
      
      /*Path outputPath = dataDir.resolve("output/dilated-image.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binDilatedImage, image), "png", out);
         out.flush();
      }*/
      
      Path outputPath = dataDir.resolve("output/00000009-eroded.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binErodedImage, image), "png", out);
         out.flush();
      }
      
      assertTrue("", true);
   }
}
