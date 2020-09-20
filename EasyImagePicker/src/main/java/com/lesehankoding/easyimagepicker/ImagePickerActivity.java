package com.lesehankoding.easyimagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;

import static androidx.core.content.FileProvider.getUriForFile;

public class ImagePickerActivity extends AppCompatActivity {
    private static final String TAG = ImagePickerActivity.class.getSimpleName();
    private static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    private static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    private static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    private static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    private static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    private static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    private static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    private static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";


    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_GALLERY_IMAGE = 1;

    private boolean islockAspectRatio = true, isSetBitmapMaxWidthHeight = true;
    private boolean lockAspectRatio = true, setBitmapMaxWidthHeight = true;
    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 680, bitmapMaxHeight = 680;
    private int IMAGE_COMPRESSION = 80;
    private static String fileName;
    private static int MAX_WIDTH = 480;
    private static int MAX_HEIGHT = 480;

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easyimagepicker_activity_xyz);
        ImagePickerActivity.clearCache(this);
        Intent intent = getIntent();
        if (intent == null) {
            Toast.makeText(getApplicationContext(), "Image picker is missing!", Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, true);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }

    }

    private void takeCameraImage() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                fileName = System.currentTimeMillis() + ".jpg";

//                Bitmap bitmap = Bitmap.createScaledBitmap(fileName, 480, 480, true);


                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                showSettingsDialog();
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Berikan akses");
        builder.setMessage("Berikan akses untuk mengambil kamera dan melihat galerry");
        builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        openSettings();
                        finish();
                    }
                });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void chooseImageFromGallery() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        Permissions.check(this, permissions, null, null, new PermissionHandler() {
            @Override
            public void onGranted() {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                finish();
                Toast.makeText(context,"Berikan akses untuk mengambil kamera dan melihat galerry",Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    cropImage(getCacheImagePath(fileName));
                } else {
                    setResultCancelled();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();

                    cropImage(imageUri);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.REQUEST_CROP:
                if (resultCode == RESULT_OK) {
                    handleUCropResult(data);
                } else {
                    setResultCancelled();
                }
                break;
            case UCrop.RESULT_ERROR:
                final Throwable cropError = UCrop.getError(data);
                Log.e(TAG, "Crop error: " + cropError);
                setResultCancelled();
                break;
            default:
                setResultCancelled();
        }

    }

    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(IMAGE_COMPRESSION);
        Log.d("ImagePickerActivity", "cropImage: "+destinationUri.getAuthority());
        Log.d("ImagePickerActivity", "cropImage: "+destinationUri.getEncodedAuthority());
        Log.d("ImagePickerActivity", "cropImage: "+destinationUri.getEncodedUserInfo());
        Log.d("ImagePickerActivity", "cropImage: "+destinationUri.getUserInfo());


        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));

        //sizing
        options.setMaxBitmapSize(800);
        options.setCompressionQuality(80);

        if (islockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        if (isSetBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(this);

    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        final int MAX_W = data.getIntExtra(INTENT_BITMAP_MAX_WIDTH,MAX_WIDTH);
        final int MAX_H = data.getIntExtra(INTENT_BITMAP_MAX_HEIGHT,MAX_HEIGHT);
        setResultOk(resultUri,MAX_W,MAX_H);
    }

    private void setResultOk(Uri imagePath,int MAX_W,int MAX_H) {
        File fileImgProduk = new File(imagePath.getPath());
        try {
            File newFileImageCompress = new Compressor(this)
                    .setMaxHeight(MAX_H)
                    .setMaxWidth(MAX_W)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .compressToFile(fileImgProduk);
            if(newFileImageCompress != null) {
                Log.d(TAG, "onActivityResult: "+newFileImageCompress);
                Uri uri = Uri.parse(newFileImageCompress.getPath());
                Intent intent = new Intent();
                intent.putExtra("path", uri);
                setResult(RESULT_OK, intent);
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "setResultOk: "+e.getMessage());
        }
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ImagePickerActivity.this, getPackageName() + ".provider", image);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    private static void clearCache(Context context) {
        File path = new File(context.getExternalCacheDir(), "camera");
        if (path.exists() && path.isDirectory()) {
            for (File child : path.listFiles()) {
                child.delete();
            }
        }
    }

    public static void showImagePickerOptions(final Context ctx, final int RESULTCODE, final int MAX_WIDTH, final int MAX_HEIGHT) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//        builder.setTitle("Ambil gambar menggunakan");
//        // add a list
        String[] animals = {"Kamera", "Galery"};
//        builder.setItems(animals,(new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case 0:
//                        launchCameraIntent(ctx,RESULTCODE,MAX_WIDTH,MAX_WIDTH);
//                        break;
//                    case 1:
//                        launchGalleryIntent(ctx,RESULTCODE,MAX_WIDTH,MAX_HEIGHT);
//                        break;
//                }
//            }
//        }));
//        // create and show the alert dialog
//        AlertDialog dialog = builder.create();
//        dialog.show();
//

        final BottomSheetDialog dialog = new BottomSheetDialog(ctx,R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(ctx).inflate(R.layout.bottomdlg_easyimagepicker,(LinearLayout) dialog.findViewById(R.id.lnroot));

        dialog.setContentView(view);
        ImageButton camera_sel =  view.findViewById(R.id.btnCamera);
        ImageButton gallery_sel =  view.findViewById(R.id.btnImage);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCameraIntent(ctx,RESULTCODE,MAX_WIDTH,MAX_WIDTH);
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGalleryIntent(ctx,RESULTCODE,MAX_WIDTH,MAX_HEIGHT);
                dialog.dismiss();
            }
        });
        dialog.show();

//
//        ImagePickerActivity.showImagePickerOptions(ctx, new ImagePickerActivity.PickerOptionListener() {
//            @Override
//            public void onTakeCameraSelected() {
////                launchCameraIntent(ctx,REQUEST_IMAGE,MAX_WIDTH,MAX_HEIGHT);
//                Intent intent = new Intent(ctx, ImagePickerActivity.class);
//                intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
//
//                // setting aspect ratio
//                intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//
//                // setting maximum bitmap width and height
//                intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
//                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, MAX_WIDTH);
//                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, MAX_HEIGHT);
//                ((Activity) ctx).startActivityForResult(intent, REQUEST_IMAGE);
//            }
//
//            @Override
//            public void onChooseGallerySelected() {
////                launchGalleryIntent(ctx,REQUEST_IMAGE,MAX_WIDTH,MAX_HEIGHT);
//                Intent intent = new Intent(ctx, ImagePickerActivity.class);
//                intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
//
//                // setting aspect ratio
//                intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
//                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
//                intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
//
//                // setting maximum bitmap width and height
//                intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
//                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, MAX_WIDTH);
//                intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, MAX_HEIGHT);
//
//                ((Activity)ctx).startActivityForResult(intent, REQUEST_IMAGE);
//            }
//        });
    }

    private static void launchCameraIntent(Context ctx, int REQUEST_IMAGE, int MAX_WIDTH, int MAX_HEIGHT) {
        Intent intent = new Intent(ctx, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, MAX_WIDTH);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, MAX_HEIGHT);
        ((Activity) ctx).startActivityForResult(intent, REQUEST_IMAGE);
    }

    private static void launchGalleryIntent(Context ctx, int REQUEST_IMAGE, int MAX_WIDTH, int MAX_HEIGHT) {
        Intent intent = new Intent(ctx, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, MAX_WIDTH);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, MAX_HEIGHT);

        ((Activity)ctx).startActivityForResult(intent, REQUEST_IMAGE);
    }
}
