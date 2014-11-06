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
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;
import edu.tamu.tcat.dia.morphological.DilationOperator;
import edu.tamu.tcat.dia.morphological.ErosionOperator;
import edu.tamu.tcat.dia.morphological.ExpansionOperator;
import edu.tamu.tcat.dia.morphological.ThresholdReducer;
import edu.tamu.tcat.dia.morphological.UnionOperator;

public class BloombergTest
{

   private BufferedImage image;
   private BinaryImage binImage, unionImage;
   private BinaryImage binExpandedImage1, binExpandedImage2, binExpandedImage3, binExpandedImage4;
   private BinaryImage binReducedImage1, binReducedImage2, binReducedImage3, binReducedImage4;
   private Path dataDir;


   public BloombergTest()
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
      Path imagePath = dataDir.resolve("00000008.jp2");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      Iterator<ImageReader> imageReadersBySuffix = ImageIO.getImageReadersBySuffix("jp2");
      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      System.out.println("Created binary image");
   }

   @Test
   public void testBloomberg() throws IOException
   {

      Path outputPath = dataDir.resolve("output/00000008-bin.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binImage, image), "png", out);
         out.flush();
      }
      System.out.println("Binary image written to file");
      
      System.out.println("Will run 4x1 Threshold reducer twice, T=1");
      
      System.out.println("Pass 1:");
      ThresholdReducer reducer = new ThresholdReducer(binImage, 4, 1);
      binReducedImage1 = reducer.run();
      System.out.println("binReducedImage1 size: "+binReducedImage1.getHeight()+","+binReducedImage1.getWidth());
      
      System.out.println("Pass 2:");
      reducer = new ThresholdReducer(binReducedImage1, 4, 1);
      binReducedImage2 = reducer.run();
      System.out.println("binReducedImage2 size: "+binReducedImage2.getHeight()+","+binReducedImage2.getWidth());
      System.out.println("======================");
      
      System.out.println("Will run 4x1 Threshold reducer, T=4");
      reducer = new ThresholdReducer(binReducedImage2, 4, 4);
      binReducedImage3 = reducer.run();
      System.out.println("binReducedImage3 size: "+binReducedImage3.getHeight()+","+binReducedImage3.getWidth());
      System.out.println("======================");
      
      System.out.println("Will run 4x1 Threshold reducer, T=3");
      reducer = new ThresholdReducer(binReducedImage3, 4, 3);
      binReducedImage4 = reducer.run();
      System.out.println("binReducedImage4 size: "+binReducedImage4.getHeight()+","+binReducedImage4.getWidth());
      System.out.println("======================");
            
      outputPath = dataDir.resolve("output/00000008-bin-reduced4.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binReducedImage4, image), "png", out);
         out.flush();
      }
      
      dataDir = Paths.get("C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output");
      Path imagePath = dataDir.resolve("00000008-bin-reduced4.png");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");
      
      String filename = imagePath.toString();
      String dilatedFilename = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output\\00000008-dilated.png";
      String erodedFilename = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output\\00000008-eroded.png";
      
      System.out.println("Will run opening with SE=5x5 element");
      System.out.println("Eroding first");
      ErosionOperator eroder = new ErosionOperator(filename, erodedFilename, null, null);
      eroder.run();
      
      System.out.println("Dilating now");
      DilationOperator dilator = new DilationOperator(erodedFilename, dilatedFilename, null,null);
      dilator.run();
      
      //need binary image to run 
      dataDir = Paths.get("C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output");
      imagePath = dataDir.resolve("00000008-dilated.png");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      System.out.println("Created binary image for result of opening operation");
      
      ExpansionOperator expander = new ExpansionOperator(binImage, 4);
      binExpandedImage1 = expander.run();
      
      expander = new ExpansionOperator(binExpandedImage1, 4);
      binExpandedImage2 = expander.run();
      
      //binExpandedImage2 should have same size as image after first 2 passes of threshold reduction
      System.out.println("binExpandedImage2 size: "+binExpandedImage2.getHeight()+","+binExpandedImage2.getWidth());
      UnionOperator unionOp = new UnionOperator(binReducedImage2,binExpandedImage2,true);
      unionImage = unionOp.run();
      
      System.out.println("Writing result of union image to file for further operations");
      outputPath = dataDir.resolve("00000008-bin-union.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)unionImage, image), "png", out);
         out.flush();
      }
      
      dilatedFilename = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output\\00000008-dilated-3x3.png";
      String unionFilename =  "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output\\00000008-bin-union.png";
      System.out.println("Dilating using 3x3 SE");
      dilator = new DilationOperator(unionFilename, dilatedFilename, 3,null);
      dilator.run();
      
      //need binary image to run expansion
      dataDir = Paths.get("C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output");
      imagePath = dataDir.resolve("00000008-dilated-3x3.png");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      iImage = IntegralImageImpl.create(image.getData());
      binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      System.out.println("Created binary image from result of dilation 3x3 operation, to be used by expansion operator");
      
      System.out.println("Running 2 1x4 expansions");
      expander = new ExpansionOperator(binImage, 4);
      binExpandedImage3 = expander.run();
      
      expander = new ExpansionOperator(binExpandedImage3, 4);
      binExpandedImage4 = expander.run();
      
      System.out.println("Writing final image");
      outputPath = dataDir.resolve("00000008-bin-expanded-final.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binExpandedImage4, image), "png", out);
         out.flush();
      }
      
      
      
      
      
      
      /*ExpansionOperator expander = new ExpansionOperator(binReducedImage, 4);
      binExpandedImage = expander.run();
      
      outputPath = dataDir.resolve("output/00000009-bin-expanded.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binExpandedImage, image), "png", out);
         out.flush();
      }*/
      
      assertTrue("", true);
   }
}
