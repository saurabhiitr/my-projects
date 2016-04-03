package org.falcon.ImageDiff;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


public class SimpleRendererEx {
	public List<File> convertImage(File file,String destinationDir)  {
		List<File> list_Files = new ArrayList<File>();
		System.load("C:/Users/Saurabh/Desktop/final image diff/ImageDiffFinal/gsdll64.dll");
		try {
			String fileName = file.getName().replace(".pdf", "");
			// load PDF document
			PDFDocument document = new PDFDocument();
			document.load(file);

			// create renderer
			SimpleRenderer renderer1 = new SimpleRenderer();

			// set resolution (in DPI)
			renderer1.setResolution(200);

			List<Image> images = renderer1.render(document);

			// write images to files to disk as PNG
			try {
				for (int i = 0; i < images.size(); i++) {
					File imageFile = new File(destinationDir + fileName +"_" + (i + 1) + ".png");// change file format here. Ex: .png, .jpg, .jpeg, .gif, .bmp

					if (imageFile.exists()) {
						imageFile.delete();
					}
					ImageIO.write((RenderedImage) images.get(i), "png",imageFile);
					list_Files.add(imageFile);
					System.out.println(imageFile.getName() +" File created in Folder: ");
				}

			} catch (IOException e) {
				System.out.println("ERROR: " + e.getMessage());
			}

		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}

		return list_Files;

	}
	//            RandomAccessFile raf = new RandomAccessFile(file, "r");
	//            FileChannel channel = raf.getChannel();
	//            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	//  //          PDFDocument pdf= new PDFDocument(file);
	//            PDFFile pdf = new PDFFile(buf);
	//            System.out.println("Total Pages: "+ pdf.getNumPages());
	//            for (int i = 1; i <= pdf.getNumPages(); i++) {
	//                PDFPage page = pdf.getPage(i);
	//                System.out.println(page.getPageNumber());
	//                // image dimensions 
	//                int width = (int) page.getBBox().getWidth();
	//                int height = (int) page.getBBox().getHeight();
	//
	//                // create the image
	//               // Rectangle rect = new Rectangle(0, 0, width*2 ,height*2 );
	//                BufferedImage bufferedImage = new BufferedImage(width*2, height*2, BufferedImage.TYPE_INT_RGB);
	//
	//                // width & height, // clip rect, // null for the ImageObserver, // fill background with white, // block until drawing is done
	//                Image image = page.getImage(width*2, height*2, null, null, true, true );
	//               
	//                Graphics2D bufImageGraphics = bufferedImage.createGraphics();
	////                bufImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	////                PDFRenderer renderer = new PDFRenderer(page, bufImageGraphics, new Rectangle(0, 0, 500, 500), null, Color.RED);
	////                page.waitForFinish();
	////                renderer.run();
	//                
	//                bufImageGraphics.drawImage(image, 0, 0, null);
	//
	////                File imageFile = new File( destinationDir + fileName +"_"+ i +".png" );// change file format here. Ex: .png, .jpg, .jpeg, .gif, .bmp
	////
	////                if (imageFile.exists()) {
	////                    imageFile.delete();
	////                }
	//                ImageIO.write(bufferedImage, "png", imageFile);
	//                
	////                list_Files.add(imageFile);
	////                System.out.println(imageFile.getName() +" File created in Folder: ");
	//            }
	//            raf.close();
	//            
	//      
	//    } catch (Exception e) {
	//        e.printStackTrace();
	//    }

	//        return list_Files;

	//    public static void main(String[] args) {
	//    	String myLibraryPath = System.getProperty("user.dir");//or another absolute or relative path
	//
	//    	System.setProperty("java.library.path", myLibraryPath);
	//    	System.loadLibrary("gsdll64");
	//    	System.load("C:/Users/Saurabh/Desktop/jar files/ghost4j-1.0.0/lib/win32-x86-64/gsdll64.dll");
	//	try {
	//
	//	    // load PDF document
	//	    PDFDocument document = new PDFDocument();
	//	    document.load(new File("C:/Users/Saurabh/Desktop/final image diff/temp/20140515_ECPPM_AWU_ROW.pdf"));
	//
	//	    // create renderer
	//	    SimpleRenderer renderer = new SimpleRenderer();
	//
	//	    // set resolution (in DPI)
	//	    renderer.setResolution(300);
	//
	// render
	//	    List<Image> images = renderer.render(document);
	//
	//	    // write images to files to disk as PNG
	//	    try {
	//		for (int i = 0; i < images.size(); i++) {
	//		    ImageIO.write((RenderedImage) images.get(i), "png",
	//			    new File((i + 1) + ".png"));
	//		}
	//	    } catch (IOException e) {
	//		System.out.println("ERROR: " + e.getMessage());
	//	    }
	//
	//	} catch (Exception e) {
	//	    System.out.println("ERROR: " + e.getMessage());
	//	}
	//    	File input=new File("C:/Users/Saurabh/Desktop/final image diff/temp/20140515_ECPPM_AWU_ROW.pdf");
	//		String output="C:/Users/Saurabh/Desktop/ImgDiff/output/";
	//		SimpleRendererEx k=new SimpleRendererEx();
	//		k.convertImage(input,output);
	//    }
	public static void main(String[] args)
	{
		SimpleRendererEx ex=new SimpleRendererEx();
		File file=new File("C:/Users/Saurabh/Desktop/HistoricalFIles/Files/GMCAC_MCIA_P_OAP_C10_0501_B.pdf");
		ex.convertImage(file, "C:/Users/Saurabh/Desktop/new/");
	}
}
