/*

AVM SDK interface v1.0

*/


package com.megaview.avm;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.megaview.ghost.Ghost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;



public class AVM {
    private static final String TAG = "[AVMSDK]";
    static private Bitmap mBitmap;
    static public AssetManager mManager;

    static public int angle =0;    //3d全景视图当前角度
    static public int firstFrameFlag =1;    //第一帧标记
    static public int rotateFlag =0;    //旋转状态标记

    //static public int screenWidth =2700-100;//AVM窗口宽
    //static public int screenHeight =1224;//AVM窗口高

    static public int screenWidth =1920;//AVM窗口宽
    static public int screenHeight =1080;//AVM窗口高
    static public int cameraWidth =1280;
    static public int cameraHeight =720;

    static public int clothType =2;//0:2big   1: 2 little+ 4 chessboard    2:2big new    3 2big+4chessboard   4 2big+2chessboard

    static public int imgSrcFlag =0;//0:   1:/sdcard/avm/

    static public int customLenParamFlag = 0;//0:default   1:custom camera len
    static public int demoFlag = 0;//0:camera    1:static picture

    static public int rectCornerType = 0;//value 0:one rect point   1:two rect point
    static public int chessboardType = 0;//value 0:4*6   1:4*4

    static public int carBottomViewFlag = 0;//0   1:open bottom view

    static public int singleCameraFlag = 0;//0：田字格大图   1:四个单独camera图像

    //H screen
    //0:1024*600
    //1:1280*720
    //2:1280*480
    //3:1280*800
    //4:1920*720
    //5:1920*860
    //6:1920*1080
    //7:2000*1200
    //19:1024*384//for test

    //V screen
    //20 768*1024
    //21 800*1280
    //22 1080*1920
    //23 960*1280
    //24 768*720
    //25 768*880
    //26 800*1100
    //27 1080*1650
    //39:450*600 //for test
    static public int resolutionIndex =39;
    static public int camera_1920_flag = 0;
    static public int luma_on = 1;
    static public int rotate_style = 1;//0 分段旋转，1 任意旋转
    static public int moving_flag = 0;//0 no move，1 move

    static public int h_2d_left_flag = 1;// //horizontal screen 2d position,1:2d at left   0:2d at right

    static public int LOAD_CAR =1;    //车模入库
    static public int SET_CAR0 =0;    //设置当前车模方式0
    static public int SET_CAR2 =2;    //设置当前车模方式2，只能在onSurfaceCreated函数里使用(AVM.avmInit之后)，用于启动app时第一次设置车模
    static public int SET_CAR3 =3;    //设置当前车模方式3，只能在onSurfaceCreated函数里使用(AVM.avmInit之后)，用于启动app时第一次设置车模
    static public int SET_CAR8 =8;    //设置当前车模方式8，只能在onSurfaceCreated函数里使用(AVM.avmInit之后)，用于启动app时第一次设置车模


    static public int VIEW_SINGLE_F =0;    //前视图
    static public int VIEW_SINGLE_B =1;    //后视图
    static public int VIEW_SINGLE_L =2;    //左视图
    static public int VIEW_SINGLE_R =3;    //右视图
    static public int VIEW_QUAD=4;         //四分视图
    static public int VIEW_3D_ALL=5;       //3D全景视图
    static public int VIEW_3D_L=6;         //3D左视图
    static public int VIEW_3D_R=7;         //3D右视图
    static public int VIEW_3D_F=8;         //前随动视图
    static public int VIEW_3D_B=9;         //后随动视图
    static public int VIEW_NARROW=10;      //前窄道视图
    static public int VIEW_NARROW_F=11;    //窄道视图
    static public int VIEW_SINGLE_2DF=12;  //放大前视图
    static public int VIEW_SINGLE_2DB=13;  //放大后视图
    static public int VIEW_3D_ALL_CAR=15;       //3D车模设置视图，只显示车模
    static public int VIEW_3D_L2=16; //校正左视图
    static public int VIEW_3D_R2=17; ////校正右视图
    static public int VIEW_LOGO=20;       //显示LOGO
    static public int VIEW_2D_FULLSCREEN_H=21;//横屏2d全屏视图
    static public int VIEW_NARROW_F2 = 22; //窄道校正视图 前轮
    static public int VIEW_2D_FULLSCREEN_V=23;       //竖屏2d全屏视图

