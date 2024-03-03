package com.example.test;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDepositDialog extends Dialog implements View.OnClickListener {

    private ImageView qrCodeImageView;
    private TextView walletAddressTextView, walletTypeTextView;
    private EditText amountEditText;
    private Button depositButton;

    // Pass necessary data through constructor or setter methods
    private String walletAddress;
    private String walletType;
    private String qrCodeImageUrl;

    public CustomDepositDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_deposit_dialog);

        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        walletAddressTextView = findViewById(R.id.walletAddressTextView);
        walletTypeTextView = findViewById(R.id.walletTypeTextView);
        amountEditText = findViewById(R.id.amountEditText);
        depositButton = findViewById(R.id.depositButton);

        // Set data to views
        walletAddressTextView.setText("Wallet Address: " + walletAddress);
        walletTypeTextView.setText("Wallet Type: " + walletType);
        // Load QR code image using your preferred method, for example using an image loading library
        // Glide.with(getContext()).load(qrCodeImageUrl).into(qrCodeImageView);

        depositButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.depositButton) {
            // Get the entered deposit amount
            double depositAmount = Double.parseDouble(amountEditText.getText().toString());

            // Handle deposit button click, e.g., initiate deposit process with the entered amount
            // ...

            dismiss();
        }
    }
}
