package edu.tamu.tcat.dia.morphological;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.morphological.opencv.util.BufferedImageAdapterStrategy;
import edu.tamu.tcat.dia.morphological.opencv.util.StandardBufferedImageAdapters;

public class GaussianBlurOperator
{
   static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); } // static initializer to load DLL. 

   private BufferedImageAdapterStrategy getStrategy(BufferedImage input)
   {
      for (StandardBufferedImageAdapters adapter : StandardBufferedImageAdapters.values())
      {
         if (input.getType() == adapter.getBufferedImageType())
            return adapter;
      }
      
      return StandardBufferedImageAdapters.BGR_3BYTE;    // Use this as a default. Will force conversion of the imput image.
   }
   
   public BufferedImage blur(BufferedImage input, int kSize, double sigmaX, double sigmaY) throws Exception
   {
      if (kSize < 0 || kSize % 2 != 1)
         throw new IllegalArgumentException("The supplied kernel size [" + kSize + "] must be positive and odd.");
      
      try 
      {
         Mat sourceImage = null;  
         Mat destination = null;
         try
         {
            // TODO ensure that this is a valid type. See doc on GaussianBlur method below
            BufferedImageAdapterStrategy strategy = getStrategy(input);
            sourceImage = strategy.adapt(input);
            destination = strategy.fromTemplate(input);

            // TODO need to learn how these work better and document
            Imgproc.GaussianBlur(sourceImage, destination, new Size(kSize, kSize), sigmaX, sigmaY);

            return strategy.adapt(destination);
         }
         finally
         {
            if (sourceImage != null)
               sourceImage.release();

            if (destination != null)
               destination.release();
         }
      }
      catch (Exception ex)
      {
         throw new Exception("", ex);
      }
   }
}