    static public int VIEW_FRONT_ORI_FULLSCREEN = 24; //全屏原始前视图
    static public int VIEW_BACK_ORI_FULLSCREEN = 25; //全屏原始后视图
    static public int VIEW_LEFT_ORI_FULLSCREEN = 26; //全屏原始左视图
    static public int VIEW_RIGHT_ORI_FULLSCREEN = 27; //全屏原始右视图

    static public int VIEW_FRONT_NORMAL_FULLSCREEN = 28; //全屏校正广角前视图
    static public int VIEW_BACK_NORMAL_FULLSCREEN = 29; //全屏校正广角后视图

    static public int VIEW_NARROW_ORI_FRONT = 30;//窄道原始图 前轮
    static public int VIEW_NARROW_ORI_BACK = 31;//窄道原始图 后轮

    static public int VIEW_NARROW_ORI_FULL = 32;//窄道原始图 四轮
    static public int VIEW_NARROW_ORI_FULL_REVERSE = 33;//窄道原始图镜像 四轮
    static public int VIEW_NARROW_B2 = 36;////窄道校正视图 后轮

    static public int VIEW_3D_LEFT_FRONT=42;//2d全景图 + 3d左前视图
    static public int VIEW_3D_RIGHT_FRONT=43;//2d全景图 + 3d右前视图

    //
    static public int PREVIEW_SIZE_1280_720 = 0;
    static public int PREVIEW_SIZE_1280_960 = 1;
    static public int PREVIEW_SIZE_1920_1080 = 2;


    static public int CMD_VIEW = 0;        //设置视图
    static public int CMD_REVERSE = 1;     //设置倒挡状态    1倒挡   0：非倒挡
    static public int CMD_SPEED=2;         //设置当前车速    车速单位：米/小时
    static public int CMD_TIRE_ANGLE=3;    //设置前轮转角   范围:[32,-32] degree，左正右负中间为0
    static public int CMD_DOOR=4;         //车门状态

    static public int CMD_RADAR=5;         //设置雷达状态    value:[xxxx xxxx]  [xxxx xxxx]  [xxxx xxxx]  [xxxx xxxx]     4个字节
                                              //                     0000 0000    0000 0000     前1  前2      前3  前4      前雷达
                                              //                     0000 0000    0000 0001     后1  后2      后3  后4      后雷达
                                              //                                               xxxx：0 很远  1 远  2 近  3 很近


    static public int CMD_TURN_LAMP=6;     //设置转向灯状态,0:转向关闭   1:左转向   2:右转向
    static public int CMD_3D_ANGLE=7;      //设置3d全景视图角度,45*n degree
    static public int CMD_PHOTO_SPLIT = 10;//把4合1的大图分割为前后左右单独图像并保存
    static public int CMD_DEMO_MODE = 13;  //设置静态模式，1：静态图片模式(avmInit之前设置,静态图片需放在/sdcard/avm/pic.bmp)  0：正常模式  默认为正常模式
    static public int CMD_SET_PLATE = 15;  //设置显示车牌 1:车模显示车牌，0:车模不显示车牌
    static public int CMD_SET_TEST = 16;  //设置自动测试模式,1:自动测试   0:正常模式  默认为正常模式
    static public int CMD_SET_PLATFORM_ID = 17;  //
    static public int CMD_SET_BOTTOM_OFFSET = 18;
    static public int CMD_SET_SCREEN_RESOLUTION = 19;//设置显示屏分辨率,默认 0: 1024*600   1: 1280*720
    static public int CMD_SET_2D_SCALE = 20;//2d俯视图缩放 1放大，-1缩小
    static public int CMD_SET_3D_FULLSCREEN = 21;//
    //static public int CMD_SET_MOVE = 22;//

    static public int CMD_SET_FRONT_TRACK = 23;//value 1显示前轨迹  0 不显示前轨迹
    static public int CMD_SET_REAR_TRACK = 24;//value 1显示后轨迹  0 不显示后轨迹
    static public int CMD_LOAD_PARAMS = 25;//导入拼接参数

