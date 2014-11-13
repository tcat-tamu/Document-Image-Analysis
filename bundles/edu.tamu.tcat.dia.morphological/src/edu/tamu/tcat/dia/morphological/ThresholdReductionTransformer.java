package edu.tamu.tcat.dia.morphological;

import java.util.HashMap;
import java.util.Map;

import edu.tamu.tcat.analytics.datatrax.DataSink;
import edu.tamu.tcat.analytics.datatrax.DataSource;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class ThresholdReductionTransformer implements TransformerFactory
{
   private int scaleFactor = 1;
   private int threshold = 1;

   @Override
   public Class<?> getSourceType()
   {
      return BinaryImage.class;
   }

   @Override
   public Class<?> getOutputType()
   {
      return BinaryImage.class;
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // HACK need to read from config data
      scaleFactor = 4;
      threshold = 1;
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      return params;
   }

   @Override
   public Runnable create(DataSource<?> source, DataSink<?> sink)
   {
      return new Runnable()
      {
         
         @Override
         public void run()
         {
            int width = ((BinaryImage)source).getWidth();
            int height = ((BinaryImage)source).getHeight();

            if (height % 2 != 0)
               height = height - 1;

            if (width % 2 != 0)
               width = width - 1;


            int scaledWidth = (int)(width / Math.sqrt(scaleFactor));
            int scaledHeight = (int)(height / Math.sqrt(scaleFactor));
           
            BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
            
            int outputRowIx = 0;
            int outputColIx = 0;
            int offset = 0;
            for (int rowIx = 0; rowIx < height; rowIx += 2)
            {
               outputColIx = 0;
               for (int colIx = 0; colIx < width; colIx += 2)
               {
                  int sumValues = 0;
                  if (((BinaryImage)source).isForeground(colIx, rowIx))
                     sumValues += 1;

                  if (((BinaryImage)source).isForeground(colIx, rowIx + 1))
                     sumValues += 1;

                  if (((BinaryImage)source).isForeground(colIx + 1, rowIx))
                     sumValues += 1;

                  if (((BinaryImage)source).isForeground(colIx + 1, rowIx + 1))
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
            ((DataSink)sink).accept((BinaryImage)output);
         }
         
      };
   }

}
