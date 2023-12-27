package net.bowen;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EdgeDetector extends JFrame {
    /*
     * Reference: https://chtseng.wordpress.com/2016/12/05/opencv-edge-detection%E9%82%8A%E7%B7%A3%E5%81%B5%E6%B8%AC/
     * */
    public EdgeDetector(Mat imgMat, String title) {
        // Convert imgMat to bufferedImage
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imgMat, matOfByte);

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
            matOfByte.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Prepare for window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        setTitle(title);
        add(new JLabel(new ImageIcon(bufferedImage)));
        setVisible(true);
    }

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

    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread("resources/pictures/houmai.jpg");
        Imgproc.resize(src, src, new Size(), .5f, .5f);

        // Get laplacian.
        Mat laplacianResult = laplacian(src);
        // Create new window to display img.
        new EdgeDetector(laplacianResult, "Laplacian Edge Detection");
        laplacianResult.release();

        // Get sobel.
        Mat sobelResult = sobel(src);
        new EdgeDetector(sobelResult, "Sobel Edge Detection");
        sobelResult.release();

        src.release();
    }
}