    static public int CMD_FRONT_X_OFFSET = 26;//VIEW_SINGLE_F视图横向偏移 1右偏移，-1左偏移
    static public int CMD_FRONT_Y_OFFSET = 27;//VIEW_SINGLE_F视图纵向偏移 1下偏移，-1上偏移
    static public int CMD_REAR_X_OFFSET = 28;//VIEW_SINGLE_B视图横向偏移 1右偏移，-1左偏移
    static public int CMD_REAR_Y_OFFSET = 29;//VIEW_SINGLE_B视图纵向偏移 1下偏移，-1上偏移

    //left & right view:x <-> y exchange for the view have rotate 90 degree
    static public int CMD_LEFT_X_OFFSET = 31;//VIEW_SINGLE_L视图横向偏移 1右偏移，-1左偏移
    static public int CMD_LEFT_Y_OFFSET = 30;//VIEW_SINGLE_L视图纵向偏移 1下偏移，-1上偏移
    static public int CMD_RIGHT_X_OFFSET = 33;//VIEW_SINGLE_R视图横向偏移 1右偏移，-1左偏移
    static public int CMD_RIGHT_Y_OFFSET = 32;//VIEW_SINGLE_R视图纵向偏移 1下偏移，-1上偏移


    static public int CMD_FRONT_SCALE = 34;//VIEW_SINGLE_F视图缩放 1放大，-1缩小
    static public int CMD_REAR_SCALE = 35;//VIEW_SINGLE_B视图缩放 1放大，-1缩小
    static public int CMD_LEFT_SCALE = 36;//VIEW_SINGLE_L视图缩放 1放大，-1缩小
    static public int CMD_RIGHT_SCALE = 37;//VIEW_SINGLE_R视图缩放 1放大，-1缩小

    static public int CMD_CONFIG_SAVE = 60;//保存视图编辑后的参数
    static public int CMD_NO_NAME1 = 61;//
    static public int CMD_SET_DEBUG_CLOTH_TYPE = 62;//调用找点接口前设置调试布类型，0: 两块大布(440cm)   1：带棋盘格四块布  2：两块大布(可变长度)
    static public int CMD_SET_CAR_BOTTOM = 63;//设置车模底图 1:car_bottom1.png  2:car_bottom2.png  3:car_bottom3.png
    static public int CMD_NO_NAME2 = 64;//
    static public int CMD_RADAR_FRONT=65;//设置前雷达数据
    static public int CMD_RADAR_BACK=66;//设置后雷达数据

    static public int CMD_TRACK_TYPE=67;//轨迹0(不带车轮轨迹)   轨迹1(带车轮轨迹)  轨迹2(带横条车轮轨迹)
    static public int CMD_RADAR_TYPE=68;//设置雷达显示风格  value 0:线条风格  1:雷达墙风格


    static public int CMD_RESET=69;//暂时不用
    static public int CMD_3D_SCALE=70;//3d视图放大缩小
    static public int CMD_MOVE_STATUS=71;//获得动画状态
    //static public int CMD_LR_TYPE=72;//
    static public int CMD_ROTATE_STYLE=73;// 0：分段旋转   1:任意旋转
    static public int CMD_TOUCH_SCREEN=74;// 0：touch down   1:touch up

    static public int CMD_DISPLAY_TIRE_PRESSURE=75;// 0：不显示   1:显示胎压信息     左右3d视图
    static public int CMD_DISPLAY_TIRE_OK=76;// 0：不显示   1:胎压正常时显示OK图标   3d全屏视图
    static public int CMD_LOAD_FLAG=77;// 0：不加载(系统启动时logo显示时间会短些)   1:加载
    static public int CMD_NARROW_MODE=78;//narrow mode
    static public int CMD_2D_PART_DISPLAY = 81;//vertical screen 2d view mode,1: part width,0: full width
    static public int CMD_H_2D_LEFT=82;//0 H 2D at right side,1 at left side
    static public int CMD_VSCREEN_3D_2D = 83;//1： 竖屏3d旋转视图下面显示2d视图  0： 不是显示
    static public int CMD_GET_VERSION = 84;//get avm.so version,e.g. vesion 2.25 return value 225
    static public int CMD_3D_SCALE_ALL = 85;
    static public int CMD_LR2_OFFSET = 86;
    static public int CMD_LR2_ANGLE = 87;
    static public int CMD_SET_SAFE_LINE = 90;//value 1:车轮视图显示黄色辅助线   0:车轮视图不显示黄色辅助线

