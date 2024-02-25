package net.bowen;

import com.fazecast.jSerialComm.SerialPort;
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
        ImageMatDisplay display = new ImageMatDisplay(displayImg, "Webcam");


        System.out.println("Available ports:");
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        System.out.println("-----------");
        for (SerialPort availablePort : availablePorts) {
            System.out.println(availablePort);
        }
        System.out.println("-----------");

        SerialPort commPort = SerialPort.getCommPort("COM3");
        commPort.setComPortParameters(115200, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        commPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
        if (!commPort.openPort())
            System.err.println(commPort + " not available.");
        else
            System.out.println(commPort + " opened.");

        while (display.isDisplayable()) {
            videoCapture.read(camImg);
            ObjectClassifier.detect(camImg, displayImg);
            display.refresh();


            if (commPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[commPort.bytesAvailable()];
                commPort.readBytes(readBuffer, readBuffer.length);

                System.out.println(new String(readBuffer));
            }
        }

        videoCapture.release();
        camImg.release();
        displayImg.release();
        commPort.closePort();
    }
}
