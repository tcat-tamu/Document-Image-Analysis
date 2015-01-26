package edu.tamu.tcat.dia.segmentation.images.bloomberg.datatrax;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.segmentation.images.bloomberg.ExpansionOperator;

public class ExpansionTransformer implements Transformer
{
   public final static String EXTENSION_ID = "tcat.dia.morphological.expansion"; 
   public static final String BINARY_IMAGE_PIN = "binary_image";

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
      final BinaryImage source = (BinaryImage)ctx.getValue(BINARY_IMAGE_PIN);
      
      return () -> {
         return ExpansionOperator.expand(source);
      };
   }
}
