package edu.tamu.tcat.dia.morphological;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.file.Path;
import java.util.Hashtable;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.awt.image.ColorModel;




import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class DilationOperator {

	private BinaryImage input;
	private int width;
	private int height;
	private BooleanArrayBinaryImage output;
	private Mat structuringElement;
	private Point anchorPoint = null;
	private String inFile, outFile;
	private byte[] imageByteArray, outImageByteArray;
	private int defaultSize = 3;

	public static BufferedImage toImage(BinaryImage im, BufferedImage model) {
		int offset = 0;
		int width = im.getWidth();
		int height = im.getHeight();

		ColorModel colorModel = model.getColorModel();
		WritableRaster raster = colorModel.createCompatibleWritableRaster(
				width, height);
		int numBands = raster.getNumBands();
		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				int value = im.isForeground(offset + c) ? 0 : 255;
				for (int b = 0; b < numBands; b++)
					raster.setSample(c, r, b, value);
			}

			offset += width;
		}

		return new BufferedImage(colorModel, raster, true, new Hashtable<>());
	}
	
	public static byte boolToByte(boolean b) {
	    return (byte) (b ? 0 : 1);
	}
	
	public static boolean byteToBool(byte b) {
	    if(b == 0)
	    	return true;
	    else
	    	return false;
	}

	public DilationOperator(String filename, String outFilename,
			Mat structElem, Point anchor) {

		this.inFile = filename;
		this.outFile = outFilename;
		this.structuringElement = structElem;
		this.anchorPoint = anchor;
		this.input = null;

	}
	
	public DilationOperator(String filename, String outFilename,
			int size, Point anchor) {

		this.inFile = filename;
		this.outFile = outFilename;
		this.anchorPoint = anchor;
		this.input = null;
		System.out.println("Creating structuring element, rect of size "+size);
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
			new Size(size, size));
		this.structuringElement = element;

	}

	public DilationOperator(BinaryImage source, Mat structElem, Point anchor) {

		this.input = source;
		this.width = source.getWidth();
		this.height = source.getHeight();
		this.output = new BooleanArrayBinaryImage(width, height);
		this.structuringElement = structElem;
		this.anchorPoint = anchor;
		//this.outFile = "C:\\Users\\deepa.narayanan\\git\\citd.dia\\tests\\edu.tamu.tcat.dia.binarization.sauvola.test\\res\\binary-out.jpg";

		System.out.println("Source size: height "+height+" width: "+width);
		System.out.println("Converting binary image to byte array");
		this.imageByteArray = new byte[input.getSize()];
		for(int i=0;i<input.getSize();i++){
			imageByteArray[i] = boolToByte(input.isForeground(i));
		}
	}

	public BinaryImage run() {

		Mat sourceImage;
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		if(input != null){
			
			System.out.println("Creating matrix from binary Image");
			sourceImage = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8U);
			sourceImage.put(0, 0, imageByteArray);
			System.out.println("sourceImageMatrix contains "+sourceImage.height()+" rows, "+sourceImage.width()+" cols");			
			
			//To test writing sourceImage to file use this
			//sourceImage.convertTo(sourceImage, CvType.CV_8UC3, 255.0); 
			//Highgui.imwrite(this.outFile, sourceImage);
			
		}
		else{
			sourceImage = Highgui.imread(this.inFile);
		}
		
		Mat destination = new Mat(sourceImage.rows(), sourceImage.cols(),
				sourceImage.type());
		destination = sourceImage;

		if(this.structuringElement == null){
			System.out.println("Creating default structuring element, 3x3 rect");
			int dilationSize = defaultSize;
			Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(dilationSize, dilationSize));
			this.structuringElement = element;
		}
		System.out.println("sourceImage num channels "+sourceImage.channels()+" type "+sourceImage.toString());
		Imgproc.dilate(sourceImage, destination, this.structuringElement);
		//Highgui.imwrite(this.outFile, destination);
		
		if(input != null){
			System.out.println("Writing to binary array");
			outImageByteArray = new byte[input.getSize()];	
			destination.get(0, 0,outImageByteArray);
			for(int i=0;i<input.getSize();i++){
				if(outImageByteArray[i] == 0){
					output.setForeground(i);
				}
			}
		}
		else
			Highgui.imwrite(this.outFile, destination);
		
		return output;

	}

}
