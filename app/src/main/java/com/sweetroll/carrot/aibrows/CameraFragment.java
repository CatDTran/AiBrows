package com.sweetroll.carrot.aibrows;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import java.text.SimpleDateFormat;

import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by Tony Stark on 5/24/2017.
 */

public class CameraFragment extends Fragment {

    private Camera mCamera;
    private int mCameraID = 0;
    private CameraPreview mPreview;
    public ImageButton mCaptureButton;
    private ImageButton mOvalButton;
    private ImageButton mGalleryButton;
    private ImageView mOvalFace;
    private RelativeLayout mCameraControlLayout;
    private RelativeLayout mPictureViewLayout;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int REQUEST_CODE_PICTURE = 69;
    private static final String TAG = "CameraFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //hide action (title) bar in this camera fragment
        if(getActivity().getActionBar() != null) {
            getActivity().getActionBar().hide();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraControlLayout = (RelativeLayout) rootView.findViewById(R.id.camera_control_layout);
        mPictureViewLayout = (RelativeLayout) rootView.findViewById((R.id.picture_view_layout));
        mCaptureButton = (ImageButton) rootView.findViewById(R.id.button_capture);
        mOvalFace = (ImageView) rootView.findViewById(R.id.oval_face);
        //onClick() functionality for display oval button
        mOvalButton = (ImageButton) rootView.findViewById(R.id.oval_button);
        mOvalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOvalFace.getVisibility() == View.VISIBLE){
                    mOvalFace.setVisibility(View.INVISIBLE);
                }
                else{
                    mOvalFace.setVisibility(View.VISIBLE);
                }
            }
        });
        //onClick() functionality for gallery button
        mGalleryButton = (ImageButton) rootView.findViewById(R.id.button_gallery);
        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_PICTURE);
            }
        });
        setHasOptionsMenu(false);
        mPreview = new CameraPreview(getActivity(), this, mCamera, mCameraID);
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume(): mCamera is " + mPreview.getCamera());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_camera_menu, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //first check if request code, result code, returned from image picking activity is appropriate
        if ((requestCode == REQUEST_CODE_PICTURE) && (resultCode == RESULT_OK) && (data.getData() != null)){
            Uri uri = data.getData();
            displayPictureFromFile(mCameraControlLayout, mPictureViewLayout, data);
        }
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

    //helper method to return the capture button
    public void implementOnclickListener(final Camera camera){
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(mShutterCallback, null, mPicture);
            }
        });
    }

    //Implement PictureCallBack interface to save captured pictures
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        //this callback method get called when a picture is taken
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken() called");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }
            //try to write picture out to file
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                String filePath = fos.getFD().toString();
                fos.close();
                //automatically start camera's preview again after taking a picture
                camera.startPreview();
                Log.d(TAG, "File saved successfully at " + pictureFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    //Implement ShuttleCallBack to place sound when picture is taken
    private final Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            AudioManager mgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
        }
    };

    //Helper method to create an URI for saving image or video
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //Helper method to save image an video
    private static File getOutputMediaFile(int type){
        // check if the external storage (SD card) is mounted and available for saving media files
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "BrowsRuzler");
            //if mediaStorageDir does not exist
            if(!mediaStorageDir.exists()){
                //if failed trying to create a directory
                if(!mediaStorageDir.mkdir()){
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }
            //Create media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            } else {
                Log.d(TAG, "Wrong media type");
                return null;
            }
            return mediaFile;
        } else{ //if external storage (SD card) is not available, then let the user know with a Toast
            Toast.makeText(MainActivity.getContext(), "External Storage (SD card) is not available", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    //Helper method to hide camera control layout and load a picture from file into image view layout
    private void displayPictureFromFile(final RelativeLayout cameraControlLayout, final RelativeLayout pictureViewLayout, Intent data){
        cameraControlLayout.setVisibility(View.INVISIBLE);
        pictureViewLayout.setVisibility(View.VISIBLE);
        ImageButton closeImageButton = (ImageButton) pictureViewLayout.findViewById(R.id.close_image_view_button);
        ImageView imageView = (ImageView) pictureViewLayout.findViewById(R.id.picture_view);
        //get data from intent, convert to bitmap and display it on the ImageView
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri.toString());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        //try to get bitmap from uri using ContentResolver
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            Log.d(TAG, "Uri's scheme is: " + uri.getScheme());
            imageView.setImageBitmap(bitmap);
        } catch (Exception e){
            Log.d(TAG, e.toString());
            Toast.makeText(getContext(), "Failed to display image file", Toast.LENGTH_LONG).show();
        }
        //implement onClickListener for the close picture button
        closeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureViewLayout.setVisibility(View.INVISIBLE);
                cameraControlLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
