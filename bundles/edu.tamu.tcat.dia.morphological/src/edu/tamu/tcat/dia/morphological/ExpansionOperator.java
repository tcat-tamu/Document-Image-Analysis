package edu.tamu.tcat.dia.morphological;

import java.util.logging.Logger;

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
	private final static Logger logger = Logger.getLogger("edu.tamu.tcat.dia.morph.expansion");


	public ExpansionOperator(BinaryImage source, int scaleFactor) {
		this.input = source;

		this.scaleFactor = scaleFactor;
		
		this.width = source.getWidth();
		this.height = source.getHeight();
		
		logger.fine("Expander Input: Height: "+height+", Width: "+width);
		this.scaledWidth = (int) (width * Math.sqrt(scaleFactor));
		this.scaledHeight = (int) (height * Math.sqrt(scaleFactor));
		
		this.output = new BooleanArrayBinaryImage(scaledWidth, scaledHeight);
	}

	public BinaryImage run() {
		
		int pixelX = 0;
		int pixelY = 0;
		for (int rowIx = 0; rowIx < height; rowIx++) {
			//logger.fine("Processing row: "+rowIx);
			
			for (int colIx = 0; colIx < width; colIx++){
				//logger.finest("Processing col: "+colIx);
				if(input.isForeground(colIx, rowIx)){
					//Set foreground for 4 pixels
					pixelX = rowIx*2;
					pixelY = colIx*2;
					//logger.finest("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+(pixelX*scaledWidth+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2;
					pixelY = colIx*2+1;
					//logger.finest("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth+pixelY)));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2+1;
					pixelY = colIx*2;	
					//logger.finest("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth)+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					pixelX = rowIx*2+1;
					pixelY = colIx*2+1;	
					//logger.finest("Setting pixelX, pixelY: "+pixelX+","+pixelY+" index: "+((pixelX*scaledWidth)+pixelY));
					output.setForeground(pixelX*scaledWidth+pixelY);
					
				}
				
				
			}
			
		}
		logger.fine("Expanded Height: "+output.getHeight()+", width: "+output.getWidth());
		return output;

	}
}
