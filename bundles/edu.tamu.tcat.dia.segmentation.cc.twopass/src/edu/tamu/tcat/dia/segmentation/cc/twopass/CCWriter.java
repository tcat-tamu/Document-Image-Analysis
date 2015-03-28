package edu.tamu.tcat.dia.segmentation.cc.twopass;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

import edu.tamu.tcat.analytics.datatrax.Transformer;
import edu.tamu.tcat.analytics.datatrax.TransformerConfigurationException;
import edu.tamu.tcat.analytics.datatrax.TransformerContext;
import edu.tamu.tcat.analytics.image.region.BoundingBox;
import edu.tamu.tcat.analytics.image.region.Point;
import edu.tamu.tcat.dia.segmentation.cc.ConnectComponentSet;
import edu.tamu.tcat.dia.segmentation.cc.ConnectedComponent;

public class CCWriter implements Transformer
{

   public static final String EXTENSION_ID = "tcat.dia.seg.adapters.cc.writer";
   public static final String CONNECTED_COMPONENTS_PIN = "connected_components";
   public static final String MODEL_PIN = "model";

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
   }

   @Override
   public Map<String, Object> getConfiguration()
   {
      HashMap<String, Object> config = new HashMap<String, Object>();
      config.put("colorMap", colorMap);
      
      return config;
   }

   @Override
   public Callable<BufferedImage> create(TransformerContext ctx)
   {
      final ConnectComponentSet components = (ConnectComponentSet)ctx.getValue(CONNECTED_COMPONENTS_PIN);
      int w = components.getWidth();
      int h = components.getHeight();
      return () -> render(components.asSet(), w, h, colorMap);
   }

   public static BufferedImage render(ConnectComponentSet components) {
      return render(components.asSet(), components.getWidth(), components.getHeight(), DEFAULT_COLOR_MAP);
   }
   
   public static BufferedImage render(Collection<ConnectedComponent> components, int width, int height) 
   {
      return render(components, width, height, DEFAULT_COLOR_MAP);
   }
   
   public static BufferedImage render(Collection<ConnectedComponent> components, int width, int height, int[][] colorMap) 
   {
//      int width = components.getWidth();
//      int height = components.getHeight();

      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster raster = image.getRaster();      // TODO use a Graphics2D?

      initializeBackground(raster);
      writeComponents(components, colorMap, raster);
      image.flush();
      
      return image;
   }

   public static void write(ConnectedComponent cc, WritableRaster raster, int[] color)
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

   private static void writeComponents(Collection<ConnectedComponent> components, int[][] colorMap, WritableRaster raster)
   {
      int cIx = 0;
      for (ConnectedComponent cc : components)
      {
         write(cc, raster, colorMap[cIx]);
         cIx = cIx < colorMap.length - 1 ? cIx + 1 : 0;
      }
   }

   private static void initializeBackground(WritableRaster raster)
   {
      int width = raster.getWidth();
      int height = raster.getHeight();
      
      // TODO seems like there should be a faster/better way to do this.
      // should investigate the following
//      int[] rgbArray = new int[width * height];
//      Arrays.fill(rgbArray, 0xFFFFFFFF);
//      image.setRGB(0, 0, width, height, rgbArray, 0, width);

      // set to white background
      int bands = raster.getNumBands();
      for (int r = 0; r < height; r++)  {
         for (int c = 0; c < width; c++) {
            for (int b = 0; b < bands; b++) {
               raster.setSample(c, r, b, 255);
            }
         }
      }
   }
}
