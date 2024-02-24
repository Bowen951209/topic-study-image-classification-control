package net.bowen;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageMatDisplay extends JFrame {
    private final Mat imgMat;
    private final MatOfByte matOfByte = new MatOfByte();
    private final BufferedImage bufferedImage;

    public ImageMatDisplay(Mat imgMat, String title) {
        this.imgMat = imgMat;
        this.bufferedImage = new BufferedImage(imgMat.width(), imgMat.height(), BufferedImage.TYPE_3BYTE_BGR);

        refresh();

        // Prepare for window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        setTitle(title);
        add(new JLabel(new ImageIcon(bufferedImage)));
        setVisible(true);
    }

    public void refresh() {
        Imgcodecs.imencode(".jpg", imgMat, matOfByte);

        // Get the BufferedImage's backing array and copy the pixels directly into it
        byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        imgMat.get(0, 0, data);

        repaint();
    }
}
