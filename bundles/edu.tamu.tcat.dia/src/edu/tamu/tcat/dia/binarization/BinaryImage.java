package edu.tamu.tcat.dia.binarization;

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
