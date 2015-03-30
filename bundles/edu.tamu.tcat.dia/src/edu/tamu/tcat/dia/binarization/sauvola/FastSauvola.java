package edu.tamu.tcat.dia.binarization.sauvola;

import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.dia.binarization.BinarizationAlgorithm;
import edu.tamu.tcat.dia.binarization.BinarizationException;
import edu.tamu.tcat.dia.binarization.BinaryImage;

public class FastSauvola implements BinarizationAlgorithm<IntegralImage>
{
   private int    ts = -1;       // tile size, will default to width / 15
   private double k  = 0.5;      //
   
   public FastSauvola()
   {
   }

   /**
    * The size of the window used to evaluate local features. This should be set to a value 
    * that is approximately three characters wide. That is, it should be large enough to 
    * capture the variation between the text and background, but small enough that the 
    * background (including any damage to the text) is relatively uniform. By default, this 
    * is set to w / 15, where 'w' is the image width.
    * 
    * @param size The window size for evaluating local features. 
    */
   public void setWindowSize(int size)
   {
      this.ts = size;
   }
   
   /**
    * A weighting factor that adjusts how sensitve the algorithm is to the standard  deviation. 
    * Lower values will result in more pixels being identified as foreground, raising this 
    * value will result in more pixels being identified as background. The default value 0.5 
    * following the recommendation of the original Sauvola paper.
    * 
    * @param k The value for parameter {@code k}
    */
   public void setK(double k)
   {
      this.k = k;
   }
   
   @Override
   public Class<IntegralImage> getInputType()
   {
      return IntegralImage.class;
   }

   @Override
   public BinaryImage binarize(IntegralImage image) throws BinarizationException
   {
      FastSauvolaBinarizer binarizer = new FastSauvolaBinarizer(image, ts, k);
      return binarizer.run();
   }
}
