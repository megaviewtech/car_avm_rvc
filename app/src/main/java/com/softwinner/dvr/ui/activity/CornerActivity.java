package com.softwinner.dvr.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.megaview.avm.AVM;
import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.ui.fragment.CameraDrawer;
import com.softwinner.dvr.util.Logger;

import java.util.ArrayList;
import java.util.List;


public class CornerActivity extends BaseActivity implements  View.OnClickListener,View.OnTouchListener{
        private static String TAG = "RecordActivity";
        private int x=100,y=100;
    private ImageButton crossBtn1 = null;
    int curImg=0,curCorner=0;
    float imagespoints[];
    int debugParams[];
    int debugSucessFlag;
    int sourceFlag = AVM.imgSrcFlag;//1 sdcard
    int clothType = AVM.clothType;//0   1 for chessboard    2    3 2big+4chessboard   4 2big+2chessboard
    int srcWidth = AVM.screenWidth,srcHeight = AVM.screenHeight;

    private ImageButton crossBtn[];
    Bitmap bmp;
    private ProgressDialog pd;
    private ImageButton imgBtn1 = null;
    private ImageButton imgBtn2 = null;
    private ImageButton imgBtn3 = null;
    private ImageButton imgBtn4 = null;

    private ImageButton upBtn = null;
    private ImageButton downBtn = null;
    private ImageButton leftBtn = null;
    private ImageButton rightBtn = null;

    private ImageButton okBtn = null;
    private ImageButton detectBtn = null;
    private ImageButton retBtn = null;
    private ImageButton debugBtn = null;

    private ImageView imageView = null;
    private FrameLayout frameLayout = null;

    private static final int MSG_CORNER_SUCCESS = 100;
    private static final int MSG_DEBUG_SUCCESS = 101;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        debugParams = new int[10];
        Intent intent = getIntent();
        debugParams[0] = intent.getIntExtra("carLength", 523);
        debugParams[1] = intent.getIntExtra("carWidth", 196);

/*        if(AVM.resolutionIndex == 0) {
            srcWidth = 1024;
            srcHeight = 600;
        }
        else if(AVM.resolutionIndex == 1){
            srcWidth = 1280;
            srcHeight = 720;
        }*/

        switch(AVM.resolutionIndex){

            case 0:
                srcWidth = 1024;
                srcHeight = 600;
                break;

            case 1:
                srcWidth = 1280;
                srcHeight = 720;
                break;

            case 2:
                srcWidth = 1280;
                srcHeight = 480;
                break;

            case 3:
                srcWidth = 1280;
                srcHeight = 800;
                break;

            case 4:
                srcWidth = 1920;
                srcHeight = 720;
                break;

            case 5:
                srcWidth = 1920;
                srcHeight = 860;
                break;

            case 6:
                srcWidth = 1920;
                srcHeight = 1080;
                break;

            case 7:
                srcWidth = 2000;
                srcHeight = 1200;
                break;

            default:
                srcWidth = 1024;
                srcHeight = 600;
                break;
        }


        float[] temp = intent.getFloatArrayExtra("imagePoints");

