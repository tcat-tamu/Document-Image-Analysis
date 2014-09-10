package edu.tamu.tcat.dia.binarization.sauvola;

import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class FastSauvolaBinarizer
{
   private final int width;
   private final int height;
   
   private final int ts;        // tile size
   private final int whalf;   // half the window size
   private final double k;      //
   private int    range  = 128;     // control for dynamic range
   
   BooleanArrayBinaryImage  output;
   private IntegralImage input;
   
   FastSauvolaBinarizer(IntegralImage source, int windowSize, double k)
   {
      this.input = source;
      
      this.width = source.getWidth();
      this.height = source.getHeight();
      
      this.output = new BooleanArrayBinaryImage(width, height);
      
      this.ts = (windowSize > 0) ? windowSize : width / 15; 
      this.whalf = this.ts / 2;
      this.k = k;
   }
   
   public BinaryImage run()
   {
      int offset = 0;
      for (int rowIx = 0; rowIx < height; rowIx++) {
         for (int colIx = 0; colIx < width; colIx++) 
         {
            int ymin = Math.max(0, rowIx - whalf);
            int xmin = Math.max(0, colIx - whalf);
            int xmax = Math.min(width - 1, colIx + whalf);
            int ymax = Math.min(height - 1, rowIx + whalf);
            
            double[] model = input.getGausModel(xmin, ymin, xmax, ymax);
            double mean = model[0];
            double stddev = Math.sqrt(model[1]);
            
            double threshold = mean * (1 + k * ((stddev / range) - 1));
            
            if (input.get(offset + colIx) > threshold)
               output.setBackground(offset + colIx);
            else
               output.setForeground(offset + colIx);
         }

         offset += width;
      }
      
      return output;
   }
}