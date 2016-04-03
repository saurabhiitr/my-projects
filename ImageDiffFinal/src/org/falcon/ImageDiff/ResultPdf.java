package org.falcon.ImageDiff;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

public class ResultPdf {
    public void d(File f1, File f2, String outputDir,List<Diff> diffs){
    	
    	Diff diff = new Diff();

		String nameAdiffB = FilenameUtils.removeExtension(f1.getName())
				+ "_diff_" + FilenameUtils.removeExtension(f2.getName())
				+ ".pdf";
		String nameBdiffA = FilenameUtils.removeExtension(f2.getName())
				+ "_diff_" + FilenameUtils.removeExtension(f1.getName())
				+ ".pdf";
		String nameAcombineB = FilenameUtils.removeExtension(f1.getName())
				+ "_combine_" + FilenameUtils.removeExtension(f2.getName())
				+ ".pdf";
		String outputAdiffB = outputDir.endsWith(File.separator) ? outputDir
				+ nameAdiffB : outputDir + File.separator + nameAdiffB;
		String outputBdiffA = outputDir.endsWith(File.separator) ? outputDir
				+ nameBdiffA : outputDir + File.separator + nameBdiffA;
		String outputAcombineB = outputDir.endsWith(File.separator) ? outputDir
				+ nameAcombineB : outputDir + File.separator + nameAcombineB;
		try {
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputAdiffB);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.open();
			int flag=0;
			
			for (Diff d : diffs) {
				String input = d.getAdiffb().getAbsolutePath();

				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
				if(flag==0)
                {document.open();flag++;}
				
				document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
				
			}
			document.close();
			writer.close();
			diff.setAdiffb(new File(outputAdiffB));
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		try {
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputBdiffA);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.open();
			int flag=0;
			for (Diff d : diffs) {
				String input = d.getBdiffa().getAbsolutePath();
				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
                if(flag==0)
                    {document.open();flag++;}
                
                document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			}
			document.close();
			writer.close();
			diff.setBdiffa(new File(outputBdiffA));
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		try {
			Document document = new Document();
			FileOutputStream fos = new FileOutputStream(outputAcombineB);
			PdfWriter writer = PdfWriter.getInstance(document, fos);
			writer.open();
			int flag=0;
			for (Diff d : diffs) {
				String input = d.getCombine().getAbsolutePath();
				document.setPageSize(com.lowagie.text.pdf.codec.PngImage.getImage(input));
                if(flag==0)
                    {document.open();flag++;}
                
                document.add(com.lowagie.text.pdf.codec.PngImage.getImage(input));
			}
			document.close();
			writer.close();
			diff.setCombine(new File(outputAcombineB));
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}
    }
}
