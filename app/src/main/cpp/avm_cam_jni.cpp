#include <jni.h>
#include <string>
#include <pthread.h>
#include <unistd.h>
#include <dlfcn.h>
#include <vector>
#include <chrono>
#include <android/log.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "avm_cam.h"


#define _WIDTH_ 2560
#define _HEIGHT_ 1920

#define _WIDTH_RECORDER_ 1920
#define _HEIGHT_RECORDER_ 1080

static const char* const kClassPathName = "com/example/nativesurface/AVMCam";
#undef LOG_TAG
#define LOG_TAG "AVMCamJni"
#define _dbg(fmt, ...) do {__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "[%s:%d] " fmt "\n", __FUNCTION__, __LINE__, ##__VA_ARGS__);} while(0)
#define _err(fmt, ...) do {__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "[%s:%d] " fmt "\n", __FUNCTION__, __LINE__, ##__VA_ARGS__);} while(0)


#ifndef _Included_com_autochips_avm_AVMCam
#define _Included_com_autochips_avm_AVMCam
#ifdef __cplusplus
extern "C" {
#endif
#undef AVMCam_AVM_CAM_OUTPUT_MODE_NORMAL
#define AVMCam_AVM_CAM_OUTPUT_MODE_NORMAL 0L
#undef AVMCam_AVM_CAM_OUTPUT_MODE_ALL_IN_ONE
#define AVMCam_AVM_CAM_OUTPUT_MODE_ALL_IN_ONE 1L

#define WINDOW_FORMAT_NV21   (0x11)
#define WINDOW_FORMAT_NV12   (0x15)
#define WINDOW_FORMAT_YV12   (0x32315659)

ANativeWindow* mOutputWindow;
ANativeWindow* mOutputWindowForRecorder;

static jmethodID method_callback;
jclass global_class = NULL;
jclass myClass = NULL;
JavaVM* gJavaVM = NULL;

jint g_recorder_type = 0;

class Mutex_Autolock {
public:
    Mutex_Autolock(pthread_mutex_t& mutex) : mMutex(mutex) {
        pthread_mutex_lock(&mMutex);
    }
    virtual ~Mutex_Autolock() {
        pthread_mutex_unlock(&mMutex);
    }
private:
    pthread_mutex_t& mMutex;
};

static pthread_mutex_t mMutex = PTHREAD_MUTEX_INITIALIZER;
static int mIsStart = false;
static bool mCamChnEn[AVM_CAM_CHANNEL_MAX];
static bool mCamChnReady[AVM_CAM_CHANNEL_MAX];

static const avm_cam_ops* avm_cam = NULL;
void* dl_hnd = NULL;

static int libavm_cam_load (void)
{
    char* dl_err = NULL;
    const avm_cam_ops* (*avm_cam_get_ops_fn)(void) = NULL;
    dl_hnd = dlopen("libavm_cam.so", RTLD_NOW);
    if (NULL == dl_hnd) {
        dl_err = dlerror();
        _err("dlopen error %s", dl_err);
        return -1;
    }

    avm_cam_get_ops_fn = (const avm_cam_ops*(*)()) dlsym(dl_hnd, "avm_cam_get_ops");
    if (NULL == avm_cam_get_ops_fn) {
        dl_err = dlerror();
        _err("dlsym avm_cam_get_ops error %s", dl_err);
        return -1;
    }

    avm_cam = avm_cam_get_ops_fn();
    return 0;
}

static void onBufferDone (int ch)
{
    avm_cam_buf buf;
    int ret = 0;
    _dbg("onBufferDone ch:%d",ch);

     if(ch == AVM_CAM_ID_TOTAL){
        if(mOutputWindow == NULL){
            return;
        }
        avm_cam->get_buf(ch, &buf);
        ANativeWindow_Buffer outBuf;

        int32_t width = _WIDTH_;
        int32_t height = _HEIGHT_;
        auto start = std::chrono::high_resolution_clock::now();

        _err("onBufferDone() ch:%d----- width:%d,height:%d,buf.height:%d,widht:%d",ch, width,height,buf.height,buf.width);
        ret = ANativeWindow_lock(mOutputWindow, &outBuf, NULL);

        if (ret < 0) {
            _err("ANativeWindow_lock() fail. %d", ret);
        }
        memcpy(outBuf.bits,buf.ptr,buf.buffer_size);

        ret = ANativeWindow_unlockAndPost(mOutputWindow);

        auto end = std::chrono::high_resolution_clock::now();
        std::chrono::duration<double, std::milli> elapsed = end - start;
        _dbg("ANativeWindow_show tooks : %dms\n",(int)(elapsed.count()));
    } else if(ch == AVM_CAM_ID_TOTAL_MAX_PREVIEW){
         if(g_recorder_type == 0){
             JNIEnv *env = NULL;
             int status;
             bool isAttached = false;
             status = gJavaVM->GetEnv((void**)&env, JNI_VERSION_1_4);
             if (status < 0) {
                 if (gJavaVM->AttachCurrentThread(&env, NULL))////将当前线程注册到虚拟机中
                 {
                     return;
                 }
                 isAttached = true;
             }
             //实例化该类
             jobject jobject = env->AllocObject(global_class);//分配新 Java 对象而不调用该对象的任何构造函数。返回该对象的引用。
             jclass clazz = env->GetObjectClass(jobject);
             //调用Java方法
             avm_cam->get_buf(ch, &buf);
             jbyte* buffer = new jbyte[buf.width * buf.height * 3 /2];

             memcpy(buffer,buf.ptr,buf.width * buf.height * 3 /2);

             jbyteArray bytes = env->NewByteArray(buf.width * buf.height * 3 /2);

             env->SetByteArrayRegion(bytes,0,buf.width * buf.height * 3 /2,buffer);
             _dbg("CallStaticVoidMethod onFrameUpdate");
             env->CallStaticVoidMethod(clazz, method_callback,bytes,buf.width * buf.height * 3 /2);

             env->DeleteLocalRef(bytes);
             delete[] buffer;

             if (isAttached) {
                 gJavaVM->DetachCurrentThread();
             }
         } else if (g_recorder_type == 1){
             if(mOutputWindowForRecorder == NULL){
                 return;
             }
             avm_cam->get_buf(ch, &buf);
             ANativeWindow_Buffer outBuf;

             int32_t width = _WIDTH_RECORDER_;//ANativeWindow_getWidth(nativeWindow);
             int32_t height = _HEIGHT_RECORDER_;//ANativeWindow_getHeight(nativeWindow);
             auto start = std::chrono::high_resolution_clock::now();

             ret = ANativeWindow_lock(mOutputWindowForRecorder, &outBuf, NULL);
             _err("onBufferDone() mOutputWindowForRecorder ch:%d----- width:%d,height:%d,stride:%d,---------- buf.height:%d,widht:%d",ch, width,height,outBuf.stride,buf.height,buf.width);
             if (ret < 0) {
                 _err("ANativeWindow_lock() fail. %d", ret);
             }
             _dbg("memcpy buf start");
              memcpy(outBuf.bits,buf.ptr,buf.buffer_size);
             _dbg("memcpy buf end");

             ret = ANativeWindow_unlockAndPost(mOutputWindowForRecorder);

             auto end = std::chrono::high_resolution_clock::now();
             std::chrono::duration<double, std::milli> elapsed = end - start;
             _dbg("ANativeWindow_show mOutputWindowForRecorder tooks : %dms\n",(int)(elapsed.count()));
         }
     }
}
static void onSignalChange (int ch, int width, int height)
{
    if (width > 0 && height > 0) {
        _dbg("Channel %d Signal:Ready Size:%dx%d", ch, width, height);
    } else {
        _dbg("Channel %d Signal:Lost", ch);
    }
}
static void event_callback (int event, int ch, int param1, int param2)
{

    while (0 != pthread_mutex_trylock(&mMutex)) {
        if (!mIsStart) {
            return;
        }
        usleep(1000);
    }
    if (!mIsStart) {
        pthread_mutex_unlock(&mMutex);
        return;
    }

    if (ch < 0 /*|| ch >= AVM_CAM_CHANNEL_MAX*/) {
        pthread_mutex_unlock(&mMutex);
        return;
    }

    switch (event) {
        case AVM_CAM_EVENT_SIGNAL:
            onSignalChange(ch, param1, param2);
            break;
        case AVM_CAM_EVENT_BUF_DONE:
            onBufferDone(ch);
            break;
        default:
            _err("Unknown event:%d ch:%d param1:%d param2:%d", event, ch, param1, param2);
            break;
    }

    pthread_mutex_unlock(&mMutex);
}
JNIEXPORT jint JNICALL AVMCam_open
        (JNIEnv *env, jclass object)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");

    _dbg("start libavm_cam_load");
    if (libavm_cam_load() < 0) {
        _err("libavm_cam_load() fail.");
        return -1;
    }
    if (mIsStart) {
        _err("is started");
        return -1;
    }

    int ret = avm_cam->open(event_callback);
    if (ret < 0) {
        _err("avm camera open fail %d", ret);
        return ret;
    }

    mIsStart = false;

    for (int ch = 0; ch < AVM_CAM_CHANNEL_MAX; ch++) {
        mCamChnEn[ch] = false;
        mCamChnReady[ch] = false;
    }
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_enableChannel
        (JNIEnv *env, jclass, jbooleanArray enable)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");

    if (enable == NULL) {
        _err("enable is null");
        return -1;
    }

    if (mIsStart) {
        _err("is started");
        return -1;
    }

    jboolean *jEnablePtr = env->GetBooleanArrayElements(enable, NULL);
    jsize jEnableLen = env->GetArrayLength(enable);

    if (jEnablePtr == NULL || jEnableLen != AVM_CAM_CHANNEL_MAX) {
        _err("Invalid enable param");
        if (NULL != jEnablePtr) {
            env->ReleaseBooleanArrayElements(enable, jEnablePtr, 0);
        }
        return -1;
    }

    int channel_en[AVM_CAM_CHANNEL_MAX] = {0};
    for (int ch = 0; ch < AVM_CAM_CHANNEL_MAX; ch++) {
        channel_en[ch] = !!jEnablePtr[ch];
    }

    int ret = avm_cam->enable_channel(channel_en);
    if (ret < 0) {
        _err("avm camera enable fail %d", ret);
        env->ReleaseBooleanArrayElements(enable, jEnablePtr, 0);
        return ret;
    }

    for (int ch = 0; ch < AVM_CAM_CHANNEL_MAX; ch++) {
        mCamChnEn[ch] = channel_en[ch];
    }

    env->ReleaseBooleanArrayElements(enable, jEnablePtr, 0);
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_setRecorderType
        (JNIEnv *env, jclass, jint type)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    g_recorder_type = type;
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_setSurface
        (JNIEnv *env, jclass, jobject outSurface)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    int ret = -1;
    if(NULL == outSurface){
        mOutputWindow = NULL;
    }
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env,outSurface);

    if(nativeWindow == NULL){
        _err("nativeWindow == NULL ,error");
    }

    mOutputWindow = nativeWindow;

    ANativeWindow_acquire(mOutputWindow);

    int32_t format = ANativeWindow_getFormat(mOutputWindow);
    int32_t width = _WIDTH_;
    int32_t height = _HEIGHT_;

    format = WINDOW_FORMAT_YV12;

    ret = ANativeWindow_setBuffersGeometry(mOutputWindow, width, height, format);
    _dbg("Leave");
    return 0;
}


