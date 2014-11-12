package edu.tamu.tcat.dia.morphological;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.datatrax.DataSink;
import edu.tamu.tcat.analytics.datatrax.DataSource;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerFactory;

public class OpenCvErosionTransformer implements TransformerFactory
{
   private Mat kernel;

   public OpenCvErosionTransformer()
   {
      // TODO Auto-generated constructor stub
   }

   @Override
   public Class<?> getSourceType()
   {
      return OpenCvMatrix.class;
   }

   @Override
   public Class<?> getOutputType()
   {
      return OpenCvMatrix.class;
   }

   public void setKernelSize(int sz)
   {
      kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz, sz));
   }
   
   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      // HACK need to read from config data
      setKernelSize(3);
      // TODO allow for size
      // TODO Auto-generated method stub
      
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      return params;
   }
   
   @Override
   public Runnable create(final DataSource<?> source, final DataSink<?> sink)
   {
      return new Runnable()
      {
         
         @Override
         public void run()
         {
            OpenCvMatrix input = (OpenCvMatrix)(source.get());
            Mat mat = input.get();
            Mat dest = new Mat(mat.rows(), mat.cols(), mat.type());
            Imgproc.erode(mat, dest, kernel);
            
            ((DataSink)sink).accept(new OpenCvMatrix(dest));
         }
      };
   }

}
