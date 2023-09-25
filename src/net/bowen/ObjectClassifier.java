package net.bowen;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ObjectClassifier extends JFrame {
    private static final String IMG_SRC = "resources/pictures/kids.jpg";
    private static final String MODEL_CFG_SRC = "resources/yolo/yolov3-320.cfg";
    private static final String MODEL_WEIGHTS_SRC = "resources/externalFiles/yolov3.weights";

    private ObjectClassifier() {
        // Prepare fot image
        Mat imgMat = Imgcodecs.imread(IMG_SRC);
        Imgproc.resize(imgMat, imgMat, new Size(), .3, .3);

        // Prepare for model
        Net net = Dnn.readNetFromDarknet(MODEL_CFG_SRC, MODEL_WEIGHTS_SRC);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        // Passing picture into network
        Mat blob = Dnn.blobFromImage(imgMat, 1f / 255f, new Size(320, 320),
                new Scalar(0, 0, 0), true, false);
        net.setInput(blob);

        List<String> unconnectedOutLayersNames = net.getUnconnectedOutLayersNames();

        List<Mat> outputBlobs = new ArrayList<>();
        net.forward(outputBlobs, unconnectedOutLayersNames);

        findObjects(outputBlobs, imgMat);


        // Convert img to bufferedImage
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", imgMat, matOfByte);
        BufferedImage bufferedImage;
        try {
            bufferedImage =
                    ImageIO.read(new ByteArrayInputStream(matOfByte.toArray()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Prepare for window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(imgMat.width(), imgMat.height());
        setTitle(Path.of(IMG_SRC).getFileName().toString());
        add(new JLabel(new ImageIcon(bufferedImage)));
        setVisible(true);
    }


    /**
     * Calculation rule according to:
     * <a href="https://github.com/Bowen951209/topic-study-image-classification-control/blob/48d7def412acc89bb5d174428328e3a9c2e783be/resources/figures/figure0.png">figure</a>
     */
    private void findObjects(List<Mat> outputBlobs, Mat img) {
        List<Rect2d> rectList = new ArrayList<>();
        List<Integer> labelIDList = new ArrayList<>();
        List<Float> confidenceList = new ArrayList<>();

        for (Mat outputBlob : outputBlobs) {
            for (int row = 0; row < outputBlob.height(); row++) {
                // find confidence in a row
                double confidence = outputBlob.get(row, 4)[0];
                if (confidence < .5)// if the confidence is low, don't consider
                    continue;
                confidenceList.add((float) confidence);

                // find what label is the most possible in this row
                labelIDList.add(findMaxInRow(outputBlob, row) - 5);

                // store the rectangle
                Rect2d rect = new Rect2d();
                rect.width = (int) (outputBlob.get(row, 2)[0] * img.width());
                rect.height = (int) (outputBlob.get(row, 3)[0] * img.height());
                rect.x = (int) (outputBlob.get(row, 0)[0] * img.width() - rect.width / 2);
                rect.y = (int) (outputBlob.get(row, 1)[0] * img.height() - rect.height / 2);
                rectList.add(rect);
            }
        }

        MatOfRect2d matOfRect = new MatOfRect2d();
        matOfRect.fromList(rectList);
        MatOfFloat matOfConfidence = new MatOfFloat();
        matOfConfidence.fromList(confidenceList);

        // ----------Remove same rectangles---------
        // indices pointing out which indices are the final result
        MatOfInt resultIndicesMat = new MatOfInt();
        Dnn.NMSBoxes(matOfRect, matOfConfidence, .5f, .3f, resultIndicesMat);
        // -------------------------------------------

        for (int idx : resultIndicesMat.toList()) {
            // draw rectangles
            Rect2d rect2d = rectList.get(idx);
            Rect rect = new Rect((int) rect2d.x, (int) rect2d.y, (int) rect2d.width, (int) rect2d.height);
            Imgproc.rectangle(img, rect, new Scalar(0, 255, 0));

            // draw texts
            Imgproc.putText(img, labelIDList.get(idx) + " " + confidenceList.get(idx),
                    new Point(rect.x, rect.y), Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0,
                            0), 2);
        }
    }

    /**
     * This method only finds colum 5 ~ ...  According to  <a href="https://github.com/Bowen951209/topic-study-image-classification-control/blob/48d7def412acc89bb5d174428328e3a9c2e783be/resources/figures/figure0.png">figure</a>
     */
    private static int findMaxInRow(Mat mat, int row) {
        double maxScore = 0;
        int maxIndex = -1;
        for (int column = 5; column < mat.width(); column++) {
            if (mat.get(row, column)[0] > maxScore) {
                maxScore = mat.get(row, column)[0];
                maxIndex = column;
            }
        }

        return maxIndex;
    }

    public static void main(String[] args) {
        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Show on window.
        new ObjectClassifier();
    }

}
