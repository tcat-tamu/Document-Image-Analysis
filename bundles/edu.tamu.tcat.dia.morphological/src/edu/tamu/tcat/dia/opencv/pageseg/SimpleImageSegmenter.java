package edu.tamu.tcat.dia.opencv.pageseg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import edu.tamu.tcat.analytics.image.integral.IntegralImage;
import edu.tamu.tcat.analytics.image.integral.IntegralImageImpl;
import edu.tamu.tcat.dia.adapters.opencv.BinaryToOpenCvMatrix;
import edu.tamu.tcat.dia.adapters.opencv.OpenCvMatrixToBinary;
import edu.tamu.tcat.dia.binarization.BinaryImage;
import edu.tamu.tcat.dia.binarization.sauvola.FastSauvola;
import edu.tamu.tcat.dia.morphological.opencv.OpenCvMatrix;

public class SimpleImageSegmenter
{
   private BinaryImage dilated;
   private BinaryImage eroded;
   private BinaryImage edges;
   private BufferedImage contoursIm;
   private boolean hasImages;
   static {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   }
   
   public SimpleImageSegmenter()
   {
      // TODO Auto-generated constructor stub
   }
//   http://chris.improbable.org/2013/08/31/extracting-images-from-scanned-pages/
//   The current process (locate-figures.py) is rather primitive:
//
//      convert the image to grayscale, which is both necessary for some of the algorithms
//      apply a binary filter converting image to black and white
//      Optionally, apply an erode or dilate filter (see the OpenCV erosion and dilation tutorial)
//      Optionally, apply Canny edge detection (OpenCV tutorial)
//      find contours (i.e. what appear to be lines) (OpenCV tutorial)
//      Filter contours which are very small or very large, to avoid extracting small things like defects, letters, etc. or large artifacts like borders from the scanning process which span an entire edge
   public void findIllustrations(BufferedImage pageImage)
   {
      int h = pageImage.getHeight();
      int w = pageImage.getWidth();
      int area = h * w;
      try
      {
//         byte[] pixels = ((DataBufferByte)pageImage.getRaster().getDataBuffer()).getData();
//         Mat image_final = new Mat(h, w, CvType.CV_8UC3);
//         image_final.put(0, 0, pixels);
         
         FastSauvola binarizer = new FastSauvola();
         IntegralImage integralImage = IntegralImageImpl.create(pageImage);
         BinaryImage binaryImage = binarizer.binarize(integralImage);
         
         int sz = 5;
         Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(sz, sz));
        // Mat intermediate = new Mat(h, w, CvType.CV_8U);
         Mat dilatedMat = new Mat(h, w, CvType.CV_8U);
         Mat erodedMat = new Mat(h, w, CvType.CV_8U);
         Mat edgesMat = new Mat(h, w, CvType.CV_8U);
         Mat contoursMat = new Mat(h, w, CvType.CV_8UC3);
         
         try (OpenCvMatrix openCvMatrix = BinaryToOpenCvMatrix.toOpenCvMatrix(binaryImage)) {
            Mat binMat = openCvMatrix.get();
            Mat blurred = new Mat();
            Imgproc.blur(binMat, blurred, new Size(3, 3));
            // TODO create structuring element
            Imgproc.dilate(blurred, dilatedMat, structuringElement);
            Imgproc.erode(binMat, erodedMat, structuringElement);
            int cannythreshold = 100;
//            Imgproc.Canny(dilatedMat, edgesMat, cannythreshold, cannythreshold * 3, 3, true);
            Imgproc.Canny(dilatedMat, edgesMat, cannythreshold, cannythreshold * 3);
            List<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(dilatedMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            
            
            double minAreaPercentage = 0.025;
            double minArea = minAreaPercentage * Math.sqrt(area);    
            
            // HACK: ignore likely scan artifacts
            int maxHeight = (int)Math.round(0.9 * h);
            int maxWidth = (int)Math.round(0.9 * w);
            int minHeight = (int)Math.round(0.9 * h);
            int minWidth = (int)Math.round(0.9 * w);
            List<MatOfPoint> filteredContours = new ArrayList<>();
            for (MatOfPoint contour : contours)
            {
               // http://stackoverflow.com/questions/11273588/how-to-convert-matofpoint-to-matofpoint2f-in-opencv-java-api
               // see second answer for better solution
//               MatOfPoint2f pt = new MatOfPoint2f(contour.toArray());
               double contourArea = Imgproc.contourArea(contour);
//               double arcLength = Imgproc.arcLength(pt, false);
               
               if (Math.sqrt(contourArea) < minArea)
               {
//                  System.out.println("Skipping: " + minArea);
                  continue;
               }
               
               filteredContours.add(contour);
            }
            
            hasImages = filteredContours.size() > 3;
//            Imgproc.drawContours(contoursMat, filteredContours, -1, new Scalar(255, 255, 255));
//            
//            contoursIm = toBufferedImage(contoursMat);
//            dilated = OpenCvMatrixToBinary.adapt(dilatedMat);
//            eroded = OpenCvMatrixToBinary.adapt(erodedMat);
//            edges = OpenCvMatrixToBinary.adapt(edgesMat);
            // TODO apply canny edge detection
//            blur(src,src,Size(3,3));
//            cvtColor(src,tmp,CV_BGR2GRAY);
//            Canny( src, thr, 10, 100, 3 );
//            return BinaryImage.toBufferedImage(dilated);
            
         }
         finally 
         {
            structuringElement.release();
            dilatedMat.release();
            erodedMat.release();
            edgesMat.release();
         }
      }
      catch (Exception ex)
      {
         throw new IllegalStateException(ex);
      }
   }
   
   public static BufferedImage toBufferedImage(Mat source) throws IOException
   {
      MatOfByte bytemat = new MatOfByte();

      try 
      {
         Highgui.imencode(".jpg", source, bytemat);
         byte[] bytes = bytemat.toArray();

         try (InputStream in = new ByteArrayInputStream(bytes))
         {
            BufferedImage img = ImageIO.read(in);
            return img;
         }
         catch (Exception e) 
         {
            throw new IOException("Failed to convert OpenCV matrix to buffered image");
         }
      }
      finally
      {
         if (bytemat != null)
            bytemat.release();
      }
   }
   
   public boolean hasImages() 
   {
      return hasImages;
   }
   
   public BufferedImage getDilated()
   {
      return BinaryImage.toBufferedImage(dilated);
   }
   
   public BufferedImage getEroded()
   {
      return BinaryImage.toBufferedImage(eroded);
   }
   
   public BufferedImage getEdges()
   {
      return BinaryImage.toBufferedImage(edges);
   }
   
   public BufferedImage getContours()
   {
      return contoursIm;
   }

}
