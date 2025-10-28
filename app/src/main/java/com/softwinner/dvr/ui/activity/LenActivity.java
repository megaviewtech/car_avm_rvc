package com.softwinner.dvr.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.megaview.avm.AVM;
import com.softwinner.dvr.R;
import com.softwinner.dvr.ui.fragment.CameraDrawer;
import com.softwinner.dvr.util.Logger;


import java.util.ArrayList;
import java.util.List;


public class LenActivity extends BaseActivity implements  View.OnClickListener{
        private static String TAG = "RecordActivity";
    private ImageButton debugBtn = null;
    private ImageButton autoDebugBtn = null;
    private List<String> list = new ArrayList<String>();
    private Spinner spinnertext;
    private ArrayAdapter<String> adapter;
    private EditText car_width,car_length;
    private ProgressDialog pd;
    int debugParams[];
    int debugSucessFlag;
    float imagespoints[];
    private static final int MSG_CORNER_SUCCESS = 100;
    private static final int MSG_DEBUG_SUCCESS = 101;

    float[] temp =  { 326.000000f,412.000000f,
            452.000000f,412.000000f,
            326.000000f,475.000000f,
            452.000000f,475.000000f,
            867.000000f,412.000000f,
            966.000000f,412.000000f,
            867.00000f,475.000000f,
            966.00000f,475.000000f,


            326.000000f,412.000000f,
            452.000000f,412.000000f,
            326.000000f,475.000000f,
            452.000000f,475.000000f,
            867.000000f,412.000000f,
            966.000000f,412.000000f,
            867.00000f,475.000000f,
            966.00000f,475.000000f,


            326.000000f,412.000000f,
            452.000000f,412.000000f,
            326.000000f,475.000000f,
            452.000000f,475.000000f,
            867.000000f,412.000000f,
            966.000000f,412.000000f,
            867.00000f,475.000000f,
            966.00000f,475.000000f,

            326.000000f,412.000000f,
            452.000000f,412.000000f,
            326.000000f,475.000000f,
            452.000000f,475.000000f,
            867.000000f,412.000000f,
            966.000000f,412.000000f,
            867.00000f,475.000000f,
            966.00000f,475.000000f,

            //for cloth2 chessboard
            560.000000f,412.000000f,
            720.000000f,412.000000f,
            560.00000f,475.000000f,
            720.00000f,475.000000f,

            560.000000f,412.000000f,
            720.000000f,412.000000f,
            560.00000f,475.000000f,
            720.00000f,475.000000f,


            560.000000f,412.000000f,
            720.000000f,412.000000f,
            560.00000f,475.000000f,
            720.00000f,475.000000f,


            560.000000f,412.000000f,
            720.000000f,412.000000f,
            560.00000f,475.000000f,
            720.00000f,475.000000f};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.add("6001");
        list.add("6007");



        setContentView(R.layout.activity_len);
        debugBtn = (ImageButton) findViewById(R.id.debugBtn);
        autoDebugBtn = (ImageButton) findViewById(R.id.autoDebugBtn);

        spinnertext = (Spinner)findViewById(R.id.spinner1);
        car_width = (EditText)findViewById(R.id.editWidth);
        car_length = (EditText)findViewById(R.id.editLength);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertext.setAdapter(adapter);

        spinnertext.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> argO, View argl, int arg2, long arg3) {
// TODO Auto-generated method stub
                /* 将所选spinnertext的值带入myTextView中*/
                //textview.setText("你的血型是:" + adapter.getItem(arg2));
                /* 将 spinnertext 显示^*/
                Log.i(TAG, "onItemSelected: "+adapter.getItem(arg2));
                argO.setVisibility(View.VISIBLE);
            }

            public void onNothingSelected(AdapterView<?> argO) {
// TODO Auto-generated method stub
                //textview.setText("NONE");
                argO.setVisibility(View.VISIBLE);
            }
        });


