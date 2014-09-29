package edu.tamu.tcat.dia.segmentation.cc;

import java.util.Collection;

// TODO rename to ConnectedComponentSet
public interface ConnectComponentSet
{

   Collection<Integer> listLabels();

   ConnectedComponent get(int label);
   
   int getWidth();
   
   int getHeight();

}
