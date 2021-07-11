package com.sunflower.vspace;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegProfileSetup extends AppCompatActivity implements View.OnClickListener  {
    private static final int RESULT_LOAD_IMG = 200;
    //region ::::::::VIEW ELEMENTS:::::::::
    private ImageView Avatar;
    // One Preview Image
    private ImageView IVPreviewImage;

    // constant to compare
    // the activity result code
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    String currentPhotoPath;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_profile_setup);
        getSupportActionBar().hide();
        Avatar = (ImageView) findViewById(R.id.imageView5);
    }
    private void galleryAddPic() {
        Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 105);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                Avatar.setImageURI(Uri.fromFile(f));
                Log.d("tag", "ABsolute Url of Image is " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }

        }
            if(requestCode == GALLERY_REQUEST_CODE){
                if(resultCode == Activity.RESULT_OK){
                    Uri contentUri = data.getData();
                    Uri lol = data.getData();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp +"."+getFileExt(contentUri);
                    Log.d("taggttt", "onActivityResult: Gallery Image Uri:  " +  imageFileName);
                    Avatar.setImageURI(data.getData());



                }

            }


    }
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
    @Override
    public void onClick(View v) {
        galleryAddPic();
    }
}