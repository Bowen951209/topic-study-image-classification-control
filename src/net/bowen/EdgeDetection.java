package net.bowen;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EdgeDetection extends JFrame {
    /*
     * Reference: https://chtseng.wordpress.com/2016/12/05/opencv-edge-detection%E9%82%8A%E7%B7%A3%E5%81%B5%E6%B8%AC/
     * */
    public EdgeDetection(Mat imgMat, String title) {
        // Convert imgMat to bufferedImage
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imgMat, matOfByte);
        imgMat.release();

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

    public static Mat laplacianEdgeDetection(Mat src) {
        // Prepare fot image
        Imgproc.resize(src, src, new Size(), .8, .8);

        // Turn to gray scale
        Mat grayScale = new Mat();
        Imgproc.cvtColor(src, grayScale, Imgproc.COLOR_BGR2GRAY);
        src.release();

        // Apply Laplacian edge detection
        Mat finalImg = new Mat();
        Imgproc.Laplacian(grayScale, finalImg, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT);
        grayScale.release();

        // Converting back to CV_8U
        Core.convertScaleAbs(finalImg, finalImg);

        return finalImg;
    }

    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread("resources/pictures/houmai.jpg");

        // Get laplacian.
        Mat laplacianResult = laplacianEdgeDetection(src);
        // Create new window to display img.
        new EdgeDetection(laplacianResult, "Laplacian Edge Detection");
    }
}
