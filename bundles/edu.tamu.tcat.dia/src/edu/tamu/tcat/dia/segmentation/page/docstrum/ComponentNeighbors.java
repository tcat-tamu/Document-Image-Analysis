package edu.tamu.tcat.dia.segmentation.page.docstrum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.common.primitives.Doubles;

import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class ComponentNeighbors
{
   ConnectedComponent cc;
   List<ComponentNeighbors.AdjacentCC> neighbors = new ArrayList<>();
   
   ComponentNeighbors(ConnectedComponent ref, Set<ConnectedComponent> components, int k) 
   {
      this.cc = ref;
      final Point px = ref.getCentroid();
      SortedSet<ConnectedComponent> sorted = new TreeSet<>((a, b) -> {
         double aDist = distance(px, a.getCentroid());
         double bDist = distance(px, b.getCentroid());
         
         return Doubles.compare(aDist, bDist);
      });
      sorted.addAll(components);
      sorted.remove(ref);

      List<ComponentNeighbors.AdjacentCC> sortedNeighbors = sorted.stream().limit(k)
               .map(cc -> {
                  Point centroid = cc.getCentroid();
                  
                  ComponentNeighbors.AdjacentCC adjacenctCC = new AdjacentCC();
                  adjacenctCC.cc = cc;
                  adjacenctCC.dist = distance(px, centroid);
                  adjacenctCC.theta = angle(px, centroid);
                  
                  return adjacenctCC;
               })
               .collect(Collectors.toList());
      
      this.neighbors = Collections.unmodifiableList(sortedNeighbors);
   }
   
   private static double distance(Point a, Point b)
   {
      int x = a.getX() - b.getX();
      int y = a.getY() - b.getY();
      
      return Math.sqrt(x * x + y * y);
   }
   
   private static double angle(Point a, Point b)
   {
      int x = b.getX() - a.getX();
      int y = b.getY() - a.getY();
      
      return Math.atan2(y, x);
   }

public static class AdjacentCC implements Comparable<ComponentNeighbors.AdjacentCC> 
   {
      ConnectedComponent cc;
      double dist;
      double theta;
      
      @Override
      public int compareTo(ComponentNeighbors.AdjacentCC other)
      {
         return Doubles.compare(dist, other.dist);
      }
      
   }
}