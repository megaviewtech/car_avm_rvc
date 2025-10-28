/* Copyright Statement:
 *
 * This software/firmware and related documentation ("AutoChips Software") are
 * protected under relevant copyright laws. The information contained herein is
 * confidential and proprietary to AutoChips Inc. and/or its licensors. Without
 * the prior written permission of AutoChips inc. and/or its licensors, any
 * reproduction, modification, use or disclosure of AutoChips Software, and
 * information contained herein, in whole or in part, shall be strictly
 * prohibited.
 *
 * AutoChips Inc. (C) 2019. All rights reserved.
 *
 * BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND AGREES
 * THAT THE SOFTWARE/FIRMWARE AND ITS DOCUMENTATIONS ("AUTOCHIPS SOFTWARE")
 * RECEIVED FROM AUTOCHIPS AND/OR ITS REPRESENTATIVES ARE PROVIDED TO RECEIVER
 * ON AN "AS-IS" BASIS ONLY. AUTOCHIPS EXPRESSLY DISCLAIMS ANY AND ALL
 * WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NONINFRINGEMENT. NEITHER DOES AUTOCHIPS PROVIDE ANY WARRANTY WHATSOEVER WITH
 * RESPECT TO THE SOFTWARE OF ANY THIRD PARTY WHICH MAY BE USED BY,
 * INCORPORATED IN, OR SUPPLIED WITH THE AUTOCHIPS SOFTWARE, AND RECEIVER AGREES
 * TO LOOK ONLY TO SUCH THIRD PARTY FOR ANY WARRANTY CLAIM RELATING THERETO.
 * RECEIVER EXPRESSLY ACKNOWLEDGES THAT IT IS RECEIVER'S SOLE RESPONSIBILITY TO
 * OBTAIN FROM ANY THIRD PARTY ALL PROPER LICENSES CONTAINED IN AUTOCHIPS
 * SOFTWARE. AUTOCHIPS SHALL ALSO NOT BE RESPONSIBLE FOR ANY AUTOCHIPS SOFTWARE
 * RELEASES MADE TO RECEIVER'S SPECIFICATION OR TO CONFORM TO A PARTICULAR
 * STANDARD OR OPEN FORUM. RECEIVER'S SOLE AND EXCLUSIVE REMEDY AND AUTOCHIPS'S
 * ENTIRE AND CUMULATIVE LIABILITY WITH RESPECT TO THE AUTOCHIPS SOFTWARE
 * RELEASED HEREUNDER WILL BE, AT AUTOCHIPS'S OPTION, TO REVISE OR REPLACE THE
 * AUTOCHIPS SOFTWARE AT ISSUE, OR REFUND ANY SOFTWARE LICENSE FEES OR SERVICE
 * CHARGE PAID BY RECEIVER TO AUTOCHIPS FOR SUCH AUTOCHIPS SOFTWARE AT ISSUE.
 */

#include <android/native_window.h>
#include <android/native_window_jni.h>

#ifndef __AVM_CAM_H__
#define __AVM_CAM_H__

#ifdef __cplusplus
extern "C" {
#endif

#define AVM_CAM_CHANNEL_MAX     (4)
#define AVM_CAM_CHN_BUF_MAX     (4)

enum {
    AVM_CAM_EVENT_NONE = 0,
    AVM_CAM_EVENT_SIGNAL, /*param1:width param2:height  0x0:signal_lost  non-0x0:signal_ready*/
    AVM_CAM_EVENT_BUF_DONE, /*param1:buf_id*/
};

enum {
    AVM_CAM_ID_0 = 0,         /*<  camera id for camera 0 */
    AVM_CAM_ID_1 = 1,         /*<  camera id for camera 1 */
    AVM_CAM_ID_2 = 2,         /*<  camera id for camera 2 */
    AVM_CAM_ID_3 = 3,         /*<  camera id for camera 3 */
    AVM_CAM_ID_TOTAL = 4,      /*<  camera id for total, full size frame */
    AVM_CAM_ID_TOTAL_MAX_PREVIEW = 5      /*<  camera id for total, full preview size frame */
};

typedef void (*avm_cam_event_callback) (int event, int ch, int param1, int param2);
typedef struct {
    void* ptr;
    int fd;
    int format; //1:YUYV
    int width;
    int height;
    int stride;
    int buffer_size;
} avm_cam_buf;

int avm_cam_open (avm_cam_event_callback callback);
int avm_cam_enable_channel (const int enable[AVM_CAM_CHANNEL_MAX]);
//int avm_cam_query_buf (int ch, int buf_id, avm_cam_buf* buf);
int avm_cam_setANativeWindow(ANativeWindow* nativeWindow);
int avm_cam_start (void);
//int avm_cam_queue_buf (int ch, int buf_id);
//int avm_cam_deque_buf (int ch);
int avm_cam_get_buf(int ch,avm_cam_buf* buf);
int avm_cam_stop (void);
int avm_cam_close (void);

//for dlopen
typedef struct {
    int (*open) (avm_cam_event_callback);
    int (*enable_channel) (const int[]);
    //int (*query_buf) (int, int, avm_cam_buf*);
    int (*setANativeWindow) (ANativeWindow* nativeWindow);
    int (*start) (void);
    //int (*queue_buf) (int, int);
    //int (*deque_buf) (int);
    int (*get_buf) (int,avm_cam_buf*);
    int (*stop) (void);
    int (*close) (void);
} avm_cam_ops;
const avm_cam_ops* avm_cam_get_ops (void);

#ifdef __cplusplus
}
#endif
#endif
