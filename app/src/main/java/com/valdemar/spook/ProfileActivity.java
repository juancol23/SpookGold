package com.valdemar.spook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView stateTextView;
    private boolean isStateA = true;


    private CircleImageView profileImageView;
    private TextView profileNameTextView;
    private TextView profileStatusTextView;
    private Button messageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        profileStatusTextView = findViewById(R.id.profileStatusTextView);
        messageButton = findViewById(R.id.messageButton);

        // Simulando datos para el perfil
        profileImageView.setImageResource(R.drawable.fondo_moneda);
        profileNameTextView.setText("Juan Pérez");
        profileStatusTextView.setText("Disponible");

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simulando la acción de enviar un mensaje
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra("recipientName", "Juan Pérez");
                startActivity(intent);
            }
        });
    }
}