package com.teamNFC.spartap;

import java.util.HashMap;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;



public class QRCheckin extends Activity
{
	
	private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private TextView scanText;
    private Button scanButton;

    private ImageScanner scanner;

    private boolean barcodeScanned = false;
    private boolean previewing = true;
    
    static 
    {
        System.loadLibrary("iconv");
    } 
    
    
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qr_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        
        //initCamera();
        
        scanText = (TextView)findViewById(R.id.scanText);
        scanButton = (Button)findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new OnClickListener() 
        {
            public void onClick(View v) 
            {
                if (barcodeScanned) 
                {
                    barcodeScanned = false;
                    scanText.setText("Scanning...");
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }

    private void initCamera()
    {
    	autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
    }
    
    @Override
    public void onPause() 
    {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onResume()
    {
    	super.onResume();
    	initCamera();
    }
    
    /** A safe way to get an instance of the Camera object. */
    private Camera getCameraInstance()
    {
        Camera c = null;
        try 
        {
            c = Camera.open();
        } 
        catch (Exception e){}
        return c;
    }

    private void releaseCamera() 
    {
        if (mCamera != null) 
        {
            previewing = false;
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() 
    {
        public void run() 
        {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    PreviewCallback previewCb = new PreviewCallback() 
    {
        public void onPreviewFrame(byte[] data, Camera camera) 
        {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);
            
            if (result != 0) 
            {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                
                SymbolSet syms = scanner.getResults();
                for (final Symbol sym : syms) 
                {
                    barcodeScanned = true;
                    AlertDialog.Builder builder = new AlertDialog.Builder(QRCheckin.this);
                    builder.setCancelable(true);
                    builder.setTitle("NFC Tag Found");
                    builder.setMessage("Would you like to check in?");
                    builder.setInverseBackgroundForced(true);
                    builder.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                	Intent myIntent = new Intent(QRCheckin.this, CheckIn.class);
                                	myIntent.putExtra("nfc_id",sym.getData());
                                	startActivity(myIntent);
                                	finish();
                                    dialog.dismiss();   
                                }
                            });
                    builder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                    Intent myIntent = new Intent(QRCheckin.this, InfoActivity.class);
                                	myIntent.putExtra("nfc_id",sym.getData());
                                	startActivity(myIntent);
                                    
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
        	    }
             }
                }
            };
        
    
    
    // Mimic continuous auto-focusing
     AutoFocusCallback autoFocusCB = new AutoFocusCallback() 
    {
        public void onAutoFocus(boolean success, Camera camera) 
        {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
