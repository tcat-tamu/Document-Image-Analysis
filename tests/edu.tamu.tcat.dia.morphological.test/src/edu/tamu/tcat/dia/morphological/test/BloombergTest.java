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
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.ThresholdedBinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;
import edu.tamu.tcat.dia.morphological.DilationOperator;
import edu.tamu.tcat.dia.morphological.ErosionOperator;
import edu.tamu.tcat.dia.morphological.ExpansionOperator;
import edu.tamu.tcat.dia.morphological.ThresholdReducer;
import edu.tamu.tcat.dia.morphological.UnionOperator;

public class BloombergTest
{

   private BufferedImage image;
   private BinaryImage binImage, unionImage, binEroded, binDilated, binDilated2;
   private BinaryImage binExpandedImage1, binExpandedImage2, binExpandedImage3, binExpandedImage4;
   private BinaryImage binReducedImage1, binReducedImage2, binReducedImage3, binReducedImage4, binReducedImageInter;
   private Path dataDir;
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morphological.test.bloomberg");

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
      dataDir = Paths.get("C:\\Projects\\VisualPage\\images");
      Path imagePath = dataDir.resolve("00000009.jp2");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      Iterator<ImageReader> imageReadersBySuffix = ImageIO.getImageReadersBySuffix("jp2");
      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath + "]");

      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();

   }

   @Test
   public void testBloomberg() throws IOException
   {

      logger.info("Will run 4x1 Threshold reducer twice, T=3");

      logger.info("Pass 1:");
      ThresholdReducer reducer = new ThresholdReducer(binImage, 4, 3);
      binReducedImage1 = reducer.run();
      logger.info("binReducedImage1 size: " + binReducedImage1.getHeight() + "," + binReducedImage1.getWidth());

      Path outputPath = dataDir.resolve("reduction1.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binReducedImage1, image), "png", out);
         out.flush();
      }

      logger.info("Pass 2:");
      reducer = new ThresholdReducer(binReducedImage1, 4, 3);
      binReducedImage2 = reducer.run();
      logger.info("binReducedImage2 size: " + binReducedImage2.getHeight() + "," + binReducedImage2.getWidth());
      logger.info("======================");

      /*
       * logger.info("Pass 2.5:"); reducer = new ThresholdReducer(binReducedImage2, 4, 1); binReducedImageInter =
       * reducer.run();
       * logger.info("binReducedImage2 size: "+binReducedImageInter.getHeight()+","+binReducedImageInter.getWidth());
       * logger.info("======================");
       */

      logger.info("Will run 4x1 Threshold reducer, T=4");
      reducer = new ThresholdReducer(binReducedImage2, 4, 4);
      binReducedImage3 = reducer.run();
      logger.info("binReducedImage3 size: " + binReducedImage3.getHeight() + "," + binReducedImage3.getWidth());
      logger.info("======================");

      logger.info("Will run 4x1 Threshold reducer, T=3");
      reducer = new ThresholdReducer(binReducedImage3, 4, 3);
      binReducedImage4 = reducer.run();
      logger.info("binReducedImage4 size: " + binReducedImage4.getHeight() + "," + binReducedImage4.getWidth());
      logger.info("======================");

      outputPath = dataDir.resolve("beforeOpening.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binReducedImage2, image), "png", out);
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
      BufferedImage input = ImageIO.read(Paths.get(erodedFilename).toFile());
      BinaryImage bin = new ThresholdedBinaryImage(input, 100);
      DilationOperator dilator = new DilationOperator(bin);
      BinaryImage result = dilator.run();
      ImageIO.write(BinaryImage.toBufferedImage(result, input), "png", Files.newOutputStream(Paths.get(dilatedFilename), StandardOpenOption.CREATE, StandardOpenOption.WRITE));
      
      //need binary image to run 
      dataDir = Paths.get("C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\output");
      imagePath = dataDir.resolve("00000008-dilated.png");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

//      logger.info("Will run opening with SE=3x3 element");
//      logger.info("Eroding first");
//      ErosionOperator eroder = new ErosionOperator(binReducedImage4, 3);
//      binEroded = eroder.run();
//
//      logger.info("Dilating now");
//      DilationOperator dilator = new DilationOperator(binEroded, 3);
//      binDilated = dilator.run();

      outputPath = dataDir.resolve("afterOpening.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binDilated, image), "png", out);
         out.flush();
      }

      ExpansionOperator expander = new ExpansionOperator(binDilated, 4);
      binExpandedImage1 = expander.run();

      expander = new ExpansionOperator(binExpandedImage1, 4);
      binExpandedImage2 = expander.run();

      outputPath = dataDir.resolve("seed.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binExpandedImage2, image), "png", out);
         out.flush();
      }

      //binExpandedImage2 should have same size as image after first 2 passes of threshold reduction
      logger.info("binExpandedImage2 size: " + binExpandedImage2.getHeight() + "," + binExpandedImage2.getWidth());
      UnionOperator unionOp = new UnionOperator(binReducedImage2, binExpandedImage2, true);
      unionImage = unionOp.run();

      logger.info("Dilating using 3x3 SE");
      dilator = new DilationOperator(unionImage, 3);
      binDilated2 = dilator.run();

      logger.info("Running 2 1x4 expansions");
      expander = new ExpansionOperator(binDilated2, 4);
      binExpandedImage3 = expander.run();

      expander = new ExpansionOperator(binExpandedImage3, 4);
      binExpandedImage4 = expander.run();

      logger.info("Writing final image");
      outputPath = dataDir.resolve("9-final.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binExpandedImage4, image), "png", out);
         out.flush();
      }

      assertTrue("", true);
   }
}
