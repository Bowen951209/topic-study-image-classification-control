package net.bowen;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Webcam {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture videoCapture = new VideoCapture(0);
        Mat img = new Mat();
        videoCapture.read(img);

        ImageMatDisplay display = new ImageMatDisplay(img, "webcam");
        while (display.isDisplayable()) {
            videoCapture.read(img);
            display.refresh();
        }



        videoCapture.release();
    }
}
