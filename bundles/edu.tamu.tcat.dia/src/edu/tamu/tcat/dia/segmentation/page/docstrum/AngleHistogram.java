package edu.tamu.tcat.dia.segmentation.page.docstrum;

import java.util.List;
import java.util.Set;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import edu.tamu.tcat.dia.segmentation.page.docstrum.Polynomial.CriticalPoint;

class AngleHistogram
{
   private static final double halfPi = Math.PI / 2;

   double[] histogram;
   Polynomial fitHistogram;
   double stdDev;

   // orientation parameters
   private double orientation;
   private double upperBound;
   private double lowerBound;
   
   boolean valid = true;
   
   private AngleHistogram(double[] histogram, Polynomial eq)
   {
      this.histogram = histogram;
      this.fitHistogram = eq;
      
      // compute the standard deviation of the histogram. If this is too low (e.g., < 1E-3), then
      // the orientation is unlike to be very clear. eg: .001 vs .0001
      int sz = histogram.length;
      double mean = DoubleStream.of(histogram).parallel().sum() / sz;
      double sumSq = DoubleStream.of(histogram)
            .reduce(0.0, (memo, x) -> memo + Math.pow(mean - x, 2));
      stdDev = Math.sqrt(sumSq / sz);
      
      if (stdDev < 1E-3)
      {
         valid = false;
         return;
      }
      
      // calculate orientation
      List<CriticalPoint> points = fitHistogram.findCriticalPoints(0, histogram.length, 1);
      if (points.size() < 3)
      {
         valid = false;
         return;
      }
      
      // TODO this is in the range 0 - 360 -- need to map back to -PI/2, PI/2
      this.orientation = toRadian(points.get(1).point);
      this.lowerBound = toRadian(points.get(0).point);
      this.upperBound = toRadian(points.get(2).point);
   }
   
   /**
    * Convert a x value on the histogram to an angle in the range [-PI/2, PI/2]
    * @param x
    * @return
    */
   public final double toRadian(double x)
   {
      return ((double)x / histogram.length) * Math.PI - halfPi;
   }

   public double getStdDev()
   {
      return stdDev;
   }
   
   /**
    * Estimates whether the supplied angle is within the normal  within-line rotation 
    * of this histogram.
    * 
    * @param theta
    * @return
    */
   public boolean isWithinLine(ComponentNeighbors.AdjacentCC adj)
   {
      double tolerance = Math.PI / 10;
      double theta = toNormalizedAngle(adj);
      return theta > (orientation - tolerance) && theta < (orientation + tolerance);
   }
   
   public boolean isBetweenLine(ComponentNeighbors.AdjacentCC adj)
   {
      double perpendicular = orientation + halfPi;
      double tolerance = Math.PI / 10;

      double theta = toNormalizedAngle(adj);
      if (theta < orientation)
         theta += Math.PI;
      
      return theta > (perpendicular - tolerance) && theta < (perpendicular + tolerance);
   }
   
//   public BufferedImage plot()
//   {
//      int width = 400;
//      int height = 400;
//      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//      WritableRaster raster = image.getRaster();      // TODO use a Graphics2D?
//      Docstrum.initializeBackground(raster);
//   
//      Graphics2D g = image.createGraphics();
//      g.setColor(Color.black);
//      int barWidth = width / histogram.length;
//      
//      for (Polynomial.CriticalPoint cp : fitHistogram.findCriticalPoints(0, 360, 1))
//      {
//         g.drawLine((int)cp.point, 0, (int)cp.point, height);
//      }
//      
//      for (int x = 1; x < histogram.length; x++)
//      {
//         double y = fitHistogram.applyAsDouble(x);
//         
//         int a = (int)(y * height * 20);
//         int barHeight = Math.min(a, height);      // HACK: scale by 20 for better display
//
//         g.fillRect(x, height - barHeight, barWidth, barHeight);
//      }
//      
//      
//      image.flush();
//      return image;
//   }

   public static AngleHistogram create(Set<ComponentNeighbors> adjTable) 
   {
      // map theta from -PI to PI to - PI / 2 to PI / 2 
      double[] angles = adjTable.parallelStream()
                           .flatMap(neighbors -> neighbors.neighbors.stream())
                           .mapToDouble(AngleHistogram::toNormalizedAngle)
                           .toArray();
      
      double[] h = computeAngleHistogram(angles, 360);          // HACK: hard coded 180 deg. at .5 degree resolution
      
      Polynomial bestFit = Polynomial.fit(h, 5);
      
      return new AngleHistogram(h, bestFit);
   }

   /**
    * Maps the angle between two adjacent connected components from {@code [-PI, PI]} to 
    * {@code [-PI/2, PI/2]}.
    * 
    * @param adj The adjacent components.
    * @return The mapped angle.
    */
   private static double toNormalizedAngle(ComponentNeighbors.AdjacentCC adj)
   {
      return (adj.theta > halfPi) ? adj.theta - Math.PI 
                              : (adj.theta < -halfPi) ? adj.theta + Math.PI 
                              : adj.theta;
   }
   
   private static double[] smoothAndNormalize(int[] histogram, double alpha, int numElements)
   {
      int nbins = histogram.length;
      int paddingSize = (int)Math.floor(alpha * nbins / 2);
      int windowSize = 2 * paddingSize;
      
   // compute integral of the histogram with padding before and after to allow the 
      // histogram to wrap around the end
      int[] iHistogram = new int[nbins + windowSize];
      iHistogram[0] = histogram[nbins - paddingSize];
      for (int i = 1; i < iHistogram.length; i++)
      {
         int index = (nbins - paddingSize + i) % nbins; 
         iHistogram[i] = iHistogram[i - 1] + histogram[index];
      }
      
      // divide by window size for smoothing
      // divide by nbins to normalize histogram in range 0..1 
      double denominator = numElements * windowSize;  
      return IntStream.range(0,  nbins)
            .parallel()
            .mapToDouble(i -> (iHistogram[i + windowSize] - iHistogram[i]) / denominator)
            .toArray();
   }

   private static double[] computeAngleHistogram(double[] angles, int nbins)
   {
      final double halfPi = Math.PI / 2;
      final double binSize = Math.PI / nbins; 
      int[] histogram = DoubleStream.of(angles).collect(
            () -> new int[nbins], 
            (histogramMemo, theta) -> { 
               // NOTE: rotate from [-PI/2, PI/2] to [0, PI] and linear map to [0, nbins)  
               int ix = (int)Math.floor((theta + halfPi) / binSize);
               if (ix == nbins)
                  ix = 0;
               histogramMemo[ix] = histogramMemo[ix] + 1;
            }, 
            (a, b) -> IntStream.range(0, nbins).parallel().map(i -> a[i] + b[i]).toArray()
      );
      
      
      double alpha = 0.25;    // size of smoothing window
      return smoothAndNormalize(histogram, alpha, angles.length);
   }
}