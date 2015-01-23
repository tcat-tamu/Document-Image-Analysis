package edu.tamu.tcat.dia.morphological.opencv;

import java.util.Objects;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;
import edu.tamu.tcat.dia.morphological.DilationOperator;
import edu.tamu.tcat.dia.morphological.MorphologicalOperationException;

/**
 *  Provides an implementation of the dilation command based on the OpenCV library. 
 *  
 *  <p>
 *  Note that this implementation is not tread safe.
 */
public final class OpenCVDilationOperator implements DilationOperator {
   private final static Logger logger = Logger.getLogger(OpenCVDilationOperator.class.getName());

   // static initializer to load DLL. 
   static {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   }

   public static final int defaultSize = 3;

   private Mat input;
   private Mat structuringElement;
   private boolean shouldCloseImage = false;
   private boolean shouldCloseStrucElem = true;

   private Mat destination;



   // NOTE not thread safe.

   /**
    * No need for a binary image, this should be grayscale.
    * @param image
    */
   @Override
   public void setInput(BinaryImage image)
   {
      closeInputImage();

      this.input = BinaryImageHelper.toMatrix(image);
      shouldCloseImage = true;
   }

   public void setInput(Mat matrix)
   {
      closeInputImage();

      this.input = matrix;
      shouldCloseImage = false;
   }


   @Override
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

   /**
    * 
    * Note that this operation may be called multiple times. Subsequent calls will perform
    * the same dilation operation on the ouptut of the previous call.
    * 
    * @return
    * @throws MorphologicalOperationException
    */
   @Override
   public void execute() throws MorphologicalOperationException
   {
      Objects.requireNonNull(input);
      Objects.requireNonNull(structuringElement);
      int h = this.input.height();
      int w = this.input.width();

      destination = new Mat(h, w, CvType.CV_8U);
      try 
      {
         Imgproc.dilate(input, destination, structuringElement);
         setInput(destination);
      }
      catch (Exception ex)
      {
         throw new MorphologicalOperationException("Image dilation failed", ex);
      }
   }

   @Override
   public BinaryImage getResult()
   {
      if (destination == null)
         throw new IllegalStateException("Cannot retrieve output data. No results have been generated.");

      // FIXME should write to BufferedImage
      int width = destination.width();
      int height = input.height();
      int sz = width * height;

      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(width, height);
      logger.fine("Writing to binary array");

      byte[] outImageByteArray = new byte[sz];
      destination.get(0, 0, outImageByteArray);
      for (int i = 0; i < sz; i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }

      return output;
   }

   @Override
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
