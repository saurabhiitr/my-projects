package org.falcon.ImageDiff;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.ghost4j.document.DocumentException;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.RendererException;
import org.ghost4j.renderer.SimpleRenderer;

public class PdfToPngGhostScript {

	public List<File> getImageFile(File file) throws FileNotFoundException, IOException, RendererException, DocumentException {
		System.load("C:/Users/Saurabh/Desktop/jar files/ghost4j-1.0.0/lib/win32-x86-64/gsdll64.dll");
		List<File> tempFiles = new ArrayList<File>();
		List<Image> images = null;
		
			PDFDocument document = new PDFDocument();
			document.load(file);

			SimpleRenderer renderer = new SimpleRenderer();
			// renderer.setMaxProcessCount(Runtime.getRuntime().availableProcessors());

			// set resolution (in DPI)
			renderer.setResolution(300);

			images = renderer.render(document);
			System.out.println("Images obtained.Images size:" + images.size());
			for (int i = 0; i < images.size(); i++) {
				Image image = images.get(i);
				RenderedImage rImage = (RenderedImage) image;
				String outputFile = file.getName().substring(0,
						file.getName().lastIndexOf("."));
				System.out.println("of:" + outputFile);
				File tempFile=new File("C:/Users/Saurabh/Desktop/ImgDiff/output/a");
				try {
					tempFile = File.createTempFile(outputFile, ".png",
							new File("C:/Users/Saurabh/Desktop/ImgDiff/output/a"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				tempFiles.add(tempFile);
				try {
					ImageIO.write(rImage, "png", tempFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Image written to disk:"
						+ tempFile.getAbsolutePath());

				if (image != null) {
					image.flush();
					System.out.println("image flushed");
				}

			}
//
//		} catch (Throwable e) {
//			System.out.println("saurabh");
//			//logger.debug(e.getMessage(), e);
//			//e.printStackTrace();
//		} finally {
//
//		}
		return tempFiles;

	}
	public static void main(String[] args) throws FileNotFoundException, IOException, RendererException, DocumentException {
		
		File input=new File("C:/Users/Saurabh/Desktop/final image diff/temp/20140515_ECPPM_AWU_ROW.pdf");
		String output="C:/Users/Saurabh/Desktop/ImgDiff/output/";
		PdfToPngGhostScript k=new PdfToPngGhostScript();
		k.getImageFile(input);
	}
	
}
