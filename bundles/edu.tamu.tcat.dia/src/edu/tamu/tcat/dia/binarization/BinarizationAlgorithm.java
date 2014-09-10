package edu.tamu.tcat.dia.binarization;


/**
 * Converts an image into a pure black and white representation of that image. This is 
 * frequently an early-stage pre-processing step within document image analysis systems. For 
 * modern documents that have been digitized under controlled settings, this is typically a 
 * relatively straight-forward process. For documents with historical damage and/or poorly 
 * controlled digitization environments this may be quite complex and a number of algorithms
 * have been developed to support this task. These frequently trade off quality of the resulting 
 * binarization with the speed of the algorithm.
 *  
 * @param <Image> The input image type. This will frequently be a 
 *    {@link java.awt.image.BufferedImage} or  {@link java.awt.image.Raster}, but the specific 
 *    image type is left to implementations to enable support for multiple input data types.
 *    Implementations may supply a thin-wrapper around the core algorithm to handle the task of 
 *    converting multiple types of image input into a format suitable for processing.
 */
public interface BinarizationAlgorithm<Image>
{
   /**
    * @return The type of image object accepted as input. Useful to guide applications as to 
    *    which of several different binarization implementations are applicable to specific 
    *    data formats.  
    */
   Class<Image> getInputType();
   
   /**
    * 
    * @param image
    * @return
    * @throws BinarizationException
    */
   BinaryImage binarize(Image image) throws BinarizationException;

}
