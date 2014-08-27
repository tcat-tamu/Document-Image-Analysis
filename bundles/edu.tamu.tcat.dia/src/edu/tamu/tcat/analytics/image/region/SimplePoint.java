package edu.tamu.tcat.analytics.image.region;

public final class SimplePoint implements Point
{
   public final int x;
   public final int y;
   
   public SimplePoint(int x, int y)
   {
      this.x = x;
      this.y = y;
   }
   
   public SimplePoint(int[] coord)
   {
      this.x = coord[0];
      this.y = coord[1];
   }
   
   public SimplePoint(java.awt.Point p)
   {
      this.x = p.x;
      this.y = p.y;
   }
   
   @Override
   public int getX()
   {
      return x;
   }
   
   @Override
   public int getY()
   {
      return y;
   }
}