JNIEXPORT jint JNICALL AVMCam_setSurfaceForRecorder
        (JNIEnv *env, jclass, jobject outSurface)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    int ret = -1;
    if(NULL == outSurface){
        mOutputWindowForRecorder = NULL;
    }
    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env,outSurface);

    if(nativeWindow == NULL){
        _err("nativeWindow == NULL ,error");
    }

    mOutputWindowForRecorder = nativeWindow;

    ANativeWindow_acquire(mOutputWindowForRecorder);

    int32_t format = ANativeWindow_getFormat(nativeWindow);
    int32_t width = _WIDTH_RECORDER_;//ANativeWindow_getWidth(nativeWindow);
    int32_t height = _HEIGHT_RECORDER_;//ANativeWindow_getHeight(nativeWindow);

    format = 1;//WINDOW_FORMAT_NV21;
    _dbg("ANativeWindow  format %d, size %d x %d", format, width, height);
    _dbg("start ANativeWindow_setBuffersGeometry");
    ret = ANativeWindow_setBuffersGeometry(mOutputWindowForRecorder, width, height, format);
    _dbg("end ANativeWindow_setBuffersGeometry");
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_start
        (JNIEnv *, jclass)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");

    if (mIsStart) {
        _err("is started");
        return -1;
    }

    int ret = avm_cam->start();
    if (ret < 0) {
        _err("avm camera start fail %d", ret);
        return ret;
    }

    mIsStart = true;
    _dbg("Leave");
    return 0;
}
JNIEXPORT jint JNICALL AVMCam_releaseSurface
        (JNIEnv *, jclass)
{

    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    mOutputWindow = NULL;
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_releaseSurfaceForRecorder
        (JNIEnv *, jclass)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    mOutputWindowForRecorder = NULL;
    _dbg("Leave");
    return 0;
}