/*        spinnertext.setOnTouchListener(new Spinner.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
// TODO Auto-generated method stub
// 将mySpinner隐藏
                v.setVisibility(View.INVISIBLE);
                //Log.i("spinner", "Spinner Touch事件被触发!");
                return false;
            }
        });

        spinnertext.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
// TODO Auto-generated method stub
                v.setVisibility(View.VISIBLE);
                //Log.i("spinner", "Spinner FocusChange事件被触发！");
            }
        });*/


        //int carWidth = Integer.parseInt(car_width.getText().toString());
        //int carHight = Integer.parseInt(car_height.getText().toString());

        debugBtn.setOnClickListener(this);
        autoDebugBtn.setOnClickListener(this);
    }


    public int checkImagesPoints(float[] imagespoints)
    {
/*        float[] temp =  { 326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,


                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,


                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,

                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f};*/

        for(int i = 0;i<8 ;i++)
            if(imagespoints[i] == temp[i] && imagespoints[i+1] == temp[i+1] && imagespoints[i+2] == temp[i+2] && imagespoints[i+3] == temp[i+3])
                return 0;//some rect fail

        return 1;//all rect is ok.
    }

    @Override
    public void onClick(View v) {

/*        float[] temp =  { 326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,


                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,


                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f,

                326.000000f,412.000000f,
                452.000000f,412.000000f,
                326.000000f,475.000000f,
                452.000000f,475.000000f,
                867.000000f,412.000000f,
                966.000000f,412.000000f,
                867.00000f,475.000000f,
                966.00000f,475.000000f};*/

        switch (v.getId()) {
            case R.id.debugBtn:

                if(TextUtils.isEmpty(car_width.getText()) || TextUtils.isEmpty(car_length.getText())) {

                    Toast.makeText(getApplicationContext(), "输入不能为空！", Toast.LENGTH_LONG)
                            .show();
                }
                else {

                    String str1, str2;
                    str1 = car_width.getText().toString();
                    str2 = car_length.getText().toString();

                    int carWidth = Integer.parseInt(str1);
                    int carLength = Integer.parseInt(str2);

                    //if((carWidth > 150 && carWidth < 250) && (carLength > 400 && carLength < 700)) {
                        Intent intent = new Intent(this, CornerActivity.class);
                        intent.putExtra("carLength", carLength);
                        intent.putExtra("carWidth", carWidth);
                        if(imagespoints != null)
                            intent.putExtra("imagePoints", imagespoints);
                        else
                            intent.putExtra("imagePoints", temp);

                        Log.i(TAG, "carWidth1: " + carWidth + "，car Length2:" + carLength);
                        startActivity(intent);
/*                    }else{

                        Toast.makeText(getApplicationContext(), "输入值无效！", Toast.LENGTH_LONG)
                                .show();
                    }*/

                }
                break;

            case R.id.autoDebugBtn:
                Logger.i(TAG, "-------------debug,auto mode!");
                if(TextUtils.isEmpty(car_width.getText()) || TextUtils.isEmpty(car_length.getText())) {

                    Toast.makeText(getApplicationContext(), "输入不能为空！", Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    String str1, str2;
                    str1 = car_width.getText().toString();
                    str2 = car_length.getText().toString();

                    int carWidth = Integer.parseInt(str1);
                    int carLength = Integer.parseInt(str2);

                    if((carWidth > 150 && carWidth < 250) && (carLength > 400 && carLength < 700)) {

                        Log.i(TAG, "carWidth: " + carWidth + "，car Length:" + carLength);

                        pd= ProgressDialog.show(this, "自动拼接", "请等待…");
                        new Thread() {
                            @Override
                            public void run() {
                                Logger.i(TAG, "GetImagesPoints 1 ");

                                imagespoints = AVM.GetImagesPoints();
                                Logger.i(TAG, "GetImagesPoints 2 ");
                                //mHandler.sendEmptyMessage(MSG_CORNER_SUCCESS);

                                if(checkImagesPoints(imagespoints) == 1) {

                                    int b[] = new int[10];
                                    b[0] = carLength;//debugParams[0];
                                    b[1] = carWidth;//debugParams[1];
                                    b[2] = 57;
                                    b[3] = 85;
                                    b[4] = 0;
                                    debugSucessFlag = AVM.Debug(b, imagespoints);
                                    mHandler.sendEmptyMessage(MSG_DEBUG_SUCCESS);
                                }else {

                                    debugSucessFlag = 0;
                                    mHandler.sendEmptyMessage(MSG_DEBUG_SUCCESS);
                                }

                            }
                        }.start();

                    }else{
                        Logger.i(TAG, "-------------debug,input invalid!");
                        Toast.makeText(getApplicationContext(), "输入值无效！", Toast.LENGTH_LONG)
                                .show();
                    }


                }
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap;
            FrameLayout.LayoutParams btnLp;
            int offset;
            switch (msg.what) {
                case MSG_CORNER_SUCCESS:

                    pd.dismiss();

                    Toast.makeText(getApplicationContext(), "角点搜索完成！", Toast
                            .LENGTH_SHORT).show();
                    break;

                case MSG_DEBUG_SUCCESS:
                    pd.dismiss();
                    if(debugSucessFlag == 1)
                        Toast.makeText(getApplicationContext(), "拼接成功！", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getApplicationContext(), "拼接失败！", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


}
