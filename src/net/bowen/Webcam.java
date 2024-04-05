package net.bowen;

import com.fazecast.jSerialComm.SerialPort;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Webcam {
    private static final List<Integer> AVAIL_OBJ = Arrays.asList(76, 39, 38); // scissors, bottle ,tennis rocket
    private static final Timer TIMER = new Timer();

    private static boolean isMovingServo;
    private static final int DELAY_TIME = 0;
    private static final int HOLD_TIME = 1500;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Init the camera image & the detection display image.
        VideoCapture videoCapture = new VideoCapture(0);
        Mat camImg = new Mat();
        Mat displayImg = new Mat();
        videoCapture.read(camImg);
        ObjectClassifier objectClassifier = new ObjectClassifier();
        objectClassifier.detect(camImg, displayImg);

        // The display window.
        ImageMatDisplay display = new ImageMatDisplay(displayImg, "Webcam");

        // Get the port.
        printAvailablePorts();
        SerialPort commPort = getUNOPort();

        while (display.isDisplayable()) {
            // Update camImg and detect objects.
            videoCapture.read(camImg);
            objectClassifier.detect(camImg, displayImg);
            display.refresh();

            if (commPort == null) continue;

            // If containing available objects, control the servos.
            var itemList = objectClassifier.getLabelIDList();
            itemList.retainAll(AVAIL_OBJ);
            if (itemList.isEmpty()) continue;

            // NOTE: I only detect for the 1st item, since the picture should only have single item at a time.
            Integer item = itemList.get(0);

            if (item.equals(AVAIL_OBJ.get(0))) {
                moveServo(commPort, 0);
            } else if (item.equals(AVAIL_OBJ.get(1))) {
                moveServo(commPort, 1);
            } else {// it's the 3rd available object.
                moveServo(commPort, 2);
            }
        }

        videoCapture.release();
        camImg.release();
        displayImg.release();
        if (commPort != null)
            commPort.closePort();
    }

    private static void moveServo(SerialPort port, int servoID) {
        if (isMovingServo) {
            System.out.println("busy");
            return;
        }
        TimerTask moveServo = new TimerTask() {
            @Override
            public void run() {
                isMovingServo = true;

                portWriteString(port, "SER" + servoID + " " + 45);
                System.out.println("Moved servo" + servoID);
            }
        };

        TimerTask restoreServo = new TimerTask() {
            @Override
            public void run() {
                portWriteString(port, "SER" + servoID + " 0");
                System.out.println("Restored servo" + servoID);

                isMovingServo = false;
            }
        };

        TIMER.schedule(moveServo, DELAY_TIME);
        TIMER.schedule(restoreServo, HOLD_TIME);
    }

    private static void portWriteString(SerialPort port, String string) {
        byte[] buffer = string.getBytes();
        port.writeBytes(buffer, buffer.length);
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
            if (port.getPortDescription().contains("Arduino Uno") || port.getPortDescription().contains("USB Serial")) {
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
