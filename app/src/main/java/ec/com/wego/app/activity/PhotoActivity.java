package ec.com.wego.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import ec.com.wego.app.R;
import ec.com.wego.app.config.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoActivity extends AppCompatActivity implements
      IPickResult  {

    private Toolbar toolbar;
    private String TAG = PhotoActivity.class.getName();
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private File outPutFile = null;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;
    private String image = "";
    private ImageView img1,img2;
    private Integer select=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);



        /* toolbar*/
        toolbar = (Toolbar) findViewById(R.id.toolbaruser);

        TextView title = (TextView) findViewById(R.id.txtTitle);

        title.setText(getString(R.string.app_name));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSupportActionBar().setHomeAsUpIndicator(getDrawable(R.drawable.ic_arrow));
        } else {
            getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow));
        }


        img1=(ImageView) findViewById(R.id.img1);
        img2=(ImageView) findViewById(R.id.img2);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select=1;
                showFileChooser();
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select =2;
                showFileChooser();

            }
        });





    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //--> ARROW BACK
                onBackPressed();
                finish();
                //------------
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* secondary menu*/
        //getMenuInflater().inflate(R.menu.menu_save, menu);
        //item = menu.findItem(R.id.action_save);
        return true;
    }


    public void showFileChooser() {

        PickImageDialog.build(new PickSetup()
                .setTitle(getResources().getString(R.string.image))
                .setTitleColor(getResources().getColor(R.color.colorPrimaryText))
                .setCameraButtonText(getResources().getString(R.string.camera))
                .setGalleryButtonText(getResources().getString(R.string.sd))
                .setButtonTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setBackgroundColor(getResources().getColor(R.color.colorIcons))
                .setCancelText(getResources().getString(R.string.cancelar))
                .setCancelTextColor(getResources().getColor(R.color.colorPrimaryText))
                .setGalleryIcon(R.drawable.ic_perm_media_black_24dp)
                .setCameraIcon(R.drawable.ic_photo_camera_black_24dp)

        ).show(getSupportFragmentManager());


    }


    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),  r.getBitmap(), "temp", null);
            performCrop(Uri.parse(path));

        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void performCrop(Uri uri) {

        int x=dpToPx(280);
        int y=dpToPx(280);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", x);
        intent.putExtra("outputY", y);
        //intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);
        //Create output file here
        try {
            /*mImageCaptureUri = FileProvider.getUriForFile(AddPlatoActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    createImageFile());*/
            outPutFile =createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }



        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
        startActivityForResult(intent, CROP_FROM_CAMERA);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_FROM_FILE) && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            //bitmap = ProcessImage.compressImage(filePath, getApplicationContext(), null);
            //Getting the Bitmap from Gallery
            performCrop(filePath);

        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {

            Uri imageUri = Uri.parse(mCurrentPhotoPath);
            // ScanFile so it will be appeared on Gallery
            MediaScannerConnection.scanFile(PhotoActivity.this,
                    new String[]{imageUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            performCrop(uri);
                        }
                    });


        }

        if(requestCode==CROP_FROM_CAMERA) {
            try {
                if(outPutFile.exists()){
                    //bitmap = decodeFile(outPutFile);

                    InputStream ims = new FileInputStream(outPutFile);
                    bitmap= BitmapFactory.decodeStream(ims);

                    //imagen.setImageBitmap(bitmap);

                    image = Constants.getStringImage(bitmap);

                    if(select==1){
                        img1.setImageBitmap(bitmap);
                    }else if(select==2)
                    {
                        img2.setImageBitmap(bitmap);
                    }


                    //imagen.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }



}
