package edu.tamu.tcat.analytics.image.binary;

public interface BinaryImage
{
   int getWidth();
   
   int getHeight();
   
   /**
    * @return The size of this image in number of pixels. This will equal
    *    {@code getWidth() * getHeight()}. 
    */
   int getSize();
   
   boolean isForeground(int w, int h) throws IllegalArgumentException;

   boolean isForeground(int ix) throws IllegalArgumentException;
}
