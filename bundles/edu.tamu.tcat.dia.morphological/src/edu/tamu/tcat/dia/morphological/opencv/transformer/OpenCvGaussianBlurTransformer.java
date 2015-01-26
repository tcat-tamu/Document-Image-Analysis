package edu.tamu.tcat.dia.morphological.opencv.transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.morphological.opencv.OpenCvMatrix;

public class OpenCvGaussianBlurTransformer implements Transformer
{

   private static final Logger ERROR_LOGGER = Logger.getAnonymousLogger("edu.tamu.tcat.dia.morphological.datatrax.errors");

   public final static String EXTENSION_ID = "tcat.dia.morphological.opencv.gaussianBlur";
   public static final String IMAGE_MATRIX_PIN = "image_matrix";
   public static final String PARAM_SIGMA_X = "sigma_x";
   public static final String PARAM_SIGMA_Y = "sigma_y";
   public static final String PARAM_KERNEL_SIZE = "kernel_size";
   protected int kernelSize;
   protected double sigmaX, sigmaY;

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      params.put(PARAM_KERNEL_SIZE, Integer.valueOf(kernelSize));
      params.put(PARAM_SIGMA_X, Double.valueOf(sigmaX));
      params.put(PARAM_SIGMA_Y, Double.valueOf(sigmaY));
      return params;
   }

   @Override
   public Callable<?> create(TransformerContext ctx)
   {
      final OpenCvMatrix input = (OpenCvMatrix)ctx.getValue(IMAGE_MATRIX_PIN);
      return new Callable<OpenCvMatrix>()
      {
         @Override
         public OpenCvMatrix call() throws Exception
         {
            Mat mat = input.get();
            Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());
            Imgproc.GaussianBlur(mat, dest, new Size(kernelSize, kernelSize), sigmaX, sigmaY);
            
            return new OpenCvMatrix(dest);

         }
      };

   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      kernelSize = 3;
      sigmaX = 2.0;
      sigmaY = 1.0;

      Object o = data.get(PARAM_KERNEL_SIZE);
      if (o instanceof Integer)
      {
         kernelSize = ((Integer)o).intValue();
      }
      else if (o instanceof String)
      {
         try
         {
            kernelSize = Integer.parseInt((String)o);
         }
         catch (Exception e)
         {
            ERROR_LOGGER.log(Level.SEVERE, "Failed to parse config parameter kernel_size. Expected integer value but found [" + o + "].", e);
         }
      }

      o = data.get(PARAM_SIGMA_X);
      if (o instanceof Double)
      {
         sigmaX = ((Double)o).doubleValue();
      }
      else if (o instanceof String)
      {
         try
         {
            sigmaX = Double.parseDouble((String)o);
         }
         catch (Exception e)
         {
            ERROR_LOGGER.log(Level.SEVERE, "Failed to parse config parameter sigma_x. Expected double value but found [" + o + "].", e);
         }
      }

      o = data.get(PARAM_SIGMA_Y);
      if (o instanceof Double)
      {
         sigmaY = ((Double)o).doubleValue();
      }
      else if (o instanceof String)
      {
         try
         {
            sigmaY = Double.parseDouble((String)o);
         }
         catch (Exception e)
         {
            ERROR_LOGGER.log(Level.SEVERE, "Failed to parse config parameter sigma_y. Expected double value but found [" + o + "].", e);
         }
      }

   }

}
