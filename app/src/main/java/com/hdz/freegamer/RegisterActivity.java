package com.hdz.freegamer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    //para volver a la vista anterior
    CircleImageView  mCircleImageViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mCircleImageViewBack = findViewById(R.id.circleImageBack);
        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                finish(); //para que llevarnos hacia atras
            }
        });
    }
}
