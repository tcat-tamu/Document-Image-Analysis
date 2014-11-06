package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class ThresholdReducer {

	private BinaryImage input;
	private int scaleFactor;
	private int threshold;
	private int width;
	private int height;
	private int scaledWidth;
	private int scaledHeight;
	private BooleanArrayBinaryImage output;

	public ThresholdReducer(BinaryImage source, int scaleFactor, int threshold) {
		this.input = source;

		this.scaleFactor = scaleFactor;
		this.threshold = threshold;

		this.width = source.getWidth();
		this.height = source.getHeight();
		
		if(height%2 != 0)
			this.height = height-1;
		
		
		if(width%2 != 0)
			this.width = width-1;
		
		
		//System.out.println("Height: "+height+", Width: "+width);
		//this.width = 4;
		//this.height = 6;

		this.scaledWidth = (int) (width / Math.sqrt(scaleFactor));
		this.scaledHeight = (int) (height / Math.sqrt(scaleFactor));
		System.out.println("Scaled Height: "+scaledHeight+", Scaled Width: "+scaledWidth);
		
		this.output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
	}

	public BinaryImage run() {
		
		//Each row is processed in blocks of four pixels and stored in scaled image
		//TODO - Block size should be changed to a square based on the scale factor
		//if sum of values of 4 pixels >= threshold, set output pixel to true
		//Note that processing is done row-wise and storage is done column-wise
		//New image pixels are generated in this order (0,0);(0,1);(1,0);(1,1)
		//Storage is in this array index order 0,2,1,3. 
		//Storage index is determined by: outputRowIx+(outputColIx*scaledWidth)
		//Pixel (0,0) in new image is stored in 0; (0,1) in 2; (1,0) in 1 and (1,1) in 3.
		
		
		int outputRowIx = 0;
		int outputColIx = 0;
		int offset = 0;
		for (int rowIx = 0; rowIx < height; rowIx+=2) {
			//System.out.println("Reducer: processing row: "+rowIx);
			outputColIx = 0;
			for (int colIx = 0; colIx < width; colIx+=2){
				//System.out.println("Reducer: processing col: "+colIx);
				
				int sumValues = 0;
				//System.out.println("Will manipulate these pixels:");
				
				//System.out.println(rowIx+","+colIx+" value at: "+(rowIx * this.width + colIx)+" is "+input.isForeground(rowIx * this.width + colIx));			
				if(input.isForeground(colIx, rowIx))
					sumValues+=1;
				
				//System.out.println((rowIx+1)+","+colIx+" value at: "+((rowIx+1) * this.width + (colIx)));
				if(input.isForeground(colIx, rowIx+1))
					sumValues+=1;
				
				//System.out.println(rowIx+","+(colIx+1)+" value at: "+((rowIx) * this.width + (colIx+1)));
				if(input.isForeground(colIx+1, rowIx))
					sumValues+=1;
				
				//System.out.println((rowIx+1)+","+(colIx+1)+" value at: "+((rowIx+1) * this.width + (colIx+1)));
				if(input.isForeground(colIx+1, rowIx+1))
					sumValues+=1;
				
				//System.out.println("sumValues is "+sumValues);
				if(sumValues >= threshold){
					//System.out.println("Will set foreground at "+(offset+outputColIx));
					//System.out.println("Output Indexes: outRowIx: "+outputRowIx+",outputColIx: "+outputColIx+". Will store value in "+(outputRowIx+(outputColIx*scaledWidth)));
					//output.setForeground(outputRowIx+(outputColIx*scaledWidth));
					//System.out.println("Output Indexes: outRowIx: "+outputRowIx+",outputColIx: "+outputColIx+". Will store value in "+(offset+outputColIx));
					output.setForeground(offset+outputColIx);
				}
				
				outputColIx++;
				
				//System.out.println("==============================");
				
				
			}
			offset += scaledWidth;
			outputRowIx++;
			
		}
		
		System.out.println("Last output row and column written: "+outputRowIx+","+outputColIx);
		System.out.println("Processed Height: "+output.getHeight()+", width: "+output.getWidth());
		return output;

	}
}
