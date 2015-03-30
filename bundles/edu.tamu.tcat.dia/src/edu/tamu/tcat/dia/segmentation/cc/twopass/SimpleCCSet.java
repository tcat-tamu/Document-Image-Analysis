package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class SimpleCCSet implements ConnectComponentSet
{
   private final Map<Integer, SimpleCC> components;
   private final int width;
   private final int height;
   
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
   
   @Override
   public Set<ConnectedComponent> asSet()
   {
      return new HashSet<>(components.values());
   }
   
   @Override
   public int getWidth()
   {
      return width;
   }
   
   @Override
   public int getHeight()
   {
      return height;
   }

   @Override
   public Iterator<ConnectedComponent> iterator()
   {
      return new IteratorImpl(components.values());
   }
   
   private class IteratorImpl implements Iterator<ConnectedComponent>
   {
      private Iterator<SimpleCC> iterator;

      public IteratorImpl(Collection<SimpleCC> collection)
      {
         iterator = collection.iterator();
      }
      
      @Override
      public boolean hasNext()
      {
         return iterator.hasNext();
      }

      @Override
      public ConnectedComponent next()
      {
         return iterator.next();
      }
      
   }
}