JNIEXPORT jint JNICALL AVMCam_stop
        (JNIEnv *, jclass)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    int ret = 0;


    if (!mIsStart) {
        _err("not start");
        return -1;
    }

    mIsStart = false;

    ret = avm_cam->stop();

    if (ret < 0) {
        _err("avm camera stop fail. %d", ret);
        return -1;
    }

    _dbg("Leave");
    return 0;
}
JNIEXPORT jint JNICALL AVMCam_close
        (JNIEnv *, jclass)
{
    Mutex_Autolock _lock(mMutex);
    _dbg("Enter");
    int ret = avm_cam->close();
    if (ret < 0) {
        _err("avm camera close fail. %d", ret);
        return -1;
    }
    dlclose(dl_hnd);
    _dbg("Leave");
    return ret;
}


static JNINativeMethod sMethods[] = {
    {"open", "()I", (void*)AVMCam_open},
    {"enableChannel", "([Z)I", (void*)AVMCam_enableChannel},
    {"setRecorderType", "(I)I", (void*)AVMCam_setRecorderType},
    {"setSurface",  "(Landroid/view/Surface;)I", (void*)AVMCam_setSurface},
    {"releaseSurface", "()I", (void*)AVMCam_releaseSurface},
    {"setSurfaceForRecorder",  "(Landroid/view/Surface;)I", (void*)AVMCam_setSurfaceForRecorder},
    {"releaseSurfaceForRecorder", "()I", (void*)AVMCam_releaseSurfaceForRecorder},
    {"start", "()I", (void*)AVMCam_start},
    {"stop", "()I", (void*)AVMCam_stop},
    {"close", "()I", (void*)AVMCam_close}
};

jint JNI_OnLoad(JavaVM *jvm, void* reserved) {
    JNIEnv *env;
    int status;
    jclass clazz;
    gJavaVM = jvm;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_4)) {
        //ALOGE("JNI version mismatch error");
        return JNI_ERR;
    }

    clazz = env->FindClass(kClassPathName);
    global_class = (jclass)env->NewGlobalRef(clazz);
    method_callback = (env)->GetStaticMethodID(clazz,"onFrameUpdate","([BI)V");
    if (NULL == clazz) {
        _err("Unable to find class com.autochips.avm.AVMCam");
        return -1;
    }

    env->NewGlobalRef(clazz);

    status = (env)->RegisterNatives(clazz, sMethods, sizeof(sMethods) / sizeof(sMethods[0]));
    if (status < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_4;
}
#ifdef __cplusplus
}
#endif
#endif
