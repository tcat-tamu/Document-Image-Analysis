package edu.tamu.tcat.dia.segmentation.page.docstrum;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.analytics.image.region.SimpleBoundingBox;
import edu.tamu.tcat.analytics.image.region.SimplePoint;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

/**
 * A line of text is defined, in this case, as a collection of connected components  
 *
 */
public class Line
{
   private final static double halfPi = Math.PI / 2;
   
   private final int sequence;
   private final List<ConnectedComponent> components;
   
   private final Polynomial fitline;
   private final BoundingBox bounds;
   
   private final Point start;
   private final Point end; 
   
   public Line(List<ConnectedComponent> components, int seqId)
   {
      this.components = new ArrayList<>(components);
      this.sequence = seqId;

      PolynomialCurveFitter fitter = PolynomialCurveFitter.create(1);
      List<WeightedObservedPoint> points = components.stream().map(cc -> { 
         Point centroid = cc.getCentroid();
         return new WeightedObservedPoint(1, centroid.getX(), centroid.getY());
      })
      .collect(Collectors.toList());
      
      // compute centroid bounding box and line bounding box
      int[] lineBounds = new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      int xMin = Integer.MAX_VALUE;
      int xMax = Integer.MIN_VALUE;
      for (ConnectedComponent cc : components)
      {
         BoundingBox box = cc.getBounds();
         lineBounds[0] = Math.min(lineBounds[0], box.getLeft());
         lineBounds[1] = Math.min(lineBounds[1], box.getTop());
         lineBounds[2] = Math.max(lineBounds[2], box.getRight());
         lineBounds[3] = Math.max(lineBounds[3], box.getBottom());
         
         Point centroid = cc.getCentroid();
         xMin = Math.min(xMin, centroid.getX());
         xMax = Math.min(xMax, centroid.getX());
      }
      
      bounds = new SimpleBoundingBox(lineBounds[0], lineBounds[1], lineBounds[2], lineBounds[3]);
      fitline = new Polynomial(fitter.fit(points));
      
      int y = (int)Math.round(fitline.applyAsDouble(xMin));
      start = new SimplePoint(xMin, y);
      
      y = (int)Math.round(fitline.applyAsDouble(xMax));
      end = new SimplePoint(xMax, y);
   }
   
   public int getId()
   {
      return sequence;
   }
   
   public Point getStart() 
   {
      return start;
   }
   
   public Point getEnd()
   {
      return end;
   }

   public BoundingBox getBounds()
   {
      return bounds;
   }

   /** 
    * Convert the supplied angle in the range {@code [PI, PI]} to the range {@code [-PI/2, P/2]}
    * @param theta
    * @return 
    */
   private static double normalize(double theta)
   {
      return (theta > halfPi) ? theta - Math.PI : (theta < -halfPi) ? theta + Math.PI : theta;
   }
   
   /**
    * @return The angle of this line from the horizontal axis. In radians in the range
    *       {@code [-PI/2, P/2]}
    */
   public double getOrientation()
   {
      int dx = end.getX() - start.getX();
      int dy = end.getY() - start.getY();
      
      double theta = Math.atan2(dy, dx);
      return normalize(theta);
   }

   /**
    * @return The angle between the supplied line and this line. In radians in the range
    *       {@code [-PI/2, P/2]}
    */
   public double angularDifference(Line l)
   {
      double theta = l.getOrientation() - this.getOrientation();
      return normalize(theta);
   }
   
   public void drawCenterLine(Graphics g)
   {
      g.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
   }
   
   public void drawBox(Graphics g)
   {
      g.drawRect(bounds.getLeft(), bounds.getTop(), bounds.getWidth(), bounds.getHeight());
   }
}