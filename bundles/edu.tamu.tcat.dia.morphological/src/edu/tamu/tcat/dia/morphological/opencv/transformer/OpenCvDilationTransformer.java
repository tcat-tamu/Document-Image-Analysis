package edu.tamu.tcat.dia.morphological;

import java.util.concurrent.Callable;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.TransformerContext;

public class OpenCvDilationTransformer extends KernelBasedTransformer 
{
   public final static String EXTENSION_ID = "tcat.dia.morphological.opencv.dilation"; 
   public static final String IMAGE_MATRIX_PIN = "image_matrix";
   
   public OpenCvDilationTransformer()
   {
   }

   @Override
   public Callable<OpenCvMatrix> create(TransformerContext ctx)
   {
      // FIXME input may have been disposed.
      
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
               Imgproc.dilate(mat, dest, kernel.get());

               return new OpenCvMatrix(dest);
            }
         }
      };
   }
}