        Logger.i(TAG, "----debugParams:"+debugParams[0]+" "+debugParams[1]);
        setContentView(R.layout.activity_corner);
        imagespoints = new float[64];

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
                966.00000f,475.000000f,};*/

        System.arraycopy(temp, 0,imagespoints , 0, 64);
        //imagespoints = AVM.GetImagesPoints();
        // for(int i=0;i<64;i++)
         //   Logger.i(TAG, "----points:"+imagespoints[i]);

        crossBtn=new ImageButton[8];
        //crossBtn1 = (ImageButton) findViewById(R.id.crossBtn1);
        crossBtn[0] = (ImageButton) findViewById(R.id.crossBtn0);
        crossBtn[1] = (ImageButton) findViewById(R.id.crossBtn1);
        crossBtn[2] = (ImageButton) findViewById(R.id.crossBtn2);
        crossBtn[3] = (ImageButton) findViewById(R.id.crossBtn3);
        crossBtn[4] = (ImageButton) findViewById(R.id.crossBtn4);
        crossBtn[5] = (ImageButton) findViewById(R.id.crossBtn5);
        crossBtn[6] = (ImageButton) findViewById(R.id.crossBtn6);
        crossBtn[7] = (ImageButton) findViewById(R.id.crossBtn7);

        imgBtn1 = (ImageButton) findViewById(R.id.imgBtn1);
        imgBtn2 = (ImageButton) findViewById(R.id.imgBtn2);
        imgBtn3 = (ImageButton) findViewById(R.id.imgBtn3);
        imgBtn4 = (ImageButton) findViewById(R.id.imgBtn4);

        upBtn = (ImageButton) findViewById(R.id.topBtn);
        downBtn = (ImageButton) findViewById(R.id.downBtn);
        leftBtn = (ImageButton) findViewById(R.id.leftBtn);
        rightBtn = (ImageButton) findViewById(R.id.rightBtn);

        okBtn = (ImageButton) findViewById(R.id.okBtn);
        detectBtn =(ImageButton) findViewById(R.id.detectBtn);
        debugBtn =(ImageButton) findViewById(R.id.debugBtn);
        retBtn =(ImageButton) findViewById(R.id.retBtn);

        imageView= (ImageView) findViewById(R.id.subPic);
        frameLayout = (FrameLayout) findViewById(R.id.cornerFL);
        //crossBtn1.setOnClickListener(this);
        crossBtn[0].setOnClickListener(this);
        crossBtn[1].setOnClickListener(this);
        crossBtn[2].setOnClickListener(this);
        crossBtn[3].setOnClickListener(this);
        crossBtn[4].setOnClickListener(this);
        crossBtn[5].setOnClickListener(this);
        crossBtn[6].setOnClickListener(this);
        crossBtn[7].setOnClickListener(this);



        imgBtn1.setOnClickListener(this);
        imgBtn2.setOnClickListener(this);
        imgBtn3.setOnClickListener(this);
        imgBtn4.setOnClickListener(this);

        upBtn.setOnClickListener(this);
        downBtn.setOnClickListener(this);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        detectBtn.setOnClickListener(this);
        debugBtn.setOnClickListener(this);
        retBtn.setOnClickListener(this);

        if(sourceFlag == 1)
            bmp = BitmapFactory.decodeFile("/sdcard/avm/1.bmp");
        else
            bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"1.bmp");

        curImg = 0;
        curCorner = 0;
        Bitmap bitmap;
        int offset;
        FrameLayout.LayoutParams btnLp;

        frameLayout.setBackground(new BitmapDrawable(bmp));


        for(int i=0;i<8;i++) {
            btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
            //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);

            if(i%2 == 0)
                offset = 36;
            else
                offset = 12;

            btnLp.setMargins((int) (srcWidth * imagespoints[i*2] / 1280.0) - offset, (int) (imagespoints[i*2+1] * srcHeight / 720.0) - 12, 0, 0);
            crossBtn[i].requestLayout();

        }
        bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[curImg*16+0*2]-25,(int)imagespoints[curImg*16+0*2+1]-25,51,51);
        imageView.setImageBitmap(bitmap);

        crossBtn[0].setImageResource(R.drawable.cross_r1);
        imgBtn1.setImageResource(R.drawable.top_selected);


        Logger.i(TAG, "----create out ");
    }


    @Override
    public void onClick(View v) {
        Bitmap bitmap;
        FrameLayout.LayoutParams btnLp;
        int offset;
        switch (v.getId()) {
            case R.id.crossBtn0:

                curCorner = 0;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();


                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_r1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);

                break;

            case R.id.crossBtn1:

                curCorner = 1;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();


                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);

                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_r2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
                break;
            case R.id.crossBtn2:

                curCorner = 2;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();


                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_r3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
                break;
            case R.id.crossBtn3:

                curCorner = 3;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();


                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_r4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
                break;
            case R.id.crossBtn4:

                curCorner = 4;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();



                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_r5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
               break;
            case R.id.crossBtn5:

                curCorner = 5;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();



                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_r6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
                break;
            case R.id.crossBtn6:

                curCorner = 6;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();



                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_r7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);
               break;
            case R.id.crossBtn7:

                curCorner = 7;
                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();



                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_g1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_r8);
                break;


            case R.id.imgBtn1:
                curImg = 0;
                curCorner = 0;

                if(sourceFlag == 1)
                    bmp = BitmapFactory.decodeFile("/sdcard/avm/1.bmp");
                else
                    bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"1.bmp");
                frameLayout.setBackground(new BitmapDrawable(bmp));

                for(int i=0;i<8;i++) {
                    btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
                    //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);

                    if(i%2 == 0)
                        offset = 36;
                    else
                        offset = 12;

                    btnLp.setMargins((int) (srcWidth * imagespoints[i*2] / 1280.0) - offset, (int) (imagespoints[i*2+1] * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[i].requestLayout();

                }

                //bitmap = Bitmap.createBitmap(bmp,x-25,y-25,50,50);
                bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[0*2]-25,(int)imagespoints[0*2+1]-25,51,51);
                imageView.setImageBitmap(bitmap);

                crossBtn[0].setImageResource(R.drawable.cross_r1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);

                imgBtn1.setImageResource(R.drawable.top_selected);
                imgBtn2.setImageResource(R.drawable.bottom_gray);
                imgBtn3.setImageResource(R.drawable.left_gray);
                imgBtn4.setImageResource(R.drawable.right_gray);
                break;
            case R.id.imgBtn2:
                curImg = 1;
                curCorner = 0;
                if(sourceFlag == 1)
                    bmp = BitmapFactory.decodeFile("/sdcard/avm/2.bmp");
                else
                    bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"2.bmp");
                frameLayout.setBackground(new BitmapDrawable(bmp));

                for(int i=0;i<8;i++) {

                    if(i%2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
                    //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    btnLp.setMargins((int) (srcWidth * imagespoints[curImg*16+i*2] / 1280.0) - offset, (int) (imagespoints[curImg*16+i*2+1] * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[i].requestLayout();

                }

                //bitmap = Bitmap.createBitmap(bmp,x-25,y-25,50,50);
                bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[curImg*16+0*2]-25,(int)imagespoints[curImg*16+0*2+1]-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_r1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);

                imgBtn1.setImageResource(R.drawable.top_gray);
                imgBtn2.setImageResource(R.drawable.bottom_selected);
                imgBtn3.setImageResource(R.drawable.left_gray);
                imgBtn4.setImageResource(R.drawable.right_gray);
                break;
            case R.id.imgBtn3:
                curImg = 2;
                curCorner = 0;
                if(sourceFlag == 1)
                    bmp = BitmapFactory.decodeFile("/sdcard/avm/3.bmp");
                else
                    bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"3.bmp");
                frameLayout.setBackground(new BitmapDrawable(bmp));

                for(int i=0;i<8;i++) {

                    if(i%2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
                    //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    btnLp.setMargins((int) (srcWidth * imagespoints[curImg*16+i*2] / 1280.0) - offset, (int) (imagespoints[curImg*16+i*2+1] * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[i].requestLayout();

                }

                //bitmap = Bitmap.createBitmap(bmp,x-25,y-25,50,50);
                bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[curImg*16+0*2]-25,(int)imagespoints[curImg*16+0*2+1]-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_r1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);

                imgBtn1.setImageResource(R.drawable.top_gray);
                imgBtn2.setImageResource(R.drawable.bottom_gray);
                imgBtn3.setImageResource(R.drawable.left_selected);
                imgBtn4.setImageResource(R.drawable.right_gray);
                break;
            case R.id.imgBtn4:
                curImg = 3;
                curCorner = 0;
                if(sourceFlag == 1)
                    bmp = BitmapFactory.decodeFile("/sdcard/avm/4.bmp");
                else
                    bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"4.bmp");
                frameLayout.setBackground(new BitmapDrawable(bmp));

                for(int i=0;i<8;i++) {

                    if(i%2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
                    //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    btnLp.setMargins((int) (srcWidth * imagespoints[curImg*16+i*2] / 1280.0) - offset, (int) (imagespoints[curImg*16+i*2+1] * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[i].requestLayout();

                }

                //bitmap = Bitmap.createBitmap(bmp,x-25,y-25,50,50);
                bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[curImg*16+0*2]-25,(int)imagespoints[curImg*16+0*2+1]-25,51,51);
                imageView.setImageBitmap(bitmap);
                crossBtn[0].setImageResource(R.drawable.cross_r1);
                crossBtn[1].setImageResource(R.drawable.cross_g2);
                crossBtn[2].setImageResource(R.drawable.cross_g3);
                crossBtn[3].setImageResource(R.drawable.cross_g4);
                crossBtn[4].setImageResource(R.drawable.cross_g5);
                crossBtn[5].setImageResource(R.drawable.cross_g6);
                crossBtn[6].setImageResource(R.drawable.cross_g7);
                crossBtn[7].setImageResource(R.drawable.cross_g8);

                imgBtn1.setImageResource(R.drawable.top_gray);
                imgBtn2.setImageResource(R.drawable.bottom_gray);
                imgBtn3.setImageResource(R.drawable.left_gray);
                imgBtn4.setImageResource(R.drawable.right_selected);
                break;

            case R.id.topBtn:

                if((imagespoints[curImg * 16 + curCorner * 2 + 1] - 1.0)>26.0) {
                    x = (int) imagespoints[curImg * 16 + curCorner * 2];

                    imagespoints[curImg * 16 + curCorner * 2 + 1] -= 1.0;
                    y = (int) imagespoints[curImg * 16 + curCorner * 2 + 1];


                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                //Log.i(TAG, "----createBitmap2: ");

                break;

            case R.id.downBtn:
                if((imagespoints[curImg * 16 + curCorner * 2 + 1] + 1.0)<(720.0-26.0)) {
                    x = (int) imagespoints[curImg * 16 + curCorner * 2];

                    imagespoints[curImg * 16 + curCorner * 2 + 1] += 1.0;
                    y = (int) imagespoints[curImg * 16 + curCorner * 2 + 1];

                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;


                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                break;

            case R.id.leftBtn:
                if((imagespoints[curImg * 16 + curCorner * 2] - 1.0)>26.0) {
                    imagespoints[curImg * 16 + curCorner * 2] -= 1.0;
                    x = (int) imagespoints[curImg * 16 + curCorner * 2];
                    y = (int) imagespoints[curImg * 16 + curCorner * 2 + 1];


                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                break;

            case R.id.rightBtn:
                if((imagespoints[curImg * 16 + curCorner * 2] +1.0)<(1280.0-26.0)) {
                    imagespoints[curImg * 16 + curCorner * 2] += 1.0;
                    x = (int) imagespoints[curImg * 16 + curCorner * 2];

                    y = (int) imagespoints[curImg * 16 + curCorner * 2 + 1];


                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                break;

            case R.id.okBtn:
                curCorner+=1;
                curCorner = curCorner%8;


                x =(int)imagespoints[curImg*16+curCorner*2];

                y = (int)imagespoints[curImg*16+curCorner*2+1];


                if(curCorner%2==0)
                    offset=36;
                else
                    offset=12;
                btnLp = (FrameLayout.LayoutParams)crossBtn[curCorner].getLayoutParams();
                btnLp.setMargins((int)(srcWidth*x/1280.0)-offset, (int)(y*srcHeight/720.0)-12, 0, 0);
                crossBtn[curCorner].requestLayout();


               // Log.i(TAG, "----onClick: ok:"+x+" "+y);

                bitmap = Bitmap.createBitmap(bmp,x-25,y-25,51,51);
                imageView.setImageBitmap(bitmap);

                switch (curCorner) {
                    case 0:
                    crossBtn[0].setImageResource(R.drawable.cross_r1);
                    crossBtn[1].setImageResource(R.drawable.cross_g2);
                    crossBtn[2].setImageResource(R.drawable.cross_g3);
                    crossBtn[3].setImageResource(R.drawable.cross_g4);
                    crossBtn[4].setImageResource(R.drawable.cross_g5);
                    crossBtn[5].setImageResource(R.drawable.cross_g6);
                    crossBtn[6].setImageResource(R.drawable.cross_g7);
                    crossBtn[7].setImageResource(R.drawable.cross_g8);
                    break;
                    case 1:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_r2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 2:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_r3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 3:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_r4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 4:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_r5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 5:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_r6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 6:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_r7);
                        crossBtn[7].setImageResource(R.drawable.cross_g8);
                        break;
                    case 7:
                        crossBtn[0].setImageResource(R.drawable.cross_g1);
                        crossBtn[1].setImageResource(R.drawable.cross_g2);
                        crossBtn[2].setImageResource(R.drawable.cross_g3);
                        crossBtn[3].setImageResource(R.drawable.cross_g4);
                        crossBtn[4].setImageResource(R.drawable.cross_g5);
                        crossBtn[5].setImageResource(R.drawable.cross_g6);
                        crossBtn[6].setImageResource(R.drawable.cross_g7);
                        crossBtn[7].setImageResource(R.drawable.cross_r8);
                        break;
                }
                break;

            case R.id.detectBtn:
                pd= ProgressDialog.show(this, "自动找点", "请等待…");
                new Thread() {
                    @Override
                    public void run() {
                        Logger.i(TAG, "GetImagesPoints 1 ");

                        AVM.avmSetCmd(62,clothType);//set cloth type,1 chessboard cloth
                        imagespoints = AVM.GetImagesPoints();

                        Logger.i(TAG, "GetImagesPoints 2 ");
                        mHandler.sendEmptyMessage(MSG_CORNER_SUCCESS);

                    }
                }.start();
                break;

            case R.id.retBtn:
                this.finish();
                break;

            case R.id.debugBtn:

                pd= ProgressDialog.show(this, "拼接调试", "请等待…");
                new Thread() {
                    @Override
                    public void run() {
                        Logger.i(TAG, "GetImagesPoints 1 ");

                        int b[]=new int[10];
                        b[0]=debugParams[0];
                        b[1]=debugParams[1];
                        b[2]=57;//front camera height
                        b[3]=85;//rear camera height
                        b[4]=clothType;//cloth type,1 chessboard cloth
                        b[5]=140;//fix 140cm
                        b[6]=440;
                        b[7]=0;
                        b[8]=0;//ts18 flag

                        debugSucessFlag = AVM.Debug(b,imagespoints);
                        if(debugSucessFlag == 0) {
                            int failIndex;
                            failIndex = AVM.avmSetCmd(92, 0);
                            Log.i(TAG, "Debug fail:"+failIndex);

                        }

                        mHandler.sendEmptyMessage(MSG_DEBUG_SUCCESS);

                    }
                }.start();


                break;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i(TAG, "onTouch ");

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:// 获取手指第一次接触屏幕
                int sx = (int) motionEvent.getRawX();
                int sy = (int) motionEvent.getRawY();


                break;
            case MotionEvent.ACTION_MOVE:// 手指在屏幕上移动对应的事件
                int x1 = (int) motionEvent.getRawX();
                int y1 = (int) motionEvent.getRawY();
               // Log.i(TAG, "onTouch:ACTION_MOVE "+x+" "+y);

                break;
            case MotionEvent.ACTION_UP:// 手指离开屏幕对应事件
                //Log.i(TAG, "onTouch:ACTION_UP ");
                break;
        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Bitmap bitmap;
        FrameLayout.LayoutParams btnLp;
        int offset;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                int sx = (int) event.getRawX();
                int sy = (int) event.getRawY();

                //Log.i(TAG, "onTouchEvent:ACTION_DOWN "+sx+" "+sy);
                if((sx > 22*(srcWidth/1024.0) && sx < (srcWidth-22*(srcWidth/1024.0))) && (sy > 220*(srcHeight/600.0) && sy < (srcHeight-90*(srcHeight/600.0)))) {
                    imagespoints[curImg * 16 + curCorner * 2] = (float) (sx * 1280.0 / srcWidth);
                    imagespoints[curImg * 16 + curCorner * 2 + 1] = (float) (sy * 720.0 / srcHeight);

                    x = (int) (sx * 1280.0 / srcWidth);
                    y = (int) (sy * 720.0 / srcHeight);
                    // Log.i(TAG, "onTouch:ACTION_DOWN "+sx+" "+sy);
                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                int x1 = (int) event.getRawX();
                int y1 = (int) event.getRawY();
                //Log.i(TAG, "onTouchEvent:ACTION_MOVE "+x+" "+y);
                if((x1 > 22*(srcWidth/1024.0) && x1 < (srcWidth-22*(srcWidth/1024.0))) && (y1 > 220*(srcHeight/600.0) && y1 < (srcHeight-90*(srcHeight/600.0)))) {
                    imagespoints[curImg * 16 + curCorner * 2] = (float) (x1 * 1280.0 / srcWidth);
                    imagespoints[curImg * 16 + curCorner * 2 + 1] = (float) (y1 * 720.0 / srcHeight);
                    x = (int) (x1 * 1280.0 / srcWidth);
                    y = (int) (y1 * 720.0 / srcHeight);
                    if (curCorner % 2 == 0)
                        offset = 36;
                    else
                        offset = 12;
                    btnLp = (FrameLayout.LayoutParams) crossBtn[curCorner].getLayoutParams();
                    btnLp.setMargins((int) (srcWidth * x / 1280.0) - offset, (int) (y * srcHeight / 720.0) - 12, 0, 0);
                    crossBtn[curCorner].requestLayout();



                    bitmap = Bitmap.createBitmap(bmp, x - 25, y - 25, 51, 51);
                    imageView.setImageBitmap(bitmap);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent:ACTION_UP ");
                break;
        }


        return super.onTouchEvent(event);
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
                    Logger.i(TAG, "detect corner success");
                    curImg = 0;
                    curCorner = 0;
                    if(sourceFlag == 1)
                        bmp = BitmapFactory.decodeFile("/sdcard/avm/1.bmp");
                    else
                        bmp = BitmapFactory.decodeFile(CameraDrawer.rootPath+"1.bmp");
                    frameLayout.setBackground(new BitmapDrawable(bmp));

                    for(int i=0;i<8;i++) {
                        btnLp = (FrameLayout.LayoutParams) crossBtn[i].getLayoutParams();
                        //btnLp.setMargins((int) (srcWidth * x / 1280.0) - 12, (int) (y * srcHeight / 720.0) - 12, 0, 0);

                        if(i%2 == 0)
                            offset = 36;
                        else
                            offset = 12;

                        btnLp.setMargins((int) (srcWidth * imagespoints[i*2] / 1280.0) - offset, (int) (imagespoints[i*2+1] * srcHeight / 720.0) - 12, 0, 0);
                        crossBtn[i].requestLayout();

                    }

                    //bitmap = Bitmap.createBitmap(bmp,x-25,y-25,50,50);
                    bitmap = Bitmap.createBitmap(bmp,(int)imagespoints[0*2]-25,(int)imagespoints[0*2+1]-25,51,51);
                    imageView.setImageBitmap(bitmap);

                    crossBtn[0].setImageResource(R.drawable.cross_r1);
                    crossBtn[1].setImageResource(R.drawable.cross_g2);
                    crossBtn[2].setImageResource(R.drawable.cross_g3);
                    crossBtn[3].setImageResource(R.drawable.cross_g4);
                    crossBtn[4].setImageResource(R.drawable.cross_g5);
                    crossBtn[5].setImageResource(R.drawable.cross_g6);
                    crossBtn[6].setImageResource(R.drawable.cross_g7);
                    crossBtn[7].setImageResource(R.drawable.cross_g8);

                    imgBtn1.setImageResource(R.drawable.top_selected);
                    imgBtn2.setImageResource(R.drawable.bottom_gray);
                    imgBtn3.setImageResource(R.drawable.left_gray);
                    imgBtn4.setImageResource(R.drawable.right_gray);
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
