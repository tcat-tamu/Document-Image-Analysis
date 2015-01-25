package edu.tamu.tcat.dia.morphological.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public class BaseMorphologicalOperator
{
   // static initializer to load DLL. 
   static {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   }
   
   protected Mat input;
   protected Mat destination;
   protected Mat structuringElement;
   private boolean shouldCloseImage = false;
   private boolean shouldCloseStrucElem = true;

   /**
    * No need for a binary image, this should be grayscale.
    * @param image
    */
   public void setInput(BinaryImage image)
   {
      closeInputImage();
   
      this.input = OpenCvImageHelper.toMatrix(image);
      shouldCloseImage = true;
   }

   public void setInput(Mat matrix)
   {
      closeInputImage();
   
      this.input = matrix;
      shouldCloseImage = false;
   }

   public void setStructuringElement(int sz)
   {
      closeStructuringElement();
   
      structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz, sz));
      shouldCloseStrucElem = true;
   }

   /**
    * Sets the structuring element. Note that the supplied {@link Mat} will not be closed
    * when this dilation operation is finished.
    * 
    * @param elem
    */
   public void setStructuringElement(Mat elem)
   {
      closeStructuringElement();
   
      structuringElement = elem;
      shouldCloseStrucElem = false;
   }

   public BinaryImage getResult()
   {
      if (destination == null)
         throw new IllegalStateException("Cannot retrieve output data. No results have been generated.");
   
      // FIXME should write to BufferedImage
      int width = destination.width();
      int height = input.height();
      int sz = width * height;
   
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(width, height);
   
      byte[] outImageByteArray = new byte[sz];
      destination.get(0, 0, outImageByteArray);
      for (int i = 0; i < sz; i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }
   
      return output;
   }

   public void close()
   {
      closeInputImage();
      closeStructuringElement();
   
      destination.release();
   }

   private void closeInputImage()
   {
      if (shouldCloseImage && input != null)
      {
         input.release();
         input = null;
      }
   }

   private void closeStructuringElement()
   {
      if (shouldCloseStrucElem && structuringElement != null)
      {
         structuringElement.release();
         structuringElement = null;
      }
   }

}
