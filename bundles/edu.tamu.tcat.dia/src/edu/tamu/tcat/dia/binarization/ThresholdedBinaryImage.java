package edu.tamu.tcat.dia.binarization;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * A simple {@link BinaryImage} implementation that interprets pixels from a source image as 
 * either foreground/background values based on whether they are lighter or darker than some
 * threshold value. This is typically the easiest and fastest method for thresholding images
 * with clearly separated foreground and background layers.  
 *
 */
public class ThresholdedBinaryImage implements BinaryImage
{

   private final int k;
   private final int width;
   private final int height;
   private final int size;
   private final int numBands;
   
   private final WritableRaster raster;

   public ThresholdedBinaryImage(BufferedImage image, int k)
   {
      raster = image.getRaster();
      numBands = raster.getNumBands();
      
      this.width = image.getWidth();
      this.height = image.getHeight();
      this.size = width * height;
      this.k = k;
   }

   @Override
   public int getWidth()
   {
      return width;
   }

   @Override
   public int getHeight()
   {
      return height;
   }

   @Override
   public int getSize()
   {
      return size;
   }

   @Override
   public boolean isForeground(int x, int y) throws IllegalArgumentException
   {
      if (x >= width || y >= height)
      {
         String msg = String.format("Pixel [%d, %d] is out of bounds. Image size [%d, %d]", x, y, width, height);
         throw new IndexOutOfBoundsException(msg);
      }
      
      int px = 0;
      if (numBands == 0)
      {
         px = raster.getSample(x, y, 0);
      }
      else 
      {
         for (int b = 0; b < numBands; b++)
         {
            px += raster.getSample(x, y, b);
         }
         
         px = px / numBands;
      }
      
      return (px < k);
   }

   @Override
   public boolean isForeground(int ix) throws IllegalArgumentException
   {
      return isForeground(ix % width, ix / width);
   }

}
