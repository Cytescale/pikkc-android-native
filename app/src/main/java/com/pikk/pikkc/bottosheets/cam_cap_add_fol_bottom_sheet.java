package com.pikk.pikkc.bottosheets;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.api.services.drive.model.File;
import com.pikk.pikkc.R;
import com.pikk.pikkc.land.land_act;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class cam_cap_add_fol_bottom_sheet extends BottomSheetDialogFragment {

    public land_act LAND_ACT;
    public EditText app_cam_bot_cal_edt_txt;
    public Button app_cam_bot_cal_sav_butt;


    public cam_cap_add_fol_bottom_sheet(land_act LAND_ACT){
        this.LAND_ACT = LAND_ACT;
    }

    public  cam_cap_add_fol_bottom_sheet newInstance(int itemCount) {
        final cam_cap_add_fol_bottom_sheet fragment = new cam_cap_add_fol_bottom_sheet(LAND_ACT);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cam_cap_add_fol_bottom_sheet_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageButton app_cam_bot_cal_butt_2 = view.findViewById(R.id.app_cam_bot_cal_butt_2);
        app_cam_bot_cal_edt_txt = view.findViewById(R.id.app_cam_bot_cal_edt_txt);
        app_cam_bot_cal_sav_butt = view.findViewById(R.id.app_cam_bot_cal_sav_butt);
        app_cam_bot_cal_sav_butt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                if(!app_cam_bot_cal_edt_txt.getText().toString().equals("")&&!app_cam_bot_cal_edt_txt.getText().toString().equals(" ")){
                File fileMetadata = new File();
                fileMetadata.setName(app_cam_bot_cal_edt_txt.getText().toString());
                fileMetadata.setMimeType("application/vnd.google-apps.folder");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            File file = LAND_ACT.googleDriveService.files().create(fileMetadata).setFields("id").execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("dtask", "onPostExecute: FOLDER CREATION ERROR"+e.toString());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Log.d("dtask", "onPostExecute: FOLDER CREATED");
                        Toast.makeText(LAND_ACT, "Folder Created", Toast.LENGTH_SHORT).show();
                        LAND_ACT.refreshFolderList();
                        dismiss();

                    }
                }.execute();
                }
                else{
                    Toast.makeText(LAND_ACT, "Enter folder name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        app_cam_bot_cal_butt_2.setOnClickListener(v -> dismiss());
    }

}