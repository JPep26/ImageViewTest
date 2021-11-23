package com.example.imageviewtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class ImgActivity extends AppCompatActivity {
    private final int GALLERY_CODE=10;
    ImageView photo;
    private FirebaseStorage storage;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        photo=(ImageView) findViewById(R.id.image);
        storage=FirebaseStorage.getInstance();
    }
    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.image:
                loadAlbum();
                break;
            }
        }
    };

    private void loadAlbum(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode,final int resultCode,final Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_CODE){
            Uri file=data.getData();
            StorageReference storageRef=storage.getReference();
            StorageReference riversRef=storageRef.child("photo/1.png");
            UploadTask uploadTask=riversRef.putFile(file);

            try{
                InputStream in=getContentResolver().openInputStream(data.getData());
                Bitmap img=BitmapFactory.decodeStream(in);
                in.close();
                photo.setImageBitmap(img);
            } catch(Exception e){
                e.printStackTrace();
            }

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ImgActivity.this,"사진이 정상적으로 업로드 되지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(ImgActivity.this,"사진이 정상적으로 업로드 되었습니다.",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
