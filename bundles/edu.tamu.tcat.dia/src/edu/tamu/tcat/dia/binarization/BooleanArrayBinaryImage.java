package edu.tamu.tcat.dia.binarization;


public class BooleanArrayBinaryImage implements BinaryImage
{
   private final boolean[] image;
   private final int width;
   private final int height;
   private final int size;

   public BooleanArrayBinaryImage(int w, int h) {
      this.width = w;
      this.height = h;
      this.size = w * h;
      this.image = new boolean[size];
   }
   
   @Override
   public int getHeight()
   {
      return height;
   }
   
   @Override
   public int getWidth()
   {
      return width;
   }
   
   @Override
   public int getSize()
   {
      return size;
   }
   
   @Override
   public boolean isForeground(int x, int y) throws IllegalArgumentException
   {
      return image[y * this.width + x];
   }
   
   @Override
   public boolean isForeground(int ix) throws IllegalArgumentException
   {
      return image[ix];
   }
   
   public void setForeground(int ix)
   {
      image[ix] = true;
   }
   
   public void setBackground(int ix)
   {
      image[ix] = false;
   }
}
