package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.HashMap;
import java.util.Map;

import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class ConnectedComponentSet
{
   // NOT THREAD SAFE
   Map<Integer, SimpleCC> components = new HashMap<>();
   
   void create(int label) 
   {
      
   }
   
   public ConnectedComponent get(int label)
   {
      return components.get(Integer.valueOf(label));
   }
}
