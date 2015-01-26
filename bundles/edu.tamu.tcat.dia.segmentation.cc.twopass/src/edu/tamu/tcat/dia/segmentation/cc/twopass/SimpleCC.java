package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.BoxUtils;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.analytics.image.region.SimpleBoundingBox;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

/**
 * Note that this class is not immutable 
 */
public class SimpleCC implements ConnectedComponent {

	// TODO make this immutable -- create a builder
	private final Set<Point> points = new HashSet<>();
	private int xMin = -1;
	private int xMax = -1;
	private int yMin = -1;
	private int yMax = -1;
   private int sequence;

	public SimpleCC() {
	}

	@Override
   public BoundingBox getBounds()
   {
	   return new SimpleBoundingBox(xMin, yMin, xMax, yMax);
   }

   @Override
   public Set<Point> getPoints()
   {
      return Collections.unmodifiableSet(points);
   }

   @Override
   public int getNumberOfPixels()
	{
	   return points.size();
	}
	
	@Override
   public int getSequence()
   {
      return sequence;
   }
	
	@Override
	public boolean intersects(ConnectedComponent cc)
	{
	   if (!BoxUtils.intersects(getBounds(), cc.getBounds()))
	      return false;
	   
	   return (cc instanceof SimpleCC) 
	         ? intersectsInternal((SimpleCC)cc) 
            : intersectsGeneral(cc);
	}
	
	private boolean intersectsGeneral(ConnectedComponent cc)
	{
	   throw new UnsupportedOperationException();
	}
	
	private boolean intersectsInternal(SimpleCC cc)
	{
	   for (Point p : points)
	   {
	      if (cc.points.contains(p))
	         return true;
	   }
	   
	   return false;
	}
	
	void add(int x, int y)
	{
		if (xMin < 0 || xMin > x)
			xMin = x;
		if (xMax < 0 || xMax < x)
			xMax = x;
		if (yMin < 0 || yMin > y)
			yMin = y;
		if (yMax < 0 || yMax < y)
			yMax = y;

		points.add(new PointImpl(x, y));
	}
	
	void setSequence(int i)
	{
	   this.sequence = i;
	}
	
	private static class PointImpl implements Point 
	{
	   private final int y;
      private final int x;

      public PointImpl(int x, int y)
      {
         this.x = x;
         this.y = y;
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
	   
      @Override
      public boolean equals(Object obj)
      {
         if (!(obj instanceof Point))
            return false;
         
         Point p = (Point)obj;
         return this.x == p.getX() && this.y == p.getY();
      }
      
      @Override
      public int hashCode()
      {
         int result = 17;
         result = 37 * result + x;
         result = 37 * result + y;
         return result;
      }
	}
}
