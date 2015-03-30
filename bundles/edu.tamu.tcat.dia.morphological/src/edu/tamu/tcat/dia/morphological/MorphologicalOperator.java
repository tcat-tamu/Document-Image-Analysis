package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.dia.binarization.BinaryImage;

public interface MorphologicalOperator
{
   BinaryImage execute(BinaryImage image, StructuringElement kernel) throws MorphologicalOperationException;
}
