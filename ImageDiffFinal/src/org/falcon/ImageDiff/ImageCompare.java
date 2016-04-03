package org.falcon.ImageDiff;

import java.awt.Graphics;
import java.awt.Image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
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

import org.apache.commons.io.FilenameUtils;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/*class Diff
{
	private File adiffb;
	private File bdiffa;
	private File combine;
	
	public File getAdiffb() 			{	return adiffb;}
	public File getBdiffa() 			{	return bdiffa;}
	public File getCombine() 			{	return combine;}
	
	public void setAdiffb(File adiffb) 	{	this.adiffb = adiffb;}
	public void setBdiffa(File bdiffa) 	{	this.bdiffa = bdiffa;}
	public void setCombine(File combine){	this.combine = combine;}
}*/

	

class Comparision 
{

	private String workingDir = null;
	
	//01
	public Comparision() {System.out.println("running-------01");System.out.println("end-------01");}
	
	//02
	public Comparision(String workingDir) {System.out.println("running-------02");this.workingDir = workingDir;System.out.println("end-------02");}

	//03
	public void Compare_Directory(File file, String Output_Directory) 
	{
		System.out.println("running-------03");
		List<File> files = new ArrayList<File>();
		
		
		for (File f : file.listFiles()) 
		{
			if(f.isDirectory()) 
			{
				String out = Output_Directory.endsWith(File.separator) 
						? Output_Directory+ f.getName() 
						: Output_Directory + File.separator+ f.getName();
				Compare_Directory(f, out);
			} 
			else
			{
				files.add(f);
			}
		}
		System.out.println("Directry is "+Output_Directory+"\n and Number of Files is "+files.size());
		for (int i = 0; i < files.size() - 1; i++) 
		{
			File f1 =files.get(i);//selectPdf()
			for (int j = i + 1; j < files.size(); j++) 
			{
				File f2=files.get(j);//=selectPdf() ;
				System.out.println("Comparing file1:"+f1.getName()+"...file2:"+f2.getName()+"....");
				Diff diff = pdfCompare(f1, f2, Output_Directory);
				
				System.out.println("Comparison file1:"+f1.getName()+"...file2:"+f2.getName()+"...."+"done");
				System.out.println("adiffb:"+diff.getAdiffb().getAbsolutePath());
				System.out.println("bdiffa:"+diff.getBdiffa().getAbsolutePath());
				System.out.println("combine:"+diff.getCombine().getAbsolutePath());
			}
		}
		System.out.println("end-------03");

	}

	//04
	public Diff pdfCompare(File f1, File f2, String Output_Directory) 
{
		System.out.println("running-------04");
		List<Diff> Diff_List = compare(f1, f2);
		Diff diff = new Diff();

		String nameAdiffB = FilenameUtils.removeExtension(f1.getName())+ "_diff_" + FilenameUtils.removeExtension(f2.getName())+ ".pdf";
		String nameBdiffA = FilenameUtils.removeExtension(f2.getName())+ "_diff_" + FilenameUtils.removeExtension(f1.getName())+ ".pdf";
		String nameAcombineB=FilenameUtils.removeExtension(f1.getName())+"_combine_"+ FilenameUtils.removeExtension(f2.getName())+".pdf";
		
		String outputAdiffB = Output_Directory.endsWith(File.separator) ? Output_Directory+ nameAdiffB : Output_Directory + File.separator + nameAdiffB;
		String outputBdiffA = Output_Directory.endsWith(File.separator) ? Output_Directory+ nameBdiffA : Output_Directory + File.separator + nameBdiffA;
		String outputAcombineB = Output_Directory.endsWith(File.separator) ? Output_Directory+ nameAcombineB : Output_Directory + File.separator + nameAcombineB;
		try 
		{
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputAdiffB);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			
			int flag=0;
			writer.open();
			for (Diff d : Diff_List) 
			{
				String input = d.getAdiffb().getAbsolutePath();
				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			
				if(flag==0)
					{document.open();flag++;}
			
				document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			}
			document.close();
			writer.close();
				
			diff.setAdiffb(new File(outputAdiffB));
		} 
		catch (Exception e)	{System.out.println("");}
				

		try 
		{
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputBdiffA);
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			int flag=0;
			writer.open();
			for (Diff d : Diff_List) 
			{
				String input = d.getBdiffa().getAbsolutePath();

				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
				if(flag==0)
					{document.open();flag++;}
				
				document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			}
			document.close();
			writer.close();
			
