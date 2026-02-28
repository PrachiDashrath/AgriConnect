package com.example.agriconnect.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.agriconnect.R;
import com.example.agriconnect.models.TransportBill;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TransportBillActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private TextView tvBillNo, tvDate, tvCompanyName, tvCompanyGst;
    private TextView tvCustomerName, tvCustomerPhone;
    private TextView tvPickup, tvDestination, tvDistance;
    private TextView tvBaseFare, tvToll, tvLoading, tvUnloading, tvInsurance;
    private TextView tvSubtotal, tvGst, tvTotal, tvAmountInWords;
    private Button btnDownload, btnShare, btnTrack;
    private ProgressBar progressBar;

    private String billId;
    private String bookingId;
    private DatabaseReference billsRef;
    private TransportBill currentBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_bill);

        billId = getIntent().getStringExtra("billId");
        bookingId = getIntent().getStringExtra("bookingId");

        FirebaseDatabase database = FirebaseDatabase.getInstance(
                "https://agriconnect-5cd4a-default-rtdb.asia-southeast1.firebasedatabase.app/"
        );
        billsRef = database.getReference("transport_bills");

        initViews();

        if (billId != null) {
            loadBillDetails();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Bill ID missing", Toast.LENGTH_SHORT).show();
        }

        setupClickListeners();
    }

    private void initViews() {
        tvBillNo      = findViewById(R.id.tvBillNo);
        tvDate        = findViewById(R.id.tvDate);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvCompanyGst  = findViewById(R.id.tvCompanyGst);
        tvCustomerName  = findViewById(R.id.tvCustomerName);
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone);
        tvPickup      = findViewById(R.id.tvPickup);
        tvDestination = findViewById(R.id.tvDestination);
        tvDistance    = findViewById(R.id.tvDistance);
        tvBaseFare    = findViewById(R.id.tvBaseFare);
        tvToll        = findViewById(R.id.tvToll);
        tvLoading     = findViewById(R.id.tvLoading);
        tvUnloading   = findViewById(R.id.tvUnloading);
        tvInsurance   = findViewById(R.id.tvInsurance);
        tvSubtotal    = findViewById(R.id.tvSubtotal);
        tvGst         = findViewById(R.id.tvGst);
        tvTotal       = findViewById(R.id.tvTotal);
        tvAmountInWords = findViewById(R.id.tvAmountInWords);
        btnDownload   = findViewById(R.id.btnDownload);
        btnShare      = findViewById(R.id.btnShare);
        btnTrack      = findViewById(R.id.btnTrack);
        progressBar   = findViewById(R.id.progressBar);
    }

    private void loadBillDetails() {
        progressBar.setVisibility(View.VISIBLE);
        billsRef.child(billId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                currentBill = snapshot.getValue(TransportBill.class);
                if (currentBill != null) {
                    if (bookingId == null && currentBill.getBookingId() != null) {
                        bookingId = currentBill.getBookingId();
                    }
                    displayBill(currentBill);
                } else {
                    Toast.makeText(TransportBillActivity.this,
                            "Bill not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransportBillActivity.this,
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBill(TransportBill bill) {
        tvBillNo.setText("Bill No: " + safe(bill.getBillId()));
        tvDate.setText("Date: " + safe(bill.getBillDate()));
        tvCompanyName.setText(safe(bill.getTransporterName()));
        tvCompanyGst.setText("GST: " + safe(bill.getTransporterGst()));
        tvCustomerName.setText("Customer: " + safe(bill.getCustomerName()));
        tvCustomerPhone.setText("Phone: " + safe(bill.getCustomerPhone()));
        tvPickup.setText("From: " + safe(bill.getPickupAddress()));
        tvDestination.setText("To: " + safe(bill.getDestinationAddress()));
        tvDistance.setText(String.format("Distance: %.2f km", bill.getDistanceKm()));
        tvBaseFare.setText(String.format("₹%.2f", bill.getBaseFare()));
        tvToll.setText(String.format("₹%.2f", bill.getTollCharges()));
        tvLoading.setText(String.format("₹%.2f", bill.getLoadingCharges()));
        tvUnloading.setText(String.format("₹%.2f", bill.getUnloadingCharges()));
        tvInsurance.setText(String.format("₹%.2f", bill.getInsuranceCharges()));
        double subtotal = bill.getBaseFare() + bill.getTollCharges()
                + bill.getLoadingCharges() + bill.getUnloadingCharges()
                + bill.getInsuranceCharges();
        tvSubtotal.setText(String.format("₹%.2f", subtotal));
        tvGst.setText(String.format("₹%.2f", bill.getGstAmount()));
        tvTotal.setText(String.format("₹%.2f", bill.getTotalAmount()));
        tvAmountInWords.setText("Rupees " + numberToWords((int) bill.getTotalAmount()) + " Only");
    }

    private void setupClickListeners() {

        // ── DOWNLOAD ──────────────────────────────────────────────────
        btnDownload.setOnClickListener(v -> {
            if (currentBill == null) {
                Toast.makeText(this, "Bill not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }
            // Android 9 and below need runtime permission
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                    return;
                }
            }
            generateAndSavePdf();
        });

        // ── SHARE ─────────────────────────────────────────────────────
        btnShare.setOnClickListener(v -> {
            if (currentBill == null) {
                Toast.makeText(this, "Bill not loaded yet", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    "Transport Bill - " + safe(currentBill.getBillId()));
            shareIntent.putExtra(Intent.EXTRA_TEXT, buildBillText());
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // ── TRACK ─────────────────────────────────────────────────────
        btnTrack.setOnClickListener(v -> {
            String trackingId = bookingId;
            if (trackingId == null && currentBill != null) {
                trackingId = currentBill.getBookingId();
            }
            if (trackingId == null) {
                Toast.makeText(this, "Booking ID not found", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, TransportTrackingActivity.class);
            intent.putExtra("bookingId", trackingId);
            if (currentBill != null) {
                intent.putExtra("transporterName", currentBill.getTransporterName());
                intent.putExtra("customerName", currentBill.getCustomerName());
                intent.putExtra("pickup", currentBill.getPickupAddress());
                intent.putExtra("destination", currentBill.getDestinationAddress());
            }
            startActivity(intent);
        });
    }

    // ── PDF GENERATION ─────────────────────────────────────────────────
    private void generateAndSavePdf() {
        // Build the PDF in memory
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page page = document.startPage(pageInfo);
        drawPdfContent(page.getCanvas());
        document.finishPage(page);

        String fileName = "TransportBill_" + safe(currentBill.getBillId())
                .replace(":", "-") + ".pdf";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ — use MediaStore (no permission needed)
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                if (uri != null) {
                    OutputStream os = getContentResolver().openOutputStream(uri);
                    document.writeTo(os);
                    os.close();
                    Toast.makeText(this,
                            "PDF saved to Downloads:\n" + fileName,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Android 9 and below — write directly to Downloads folder
                File downloadsDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) downloadsDir.mkdirs();
                File file = new File(downloadsDir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                document.writeTo(fos);
                fos.close();
                Toast.makeText(this,
                        "PDF saved to Downloads:\n" + fileName,
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Toast.makeText(this,
                    "Failed to save PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } finally {
            document.close();
        }
    }

    private void drawPdfContent(Canvas canvas) {
        TransportBill b = currentBill;
        int left = 40, right = 555, center = 297;
        float y = 60f;

        // Paints
        Paint titlePaint = makePaint(Color.rgb(27, 94, 32), 22f, true, Paint.Align.CENTER);
        Paint headerPaint = makePaint(Color.rgb(21, 101, 192), 13f, true, Paint.Align.LEFT);
        Paint normalPaint = makePaint(Color.BLACK, 11f, false, Paint.Align.LEFT);
        Paint boldPaint   = makePaint(Color.BLACK, 12f, true, Paint.Align.LEFT);
        Paint totalPaint  = makePaint(Color.rgb(27, 94, 32), 15f, true, Paint.Align.LEFT);
        Paint linePaint   = makePaint(Color.LTGRAY, 1f, false, Paint.Align.LEFT);
        linePaint.setStyle(Paint.Style.STROKE);

        // Title
        canvas.drawText("AGRICONNECT - TRANSPORT BILL", center, y, titlePaint);
        y += 8;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 20;

        // Bill info
        canvas.drawText("Bill No: " + safe(b.getBillId()), left, y, boldPaint);
        canvas.drawText("Date: " + safe(b.getBillDate()), 400, y, normalPaint);
        y += 24;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 18;

        // Transporter
        canvas.drawText("TRANSPORTER DETAILS", left, y, headerPaint);
        y += 18;
        canvas.drawText("Name : " + safe(b.getTransporterName()), left, y, normalPaint);
        y += 16;
        canvas.drawText("GST  : " + safe(b.getTransporterGst()), left, y, normalPaint);
        y += 22;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 18;

        // Customer
        canvas.drawText("CUSTOMER DETAILS", left, y, headerPaint);
        y += 18;
        canvas.drawText("Name  : " + safe(b.getCustomerName()), left, y, normalPaint);
        y += 16;
        canvas.drawText("Phone : " + safe(b.getCustomerPhone()), left, y, normalPaint);
        y += 22;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 18;

        // Trip
        canvas.drawText("TRIP DETAILS", left, y, headerPaint);
        y += 18;
        canvas.drawText("From     : " + safe(b.getPickupAddress()), left, y, normalPaint);
        y += 16;
        canvas.drawText("To       : " + safe(b.getDestinationAddress()), left, y, normalPaint);
        y += 16;
        canvas.drawText(String.format("Distance : %.2f km", b.getDistanceKm()), left, y, normalPaint);
        y += 22;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 18;

        // Cost breakdown
        canvas.drawText("COST BREAKDOWN", left, y, headerPaint);
        y += 18;
        y = pdfRow(canvas, "Base Fare",         fmt(b.getBaseFare()),         left, right, y, normalPaint);
        y = pdfRow(canvas, "Toll Charges",       fmt(b.getTollCharges()),       left, right, y, normalPaint);
        y = pdfRow(canvas, "Loading Charges",    fmt(b.getLoadingCharges()),    left, right, y, normalPaint);
        y = pdfRow(canvas, "Unloading Charges",  fmt(b.getUnloadingCharges()),  left, right, y, normalPaint);
        y = pdfRow(canvas, "Insurance",          fmt(b.getInsuranceCharges()),  left, right, y, normalPaint);
        y += 4;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 14;

        double subtotal = b.getBaseFare() + b.getTollCharges() + b.getLoadingCharges()
                + b.getUnloadingCharges() + b.getInsuranceCharges();
        y = pdfRow(canvas, "Subtotal",   fmt(subtotal),          left, right, y, boldPaint);
        y = pdfRow(canvas, "GST (18%)",  fmt(b.getGstAmount()),  left, right, y, normalPaint);
        y += 4;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 4;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 16;
        y = pdfRow(canvas, "TOTAL AMOUNT", fmt(b.getTotalAmount()), left, right, y, totalPaint);
        y += 14;

        // Amount in words
        Paint wordsPaint = makePaint(Color.GRAY, 10f, false, Paint.Align.CENTER);
        canvas.drawText("Rupees " + numberToWords((int) b.getTotalAmount()) + " Only",
                center, y, wordsPaint);
        y += 28;
        canvas.drawLine(left, y, right, y, linePaint);
        y += 14;
        Paint footerPaint = makePaint(Color.GRAY, 9f, false, Paint.Align.CENTER);
        canvas.drawText("Thank you for using AgriConnect Transport Services", center, y, footerPaint);
    }

    private float pdfRow(Canvas c, String label, String value,
                         int left, int right, float y, Paint paint) {
        c.drawText(label, left, y, paint);
        Paint rPaint = new Paint(paint);
        rPaint.setTextAlign(Paint.Align.RIGHT);
        c.drawText(value, right, y, rPaint);
        return y + 18f;
    }

    private Paint makePaint(int color, float size, boolean bold, Paint.Align align) {
        Paint p = new Paint();
        p.setColor(color);
        p.setTextSize(size);
        p.setFakeBoldText(bold);
        p.setTextAlign(align);
        p.setAntiAlias(true);
        return p;
    }

    private String fmt(double value) {
        return String.format("Rs %.2f", value);
    }

    private String safe(String s) {
        return s != null ? s : "N/A";
    }

    private String numberToWords(int n) {
        if (n < 1000) return String.valueOf(n);
        if (n < 100000) return (n / 1000) + " Thousand";
        return (n / 100000) + " Lakh";
    }

    private String buildBillText() {
        return "=== TRANSPORT BILL ===\n\n"
                + tvBillNo.getText() + "\n"
                + tvDate.getText() + "\n\n"
                + "Transporter: " + tvCompanyName.getText() + "\n"
                + tvCompanyGst.getText() + "\n\n"
                + tvCustomerName.getText() + "\n"
                + tvCustomerPhone.getText() + "\n\n"
                + tvPickup.getText() + "\n"
                + tvDestination.getText() + "\n"
                + tvDistance.getText() + "\n\n"
                + "Base Fare    : " + tvBaseFare.getText() + "\n"
                + "Toll         : " + tvToll.getText() + "\n"
                + "Loading      : " + tvLoading.getText() + "\n"
                + "Unloading    : " + tvUnloading.getText() + "\n"
                + "Insurance    : " + tvInsurance.getText() + "\n"
                + "Subtotal     : " + tvSubtotal.getText() + "\n"
                + "GST (18%)    : " + tvGst.getText() + "\n"
                + "─────────────────────\n"
                + "TOTAL        : " + tvTotal.getText() + "\n\n"
                + tvAmountInWords.getText();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generateAndSavePdf();
        } else {
            Toast.makeText(this,
                    "Storage permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}