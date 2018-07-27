package me.leojlindo.travelbud;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstnameInput;
    private EditText lastnameInput;
    private EditText usernameInput;
    private EditText emailInput;
    private EditText phoneInput;
    private EditText passwordInput;
    private Button signupBtn;
    private Button photoBtn;
    private Uri mediaUrl;
    private ImageView profInput;
    public static final int TAKE_PIC_REQUEST_CODE = 0;
    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    public Uri bmpUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstnameInput = findViewById(R.id.firstname_tv);
        lastnameInput = findViewById(R.id.lastname_tv);
        usernameInput = findViewById(R.id.user_tv);
        emailInput = findViewById(R.id.email_tv);
        phoneInput = findViewById(R.id.phone_tv);
        passwordInput = findViewById(R.id.password_tv);
        signupBtn = findViewById(R.id.Sign_up_btn);
        photoBtn = findViewById(R.id.photo_btn);
        profInput = findViewById(R.id.profpic_iv);

        photoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // showing dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Upload or Take a photo");
                builder.setPositiveButton("Upload", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        // upload image
                        Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePictureIntent.setType("image/+");
                        startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton("Take Photo", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Create a File reference to access to future access
                        photoFile = getPhotoFileUri(photoFileName);

                        // wrap File object into a content provider
                        // required for API >= 24
                        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                        Uri fileProvider = FileProvider.getUriForFile(SignUpActivity.this, "com.codepath.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                        // So as long as the result is not null, it's safe to use the intent.
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // Start the image capture intent to take photo
                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        // connecting sign up button to the main page after signing up
        signupBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();
                final String email = emailInput.getText().toString();

                signUp(view);
            }
        });
    }
    // creating a new user and sending the data to the server
    private void signUp(View view){
        firstnameInput = findViewById(R.id.firstname_tv);
        lastnameInput = findViewById(R.id.lastname_tv);
        usernameInput = findViewById(R.id.user_tv);
        emailInput = findViewById(R.id.email_tv);
        phoneInput = findViewById(R.id.phone_tv);
        passwordInput = findViewById(R.id.password_tv);
        signupBtn = findViewById(R.id.Sign_up_btn);

        ParseUser user = new ParseUser();

        // filling inputs for new user
        user.setPassword(passwordInput.getText().toString());
        user.setUsername(usernameInput.getText().toString());
        user.setEmail(emailInput.getText().toString());
        user.put("firstName", firstnameInput.getText().toString());
        user.put("lastName", lastnameInput.getText().toString());
        user.put("phoneNum",phoneInput.getText().toString());
        user.put("trips", 0);
        user.put("picture", profInput);



        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUp", "Sign Up Success");
                    final Intent intent = new Intent(SignUpActivity.this, Biography.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.d("SignUp", "Sign Up Failed");
                    Toast.makeText(SignUpActivity.this,"Sign Up Failed",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        /*
        photoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Bitmap bitmap = ((BitmapDrawable)) profpic_iv.getDrawable()).getBitmap();
            }
        });
        */
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ImageView ivPreview = (ImageView) findViewById(R.id.profpic_iv);
                ivPreview.setImageBitmap(takenImage);
            }
            else{
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TravelBud");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            //Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        bmpUri = FileProvider.getUriForFile(SignUpActivity.this, "com.codepath.fileprovider", file);

        return file;
    }




    private Uri getOutputMediaFileUri(int mediaTypeImage){
        if(isExternalStorageAvailable()){
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "UPLOADIMAGES");
            if(!mediaStorageDir.exists()){
                if(! mediaStorageDir.mkdirs()){
                    return null;
                }
            }
            File mediaFile = null;
            Date now = new Date();
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

            String path = mediaStorageDir.getPath() + File.separator;
            if(mediaTypeImage == MEDIA_TYPE_IMAGE){
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
            }

            Log.d("UPLOADIMAGE", "FILE: " + Uri.fromFile(mediaFile));
            return Uri.fromFile(mediaFile);
        } else{
            return null;
        }

    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

}
