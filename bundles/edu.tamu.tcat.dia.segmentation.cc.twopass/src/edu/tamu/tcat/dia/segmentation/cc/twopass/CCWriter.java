package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.datatrax.ImageWriterUtils;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class CCWriter implements Transformer
{

   public static final String EXTENSION_ID = "tcat.dia.seg.adapters.cc.writer";
   public static final String CONNECTED_COMPONENTS_PIN = "connected_components";


   private Path path;
   private String format = "jpg";
   private byte[] model;

   private int[][] colorMap = DEFAULT_COLOR_MAP;
   private static int[][] DEFAULT_COLOR_MAP = new int[][]
   {
      {127, 201, 127},
      {190, 174, 212},
      {253, 192, 134},
      {255, 255, 153},
      {56, 108, 176},
      {240, 2, 127},
      {191, 91, 23},
      {102, 102, 102}
   };
   
   /**
    * Given a {@link ConnectComponentSet}, writes the connected components to a colorized 
    * image and passes the unmodified {@code ConnectComponentSet} through to output.
    */
   public CCWriter()
   {
      // TODO Auto-generated constructor stub
   }

   public void setPath(Path path) throws TransformerConfigurationException
   {
      this.path = ImageWriterUtils.processOutputImagePath(path);
   }
   
   public void setFormat(String format) throws TransformerConfigurationException
   {
      this.format = ImageWriterUtils.checkFormat(format);
   }

   public void setModel(BufferedImage model)
   {
      this.model = ImageWriterUtils.getModelData(model);
   }
   
   public void setColorMap(int[][] colorMap)
   {
      if (colorMap == null) 
         this.colorMap = DEFAULT_COLOR_MAP;
      else 
         this.colorMap = colorMap;
   }

   @Override
   public void configure(Map<String, Object> data) throws TransformerConfigurationException
   {
      Object cMap = data.get("colorMap");
      if (cMap != null)
      {
         if (!(cMap instanceof int[][]))
            throw new TransformerConfigurationException("Invalid color map. Expected int[][] but recieved " + cMap.getClass());
         
         setColorMap((int[][])cMap);
      }
      
      this.path = ImageWriterUtils.processOutputImagePath((Path)data.get("path"));
      this.format = ImageWriterUtils.checkFormat((String)data.get("format"));
      
      this.format = ImageWriterUtils.checkFormat((String)data.get("format"));
      
      Object model = data.get("model");
      if (model instanceof BufferedImage)
         this.model = ImageWriterUtils.getModelData((BufferedImage)model);
      else if (model instanceof byte[])
         this.model = (byte[])model;
      else
         throw new TransformerConfigurationException("Invalid type for model data. Expected BufferedImage or byte[] but found " + model.getClass());
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> config = new HashMap<String, Object>();
      config.put("path", path);
      config.put("format", format);
      config.put("model", model);
      config.put("colorMap", colorMap);
      
      return config;
   }

   @Override
   public Callable<BufferedImage> create(TransformerContext ctx)
   {
      final ConnectComponentSet components = (ConnectComponentSet)ctx.getValue(CONNECTED_COMPONENTS_PIN);
      return new Callable<BufferedImage>()
      {
         @Override
         public BufferedImage call()
         {
            try 
            {
               return render(components, ImageWriterUtils.restoreModel(model), colorMap);
            }
            catch (IOException e)
            {
               throw new IllegalStateException("Failed to instantiate model image.", e);
            }
         }
      };
   }

   private static BufferedImage render(ConnectComponentSet components, BufferedImage model, int[][] colorMap) {
      int width = components.getWidth();
      int height = components.getHeight();

      ColorModel colorModel = model.getColorModel();
      WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
      int bands = colorModel.getNumColorComponents();
      for (int r = 0; r < height; r++)  {
         for (int c = 0; c < width; c++) {
            for (int b = 0; b < bands; b++) {
               raster.setSample(c, r, b, 255);
            }
         }
      }

      int cIx = 0;
      for (Integer label : components.listLabels())
      {
         ConnectedComponent cc = components.get(label.intValue());
         BoundingBox box = cc.getBounds();
         if (box.getWidth() > 150 || box.getHeight() > 60 || cc.getNumberOfPixels() < 10)
            continue;
         write(cc, raster, colorMap[cIx]);
         cIx = cIx < colorMap.length - 1 ? cIx + 1 : 0;
      }

      return new BufferedImage(colorModel, raster, true, new Hashtable<>());
   }
   
   private static void write(ConnectedComponent cc, WritableRaster raster, int[] color)
   {
      int bands = color.length;
      Objects.requireNonNull(cc, "CC was null");
      for (Point point : cc.getPoints())
      {
         for (int b = 0; b < bands; b++) {
            raster.setSample(point.getX(), point.getY(), b, color[b]);
         }
      }
   }
}
