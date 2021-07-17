package com.pikk.pikkc.Login;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.pikk.pikkc.R;
import com.pikk.pikkc.land.land_act;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class login_act extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_AUTHORIZE_DRIVE = 007;
    private Button app_log_gog_butt;
    public Button signInButton;
    Scope ACCESS_DRIVE_SCOPE = new Scope(DriveScopes.DRIVE);
    Scope SCOPE_EMAIL = new Scope(Scopes.EMAIL);
    public GoogleSignInClient mGoogleSignInClient;

    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final int REQUEST_CODE_PERMISSIONS = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_act);
        checkAndRequestPermissions();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(ACCESS_DRIVE_SCOPE)
                .build();

        mGoogleSignInClient  = GoogleSignIn.getClient(this, gso);
        _init_elements();
        _set_elements();
    }


    private  boolean checkAndRequestPermissions() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionExtStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
//        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.R){
//            if (!Environment.isExternalStorageManager()) {
//                Log.d("dtask","NO PERMISET");
//                //startActivity(ACTION);
//                listPermissionsNeeded.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
//
//            }
//        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_CODE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.d("dtask", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MANAGE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (
                                perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                         Log.d("dtask", "camera and storage granted");
                        Toast.makeText(getAct(),"All permissions granted", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("dtask", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }
//                        else if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.R){
//                                if(!Environment.isExternalStorageManager()){
//                                Log.d("dtask","EXTERNAL STORAGE PERMISSION");
//                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivity(intent);
//                                //Toast.makeText(this, "Go to settings and enable stroage permission", Toast.LENGTH_LONG).show();
//                            }
//                        }
                        else {

                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void checkForGooglePermissions() {
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getApplicationContext()), ACCESS_DRIVE_SCOPE, SCOPE_EMAIL))
            {
                    GoogleSignIn.requestPermissions(
                    login_act.this,
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

    private boolean _init_elements(){
        signInButton = findViewById(R.id.sign_in_button);
        app_log_gog_butt = findViewById(R.id.app_log_gog_butt);
        return false;
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
                    Log.d("dtask", "onActivityResult: LOGIN RESULT FAILED FOR DRIVE"+e.toString());
                });

                break;
            }
            case RC_AUTHORIZE_DRIVE:
            {
                Log.d("dtask", "onActivityResult: DRIVE AUTHO CODE"+resultCode);
                checkForGooglePermissions();
                break;
            }
            case 200:{

            }
            default:{
                Toast.makeText(getAct(),"Login Failed", Toast.LENGTH_SHORT).show();
                Log.d("dtask", "AUTH FAILED ");
                break;
            }
        }
    }


    private boolean _set_elements(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getAct());
        //signInButton.setSize(SignInButton.SIZE_WIDE);
        if(account!=null) { checkForGooglePermissions(); }
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions()){
                if(account!=null) {
                    checkForGooglePermissions();
                }
                else{
                    signIn();
                }
                }
            }
        });
        app_log_gog_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions()){
                startActivity(new Intent(getAct(), land_act.class));
                finish();
                }
            }
        });

        return false;
    }

    public login_act getAct(){
        return this;
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

