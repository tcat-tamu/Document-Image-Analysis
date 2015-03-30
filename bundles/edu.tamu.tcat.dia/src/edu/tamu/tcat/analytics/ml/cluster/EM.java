package edu.tamu.tcat.analytics.ml.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.tamu.tcat.analytics.ml.cluster.GaussianStats.GaussianModel;

public class EM<T>
{

   // TODO pass in accumulator factory
   
   private static final int MAX_ITER = 0;
   private final ToDoubleFunction<T> transform;
   private final int k;
   private final double epsilon;

   public EM(int k, double epsilon, ToDoubleFunction<T> transform)
   {
      this.transform = transform;
      this.k = k;
      this.epsilon = epsilon;
   }
   
   private double calculateMean(double[] values)
   {
      double sum = DoubleStream.of(values).reduce(Double::sum).orElse(0);
      return sum / values.length;
   }
   
   private double calculateSigma(double[] values, double mean)
   {
      double sumSq = DoubleStream.of(values)
            .map(x -> Math.pow(x - mean, 2))
            .reduce(Double::sum)
            .orElse(0);
      return Math.sqrt(sumSq / values.length);
   }
   
   public Collection<GaussianModel> buildMixtureModel(Collection<T> data)
   {
      if (data.size() <= k)
         throw new IllegalArgumentException("There must be at least [" + k + "] data points");

      double[] values = data.parallelStream().mapToDouble(transform).toArray();
      
      boolean changed = true;
      double[] outliers = findOutliers(values);
      values = filterOutliers(values);
      if (values.length == 0)
         outliers = values;
      double[] retain = filterOutliers(outliers);
      values = DoubleStream.concat(DoubleStream.of(values), DoubleStream.of(retain)).toArray();
      
      // TODO might start by finding outliers and creating clusters based on them?
      
      List<GaussianModel> updated = initialize(values);
      GaussianStats[] assignments;
      int ct = 0;
      do 
      {
         List<GaussianModel> current = updated;
         
         GaussianStats[] accumulators = new GaussianStats[current.size()];
         for (int i = 0; i < current.size(); i++) {
            accumulators[i] = new GaussianStats();
         }
         
         DoubleStream.of(values).forEach(x -> {
            int ix = selectCluster(current, x);
            accumulators[ix].add(x);
         });
         
         // TODO update model
         updated = updateModel(accumulators);
         assignments = accumulators;
         changed = checkConvergence(current, updated);
      }
      while (changed && ++ct < MAX_ITER);
      
      return Collections.unmodifiableCollection(updated);
   }

   private int selectCluster(List<GaussianModel> current, double x)
   {
      GaussianModel reduce = current.parallelStream().reduce((memo, candidate) -> {
         return (memo.likelihood(x) > candidate.likelihood(x)) ? memo : candidate;
      }).orElse(null);
      
      Objects.requireNonNull(reduce);
      return current.indexOf(reduce);
   }

   private boolean checkConvergence(List<GaussianModel> current, List<GaussianModel> updated)
   {
      double sum = IntStream.range(0, current.size())
         .mapToDouble(ix -> Math.abs(current.get(ix).distance(updated.get(ix))))
         .sum();
      
      return sum > epsilon;
   }

   private List<GaussianModel> updateModel(GaussianStats[] accumulators)
   {
      return Arrays.stream(accumulators)
               .map(acc -> acc.computeModel())
               .collect(Collectors.toList());
   }

   private List<GaussianModel> initialize(double[] values)
   {
//      double[] outliers = findOutliers(values);
      if (values.length < k)
         throw new IllegalArgumentException("Must supply");
      double[] data = filterOutliers(values);
      double mean = calculateMean(data);
      double sigma = calculateSigma(data, mean);
      Arrays.parallelSort(values);
      
      int sz = values.length / k;
      List<GaussianModel> clusters = new ArrayList<>();
      for (int i = 0; i < k; i++)
      {
         double m = values[sz * i]; 
         clusters.add(new GaussianModel(m, sigma));
      }
//      DoubleSummaryStatistics stats = DoubleStream.of(data)
//            .collect(DoubleSummaryStatistics::new,
//                  DoubleSummaryStatistics::accept,
//                  DoubleSummaryStatistics::combine);
//      
//      double sumSq = DoubleStream.of(data)
//                        .map(x -> Math.pow(x - stats.getAverage(), 2))
//                        .reduce(Double::sum)
//                        .orElse(Double.valueOf(0));
//      double sigma = Math.sqrt(sumSq / data.length);
//      double min = stats.getMin();
//      double max = stats.getMax();
//      
//      Random random = new Random();
//      for (int i = 0; i < k; i++)
//      {
//         double mean = (max - min) * random.nextDouble() + min;
//         clusters.add(new GaussianModel(mean, sigma));
//      }
//      
      return clusters;
   }

   /**
    * Returns a copied of the supplied values that has been sorted and had outliers trimmed off.
    * @param values
    * @return
    */
   private double[] prepareDataValues(double[] values)
   {
      double[] data = Arrays.copyOf(values, values.length);
      Arrays.parallelSort(data);
      if (data.length > 10)
      {
         // discard outliers
         int trimSize = Math.max(values.length / 10, 1);
         data = Arrays.copyOfRange(data, trimSize, data.length - (1 + trimSize));
      }
      return data;
   }
   
   public static double[] filterOutliers(double[] values)
   {
      double[] data = Arrays.copyOf(values, values.length);
      Arrays.parallelSort(data);
      
      double median = data[(data.length - 1) / 2];
      double[] devMedian = Arrays.stream(data).map(x -> Math.abs(x - median)).toArray();
      Arrays.parallelSort(devMedian);
      double madn = devMedian[(devMedian.length - 1) / 2] / 0.6745;
      
      return Arrays.stream(values)
         .filter(x -> Math.abs(x - median) / madn <= 2.24)
         .toArray();
   }
   
   public static double[] findOutliers(double[] values)
   {
      // See MAD-Median rule 
      // http://stats.stackexchange.com/questions/22627/detect-outliers-in-mixture-of-gaussians
      double[] data = Arrays.copyOf(values, values.length);
      Arrays.parallelSort(data);
      
      double median = data[(data.length - 1) / 2];
      double[] devMedian = Arrays.stream(data).map(x -> Math.abs(x - median)).toArray();
      Arrays.parallelSort(devMedian);
      double madn = devMedian[(devMedian.length - 1) / 2] / 0.6745;
      
      return Arrays.stream(values)
         .filter(x -> Math.abs(x - median) / madn > 2.24)
         .toArray();
   }
}
