package edu.tamu.tcat.dia.morphological;

import java.util.function.Supplier;

import org.opencv.core.Mat;

/**
 * A wrapper around the OpenCV {@link Mat} class that that implements the {@link AutoCloseable}
 * interface. This is intended to allow the DataTrax (and other) framework to discover that 
 * this data object needs to be disposed once it is no longer neede by the framework.
 */
public class OpenCvMatrix implements Supplier<Mat>, AutoCloseable
{
   private final Mat matrix;
   private boolean closed = false;
   
   public OpenCvMatrix(Mat matrix)
   {
      this.matrix = matrix;
   }

   @Override
   public void close() throws Exception
   {
      synchronized (this)
      {
         closed = true;
         matrix.release();
      }
   }

   @Override
   public Mat get()
   {
      synchronized (this)
      {
         if (closed)
            throw new IllegalStateException("This matrix has already been disposed");
         
         return matrix;
      }
   }

}
