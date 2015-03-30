package edu.tamu.tcat.analytics.ml.cluster;

import java.util.stream.DoubleStream;

public class GaussianStats
{
   private final DoubleStream.Builder values = DoubleStream.builder();
   
   private int ct = 0;
   private double sum = 0;
   
   public GaussianStats()
   {
      // TODO Auto-generated constructor stub
   }
   
   public synchronized void add(double... observation)
   {
      ct++;
      sum += observation[0];
      values.accept(observation[0]);
   }

   public GaussianModel computeModel()
   {
      double mean = sum / ct;
      double sigma = values.build()
               .map(x -> Math.pow(x - mean, 2))
               .reduce(Double::sum)
               .orElse(Double.valueOf(0));
      sigma = Math.sqrt(sigma / ct);
      
      return new GaussianModel(mean, sigma);
   }
   
   public static class GaussianModel
   {
      public final double mean;
      public final double sigma;
      
      public GaussianModel(double mean, double sigma)
      {
         this.mean = mean;
         this.sigma = sigma;
      }
      
      public double likelihood(double... observation)
      {
         double exponent = Math.pow((observation[0] - mean), 2) / (2 * Math.pow(sigma, 2));
         return Math.exp(-exponent) / (sigma * Math.sqrt(2 * Math.PI)); 
      }
      
      public double distance(GaussianModel model)
      {
         // SEE this
         // http://stats.stackexchange.com/questions/12209/percentage-of-overlapping-regions-of-two-normal-distributions
         // HACK: for now, we'll just return how much the mean moved.
         //       should compute difference between areas under the curve
         return Math.abs(model.mean - mean);
      }
   }
}
