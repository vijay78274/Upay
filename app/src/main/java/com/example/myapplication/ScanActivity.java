package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.myapplication.databinding.ActivityScanBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ScanActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private ImageAnalysis imageAnalysis;
    String accountNo;
    RetrofitService service;
    Apirequest request;
    String rawValue;
ActivityScanBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        service = new RetrofitService();
        request = service.getRetrofit().create(Apirequest.class);
        cameraExecutor = Executors.newSingleThreadExecutor();

        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        barcodeScanner = BarcodeScanning.getClient(options);
        startCamera();
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors (including cancellation) here.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        imageAnalysis = new ImageAnalysis.Builder().build();
        imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void analyzeImage(@NonNull ImageProxy imageProxy) {
        @OptIn(markerClass = ExperimentalGetImage.class) InputImage image = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()), imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                         rawValue = barcode.getRawValue();
                        Log.d("ScanActivity", "Scanned QR Code: " + rawValue);
                        // Stop scanning after successful scan
//                        stopScanning();
                        checkAccountOnServer(rawValue);
                        return; // Exit the loop
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ScanActivity.this, "Failed to scan QR code", Toast.LENGTH_SHORT).show();
                    Log.e("ScanActivity", "Failed to scan QR code", e);
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private boolean isScanningStopped = false;

    private void stopScanning() {
        if (isFinishing() || isScanningStopped) return; // Prevent multiple executions

        isScanningStopped = true; // Set the flag to prevent further calls

        if (imageAnalysis != null) {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) ((ListenableFuture<ProcessCameraProvider>) ProcessCameraProvider.getInstance(this)).get();
                cameraProvider.unbind(imageAnalysis);
                imageAnalysis.clearAnalyzer();
            } catch (ExecutionException | InterruptedException e) {
                Log.e("ScanActivity", "Error unbinding image analysis", e);
            }
        }

        cameraExecutor.shutdown();

        // Navigate to the next activity
        Intent intent = new Intent(ScanActivity.this, Qrpayment.class);
        intent.putExtra("accountNo", accountNo);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
    }
    private void checkAccountOnServer(String accountNumber) {
        request.getAccountByaccountNumber(accountNumber).enqueue(new Callback<payment>() {
            @Override
            public void onResponse(Call<payment> call, Response<payment> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Account found, stop scanning
                    Log.d("ScanActivity", "Account found: " + accountNumber);
                    accountNo=accountNumber;
                    stopScanning();
                }
            }

            @Override
            public void onFailure(Call<payment> call, Throwable t) {
                // Handle failure
                Toast.makeText(ScanActivity.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                Log.e("ScanActivity", "Failed to connect to server", t);
            }
        });
    }
}
