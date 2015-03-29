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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import org.junit.Before;
import org.junit.Test;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaBinarizer;
import edu.tamu.tcat.dia.classifier.music.runlength.RunLengthRatioGenerator;
import edu.tamu.tcat.dia.classifier.music.runlength.RunLengthRatioGenerator.RunLengthStruct;

public class RLGeneratorTest
{

   private BufferedImage image;
   private BinaryImage binImage;
   private BinaryImage binReducedImage;
   private Path dataDir;

   public RLGeneratorTest()
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
   
   @Before
   public void setup() throws IOException
   {
      dataDir = Paths.get("I:\\Projects\\HathiTrust WCSA\\WCSA initial small dataset\\39015049753844");
      Path imagePath = dataDir.resolve("00000027.tif");
//      dataDir = Paths.get("I:\\Projects\\HathiTrust WCSA\\WCSA initial small dataset\\ark+=13960=t00z72x8w");
//      Path imagePath = dataDir.resolve("00000027.jp2");
      if (!Files.exists(imagePath))
         throw new IllegalArgumentException("Source image does not exist [" + imagePath + "]");

      Iterator<ImageReader> imageReadersBySuffix = ImageIO.getImageReadersBySuffix("png");
      image = ImageIO.read(Files.newInputStream(imagePath, StandardOpenOption.READ));
      Objects.requireNonNull(image, "Failed to load source image [" + imagePath +"]");
      
      IntegralImageImpl iImage = IntegralImageImpl.create(image.getData());
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(iImage, image.getWidth() / 15, 0.3);
      binImage = binarizer.run();
      
   }

   @Test
   public void testRLRatioGenerator() throws IOException
   {

//      Path outputPath = dataDir.resolve("bach-sonata-bin.png");
//      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
//      {
//         ImageIO.write(toImage((BinaryImage)binImage, image), "png", out);
//         out.flush();
//      }
      
//      Map<Integer, ArrayList<Float>> rlratio; 
      List<RunLengthStruct> ratios = RunLengthRatioGenerator.findRunLengthRatios(binImage, 5);
//      RunLengthRatioGenerator rlgenerator = new RunLengthRatioGenerator(binImage, 5);
//      rlratio = rlgenerator.run();
      for (RunLengthStruct rl : ratios)
      {
         System.out.print(rl.mode + ", ");
      }
      System.out.println();
//      System.out.println("keys: "+rlratio.keySet());
//      System.out.println("values: "+rlratio.values());
//      
      /*for(int i=0;i<rlratio.size();i++){
    	  
    	  if(rlratio.get(i) == Collections.EMPTY_LIST)
    		  System.out.println("RL ratio list "+i+" is empty");
    	  else
    		  System.out.println("RL ratio list "+i+": "+rlratio.get(i).toString());
      }*/
      
      /*outputPath = dataDir.resolve("output/00000009-bin-reduced.png");
      try (OutputStream out = Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE))
      {
         ImageIO.write(toImage((BinaryImage)binReducedImage, image), "png", out);
         out.flush();
      }*/
      
      assertTrue("", true);
   }
}
