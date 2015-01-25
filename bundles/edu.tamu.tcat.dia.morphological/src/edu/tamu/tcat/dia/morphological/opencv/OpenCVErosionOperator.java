package edu.tamu.tcat.dia.morphological.opencv;

import java.util.Objects;
import java.util.logging.Logger;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.morphological.ErosionOperator;
import edu.tamu.tcat.dia.morphological.MorphologicalOperationException;

/**
 *  Provides an implementation of the erosion command based on the OpenCV library. 
 *  
 *  <p>
 *  Note that this implementation is not tread safe.
 */
public final class OpenCVErosionOperator extends BaseMorphologicalOperator implements ErosionOperator {
   final static Logger logger = Logger.getLogger(OpenCVErosionOperator.class.getName());

   public static final int defaultSize = 3;

   // NOTE not thread safe.

   /**
    * 
    * Note that this operation may be called multiple times. Subsequent calls will perform
    * the same erosion operation on the ouptut of the previous call.
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

      destination = new Mat(h, w, CvType.CV_8U);
      try 
      {
         Imgproc.erode(input, destination, structuringElement);
         setInput(destination);
      }
      catch (Exception ex)
      {
         throw new MorphologicalOperationException("Image dilation failed", ex);
      }
   }


}
