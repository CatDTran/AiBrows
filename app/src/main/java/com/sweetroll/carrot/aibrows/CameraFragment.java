package com.sweetroll.carrot.aibrows;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Tony Stark on 5/24/2017.
 */

public class CameraFragment extends Fragment {

    private Camera mCamera;
    private int mCameraID = 0;
    private CameraPreview mPreview;
    private static final String TAG = "CameraFragment";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        setHasOptionsMenu(true);
        mPreview = new CameraPreview(getActivity(), mCamera, mCameraID);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_camera_menu, menu);
    }

    // Check if this device has a camera
    private boolean checkCameraHardware(Context context) {
        // this device has a camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else { // if this device does not
            return false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //release camera before when fragment is on pause
        if (mCamera != null) {
            mCamera.release();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }


    //A helper method which provides a safe way to get an instance of the Camera object
    public static Camera getCameraInstance(int cameraID){
        Camera camera = null;
        try {
            camera = Camera.open(cameraID); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "Camera cannot be opend ");
        }
        return camera; // returns null if camera is unavailable
    }

}
