package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.BooleanArrayBinaryImage;

public final class ExpansionOperator {

	private BinaryImage input;
	private int scaleFactor;
	private int width;
	private int height;
	private int scaledWidth;
	private int scaledHeight;
	private BooleanArrayBinaryImage output;

	public ExpansionOperator(BinaryImage source, int scaleFactor) {
		this.input = source;

		this.scaleFactor = scaleFactor;
		
		this.width = source.getWidth();
		this.height = source.getHeight();
		
		System.out.println("Expander Input: Height: "+height+", Width: "+width);
		//this.width = 2;
		//this.height =2;

		this.scaledWidth = (int) (width * Math.sqrt(scaleFactor));
		this.scaledHeight = (int) (height * Math.sqrt(scaleFactor));
		
		this.output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
	}

	public BinaryImage run() {
		
		int pixelX = 0;
		int pixelY = 0;
		for (int rowIx = 0; rowIx < height; rowIx++) {
			//System.out.println("Processing row: "+rowIx);
			
			for (int colIx = 0; colIx < width; colIx++){
				//System.out.println("Processing col: "+colIx);
				if(input.isForeground(colIx, rowIx)){
					//Set foreground for 4 pixels
					pixelX = rowIx*2;
					pixelY = colIx*2;
					//System.out.println("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+(pixelX*scaledWidth+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2;
					pixelY = colIx*2+1;
					//System.out.println("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth+pixelY)));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2+1;
					pixelY = colIx*2;	
					//System.out.println("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth)+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2+1;
					pixelY = colIx*2+1;	
					//System.out.println("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth)+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					
				}
				
				
			}
			//System.out.println("=========================");
			
		}
		System.out.println("Expanded Height: "+output.getHeight()+", width: "+output.getWidth());
		return output;

	}
}
