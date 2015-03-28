package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.BoxUtils;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.analytics.image.region.SimplePoint;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class ImutableCC implements ConnectedComponent
{
   // TODO project into hough space. -- more generally, allow for projections.
   
   private int seqId;
   private BoundingBox box;
   private Set<Point> pixels;
   private Point center;
   private Point centroid;

   public ImutableCC(int seqId, BoundingBox box, Set<Point> pixels, Point centroid)
   {
      this.seqId = seqId;
      int x = box.getWidth() / 2 + box.getLeft();
      int y = box.getHeight() / 2 + box.getTop();
      this.box = box;
      this.pixels = Collections.unmodifiableSet(new HashSet<>(pixels));

      this.center = new SimplePoint(x, y);
      this.centroid = centroid;
   }
   
   @Override
   public int getSequence()
   {
      return seqId;
   }

   @Override
   public BoundingBox getBounds()
   {
      return box;
   }

   @Override
   public Set<Point> getPoints()
   {
      return pixels;
   }

   @Override
   public Point getCenter()
   {
      return center;
   }

   @Override
   public Point getCentroid()
   {
      return centroid;
   }

   @Override
   public int getNumberOfPixels()
   {
      return pixels.size();
   }

   @Override
   public boolean intersects(ConnectedComponent cc)
   {
      if (!BoxUtils.intersects(getBounds(), cc.getBounds()))
         return false;
      
      Set<Point> otherPoints = cc.getPoints();
      return otherPoints.parallelStream().anyMatch(p -> this.pixels.contains(p));
   }
}
