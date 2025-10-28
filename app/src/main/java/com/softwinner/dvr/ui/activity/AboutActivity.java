package com.softwinner.dvr.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.softwinner.dvr.R;


public class AboutActivity extends Activity implements  View.OnClickListener{
        private static String TAG = "AboutActivity";
    private ImageButton test = null;
    private ImageButton nextBtn = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        nextBtn = (ImageButton) findViewById(R.id.closeBtn);
        nextBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeBtn:

                this.finish();

                break;
        }
    }
}
