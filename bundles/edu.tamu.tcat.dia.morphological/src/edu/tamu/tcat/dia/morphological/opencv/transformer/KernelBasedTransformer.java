package edu.tamu.tcat.dia.morphological.opencv.transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.dia.morphological.opencv.OpenCvMatrix;

public abstract class KernelBasedTransformer implements Transformer
{
   private static final Logger ERROR_LOGGER = Logger.getAnonymousLogger("edu.tamu.tcat.dia.morphological.datatrax.errors");
   
   public static final String PARAM_KERNEL_SIZE = "kernel_size";
   protected int kernalSize;

   public KernelBasedTransformer()
   {
      super();
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      kernalSize = 3;
      Object o = data.get(PARAM_KERNEL_SIZE);
      if (o instanceof Integer)
      {
         kernalSize = ((Integer)o).intValue();
      }
      else if (o instanceof String)
      {
         try 
         {
            kernalSize = Integer.parseInt((String)o);
         }
         catch (Exception e)
         {
            ERROR_LOGGER.log(Level.SEVERE, "Failed to parse config parameter kernel_size. Expected integer value but found [" + o + "].", e);
         }
      }
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      params.put(PARAM_KERNEL_SIZE, Integer.valueOf(kernalSize));
      
      return params;
   }
   
   protected OpenCvMatrix getKernel()
   {
      Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(kernalSize, kernalSize));
      OpenCvMatrix k = new OpenCvMatrix(kernel);
      return k;
   }

}
