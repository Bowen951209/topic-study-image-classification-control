package net.bowen;

import com.fazecast.jSerialComm.SerialPort;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class Webcam {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Init the camera image & the detection display image.
        VideoCapture videoCapture = new VideoCapture(0);
        Mat camImg = new Mat();
        Mat displayImg = new Mat();
        videoCapture.read(camImg);
        ObjectClassifier.detect(camImg, displayImg);

        // The display window.
        ImageMatDisplay display = new ImageMatDisplay(displayImg, "Webcam");

        // Get the port.
        printAvailablePorts();
        SerialPort commPort = getUNOPort();

        while (display.isDisplayable()) {
            // Update camImg and detect objects.
            videoCapture.read(camImg);
            ObjectClassifier.detect(camImg, displayImg);
            display.refresh();


            // Read data from the port and print it as a string.
            if (commPort != null) {
                if (commPort.bytesAvailable() > 0) {
                    byte[] readBuffer = new byte[commPort.bytesAvailable()];
                    commPort.readBytes(readBuffer, readBuffer.length);

                    System.out.println(new String(readBuffer));
                }
            }
        }

        videoCapture.release();
        camImg.release();
        displayImg.release();
        if (commPort != null)
            commPort.closePort();
    }

    private static void printAvailablePorts() {
        System.out.println("Available ports:");
        SerialPort[] availablePorts = SerialPort.getCommPorts();
        System.out.println("-----------");
        for (SerialPort availablePort : availablePorts) {
            System.out.println(availablePort);
        }
        System.out.println("-----------");
    }

    /**
     * @return the port that connects to the Arduino UNO. If there's no port connects to it, return null.
     */
    private static SerialPort getUNOPort() {
        SerialPort unoPort = null;
        SerialPort[] availablePorts = SerialPort.getCommPorts();

        // Get the port that is connected to Arduino UNO.
        for (SerialPort port : availablePorts) {
            if (port.getPortDescription().contains("Arduino Uno")) {
                unoPort = port;
                break;
            }
        }

        if (unoPort == null) {
            System.err.println("Can't find a port connected to Arduino UNO");
            return null;
        }

        unoPort.setComPortParameters(115200, Byte.SIZE, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        unoPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);

        if (!unoPort.openPort())
            System.err.println(unoPort + " not available.");
        else
            System.out.println(unoPort + " opened.");

        return unoPort;
    }
}
