package net.bowen;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.nio.file.Path;

public class FaceClassifier extends JFrame {
    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        CascadeClassifier faceDetector = new CascadeClassifier("resources/haarcascades/lbpcascade_frontalface.xml");
        final String IMG_SRC = "resources/pictures/MeAndSister.JPG";

        // Import image.
        Mat img = Imgcodecs.imread(IMG_SRC);
        System.out.println("Original image size: " + img.width() + "x" + img.height());
        // Resize image.
        Imgproc.resize(img, img, new Size((double) img.width() / 3, (double) img.height() / 3));
        System.out.println("Image size set to: " + img.width() + "x" + img.height());

        // Detect faces.
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(img, faceDetections);
        System.out.println("Detected " + faceDetections.toArray().length + " faces");

        // Draw rectangles on detected faces.
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(img, rect, new Scalar(0, 255, 0));
        }
        faceDetections.release();

        // Display
        new ImageMatDisplay(img, Path.of(IMG_SRC).getFileName().toString());
    }
}
