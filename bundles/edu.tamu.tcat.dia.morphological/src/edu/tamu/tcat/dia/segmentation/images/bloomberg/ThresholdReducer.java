package edu.tamu.tcat.dia.segmentation.images.bloomberg;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

/**
 *  
 *
 */
public final class ThresholdReducer
{
   // private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.reduction");

   public static BinaryImage threshold(BinaryImage input, int threshold)
   {
      // TODO enable customizable block size. For now, we'll stick with blocks of 4 pixels
      int w = makeEven(input.getWidth());
      int h = makeEven(input.getHeight());

      int scaledWidth = (int)(w / 2);
      int scaledHeight = (int)(h / 2);
      
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);

      // Each row is processed in blocks of four pixels and stored in scaled image
      // if sum of values of 4 pixels >= threshold, set output pixel to true
      int outputColIx = 0;
      int offset = 0;
      for (int rowIx = 0; rowIx < h; rowIx += 2)
      {
         outputColIx = 0;
         for (int colIx = 0; colIx < w; colIx += 2)
         {
            int ct = countForegroundPixels(input, rowIx, colIx);
            if (ct >= threshold)
               output.setForeground(offset + outputColIx);

            outputColIx++;
         }
         
         offset += scaledWidth;
      }

      return output;
   }
   
   /**
    * @return 1 if the x,y coordinates of the image are foreground, 0 otherwise.
    */
   private static int asInt(BinaryImage input, int x, int y)
   {
      return input.isForeground(x, y) ? 1 : 0;
   }
   
   /**
    * @return And even number that is equal to {@code value} or {@code value - 1}  
    */
   private static int makeEven(int value)
   {
      return (value % 2 != 0) ? value - 1 : value;
   }
   

   private static int countForegroundPixels(BinaryImage input, int rowIx, int colIx)
   {
      int ct =
            asInt(input, colIx, rowIx) +
            asInt(input, colIx, rowIx + 1) +
            asInt(input, colIx + 1, rowIx) +
            asInt(input, colIx + 1, rowIx + 1);
      return ct;
   }
}
