package com.pikk.pikkc.land;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;
import com.google.common.util.concurrent.ListenableFuture;
import com.pikk.pikkc.Login.login_act;
import com.pikk.pikkc.R;
import com.pikk.pikkc.bottosheets.cam_cap_bottom_sheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class land_act extends AppCompatActivity {
    public cam_cap_bottom_sheet _CAM_CAP_BOTT_SHT ;
    public Button app_cam_cap_dat_set_butt;
    public NavigationView app_cam_cap_nav_cont;
    public DrawerLayout app_cam_cap_draw_lay;
    public AppBarLayout app_cam_cap_app_bar;
    public Toolbar app_cam_cap_tool_bar;
    public ImageButton app_cam_cap_cam_swt_butt;
    public ImageButton app_cam_cap_fin_butt;
    public PreviewView app_cam_prev_viw;
    public TextView app_cam_asp_butt;
    public LinearLayout app_cam_cap_base_noti;
    public Button app_cam_cap_sign_in_button;
    public Button app_side_nav_loggd_name_butt;
    public Switch app_cam_cap_comp_swt;

    public boolean _CAM_FULL_ASP_FLAG=false;
    public int _DISP_WIDH_INT=0;
    public int _DISP_HGT_INT = 0;
    public int _CURR_CAM_FACING = 1;
    public int _APP_CAM_FLASH_FLAG = 2;


    Scope ACCESS_DRIVE_SCOPE = new Scope(DriveScopes.DRIVE);
    Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);
    private static final int RC_SIGN_IN = 1;
    private static final int RC_AUTHORIZE_DRIVE = 007;
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    public String APP_LOGGED_NAME = "NAME";
    public Boolean APP_LOGGED_BOOL = false;


    public GoogleSignInClient mGoogleSignInClient;
    public Drive googleDriveService;
    CameraSelector cameraSelector;

    private Executor executor = Executors.newSingleThreadExecutor();

    public ImageButton app_lnkd_butt;
    public ImageButton app_insta_butt;
    public ImageButton app_mail_butt;

    protected GoogleSignInAccount account;
    public ArrayList<com.google.api.services.drive.model.File> getDriveFiles = null;
    public GoogleAccountCredential credential;

    public com.google.api.services.drive.model.File selec_folder = null;
    public int selec_posi;
    MediaPlayer mp;

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        mGoogleSignInClient  = GoogleSignIn.getClient(this, gso);
        mp = MediaPlayer.create(getAct(), R.raw.cam_click);
        if(mGoogleSignInClient !=null) {
            account = GoogleSignIn.getLastSignedInAccount(this);
            if(account==null){
                Toast.makeText(getAct(),"No user logged in", Toast.LENGTH_SHORT).show();
                APP_LOGGED_BOOL = false;
            }
            else{
                credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Collections.singleton(DriveScopes.DRIVE));
                credential.setSelectedAccount(account.getAccount());
                APP_LOGGED_BOOL = true;
                APP_LOGGED_NAME = account.getGivenName();
            }
        }

        setContentView(R.layout.activity_land_act);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        _DISP_WIDH_INT = displayMetrics.widthPixels;
        _DISP_HGT_INT  = displayMetrics.heightPixels;

        _init_elements();
        _set_elements();
    }




    private boolean _init_elements(){
        app_lnkd_butt = findViewById(R.id.app_lnkd_butt);
        app_cam_prev_viw = findViewById(R.id.camera);
        app_mail_butt = findViewById(R.id.app_mail_butt);
        app_insta_butt = findViewById(R.id.app_insta_butt);
        app_cam_cap_dat_set_butt = findViewById(R.id.app_cam_cap_dat_butt);
        app_cam_cap_app_bar = findViewById(R.id.app_cam_cap_appbar);
        app_cam_cap_tool_bar = findViewById(R.id.app_cam_cap_toolbar);
        app_cam_cap_nav_cont = findViewById(R.id.app_cam_cap_nav_cont);
        app_cam_cap_draw_lay = findViewById(R.id.app_cam_cap_draw_lay);
        app_cam_cap_cam_swt_butt = findViewById(R.id.app_cam_cap_cam_swt_butt);
        app_cam_cap_fin_butt = findViewById(R.id.app_cam_cap_fin_butt);
        app_cam_asp_butt = findViewById(R.id.app_cam_asp_butt);
        app_cam_cap_base_noti = findViewById(R.id.app_cam_cap_base_noti);
        app_side_nav_loggd_name_butt = findViewById(R.id.app_side_nav_loggd_name_butt);
        app_cam_cap_comp_swt = findViewById(R.id.app_cam_cap_comp_swt);
        app_cam_cap_sign_in_button = findViewById(R.id.app_cam_cap_sign_in_button);
        return true;
    }


    private void checkForGooglePermissions() {
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getApplicationContext()), ACCESS_DRIVE_SCOPE, SCOPE_EMAIL))
        {
            GoogleSignIn.requestPermissions(
                    getAct(),
                    RC_AUTHORIZE_DRIVE,
                    GoogleSignIn.getLastSignedInAccount(getAct()),
                    ACCESS_DRIVE_SCOPE,
                    SCOPE_EMAIL);
        } else {
            Intent land_int = new Intent(getAct(), land_act.class);
            Log.d("dtask", "onActivityResult: LOGGIN TRUE");
            Toast.makeText(getAct(),"Login successful. Sorted!", Toast.LENGTH_SHORT).show();
            land_int.setAction("UserRecoverableAuthIOException");
            startActivity(land_int);
            finish();
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RC_SIGN_IN:{
                Log.d("dtask", "onActivityResult: GOOGLE SIGNIN RESULT");
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .addOnSuccessListener(googleSignInAccount -> {
                            checkForGooglePermissions();
                        }).addOnFailureListener(e -> {
                            Log.d("dtask", "onActivityResult: LOGIN RESULT FAILED FOR DRIVE");
                        });
                break;
            }
            default:{
                Toast.makeText(getAct(),"Login Failed", Toast.LENGTH_SHORT).show();
                Log.d("dtask", "AUTH FAILED ");
                break;
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean _set_elements(){
        //app_cam_cap_sign_in_button.setSize(SignInButton.SIZE_WIDE);
        if(checkSignIn()){
            app_cam_cap_sign_in_button.setVisibility(View.GONE);
        }
            app_cam_cap_sign_in_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(account!=null) {
                            checkForGooglePermissions();
                        }
                        else{
                            signIn();
                        }
                }
            });
        refreshFolderList();
        getDriveFiles = new ArrayList();
        _CAM_CAP_BOTT_SHT = new cam_cap_bottom_sheet(getAct(),getDriveFiles);
        if(APP_LOGGED_BOOL){
            app_side_nav_loggd_name_butt.setText(APP_LOGGED_NAME+"'s Google Drive");
        }else{
            app_side_nav_loggd_name_butt.setText("Guest User");
        }



        app_insta_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/pikk.co.in?igshid=1acxjy6ajc4s0"));
                startActivity(browserIntent);
            }
        });
        app_lnkd_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/pikk"));
                startActivity(browserIntent);
            }
        });

        app_mail_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:pikkcompany@gmail.com"));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                startActivity(intent);
            }
        });

        app_side_nav_loggd_name_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSignIn()){
                AlertDialog.Builder builder = new AlertDialog.Builder(getAct());
                builder.setCancelable(true);
                builder.setTitle("Logout");
                builder.setMessage("Do you want to logout?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                startActivity(new Intent(getAct(), login_act.class));
                                finish();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                }
            }
        });

        setSupportActionBar(app_cam_cap_tool_bar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_reorder_three_outline);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("pikkC");


        app_cam_prev_viw.setLayoutParams(new LinearLayout.LayoutParams(_DISP_WIDH_INT,_DISP_WIDH_INT));
        Log.d("dtask", "_set_elements: "+CameraSelector.LENS_FACING_FRONT);
        app_cam_cap_dat_set_butt.setOnClickListener(v -> {
                        if (checkSignIn()) {
                            _CAM_CAP_BOTT_SHT.show(getSupportFragmentManager(), "bott");
                        }
                }
        );
        app_cam_cap_nav_cont.findViewById(R.id.app_app_bar_head_thr_butt).setOnClickListener(v -> app_cam_cap_draw_lay.closeDrawer(Gravity.LEFT));

        app_cam_asp_butt.setOnClickListener(v -> {
            Log.d("dtask", "_set_elements: ASPECT CLICK");
            if(!_CAM_FULL_ASP_FLAG){
                app_cam_prev_viw.setLayoutParams(new LinearLayout.LayoutParams(_DISP_WIDH_INT,1440));
                app_cam_asp_butt.setText("4:3");
                app_cam_cap_base_noti.setVisibility(View.GONE);
                _CAM_FULL_ASP_FLAG=true;
            }
            else{
                app_cam_prev_viw.setLayoutParams(new LinearLayout.LayoutParams(_DISP_WIDH_INT,_DISP_WIDH_INT));
                app_cam_cap_base_noti.setVisibility(View.VISIBLE);
                app_cam_asp_butt.setText("1:1");
                _CAM_FULL_ASP_FLAG=false;
            }
        });

        if(allPermissionsGranted()){ startCamera();
        } else{ ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS); }
        return true;
    }

    public boolean checkSignIn(){
        if(account==null){
            //Toast.makeText(getAct(),"Google account not signed in", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            Log.d("dtask", "land auth check Google account signed in");
            //Toast.makeText(getAct(),"Google account signed in", Toast.LENGTH_SHORT).show();
            return true;
        }
    }


    @SuppressLint("StaticFieldLeak")
    public boolean refreshFolderList(){
        if(checkSignIn()){
        //Toast.makeText(getAct(),"Folder list refreshing", Toast.LENGTH_SHORT).show();
        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("Pikkc").build();

                    getDriveFiles.clear();
                    Log.d("dtask", "doInBackground: INIT LIST SIZE"+getDriveFiles.size());
                    Log.d("dtask", "doInBackground: FILE GETTER INIT");
                    FileList result = googleDriveService.files().list().setQ("trashed = false").execute();
                    List<com.google.api.services.drive.model.File> files = result.getFiles();
                    Log.d("dtask", "doInBackground: FILES COUNT"+files.size());
                    for (com.google.api.services.drive.model.File file : files) {
                        if(file.getMimeType().equals("application/vnd.google-apps.folder")){
                            Log.d("dtask", "doInBackground: FOLDER ADDED for name"+file.getName()+" is"+file.getTrashed());
                            getDriveFiles.add(file);
                        }
                        else{
                            Log.d("dtask", "doInBackground: NOT FOLDER");
                        }
                    }

                } catch (IOException e) {
                    Log.d("dtask", "onCreate: FILES FAILED"+e.toString());

                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
               synchronized (_CAM_CAP_BOTT_SHT){
                _CAM_CAP_BOTT_SHT.refreshList();}
            }
        }.execute();
            return true;
        }
        return false;
    }

    private void signOut() {
        if(checkSignIn()){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getAct(),"Sign out successfull", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getAct(), login_act.class));
                        finish();
                    }
                });
        }
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                app_cam_cap_cam_swt_butt.setOnClickListener(v -> {
                    swtCurrCam();
                    bindPreview(cameraProvider);
                });
                app_cam_cap_comp_swt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            bindPreview(cameraProvider);
                            Toast.makeText(getAct(),"Image compression ON[40%]", Toast.LENGTH_SHORT).show();
                        }else{
                            bindPreview(cameraProvider);
                            Toast.makeText(getAct(),"Image compression OFF", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                app_cam_cap_tool_bar.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.app_bar_flash_butt:{
                            if(_APP_CAM_FLASH_FLAG==1){
                                Toast.makeText(getAct(),"Flash Off", Toast.LENGTH_SHORT).show();
                                item.setIcon(ContextCompat.getDrawable(getAct(), R.drawable.ic_flash_off_outline));
                                _APP_CAM_FLASH_FLAG = 2;
                                bindPreview(cameraProvider);
                            }
                            else if(_APP_CAM_FLASH_FLAG==2){
                                Toast.makeText(getAct(),"Flash On", Toast.LENGTH_SHORT).show();
                                item.setIcon(ContextCompat.getDrawable(getAct(), R.drawable.ic_flash_outline));
                                _APP_CAM_FLASH_FLAG = 1;
                                bindPreview(cameraProvider);
                            }
                            return true;
                        }
                        default:
                            return onOptionsItemSelected(item);
                    }
                });
                bindPreview(cameraProvider);
        } catch (ExecutionException | InterruptedException e) {
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void swtCurrCam(){
        Log.d("dtask", "CURR CAM CHANGE");
        if(_CURR_CAM_FACING==Camera.CameraInfo.CAMERA_FACING_FRONT){_CURR_CAM_FACING=Camera.CameraInfo.CAMERA_FACING_BACK;
            app_cam_cap_cam_swt_butt.setImageResource(R.drawable.ic_camera_reverse_outline);
        }
        else{_CURR_CAM_FACING=Camera.CameraInfo.CAMERA_FACING_FRONT;
            app_cam_cap_cam_swt_butt.setImageResource(R.drawable.ic_camera_outline__1_);
        }
    }

    @SuppressLint("WrongConstant")
    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        cameraSelector = new CameraSelector.Builder().requireLensFacing(_CURR_CAM_FACING).build();
        cameraProvider.unbindAll();
        Preview preview = new Preview.Builder()
                .build();
        @SuppressLint("RestrictedApi") ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(_DISP_WIDH_INT,_DISP_WIDH_INT))
                .setDefaultResolution(new Size(_DISP_WIDH_INT,_DISP_WIDH_INT))
                .setMaxResolution(new Size(_DISP_WIDH_INT,_DISP_WIDH_INT))
                .build();
        ImageCapture.Builder builder = new ImageCapture.Builder().setFlashMode(_APP_CAM_FLASH_FLAG).setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetAspectRatio(AspectRatio.RATIO_4_3);
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }
        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(app_cam_prev_viw.createSurfaceProvider());
        androidx.camera.core.Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        app_cam_cap_fin_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mp.start();
                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                //File file = new File(getBatchDirectoryName(), mDateFormat.format(new Date()) + ".jpg");
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, mDateFormat.format(new Date()) + "_GEN_IMAGE");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder( getContentResolver(),MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {
                            int qual;
                            if (app_cam_cap_comp_swt.isChecked()) {
                                qual = 40;
                            } else {
                                qual = 100;
                            }
                            com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
                            Log.d("dtask", "onImageSaved: SAVED URI"+outputFileResults.getSavedUri().getPath().toString()   );
                            fileMetadata.setName(mDateFormat.format(new Date()) + "_GEN_IMAGE");
                            fileMetadata.setMimeType("image/jpeg");
                            if (selec_folder != null) {
                                Log.d("dtask", "onImageSaved: id============> " + selec_folder.getId());
                                fileMetadata.setParents(Collections.singletonList(selec_folder.getId()));
                            } else {
                                Log.d("dtask", "no folder set");
                            }

                            java.io.File filePath = new java.io.File(getPath(outputFileResults.getSavedUri()));

                            Bitmap original = null;
                            try {
                                original = BitmapFactory.decodeStream(new FileInputStream(filePath));
                                FileOutputStream out = new FileOutputStream(filePath);
                                original.compress(Bitmap.CompressFormat.JPEG, qual, out);
                            } catch (FileNotFoundException e) {
                                Log.d("dtask", "onImageSaved: COMPRESSION ERROR OCCUR" + e.toString());
                                e.printStackTrace();
                            }
                            if (checkSignIn()) {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        try {
                                            if (checkSignIn()) {
                                                Log.d("dtask", "doInBackground: TRY TO GOOGLE UPLOAD");
                                                FileContent mediaContent = new FileContent("image/jpeg", filePath);
                                                googleDriveService.files().create(fileMetadata, mediaContent).setFields("id").execute();
                                            } else {
                                                Log.d("dtask", "doInBackground: GOOGLE UPLOAD NO ACCOUNT");
                                            }
                                            Log.d("dtask", "onClick: FILE UPLOAD SUCCESS");
                                        } catch (IOException e) {
                                            Log.d("dtask", "onClick: FILE UPLOAD ERROR OCCUR" + e.toString());
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        Toast.makeText(getAct(), "Image saved to drive", Toast.LENGTH_SHORT).show();
                                    }
                                }.execute();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() -> {
                            Log.e("dtask", "run: " + exception.toString());
                            Toast.makeText(getAct(), "Image cannot be Captured" + exception.toString(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public String getBatchDirectoryName() {
        Log.d("dtask", "getBatchDirectoryName: ############################"+ MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        String app_folder_path = "";
        //app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/pikkc";
        app_folder_path =  Environment.getExternalStorageDirectory().toString() + "/pikkc";
        //app_folder_path = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath().toString()+"/pikcc";
        Log.d("dtask", "getBatchDirectoryName APP PATH: ############################"+ app_folder_path);
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(getAct(), "Folder Cannot be created", Toast.LENGTH_SHORT).show();
        }
        return app_folder_path;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_main_butt, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                app_cam_cap_draw_lay.openDrawer(Gravity.LEFT);
                return true;
            case R.id.app_bar_flash_butt:{
                if(_APP_CAM_FLASH_FLAG==1){
                    Toast.makeText(this,"Flash Off", Toast.LENGTH_SHORT).show();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_off_outline));
                    _APP_CAM_FLASH_FLAG = 2;
                }
                else if(_APP_CAM_FLASH_FLAG==2){
                    Toast.makeText(this,"Flash On", Toast.LENGTH_SHORT).show();
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_flash_outline));
                    _APP_CAM_FLASH_FLAG = 1;
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public land_act getAct(){
        return this;
    }

    private boolean allPermissionsGranted(){

        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}