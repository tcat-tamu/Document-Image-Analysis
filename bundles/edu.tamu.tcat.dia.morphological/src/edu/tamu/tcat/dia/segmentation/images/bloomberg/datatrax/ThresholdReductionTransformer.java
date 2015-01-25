package edu.tamu.tcat.dia.segmentation.images.bloomberg.datatrax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class ThresholdReductionTransformer implements Transformer
{
   public final static String EXTENSION_ID = "tcat.dia.morphological.reduction"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";
   
   private int scaleFactor = 4;
   private int threshold = 1;

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // HACK need to read from config data
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      return params;
   }

   @Override
   public Callable<BinaryImage> create(TransformerContext ctx)
   {
      final BinaryImage source = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
      
      return new Callable<BinaryImage>()
      {
         @Override
         public BinaryImage call() throws Exception
         {
            int width = source.getWidth();
            int height = source.getHeight();

            if (height % 2 != 0)
               height = height - 1;

            if (width % 2 != 0)
               width = width - 1;

            // FIXME scale factor is configurable, but the inner loop only supports a factor of 4.
            int scaledWidth = (int)(width / Math.sqrt(scaleFactor));
            int scaledHeight = (int)(height / Math.sqrt(scaleFactor));
           
            BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
            
            int outputRowIx = 0;    // FIXME why is this unused?
            int outputColIx = 0;
            int offset = 0;
            for (int rowIx = 0; rowIx < height; rowIx += 2)
            {
               outputColIx = 0;
               for (int colIx = 0; colIx < width; colIx += 2)
               {
                  int sumValues = 0;
                  if (source.isForeground(colIx, rowIx))
                     sumValues += 1;

                  if (source.isForeground(colIx, rowIx + 1))
                     sumValues += 1;

                  if (source.isForeground(colIx + 1, rowIx))
                     sumValues += 1;

                  if (source.isForeground(colIx + 1, rowIx + 1))
                     sumValues += 1;

                  if (sumValues >= threshold)
                     output.setForeground(offset + outputColIx);
                  
                  outputColIx++;
               }
               
               offset += scaledWidth;
               outputRowIx++;
            }
            
            return output;
         }
      };
   }
}
