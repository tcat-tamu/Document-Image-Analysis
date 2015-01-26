package edu.tamu.tcat.dia.segmentation.images.bloomberg;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class ExpansionOperator {

	public static BinaryImage expand(BinaryImage source) 
	{
	   int height = source.getHeight();
      int width = source.getWidth();
      int scaledWidth = width * 2;
      int scaledHeight = height * 2;

      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
      for (int rowIx = 0; rowIx < height; rowIx++) {
         int r = rowIx * 2;

         for (int colIx = 0; colIx < width; colIx++) {
            
            if (source.isForeground(colIx, rowIx)) {
               //Set foreground for 4 pixels
               int ix = r * scaledWidth + colIx * 2;           // base index
               output.setForeground(ix);                       // r, c
               output.setForeground(ix + 1);                   // r, c + 1
               output.setForeground(ix + scaledWidth);         // r + 1, c
               output.setForeground(ix + scaledWidth + 1);     // r + 1, c + 1
               
            }
         }
      }
      
      return output;
	}
}
