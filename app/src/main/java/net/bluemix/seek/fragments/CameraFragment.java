package net.bluemix.seek.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import net.bluemix.seek.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by john on 7/29/2015.
 */
public class CameraFragment extends Fragment {

    public static final String TAG="CameraFragment";
    public static final String EXTRA_PHOTO_FILENAME =
            "net.bluemix.seek.fragments.camerafragment.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private ImageButton mTakePicBtn;
    private ImageButton mSwitchCameraBtn;
    private ProgressBar mProgressBarCamera;

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            mProgressBarCamera.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            boolean success = true;
            if (data != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                if(bitmap!=null) {

                    File file = new File(Environment.getExternalStorageDirectory() + "/seekapp");
                    if (!file.isDirectory()) {
                        file.mkdir();
                    }

                    file = new File(Environment.getExternalStorageDirectory() + "/seekapp", System.currentTimeMillis() + ".jpg");
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        success = false;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.e(TAG, "Error writing to file " + file.getParent(), exception);
                        success = false;
                    }

                    if (success) {
                        //Toast.makeText(getActivity().getApplicationContext(),"JPEG saved at : "+ file.getPath(), Toast.LENGTH_LONG).show();
                        //Log.i(TAG, "JPEG saved at " + Environment.getExternalStorageDirectory().toString());
                        Intent i=new Intent();
                        i.putExtra(EXTRA_PHOTO_FILENAME,file.getPath());
                        getActivity().setResult(Activity.RESULT_OK, i);

                        getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                    }
                    else
                    {
                        getActivity().setResult(Activity.RESULT_CANCELED);
                    }
                }
            }

            getActivity().finish();

        }
    };


    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_camera,container,false);
        mTakePicBtn=(ImageButton)rootView.findViewById(R.id.btnTakePic);
        mSwitchCameraBtn=(ImageButton)rootView.findViewById(R.id.btnCameraSwitch);
        mProgressBarCamera=(ProgressBar)rootView.findViewById(R.id.progressBarCamera);
        mSurfaceView=(SurfaceView)rootView.findViewById(R.id.surfaceViewCamera);
        final SurfaceHolder holder=mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mProgressBarCamera.setVisibility(View.INVISIBLE);

        mTakePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_anim));
                if(mCamera!=null)
                {
                    mCamera.takePicture(mShutterCallback,null,mJpegCallback);
                }
            }
        });

        mSwitchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwitchCameraBtn.startAnimation(AnimationUtils.loadAnimation(container.getContext(), R.anim.rotate_anim));

            }
        });

        setupHolder(holder);

        PackageManager pm = getActivity().getPackageManager();
        boolean hasACamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) ||
                pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD &&
                        Camera.getNumberOfCameras() > 0);
        if (!hasACamera) {
            mTakePicBtn.setEnabled(false);
        }

        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    private void setupHolder(SurfaceHolder holder)
    {
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (mCamera == null) return;
                // The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(),width,height);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(90);
                try {

                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        openCam();

    }

    @Override
    public void onPause() {
        super.onPause();

        releaseCam();
    }

    private void releaseCam()
    {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void openCam()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            mCamera = Camera.open();
        }
    }


    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }
}
