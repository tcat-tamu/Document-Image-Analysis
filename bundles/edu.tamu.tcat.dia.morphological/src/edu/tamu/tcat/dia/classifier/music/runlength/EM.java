package edu.tamu.tcat.dia.classifier.music.runlength;

import java.util.Collection;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class EM 
{
   private final Collection<Double> values;
   private final Set<EM.Cluster> clusters;
   
   EM(Collection<Double> values, int numClusters)
   {
      this.values = values;
      DoubleSummaryStatistics stats = values.parallelStream()
            .collect(DoubleSummaryStatistics::new,
                  DoubleSummaryStatistics::accept,
                  DoubleSummaryStatistics::combine);
      
      double sumSq = values.parallelStream()
                        .map(x -> Math.pow(x.doubleValue() - stats.getAverage(), 2))
                        .reduce(Double::sum)
                        .orElse(Double.valueOf(0));
      double sigma = Math.sqrt(sumSq / values.size());
      double min = stats.getMin();
      double max = stats.getMax();
      
      Random random = new Random();
      clusters = new HashSet<>();
      for (int i = 0; i < numClusters; i++)
      {
         Cluster c = new Cluster();
         c.mean = (max - min) * random.nextDouble() + min;
         c.sigma = sigma;
         clusters.add(c);
      }
   }
   
   public Set<Cluster> estimate() 
   {
      int numSteps = 0;
      do {
         numSteps++;
         clusters.parallelStream().forEach(Cluster::clearValues);

         expectation();
         maximize();
         
      } while (numSteps < 100);      // HACK check for convergence
      
      return Collections.unmodifiableSet(clusters);
   }
   
   private void expectation()
   {
      values.forEach(x -> {
         // find best fit cluster (i.e. max likelihood)
         Cluster best = clusters.parallelStream()
                             .reduce((memo, c) -> memo.likelihood(x) >= c.likelihood(x) ? memo : c)
                             .orElse(null);
         Objects.requireNonNull(best, "Failed to find matching cluster");
         best.add(x);
      });
   }
   
   private void maximize()
   {
      clusters.parallelStream().forEach(Cluster::update);
   }
   
   public static class Cluster
   {
      Set<Double> values = new HashSet<>();
      double sum = 0;
      private double min;
      private double max;
      
      double mean;
      double sigma;

      void add(double value)
      {
         Double d = Double.valueOf(value);
         values.add(d);
         min = Math.min(min, value);
         max = Math.max(max, value);
         sum += value;
      }
      
      /** Clears the supplied values while leaving the mean and sigma untouched. */
      void clearValues() {
         sum = 0;
         min = Double.MIN_VALUE;
         max = Double.MAX_VALUE;
         
         values.clear();
      }
      
      void update()
      {
         mean = sum / values.size();
         double sigma = values.parallelStream()
                  .map(x -> Math.pow(x.doubleValue() - mean, 2))
                  .reduce(Double::sum)
                  .orElse(Double.valueOf(0));
         sigma = Math.sqrt(sigma / values.size());
      }

      public int size()
      {
         return values.size();
      }
      
      public double mean() 
      {
         return mean;
      }
      
      public double sigma()
      {
         return sigma;
      }
      
      public double likelihood(double x)
      {
         double exponent = Math.pow((x - mean), 2) / (2 * Math.pow(sigma, 2));
         return Math.exp(-exponent) / (sigma * Math.sqrt(2 * Math.PI)); 
      }
   }
}