

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;


public class PdfToPng {
	public BufferedImage convertImage(File file)  {
		BufferedImage bufferedImage=null;
		List<File> list_Files = new ArrayList<File>();
		try {
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			PDFFile pdf = new PDFFile(buf);
			System.out.println("Total Pages: "+ pdf.getNumPages());
			for (int i = 1; i <= pdf.getNumPages(); i++) {
				PDFPage page = pdf.getPage(i);
				//    System.out.println(page.getPageNumber());
				// image dimensions 
				int width = (int) page.getBBox().getWidth();
				int height = (int) page.getBBox().getHeight();

				// create the image
				//System.out.println(width + " " +  height);
				//                Rectangle rect = new Rectangle((width/2), (height/2) , width/2 ,height/2 );
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

				// width & height, // clip rect, // null for the ImageObserver, // fill background with white, // block until drawing is done
				Image image = page.getImage(width, height, null, null, true, true );

				Graphics2D bufImageGraphics = bufferedImage.createGraphics();

				bufImageGraphics.drawImage(image, 0, 0, null);

				File imageFile = new File( "C:/Users/Saurabh/Desktop/temp/original.png" );// change file format here. Ex: .png, .jpg, .jpeg, .gif, .bmp

				if (imageFile.exists()) {
					imageFile.delete();
				}

				ImageIO.write(bufferedImage, "png", imageFile);

				list_Files.add(imageFile);
				System.out.println(imageFile.getName() +" File created in Folder: ");

			}
			raf.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

		return bufferedImage;
	}

	//	public static void main(String Args[])
	//	{
	//		File input=new File("C:/Users/Saurabh/Desktop/HistoricalFIles/Files/GMCAC_MCIA_D_OAP_F600001_A.pdf");
	//		String output="C:/Users/Saurabh/Desktop/ImgDiff/output/";
	//		PdfToPng k=new PdfToPng();
	//		k.convertImage(input,output);
	//		
	//	}
}