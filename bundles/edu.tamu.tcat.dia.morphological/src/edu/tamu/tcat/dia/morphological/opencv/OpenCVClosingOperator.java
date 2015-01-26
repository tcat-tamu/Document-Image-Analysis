package edu.tamu.tcat.dia.morphological.opencv;

import java.util.Objects;
import java.util.logging.Logger;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.morphological.ErosionOperator;
import edu.tamu.tcat.dia.morphological.MorphologicalOperationException;

/**
 *  Provides an implementation of the opening operation based on the OpenCV library. 
 *  
 *  <p>
 *  Note that this implementation is not tread safe.
 */
public final class OpenCVClosingOperator extends BaseMorphologicalOperator implements ErosionOperator {
   final static Logger logger = Logger.getLogger(OpenCVClosingOperator.class.getName());

   public static final int defaultSize = 3;

   // NOTE not thread safe.

   /**
    * 
    * @return
    * @throws MorphologicalOperationException
    */
   @Override
   public void execute() throws MorphologicalOperationException
   {
      Objects.requireNonNull(input);
      Objects.requireNonNull(structuringElement);
      int h = input.height();
      int w = input.width();

      Mat intermediate = new Mat(h, w, CvType.CV_8U);
      destination = new Mat(h, w, CvType.CV_8U);
      try 
      {
         Imgproc.dilate(input, intermediate, structuringElement);
         Imgproc.erode(intermediate, destination, structuringElement);
      }
      catch (Exception ex)
      {
         throw new MorphologicalOperationException("Image dilation failed", ex);
      }
      finally 
      {
         intermediate.release();
      }
   }
}
