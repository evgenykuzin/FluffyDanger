package com.mygdx.game;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SelfyManager extends Activity {

    SurfaceView sv;
    SurfaceHolder holder;
    // HolderCallback holderCallback;
    Camera camera;
    CameraDevice cameraDevice;
    TextureView textureView;
    final int CAMERA_ID = 0;
    final boolean FULL_SCREEN = true;

    public SelfyManager() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // sv = (SurfaceView) findViewById(R.id.surfaceView);
        holder = sv.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

//        holderCallback = new HolderCallback();
//        holder.addCallback(holderCallback);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void takePicture() {
        if (null == cameraDevice) {
            // Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            CaptureRequest.Builder captureBuilder = null;
            try {
                captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            GradientDrawable.Orientation ORIENTATIONS;
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
            final File file = new File(Environment.getExternalStorageDirectory() + "/Billboard_" + timeStamp + ".jpg");
            //get the location from the NetworkProvider
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
//                    longitude = location.getLongitude();
//                    latitude = location.getLatitude();
//                    storeGeoCoordsToImage(file, location);
//                    Log.e(TAG, "Latitude = " + latitude);
//                    Log.e(TAG, "Longitude = " + longitude);
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            };
            // update location listener lm.
            //requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            // reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    //createCameraPreview();
                }
            };
            Handler mBackgroundHandler = null;
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
//                    try {
//                        //session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
//                    } catch (CameraAccessException e) {
//                        e.printStackTrace();
//                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

}