package com.droidhubworld.qrcodescannersample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.droidhubworld.qrscanner.AutoFocusMode;
import com.droidhubworld.qrscanner.ScanMode;
import com.droidhubworld.qrscanner.Scanner;
import com.droidhubworld.qrscanner.ScannerView;
import com.droidhubworld.qrscanner.callback.DecodeCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.LinkedList;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    View view;

    private Scanner mScanner;
    private int maxZoom = 0;
    private int zoomStep = 5;
    LinkedList<BarcodeFormat> formats = new LinkedList<BarcodeFormat>() {
        {
            add(BarcodeFormat.QR_CODE);
            add(BarcodeFormat.DATA_MATRIX);
            add(BarcodeFormat.AZTEC);
            add(BarcodeFormat.PDF_417);
            add(BarcodeFormat.EAN_13);
            add(BarcodeFormat.EAN_8);
            add(BarcodeFormat.UPC_E);
            add(BarcodeFormat.UPC_A);
            add(BarcodeFormat.CODE_128);
            add(BarcodeFormat.CODE_93);
            add(BarcodeFormat.CODE_39);
            add(BarcodeFormat.CODABAR);
            add(BarcodeFormat.ITF);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.main_container);

        initFlashButton();
        handleScanFromFileClicked();
        handleZoomChanged();
        handleDecreaseZoomClicked();
        handleIncreaseZoomClicked();

        if (!checkPermission()) {
            requestPermission();
        } else {
            initScanner();
        }
    }

    private void initScanner() {
        ScannerView scannerView = findViewById(R.id.scanner_view);
        mScanner = new Scanner(this, scannerView);
        mScanner.setCamera(Scanner.CAMERA_BACK);
        mScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mScanner.setFormats(formats);
        mScanner.setScanMode(ScanMode.SINGLE);
        mScanner.setAutoFocusEnabled(true);
        mScanner.setFlashEnabled(false);
        mScanner.setTouchFocusEnabled(true);
        mScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanner.startPreview();
            }
        });
    }

    private void initFlashButton() {
        findViewById(R.id.layout_flash_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFlash();
            }
        });

        ((ImageView) findViewById(R.id.image_view_flash)).setActivated(false);
    }

    private void handleScanFromFileClicked() {
        findViewById(R.id.layout_scan_from_file_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                navigateToScanFromFileScreen();
                Toast.makeText(MainActivity.this, "Coming Soon..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFlash() {
        ((ImageView) findViewById(R.id.image_view_flash)).setActivated(!((ImageView) findViewById(R.id.image_view_flash)).isActivated());
        mScanner.setFlashEnabled(!mScanner.isFlashEnabled());
    }


    private void handleZoomChanged() {
        ((SeekBar) findViewById(R.id.seek_bar_zoom)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    mScanner.setZoom(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void handleIncreaseZoomClicked() {
        ((ImageView) findViewById(R.id.button_increase_zoom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseZoom();
            }
        });
    }

    private void handleDecreaseZoomClicked() {
        ((ImageView) findViewById(R.id.button_decrease_zoom)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseZoom();
            }
        });
    }

    private void decreaseZoom() {
        if (mScanner.getZoom() > zoomStep) {
            mScanner.setZoom(mScanner.getZoom() - zoomStep);
        } else {
            mScanner.setZoom(0);
        }
        ((SeekBar) findViewById(R.id.seek_bar_zoom)).setProgress(mScanner.getZoom());
    }

    private void increaseZoom() {
        if (mScanner.getZoom() < maxZoom - zoomStep) {
            mScanner.setZoom(mScanner.getZoom() + zoomStep);
        } else {
            mScanner.setZoom(maxZoom);
        }
        ((SeekBar) findViewById(R.id.seek_bar_zoom)).setProgress(mScanner.getZoom());
    }

    private void initZoomSeekBar() {
        Camera.Parameters parameters = getCameraParameters(true);
        maxZoom = parameters.getMaxZoom();
        ((SeekBar) findViewById(R.id.seek_bar_zoom)).setMax(maxZoom);
        ((SeekBar) findViewById(R.id.seek_bar_zoom)).setProgress(parameters.getZoom());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()) {
            initZoomSeekBar();
            mScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (mScanner != null)
            mScanner.releaseResources();
        super.onPause();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {
                        initZoomSeekBar();
                        initScanner();
//                        mScanner.startPreview();
                        Snackbar.make(view, "Permission Granted, Now you can access camera.", Snackbar.LENGTH_LONG).show();
                    } else {

                        Snackbar.make(view, "Permission Denied, You cannot access camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to the permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private Camera.Parameters getCameraParameters(Boolean isBackCamera) {
        try {
            int cameraFacing = getCameraFacing(isBackCamera);
            int cameraId = getCameraId(cameraFacing);
            return Camera.open(cameraId).getParameters();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private int getCameraFacing(Boolean isBackCamera) {
        if (isBackCamera) {
            return Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            return Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
    }

    private int getCameraId(int cameraFacing) {


        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == cameraFacing) {
                return i;
            }
        }
        return -1;

    }
}