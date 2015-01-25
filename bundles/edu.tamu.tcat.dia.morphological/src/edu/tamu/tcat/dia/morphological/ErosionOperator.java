package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.dia.binarization.BinaryImage;

public interface ErosionOperator extends AutoCloseable
{
   void setInput(BinaryImage image);

   void setStructuringElement(int sz);

   void execute() throws MorphologicalOperationException;

   BinaryImage getResult();

}
