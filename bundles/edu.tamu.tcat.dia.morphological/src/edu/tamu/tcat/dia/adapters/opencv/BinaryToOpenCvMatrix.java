package edu.tamu.tcat.dia.adapters.opencv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.morphological.OpenCvMatrix;

public class BinaryToOpenCvMatrix implements Transformer
{
   public final static String EXTENSION_ID = "tcat.dia.adapters.opencv.binary2matrix"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";

   public BinaryToOpenCvMatrix()
   {
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
   public Callable<OpenCvMatrix> create(TransformerContext ctx)
   {
      final BinaryImage im = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
      return new Callable<OpenCvMatrix>()
      {
         @Override
         public OpenCvMatrix call() throws Exception
         {
            Mat matrix = new Mat(im.getHeight(), im.getWidth(), CvType.CV_8U);
            matrix.put(0, 0, toByteArray(im));
            
            return new OpenCvMatrix(matrix);
         }
      };
   }
}
