package edu.tamu.tcat.dia.adapters.opencv;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.tamu.tcat.analytics.datatrax.DataSink;
import edu.tamu.tcat.analytics.datatrax.DataSource;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;
import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.morphological.OpenCvMatrix;

public class BinaryToOpenCvMatrix implements TransformerFactory
{

   public BinaryToOpenCvMatrix()
   {
   }

   @Override
   public Class<BufferedImage> getSourceType()
   {
      return BufferedImage.class;
   }

   @Override
   public Class<IntegralImage> getOutputType()
   {
      return IntegralImage.class;
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // no-op
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      return new HashMap<>();
   }

   public static byte boolToByte(boolean b) {
      return (byte) (b ? 0 : 1);
   }

   private static byte[] toByteArray(BinaryImage data)
   {
      int sz = data.getSize();
      byte[] imageByteArray = new byte[sz];
      for (int i = 0; i < sz; i++)
      {
         imageByteArray[i] = boolToByte(data.isForeground(i));
      }

      return imageByteArray;
   }
   
   @Override
   public Runnable create(DataSource<?> source, DataSink<?> sink)
   {
      return new Runnable()
      {
         
         @Override
         public void run()
         {
            BinaryImage im = (BinaryImage)source.get();
            Mat matrix = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8U);
            matrix.put(0, 0, toByteArray(im));
            
            ((DataSink)sink).accept(new OpenCvMatrix(matrix));
         }
      };
   }
}
