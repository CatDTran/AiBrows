package com.sweetroll.carrot.aibrows;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
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
    private static final String TAG = "CameraPreview";

    //Constructor for this class which takes a camera object as one of its arguments
    public CameraPreview(Context context, Camera camera){
        super(context);
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    //Helper method to tell the camera where to display the preview
    public void surfaceCreated(SurfaceHolder holder){
        try{
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException ioe){
            Log.d(TAG, "Error setting camera preview" + ioe.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    //Helper method to determine what to do when preview change or rotate
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if(mHolder == null){
            return;
        }
        //stop preview before change it
        try{
            mCamera.stopPreview();
        } catch (Exception e){
            Log.d(TAG, "No existing preview to stop");
        }
        //Now set new preview setting here, such as width, height, orientation ...etc. When setting preview size, must use values returned from getSupportedPreviewSizes() only
        /*......................new setting code here........................
        ........................new setting code here........................*/

        // start preview with the new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
