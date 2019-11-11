package com.mygdx.game.face_detecting;

import com.mygdx.game.MyGame;
import com.mygdx.game.resources.RTextures;
import com.mygdx.game.screens.GameScreen;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.indexer.*;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_calib3d.*;
import org.bytedeco.opencv.opencv_objdetect.*;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_calib3d.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static org.bytedeco.opencv.global.opencv_calib3d.Rodrigues;
import static org.bytedeco.opencv.global.opencv_core.CV_64FC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.opencv.core.CvType.CV_8UC1;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.DoublePointer;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.*;
import org.bytedeco.opencv.opencv_highgui.*;
import org.bytedeco.opencv.opencv_imgproc.*;
import org.bytedeco.opencv.opencv_objdetect.*;
import org.opencv.core.CvType;
import org.opencv.core.MatOfRect;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_face.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_objdetect.*;

public class FaceDetector implements Runnable {
    GameScreen gs;
    CanvasFrame frame;
    FrameGrabber grabber;
    OpenCVFrameConverter.ToMat converter;
    FrameRecorder recorder;
    Mat grabbedImage;
    Mat grayImage;
    Mat rotatedImage;
    String faceClassifierName;
    String mouthClassifierName;
    CascadeClassifier face_cascade;
    CascadeClassifier mouth_cascade;
    public boolean needChange;
    int cmy;
    int cmx;
    int cmw;
    int cmh;

    Target target;

    public FaceDetector(GameScreen gs) {
        this.gs = gs;
        needChange = false;
    }

    private FaceDetector(){
    }

    public static void main(String[] args) throws Exception {
        FaceDetector faceDetector = new FaceDetector();
        //faceDetector.main_4();
        main_1(args);
    }

