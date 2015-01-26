package edu.tamu.tcat.dia.morphological.opencv.util;

import java.awt.image.BufferedImage;

import org.opencv.core.Mat;

/**
 * Used to adapt {@link BufferedImage} instances into {@link Mat} instances and vice versa. 
 */
public interface BufferedImageAdapterStrategy
{
   /**
    * @return The image type of the {@link Mat} associated with this strategy. 
    *    This adapter will attempt to produce {@link Mat} instances with the supplied
    *    type regardless of the supplied input values.
    */
   int getMatType();
   
   /**
    * @return The image type of the {@link BufferedImage} associated with this strategy. 
    *    This adapter will attempt to produce {@link BufferedImage} instances with the supplied
    *    type regardless of the supplied input values.
    */
   int getBufferedImageType();
   
   /**
    * Creates a new {@link Mat} based on the supplied image. This will have the same dimensions  
    * as the supplied image and data type as indicated by {@link #getMatType()}. No image data 
    * will be copied into the returned image. 
    * 
    * @param image The image to use as a template.
    * @return The newly created matrix. Must be released by client when no longer in use.
    * @throws IllegalArgumentException If a new {@link Mat} could not be constructed from the 
    *       supplied image.
    */
   Mat fromTemplate(BufferedImage image) throws IllegalArgumentException;
   
   /**
    * Creates a new based on {@link BufferedImage} from the supplied OpenCV matrix. This will  
    * have the same dimensions as the supplied matrix and data type as indicated by 
    * {@link #getBufferedImageType()}. No image data will be copied into the returned image. 
    * 
    * @param image The matrix to use as a template.
    * @return The newly created image. Must be released by client when no longer in use.
    * @throws IllegalArgumentException If a new {@link BufferedImage} could not be constructed 
    *       from the supplied matrix.
    */
   BufferedImage fromTemplate(Mat matrix) throws IllegalArgumentException;
   
   /**
    * Copies the supplied image into a new {@link Mat}. This will have the same dimensions as 
    * the supplied image and data type as indicated by {@link #getMatType()}. If the supplied 
    * image cannot be used directly or converted to an appropriate type, an 
    * {@link IllegalArgumentException} will be thrown.
    * 
    * @param image The image to use as a template.
    * @return The newly created matrix. Must be released by client when no longer in use.
    * @throws IllegalArgumentException If a new {@link Mat} could not be constructed from the 
    *       supplied image.
    */
   Mat adapt(BufferedImage image) throws IllegalArgumentException;
   
   /**
    * Copies the supplied matrix into a new {@link BufferedImage}. This will have the same 
    * dimensions as the supplied image and data type as indicated by 
    * {@link #getBufferedImageType()}. If the supplied image cannot be used directly or 
    * converted to an appropriate type, an {@link IllegalArgumentException} will be thrown.
    * 
    * @param image The matrix to use as a template.
    * @return The newly created image. 
    * @throws IllegalArgumentException If a new {@link BufferedImage} could not be 
    *       constructed from the supplied image.
    */
   BufferedImage adapt(Mat matrix) throws IllegalArgumentException;
}