package com.pikk.pikkc.bottosheets;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;


import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.api.services.drive.model.File;
import com.pikk.pikkc.R;
import com.pikk.pikkc.land.land_act;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class cam_cap_bottom_sheet extends BottomSheetDialogFragment {

    public land_act LAND_ACT;
    public cam_cap_add_fol_bottom_sheet _CAM_ADD_FOL_BOT;
    public RecyclerView app_cam_bot_rec_list;
    public CamBottAdapt camBottAdapt;
    public ArrayList<com.google.api.services.drive.model.File>FOL_LIST;
    public cam_cap_bottom_sheet(land_act LAND_ACT, ArrayList<com.google.api.services.drive.model.File> list){
        this.LAND_ACT = LAND_ACT;
        this.FOL_LIST = list;
        _CAM_ADD_FOL_BOT = new cam_cap_add_fol_bottom_sheet(LAND_ACT);
        Log.d("dtask", "newInstance: FOLDER COUNT AT BOTTOM"+FOL_LIST.size());
    }

    // TODO: Customize parameters
    public cam_cap_bottom_sheet newInstance(int itemCount) {
        final cam_cap_bottom_sheet fragment = new cam_cap_bottom_sheet(LAND_ACT,FOL_LIST);
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
        return inflater.inflate(R.layout.fragment_cam_cap_bottom_sheet_list_dialog, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout app_cam_bot_new_butt = view.findViewById(R.id.app_cam_bot_new_butt);
        ImageButton app_cam_bot_cal_butt_1 = view.findViewById(R.id.app_cam_bot_cal_butt_1);
        app_cam_bot_rec_list = view.findViewById(R.id.app_cam_bot_rec_list);

        camBottAdapt = new CamBottAdapt(FOL_LIST,LAND_ACT);
        app_cam_bot_rec_list.setAdapter(camBottAdapt);
        app_cam_bot_rec_list.setLayoutManager(new LinearLayoutManager(getContext()));



//        if(LAND_ACT.selec_folder!=null){
//            Log.d("dtask", "onViewCreated: THE SELECD POSI"+LAND_ACT.selec_posi);;
//            RadioButton rad = app_cam_bot_rec_list.getChildAt(LAND_ACT.selec_posi).findViewById(R.id.app_cam_bot_rad);
//            rad.setChecked(true);
//        }


        app_cam_bot_cal_butt_1.setOnClickListener(v -> dismiss());
        app_cam_bot_new_butt.setOnClickListener(v -> {
            _CAM_ADD_FOL_BOT.show(LAND_ACT.getSupportFragmentManager(),"bott");
            dismiss();
        });



    }

    public void refreshList(){
        for(int i = 0 ; i < FOL_LIST.size() ; i++){
            Log.d("dtask", "refreshList: FOLDER NAME"+FOL_LIST.get(i).getName());
        }
        if(app_cam_bot_rec_list!=null){
                camBottAdapt.notifyDataSetChanged();
        }
    }
}


class CamBottAdapt extends RecyclerView.Adapter<CamBottAdapt.ViewHolder> {

    private List<com.google.api.services.drive.model.File> folderSet;
    private RadioButton lastCheckedRB = null;
    private int CheckedListInt;
    public  land_act LAND_ACT;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView choice_butt;
        private final RelativeLayout app_cam_bot_fold_lay;
        private final RadioButton app_cam_bot_rad ;


        public ViewHolder(View view) {
            super(view);
            choice_butt = view.findViewById(R.id.app_cam_bott_fold_butt);
            app_cam_bot_fold_lay = view.findViewById(R.id.app_cam_bot_fold_lay);
            app_cam_bot_rad = view.findViewById(R.id.app_cam_bot_rad);
        }
        public TextView getButton(){
            return choice_butt;
        }
        public RelativeLayout getLay(){
            return app_cam_bot_fold_lay;
        }
        public RadioButton getRad(){
            return app_cam_bot_rad;
        }

    }

    public CamBottAdapt(List<com.google.api.services.drive.model.File> dataSet,land_act LAND_ACT) {
        folderSet = dataSet;
        this.LAND_ACT = LAND_ACT;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.app_cam_bot_fold_butt_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            if(LAND_ACT.selec_folder!=null && LAND_ACT.selec_folder.getId().equals(folderSet.get(position).getId())){
                viewHolder.getRad().setChecked(true);
                lastCheckedRB =  viewHolder.getRad();
            }
        viewHolder.getButton().setText(folderSet.get(position).getName());
        viewHolder.getLay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LAND_ACT,"Save destination changed", Toast.LENGTH_SHORT).show();
                LAND_ACT.selec_folder = folderSet.get(position);
                LAND_ACT.selec_posi = position;
                LAND_ACT.app_cam_cap_dat_set_butt.setText(folderSet.get(position).getName());
                viewHolder.getRad().setChecked(true);

                RadioButton checked_rb = viewHolder.getRad();
                if (lastCheckedRB != null && lastCheckedRB != checked_rb) {
                    lastCheckedRB.setChecked(false);
                }
                lastCheckedRB = checked_rb;
                CheckedListInt = position;
                //RadioButton rad = v.findViewById(R.id.app_cam_bot_rad);
                //rad.setChecked(true);
            }
        });
        viewHolder.getRad().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return folderSet.size();
    }
}
