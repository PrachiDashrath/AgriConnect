package com.example.agriconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertificateActivity extends AppCompatActivity {

    TextView tvCertId, tvIssueDate;
    TextView tvFarmerName, tvVillage;
    TextView tvCertifiedCrops, tvInspectionDate, tvNextInspection;

    private PdfDocument pdfDocument;

    private final ActivityResultLauncher<Intent> createPdfLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            try {
                                Uri uri = result.getData().getData();
                                if (uri != null && pdfDocument != null) {

                                    OutputStream outputStream =
                                            getContentResolver().openOutputStream(uri);

                                    pdfDocument.writeTo(outputStream);

                                    outputStream.close();
                                    pdfDocument.close();

                                    Toast.makeText(this,
                                            "Certificate saved successfully!",
                                            Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(this,
                                        "Error saving PDF",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        bindViews();

        MarketPrice market =
                (MarketPrice) getIntent().getSerializableExtra("marketObject");

        if (market != null) {
            tvVillage.setText("City: " + safe(market.getLocation()));
            tvCertifiedCrops.setText("Certified Crop: " + safe(market.getCropName()));
            tvInspectionDate.setText("Grade: " + safe(market.getGrade()));
            tvNextInspection.setText("Inspector Note: " + safe(market.getInspectorFeedback()));
        }

        tvCertId.setText("Certificate No: CERT-" + System.currentTimeMillis());

        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        tvIssueDate.setText("Date of Issue: " + date);

        Button btnDownload = findViewById(R.id.btnDownloadPdf);
        btnDownload.setOnClickListener(v -> generatePdf());
    }

    private void bindViews() {
        tvCertId = findViewById(R.id.tvCertId);
        tvIssueDate = findViewById(R.id.tvIssueDate);
        tvFarmerName = findViewById(R.id.tvFarmerName);
        tvVillage = findViewById(R.id.tvVillage);
        tvCertifiedCrops = findViewById(R.id.tvCertifiedCrops);
        tvInspectionDate = findViewById(R.id.tvInspectionDate);
        tvNextInspection = findViewById(R.id.tvNextInspection);
    }

    private void generatePdf() {

        try {

            Button btnDownload = findViewById(R.id.btnDownloadPdf);

            // 🔥 Hide button before capturing layout
            btnDownload.setVisibility(View.GONE);

            View view = findViewById(R.id.certificateRoot);

            Bitmap bitmap = Bitmap.createBitmap(
                    view.getWidth(),
                    view.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);

            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo =
                    new PdfDocument.PageInfo.Builder(
                            bitmap.getWidth(),
                            bitmap.getHeight(),
                            1
                    ).create();

            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            // 🔥 Show button again after capture
            btnDownload.setVisibility(View.VISIBLE);

            // Launch system file picker
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.setType("application/pdf");
            intent.putExtra(Intent.EXTRA_TITLE,
                    "Certificate_" + System.currentTimeMillis() + ".pdf");

            createPdfLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error generating PDF",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }
}