/* Created on       May 28, 2010
 * Last Modified on $Date: $
 * $Revision: $
 * $Log: $
 *
 * Copyright Institute for Digital Christian Heritage (IDCH),
 *           Neal Audenaert
 *
 * ALL RIGHTS RESERVED.
 */
package edu.tamu.tcat.analytics.image.binary;

import java.awt.image.Raster;

/**
 */
public final class IntegralImageImpl implements IntegralImage
{
   /**
    * 
    * @param data The image to be represented as an {@link IntegralImage}. Note that this must 
    *    be a grayscale (or monochrome) image as only the first color band will be sampled.
    * @return 
    */
   public static IntegralImageImpl create(Raster data) {
      int w = data.getWidth();
      int h = data.getHeight();
      
      int[] raster = new int[w * h];
      int[] rowsumImage = new int[w * h];
      int[] rowsumSqImage = new int[w * h];

      int offset = 0; // index of the current pixel. Will be y * w + x
      for (int y = 0; y < h; y++) {
         int s = data.getSample(0, y, 0);

         raster[offset] = s;
         rowsumImage[offset] = s;
         rowsumSqImage[offset] = s * s;
         for (int x = 1; x < w; x++) {
            s = data.getSample(x, y, 0);

            int ix = offset + x;
            raster[ix] = s;
            rowsumImage[ix] = rowsumImage[ix - 1] + s;
            rowsumSqImage[ix] = rowsumSqImage[ix - 1] + s * s;
         }

         offset += w;
      }

      long[] integralImage = new long[w * h];
      long[] integralSqImage = new long[w * h];

      for (int x = 0; x < w; x++) {
         integralImage[x] = rowsumImage[x];
         integralSqImage[x] = rowsumSqImage[x];
      }

      offset = w;
      for (int y = 1; y < h; y++) {
         for (int x = 0; x < w; x++) {

            int ix = offset + x;
            integralImage[ix] = integralImage[ix - w] + rowsumImage[ix];
            integralSqImage[ix] = integralSqImage[ix - w] + rowsumSqImage[ix];

         }

         offset += w;
      }

      return new IntegralImageImpl(data, w, h, integralImage, integralSqImage);
   }
   
	final int width;
	final int height;
	final int imArea;

	final long[] iImg;			// integral image
	final long[] iImgSq;		// integral image squared
   private Raster raster;

	/** Default constructor. */
	public IntegralImageImpl(Raster data, int w, int h, long[] iImg, long[] iImgSq) {
		this.raster = data;
      this.width = w;
		this.height = h;
		this.imArea = w * h;
		this.iImg = iImg;
		this.iImgSq = iImgSq;
	}

	@Override
	public final int getWidth()
	{
		return width;
	}

	@Override
	public final int getHeight()
	{
		return height;
	}

	@Override
	public final int getArea()
	{
		return imArea;
	}

	@Override
	public int get(int ix)
	{
	   int x = ix % width;
	   int y = ix / width;
	   
	   return raster.getSample(x, y, 0);
	}
	
//	public long[] getVerticalProjection()
//	{
//		long[] result = new long[width];
//
//		int w = 10;				// size of soothing window;
//		int mid = w / 10; 		// midpoint of smoothing window
//		int y = height = 1;
//
//		int x2 = width - w - 1;
//		// NOTE, I'm really interested in the derivative, not in the raw projection
//		for (int x = 0; x < mid; x++)
//		{
//			result[x] = (iImg[w][y] - iImg[0][y]) / w;  // NOTE: this truncates rather than rounds
//			result[x2] = (iImg[x2][y] - iImg[width - 1][y]) / w;
//		}
//
//		result[0] = iImg[0][y];
//		for (int i = 0; i < width - w; i++)
//		{
//			int x = i + mid;
//			result[x] = (iImg[i + w][y] - iImg[i][y]) / w;
//		}
//
//		return result;
//	}

	@Override
	public final long getHorizontalProjection(int y, int window)
	{
		int miny = y - (window / 2);
		miny = miny > 0 ? miny : 0;

		int maxy = miny + window;
		if (maxy >= height)
		{
			maxy = height - 1;
			miny = maxy - window;
		}

		// TODO could move the scale factor (window * width) outside of the implicit loop

		int x = width - 1;
		return (iImg[maxy * width + x] - iImg[miny * width + x]) / (window * width);
	}
	
	public final long getVerticalProjection(int x, int window)
	{
	   
//	   int minx = x - (window / 2);
//	   minx = minx > 0 ? minx : 0;
//	   
//	   int maxx = minx + window;
//	   if (maxx >= width)
//	   {
//	      maxx = width - 1;
//	      minx = maxx - window;
//	   }
//	   
//	   // TODO could move the scale factor (window * width) outside of the implicit loop
//	   
//	   int y = height - 1;
//	   int ix = y * width; 
	   throw new UnsupportedOperationException(); // TODO figure out the logic for this.
//	   return (iImg[maxy * wisix + y] - iImg[minx * width + x]) / (window * width);
	}

	/**
	 * Returns the mean and variance of the selected image region.
	 *
	 * @param xmin the min x value of the image region
	 * @param ymin the min y value of the image region
	 * @param xmax the max x value of the image region
	 * @param ymax the max y value of the image region
	 *
	 * @return A two-element array where the first element is the mean value of the pixels in this region
	 * 		and the second is the variance of the pixel values in this region
	 */
	@Override
	public final double[] getGausModel(int xmin, int ymin, int xmax, int ymax) {
		double diff;
		double sqdiff;
		double mean, var;

		int ixMax = ymax * width + xmax;		// lower right corner
		if ((xmin == 0) && (ymin == 0)) {               // Point at origin
			diff = iImg[ixMax];
			sqdiff = iImgSq[ixMax];

		} else if ((xmin == 0) && (ymin != 0)) {        // first row
			int trRef = (ymin - 1) * width + xmax; 			// [xmax][ymin - 1] The point just above the top right corner
			diff = iImg[ixMax] - iImg[trRef];
			sqdiff = iImgSq[ixMax] - iImgSq[trRef];

		} else if ((xmin != 0) && (ymin == 0)) {        // first column
			int blRef = ymax * width + xmin - 1;			// [xmin - 1][ymax]
			diff = iImg[ixMax] - iImg[blRef];
			sqdiff = iImgSq[ixMax] - iImgSq[blRef];

		} else {                                        // rest of the image
			int tlRef = (ymin - 1) * width + xmin - 1;		// [xmin - 1][ymin - 1]
			int trRef = (ymin - 1) * width + xmax; 			// [xmax][ymin - 1] The point just above the top right corner
			int blRef = ymax * width + xmin - 1;			// [xmin - 1][ymax]
			double diagsum = iImg[ixMax] + iImg[tlRef];
			double idiagsum = iImg[trRef] + iImg[blRef];
			diff = diagsum - idiagsum;

			double sqdiagsum  = iImgSq[ixMax] + iImgSq[tlRef];
			double sqidiagsum = iImgSq[trRef] + iImgSq[blRef];
			sqdiff = sqdiagsum - sqidiagsum;
		}

		double area = (xmax - xmin + 1) * (ymax - ymin + 1);
		mean = diff / area;
		var = (sqdiff - (diff * diff) / area) / (area - 1);

		return new double[] { mean, var };
	}
}
