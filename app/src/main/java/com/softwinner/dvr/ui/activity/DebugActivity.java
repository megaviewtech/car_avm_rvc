package com.softwinner.dvr.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.megaview.avm.AVM;
import com.softwinner.dvr.R;

import static com.softwinner.dvr.ui.activity.RecordActivity.debugFlag;


public class DebugActivity extends BaseActivity implements  View.OnClickListener{
        private static String TAG = "RecordActivity";
    private ImageButton test = null;
    private ImageButton nextBtn = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_debug);
        //test = (ImageButton) findViewById(R.id.topViewBtn);
        nextBtn = (ImageButton) findViewById(R.id.btnNext);
        nextBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:

                //startActivity(new Intent(this, CornerActivity.class));
                this.finish();

                RecordActivity.debugFlag = 1;
                break;
        }
    }
}
