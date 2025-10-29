
package com.softwinner.dvr.ui.fragment;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.megaview.avm.AVM;
import com.megaview.ghost.Ghost;
import com.openadas.ai.Ai;
import com.softwinner.dvr.camera.BaseCamera;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import android.os.SystemProperties;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.support.constraint.Constraints.TAG;
import static com.megaview.avm.AVM.CMD_SET_DISPLAY_MODE;
import static com.megaview.avm.AVM.PREVIEW_SIZE_1280_720;
import static com.megaview.avm.AVM.PREVIEW_SIZE_1280_720;
import static java.lang.Thread.sleep;

/**
 * Description:
 */
public class CameraDrawer implements GLSurfaceView.Renderer,AVM.OnSignalListener {

    public static String rootPath;
    public  static int assets_flag;
    public static SurfaceTexture surfaceTexture;
    private int texture =0;

    //
    public static SurfaceTexture surfaceTexture6_2;
    private int texture6_2 =0;

    public AssetManager mManager;
    public AVM avm;
    Context mContext;
    int lumaFlag = 0;
    boolean LumaOn = true;//

    static int skipCount=50;
    int angle = 0;

    public void onSignal(int msg, int param1, int param2, int param3)
    {
        Log.d(TAG, "onSignal: "+msg+param1+param2+param3);
    }



    public CameraDrawer(Resources res,Context context){

            mManager = res.getAssets();
            avm = new AVM(res);
            mContext = context;
            //AVM.setOnSignalListener(this);
    }




    public SurfaceTexture getSurfaceTexture(){
        return surfaceTexture;
    }


