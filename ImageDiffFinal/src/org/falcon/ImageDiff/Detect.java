package org.falcon.ImageDiff;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

public class Detect {

	
	private String workingDir;
	
	public Detect(String outputDIR) {
		workingDir = outputDIR;
	}

	public Diff detect(File file1, File file2) {

		Diff diff = new Diff();
		String First_Image_path = file1.getAbsolutePath();

		String Second_Image_path = file2.getAbsolutePath();
		System.load("C:/Users/Saurabh/Desktop/opencv/build/java/x64/opencv_java249.dll");
		try{
			Mat img_object = Highgui.imread(First_Image_path,Highgui.CV_LOAD_IMAGE_GRAYSCALE); // 0 =
		// CV_LOAD_IMAGE_GRAYSCALE
		
		
		Mat img_scene = Highgui.imread(Second_Image_path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);

		System.out.println(img_scene.rows() + "," + img_scene.cols());
		System.out.println(img_object.rows() + "," + img_object.cols());

		int minCols = Math.min(img_scene.cols(), img_object.cols());
		int minRows = Math.min(img_scene.rows(), img_object.rows());

		Rect minRect = new Rect(0, 0, minCols, minRows);

		img_object = img_object.submat(minRect);
		img_scene = img_scene.submat(minRect);

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB); 
		MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_scene = new MatOfKeyPoint();

		detector.detect(img_object, keypoints_object);
		detector.detect(img_scene, keypoints_scene);

		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
		Mat descriptor_object = new Mat();
		Mat descriptor_scene = new Mat();

		extractor.compute(img_object, keypoints_object, descriptor_object);
		extractor.compute(img_scene, keypoints_scene, descriptor_scene);

		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING); // 1 =
		// FLANNBASED
		MatOfDMatch matches = new MatOfDMatch();

		matcher.match(descriptor_object, descriptor_scene, matches);
		List<DMatch> matchesList = matches.toList();

		Double max_dist = Double.MIN_VALUE;
		Double min_dist = Double.MAX_VALUE;

		for (int i = 0; i < matchesList.size(); i++) {
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

		for (int i = 0; i < matchesList.size(); i++) {
			if (matchesList.get(i).distance <= (max_dist) / 3) {
				good_matches.addLast(matchesList.get(i));
			}
		}

		Comparator<DMatch> c = new Comparator<DMatch>() {

			@Override
			public int compare(DMatch arg0, DMatch arg1) {
				
				return Double.valueOf(arg0.distance).compareTo(
						Double.valueOf(arg1.distance));
			}

		};
		Collections.sort(good_matches, c);
		gm.fromList(good_matches);

		try {

			Mat img_matches = new Mat();
			Features2d.drawMatches(img_object, keypoints_object, img_scene,
					keypoints_scene, gm, img_matches, new Scalar(255, 0, 0),
					new Scalar(0, 0, 255), new MatOfByte(), 2);

			String outputFileName = new File(First_Image_path).getName();

			File outputFile = File.createTempFile(outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(outputFile.getAbsolutePath(), img_matches);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		List<Point> objList = new LinkedList<Point>();
		List<Point> sceneList = new LinkedList<Point>();

		List<KeyPoint> keypoints_objectList = keypoints_object.toList();
		List<KeyPoint> keypoints_sceneList = keypoints_scene.toList();
		List<Point> obj1 = new LinkedList<Point>();
		List<Point> scene1 = new LinkedList<Point>();
		for (int i = 0; i < good_matches.size(); i++) {
			objList.add(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
			sceneList
					.add(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
			if (obj1.size() < 3) {
				obj1.add(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
			}
			if (scene1.size() < 3) {
				scene1.add(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
			}
		}
		MatOfPoint2f objMat = new MatOfPoint2f();
		objMat.fromList(obj1);

		MatOfPoint2f sceneMat = new MatOfPoint2f();
		sceneMat.fromList(scene1);

		MatOfPoint2f obj = new MatOfPoint2f();
		obj.fromList(objList);

		MatOfPoint2f scene = new MatOfPoint2f();
		scene.fromList(sceneList);
		Mat H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 20);

		Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
		Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

		obj_corners.put(0, 0, new double[] { 0, 0 });
		obj_corners.put(1, 0, new double[] { img_object.cols(), 0 });
		obj_corners.put(2, 0,
				new double[] { img_object.cols(), img_object.rows() });
		obj_corners.put(3, 0, new double[] { 0, img_object.rows() });

		Core.perspectiveTransform(obj_corners, scene_corners, H);


		Mat result = new Mat();

		Imgproc.warpPerspective(img_object, result, H,
				new Size(img_object.cols(), img_object.rows()));
		Mat output = new Mat();

		Core.absdiff(img_scene, result, output);
		try {

			String outputFileName = new File(First_Image_path).getName();
			File file = File.createTempFile("1" + outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		Mat output1 = new Mat();
		Core.subtract(img_scene, result, output1); // f1-f2
		try {

			String outputFileName = new File(First_Image_path).getName();
			File file = File.createTempFile("2" + outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output1);
			diff.setAdiffb(file);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		Mat output2 = new Mat();
		Core.subtract(result, img_scene, output2); // f2-f1
		try {

			String outputFileName = new File(First_Image_path).getName();
			File file = File.createTempFile("2" + outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output2);
			diff.setBdiffa(file);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		Mat output3 = new Mat();
		Imgproc.threshold(output1, output3, 100, 255, Imgproc.THRESH_BINARY_INV);
		try {

			String outputFileName = new File(First_Image_path).getName();
			File file = File.createTempFile("3" + outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output3);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}

		System.out.println(output2.type());
		System.out.println(output3.type());
		Mat output4 = new Mat();
		Imgproc.threshold(output2, output4, 100, 255, Imgproc.THRESH_BINARY_INV);
		try {

			String outputFileName = new File(First_Image_path).getName();
			File file = File.createTempFile("4" + outputFileName, ".png",
					new File(workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output4);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}


		Mat output7 = new Mat();
		List<Mat> images = new ArrayList<Mat>();
		Mat black = Mat.zeros(output3.rows(), output3.cols(), output3.type());
		images.add(black);
		images.add(output2);
		images.add(output1);

		Core.merge(images, output7);

		for (int i = 0; i < output7.rows(); i++) {
			for (int j = 0; j < output7.cols(); j++) {
				double out[] = output7.get(i, j);
				boolean blackC = true;
				for (int k = 0; k < out.length; k++) {

					if (out[k] >= 50.0) {
						blackC = false;
						break;
					}
				}
				if (blackC) {
					double d[] = { 255, 255, 255 };
					output7.put(i, j, d);
				}
			}
		}

		File file = null;

		try {

			String outputFileName = new File(First_Image_path).getName();
			file = File.createTempFile("6" + outputFileName, ".png", new File(
					workingDir));

			Highgui.imwrite(file.getAbsolutePath(), output7);
			diff.setCombine(file);
		} catch (Exception e) {
			//logger.debug(e.getMessage(), e);
			//e.printStackTrace();
		}
    }
		catch(Exception e)
		{e.printStackTrace();}
		
		return diff;
	}
}
