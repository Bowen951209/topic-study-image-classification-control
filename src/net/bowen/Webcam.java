package net.bowen;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Webcam {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        VideoCapture videoCapture = new VideoCapture(0);
        Mat camImg = new Mat();
        Mat displayImg = new Mat();
        videoCapture.read(camImg);
        ObjectClassifier.detect(camImg, displayImg);

        ImageMatDisplay display = new ImageMatDisplay(displayImg, "webcam");
        while (display.isDisplayable()) {
            videoCapture.read(camImg);
            ObjectClassifier.detect(camImg, displayImg);

            display.refresh();
        }



        videoCapture.release();
        camImg.release();
        displayImg.release();
    }
}