    public SurfaceTexture getSurfaceTexture6_2(){
        return surfaceTexture6_2;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        int regions[];
        int regions2[] = new int[32+1];//regions2[0] cmd value,regions2[1] to regions2[32] for 8 regions
        regions2[32]=100;//1:set regions cmd
        texture = createTextureID();
        surfaceTexture=new SurfaceTexture(texture);

        //
        if(Ghost.sixChannelCameraFlag == 1){

            texture6_2 = createTextureID();
            surfaceTexture6_2=new SurfaceTexture(texture6_2);
        }


        int ret = 0;
        if(rootPath == null)
            Log.d(TAG, "onSurfaceCreated: MEGAVIEW avmInit rootPath = null");

        //Log.i(TAG, "----GL_EXTENSIONS:"+GLES20.glGetString(GLES20.GL_EXTENSIONS));

        if(AVM.demoFlag == 1)
            AVM.avmSetCmd(AVM.CMD_DEMO_MODE,AVM.demoFlag);        //AVM.avmSetCmd(AVM.CMD_SET_SCREEN_RESOLUTION,AVM.resolutionIndex);

        if(AVM.singleCameraFlag == 1)
            AVM.avmSetCmd(AVM.PRE_CMD_SET_CAMERA_TYPE,3);


        AVM.avmSetCmd(AVM.CMD_SET_SCREEN_SIZE,(AVM.screenWidth<<16)+AVM.screenHeight);
        AVM.avmSetCmd(AVM.CMD_PRE_SET_CAMERA_SIZE,(AVM.cameraWidth<<16)+AVM.cameraHeight);
        AVM.avmSetCmd(AVM.CMD_PRE_SET_IMG_SOURCE,AVM.imgSrcFlag);

        AVM.avmSetCmd(AVM.CMD_PRE_SET_2D_VIEW_WIDTH,352);

        //AVM.avmSetCmd(AVM.CMD_PRE_SET_MODLE_K_STYLE,1);
        AVM.avmSetCmd(AVM.CMD_PRE_SET_MODLE_VERTEX_RATIO,2);
        //AVM.avmSetCmd(AVM.CMD_PRE_SET_NETWORK_FUN,1);
        //AVM.avmSetCmd(AVM.CMD_PRE_SET_BACKVIEW_MIRROR,1);
        //AVM.avmSetCmd(2008,1);

        AVM.avmInit(rootPath,assets_flag);
        //AVM.avmSetCmd(137,1);
        //AVM.avmSetCmd(AVM.CMD_SET_DISPLAY_MODE,1);
        //AVM.avmSetCmd(134,1);
        //AVM.avmSetCmd(135,1);

        //
        //AVM.avmSetCmd(AVM.CMD_DEBUG_OPT,1);


        //AVM.avmSetCmd(AVM.CMD_SET_MOVE,1);

        //AVM.avmSetCmd(61,50);

        //AVM.avmSetCmd(64,100);

        //AVM.avmSetCmd(AVM.CMD_SET_PLATFORM_ID, BaseCamera.T7);
        //DVRApplication.getInstance().setAvmFlag(true);

/*        int carIndex = DVRPreference.getInstance(mContext).getCarIndex();
        int colorIndex = DVRPreference.getInstance(mContext).getColorIndex();
        AVM.avmSetCmd(8,carIndex);
        AVM.avmSetCmd(9,colorIndex);*/

        //String carName="bentley_mulsanne_2011";

        String carNameArray[]={"lamborghini_gallardo_2018","audi_a4_2016","nissan_xtrail_2017"};

        if(assets_flag == 1) {

            for(int i=0;i<1;i++) {

                ret = AVM.SetCar(carNameArray[i] + "_color00" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);
                ret = AVM.SetCar(carNameArray[i] + "_color01" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);
                ret = AVM.SetCar(carNameArray[i] + "_color02" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);
                ret = AVM.SetCar(carNameArray[i] + "_color03" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);
                ret = AVM.SetCar(carNameArray[i] + "_color04" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);
                ret = AVM.SetCar(carNameArray[i] + "_color05" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);

/*                ret = AVM.SetCar(carNameArray[i] + "_color06" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);

                ret = AVM.SetCar(carNameArray[i] + "_color07" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);

                ret = AVM.SetCar(carNameArray[i] + "_color08" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);

                ret = AVM.SetCar(carNameArray[i] + "_color09" + ".obj", 1);
                Log.i(TAG, "SetCar: "+ret);*/
            }
        }

        AVM.avmSetCmd(AVM.CMD_LOAD_FLAG,1);


        //AVM.GenPlate("\u001e"+"BW1111",2);




        //AVM.GenPlate(18,"C"+"W287G",1);

        AVM.avmSetCmd(AVM.CMD_SET_PLATE,1);
        //AVM.avmSetCmd(AVM.CMD_SET_TEST,1);
        AVM.avmSetCmd(AVM.CMD_TRACK_TYPE,1);

        //AVM.avmSetCmd(AVM.CMD_SET_PLATFORM_ID,4);//ts18





        //AVM.avmSetCmd(63,1);




/*        AVM.avmSetCmd(AVM.CMD_RADAR_TYPE,0);
        int l=2,cl=3,cr=1,r=2;
        int frontRadar = (l&0x0f) | ((cl&0x0f) << 4) | ((cr&0x0f) << 8) | ((r&0x0f) << 12) | 0x00000;
        AVM.avmSetCmd(AVM.CMD_RADAR,frontRadar);*/

       AVM.avmSetCmd(AVM.CMD_RADAR_TYPE,1);

       int l=80,cl=30,cr=80,r=150;
        int frontRadar = (l&0xff) | ((cl&0xff) << 8) | ((cr&0xff) << 16) | ((r&0xff) << 24);
        //AVM.avmSetCmd(AVM.CMD_RADAR_FRONT,frontRadar);
        //AVM.avmSetCmd(AVM.CMD_RADAR_BACK,frontRadar);
        //frontRadar = (l&0x0f) | ((cl&0x0f) << 4) | ((cr&0x0f) << 8) | ((r&0x0f) << 12) | 0x00000;
        //Log.d(TAG, "-----frontRadar: "+frontRadar);
        //AVM.avmSetCmd(AVM.CMD_RADAR,frontRadar);

        //AVM.avmSetCmd(3,20);


/*        for(int i=0;i<20;i++) {

            int re;
            re = AVM.avmSetCmd(20, -1);
            Log.d(TAG, "-----2d scale: "+re);
        }*/

        //get rect from jni and set to HAL




/*        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);
        AVM.avmSetCmd(70,-1);*/


        //AVM.avmSetCmd(AVM.CMD_VIEW,AVM.VIEW_3D_ALL);
        //AVM.avmSetCmd(60,0);

        //version1
        //AVM.avmSetCmd(AVM.CMD_3D_ANGLE,45*4);
        //AVM.angle = 45*4;

       //version2
/*        AVM.avmSetCmd(AVM.CMD_3D_ANGLE,45);
        AVM.angle = 45;

        AVM.rotateFlag = 1;*/

        //android.os.Process.setThreadPriority (-20);

        AVM.avmSetCmd(AVM.CMD_SET_CAR_BOTTOM,1);
        //AVM.avmSetCmd(AVM.CMD_LR_TYPE,0);//0:   1:
        AVM.avmSetCmd(AVM.CMD_ROTATE_STYLE,AVM.rotate_style);



        int pressureValue[] = new int[24];

        //胎压值，有效范围:0,999
        pressureValue[0] = 130;
        pressureValue[1] = 220;
        pressureValue[2] = 120;
        pressureValue[3] = 220;

        //温度值,有效范围：-99,99
        pressureValue[4+0] = 45;
        pressureValue[4+1] = 32;
        pressureValue[4+2] = 40;
        pressureValue[4+3] = 30;

        //胎压状态
        for(int i=0;i<4;i++) {
            if (pressureValue[i] < 170)//具体报警下限应用决定
                pressureValue[8 + i] = 1;//胎压低
            else if(pressureValue[i] > 280)//具体报警上限应用决定
                pressureValue[8 + i] = 2;//胎压高
            else
                pressureValue[8 + i] = 0;//胎压正常
        }

        //漏气状态
        pressureValue[12+0] = 0;
        pressureValue[12+1] = 0;
        pressureValue[12+2] = 0;
        pressureValue[12+3] = 0;

        //温度状态
        for(int i=0;i<4;i++) {
            if (pressureValue[4+i] > 60)//具体温度报警上限应用决定
                pressureValue[16+i]  = 1;//温度高
            else
                pressureValue[16+i]  = 0;//温度正常

        }



        //电量状态
        pressureValue[20+0] = 0;
        pressureValue[20+1] = 0;
        pressureValue[20+2] = 0;
        pressureValue[20+3] = 0;


        //AVM.SetTirePressure(pressureValue);

        //AVM.avmSetCmd(AVM.CMD_DISPLAY_TIRE_PRESSURE,1);//
        //AVM.avmSetCmd(AVM.CMD_DISPLAY_TIRE_OK,1);//


        //if(AVM.luma_on == 1) {
            regions = AVM.GetLumaRect();
/*            for (int i = 0; i < 8; i++)
                if ((regions[i * 4] + regions[i * 4 + 2]) >= 1280 || (regions[i * 4 + 1] + regions[i * 4 + 3] >= 720)) {
                    lumaFlag = 0;
                    break;
                }*/

            //Log.d(TAG, "------------region:"+regions[0]+","+regions[1]+","+regions[2]+","+regions[3]);

            System.arraycopy(regions, 0, regions2, 0, 32);


            if(AVM.camera_1920_flag == 1)
            {
                //1920*1080
                for(int k=0;k<8;k++) {
                    regions2[k*4+0] = (int) (regions2[k*4+0] * (1920.0 / 1280.0));
                    regions2[k*4+1] = (int) (regions2[k*4+1] * (1920.0 / 1280.0));
                    regions2[k*4+2] = (int) (regions2[k*4+2] * (1920.0 / 1280.0));
                    regions2[k*4+3] = (int) (regions2[k*4+3] * (1920.0 / 1280.0));
                }

/*                //960*1080
                for(int k=0;k<8;k++) {
                    regions2[k*4+0] = (int) (regions2[k*4+0] * (960.0 / 1280.0));
                    regions2[k*4+1] = (int) (regions2[k*4+1] * (1920.0 / 1280.0));
                    regions2[k*4+2] = (int) (regions2[k*4+2] * (960.0 / 1280.0));
                    regions2[k*4+3] = (int) (regions2[k*4+3] * (1920.0 / 1280.0));
                }*/

            }

            //
            for(int k=0;k<8;k++) {
                regions2[k * 4 + 2] = (regions2[k * 4 + 2] / 10) * 10;
                regions2[k * 4 + 3] = (regions2[k * 4 + 3] / 10) * 10;
            }

            if(DVRApplication.getInstance().getDVR().getFrontCamera() != null) {
                ret = DVRApplication.getInstance().getDVR().getFrontCamera().SetRegions(regions2);
                if (ret == -1) {
                    AVM.luma_on = 0;
                    Log.d(TAG, "------------SetRegions not support!");
                }
            }else{
                Log.d(TAG, "------------getFrontCamera null!");
                AVM.luma_on = 0;
            }

            //com.example.nativesurface.AVMCam.setRegions(regions2);
            Log.d(TAG, "------------setRegions AVMCam!");
       // }

        AVM.avmSetCmd(AVM.CMD_SET_FRONT_TRACK,1);
        AVM.avmSetCmd(AVM.CMD_SET_REAR_TRACK,1);

        //AVM.avmSetCmd(AVM.CMD_NARROW_MODE,1);

        //AVM.avmSetCmd(64,99);

        AVM.avmSetCmd(AVM.CMD_H_2D_LEFT,AVM.h_2d_left_flag);
        //AVM.avmSetCmd(AVM.CMD_2D_PART_DISPLAY,1);
        //AVM.avmSetCmd(AVM.CMD_VSCREEN_3D_2D,0);

        AVM.avmSetCmd(AVM.CMD_BACKVIEW_MIRROR,1);


        ret = AVM.avmSetCmd(AVM.CMD_GET_VERSION,0);
        Log.d(TAG, "-----CMD_GET_VERSION:"+ ret);


        //AVM.avmSetCmd(AVM.CMD_TURN_LAMP,1);


        AVM.avmSetCmd(AVM.CMD_SET_SAFE_LINE,1);
        //AVM.avmSetCmd(91,0);
        AVM.avmSetCmd(AVM.CMD_TRACK_SAFELINE,1);
        int doorStatus = 0x01; //车门全开
        //低4位为车门状态[ 0 0 0 0   x    x    x    x ]
        //                          右后  左后 右前 左前         0 车门关闭，1 车门打开
        //AVM.avmSetCmd(AVM.CMD_DOOR,doorStatus);

        Log.d(TAG, "SetCar: in");
        ret = AVM.SetCar(carNameArray[0]+"_color01",8);
        Log.d(TAG, "SetCar: out");

        if(AVM.customLenParamFlag == 1)
            AVM.LoadCameraParams("8255.txt");
        //SystemProperties.set("persisit.sys.ParamArray","100,100,100,100,100,100,100,100");

        if(AVM.rectCornerType == 1)
            AVM.avmSetCmd(AVM.CMD_CORNER_TYPE,AVM.rectCornerType);

        if(AVM.chessboardType == 1)
            AVM.avmSetCmd(AVM.CMD_CHESSBOARD_TYPE,AVM.chessboardType);

        if(AVM.carBottomViewFlag == 1) {
            AVM.avmSetCmd(AVM.CMD_BOTTOM_VIEW_ON,AVM.carBottomViewFlag);
            AVM.avmSetCmd(AVM.CMD_CAR_ALPHA_VALUE,50);
        }

        AVM.avmSetCmd(AVM.CMD_SPEED,30000);
        AVM.avmSetCmd(AVM.CMD_TIRE_ANGLE,0);//-35  0  35
        //AVM.avmSetCmd(AVM.CMD_REVERSE,1);
        //AVM.SetCmd(AVM.CMD_VIEW,1);

        AVM.SetCmd(AVM.CMD_SET_INNER_LUMA,1);

        AVM.SetCmd(109,1);

        AVM.SetCmd(129,1);
        //AVM.SetCmd(130,1);
        AVM.SetCmd(131,1);

        //AVM.SetCmd(117,1);
        //AVM.SetCmd(133,1);

        if(Ghost.sixChannelCameraFlag == 1){

            AVM.SetCmd(AVM.CMD_SET_SIX_CHANNEL_CAMERA,1);
        }




        //String version = GLES20.glGetString(GLES20.GL_VERSION);
        //Log.d("OpenGL ES Version", version);
        //Log.d(TAG, "OpenGL ES Version"+version);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.d(TAG, "-----onSurfaceChanged: "+width+"  "+height);
        //AVM.avmSetCmd(AVM.CMD_SET_SCREE_RESOLUTION,2);
    }

