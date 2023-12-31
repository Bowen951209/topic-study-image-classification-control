package net.bowen;

import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectClassifier extends JFrame {
    private static final String MODEL_CFG_SRC = "resources/yolo/yolov3-320.cfg";
    private static final String MODEL_WEIGHTS_SRC = "resources/externalFiles/yolov3.weights";
    private static final String LABEL_NAME_LIST_SRC = "resources/yolo/coco.names";
    private static List<String> labelList;

    public static void detect(Mat src, Mat target) {
        // Prepare fot image
        Imgproc.resize(src, target, new Size(), .3, .3);

        // Prepare for model
        Net net = Dnn.readNetFromDarknet(MODEL_CFG_SRC, MODEL_WEIGHTS_SRC);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        // Passing picture into network
        Mat blob = Dnn.blobFromImage(target, 1f / 255f, new Size(320, 320),
                new Scalar(0, 0, 0), true, false);
        net.setInput(blob);
        blob.release();

        List<String> unconnectedOutLayersNames = net.getUnconnectedOutLayersNames();

        List<Mat> outputBlobs = new ArrayList<>();
        net.forward(outputBlobs, unconnectedOutLayersNames);

        findObjects(outputBlobs, target);
    }


    /**
     * Calculation rule according to:
     * <a href="https://github.com/Bowen951209/topic-study-image-classification-control/blob/48d7def412acc89bb5d174428328e3a9c2e783be/resources/figures/figure0.png">figure</a>
     */
    private static void findObjects(List<Mat> outputBlobs, Mat img) {
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
            String labelName = getLabelList().get(labelIDList.get(idx));
            Imgproc.putText(img, labelName + " " + toPercentage(confidenceList.get(idx)),
                    new Point(rect.x, rect.y), Imgproc.FONT_HERSHEY_SIMPLEX, .5, new Scalar(0,
                            255, 0), 1);
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

    private static List<String> getLabelList() {
        if (labelList == null) {
            labelList = new ArrayList<>();
            File file = new File(LABEL_NAME_LIST_SRC);
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String string;
                while((string = br.readLine()) != null)
                    labelList.add(string);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return labelList;
    }

    private static String toPercentage(float v) {
        return (int)(v * 100) + "%";
    }

    public static void main(String[] args) {
        if (new File("resources/externalFiles/yolov3.weights").exists()) {
            System.out.println("Weight file exists!");
        } else {
            System.out.println("Weight file doesn't exist, downloading...");
            long startTime = System.currentTimeMillis();
            new FileDownloader().listenProgress(500)
                    .download("https://pjreddie.com/media/files/yolov3.weights", MODEL_WEIGHTS_SRC);
            long time = System.currentTimeMillis() - startTime;
            System.out.printf("Download complete in: %.2f seconds.\n", time * .001f);
        }

        // Init
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat src = Imgcodecs.imread("resources/pictures/kids.jpg");
        Mat res = new Mat();
        detect(src, res);
        src.release();

        // Show on window.
        new ImageMatDisplayer(res, "Object Classifier");
        res.release();
    }

}
