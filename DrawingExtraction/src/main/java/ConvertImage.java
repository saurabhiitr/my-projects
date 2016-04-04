

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import org.ghost4j.document.PDFDocument;
import org.ghost4j.renderer.SimpleRenderer;




public class ConvertImage {
	public void convertImage(File file)  {
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
			renderer1.setResolution(150);

			List<Image> images = renderer1.render(document);

			// write images to files to disk as PNG
			try {
				for (int i = 0; i < images.size(); i++) {
					File imageFile = new File( "C:/Users/Saurabh/Desktop/temp/original.png" );// change file format here. Ex: .png, .jpg, .jpeg, .gif, .bmp

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

	}
	public static void main(String[] args)
	{
		ConvertImage ex=new ConvertImage();
		File file=new File("C:/Users/Saurabh/Desktop/HistoricalFIles/Files/GMCAC_MCIA_D_OAP_D600001_A.pdf");
		ex.convertImage(file);
	}


}
