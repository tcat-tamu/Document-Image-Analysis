package edu.tamu.tcat.dia.segmentation.images.bloomberg.datatrax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.UnionOperator;

public class BloombergUnionTransformer implements Transformer
{
   public final static String EXTENSION_ID = "tcat.dia.seg.images.bloomberg"; 
   public static final String SOURCE_PIN = "source";
   public static final String SEED_PIN = "seed";

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> params = new HashMap<String, Object>();
      return params;
   }

   @Override
   public Callable<BinaryImage> create(TransformerContext ctx)
   {
      final BinaryImage source = (BinaryImage)ctx.getValue(SOURCE_PIN);
      final BinaryImage seed = (BinaryImage)ctx.getValue(SEED_PIN);
      
      return () -> {
         return UnionOperator.computeMask(source, seed);
      };
   }
}