    static public int CMD_SET_SCREEN_SIZE = 89;
    static public int CMD_SET_VIEW_FOLLOW = 91;//value 1:view follow  0:static
    static public int CMD_LANE_LINE_SEARCH = 93;//search lane line,value:0
    static public int CMD_LANE_LINE_DEBUG = 94;//lane line debug,vaule:0
    static public int CMD_BACKVIEW_MIRROR = 95;//value 0:normal  1:mirror    default:mirror
    //get fail camera index when debug fail.
    //return value 1:front 2:back  3:left 4:right
    static public int CMD_GET_FAIL_CAMERA = 92;

    static public int CMD_TRACK_SAFELINE = 96;//value  0:不显示轨迹红色横线      1:显示轨迹红色横线

    static public int PRE_CMD_SET_CAMERA_TYPE = 97;//设置camera图像类型，value 0:田字格4合1图像    3:4个单独图像

    static public int CMD_CAR_ALPHA_VALUE = 100;//alpha value[0,100]
    static public int CMD_BOTTOM_VIEW_ON = 101;//value 0:off  1:on

    static public int CMD_TAKE_PHOTO = 102;//拍照 摄像头分辨率 value 0:1280*720   1:1280*960  2:1920*1080

    static public int CMD_CORNER_TYPE = 103;//value 0:one rect point   1:two rect point
    static public int CMD_CHESSBOARD_TYPE = 104;//value 0:4*6   1:4*4
    static public int CMD_SET_INNER_LUMA = 105;//value 0:    1:inner luma
    //static public int CMD_CAMERA_PREVIEW_SIZE = 106;//value 0:1280*720  1:1920*1080
    static public int CMD_DEBUG_OPT = 108;//0:off 1:on

    //
    static public int CMD_LINE_OFFSET = 110;
    static public int CMD_LINE_ANGLE = 111;
    static public int CMD_LINE_NEXT = 112;
    static public int CMD_LINE_OPT = 113;
    static public int CMD_SHOW_BEV_LINES = 115;
    static public int CMD_SET_DISPLAY_MODE = 124;
    static public int CMD_SET_SIX_CHANNEL_CAMERA = 132;
    static public int CMD_PRE_SET_IMG_SOURCE = 2001;//value 0    1:sdcard/avm
    static public int CMD_PRE_SET_CAMERA_SIZE = 2002;//
    static public int CMD_PRE_SET_2D_VIEW_WIDTH = 2003;//

    static public int CMD_PRE_SET_BACKVIEW_MIRROR = 2004;//
    static public int CMD_PRE_SET_MODLE_K_STYLE = 2005;//
    static public int CMD_PRE_SET_MODLE_VERTEX_RATIO = 2006;//
    static public int CMD_PRE_SET_NETWORK_FUN = 2007;//

    static {
        System.loadLibrary("avm");
    }

    public AVM(Resources res){
        mManager = res.getAssets();
    }

    public static int LoadTexturePng(String file,int textureID){

        try {
            if(file.startsWith("/"))
                mBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            else
                mBitmap = BitmapFactory.decodeStream(mManager.open(file));

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        if(mBitmap!=null&&!mBitmap.isRecycled()){

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
            return textureID;
        }
        return 0;
    }



    public static int LoadTextureEtc(String file,int textureID)
    {

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureID);

        try{
            InputStream is;

            if(file.startsWith("/"))
                is = new FileInputStream(file);
            else
                is = mManager.open(file);

            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, is);
        } catch (Exception e1) {

            return 0;
        }

