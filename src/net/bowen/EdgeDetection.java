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
    public EdgeDetection() {
        // Prepare fot image
        Mat src = Imgcodecs.imread("resources/pictures/houmai.jpg");
        Imgproc.resize(src, src, new Size(), .8, .8);

        // Turn to gray scale
        Mat grayScale = new Mat();
        Imgproc.cvtColor(src, grayScale, Imgproc.COLOR_BGR2GRAY);

        // Apply Laplacian edge detection
        Mat finalImg = new Mat();
        Imgproc.Laplacian(grayScale, finalImg, CvType.CV_16S, 3, 1, 0, Core.BORDER_DEFAULT);

        // converting back to CV_8U
        Core.convertScaleAbs(finalImg, finalImg);

        // Convert img to bufferedImage
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", finalImg, matOfByte);
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Prepare for window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(src.width(), src.height());
        setTitle("Edge Detection");
        add(new JLabel(new ImageIcon(bufferedImage)));
        setVisible(true);
    }

    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Show on window.
        new EdgeDetection();
    }
}
