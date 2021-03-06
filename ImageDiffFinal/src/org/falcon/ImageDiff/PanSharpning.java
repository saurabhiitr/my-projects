package org.falcon.ImageDiff;


 import java.awt.Graphics2D;

/*
  * Part of the Java Image Processing Cookbook, please see
  * http://www.lac.inpe.br/~rafael.santos/JIPCookbook.jsp
  * for information on usage and distribution.
  * Rafael Santos (rafael.santos@lac.inpe.br)
  */

  
import java.awt.RenderingHints;
 import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
 import java.awt.image.ComponentColorModel;
 import java.awt.image.DataBuffer;
 import java.awt.image.RenderedImage;
 import java.awt.image.renderable.ParameterBlock;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.jai.IHSColorSpace;
 import javax.media.jai.ImageLayout;
 import javax.media.jai.JAI;
 import javax.media.jai.PlanarImage;
 import javax.swing.JFrame;
  
 import com.sun.media.jai.codec.TIFFEncodeParam;
  
 /*import display.multiple.DisplayTwoSynchronizedImages;
  
 /**
  * This class implements the IHS-based pan-sharpening algorithm. More information about it can
  * be found on http://www.lac.inpe.br/~rafael.santos/JIPCookbook.jsp.
  * Part of this code is based on a message from Madhu V Reddy on the
  * Java Advanced Imaging API discussion group:
  * http://archives.java.sun.com/cgi-bin/wa?A2=ind0308&L=jai-interest&F=&S=&P=623
  */
 public class PanSharpning
   {
  /**
   * The application entry point. We need to provide four input images.
   */
   public static void main(String[] args)
     {
     // First we open the input images. We assume that each band is in a separate file.
     PlanarImage iRed = JAI.create("fileload","C:/Users/Saurabh/Desktop/rgb.jpg");
     PlanarImage iGreen = JAI.create("fileload","C:/Users/Saurabh/Desktop/rgb.jpg");
     PlanarImage iBlue = JAI.create("fileload","C:/Users/Saurabh/Desktop/rgb.jpg");
     // Let's also load the pan image to use it as the new I band.
     PlanarImage panImage = JAI.create("fileload","C:/Users/Saurabh/Desktop/rgb.jpg");
     // We need to combine those bands into a single RGB image.
     ParameterBlock pb = new ParameterBlock();
     pb.addSource(iRed);
     pb.addSource(iGreen);
     pb.addSource(iBlue);
     PlanarImage rgbImage = JAI.create("bandmerge", pb);
     // Let's scale it since the pan image has the double of the resolution.
     pb = new ParameterBlock();
     pb.addSource(rgbImage);
     // Calculate the scale from the images' dimensions.
     float scaleX = (1f*panImage.getWidth()/iRed.getWidth());
     float scaleY = (1f*panImage.getHeight()/iRed.getHeight());
     pb.add(scaleX);
     pb.add(scaleY);
     rgbImage = JAI.create("scale",pb);
     // Now we can convert it to the IHS color space.
     IHSColorSpace ihs = IHSColorSpace.getInstance();
     ColorModel IHSColorModel =
        new ComponentColorModel(ihs,
                                new int []{8,8,8},
                                false,false,
                                Transparency.OPAQUE,
                                DataBuffer.TYPE_BYTE);
     pb = new ParameterBlock();
     pb.addSource(rgbImage);
     pb.add(IHSColorModel);
     RenderedImage imageIHS  = JAI.create("colorconvert", pb);
     // The image is in the IHS color space. Let's separate the I, H and S band.
     PlanarImage[] IHSBands = new PlanarImage[3];
     for(int band=0;band<3;band++)
       {
       pb = new ParameterBlock();
       pb.addSource(imageIHS);
      pb.add(new int[]{band});
       IHSBands[band] = JAI.create("bandselect",pb);
       }
     // Now we can compose the new IHS image.
     // We must pass an instance of RenderingHint with the IHS color model.
     ImageLayout imageLayout = new ImageLayout();
     imageLayout.setColorModel(IHSColorModel);
     imageLayout.setSampleModel(imageIHS.getSampleModel());
     RenderingHints rendHints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,imageLayout);
     pb = new ParameterBlock();
     pb.addSource(panImage);
     pb.addSource(IHSBands[1]);
     pb.addSource(IHSBands[2]);
     RenderedImage panSharpenedIHSImage = JAI.create("bandmerge", pb, rendHints);
     // Now we convert this image back to the RGB color space.
     pb = new ParameterBlock();
     pb.addSource(panSharpenedIHSImage);
     pb.add(rgbImage.getColorModel()); // RGB color model
     PlanarImage finalImage = JAI.create("colorconvert", pb);
  
    JFrame frame = new JFrame("IHS Pan Sharpening");
      frame.add(new DisplayTwoSunc(rgbImage,finalImage));
     frame.pack();
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setVisible(true);
     // Store the resulting image, with tiling.
     TIFFEncodeParam tep = new TIFFEncodeParam();
     tep.setWriteTiled(true);
     tep.setTileSize(128,128);
     JAI.create("filestore",finalImage,"result.tiff","TIFF",tep); 
     }
   
  }