

import static net.sourceforge.tess4j.ITessAPI.TRUE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;


import javax.imageio.ImageIO;

import org.opencv.core.Point;

import com.sun.jna.Pointer;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.ITessAPI.ETEXT_DESC;
import net.sourceforge.tess4j.ITessAPI.TessBaseAPI;
import net.sourceforge.tess4j.ITessAPI.TessPageIterator;
import net.sourceforge.tess4j.ITessAPI.TessPageSegMode;
import net.sourceforge.tess4j.ITessAPI.TessResultIterator;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.util.LoadLibs;

public class DetectWord {

	TessAPI api =null;
	TessBaseAPI handle = TessAPI1.TessBaseAPICreate();
	String datapath = "C:/Users/Saurabh/Desktop/tesseract project/TESTTess/tessdata/";
	String language = "eng";

	public DetectWord() {
		super();
		api = LoadLibs.getTessAPIInstance();
		api.TessBaseAPIInit3(handle,datapath, language);
		api.TessBaseAPISetPageSegMode(handle, TessPageSegMode.PSM_AUTO);
	}

	public  void find(Point p1, Point p2) {
		String title = "";
		File file = new File("C:/Users/Saurabh/Desktop/temp/original.png");
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream(file));
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		ByteBuffer buf = ImageIOHelper.convertImageData(image);
		int bpp = image.getColorModel().getPixelSize();
		int bytespp = bpp / 8;
		int bytespl = (int) Math.ceil(image.getWidth() * bpp / 8.0);

		api.TessBaseAPISetImage(handle, buf, image.getWidth(), image.getHeight(), bytespp, bytespl);
		api.TessBaseAPISetRectangle(handle, (int) p1.x, (int) p1.y, (int) Math.abs(p1.x - p2.x),
				(int) Math.abs(p1.y - p2.y));

		ETEXT_DESC monitor = new ETEXT_DESC();
		TessAPI1.TessBaseAPIRecognize(handle, monitor);
		TessResultIterator ri = TessAPI1.TessBaseAPIGetIterator(handle);
		TessPageIterator pi = TessAPI1.TessResultIteratorGetPageIterator(ri);
		TessAPI1.TessPageIteratorBegin(pi);
		// System.out.println("Bounding boxes:\nchar(s) left top right bottom
		// confidence font-attributes");
		int level = TessAPI1.TessPageIteratorLevel.RIL_WORD;
		do {

			Pointer ptr = TessAPI1.TessResultIteratorGetUTF8Text(ri, level);
			String word = new String();
			if (ptr != null)
				word = ptr.getString(0);
			TessAPI1.TessDeleteText(ptr);
			title = title + " " + word;
			// System.out.println(String.format("%s %d %d %d %d %f", word,
			// left, top, right, bottom, confidence));

		} while (TessAPI1.TessPageIteratorNext(pi, level) == TRUE);
		// Pointer utf8Text = api.TessBaseAPIGetUTF8Text(handle);
		System.out.println(title.trim());
		buf.clear();
		buf=null;
	}
}