        return textureID;
    }


    public static int avmInit(String filesPath,int assets_flag)
    {
        Log.i(TAG,"init()");
        return Init(filesPath,assets_flag);
    }


    public static void avmRender(int[] array)
    {

        Draw(array);
    }


    public static int avmSetCmd(int cmd,int value)
    {
        int ret;

       if(cmd != 7 || (cmd == 7 && value == 270))
         Log.i(TAG,"----avmSetCmd("+cmd+","+value+")");

        ret = SetCmd(cmd,value);

        if(cmd == 0) {
            Ghost.GhostSetCmd(Ghost.CMD_CURRENT_AVM_VIEW, value);
            Log.i(TAG,"----view:"+value);

        }

        return ret;
    }


    public interface OnSignalListener{
        /**
         *Called when a signal is sent that requires the app to be notified.
         *
         *@param msg indicates the signal type
         *@param param1 optional parameter that may be associated with the signal
         *@param param2 optional parameter that may be associated with the signal
         */
        public void onSignal(int msg, int param1, int param2, int param3);
        
        //add by liugang
        //public void onSignal2(String key, String Value);
    }

    /**
     *Register a callback to be invoked when a siganl is sent.
     *
     *@param listener the callback that will be run.
     */
     public static void setOnSignalListener(OnSignalListener listener){
        Log.i(TAG,"setOnSignalListener() called\n");
        mOnSignalListener = listener;
    }

    private static OnSignalListener mOnSignalListener;

    private static void signalNotifyNative(int what, int arg1, int arg2, int arg3)
    {
        //Log.i(TAG,"signalNotifyNative() called");

        //Log.i(TAG,"signalNotifyNative():"+what);

        //add 2021-01-12
        if(what == 160)//start move
            moving_flag = 1;
        else if(what == 161)//stop move
            moving_flag = 0;

        if (mOnSignalListener != null) {


            Log.i(TAG,"handleMessage msg: onSignal "+what);

            mOnSignalListener.onSignal(what, arg1, arg2, arg3);

        } else {
            Log.i(TAG,"handleMessage mOnSignalListener not set so do not notify");
        }
    }
    
    //call by NDK
    private static int avmLoadTexture(String file,int texutreID,int type)
    {
        //Log.i(TAG,"-----avmLoadTexture"+type+","+file);
        int ret = 0;
        if(type == 2){
            ret = LoadTexturePng(file,texutreID);

        }else{

            ret = LoadTextureEtc(file,texutreID);
        }
        return ret;
    }

    /**
     * 解压缩功能.
     * 将ZIP_FILENAME文件解压到ZIP_DIR目录下.
     * @throws Exception
     */
    public static int unZipFile(String zipFileName,String zipDir) throws Exception{
        ZipFile zfile=new ZipFile(zipFileName);
        Enumeration zList=zfile.entries();
        ZipEntry ze=null;
        byte[] buf=new byte[1024];
        while(zList.hasMoreElements()){
            ze=(ZipEntry)zList.nextElement();
            if(ze.isDirectory()){
                File f=new File(zipDir+ze.getName());
                f.mkdir();
                continue;
            }
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(zipDir, ze.getName())));
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
            int readLen=0;
            while ((readLen=is.read(buf, 0, 1024))!=-1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();

        return 1;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir 指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName){
        String[] dirs=absFileName.split("/");
        File ret=new File(baseDir);
        if(dirs.length>1){
            for (int i = 0; i < dirs.length-1;i++) {
                ret=new File(ret, dirs[i]);
            }
            if(!ret.exists())
                ret.mkdirs();
            ret=new File(ret, dirs[dirs.length-1]);
            return ret;
        }
        return ret;
    }


    //call by NDK
    public static int avmUnZip(String zipFileName,String zipDir)
    {
        try {
            unZipFile(zipFileName,zipDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //初始化360,files_path avm模块工作路径;assets_flag:资源文件是否已经更新，1:已更新 0:未更新
    public native static int Init(String files_path,int assets_flag);

    //渲染 array为camera图像texture id
    public native static int Draw(int[] array);

    //cmd 命令类型 val 命令参数
    public native static int SetCmd(int cmd,int val);

    //车模设置
    //mode: 1 车模入库,carName_color为"carName_color.obj" 或者 /../carName_color.obj，1个车模只需入库一次,入库后
    //      0 设置当前车模(车库需存在此车模),carName_color为"carName_color"
    //      2 设置当前车模(车库需存在此车模),carName_color为"carName_color"   模式2只允许在onSurfaceCreated函数里使用，放在avmInit之后，用于优化性能，2020-03-25
    //      3 设置当前车模(车库需存在此车模),carName_color为"carName_color"   模式3只允许在onSurfaceCreated函数里使用，放在avmInit之后，用于优化性能，2020-03-25
    //返回值
    // 1 车模入库成功或者设置当前车模成功
    // -1 车模入库失败,车模文件不存在
    // -2 车模入库失败,车模文件不能打开
    //
    // -4 车模入库失败,车模文件crc错误
    // -5 设置当前车模失败，车模不存在
    // -9 avm模块未初始化或者无效参数
    public native static int SetCar(String carName_color,int mode);


    //获得角点数据,还回数组长度为64(调试布类型0,32个点)或者64+32(调试布类型1,32+16个点)
    //[0-15]  前8个点
    //[16-31] 后8个点
    //[32-47] 左8个点
    //[48-63] 右8个点
    //[64-71]  前棋盘格4个点(调试布1)
    //[72-79]  后棋盘格4个点(调试布1)
    //[80-87]  左棋盘格4个点(调试布1)
    //[88-95]  右棋盘格4个点(调试布1)
    //调用此接口之前需要设置调试布类型 CMD_SET_DEBUG_CLOTH_TYPE = 62  0：2块大布  1：带棋盘格调试布
    public native static float[] GetImagesPoints();




    //启动拼接调试
    //debugParams[0]	拼接长度,单位cm
    //debugParams[1]	拼接宽度,单位cm
    //debugParams[2]	前摄像头高度(固定传57cm)
    //debugParams[3]	后摄像头高度(固定传85cm)
    //debugParams[4]	调试布类型	   0：2块大布(440cm长)	 1：带棋盘格调试布(440cm长)  2:2块大布(调试布长度可变)
    //debugParams[5]	左右棋盘格和前调试布之间的距离(固定传140,单位cm,调试布1参数)
    //debugParams[6]	调试布类型2长度
    //debugParams[7]

    public native static int Debug(int[] debugParams,float[] imgPoints);


    //获得亮度调节区域 还回长度为32的int数组，数组包含8组图像区域，前后左右图像各2各组，每组区域为x_start,y_start,width,height
    public native static int[] GetLumaRect();


    //设置各个区域的亮度值,数组长度为8，对应前后左右图像8个区域的亮度值,亮度值区间为[0.0,255.0]
    public native static int SetLuma(float[] lumaValues);



    //SimpleIndex 省份索引号
    //plateStr  车牌字符串
    //colorIndex  0：蓝牌白字   1：黄牌黑字   2：绿牌黑字
    public native static int GenPlate(int SimpleIndex,String plateStr,int colorIndex);
    //
    //GenPlate车牌接口使用示例(30，"B12345",0);       生成车牌：粤B 12345   30对应省份索引号
    //AVM.avmSetCmd(AVM.CMD_SET_PLATE,1);   设置车模显示车牌
    //

    /*车牌地区对应索引号
    澳门（澳）   0
    四川（川）   1
    湖北（鄂）   2
    甘肃（甘）   3
    江西（赣）   4
    香港（港）   5
    贵州（贵）   6
    广西（桂）   7
    黑龙江（黑） 8
    江苏（苏）   9
    吉林（吉）   10
    河北（冀）   11
    天津（津）   12
    山西（晋）   13
    北京（京）    14
    辽宁（辽）    15
    山东（鲁）    16
    内蒙古（蒙）  17
    福建（闵）    18
    宁夏（宁）    19
    青海（青）    20
    海南（琼）    21
    陕西（陕）    22
    江苏（苏）    23
    台湾（台）    24
    安徽（皖）    25
    湖南（湘）    26
    新疆（新）    27
    重庆（渝）    28
    河南（豫）    29
    广东（粤）    30
    云南（云）    31
    西藏（藏）    32
    浙江（浙）    33	*/


    //胎压接口
    //tirePressure[0~3]     车胎气压[0,999],单位kPa(left front,left back,right front,right back)
    //tirePressure[4~7]     车胎温度[-99,99],单位摄氏度
    //tirePressure[8~11]    胎压状态，0:OK,1:胎压低 2:胎压高
    //tirePressure[12~15]   0:OK  1:漏气
    //tirePressure[16~19]   0:OK  1:温度高
    //tirePressure[20~23]   0:OK  1:电量低
    public native static int SetTirePressure(int[] tirePressure);


    //
    public native static int LoadCameraParams(String CameraParamsFile);

    //index:0 front 1 back 2 left 3 right
    public native static int SetObjects(int index,float[] imgObjects);

}
