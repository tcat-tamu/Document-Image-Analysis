package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class SimpleCCSet implements ConnectComponentSet
{
   private final Map<Integer, SimpleCC> components;
   private final int width;
   private final int height;
   
//   public SimpleCCSet(Map<Integer, SimpleCC> components)
//   {
//      this.components = Collections.unmodifiableMap(components);
//   }
   
   public SimpleCCSet(int w, int h, Map<Integer, SimpleCC> components)
   {
      
      this.width = w;
      this.height = h;
      this.components = Collections.unmodifiableMap(components);
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
   
   public int getWidth()
   {
      return width;
   }
   
   public int getHeight()
   {
      return height;
   }
}
