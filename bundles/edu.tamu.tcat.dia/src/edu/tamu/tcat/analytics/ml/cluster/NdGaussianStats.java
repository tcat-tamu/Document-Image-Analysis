package edu.tamu.tcat.analytics.ml.cluster;



public class NdGaussianStats
{

   public NdGaussianStats()
   {
      // TODO Auto-generated constructor stub
   }
   
   public void add(double[] observation)
   {
      
   }

   public double likelihood(double[] observation)
   {
      throw new UnsupportedOperationException();
   }
   
   public void update()
   {
      
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
   }
}
