package net.bowen;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;

public class EdgeDetector extends JFrame {
    /*
     * Reference: https://chtseng.wordpress.com/2016/12/05/opencv-edge-detection%E9%82%8A%E7%B7%A3%E5%81%B5%E6%B8%AC/
     * */

    /**
     * Apply the laplacian edge detection.
     *
     * @return The result img.
     */
    public static Mat laplacian(Mat src) {
        // Turn to gray scale
        Mat grayScale = new Mat();
        Imgproc.cvtColor(src, grayScale, Imgproc.COLOR_BGR2GRAY);

        // Apply Laplacian edge detection
        Mat finalImg = new Mat();
        Imgproc.Laplacian(grayScale, finalImg, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT);
        grayScale.release();

        // Converting back to CV_8U
        Core.convertScaleAbs(finalImg, finalImg);

        return finalImg;
    }

    /**
     * Apply the sobel edge detection.
     *
     * @return The result img.
     */
    public static Mat sobel(Mat src) {
        Mat grayScale = new Mat();
        Imgproc.cvtColor(src, grayScale, Imgproc.COLOR_BGR2GRAY);

        Mat sobelX = new Mat();
        Mat sobelY = new Mat();
        Mat sobelXY = new Mat();
        Imgproc.Sobel(grayScale, sobelX, CvType.CV_64F, 1, 1);
        Imgproc.Sobel(grayScale, sobelY, CvType.CV_64F, 0, 1);
        Core.convertScaleAbs(sobelX, sobelX);
        Core.convertScaleAbs(sobelY, sobelY);
        Core.bitwise_or(sobelX, sobelY, sobelXY);

        sobelX.release();
        sobelY.release();
        grayScale.release();

        return sobelXY;
    }

    public static Mat canny(Mat src, double threshold1, double threshold2) {
        Mat grayScale = new Mat();
        Imgproc.cvtColor(src, grayScale, Imgproc.COLOR_BGR2GRAY);

        Mat result = new Mat();
        Imgproc.Canny(grayScale, result, threshold1, threshold2);
        grayScale.release();

        return result;
    }

    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread("resources/pictures/houmai.jpg");
        Imgproc.resize(src, src, new Size(), .5f, .5f);

        // Get laplacian.
        Mat laplacianResult = laplacian(src);
        // Create new window to display img.
        new ImageMatDisplay(laplacianResult, "Laplacian Edge Detection");
        laplacianResult.release();

        // Get sobel.
        Mat sobelResult = sobel(src);
        new ImageMatDisplay(sobelResult, "Sobel Edge Detection");
        sobelResult.release();

        // Get canny.
        Mat cannyResult = canny(src, 100, 500);
        new ImageMatDisplay(cannyResult, "Canny Edge Detection");
        cannyResult.release();

        src.release();
    }
}
