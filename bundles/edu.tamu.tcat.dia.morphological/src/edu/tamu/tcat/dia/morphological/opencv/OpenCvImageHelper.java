package edu.tamu.tcat.dia.morphological.opencv;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Objects;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

/**
 * Methods to aid in converting between {@link BinaryImage}s and OpenCV constructs.
 */
public abstract class OpenCvImageHelper
{

   public static BinaryImage toBinaryImage(Mat destination)
   {
      int w = destination.width();
      int h = destination.height();
      int sz = w * h;
      
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(w, h);
      byte[] outImageByteArray = new byte[sz];
      destination.get(0, 0, outImageByteArray);
      for (int i = 0; i < sz; i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }

      return output;
   }
   
   public static Mat toMatrix(BinaryImage image)
   {
      int h = image.getHeight();
      int w = image.getWidth();
      Mat matrix = new Mat(h, w, CvType.CV_8U);
      matrix.put(0, 0, toByteArray(image));
      
      return matrix;
   }
   
   private static int getCvType(int bufferedImageType)
   {
      // TODO implement in order to get the appropriate image type
      switch (bufferedImageType)
      {
         case BufferedImage.TYPE_3BYTE_BGR:
            return CvType.CV_8UC3;
         case BufferedImage.TYPE_4BYTE_ABGR:
            return CvType.CV_8UC4;
//         case BufferedImage.TYPE_BYTE_BINARY:
//            break;
         case BufferedImage.TYPE_BYTE_GRAY:
            return CvType.CV_8U;
         default:
            throw new IllegalArgumentException("Unsupported image type [" + bufferedImageType + "]");
      }
   }
   
   private static void writeTo(DataBufferByte buffer, Mat matrix)
   {
      byte[] data = buffer.getData();
      matrix.put(0, 0, data);
   }
   
   private static void writeTo(DataBufferInt buffer, Mat matrix)
   {
      int[] data = buffer.getData();
      matrix.put(0, 0, data);
   }
   
   /**
    * Constructs a new {@link Mat} that is compatible with the supplied {@link BufferedImage} 
    * in terms of type and size.
    * @param image
    * @return Must be relaeased
    * @throws IllegalArgumentException If the supplied image does not correspond to an
    *       supported {@link CvType}.
    */
   public static Mat newMatrix(BufferedImage image)
   {
      int w = image.getWidth();
      int h = image.getHeight();
      
      return new Mat(h, w, getCvType(image.getType()));
   }
   
   /**
    * 
    * @param image 
    * @return Must be released by caller once no longer needed.
    */
   public static Mat toMatrix(BufferedImage image)
   {
      Mat matrix;
      try {
         matrix = newMatrix(image);
      } catch (IllegalArgumentException ex) {
         // try to force the image into a known supported format
         image = toBufferedImageOfType(image, BufferedImage.TYPE_3BYTE_BGR);
         matrix = newMatrix(image);
      }
      
      DataBuffer dataBuffer = image.getRaster().getDataBuffer();
      if (dataBuffer instanceof DataBufferByte)
         writeTo((DataBufferByte)dataBuffer, matrix);
      if (dataBuffer instanceof DataBufferInt)
         writeTo((DataBufferInt)dataBuffer, matrix);
      
      throw new IllegalArgumentException("Unsupported image type [" + dataBuffer.getClass() + "]");
   }

   public static BufferedImage toBufferedImage(Mat mat) {
      // Adapted From http://stackoverflow.com/questions/18424892/bufferedimage-into-opencv-mat-in-java?lq=1
      int t = mat.type();
      BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster raster = image.getRaster();
      
      DataBuffer buffer = raster.getDataBuffer();
      if (!(buffer instanceof DataBufferByte))
         throw new IllegalStateException("Invalid data buffer instance. Expected instance of [" + DataBufferByte.class + "] but found [" + buffer.getClass() + "]");
      
      byte[] data = ((DataBufferByte)buffer).getData();
      mat.get(0, 0, data);
      return image;
   }

   /**
    * Converts the supplied image to grayscale if required. If the image has only a single 
    * color band, it is returned un-modified. Otherwise a new grayscaled version of the 
    * image will be generated. 
    * 
    * @param image The image to convert.
    * @return A grayscale version of that image.
    */
   public static BufferedImage toGrayscale(BufferedImage image)
   {
      if (image.getData().getNumBands() == 1)
         return image;
      
         // convert to grayscale.
         ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
         ColorConvertOp op = new ColorConvertOp(colorSpace, null);
         return op.filter(image, null);
   }
   
   /**
    * 
    * @param original
    * @param type
    * @return
    */
   public static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
      // From http://stackoverflow.com/questions/21740729/converting-bufferedimage-to-mat-opencv-in-java?lq=1
      Objects.requireNonNull(original, "Cannot convert image. No value supplied.");

      // Don't convert if it already has correct type
      if (original.getType() == type) 
         return original;

      // Create a buffered image
      BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);

      // Draw the image onto the new buffer
      Graphics2D g = image.createGraphics();
      try {
         g.setComposite(AlphaComposite.Src);
         g.drawImage(original, 0, 0, null);
      }
      finally {
         g.dispose();
      }

      return image;
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
