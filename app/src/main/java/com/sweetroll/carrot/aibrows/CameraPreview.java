package com.sweetroll.carrot.aibrows;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Created by Tony Stark on 5/25/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraID;
    private FragmentActivity mContext;
    private static final String TAG = "CameraPreview";

    //Constructor for this class which takes a camera object as one of its arguments
    public CameraPreview(FragmentActivity context, Camera camera, int cameraID){
        super(context);
        mContext =  context;
        mCameraID = cameraID;
        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
    }


    @Override
    //Override to tell the camera where to display the preview
    public void surfaceCreated(SurfaceHolder holder){
        //Camera instance should be instatiated in surfaceCreated()
        mCamera = Camera.open();
        try{
            if(mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException ioe){
            Log.d(TAG, "surfaceCreated(): Error setting camera preview" + ioe.getMessage());
        }
    }

    @Override
    //Override to tell what happens when surface is destroyed
    public void surfaceDestroyed(SurfaceHolder holder) {
        //Camera instance should be released in surfaceDestroyed()
        if( mCamera != null) {
            mCamera.release();
        }
    }


    @Override
    //Override to tell what happens when surface changes
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if(mHolder == null){
            return;
        }
        //stop preview before change it
        try{
            mCamera.stopPreview();
        } catch (Exception e){
            Log.d(TAG, "surfaceChanged(): No existing preview to stop");
        }
        //Now set new preview setting here, such as width, height, orientation ...etc. When setting preview size, must use values returned from getSupportedPreviewSizes() only
        /*......................new setting code here........................
        ........................new setting code here........................*/
        setCameraDisplayOrientation(mContext, 0, mCamera);
        // start preview with the new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "surfaceChanged(): Error starting camera preview: " + e.getMessage());
        }
        Log.d(TAG, "onSurfaceChanged() called");
    }

    //Helper method to get the correct camera orientation on display
    public static void setCameraDisplayOrientation(FragmentActivity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.d(TAG, info.toString());
        Log.d(TAG, "result: " + result);
        try {
            camera.setDisplayOrientation(result);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

}
