package com.lesehankoding.exampleimagepicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lesehankoding.easyimagepicker.ImagePickerActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePickerActivity.showImagePickerOptions(MainActivity.this,2000,800,800);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 2000){
                ImageView imageView = findViewById(R.id.imageView);
                Uri uri = data.getParcelableExtra("path");
                Log.d("RESULT", "onActivityResult: "+uri);
                                File fileImgProduk = new File(uri.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(fileImgProduk.getAbsolutePath());

                imageView.setImageBitmap(bitmap);
            }
        }
    }
}