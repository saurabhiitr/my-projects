package org.falcon.ImageDiff;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FilenameUtils;

public class Main {
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
	
	public static File imageResolution(File f){
		File imageFile = new File( "C:\\Users\\Saurabh\\Desktop\\ImgDiff\\output\\" + f.getName() );
		try{
			Image image = ImageIO.read(f);
		    int width = image.getWidth(null);
		    int height = image.getHeight(null);
		BufferedImage img1=new BufferedImage(width*2, height*2, BufferedImage.TYPE_INT_RGB);
		Graphics2D bufImageGraphics = img1.createGraphics();
        bufImageGraphics.drawImage(image, 0, 0,width*2,height*2, null);
        
        if (imageFile.exists()) {
            imageFile.delete();
        }
        ImageIO.write(img1, "png", imageFile);
       
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 return imageFile;
}
	
   public static void main(String [] Args) {
	   String workingDir = "C:\\Users\\Saurabh\\Desktop\\ImgDiff\\output\\new\\";
	   Main m=new Main();
		long t1 = System.currentTimeMillis();
		
		Detect d=new Detect(workingDir);
		List<Diff> result = new LinkedList<Diff>();
		
		//File f1 = new File("C:\\Users\\Saurabh\\Desktop\\ImgDiff\\tmp\\11.pdf");
		//File f2 = new File("C:\\Users\\Saurabh\\Desktop\\ImgDiff\\tmp\\12.pdf");
		File f1 = m.selectPdf();
		File f2=  m.selectPdf();
		if(f1.isFile() && f2.isFile())
		{
			List<File> f1s=new ArrayList<File>();
			List<File> f2s=new ArrayList<File>();
			SimpleRendererEx t=new SimpleRendererEx();
			if((FilenameUtils.getExtension(f1.getAbsolutePath())).compareTo("pdf")==0) 
			{
			 f1s = t.convertImage(f1, workingDir);
			 f2s = t.convertImage(f2, workingDir);
			}
			else
			{  try {
				if(ImageIO.read(f1)==null)
					System.out.println("unsupported file type");
				else
				   f1s.add(imageResolution(f1));
				
				if(ImageIO.read(f2)==null)
					System.out.println("unsupported file type");
				else
				   f2s.add(imageResolution(f2));
			    } catch (IOException e) {
				e.printStackTrace();
			     }
			}
			for(int i=0;i<f1s.size() && i<f2s.size();i++){
			Diff o = d.detect(f1s.get(i), f2s.get(i));
			result.add(o);
			}
			ResultPdf r=new ResultPdf();
			r.d(f1, f2, workingDir, result);
			
		}
		long t2 = System.currentTimeMillis();

		System.out.println("time:" + (t2 - t1) / 1000);   
	
   }
}
