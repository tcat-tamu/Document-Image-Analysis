package edu.tamu.tcat.dia.segmentation.cc;

import java.util.Collection;

public interface ConnectComponentSet
{

   Collection<Integer> listLabels();

   ConnectedComponent get(int label);

}
