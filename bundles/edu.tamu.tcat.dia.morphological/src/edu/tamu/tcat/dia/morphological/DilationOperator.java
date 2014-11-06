package edu.tamu.tcat.dia.morphological;

import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class DilationOperator {
   private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.dilation");

   // static initializer to load DLL. 
   static {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   }
   
	private final BinaryImage input;
	private final Mat structuringElement;
	private static int defaultSize = 3;

	public static byte boolToByte(boolean b) {
	    return (byte) (b ? 0 : 1);
	}
	
	public DilationOperator(BinaryImage source) 
	{
	   this.input = source;
	   this.structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(defaultSize, defaultSize));
	}

	public DilationOperator(BinaryImage source, int sz) 
	{
	   this.input = source;
	   structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz, sz));
	   
	}
	
	public DilationOperator(BinaryImage source, Mat structElem) 
	{
		this.input = source;
		this.structuringElement = structElem;
	}

   
	public BinaryImage run() 
	{
	   // TODO should probably only do this once.

	   int h = input.getHeight();
      int w = input.getWidth();
      Mat sourceImage = new Mat(h, w, CvType.CV_8U);
	   Mat destination = new Mat(h, w, CvType.CV_8U);
	   try 
	   {
	      sourceImage.put(0, 0, toByteArray(input));
	      Imgproc.dilate(sourceImage, destination, this.structuringElement);
	      return writeOutput(destination);
	   }
	   finally 
	   {
	      sourceImage.release();
	      destination.release();
	   }
	}
	
	private static byte[] toByteArray(BinaryImage data)
	{
	   int sz = data.getSize();
	   byte[] imageByteArray = new byte[sz];
	   for (int i = 0; i < sz; i++)
	   {
	      imageByteArray[i] = boolToByte(data.isForeground(i));
	   }

	   return imageByteArray;
	}

   private BinaryImage writeOutput(Mat destination)
   {
      BooleanArrayBinaryImage output = new BooleanArrayBinaryImage(input.getWidth(), input.getHeight());
      logger.fine("Writing to binary array");
      byte[] outImageByteArray = new byte[input.getSize()];	
      destination.get(0, 0, outImageByteArray);
      for (int i = 0; i < input.getSize(); i++)
      {
         if (outImageByteArray[i] == 0)
            output.setForeground(i);
      }

      return output;
   }

}
