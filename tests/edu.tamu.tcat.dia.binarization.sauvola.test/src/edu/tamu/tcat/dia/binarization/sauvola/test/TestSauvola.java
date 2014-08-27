package edu.tamu.tcat.dia.binarization.sauvola.test;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Test;

import edu.tamu.tcat.dia.binarization.sauvola.FastSauvolaFactory;

public class TestSauvola
{

   public TestSauvola()
   {
      // TODO Auto-generated constructor stub
   }

   @Test
   public void thresholdTestImage() throws IOException
   {
      FastSauvolaFactory thresholder = new FastSauvolaFactory();
//      thresholder.setK(0.3);
//      thresholder.setWindowSize(15);
      URL imagePath = this.getClass().getResource("res/shipbuilding-treatise.jpg");
      ImageIO.read(imagePath.openStream());
//      ImageIO.read(Files.newInputStream(imagePath.openStream(), StandardOpenOption.READ);
      
   }
}