			diff.setBdiffa(new File(outputBdiffA));
		} 
		catch (Exception e) {System.out.println("");}

		try 
		{
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputAcombineB);
			PdfWriter writer = PdfWriter.getInstance(document, fos);

			int flag=0;
			writer.open();
			
			for (Diff d : Diff_List) 
			{
				String input = d.getCombine().getAbsolutePath();
				
				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
				if(flag==0)
					{document.open();flag++;}
				
				document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			}
			document.close();
			writer.close();
			
			diff.setCombine(new File(outputAcombineB));
		} 
		catch (Exception e)	{System.out.println("");}
		
		System.out.println("end-------04");
		return diff;
	}
	
	//05
	public List<Diff> compare(File f1, File f2) 
	{
		System.out.println("running-------05");
		List<Diff> result = new LinkedList<Diff>();
		
		try
		{
			List<File> f1s = convertImage(f1);
			List<File> f2s = convertImage(f2);
			
			int i = 0;
			for (i = 0; i < f1s.size(); i++) 
			{
				if (i < f2s.size()) 
				{
					Diff f = detect1(f1s.get(i), f2s.get(i));
					result.add(f);
				} 
				else 
				{
					Diff diff = getDiff(f1s.get(i), null);
					result.add(diff);
				}
			}
			for (; i < f2s.size(); i++) 
			{
				Diff diff = getDiff(null, f2s.get(i));
				result.add(diff);
			}
		}
		catch(Throwable e){System.out.println("");}
		System.out.println("end-------05");
		return result;
	}
	
	//06
	public List<File> convertImage(File file) throws Exception 
	{
		
		System.out.println("running-------06");
		List<File> Temp_Files = new ArrayList<File>();
		
		if((FilenameUtils.getExtension(file.getAbsolutePath())).compareTo("pdf")!=0)  //if file is not pdf then direct add to the temp file
		{
			Temp_Files.add(file);
			return Temp_Files;
		}
		
		String outputFile = file.getName().substring(0,file.getName().lastIndexOf("."));
        
		//  load a pdf from a file
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        FileChannel channel = raf.getChannel();
        ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY,0, channel.size());
        PDFFile pdffile = new PDFFile(buf);
        
        
        
        int No_of_pages = pdffile.getNumPages();  //   get number of pages
        //  iterate through the number of pages
        for (int i = 1; i <=No_of_pages; i++) 
        {
            PDFPage page = pdffile.getPage(i);
 
            int w=4*(int) page.getBBox().getWidth();
            int h=4*(int) page.getBBox().getHeight();
            
            System.out.println("---------------------------------------width="+(int) page.getBBox().getWidth());
            System.out.println("---------------------------------------height="+(int) page.getBBox().getHeight());
            //  create new image
            
            BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            
            Image img = page.getImage(w , h , null , null , true , true); 
            //width , height , clip-rect , null for the ImageObserver , fill background with white , block until drawing is done
                    
            
            Graphics g = bufferedImage.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
 
            File asd = new File(workingDir+"Output_"+ outputFile + i + ".png");
            if (asd.exists()) 
            	asd.delete();
            
            ImageIO.write(bufferedImage, "png", asd);
            Temp_Files.add(asd);
        }
        raf.close();
        
        System.out.println("end-------06");    
        return Temp_Files;
        
		
    }
	
	//---------------------------------------------------------------
	
	/*
	        try {
	        String sourceDir = "C:/Users/Saurabh/Desktop/ImgDiff/tmp/1.pdf";// PDF file must be placed in DataGet folder
	        String destinationDir = "C:/Users/Saurabh/Desktop/ImgDiff/output/";//Converted PDF page saved in this folder

	        File sourceFile = new File(sourceDir);
	        File destinationFile = new File(destinationDir);

	        String fileName = sourceFile.getName().replace(".pdf", "");
	        if (sourceFile.exists()) {
	            if (!destinationFile.exists()) {
	                destinationFile.mkdir();
	                System.out.println("Folder created in: "+ destinationFile.getCanonicalPath());
	            }

	            RandomAccessFile raf = new RandomAccessFile(sourceFile, "r");
	            FileChannel channel = raf.getChannel();
	            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
	            PDFFile pdf = new PDFFile(buf);

	            int pageNumber = 1;// which PDF page to be convert
	            PDFPage page = pdf.getPage(pageNumber);

	            // image dimensions 
	            int width = 1200;
	            int height = 1400;

	            // create the image
	            Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());
	            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	            // width & height, // clip rect, // null for the ImageObserver, // fill background with white, // block until drawing is done
	            Image image = page.getImage(width, height, rect, null, true, true );
	            Graphics2D bufImageGraphics = bufferedImage.createGraphics();
	            bufImageGraphics.drawImage(image, 0, 0, null);

	            File imageFile = new File( destinationDir + fileName +"_"+ pageNumber +".png" );// change file format here. Ex: .png, .jpg, .jpeg, .gif, .bmp

	            ImageIO.write(bufferedImage, "png", imageFile);

	            System.out.println(imageFile.getName() +" File created in: "+ destinationFile.getCanonicalPath());
	        } else {
	            System.err.println(sourceFile.getName() +" File not exists");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	}
	
	*/
	
	//------------------------------------------------------------------------------
	//07
	public Diff detect(File file1, File file2) 
	{
		System.out.println("running-------07");
		
		Diff diff = new Diff();
		String IMAGE_1_Path = file1.getAbsolutePath();   // getting image 1 path
		String IMAGE_2_Path = file2.getAbsolutePath();	//getting image 2 path
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try
		{
			Mat Img_1M = Highgui.imread(IMAGE_1_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);   //load image in matrix form 
			Mat Img_2M = Highgui.imread(IMAGE_2_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);

			System.out.println(Img_2M.rows() + "," + Img_2M.cols());
			System.out.println(Img_1M.rows() + "," + Img_1M.cols());

			int minCols = Math.min(Img_2M.cols(), Img_1M.cols());
			int minRows = Math.min(Img_2M.rows(), Img_1M.rows());

			Rect minRect = new Rect(0, 0, minCols, minRows);

			Img_1M = Img_1M.submat(minRect);
			Img_2M = Img_2M.submat(minRect);
			
			FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF); 
			
			MatOfKeyPoint Keypoint_Img1 = new MatOfKeyPoint();
			MatOfKeyPoint Keypoint_Img2 = new MatOfKeyPoint();

			detector.detect(Img_1M, Keypoint_Img1);
			detector.detect(Img_2M, Keypoint_Img2);

			DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
			
			Mat descriptor_Img1 = new Mat();
			Mat descriptor_Img2 = new Mat();

			extractor.compute(Img_1M, Keypoint_Img1, descriptor_Img1);
			extractor.compute(Img_2M, Keypoint_Img2, descriptor_Img2);

			DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED); 
			//surf surf BRUTEFORCE FLANNBASED BRUTEFORCE_L1 fail===BRUTEFORCE_HAMMING BRUTEFORCE_HAMMINGLUT
			//surf sift BRUTEFORCE_SL2 1=FLANNBASED  BRUTEFORCE_L1... BRUTEFORCE_HAMMING--fail BRUTEFORCE_HAMMINGLUT--fail
			
			MatOfDMatch matches = new MatOfDMatch();

			matcher.match(descriptor_Img1, descriptor_Img2, matches);
			
			
			List<DMatch> matchesList = matches.toList();

			Double max_dist = Double.MIN_VALUE;
			Double min_dist = Double.MAX_VALUE;

			for (int i = 0; i < matchesList.size(); i++) 
			{
				Double dist = (double) matchesList.get(i).distance;
				if (dist < min_dist)
					min_dist = dist;
				if (dist > max_dist)
					max_dist = dist;
			}

			System.out.println("-- Max dist : " + max_dist);
			System.out.println("-- Min dist : " + min_dist);

			LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();

			for (int i = 0; i < matchesList.size(); i++) 
			{
				if (matchesList.get(i).distance <= (max_dist) / 3) 
					good_matches.addLast(matchesList.get(i));
			}

			Comparator<DMatch> c = new Comparator<DMatch>() 
			{
			@Override
			public int compare(DMatch arg0, DMatch arg1) 
			{
				return Double.valueOf(arg0.distance).compareTo(Double.valueOf(arg1.distance));
			}};
			
			Collections.sort(good_matches, c);
			gm.fromList(good_matches);

			try 
			{
			Mat img_matches = new Mat();
			Features2d.drawMatches(Img_1M , Keypoint_Img1 , Img_2M , Keypoint_Img2 , gm , img_matches, 
					new Scalar(255, 0, 0),new Scalar(0, 0, 255), new MatOfByte(), 2);

			String outputFileName = "Feature2d_Drawmatch";//new File(IMAGE_1_Path).getName();
			
			File outputFile = File.createTempFile("3"+outputFileName, ".png",new File(workingDir));
			
			Highgui.imwrite(outputFile.getAbsolutePath(), img_matches);
			} 
			catch (Exception e) {System.out.println("");}
			
			
			
			

			List<KeyPoint> Keypoint_Img1List = Keypoint_Img1.toList();
			List<KeyPoint> Keypoint_Img2List = Keypoint_Img2.toList();
			
			List<Point> Img1_Point_List = new LinkedList<Point>();
			List<Point> Img2_Point_List = new LinkedList<Point>();
			
			List<Point> Img1_PList = new LinkedList<Point>();
			List<Point> Img2_PList = new LinkedList<Point>();
			
			for (int i = 0; i < good_matches.size(); i++) 
			{
				Img1_Point_List.add(Keypoint_Img1List.get(good_matches.get(i).queryIdx).pt);
				Img2_Point_List.add(Keypoint_Img2List.get(good_matches.get(i).trainIdx).pt);
			
				if (Img1_PList.size() < 3) 
					Img1_PList.add(Keypoint_Img1List.get(good_matches.get(i).queryIdx).pt);
			
				if (Img2_PList.size() < 3) 
					Img2_PList.add(Keypoint_Img2List.get(good_matches.get(i).trainIdx).pt);
			
			}
			
			MatOfPoint2f Img1_Mat_O_Point = new MatOfPoint2f();
			Img1_Mat_O_Point.fromList(Img1_PList);

			MatOfPoint2f Img2_Mat_O_Point = new MatOfPoint2f();
			Img2_Mat_O_Point.fromList(Img2_PList);

			MatOfPoint2f Img1_MOP = new MatOfPoint2f();
			Img1_MOP.fromList(Img1_Point_List);

			MatOfPoint2f Img2_MOP = new MatOfPoint2f();
			Img2_MOP.fromList(Img2_Point_List);
		
			Mat H = Calib3d.findHomography(Img1_MOP, Img2_MOP,Calib3d.RANSAC, 20);
										// srcpoint  dest point   method    threshold
			
			try 
			{
				String outputFileName = "HHHHHHH--";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("4_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), H);
			} 
			catch (Exception e) {System.out.println("");}
			
			

			Mat Img1_Corners = new Mat(4, 1, CvType.CV_32FC2);
			Mat Img2_Corners = new Mat(4, 1, CvType.CV_32FC2);

			Img1_Corners.put(0, 0, new double[] { 0, 0 });
			Img1_Corners.put(1, 0, new double[] { Img_1M.cols(), 0 });
			Img1_Corners.put(2, 0, new double[] { Img_1M.cols(), Img_1M.rows() });
			Img1_Corners.put(3, 0, new double[] { 0, Img_1M.rows() });
			
			
			
			

			Core.perspectiveTransform(Img1_Corners, Img2_Corners, H);
							///       src, dest, mat
			
			
			Mat result = new Mat();
			Imgproc.warpPerspective(Img_1M, result, H,new Size(Img_1M.cols(), Img_1M.rows()));
									//src    des	mat match	size	
			
			try 
			{
				String outputFileName = "warpPerspective--";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("5_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), result);
			} 
			catch (Exception e) {System.out.println("");}
			
			
			
			
			Mat output = new Mat();
			Core.absdiff(Img_2M, result, output);
					//   src1     src2    destination
			try 
			{
				String outputFileName = "Abs_diffrent_in_both---";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("6_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output);
			} 
			catch (Exception e) {System.out.println("");}

			Mat output1 = new Mat();
			Core.subtract(Img_2M, result, output1); // f1-f2
							//src1,src2,dest
			try 
			{
				String outputFileName = "Substract_Image2_-_Result-";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("7_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output1);
				diff.setAdiffb(file);
			} 
			catch (Exception e) {System.out.println("");}

			Mat output2 = new Mat();
			Core.subtract(result, Img_2M, output2); // f2-f1
						//src1,src2,dest
			try 
			{
				String outputFileName = "Substract_Result_-_Image2-";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("8_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output2);
				diff.setBdiffa(file);
			} catch (Exception e) {System.out.println("");}
			
			
			Mat output3 = new Mat();
			Imgproc.threshold(output1, output3, 100, 255, Imgproc.THRESH_BINARY_INV);
								//src , dest
			try 
			{
				String outputFileName = "grayscal_to_binary---";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("9_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output3);
			} 
			catch (Exception e) {System.out.println("");}
			
		System.out.println(output2.type());
		System.out.println(output3.type());
		
			Mat output4 = new Mat();
			Imgproc.threshold(output2, output4, 100, 255, Imgproc.THRESH_BINARY_INV);
			try 
			{
				String outputFileName ="grayscale_to_binary--";// new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("10_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output4);
			} 
			catch (Exception e) {System.out.println("");}

			Mat black = Mat.zeros(output3.rows(), output3.cols(), output3.type());
			Mat output7 = new Mat();
		
			List<Mat> images = new ArrayList<Mat>();
			images.add(black);
			images.add(output2);
			images.add(output1);

			Core.merge(images, output7);

			for (int i = 0; i < output7.rows(); i++) 
			{
				for (int j = 0; j < output7.cols(); j++) 
				{
					double out[] = output7.get(i, j);
					boolean blackC = true;
					for (int k = 0; k < out.length; k++) 
					{
						if (out[k] >= 50.0) 
						{
							blackC = false;
							break;
						}
					}
				
					if (blackC) 
					{
						double d[] = { 255, 255, 255 };
						output7.put(i, j, d);
					}
				}
			}

			File file = null;

			try 
			{
				String outputFileName = "combine----";//new File(IMAGE_1_Path).getName();
				file = File.createTempFile("11_" + outputFileName, ".png", new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output7);
				diff.setCombine(file);
			} 
			catch (Exception e) {System.out.println("");}
		}
		catch(Exception e) {System.out.println("");}
		System.out.println("end-------07");
		return diff;
	}


//testing_detect----------------------------------------------------------------------------------------
	
	public Diff detect1(File file1, File file2) 
	{
		System.out.println("running-------07");
		
		Diff diff = new Diff();
		String IMAGE_1_Path = file1.getAbsolutePath();   // getting image 1 path
		String IMAGE_2_Path = file2.getAbsolutePath();	//getting image 2 path
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		try
		{
			Mat Img_1M = Highgui.imread(IMAGE_1_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);   //load image in matrix form 
			Mat Img_2M = Highgui.imread(IMAGE_2_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);

			System.out.println(Img_2M.rows() + "," + Img_2M.cols());
			System.out.println(Img_1M.rows() + "," + Img_1M.cols());

			int minCols = Math.min(Img_2M.cols(), Img_1M.cols());
			int minRows = Math.min(Img_2M.rows(), Img_1M.rows());

			Rect minRect = new Rect(0, 0, minCols, minRows);

			Img_1M = Img_1M.submat(minRect);
			Img_2M = Img_2M.submat(minRect);
			
			FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF); 
			
			MatOfKeyPoint Keypoint_Img1 = new MatOfKeyPoint();
			MatOfKeyPoint Keypoint_Img2 = new MatOfKeyPoint();

			detector.detect(Img_1M, Keypoint_Img1);
			detector.detect(Img_2M, Keypoint_Img2);

			DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
			
			Mat descriptor_Img1 = new Mat();
			Mat descriptor_Img2 = new Mat();

			extractor.compute(Img_1M, Keypoint_Img1, descriptor_Img1);
			extractor.compute(Img_2M, Keypoint_Img2, descriptor_Img2);

			DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED); 
			//surf surf BRUTEFORCE FLANNBASED BRUTEFORCE_L1 fail===BRUTEFORCE_HAMMING BRUTEFORCE_HAMMINGLUT
			//surf sift BRUTEFORCE_SL2 1=FLANNBASED  BRUTEFORCE_L1... BRUTEFORCE_HAMMING--fail BRUTEFORCE_HAMMINGLUT--fail
			
			MatOfDMatch matches = new MatOfDMatch();

			matcher.match(descriptor_Img1, descriptor_Img2, matches);
			
			
			List<DMatch> matchesList = matches.toList();

			Double max_dist = Double.MIN_VALUE;
			Double min_dist = Double.MAX_VALUE;

			for (int i = 0; i < matchesList.size(); i++) 
			{
				Double dist = (double) matchesList.get(i).distance;
				if (dist < min_dist)
					min_dist = dist;
				if (dist > max_dist)
					max_dist = dist;
			}

			System.out.println("-- Max dist : " + max_dist);
			System.out.println("-- Min dist : " + min_dist);

			LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();

			for (int i = 0; i < matchesList.size(); i++) 
			{
				if (matchesList.get(i).distance <= (max_dist) / 3) 
					good_matches.addLast(matchesList.get(i));
			}

			Comparator<DMatch> c = new Comparator<DMatch>() 
			{
			@Override
			public int compare(DMatch arg0, DMatch arg1) 
			{
				return Double.valueOf(arg0.distance).compareTo(Double.valueOf(arg1.distance));
			}};
			
			Collections.sort(good_matches, c);
			gm.fromList(good_matches);

			try 
			{
			Mat img_matches = new Mat();
			Features2d.drawMatches(Img_1M , Keypoint_Img1 , Img_2M , Keypoint_Img2 , gm , img_matches, 
					new Scalar(255, 0, 0),new Scalar(0, 0, 255), new MatOfByte(), 2);

			String outputFileName = "Feature2d_Drawmatch";//new File(IMAGE_1_Path).getName();
			
			File outputFile = File.createTempFile("3"+outputFileName, ".png",new File(workingDir));
			
			Highgui.imwrite(outputFile.getAbsolutePath(), img_matches);
			} 
			catch (Exception e) {System.out.println("");}
			
			
			
			

			List<KeyPoint> Keypoint_Img1List = Keypoint_Img1.toList();
			List<KeyPoint> Keypoint_Img2List = Keypoint_Img2.toList();
			
			List<Point> Img1_Point_List = new LinkedList<Point>();
			List<Point> Img2_Point_List = new LinkedList<Point>();
			
			List<Point> Img1_PList = new LinkedList<Point>();
			List<Point> Img2_PList = new LinkedList<Point>();
			
			for (int i = 0; i < good_matches.size(); i++) 
			{
				Img1_Point_List.add(Keypoint_Img1List.get(good_matches.get(i).queryIdx).pt);
				Img2_Point_List.add(Keypoint_Img2List.get(good_matches.get(i).trainIdx).pt);
			
				if (Img1_PList.size() < 3) 
					Img1_PList.add(Keypoint_Img1List.get(good_matches.get(i).queryIdx).pt);
			
				if (Img2_PList.size() < 3) 
					Img2_PList.add(Keypoint_Img2List.get(good_matches.get(i).trainIdx).pt);
			
			}
			
			MatOfPoint2f Img1_Mat_O_Point = new MatOfPoint2f();
			Img1_Mat_O_Point.fromList(Img1_PList);

			MatOfPoint2f Img2_Mat_O_Point = new MatOfPoint2f();
			Img2_Mat_O_Point.fromList(Img2_PList);

			MatOfPoint2f Img1_MOP = new MatOfPoint2f();
			Img1_MOP.fromList(Img1_Point_List);

			MatOfPoint2f Img2_MOP = new MatOfPoint2f();
			Img2_MOP.fromList(Img2_Point_List);
		
			Mat H = Calib3d.findHomography(Img1_MOP, Img2_MOP,Calib3d.RANSAC, 20);
										// srcpoint  dest point   method    threshold
			
			try 
			{
				String outputFileName = "HHHHHHH--";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("4_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), H);
			} 
			catch (Exception e) {System.out.println("");}
			
			

			Mat Img1_Corners = new Mat(4, 1, CvType.CV_32FC2);
			Mat Img2_Corners = new Mat(4, 1, CvType.CV_32FC2);

			Img1_Corners.put(0, 0, new double[] { 0, 0 });
			Img1_Corners.put(1, 0, new double[] { Img_1M.cols(), 0 });
			Img1_Corners.put(2, 0, new double[] { Img_1M.cols(), Img_1M.rows() });
			Img1_Corners.put(3, 0, new double[] { 0, Img_1M.rows() });
			
			
			
			

			Core.perspectiveTransform(Img1_Corners, Img2_Corners, H);
							///       src, dest, mat
			
			
			Mat result = new Mat();
			Imgproc.warpPerspective(Img_1M, result, H,new Size(Img_1M.cols(), Img_1M.rows()));
									//src    des	mat match	size	
			
			try 
			{
				String outputFileName = "warpPerspective--";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("5_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), result);
			} 
			catch (Exception e) {System.out.println("");}
			
			
			
			
			Mat o1 = new Mat();
			Core.absdiff(Img_2M, result, o1);
					//   src1     src2    destination
			Mat output = new Mat();
			Imgproc.threshold(o1,output,100,255,Imgproc.THRESH_TOZERO);
			try 
			{
				String outputFileName = "Abs_diffrent_in_both---";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("6_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output);
			} 
			catch (Exception e) {System.out.println("");}

			Mat o2 = new Mat();
			Core.subtract(Img_2M, result, o2); // f1-f2
							//src1,src2,dest
			
			Mat output1 = new Mat();
			Imgproc.threshold(o2,output1,100,255,Imgproc.THRESH_TOZERO);
			try 
			{
				String outputFileName = "Substract_Image2_-_Result-";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("7_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output1);
				diff.setAdiffb(file);
			} 
			catch (Exception e) {System.out.println("");}

			Mat o3 = new Mat();
			Core.subtract(result, Img_2M, o3); // f2-f1
						//src1,src2,dest
			Mat output2 = new Mat();
			Imgproc.threshold(o3,output2,100,255,Imgproc.THRESH_TOZERO);
			try 
			{
				String outputFileName = "Substract_Result_-_Image2-";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("8_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output2);
				diff.setBdiffa(file);
			} catch (Exception e) {System.out.println("");}
			
			
			Mat o4 = new Mat();
			Imgproc.threshold(output1, o4, 100, 255, Imgproc.THRESH_BINARY_INV);
								//src , dest
			Mat output3 = new Mat();
			Imgproc.threshold(o4,output3,100,255,Imgproc.THRESH_TOZERO);
			try 
			{
				String outputFileName = "grayscal_to_binary---";//new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("9_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output3);
			} 
			catch (Exception e) {System.out.println("");}
			
		System.out.println(output2.type());
		System.out.println(output3.type());
		
			Mat output4 = new Mat();
			Imgproc.threshold(output2, output4, 100, 255, Imgproc.THRESH_BINARY_INV);
			try 
			{
				String outputFileName ="grayscale_to_binary--";// new File(IMAGE_1_Path).getName();
				File file = File.createTempFile("10_" + outputFileName, ".png",new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output4);
			} 
			catch (Exception e) {System.out.println("");}

			Mat black = Mat.zeros(output3.rows(), output3.cols(), output3.type());
			Mat output7 = new Mat();
		
			List<Mat> images = new ArrayList<Mat>();
			images.add(black);
			images.add(output2);
			images.add(output1);

			Core.merge(images, output7);

			for (int i = 0; i < output7.rows(); i++) 
			{
				for (int j = 0; j < output7.cols(); j++) 
				{
					double out[] = output7.get(i, j);
					boolean blackC = true;
					for (int k = 0; k < out.length; k++) 
					{
						if (out[k] >= 50.0) 
						{
							blackC = false;
							break;
						}
					}
				
					if (blackC) 
					{
						double d[] = { 255, 255, 255 };
						output7.put(i, j, d);
					}
				}
			}

			File file = null;

			try 
			{
				String outputFileName = "combine----";//new File(IMAGE_1_Path).getName();
				file = File.createTempFile("11_" + outputFileName, ".png", new File(workingDir));
				Highgui.imwrite(file.getAbsolutePath(), output7);
				diff.setCombine(file);
			} 
			catch (Exception e) {System.out.println("");}
		}
		catch(Exception e) {System.out.println("");}
		System.out.println("end-------07");
		return diff;
	}

		
//-------------------------------------------------------------------------------------------------------	
	//08
	public Diff createDiff()					{ System.out.println("running-------08");Diff diff = null; return diff;}
	
	//09
	public Diff pdfCompare(File f1, File f2) 	{System.out.println("running-------09");return pdfCompare(f1,f2,workingDir);}

	//10
	public Diff getDiff(File file1, File file2) 
	{
		System.out.println("running-------10");
		Diff diff = new Diff();
		Mat Mat_1 = null, Mat_2 = null;
		String filePath = null;
		
		if (file1 == null && file2 != null) 
		{
			String IMAGE_1_Path = file2.getAbsolutePath();
			filePath = file2.getAbsolutePath();

			Mat_2 = Highgui.imread(IMAGE_1_Path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			Imgproc.threshold(Mat_2, Mat_2, 100, 255, Imgproc.THRESH_BINARY_INV);
			Mat_1 = Mat.zeros(Mat_2.rows(), Mat_2.cols(), Mat_2.type());
		}

		if (file1 != null && file2 == null) 
		{
			String IMAGE_1_Path = file1.getAbsolutePath();
			filePath = file1.getAbsolutePath();

			Mat_1 = Highgui.imread(IMAGE_1_Path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			Imgproc.threshold(Mat_1, Mat_1, 100, 255, Imgproc.THRESH_BINARY_INV); 
			Mat_2 = Mat.zeros(Mat_1.rows(), Mat_1.cols(), Mat_1.type());
		}

		List<Mat> images = new ArrayList<Mat>();
		Mat black = Mat.ones(Mat_1.rows(), Mat_1.cols(), Mat_1.type());
		images.add(black);
		images.add(Mat_2);
		images.add(Mat_1);

		Mat mat = new Mat();
		Core.merge(images, mat);

		for (int i = 0; i < mat.rows(); i++) 
		{
			for (int j = 0; j < mat.cols(); j++) 
			{
				double out[] = mat.get(i, j);
				boolean blackC = true;
				for (int k = 0; k < out.length; k++) 
				{
					if (out[k] >= 50.0) 
					{
						blackC = false;
						break;
					}
				}
				if (blackC) 
				{
					double d[] = { 255, 255, 255 };
					mat.put(i, j, d);
				}
			}
		}

		File file = null;
		try 
		{
			String outputFileName = new File(filePath).getName();
			file = File.createTempFile("7" + outputFileName, ".png", new File(workingDir));
			Highgui.imwrite(file.getAbsolutePath(), mat);
			diff.setCombine(file);
		} 
		catch (Exception e) {System.out.println("");}
		
		try 
		{
			String outputFileName = new File(filePath).getName();
			file = File.createTempFile("8" + outputFileName, ".png", new File(workingDir));
			Highgui.imwrite(file.getAbsolutePath(), Mat_1);
			diff.setAdiffb(file);
		}
		catch (Exception e) {System.out.println("");}

		try  
		{
			String outputFileName = new File(filePath).getName();
			file = File.createTempFile("9" + outputFileName, ".png", new File(workingDir));
			Highgui.imwrite(file.getAbsolutePath(), Mat_2);
			diff.setBdiffa(file);
		} 
		catch (Exception e) {System.out.println("");}
		
		return diff;
	}

	//11
	/*public List<File> getImageFile(File file) 
	{
		System.out.println("running-------11");
		List<File> Temp_Files = new ArrayList<File>();
		List<Image> images = null;
		try 
		{
			PDFDocument document = new PDFDocument();
			document.load(file);
			SimpleRenderer renderer = new SimpleRenderer();
			// renderer.setMaxProcessCount(Runtime.getRuntime().availableProcessors());

			// set resolution (in DPI)
			renderer.setResolution(150);
			images = renderer.render(document);
			System.out.println("Images obtained.Images size:" + images.size());
			for (int i = 0; i < images.size(); i++) 
			{
				Image image = images.get(i);
				RenderedImage rImage = (RenderedImage) image;
				
				String outputFile = file.getName().substring(0,file.getName().lastIndexOf("."));
				System.out.println("of:" + outputFile);
				File tempFile = File.createTempFile(outputFile, ".png",new File(workingDir));
				Temp_Files.add(tempFile);
				ImageIO.write(rImage, "png", tempFile);
				System.out.println("Image written to disk:"+ tempFile.getAbsolutePath());
				if (image != null) 
				{
					image.flush();
					System.out.println("image flushed");
				}
			}
		} 
		catch (Throwable e) {System.out.println("saurabh");}
		
		return Temp_Files;
	}*/

//--------------------------------------------------------------------------------------------------------
	//12
	public File selectPdf()
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

	//13
	public void Image_To_PDF() 
	{
		Document document = new Document();
		String input = "C:/Users/ATUL/Documents/work/Example/output/11.png"; // .gif and .jpg are ok too!
		String output = "C:/Users/ATUL/Documents/work/Example/output/11.pdf";
		
		//com.lowagie.text.Rectangle r=new com.lowagie.text.Rectangle(0,0,1300,900);
		try 
		{
			FileOutputStream fos = new FileOutputStream(output);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.open();
			document.setPageSize(com.lowagie.text.Image.getInstance(input));
			document.open();
			document.add(com.lowagie.text.Image.getInstance(input));
			
			document.close();
			writer.close();
		}
		catch (Exception e) {e.printStackTrace();}
		
	}
	
}



class ImageCompare
{
	public static void main(String[] args) 
	{
		
		long t1 = System.currentTimeMillis();
	
		String workingDir = "C:/Users/Saurabh/Desktop/ImgDiff/output/";
		File f = new File("C:/Users/Saurabh/Desktop/final image diff/temp/");
		//File f2 = new File("C:/Users/ATUL/Documents/work/Example/temp/2.jpg");
		Comparision c = new Comparision(workingDir);
		c.Compare_Directory(f, workingDir);
		//c.Image_To_PDF();
		//c.detect(f1, f2);
		System.out.println("RRR");
		long t2 = System.currentTimeMillis();
		System.out.println("time:" + (t2 - t1) / 1000);
	}
}



	

