package edu.tamu.tcat.dia.morphological;

import java.util.logging.Logger;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class ThresholdReducer
{

   private BinaryImage input;
   private int scaleFactor;
   private int threshold;
   private int width;
   private int height;
   private int scaledWidth;
   private int scaledHeight;
   private BooleanArrayBinaryImage output;
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.reduction");

   public ThresholdReducer(BinaryImage source, int scaleFactor, int threshold)
   {
      this.input = source;

      this.scaleFactor = scaleFactor;
      this.threshold = threshold;

      this.width = source.getWidth();
      this.height = source.getHeight();

      if (height % 2 != 0)
         this.height = height - 1;

      if (width % 2 != 0)
         this.width = width - 1;

      logger.fine("Input Image Height: " + height + ", Width: " + width);
      
      this.scaledWidth = (int)(width / Math.sqrt(scaleFactor));
      this.scaledHeight = (int)(height / Math.sqrt(scaleFactor));
      //logger.finest("Scaled Height: " + scaledHeight + ", Scaled Width: " + scaledWidth);

      this.output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
   }

   public BinaryImage run()
   {

      //Each row is processed in blocks of four pixels and stored in scaled image
      //if sum of values of 4 pixels >= threshold, set output pixel to true

      //TODO - Block size should be changed to a square based on the scale factor

      int outputRowIx = 0;
      int outputColIx = 0;
      int offset = 0;
      for (int rowIx = 0; rowIx < height; rowIx += 2)
      {
         outputColIx = 0;
         for (int colIx = 0; colIx < width; colIx += 2)
         {

            int sumValues = 0;
            if (input.isForeground(colIx, rowIx))
               sumValues += 1;

            if (input.isForeground(colIx, rowIx + 1))
               sumValues += 1;

            if (input.isForeground(colIx + 1, rowIx))
               sumValues += 1;

            if (input.isForeground(colIx + 1, rowIx + 1))
               sumValues += 1;

            if (sumValues >= threshold)
            {
               output.setForeground(offset + outputColIx);
            }

            outputColIx++;

         }
         offset += scaledWidth;
         outputRowIx++;

      }

      logger.fine("Output Image Height: " + output.getHeight() + ", width: " + output.getWidth());
      return output;

   }
}
