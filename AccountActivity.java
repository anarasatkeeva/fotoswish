package asatkeeva.finalfotoswish;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import static android.content.ContentValues.TAG;

public class AccountActivity extends Activity {
    public final static int PICK_PHOTO_CODE = 1046;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_WRITE_PERMISSION = 786;
    private ImageButton mLogOutButton;
    private ImageView targetImage;
    private Button pic;
    private Button saveB;
    private FirebaseAuth mAuth;
    private Uri uri; //selected pic uri
    private Uri uri1; // taken's pic's uri
    private Button delete;
    private String mCurrentPhotoPath;
    private Bitmap bitmap;
    private static ContentResolver contentResolver;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<Images> all_images = new ArrayList<>();
    ArrayList<String> all_image_path;
    int i = 0;
    boolean boolean_folder;
    boolean keep;
    private GoogleSignInClient mGoogleApiClient;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        imagespath();
        Toast.makeText(this, "You can sign out at any time by swiping down anywhere on the screen", Toast.LENGTH_SHORT).show();
        targetImage = findViewById(R.id.targetimage);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) { //signed out
                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                }
            }
        };

        //Swiper. Shows what happens onSwipe. On swipe right it shows next image in Gallery
        //on swipe left, deletes
        targetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });
        targetImage.setOnTouchListener( new OnSwipeTouchListener(){
            public boolean onSwipeRight() {
                    if(all_image_path.size() > 1) {
                        imageShow(all_image_path.get(i));
                        i++;
                    }

                    if(all_image_path.size() == 1){
                        imageShow(all_image_path.get(i));
                    }
                    
                return true;
            }

            public boolean onSwipeLeft(){
                if(all_image_path.size() == 0) {
                    Toast.makeText(AccountActivity.this, "No more pictures to sort", Toast.LENGTH_SHORT).show();
                }else {
                    deleteFileFromMediaStore();
                    all_image_path.remove(i - 1);
                    targetImage.setImageBitmap(null);
                    imageShow(all_image_path.get(i));
                }
                return true;

            }
            public boolean onSwipeTop(){

                return true;
            }
            public boolean onSwipeBottom(){
               mAuth.signOut();
                return true;
            }
            });
    }

    private void imageShow(String path){
        File imgFile = new  File(path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            targetImage.setImageBitmap(myBitmap);
        }
    }

    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    
    //Requesting runtime write permissions
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    //Get uri's actual path
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    //Delete image
    public  void deleteFileFromMediaStore() {
        ContentResolver contentResolver = getContentResolver();
        if(all_image_path.size() == 1){
        File file = new File(all_image_path.get(0));
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri2 = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri2,
                MediaStore.Files.FileColumns.DATA + "=?", new String[] {canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri2,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }else{
            File file = new File(all_image_path.get(i - 1));
            String canonicalPath;
            try {
                canonicalPath = file.getCanonicalPath();
            } catch (IOException e) {
                canonicalPath = file.getAbsolutePath();
            }
            final Uri uri2 = MediaStore.Files.getContentUri("external");
            final int result = contentResolver.delete(uri2,
                    MediaStore.Files.FileColumns.DATA + "=?", new String[] {canonicalPath});
            if (result == 0) {
                final String absolutePath = file.getAbsolutePath();
                if (!absolutePath.equals(canonicalPath)) {
                    contentResolver.delete(uri2,
                            MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                }
            }
        }

        }

    //Fill an array with all image paths from gallery for each image
    public ArrayList<Images> imagespath(){
        all_images.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));

            for (int i = 0; i < all_images.size(); i++) {
                if (all_images.get(i).get_folder().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            if (boolean_folder) {
                all_image_path = new ArrayList<>();
                all_image_path.addAll(all_images.get(int_position).get_imagePath());
                all_image_path.add(absolutePathOfImage);
                all_images.get(int_position).set_imagePath(all_image_path);

            } else {
                all_image_path = new ArrayList<>();
                all_image_path.add(absolutePathOfImage);
                Images images = new Images();
                images.set_folder(cursor.getString(column_index_folder_name));
                images.set_imagePath(all_image_path);

                all_images.add(images);
            }
        }
        for (int i = 0; i < all_images.size(); i++) {
            Log.e("FOLDER", all_images.get(i).get_folder());
            for (int j = 0; j < all_images.get(i).get_imagePath().size(); j++) {
                Log.e("FILE", all_images.get(i).get_imagePath().get(j));
            }
        }
        return all_images;
    }









    }

