package edu.tamu.tcat.analytics.image.integral;

/**
 * Defines a convenient, fast data structure for performing certain computations on a grayscale 
 * image. Notably, this allows for constant-time computation of the mean and variance of pixel
 * values within any arbitrary rectangular region of the image and for smoothed horizontal and 
 * vertical projections of arbitrary sub-sections of the image.
 */
public interface IntegralImage {

   /**
    * @return The width of the image in pixels.
    */
	int getWidth();

	/**
	 * @return The height of the image in pixels.
	 */
	int getHeight();

	/**
	 * @return The area (total number of pixels) of the image.
	 */
	int getArea();
	
	int get(int ix);

	/**
	 * Computes the smoothed horizontal projection profile for the {@code y<sup>th</sup>} row
	 * of this image with the specified smoothing window. Smothing is performed by taking a 
	 * linear average of the projection profile of adjacent rows within the window centered 
	 * at row {@code y}.  Where the window cannot be centered around row {@code y} (for example,
	 * at the top and bottom rows) it is shifted up or down as required to maintain a constant 
	 * window size.
	 *  
	 * @param y The row for which the horizontal projection should be computed.
	 * @param window The size of the smoothing window to use (in pixels). Must be greater
	 * 		than zero and smaller than the height of the image.
	 * @return The
	 */
	long getHorizontalProjection(int y, int window);

	/**
	 * Computes the mean and variance of pixel values within the bounding box defined by the 
	 * corners (xmin, ymin) x (xmax, ymax). Returns an array whose first element is the mean
	 * and second element is the variance.
	 * 
	 * @param xmin The minimum x value of the bounding box.
	 * @param ymin The minimum y value of the bounding box.
	 * @param xmax The maximum x value of the bounding box.
	 * @param ymax The maximum y value of the bounding box.
	 * 
	 * @return An array whose first element is the mean value of pixels within the defined 
	 *   bounding box and whose and second element is the variance.
	 */
	double[] getGausModel(int xmin, int ymin, int xmax, int ymax);

}