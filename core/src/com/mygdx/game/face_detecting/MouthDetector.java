package com.mygdx.game.face_detecting;

import com.mygdx.game.screens.GameScreen;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.VideoInputFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static org.bytedeco.opencv.global.opencv_imgproc.CV_AA;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_RETR_LIST;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.opencv.global.opencv_imgproc.approxPolyDP;
import static org.bytedeco.opencv.global.opencv_imgproc.arcLength;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.drawContours;
import static org.bytedeco.opencv.global.opencv_imgproc.findContours;
import static org.bytedeco.opencv.global.opencv_imgproc.rectangle;
import static org.bytedeco.opencv.global.opencv_imgproc.threshold;
import static org.opencv.core.CvType.CV_8UC1;

public class MouthDetector {
    private GameScreen gs;
    String faceClassifierName;
    String mouthClassifierName;
    CascadeClassifier face_cascade;
    CascadeClassifier mouth_cascade;
    VideoInputFrameGrabber grabber;
    OpenCVFrameConverter.ToMat converter;
    Mat grabbedImage;
    Mat grayImage;
    Mat rotatedImage;
    FrameRecorder recorder;

    public MouthDetector(GameScreen gs) throws FrameGrabber.Exception {
        this.gs = gs;
        faceClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_frontalface_alt.xml";
        face_cascade = new CascadeClassifier(faceClassifierName);
        mouthClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_smile.xml";
        mouth_cascade = new CascadeClassifier(mouthClassifierName);
        try {
            grabber = VideoInputFrameGrabber.createDefault(0);
            grabber.start();
        } catch (Exception e){
            e.printStackTrace();
        }



        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        converter = new OpenCVFrameConverter.ToMat();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        Mat grabbedImage = converter.convert(grabber.grab());

        int height = grabbedImage.rows();
        int width = grabbedImage.cols();

        // Objects allocated with `new`, clone(), or a create*() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling deallocate().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        Mat grayImage = new Mat(height, width, CV_8UC1);
        Mat rotatedImage = grabbedImage.clone();

        // The OpenCVFrameRecorder class simply uses the VideoWriter of opencv_videoio,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
        FrameRecorder recorder = null;
        try {
            recorder = FrameRecorder.createDefault("output.avi", width, height);
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private MouthDetector() throws FrameGrabber.Exception {
        faceClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_frontalface_alt.xml";
        face_cascade = new CascadeClassifier(faceClassifierName);
        mouthClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_smile.xml";
        mouth_cascade = new CascadeClassifier(mouthClassifierName);

        try {
            grabber = VideoInputFrameGrabber.createDefault(0);
            grabber.start();
        } catch (Exception e){
            e.printStackTrace();
        }



        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        converter = new OpenCVFrameConverter.ToMat();

        // FAQ about IplImage and Mat objects from OpenCV:
        // - For custom raw processing of data, createBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData, and under Android we can
        //   also use that Buffer with Bitmap.copyPixelsFromBuffer() and copyPixelsToBuffer().
        // - To get a BufferedImage from an IplImage, or vice versa, we can chain calls to
        //   Java2DFrameConverter and OpenCVFrameConverter, one after the other.
        // - Java2DFrameConverter also has static copy() methods that we can use to transfer
        //   data more directly between BufferedImage and IplImage or Mat via Frame objects.
        grabbedImage = converter.convert(grabber.grab());

        int height = grabbedImage.rows();
        int width = grabbedImage.cols();

        // Objects allocated with `new`, clone(), or a create*() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling deallocate().
        // You shall NOT call cvReleaseImage(), cvReleaseMemStorage(), etc. on objects allocated this way.
        grayImage = new Mat(height, width, CV_8UC1);
        rotatedImage = grabbedImage.clone();

        // The OpenCVFrameRecorder class simply uses the VideoWriter of opencv_videoio,
        // but FFmpegFrameRecorder also exists as a more versatile alternative.
        recorder = null;
        try {
            recorder = FrameRecorder.createDefault("output.avi", width, height);
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MouthDetector mouthDetector = null;
        try {
             mouthDetector = new MouthDetector();
             mouthDetector.detect();
        } catch (Exception e){
            e.printStackTrace();
        }


    }

    public void detect() throws Exception{
        int cmx = 0;
        int cmy = 0;
        int cmw = 0;
        int cmh = 0;
        while ((grabbedImage = converter.convert(grabber.grab())) != null){
            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            RectVector mouths = new RectVector();
            face_cascade.detectMultiScale(grayImage, faces);
            Mat faceROIimage = new Mat();
            if (!faces.empty()) {
                faceROIimage = new Mat(grayImage, faces.get(0));
            }
            mouth_cascade.detectMultiScale(faceROIimage, mouths);
            long total = faces.size() < mouths.size() ? faces.size() : mouths.size();
            // total = mouths.size();

            for (long i = 0; i < total; i++) {
                Rect fr = faces.get(0);
                Rect mr = mouths.get(0);
                int x = fr.x(), y = fr.y(), w = fr.width(), h = fr.height();
                int mx = mr.x(), my = mr.y(), mw = mr.width(), mh = mr.height();
                rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);
                rectangle(grabbedImage, new Point(mx - mw, my - mh), new Point(mx, my), Scalar.GREEN, 1, CV_AA, 0);
                int wk = mr.width()/(cmw + mr.width());
                int hk = mr.height()/(cmh + mr.height());
                if ((mr.width() > cmw + mr.width() / 2 || mr.height() > cmh + mr.height() / 2 ) && wk != hk) {
                    rectangle(grabbedImage, new Point(mx - mw, my - mh), new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
                    rectangle(grabbedImage, new Point(cmx - cmw, cmy - cmh), new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);
                    System.out.println("РОТ ОТКРЫТ");

                } else if (mr.width() < cmw + mr.width() / 2 && mr.height() < cmh + mr.height() / 2) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                cmx = mr.x();
                cmy = mr.y();
                cmw = mr.width();
                cmh = mr.height();
            }
        }

    }

}
