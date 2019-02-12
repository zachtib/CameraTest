package com.zachtib.cameratest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    CameraKitView cameraKitView;
    Button takePhotoButton;

    private static final int TAKE_PHOTO_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraKitView = findViewById(R.id.camera);
        takePhotoButton = findViewById(R.id.takePhotoButton);

        takePhotoButton.setOnClickListener(v -> takePhoto());
    }

    protected void takePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Storage Permission")
                            .setMessage("Hi there! We can't save a photo without the write storage permission, could you please grant it?")
                            .setPositiveButton("Yep", (dialogInterface, i) -> ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST))
                            .setNegativeButton("No thanks", (dialogInterface, i) -> Toast.makeText(MainActivity.this, "Can't save photo without permission", Toast.LENGTH_SHORT).show())
                            .show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TAKE_PHOTO_REQUEST);
            }
            // Need to ask for permission
        } else {
            cameraKitView.captureImage((cameraKitView, bytes) -> {
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                File outputFile = new File(externalStorageDirectory, "testPhoto.jpg");
                Log.d(TAG, "Attempting to save to: " + outputFile.toString());
                try {
                    if (!outputFile.exists()) {
                        outputFile.createNewFile();
                    }
                    try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                        outputStream.write(bytes);
                        Toast.makeText(this, "Photo saved", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving photo", e);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraKitView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraKitView.onStop();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TAKE_PHOTO_REQUEST && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        }
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
