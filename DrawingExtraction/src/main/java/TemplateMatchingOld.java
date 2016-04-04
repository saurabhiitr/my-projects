

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;

public class TemplateMatchingOld
{   static int i=0;
	public void ExtractText(Point topleft,Point bottomright)
	{
		long t1=System.currentTimeMillis();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String IMAGE_1_Path = "C:/Users/Saurabh/Desktop/temp/original_template.png";   // getting
		// image
		// 1
		// path
		String IMAGE_2_Path = "C:/Users/Saurabh/Desktop/temp/original.png";    // getting
		// image
		// 2
		// path
        String name = "matchfile";
		Mat img_1 = Highgui.imread(IMAGE_1_Path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
		Mat img_2 = Highgui.imread(IMAGE_2_Path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

		if (img_1 == null || img_2 == null)
			System.out.println(" --(!) Error reading images \n");

		// img_1 = img_1.submat(minRect);
		// img_2 = img_2.submat(minRect);

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB); // (minHessian);//
		// SurfFeatureDetector detector( minHessian );

		MatOfKeyPoint keypoints_1 = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_2 = new MatOfKeyPoint();
		// vector<KeyPoint> keypoints_1, keypoints_2;

		detector.detect(img_1, keypoints_1);
		detector.detect(img_2, keypoints_2);

		// -- Step 2: Calculate descriptors (feature vectors)
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);// (minHessian);//
		// SurfDescriptorExtractor extractor;

		// surf-sift---186
		// sift-sift-----91
		// surf-surf-----25
		// sift-surf----43
		Mat descriptors_1 = new Mat();
		Mat descriptors_2 = new Mat();

		extractor.compute(img_1, keypoints_1, descriptors_1);
		extractor.compute(img_2, keypoints_2, descriptors_2);

		// -- Step 3: Matching descriptor vectors using FLANN matcher
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		// FlannBasedMatcher matcher;

		MatOfDMatch matches = new MatOfDMatch();
		// std::vector< DMatch > matches;
		matcher.match(descriptors_1, descriptors_2, matches);

		double max_dist = 0;
		double min_dist = 100;

		List<DMatch> matchesList = matches.toList();
		// -- Quick calculation of max and min distances between keypoints
		for (int i = 0; i < descriptors_1.rows(); i++)
		{
			double dist = matchesList.get(i).distance;
			if (dist < min_dist)
				min_dist = dist;

			if (dist > max_dist)
				max_dist = dist;
		}

		//				System.out.println("-- Max dist : %f \n" + max_dist);
		//				System.out.println("-- Min dist : %f \n" + min_dist);

		// -- Draw only "good" matches (i.e. whose distance is less than
		// 2*min_dist,
		// -- or a small arbitary value ( 0.02 ) in the event that min_dist is
		// very
		// -- small)
		// -- PS.- radiusMatch can also be used here.

		LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
		// std::vector< DMatch > good_matches;

		for (int i = 0; i < descriptors_1.rows(); i++)
		{
			if (matchesList.get(i).distance <= Math.max(3 * min_dist, 0.02))
			{
				good_matches.addLast(matchesList.get(i));
			}
		}

		MatOfDMatch gm = new MatOfDMatch();
		gm.fromList(good_matches);

		// -- Draw only "good" matches
		Mat img_matches = new Mat();
		// drawMatches( img_1, keypoints_1, img_2, keypoints_2,
		// good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
		// vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );

		Features2d.drawMatches(img_1, keypoints_1, img_2, keypoints_2, gm, img_matches, new Scalar(255, 0, 0), new Scalar(0, 0,
				255), new MatOfByte(), 2);
		// -- Show detected matches
		// imshow( "Good Matches", img_matches );

		try
		{
			File outputFile = new File("C:/Users/Saurabh/Desktop/match/" + name  + "s" + i + ".png");

			Highgui.imwrite(outputFile.getAbsolutePath(), img_matches);
		} catch (Exception e)
		{
			System.out.println("");
		}

	
		List<KeyPoint> Keypoint_Img1List = keypoints_1.toList();
		List<KeyPoint> Keypoint_Img2List = keypoints_2.toList();

		// *****************
		List<Point> obj = new LinkedList<Point>();
		List<Point> scene = new LinkedList<Point>();

		for (int i = 0; i < good_matches.size(); i++)
		{
			obj.add(Keypoint_Img1List.get(good_matches.get(i).queryIdx).pt);
			scene.add(Keypoint_Img2List.get(good_matches.get(i).trainIdx).pt);

		}

		MatOfPoint2f obj_p2f = new MatOfPoint2f();
		obj_p2f.fromList(obj);

		MatOfPoint2f scene_p2f = new MatOfPoint2f();
		scene_p2f.fromList(scene);

		Mat H = Calib3d.findHomography(obj_p2f, scene_p2f, Calib3d.RANSAC, 20);

		Mat Img1_Corners = new Mat(4, 1, CvType.CV_32FC2);
		Mat Img2_Corners = new Mat(4, 1, CvType.CV_32FC2);

		Img1_Corners.put(0, 0, new double[] { 0, 0 });
		Img1_Corners.put(1, 0, new double[] { img_1.cols(), 0 });
		Img1_Corners.put(2, 0, new double[] { img_1.cols(), img_1.rows() });
		Img1_Corners.put(3, 0, new double[] { 0, img_1.rows() });

		Point P3=new Point(Img1_Corners.get(0, 0));
		Point P4=new Point(Img1_Corners.get(2, 0));
		Double width_img1= Math.abs(P3.x - P4.x);
		Double height_img1=Math.abs(P3.y - P4.y);
		
		//int area_original=(int) Math.abs( width_img1 * height_img1 );

		System.out.println("width_img1 " + width_img1 + "height_img1 " + height_img1);
		Core.perspectiveTransform(Img1_Corners, Img2_Corners, H);
		Point P1=new Point(Img2_Corners.get(0, 0));
		//				System.out.println("point p1 = "+ P1.x + " " + P1.y);
		Point P2=new Point(Img2_Corners.get(2, 0));
		//				System.out.println("point p2 = "+ P2.x + " " + P2.y);
		Double width_img2= Math.abs(P1.x - P2.x);
		Double height_img2=Math.abs(P1.y - P2.y);
		
		//int area_find=(int) Math.abs( width_img2 * height_img2 );
		
		System.out.println("width_img2 " + width_img2 + "height_img2 " + height_img2);
//		Double ratio_width = width_img1/width_img2;
//		Double ratio_height = height_img1/height_img2;
//		System.out.println("Ratio of width = " + ratio_width);
//		System.out.println("Ratio of height = " + ratio_height);
		
		//Core.rectangle(img_2,P1 ,P2, new Scalar(0,0,0),-1);
		
		Point Ocr_leftup = new Point( P1.x + (int)(width_img2 / width_img1 * topleft.x ) , P1.y + (int)(height_img2 / height_img1 * topleft.y) );
		Point ocr_bottomright = new Point((int)(P1.x + width_img2 / width_img1 * bottomright.x),(int)(P1.y + height_img2/height_img1 * bottomright.y));
		System.out.println("final upper corner" + Ocr_leftup.x + " , " + Ocr_leftup.y);
		System.out.println("bottom right corner" + ocr_bottomright.x + " , "+ ocr_bottomright.y);
		//int ratio=(area_find *100) / area_original ;
//		if(ratio > 70)
//			System.out.println(area_original + " " + area_find + " " + ratio + "stamp found" );
		Core.rectangle(img_2,Ocr_leftup ,ocr_bottomright, new Scalar(0,0,0),-1);
		//Core.line(img_2, new Point(Img2_Corners.get(0, 0)), new Point(Img2_Corners.get(1, 0)), new Scalar(0, 255, 0), 4);

		/*
		 * LinearGradientPaint( img_matches,
		 * scene_corners[1] + Point2f( img_object.cols, 0),
		 * scene_corners[2] + Point2f( img_object.cols, 0),
		 * Scalar( 0, 255, 0),
		 * 4 );
		 */
		// Core.line(img_2, new Point(Img2_Corners.get(1, 0)), new Point(Img2_Corners.get(2, 0)), new Scalar(0, 255, 0), 4);

		/*
		 * line( img_matches,
		 * scene_corners[2] + Point2f( img_object.cols, 0),
		 * scene_corners[3] + Point2f( img_object.cols, 0),
		 * Scalar( 0, 255, 0),
		 * 4 );
		 */
		//   Core.line(img_2, new Point(Img2_Corners.get(2, 0)), new Point(Img2_Corners.get(3, 0)), new Scalar(0, 255, 0), 4);

		/*
		 * line( img_matches,
		 * scene_corners[3] + Point2f( img_object.cols, 0),
		 * scene_corners[0] + Point2f( img_object.cols, 0),
		 * Scalar( 0, 255, 0),
		 * 4 );
		 */
		//  Core.line(img_2, new Point(Img2_Corners.get(3, 0)), new Point(Img2_Corners.get(0, 0)), new Scalar(0, 255, 0), 4);

		// -- Show detected matches
		// imshow( "Good Matches & Object detection", img_matches );

		// waitKey(0);////////----------------------------------------------------------------------------------------------------------------

		// return 0;

		// Mat o1 = new Mat();
		// Core.absdiff(img_1, img_2, o1);
		// src1 src2 destination
		// Mat output1 = new Mat();
		// Imgproc.threshold(o1,output1,100,255,Imgproc.THRESH_TOZERO);
		try
		{

			File file_write = new File("C:/Users/Saurabh/Desktop/match/"+ name + i + ".png");
			Highgui.imwrite(file_write.getAbsolutePath(), img_2);
		} catch (Exception e)
		{
			System.out.println("");
		}
	i++;
     long t2=System.currentTimeMillis();
     System.out.println(t2-t1);

}
/**
 * @function readme
 */
void readme()
{
	System.out.println(" Usage: ./SURF_FlannMatcher <img1> <img2>\n");
}
}