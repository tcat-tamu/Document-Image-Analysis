package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class SimpleCCSet implements ConnectComponentSet
{
   private final Map<Integer, SimpleCC> components = new HashMap<>();
   
   public SimpleCCSet(Map<Integer, SimpleCC> components)
   {
      components = Collections.unmodifiableMap(components);
   }
   
   @Override
   public Collection<Integer> listLabels()
   {
      return Collections.unmodifiableCollection(components.keySet());
   }
   
   @Override
   public ConnectedComponent get(int label)
   {
      return components.get(Integer.valueOf(label));
   }
}
