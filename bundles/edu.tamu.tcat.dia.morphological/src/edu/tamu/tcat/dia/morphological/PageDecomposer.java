package edu.tamu.tcat.dia.morphological;

import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;

public final class PageDecomposer {

	
	private IntegralImageImpl iImage;
	private int smoothingWindow;

	public PageDecomposer(IntegralImageImpl iImage, int smWindow){
		this.iImage = iImage;
		this.smoothingWindow = smWindow;
	}
	
	public void run(){
		//get horizontal profile first
		
		int imageWidth = iImage.getWidth();
		int imageHeight = iImage.getHeight();
		long hProjProfile[] = new long[imageHeight];
		for(int i=0; i<imageHeight; i++ ){
			hProjProfile[i] = iImage.getHorizontalProjection(i, smoothingWindow);
		}
		
	}
	
}
