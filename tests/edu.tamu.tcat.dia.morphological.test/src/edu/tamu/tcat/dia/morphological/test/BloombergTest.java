package edu.tamu.tcat.dia.morphological.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;
import edu.tamu.tcat.dia.morphological.MorphologicalOperationException;
import edu.tamu.tcat.dia.morphological.opencv.OpenCVClosingOperator;
import edu.tamu.tcat.dia.morphological.opencv.OpenCVErosionOperator;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;
import edu.tamu.tcat.dia.segmentation.cc.twopass.Finder;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.ExpansionOperator;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.ThresholdReducer;

public class BloombergTest
{
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morphological.test.bloomberg");

   public BloombergTest()
   {
      // TODO Auto-generated constructor stub
   }


   @Before
   public void setup() throws IOException
   {
   }

   private BinaryImage loadImage(Path imagePath) throws IOException
   {
//      Path imagePath = dataDir.resolve("00000009.jp2");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      BufferedImage image = ImageIO.read(imagePath.toFile());
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath + "]");

      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      return binarizer.run();
   }
   
   @Test
   public void testBloomberg() throws IOException, MorphologicalOperationException
   {
      Path inputDir = Paths.get("I:\\Projects\\HathiTrust WCSA\\WCSA initial small dataset");
      Path outputBaseDir = Paths.get("I:\\Projects\\HathiTrust WCSA\\WCSA test results");
      
      String volumeId = "ark+=13960=t0xp6w53s";
//      String volumeId = "ark+=13960=t00z72x8w";
      String imageId = "00000035";
      Path imagePath = inputDir.resolve(volumeId).resolve(imageId + ".jp2");
      Path outputDir = outputBaseDir.resolve(volumeId).resolve(imageId);
      Files.createDirectories(outputDir);
      
      BinaryImage initial = loadImage(imagePath);
      long start = System.currentTimeMillis();

      writeImage(initial, outputDir, "original");
      logger.info("Pass 1:");
      
      // 4x1 threshold reduction with T = 1 (2x)
      BinaryImage initialReduction = ThresholdReducer.threshold(initial, 1);
      initialReduction = ThresholdReducer.threshold(initialReduction, 1);
      
//      writeImage(initialReduction, outputDir, "initial_reduction");
      
      // 4x1 threshold reduction with T = 4
      BinaryImage intermediate = ThresholdReducer.threshold(initialReduction, 4);
//      writeImage(intermediate, outputDir, "T4");
      
      // 4x1 threshold reduction with T = 3
      intermediate = ThresholdReducer.threshold(intermediate, 3);
//      writeImage(intermediate, outputDir, "T3");
      
      // opening with SE 5x5
      try (OpenCVClosingOperator opening = new OpenCVClosingOperator())
      {
         opening.setInput(intermediate);
         opening.setStructuringElement(5);
         opening.execute();
         intermediate = opening.getResult();
//         writeImage(intermediate, outputDir, "opened");
      }
      
      // 1X4 expansion (twice)
      intermediate = ExpansionOperator.expand(intermediate);
      intermediate = ExpansionOperator.expand(intermediate);
//      writeImage(intermediate, outputDir, "seed");

      // Union of overlapping components (initialReduction)
      intermediate = computeIntersection(initialReduction, intermediate);
//      writeImage(intermediate, outputDir, "union");
      
      // Dilation with SE 3x3
      try (OpenCVErosionOperator dilator = new OpenCVErosionOperator())
      {
         dilator.setInput(intermediate);
         dilator.setStructuringElement(3);
         dilator.execute();
         intermediate = dilator.getResult();
//         writeImage(intermediate, outputDir, "eroded");
      }
      // 1X4 expansion (twice)
      intermediate = ExpansionOperator.expand(intermediate);
      intermediate = ExpansionOperator.expand(intermediate);

      long end = System.currentTimeMillis();
      System.out.println("Elapsed Time: " + (end - start));
      writeImage(intermediate, outputDir, "mask");
      
   }


   private BinaryImage computeIntersection(BinaryImage initialReduction, BinaryImage intermediate)
   {
      Finder ccFinder = new Finder(intermediate, 1000);
      ConnectComponentSet seedComponents = ccFinder.call();
      ccFinder = new Finder(initialReduction, 10000);
      ConnectComponentSet sourceCCs = ccFinder.call();
      
//      System.out.println(seedComponents.listLabels().size());
//      System.out.println(sourceCCs.listLabels().size());
      Set<ConnectedComponent> union = new HashSet<>();
//      int ct = 0;
      for (ConnectedComponent sourceCC : sourceCCs)
      {
         if (sourceCC.getNumberOfPixels() < 5)
            continue;
//         ct++;
         for (ConnectedComponent seedCC : seedComponents)    
         {
            if (seedCC.intersects(sourceCC))
               union.add(sourceCC);
         }
      }
      
      int w = initialReduction.getWidth();
      int h = initialReduction.getHeight();
      BooleanArrayBinaryImage result = new BooleanArrayBinaryImage(w, h);
      for (ConnectedComponent cc : union)
      {
         for (Point p : cc.getPoints())
         {
            result.setForeground(p.getY() * w + p.getX());
         }
      }
      
      return result;
//      System.out.println(ct);
//      Integer pxCt = union.stream().map(cc -> cc.getNumberOfPixels()).reduce(0, (a, b) -> a.intValue() + b.intValue());
//      System.out.println(pxCt);
//      System.out.println(pxCt / (double)(intermediate.getWidth() * intermediate.getHeight()));
//      
//      System.out.println(union.size());
   }


   private void writeImage(BinaryImage initialReduction, Path outputDir, String name) throws IOException
   {
      Path outputImagePath = outputDir.resolve(name + ".jpeg");
      ImageIO.write(BinaryImage.toBufferedImage(initialReduction), "jpeg", outputImagePath.toFile());
   }
}
