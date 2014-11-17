package edu.tamu.tcat.dia.morphological;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class ExpansionTransformer implements Transformer
{

   public final static String EXTENSION_ID = "tcat.dia.morphological.expansion"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";
   private int scaleFactor = 1;

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // HACK need to read from config data
      scaleFactor = 4;
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
            int height = source.getHeight();
            int width = source.getWidth();
            int scaledWidth = (int) (width * Math.sqrt(scaleFactor));
            int scaledHeight = (int) (height * Math.sqrt(scaleFactor));
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
      };
   }
}
