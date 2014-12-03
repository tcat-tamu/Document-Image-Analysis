package edu.tamu.tcat.dia.morphological;

import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class GaussianBlurOperator
{
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.gaussianBlur");

   // static initializer to load DLL. 
   static
   {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   }

   public static byte boolToByte(boolean b)
   {
      return (byte)(b ? 0 : 1);
   }

   private final BinaryImage input;
   private int kernelSize;
   private double sigmaX, sigmaY;

   public GaussianBlurOperator(BinaryImage source, int kSize, double sigmaX, double sigmaY)
   {
      this.input = source;
      this.kernelSize = kSize;
      this.sigmaX = sigmaX;
      this.sigmaY = sigmaY;

   }

   public BinaryImage run()
   {
      // TODO should probably only do this once.

      int h = input.getHeight();
      int w = input.getWidth();
      Mat sourceImage = new Mat(h, w, CvType.CV_8U);
      Mat destination = new Mat(h, w, CvType.CV_8U);
      try
      {
         sourceImage.put(0, 0, toByteArray(input));
         Imgproc.GaussianBlur(sourceImage, destination, new Size(kernelSize, kernelSize), sigmaX, sigmaY);
         return writeOutput(destination);
      }
      finally
      {
         sourceImage.release();
         destination.release();
      }
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

   private BinaryImage writeOutput(Mat destination)
   {
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(input.getWidth(), input.getHeight());
      logger.fine("Writing to binary array");
      byte[] outImageByteArray = new byte[input.getSize()];
      destination.get(0, 0, outImageByteArray);
      for (int i = 0; i < input.getSize(); i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }

      return output;
   }

}
