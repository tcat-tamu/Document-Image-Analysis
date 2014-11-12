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
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.morphological.OpenCvMatrix;

public class OpenCvMatrixToBinary implements TransformerFactory
{

   public OpenCvMatrixToBinary()
   {
   }

   @Override
   public Class<OpenCvMatrix> getSourceType()
   {
      return OpenCvMatrix.class;
   }

   @Override
   public Class<BinaryImage> getOutputType()
   {
      return BinaryImage.class;
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
   
   @Override
   public Runnable create(DataSource<?> source, DataSink<?> sink)
   {
      return new Runnable()
      {
         
         @Override
         public void run()
         {
            Mat srcMat = ((OpenCvMatrix)source.get()).get();
            int srcSize = srcMat.cols() * srcMat.rows();
            BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(srcMat.cols(), srcMat.rows());
            byte[] outImageByteArray = new byte[srcSize]; 
            srcMat.get(0, 0, outImageByteArray);
            for (int i = 0; i < srcSize; i++)
            {
               if (outImageByteArray[i] == 0)
                  output.setForeground(i);
            }
            
            ((DataSink)sink).accept((BinaryImage)output);
         }
      };
   }
}
