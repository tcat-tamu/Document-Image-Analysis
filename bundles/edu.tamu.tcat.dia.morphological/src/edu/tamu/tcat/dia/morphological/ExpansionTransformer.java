package edu.tamu.tcat.dia.morphological;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.DataSink;
import edu.tamu.tcat.analytics.datatrax.DataSource;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class ExpansionTransformer implements TransformerFactory
{

   private int scaleFactor = 1;
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
            int height = ((BinaryImage)source).getHeight();
            int width = ((BinaryImage)source).getWidth();
            int scaledWidth = (int) (width * Math.sqrt(scaleFactor));
            int scaledHeight = (int) (height * Math.sqrt(scaleFactor));
            BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
            
            int pixelX = 0;
            int pixelY = 0;
            for (int rowIx = 0; rowIx < height; rowIx++) {
               
               for (int colIx = 0; colIx < width; colIx++){
                  if(((BinaryImage)source).isForeground(colIx, rowIx)){
                     //Set foreground for 4 pixels
                     pixelX = rowIx*2;
                     pixelY = colIx*2;
                     output.setForeground(pixelX*scaledWidth+pixelY);
                     
                     pixelX = rowIx*2;
                     pixelY = colIx*2+1;
                     output.setForeground(pixelX*scaledWidth+pixelY);
                     
                     pixelX = rowIx*2+1;
                     pixelY = colIx*2; 
                     output.setForeground(pixelX*scaledWidth+pixelY);
                     
                     pixelX = rowIx*2+1;
                     pixelY = colIx*2+1;  
                     output.setForeground(pixelX*scaledWidth+pixelY);
                     
                  }
                  
                  
               }
               
            }            
            ((DataSink)sink).accept((BinaryImage)output);
         }
      };

   }

}
