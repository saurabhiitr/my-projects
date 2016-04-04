

//local package

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;



public class SquareAnalysis {


	static List<MatOfPoint> squares = new LinkedList <MatOfPoint>();
	static Mat image;
	static int thresh = 50;
	static int N = 1;
	static Point tl=null;
	static Point br=null;


	//helper function:
	//finds a cosine of angle between vectors
	//from pt0->pt1 and from pt0->pt2
	static double angle( Point pt1, Point pt2, Point  pt0 )
	{
		double dx1 = pt1.x - pt0.x;
		double dy1 = pt1.y - pt0.y;
		double dx2 = pt2.x - pt0.x;
		double dy2 = pt2.y - pt0.y;
		return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
	}

	//returns sequence of squares detected on the image.
	//the sequence is stored in the specified memory storage
	static void findSquares()
	{
		squares.clear();

		Mat pyr=new Mat();
		Mat timg=new Mat();
		Mat gray=new Mat();



		// down-scale and upscale the image to filter out the noise
		Imgproc.pyrDown(image, pyr, new Size(image.cols()/2, image.rows()/2));
		Imgproc.pyrUp(pyr, timg, image.size());

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); 

		Imgproc.threshold(timg, gray, 250 , 255 , Imgproc.THRESH_BINARY);
		Imgproc.findContours(gray, contours,new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		// test each contour
		MatOfPoint2f  approxCurve = new MatOfPoint2f();

		for( int i = 0; i < contours.size(); i++ )
		{

			MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
			double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
			Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance,false);


			if( approxCurve.toList().size() == 4 &&
					Math.abs(Imgproc.contourArea(approxCurve)) > 100 &&
					Imgproc.isContourConvex(new MatOfPoint(approxCurve.toArray())) )
			{
				double maxCosine = 0;

				for( int j = 2; j < 5; j++ )
				{
					// find the maximum cosine of the angle between joint edges
					double cosine = Math.abs(angle(approxCurve.toList().get(j%4), approxCurve.toList().get(j-2), approxCurve.toList().get(j-1)));
					maxCosine = Math.max(maxCosine, cosine);
				}

				// if cosines of all angles are small
				// (all angles are ~90 degree) then write quandrange
				// vertices to resultant sequence
				if( maxCosine < 0.3 )
					squares.add(new MatOfPoint(approxCurve.toArray()));
			}
		}
	}




	//the function draws all the squares in the image
	static void drawSquares( )
	{
		sortByCoordinates();                //Sorting the squares by their area
		Mat temp_img =new Mat();
		temp_img=image.clone();
		Core.polylines(temp_img, squares, true, new Scalar(0,255,0), Core.LINE_4);
		Highgui.imwrite("C:/Users/Saurabh/Desktop/temp/temporary.png", temp_img);

		System.out.println("Total number of square found " + squares.size());
		TemplateAnalysis ta=new TemplateAnalysis();
		for( int i = 0; i < squares.size(); i++ )
		{
			MatOfPoint s=squares.get(i);

			int f = ta.find(s.toArray()[0], s.toArray()[2]);
			if(f==1)
			{
				tl=s.toArray()[0];
				br=s.toArray()[2];
				System.out.println("left upper corner " + s.toArray()[0].x +" , " + s.toArray()[0].y + " " + "bottom array "  + s.toArray()[2].x +" , " +  s.toArray()[2].y);
				System.out.println(area(squares.get(i)));
				List<MatOfPoint> templist = new ArrayList<>();
				templist.add(squares.get(i));
				Core.polylines(image, templist, true, new Scalar(0,255,0), Core.LINE_4);
				break;
			}
		}

	}

	public static int area(MatOfPoint a)
	{
		Point p1=a.toArray()[0];
		Point p2=a.toArray()[2];
		int area1=(int) (Math.abs(p1.x - p2.x) * Math.abs(p1.y - p2.y));
		//System.out.println(area1);
		return area1;
	}

	public static void sortByCoordinates() {

		Collections.sort(squares, new Comparator<MatOfPoint>(){public int compare(final MatOfPoint a, final MatOfPoint b) {
			Point p1=a.toArray()[0];
			Point p2=a.toArray()[2];
			int area1=(int) (Math.abs(p1.x - p2.x) * Math.abs(p1.y - p2.y));
			Point p3=b.toArray()[0];
			Point p4=b.toArray()[2];
			int area2=(int) (Math.abs(p3.x - p4.x) * Math.abs(p3.y - p4.y));
			if (area1  < area2) {
				return -1;
			}
			else if (area1 > area2) {
				return 1;
			}
			else {
				return 0;
			}
		}});

	}
	public void uncompressXRef(String src, String dest) throws IOException, DocumentException
	{
		PdfReader reader = new PdfReader(src);
		PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
		stamper.close();
		reader.close();		
	}
	public static BufferedImage convertImage(File file) throws IOException  {


		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdf = new PDFFile(buf);
		System.out.println("Total Pages: "+ pdf.getNumPages());
		PDFPage page = pdf.getPage(1);
		int width = (int) page.getBBox().getWidth();
		int height = (int) page.getBBox().getHeight();
		BufferedImage bufferedImage = new BufferedImage(width*4, height*4, BufferedImage.TYPE_INT_RGB);
		BufferedImage bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// width & height, // clip rect, // null for the ImageObserver, // fill background with white, // block until drawing is done
		Image image = page.getImage(width*4, height*4, null, null, true, true );
		Image image2 = page.getImage(width, height, null, null, true, true );
		Graphics2D bufImageGraphics = bufferedImage.createGraphics();
		Graphics2D bufImageGraphics2 = bufferedImage2.createGraphics();
		bufImageGraphics.drawImage(image, 0, 0, null);
		bufImageGraphics2.drawImage(image2, 0, 0, null);
		bufferedImage2=bufferedImage2.getSubimage(0  , (int)(height * 3.5/4), width, (int)(height*0.5/4));
		//ImageIO.write(bufferedImage2, "png", new File("C:/Users/Saurabh/Desktop/temp/original_template.png"));
		bufferedImage=bufferedImage.getSubimage(0  , (int)(height*3.5), width*4, (int)(height*0.5));
		raf.close();
		return bufferedImage;     
	}

	public static File selectPdf()
    {
         JFileChooser chooser = new JFileChooser();
         FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF","pdf");
         chooser.setFileFilter(filter);
         chooser.setMultiSelectionEnabled(false);
         int returnVal = chooser.showOpenDialog(null);
         File file=null;
         if(returnVal == JFileChooser.APPROVE_OPTION) 
         {
             file=chooser.getSelectedFile();
            
         }
         return file;
    }

	public static void main(String []Args) throws IOException
	{

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


		// ---------------------------------------------------------------------

//		String src="C:/Users/Saurabh/Desktop/HistoricalFIles/Files/GMCAC_MCIA_D_OAP_D600001_A.pdf";
//		String dest="C:/Users/Saurabh/Desktop/temp/temp.pdf";
//		SquareAnalysis sa=new SquareAnalysis();
//		try {
//			sa.uncompressXRef(src, dest);
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
//		File file_temp=new File("C:/Users/Saurabh/Desktop/temp/temp.pdf");
		
		
		File template=selectPdf();
		ConvertImage c =new ConvertImage();
		c.convertImage(template);
		Image img=ImageIO.read(new File("C:/Users/Saurabh/Desktop/temp/original.png"));
		int wid=img.getWidth(null);
		int hei=img.getHeight(null);
		BufferedImage bufferedImage = new BufferedImage(wid, hei, BufferedImage.TYPE_BYTE_BINARY);
		Graphics2D bufImageGraphics = bufferedImage.createGraphics();
		bufImageGraphics.drawImage(img, 0, 0, null);
		BufferedImage bi=bufferedImage.getSubimage(0  , (int)(hei*0.75), wid, (int)(hei*0.25));
		//BufferedImage bi=bufferedImage;
//		BufferedImage bi = convertImage(file_temp);					//this converImage function is local to this class
		ImageIO.write(bi, "png", new File("C:/Users/Saurabh/Desktop/temp/original_template.png"));
		ImageIO.write(bi, "png", new File("C:/Users/Saurabh/Desktop/temp/enhanced_template.png"));
		bi.flush();

//		PdfToPng p=new PdfToPng();
//		p.convertImage(file);			
		// This convert image function is in PdfToPng class and convert pdf to png

		image = Highgui.imread("C:/Users/Saurabh/Desktop/temp/enhanced_template.png", Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		//   Highgui.imwrite("C:/Users/Saurabh/Desktop/temp/sau1.png", image);

		findSquares();
		drawSquares();

		TemplateMatchingOld tm=new TemplateMatchingOld();
		//Resetting found points
//		tl.x=tl.x/4;
//		tl.y=tl.y/4;
//		br.x=br.x/4;
//		br.y=br.y/4;

		List<File> files = new ArrayList<File>();
		try{
			File Inputfile=new File("C:/Users/Saurabh/Desktop/HistoricalFIles/Files");

			for (File f : Inputfile.listFiles()) 
			{
				if(f.isDirectory()) 
				{

				} 
				else
				{
					files.add(f);
				}
			}
			System.out.println(files.size());
			long t1=System.currentTimeMillis();
			ConvertImage ci=new ConvertImage();
			for(int i=0;i<files.size();i++)
			{
//				SquareAnalysis sa2=new SquareAnalysis();
//				try {
//					sa2.uncompressXRef(files.get(i).getAbsolutePath(), "C:/Users/Saurabh/Desktop/temp/temp" + i + ".pdf");
//				} catch (DocumentException e) {
//					e.printStackTrace();
//				}
//				File file2=new File("C:/Users/Saurabh/Desktop/temp/temp" + i + ".pdf");
			
			//File file=selectPdf();
			
				ci.convertImage(files.get(i));
				tm.ExtractText(tl, br);
				System.out.println("end of " + i);

			}
			long t2=System.currentTimeMillis();
			System.out.println(t2-t1);
		}
		finally{}
	}
}
