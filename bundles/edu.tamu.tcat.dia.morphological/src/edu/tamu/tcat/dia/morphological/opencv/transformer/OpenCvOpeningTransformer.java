package edu.tamu.tcat.dia.morphological.opencv.transformer;

import java.util.concurrent.Callable;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.morphological.opencv.OpenCvMatrix;

public class OpenCvOpeningTransformer extends KernelBasedTransformer 
{
   public final static String EXTENSION_ID = "tcat.dia.morphological.opencv.opening"; 
   public static final String IMAGE_MATRIX_PIN = "image_matrix";
   
   public OpenCvOpeningTransformer()
   {
   }
   
   @Override
   public Callable<OpenCvMatrix> create(TransformerContext ctx)
   {
      final OpenCvMatrix input = (OpenCvMatrix)ctx.getValue(IMAGE_MATRIX_PIN);
      return new Callable<OpenCvMatrix>()
      {
         @Override
         public OpenCvMatrix call() throws Exception
         {
            try (OpenCvMatrix kernel = getKernel())
            {
               Mat mat = input.get();
               Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());
               Imgproc.erode(mat, dest, kernel.get());

               return new OpenCvMatrix(dest);
            }
         }
      };
   }
}
