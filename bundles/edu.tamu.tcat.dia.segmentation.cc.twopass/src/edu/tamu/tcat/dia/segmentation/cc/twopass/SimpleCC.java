package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.analytics.image.region.SimpleBoundingBox;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

/**
 * Note that this class is not immutable 
 */
public class SimpleCC implements ConnectedComponent {

	public static void write(SimpleCC cc, WritableRaster raster, int[] color)
	{
		int bands = color.length;
		for (int[] point : cc.points)
		{
			int x = point[0];
			int y = point[1];
			for (int b = 0; b < bands; b++) {
				raster.setSample(x, y, b, color[b]);
			}
		}
	}

	// TODO make this immutable -- create a builder
	private final List<int[]> points = new ArrayList<>();
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
      // TODO Auto-generated method stub
      return null;
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

		points.add(new int[] {x, y});
	}
	
	void setSequence(int i)
	{
	   this.sequence = i;
	}
}
