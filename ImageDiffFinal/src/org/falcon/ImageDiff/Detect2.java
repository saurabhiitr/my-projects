package org.falcon.ImageDiff;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Detect2
{
    public static void main(String args[] )
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        String IMAGE_1_Path = "C:/Users/Saurabh/Desktop/ImgDiff/tmp/11_1.PNG";   // getting image 1 path
        String IMAGE_2_Path = "C:/Users/Saurabh/Desktop/ImgDiff/tmp/12_1.PNG";  //getting image 2 path
        
        Mat img_1 = Highgui.imread(IMAGE_1_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat img_2 = Highgui.imread(IMAGE_2_Path,Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        if( img_1==null || img_2==null )
            System.out.println(" --(!) Error reading images \n"); 

    //-- Step 1: Detect the keypoints using SURF Detector
        int minHessian = 400;

        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT); //(minHessian);//
        //SurfFeatureDetector detector( minHessian );
        
        MatOfKeyPoint keypoints_1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints_2 = new MatOfKeyPoint();
        //vector<KeyPoint> keypoints_1, keypoints_2;


        detector.detect( img_1, keypoints_1 );
        detector.detect( img_2, keypoints_2 );

    //-- Step 2: Calculate descriptors (feature vectors)
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);//(minHessian);//
        //SurfDescriptorExtractor extractor;

        //surf-sift----186
        //sift-sift----91
        //sift-surf----43
        //surf-surf----25
        //orb-orb---7
        Mat descriptors_1=new Mat();
        Mat descriptors_2= new Mat();

        extractor.compute( img_1, keypoints_1, descriptors_1 );
        extractor.compute( img_2, keypoints_2, descriptors_2 );

    //-- Step 3: Matching descriptor vectors using FLANN matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        //FlannBasedMatcher matcher;
        
        MatOfDMatch matches = new MatOfDMatch();
        //std::vector< DMatch > matches;
        matcher.match( descriptors_1, descriptors_2, matches );

        double max_dist = 0; double min_dist = 100;

        List<DMatch> matchesList = matches.toList();
        //-- Quick calculation of max and min distances between keypoints
        for( int i = 0; i < descriptors_1.rows(); i++ )
        { 
            double dist = matchesList.get(i).distance ;
            if( dist < min_dist ) min_dist = dist;
        
            if( dist > max_dist ) max_dist = dist;
        }

        System.out.println("-- Max dist : %f \n"+max_dist );
        System.out.println("-- Min dist : %f \n"+ min_dist );

        //-- Draw only "good" matches (i.e. whose distance is less than 2*min_dist,
        //-- or a small arbitary value ( 0.02 ) in the event that min_dist is very
        //-- small)
        //-- PS.- radiusMatch can also be used here.

        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        //std::vector< DMatch > good_matches;

        for( int i = 0; i < descriptors_1.rows(); i++ )
        { 
            if( matchesList.get(i).distance <= Math.max(2*min_dist, 0.02) )
            { 
                good_matches.addLast(matchesList.get(i)); 
            }
        }
        
        MatOfDMatch gm = new MatOfDMatch();
        gm.fromList(good_matches);
        
    //-- Draw only "good" matches
        Mat img_matches =new Mat();
        //drawMatches( img_1, keypoints_1, img_2, keypoints_2,
            //  good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
                //vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS );

        
        Features2d.drawMatches(img_1 , keypoints_1 , img_2 , keypoints_2 , gm , img_matches,
                new Scalar(255, 0, 0),new Scalar(0, 0, 255), new MatOfByte(), 2);
        //-- Show detected matches
        //imshow( "Good Matches", img_matches );
        
        
        try{
        File outputFile = new File("C:/Users/Saurabh/Desktop/ImgDiff/output/ORB_ORB.png");
        Highgui.imwrite(outputFile.getAbsolutePath(), img_matches);
        } 
        catch (Exception e) {System.out.println("");}
        
        
        
        
        for( int i = 0; i < (int)good_matches.size(); i++ )
        { 
            System.out.println( "-- Good Match ["+i+"] Keypoint 1:"+good_matches.get(i).queryIdx+"   -- Keypoint 2:"+ good_matches.get(i).trainIdx); 
        }
        //waitKey(0);
            
        //return 0;
        
        /*Mat o1 = new Mat();
        Core.absdiff(img_1, img_2, o1);
                //   src1     src2    destination
        //Mat output1 = new Mat();
        //Imgproc.threshold(o1,output1,100,255,Imgproc.THRESH_TOZERO);
        try 
        {
            String outputFileName = "Abs_diffrent_in_both---";//new File(IMAGE_1_Path).getName();
            File file = File.createTempFile("2_" + outputFileName, ".png",new File("C:/Users/ATUL/Desktop/signature/"));
            Highgui.imwrite(file.getAbsolutePath(), o1);
        } 
        catch (Exception e) {System.out.println("");}
        */
        
    }

    /**
     * @function readme
     */
    void readme()
    { System.out.println(" Usage: ./SURF_FlannMatcher <img1> <img2>\n"); }
}