    @Override
    public void onDrawFrame(GL10 gl) {


        int[] array={1,1,1,1};
        int[] array2={-1,-1,-1,-1};

        array[0]=texture;

        //Log.d(TAG, "-----test,onDrawFrame: 1");

        if(surfaceTexture!=null){
            //Log.d(TAG, "-----test,updateTexImage: 1");
            surfaceTexture.updateTexImage();

            //Log.d(TAG, "-----updateTexImage: 2");
       }


        if((surfaceTexture6_2!=null) && (Ghost.sixChannelCameraFlag == 1)){

            surfaceTexture6_2.updateTexImage();
            array[1]=texture6_2;

        }


            //System.getenv();

/*            Log.d(TAG, "-----persisit1");
            SystemProperties.set("persisit.sys.ParamArray","100,100,100,100,100,100,100,100");
            //String param = SystemProperties.get("persisit.sys.ParamArray");
            Log.d(TAG, "-----persisit2");*/
/*            angle--;
            if(angle < -30)
                angle=0;
            AVM.avmSetCmd(AVM.CMD_TIRE_ANGLE,angle);*/

            //Log.d(TAG, "-----AVM.avmSetCmd = 0: ");

            //if(AVM.luma_on == 1) {
                float[] paramsValues;
                int ret;
                //ret = AVM.avmSetCmd(71,0);

                //if(ret == 0) {
                    //paramsValues = DVRApplication.getInstance().getDVR().getFrontCamera().GetParamsValue();
                    //paramsValues = com.example.nativesurface.AVMCam.getParams();
                    //Log.d(TAG, "-----paramsValues: " + paramsValues[0] + "," + paramsValues[1] + "," + paramsValues[2] + "," + paramsValues[3] + "," + paramsValues[4] + "," + paramsValues[5] + "," + paramsValues[6] + "," + paramsValues[7]);
                    //if(paramsValues[0] > 20.0 && paramsValues[0] < 255 )
                    //AVM.SetLuma(paramsValues);
               // }else{

                    //Log.d(TAG, "-----AVM.avmSetCmd = 0: ");
               // }
            //}

            if(AVM.firstFrameFlag == 1) {
                float[] imageObjects = new float[12];


                imageObjects[2] = 200;
                imageObjects[3] = 150;
                imageObjects[4] = 100;
                imageObjects[5] = 100;


                imageObjects[6+2] = 350;
                imageObjects[6+3] = 200;
                imageObjects[6+4] = 100;
                imageObjects[6+5] = 100;

                //get objects & set objects
/*                if(Ai.aiOn == 1) {
                    float[] imageObjects2;
                    imageObjects2 = Ai.GetObjects(0);
                    AVM.SetObjects(0, imageObjects2);

                    imageObjects2 = Ai.GetObjects(1);
                    AVM.SetObjects(1, imageObjects2);
                }*/


                AVM.avmRender(array);


            }


    }

    private int createTextureID(){
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

}
