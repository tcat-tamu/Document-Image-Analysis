package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.util.Collection;

import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public interface ConnectComponentSet
{

   Collection<Integer> listLabels();

   ConnectedComponent get(int label);

}
