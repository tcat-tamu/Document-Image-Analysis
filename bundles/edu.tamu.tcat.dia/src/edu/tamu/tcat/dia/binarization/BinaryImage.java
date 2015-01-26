package edu.tamu.tcat.dia.binarization;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public interface BinaryImage
{
   public static BufferedImage toBufferedImage(BinaryImage im)
   {
      int width = im.getWidth();
      int height = im.getHeight();

      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = image.getRaster();
      int offset = 0;
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

      return image;
   }
   
   int getWidth();
   
   int getHeight();
   
   /**
    * @return The size of this image in number of pixels. This will equal
    *    {@code getWidth() * getHeight()}. 
    */
   int getSize();
   
   boolean isForeground(int w, int h) throws IllegalArgumentException;

   boolean isForeground(int ix) throws IllegalArgumentException;
}
