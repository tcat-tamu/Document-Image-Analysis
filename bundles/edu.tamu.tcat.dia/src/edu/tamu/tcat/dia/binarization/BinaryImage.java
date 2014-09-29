package edu.tamu.tcat.dia.binarization;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public interface BinaryImage
{
   public static BufferedImage toBufferedImage(BinaryImage im, BufferedImage model)
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