    public static void main_1(String[] args) throws Exception {

        String classifierName = null;
        if (args.length > 0) {
            classifierName = args[0];
        } else {
            URL url = null;
            try {
                url = new URL("https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            File file = null;
            try {
                file = Loader.cacheResource(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            classifierName = file.getAbsolutePath();
        }

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CascadeClassifier classifier = new CascadeClassifier(classifierName);
        if (classifier == null) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber, OpenKinect2FrameGrabber,
        // RealSenseFrameGrabber, PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

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

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        // Let's create some random 3D rotation...
        Mat randomR = new Mat(3, 3, CV_64FC1),
                randomAxis = new Mat(3, 1, CV_64FC1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(),
                axisIdx = randomAxis.createIndexer();
        axisIdx.put(0, (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4);
        Rodrigues(randomAxis, randomR);
        double f = (width + height) / 2.0;
        Ridx.put(0, 2, Ridx.get(0, 2) * f);
        Ridx.put(1, 2, Ridx.get(1, 2) * f);
        Ridx.put(2, 0, Ridx.get(2, 0) / f);
        Ridx.put(2, 1, Ridx.get(2, 1) / f);
        System.out.println(Ridx);

        // We can allocate native arrays using constructors taking an integer as argument.
        Point hatPoints = new Point(3);

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            // Let's try to detect some faces! but we need a grayscale image...
            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            classifier.detectMultiScale(grayImage, faces);
            long total = faces.size();
            for (long i = 0; i < total; i++) {
                Rect r = faces.get(i);
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);

                // To access or pass as argument the elements of a native array, call position() before.
                hatPoints.position(0).x(x - w / 10).y(y - h / 10);
                hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
                hatPoints.position(2).x(x + w / 2).y(y - h / 2);
                fillConvexPoly(grabbedImage, hatPoints.position(0), 3, Scalar.GREEN, CV_AA, 0);
            }

            // Let's find some contours! but first some thresholding...
            threshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To check if an output argument is null we may call either isNull() or equals(null).
            MatVector contours = new MatVector();
            findContours(grayImage, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            long n = contours.size();
            for (long i = 0; i < n; i++) {
                Mat contour = contours.get(i);
                Mat points = new Mat();
                approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
                drawContours(grabbedImage, new MatVector(points), -1, Scalar.BLUE);

            }

            warpPerspective(grabbedImage, rotatedImage, randomR, rotatedImage.size());

            Frame rotatedFrame = converter.convert(rotatedImage);
            frame.showImage(rotatedFrame);
            try {
                recorder.record(rotatedFrame);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }

        }
        frame.dispose();
        try {
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        grabber.stop();
    }

    public static void main_2(String[] args) throws Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        grabber.start();

        IplImage frame = converter.convert(grabber.grab());
        IplImage image = null;
        IplImage prevImage = null;
        IplImage diff = null;

        CanvasFrame canvasFrame = new CanvasFrame("Some Title");
        canvasFrame.setCanvasSize(frame.width(), frame.height());

        CvMemStorage storage = CvMemStorage.create();

        while (canvasFrame.isVisible() && (frame = converter.convert(grabber.grab())) != null) {
            cvClearMemStorage(storage);

            cvSmooth(frame, frame, CV_GAUSSIAN, 9, 9, 2, 2);
            if (image == null) {
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            } else {
                prevImage = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                prevImage = image;
                image = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
                cvCvtColor(frame, image, CV_RGB2GRAY);
            }

            if (diff == null) {
                diff = IplImage.create(frame.width(), frame.height(), IPL_DEPTH_8U, 1);
            }

            if (prevImage != null) {
                // perform ABS difference
                cvAbsDiff(image, prevImage, diff);
                // do some threshold for wipe away useless details
                cvThreshold(diff, diff, 64, 255, CV_THRESH_BINARY);

                canvasFrame.showImage(converter.convert(diff));

                // recognize contours
                CvSeq contour = new CvSeq(null);
                cvFindContours(diff, storage, contour, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);

                while (contour != null && !contour.isNull()) {
                    if (contour.elem_size() > 0) {
                        CvBox2D box = cvMinAreaRect2(contour, storage);
                        // test intersection
                        if (box != null) {
                            CvPoint2D32f center = box.center();
                            CvSize2D32f size = box.size();
/*                            for (int i = 0; i < sa.length; i++) {
                                if ((Math.abs(center.x - (sa[i].offsetX + sa[i].width / 2))) < ((size.width / 2) + (sa[i].width / 2)) &&
                                    (Math.abs(center.y - (sa[i].offsetY + sa[i].height / 2))) < ((size.height / 2) + (sa[i].height / 2))) {
                                    if (!alarmedZones.containsKey(i)) {
                                        alarmedZones.put(i, true);
                                        activeAlarms.put(i, 1);
                                    } else {
                                        activeAlarms.remove(i);
                                        activeAlarms.put(i, 1);
                                    }
                                    System.out.println("Motion Detected in the area no: " + i +
                                            " Located at points: (" + sa[i].x + ", " + sa[i].y+ ") -"
                                            + " (" + (sa[i].x +sa[i].width) + ", "
                                            + (sa[i].y+sa[i].height) + ")");
                                }
                            }
*/
                        }
                    }
                    contour = contour.h_next();
                }
            }
        }
        grabber.stop();
        canvasFrame.dispose();
    }

    public static void main_3(String[] args) throws Exception {
        args[0] = "";

        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        if (args.length < 2) {
            System.out.println("Two parameters are required to run this program, first parameter is the analized video and second parameter is the trained result for fisher faces.");
        }

        String videoFileName = args[0];
        String trainedResult = args[1];

        CascadeClassifier face_cascade = new CascadeClassifier(
                "data\\haarcascade_frontalface_default.xml");
        FaceRecognizer lbphFaceRecognizer = LBPHFaceRecognizer.create();
        lbphFaceRecognizer.read(trainedResult);
        File f = new File(videoFileName);

        OpenCVFrameGrabber grabber = null;
        try {
            grabber = OpenCVFrameGrabber.createDefault(f);
            grabber.start();
        } catch (Exception e) {
            System.err.println("Failed start the grabber.");
        }

        Frame videoFrame = null;
        Mat videoMat = new Mat();
        while (true) {
            videoFrame = grabber.grab();
            videoMat = converterToMat.convert(videoFrame);
            Mat videoMatGray = new Mat();
            // Convert the current frame to grayscale:
            cvtColor(videoMat, videoMatGray, COLOR_BGRA2GRAY);
            equalizeHist(videoMatGray, videoMatGray);

            Point p = new Point();
            RectVector faces = new RectVector();
            // Find the faces in the frame:
            face_cascade.detectMultiScale(videoMatGray, faces);

            // At this point you have the position of the faces in
            // faces. Now we'll get the faces, make a prediction and
            // annotate it in the video. Cool or what?
            for (int i = 0; i < faces.size(); i++) {
                Rect face_i = faces.get(i);

                Mat face = new Mat(videoMatGray, face_i);
                // If fisher face recognizer is used, the face need to be
                // resized.
                // resize(face, face_resized, new Size(im_width, im_height),
                // 1.0, 1.0, INTER_CUBIC);

                // Now perform the prediction, see how easy that is:
                IntPointer label = new IntPointer(1);
                DoublePointer confidence = new DoublePointer(1);
                lbphFaceRecognizer.predict(face, label, confidence);
                int prediction = label.get(0);

                // And finally write all we've found out to the original image!
                // First of all draw a green rectangle around the detected face:
                rectangle(videoMat, face_i, new Scalar(0, 255, 0, 1));

                // Create the text we will annotate the box with:
                String box_text = "Prediction = " + prediction;
                // Calculate the position for annotated text (make sure we don't
                // put illegal values in there):
                int pos_x = Math.max(face_i.tl().x() - 10, 0);
                int pos_y = Math.max(face_i.tl().y() - 10, 0);
                // And now put it into the image:
                putText(videoMat, box_text, new Point(pos_x, pos_y),
                        FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
            }
            // Show the result:
            imshow("face_recognizer", videoMat);

            char key = (char) waitKey(20);
            // Exit this loop on escape:
            if (key == 27) {
                destroyAllWindows();
                break;
            }
        }
    }


    public void main_4() throws Exception {
        String faceFileName = "https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml";
        String mouthFileName = "https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_smile.xml";

        CascadeClassifier mouth_cascade = new CascadeClassifier("cascade_files/mouth.xml");
        CascadeClassifier face_cascade = new CascadeClassifier("cascade_files/face.xml");
        face_cascade = new CascadeClassifier(faceFileName);
        mouth_cascade = new CascadeClassifier(mouthFileName);


        String faceClassifierName = null;
        String mouthClassifierName = null;

//        URL furl = null;
//        URL murl = null;
//        try {
//            furl = new URL("https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_frontalface_alt.xml");
//            murl = new URL("https://raw.github.com/opencv/opencv/master/data/haarcascades/haarcascade_smile.xml");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        File faceFile = null;
//        File mouthFiile = null;
//        try {
//            faceFile = Loader.cacheResource(furl);
//            mouthFiile = Loader.cacheResource(murl);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        faceClassifierName = faceFile.getAbsolutePath();
//        mouthClassifierName = mouthFiile.getAbsolutePath();


        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        faceClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_frontalface_alt.xml";
        face_cascade = new CascadeClassifier(faceClassifierName);
        mouthClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_smile.xml";
        mouth_cascade = new CascadeClassifier(mouthClassifierName);
        mouth_cascade.setMaskGenerator(new BaseCascadeClassifier.MaskGenerator(new Pointer()));
//        if (classifier == null) {
//            System.err.println("Error loading classifier file \"" + classifierName + "\".");
//            System.exit(1);
//        }

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber, OpenKinect2FrameGrabber,
        // RealSenseFrameGrabber, PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = FrameGrabber.createDefault(0);
        grabber.start();

        // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
        // We need a FrameConverter to interface with other APIs (Android, Java 2D, JavaFX, Tesseract, OpenCV, etc).
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

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

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        CanvasFrame frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        // Let's create some random 3D rotation...
        Mat randomR = new Mat(3, 3, CV_64FC1), randomAxis = new Mat(3, 1, CV_64FC1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(),
                axisIdx = randomAxis.createIndexer();
        axisIdx.put(0,
                (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4);
        Rodrigues(randomAxis, randomR);
        double f = (width + height) / 2.0;
        Ridx.put(0, 2, Ridx.get(0, 2) * f);
        Ridx.put(1, 2, Ridx.get(1, 2) * f);
        Ridx.put(2, 0, Ridx.get(2, 0) / f);
        Ridx.put(2, 1, Ridx.get(2, 1) / f);
        System.out.println(Ridx);

        // We can allocate native arrays using constructors taking an integer as argument.
        Point hatPoints = new Point(3);
        int cmy = 0;
        int cmx = 0;
        int cmw = 0;
        int cmh = 0;

        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            // Let's try to detect some faces! but we need a grayscale image...
            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            RectVector mouths = new RectVector();
            face_cascade.detectMultiScale(grayImage, faces);
            Mat faceROIimage = new Mat();
            if (!faces.empty()) {
                faceROIimage = new Mat(grayImage, faces.get(0));
            }
            //mouth_cascade.detectMultiScale(grayImage, mouths);

            mouth_cascade.detectMultiScale(faceROIimage, mouths);
            long total = faces.size() < mouths.size() ? faces.size() : mouths.size();
            // total = mouths.size();

            for (long i = 0; i < total; i++) {
                Rect fr = faces.get(i);
                Rect mr = mouths.get(0);
                int x = fr.x(), y = fr.y(), w = fr.width(), h = fr.height();
                int mx = mr.x(), my = mr.y(), mw = mr.width(), mh = mr.height();
                rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);
                //rectangle(grabbedImage, new Point(mx, my), new Point(mx + mw, my + mh), Scalar.GREEN, 1, CV_AA, 0);
                rectangle(grabbedImage, new Point(mx + mw, my + mh), new Point(mx, my), Scalar.GREEN, 1, CV_AA, 0);
                //rectangle(grabbedImage, new Point(x + w/4, y + h - h/3), new Point(x + w - w/4, y + h - h/8), Scalar.YELLOW, 1, CV_AA, 0);
                if (mr.y() < cmy - mr.y() / 2 || mr.y() + mr.height() > cmy + cmh + (mr.y() + mr.height()) / 3 || mr.x() < cmx - mr.x() / 2) {
                    // rectangle(grabbedImage, new Point(mx + mw, my + mh),new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
                    // rectangle(grabbedImage,  new Point(cmx + cmw, cmy + cmh),new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);
                }

                int wk = mr.width()/(cmw + mr.width());
                int hk = mr.height()/(cmh + mr.height());
                if ((mr.width() > cmw + mr.width() / 2 || mr.height() > cmh + mr.height() / 2 ) && wk != hk) {
                    rectangle(grabbedImage, new Point(mx - mw, my - mh), new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
                    rectangle(grabbedImage, new Point(cmx - cmw, cmy - cmh), new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);
                    needChange = true;
                    setNeedChange(true);
                    //!!!!!gs.onFaceDetected(true);
                    System.out.println("РОТ ОТКРЫТ");

                } else if (mr.width() < cmw + mr.width() / 2 && mr.height() < cmh + mr.height() / 2) {
                    needChange = false;
                    setNeedChange(false);
                    //!!!!gs.onFaceDetected(false);
                    //System.out.println("РОТ ЗАКРЫТ");
                }
//                try {
//                    Thread.sleep(2500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                cmx = mr.x();
                cmy = mr.y();
                cmw = mr.width();
                cmh = mr.height();
                // To access or pass as argument the elements of a native array, call position() before.
//                hatPoints.position(0).x(x - w / 10).y(y - h / 10);
//                hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
//                hatPoints.position(2).x(x + w / 2).y(y - h / 2);
                //fillConvexPoly(grabbedImage, hatPoints.position(0), 3, Scalar.GREEN, CV_AA, 0);
            }

            // Let's find some contours! but first some thresholding...
            threshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

            // To check if an output argument is null we may call either isNull() or equals(null).
            MatVector contours = new MatVector();
            findContours(grayImage, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
            long n = contours.size();
            for (long i = 0; i < n; i++) {
                Mat contour = contours.get(i);
                Mat points = new Mat();
                approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
                drawContours(grabbedImage, new MatVector(points), -1, Scalar.BLUE);
            }

            // warpPerspective(grabbedImage, rotatedImage, randomR, rotatedImage.size());
            Frame rotatedFrame = converter.convert(rotatedImage);
            frame.showImage(rotatedFrame);
            try {
                recorder.record(rotatedFrame);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }

        }
        frame.dispose();
        try {
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }


//        target = new Target(grabbedImage, face_cascade, mouth_cascade, recorder, converter, grabber, frame);
//        Thread thread = new Thread(target);
//        thread.start();
        //target.detecting();

        grabber.stop();
    }

    public synchronized boolean isNeedChange(){
//        if (target != null) {
//            return target.getNC();
//        } else return false;
        return needChange;
    }

    public synchronized void setNeedChange(boolean t){
        needChange = t;
    }

    class Target implements Runnable {
        Mat grabbedImage;
        CascadeClassifier face_cascade;
        CascadeClassifier mouth_cascade;
        FrameRecorder recorder;
        FrameGrabber grabber;
        OpenCVFrameConverter.ToMat  converter;
        CanvasFrame frame;
        private boolean nc;

        Target(Mat grabbedImage, CascadeClassifier f_c,
               CascadeClassifier m_c, FrameRecorder recorder,
               OpenCVFrameConverter.ToMat  converter, FrameGrabber grabber, CanvasFrame frame) {
            this.grabbedImage = grabbedImage;
            this.recorder = recorder;
            this.converter = converter;
            this.grabber = grabber;
            this.frame = frame;
            face_cascade = f_c;
            mouth_cascade = m_c;
            nc = false;
        }


        public synchronized boolean getNC() {
            return nc;
        }

        public synchronized void detecting() throws Exception{
            int cmw = 0;
            int cmh = 0;
            int cmx = 0;
            int cmy = 0;

            while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
                // Let's try to detect some faces! but we need a grayscale image...
                cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
                RectVector faces = new RectVector();
                RectVector mouths = new RectVector();
                face_cascade.detectMultiScale(grayImage, faces);
                Mat faceROIimage = new Mat();
                if (!faces.empty()) {
                    faceROIimage = new Mat(grayImage, faces.get(0));
                }
                //mouth_cascade.detectMultiScale(grayImage, mouths);

                mouth_cascade.detectMultiScale(faceROIimage, mouths);
                long total = faces.size() < mouths.size() ? faces.size() : mouths.size();
                // total = mouths.size();

                for (long i = 0; i < total; i++) {
                    Rect fr = faces.get(i);
                    Rect mr = mouths.get(0);
                    int x = fr.x(), y = fr.y(), w = fr.width(), h = fr.height();
                    int mx = mr.x(), my = mr.y(), mw = mr.width(), mh = mr.height();
                    rectangle(grabbedImage, new Point(x, y), new Point(x + w, y + h), Scalar.RED, 1, CV_AA, 0);
                    //rectangle(grabbedImage, new Point(mx, my), new Point(mx + mw, my + mh), Scalar.GREEN, 1, CV_AA, 0);
                    rectangle(grabbedImage, new Point(mx + mw, my + mh), new Point(mx, my), Scalar.GREEN, 1, CV_AA, 0);
                    //rectangle(grabbedImage, new Point(x + w/4, y + h - h/3), new Point(x + w - w/4, y + h - h/8), Scalar.YELLOW, 1, CV_AA, 0);
                    if (mr.y() < cmy - mr.y() / 2 || mr.y() + mr.height() > cmy + cmh + (mr.y() + mr.height()) / 3 || mr.x() < cmx - mr.x() / 2) {
                        // rectangle(grabbedImage, new Point(mx + mw, my + mh),new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
                        // rectangle(grabbedImage,  new Point(cmx + cmw, cmy + cmh),new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);
                    }

                    if (mr.width() > cmw + mr.width() / 2 || mr.height() > cmh + mr.height() / 2) {
//                    rectangle(grabbedImage, new Point(mx + mw, my + mh), new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
//                    rectangle(grabbedImage, new Point(cmx + cmw, cmy + cmh), new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);
                        needChange = true;
                        System.out.println("РОТ ОТКРЫТ");

                    } else if (mr.width() < cmw + mr.width() / 2 && mr.height() < cmh + mr.height() / 2) {
                        needChange = false;
                        //System.out.println("РОТ ЗАКРЫТ");
                    }

                    cmx = mr.x();
                    cmy = mr.y();
                    cmw = mr.width();
                    cmh = mr.height();
                    // To access or pass as argument the elements of a native array, call position() before.
//                hatPoints.position(0).x(x - w / 10).y(y - h / 10);
//                hatPoints.position(1).x(x + w * 11 / 10).y(y - h / 10);
//                hatPoints.position(2).x(x + w / 2).y(y - h / 2);
                    //fillConvexPoly(grabbedImage, hatPoints.position(0), 3, Scalar.GREEN, CV_AA, 0);
                }

                // Let's find some contours! but first some thresholding...
                threshold(grayImage, grayImage, 64, 255, CV_THRESH_BINARY);

                // To check if an output argument is null we may call either isNull() or equals(null).
                MatVector contours = new MatVector();
                findContours(grayImage, contours, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
                long n = contours.size();
                for (long i = 0; i < n; i++) {
                    Mat contour = contours.get(i);
                    Mat points = new Mat();
                    approxPolyDP(contour, points, arcLength(contour, true) * 0.02, true);
                    drawContours(grabbedImage, new MatVector(points), -1, Scalar.BLUE);
                }

               // warpPerspective(grabbedImage, rotatedImage, randomR, rotatedImage.size());
                Frame rotatedFrame = converter.convert(rotatedImage);
                frame.showImage(rotatedFrame);
                try {
                    recorder.record(rotatedFrame);
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                }

            }
            frame.dispose();
            try {
                recorder.stop();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            try {
                detecting();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void setUp() throws Exception {
        faceClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_frontalface_alt.xml";
        face_cascade = new CascadeClassifier(faceClassifierName);
        mouthClassifierName = "C:\\Users\\JekaJops\\AndroidStudioProjects\\FluffyDanger\\android\\assets\\cascade_files\\haarcascade_smile.xml";
        mouth_cascade = new CascadeClassifier(mouthClassifierName);
        mouth_cascade.setMaskGenerator(new BaseCascadeClassifier.MaskGenerator(new Pointer()));

        // The available FrameGrabber classes include OpenCVFrameGrabber (opencv_videoio),
        // DC1394FrameGrabber, FlyCapture2FrameGrabber, OpenKinectFrameGrabber, OpenKinect2FrameGrabber,
        // RealSenseFrameGrabber, PS3EyeFrameGrabber, VideoInputFrameGrabber, and FFmpegFrameGrabber.
        grabber = FrameGrabber.createDefault(0);
        grabber.start();

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


        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        // We should also specify the relative monitor/camera response for proper gamma correction.
        frame = new CanvasFrame("Some Title", CanvasFrame.getDefaultGamma() / grabber.getGamma());

        // Let's create some random 3D rotation...
        Mat randomR = new Mat(3, 3, CV_64FC1),
                randomAxis = new Mat(3, 1, CV_64FC1);
        // We can easily and efficiently access the elements of matrices and images
        // through an Indexer object with the set of get() and put() methods.
        DoubleIndexer Ridx = randomR.createIndexer(),
                axisIdx = randomAxis.createIndexer();
        axisIdx.put(0, (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4,
                (Math.random() - 0.5) / 4);
        Rodrigues(randomAxis, randomR);
        double f = (width + height) / 2.0;
        Ridx.put(0, 2, Ridx.get(0, 2) * f);
        Ridx.put(1, 2, Ridx.get(1, 2) * f);
        Ridx.put(2, 0, Ridx.get(2, 0) / f);
        Ridx.put(2, 1, Ridx.get(2, 1) / f);
        System.out.println(Ridx);


        cmy = 0;
        cmx = 0;
        cmw = 0;
        cmh = 0;
    }


    public void detect() throws Exception {
        while (frame.isVisible() && (grabbedImage = converter.convert(grabber.grab())) != null) {
            // Let's try to detect some faces! but we need a grayscale image...
            cvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            RectVector faces = new RectVector();
            RectVector mouths = new RectVector();
            face_cascade.detectMultiScale(grayImage, faces);
            Mat faceROIimage = new Mat();
            if (!faces.empty()) {
                faceROIimage = new Mat(grayImage, faces.get(0));
            }
            //mouth_cascade.detectMultiScale(grayImage, mouths);
            mouth_cascade.detectMultiScale(faceROIimage, mouths);

            long total = faces.size() < mouths.size() ? faces.size() : mouths.size();
            // total = mouths.size();

//        for (long i = 0; i < total; i++) {
            Rect fr = faces.get(0);
            Rect mr = mouths.get(0);
            int x = fr.x(), y = fr.y(), w = fr.width(), h = fr.height();
            int mx = mr.x(), my = mr.y(), mw = mr.width(), mh = mr.height();

            if (mr.width() > cmw + mr.width() / 2 || mr.height() > cmh + mr.height() / 2) {
                //rectangle(grabbedImage, new Point(mx + mw, my + mh), new Point(mx, my), Scalar.RED, 0, CV_AA, 1);
                //rectangle(grabbedImage, new Point(cmx + cmw, cmy + cmh), new Point(cmx, cmy), Scalar.YELLOW, 0, CV_AA, 1);

                // gs.getFluffy().changeTexture();

                needChange = true;
                gs.onFaceDetected(true);

            } else if (mr.width() < cmw + mr.width() / 2 || mr.height() < cmh + mr.height() / 2) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gs.onFaceDetected(false);
                //needChange = detect();
                needChange = false;
            }


            cmx = mr.x();
            cmy = mr.y();
            cmw = mr.width();
            cmh = mr.height();

            Frame rotatedFrame = converter.convert(rotatedImage);
            try {
                recorder.record(rotatedFrame);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void dispose() {
        try {
            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        frame.dispose();
    }

//    public synchronized boolean isNeedChange() {
//        return needChange;
//    }

    @Override
    public void run() {
        try {
            main_4();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}








