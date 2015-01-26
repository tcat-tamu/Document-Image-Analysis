package edu.tamu.tcat.dia.adapters.opencv;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.opencv.core.Mat;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.morphological.opencv.OpenCvMatrix;

public class OpenCvMatrixToBinary implements Transformer
{

   public final static String EXTENSION_ID = "tcat.dia.adapters.opencv.matrix2binary"; 
   public static final String IMAGE_MATRIX_PIN = "image_matrix";

   public OpenCvMatrixToBinary()
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
   
   private BinaryImage adapt(Mat srcMat)
   {
      int srcSize = srcMat.cols() * srcMat.rows();
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(srcMat.cols(), srcMat.rows());
      
      byte[] outImageByteArray = new byte[srcSize]; 
      srcMat.get(0, 0, outImageByteArray);
      for (int i = 0; i < srcSize; i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }
      
      return output;
   }
   
   @Override
   public Callable<BinaryImage> create(TransformerContext ctx)
   {
      OpenCvMatrix ref = (OpenCvMatrix)ctx.getValue(IMAGE_MATRIX_PIN);
      // HACK: since the referenced matix may be disposed once this method exits,
      //       we'll copy the data out to a binary image here.
      BinaryImage binaryImage = adapt(ref.get());
      return new Callable<BinaryImage>()
      {
         // TODO create a simple pass through adapter.
         @Override
         public BinaryImage call()
         {
            return binaryImage;
         }
      };
   }
}
