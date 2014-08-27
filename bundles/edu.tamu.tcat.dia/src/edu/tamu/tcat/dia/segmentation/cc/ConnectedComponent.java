package edu.tamu.tcat.dia.segmentation.cc;

import java.util.Set;

import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;


public interface ConnectedComponent
{
   BoundingBox getBounds();
   
   Set<Point> getPoints();

   int getNumberOfPixels();

   int getSequence();

}
