package edu.tamu.tcat.dia.morphological.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.tamu.tcat.dia.binarization.BinaryImage;

/**
 * Methods to aid in converting between {@link BinaryImage}s and OpenCV constructs.
 */
public abstract class BinaryImageHelper
{

   public static Mat toMatrix(BinaryImage image)
   {
      int h = image.getHeight();
      int w = image.getWidth();
      Mat matrix = new Mat(h, w, CvType.CV_8U);
      matrix.put(0, 0, toByteArray(image));
      
      return matrix;
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
   
   public static byte boolToByte(boolean b) {
      return (byte) (b ? 0 : 1);
  }
}
