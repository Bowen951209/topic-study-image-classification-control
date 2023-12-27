package net.bowen;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageMatDisplayer extends JFrame {
    public ImageMatDisplayer(Mat imgMat, String title){
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
}
