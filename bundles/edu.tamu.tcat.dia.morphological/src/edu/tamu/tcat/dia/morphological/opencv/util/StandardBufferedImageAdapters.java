package edu.tamu.tcat.dia.morphological.opencv.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.Objects;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public enum StandardBufferedImageAdapters implements BufferedImageAdapterStrategy
{
   BGR_3BYTE(CvType.CV_8UC3, BufferedImage.TYPE_3BYTE_BGR),
   BGR_4BYTE(CvType.CV_8UC4, BufferedImage.TYPE_4BYTE_ABGR),
   GRAY_BYTE(CvType.CV_8U, BufferedImage.TYPE_BYTE_GRAY)
   ;
   
   StandardBufferedImageAdapters(int matType, int buffType)
   {
      this.matType = matType;
      this.buffType = buffType;
   }

   int matType;
   int buffType;
//   Class<? extends DataBuffer> clazz;
   
   @Override
   public int getMatType()
   {
      return matType;
   }

   @Override
   public int getBufferedImageType()
   {
      return buffType;
   }

   @Override
   public Mat adapt(BufferedImage image) throws IllegalArgumentException
   {
      if (image.getType() != this.getBufferedImageType())
         image = convertImage(image);
      
      Mat matrix = fromTemplate(image);
      
      DataBuffer dataBuffer = image.getRaster().getDataBuffer();
      if (dataBuffer instanceof DataBufferByte)
         return writeTo((DataBufferByte)dataBuffer, matrix);
      if (dataBuffer instanceof DataBufferInt)
         return writeTo((DataBufferInt)dataBuffer, matrix);
      
      throw new IllegalArgumentException("Unsupported image type [" + dataBuffer.getClass() + "]");
   }

   private BufferedImage convertImage(BufferedImage original)
   {
      Objects.requireNonNull(original, "Cannot convert image. No value supplied.");

      // Don't convert if it already has correct type
      if (original.getType() == buffType) 
         return original;

      BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), buffType);
      Graphics2D g = image.createGraphics();
      try {
         g.setComposite(AlphaComposite.Src);
         g.drawImage(original, 0, 0, null);
      }
      finally {
         g.dispose();
      }

      return image;
   }

   private static Mat writeTo(DataBufferByte buffer, Mat matrix)
   {
      byte[] data = buffer.getData();
      matrix.put(0, 0, data);
      
      return matrix;
   }
   
   private static Mat writeTo(DataBufferInt buffer, Mat matrix)
   {
      int[] data = buffer.getData();
      matrix.put(0, 0, data);
      
      return matrix;
   }
   
   @Override
   public BufferedImage adapt(Mat matrix) throws IllegalArgumentException
   {
      
      BufferedImage image = fromTemplate(matrix);
      WritableRaster raster = image.getRaster();

      // TODO need to move this out so that we can handle more than just byte-oriented images
      DataBuffer buffer = raster.getDataBuffer();
      if (!(buffer instanceof DataBufferByte))
         throw new IllegalArgumentException("Invalid data buffer instance. Expected instance of [" + DataBufferByte.class + "] but found [" + buffer.getClass() + "]");
      
      byte[] data = ((DataBufferByte)buffer).getData();
      matrix.get(0, 0, data);
      return image;
   }

   @Override
   public Mat fromTemplate(BufferedImage image) throws IllegalArgumentException
   {
      int w = image.getWidth();
      int h = image.getHeight();
      
      Mat matrix = new Mat(h, w, this.matType);
      return matrix;
   }

   @Override
   public BufferedImage fromTemplate(Mat matrix) throws IllegalArgumentException
   {
      int w = matrix.width();
      int h = matrix.height();
      
      BufferedImage image = new BufferedImage(w, h, buffType);
      return image;
   }
}
