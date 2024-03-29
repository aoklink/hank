/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>

/* Header for class NDKTools */

#ifndef _Included_cn_linkfeeling_hankserve_bean_NDKTools
#define _Included_cn_linkfeeling_hankserve_bean_NDKTools
#ifdef __cplusplus



extern "C" {
#endif
//#include <NDKTool.h>
#include <stdio.h>
#include<math.h>
#include<stdlib.h>
#include <android/log.h>

#define TAG "NativeJni"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型


#define DEVICE_LEN 25       // 传感器中参与距离计算的数据量
#define MOVE 15            // 平移要求量
#define AMP 100            // 归一要求比例

#define  MAX_WATCH_DATA_LEN 40
#define  MAX_DEVICE_DATA_LEN 130

#define MAX_MAC_ADDR_LEN 4
#define MAX_DEVICE_NAME_LEN 6
#define HIGH_THR_P 0.6        // 门限比例
#define DEVICE_MIN_HIGH_THR 2
#define WATCH_THR_TIME 30
#define DEVICE_THR_TIME 30
#define WATCH_MIN_HIGH_THR 7

typedef struct tag_raw_data {
    signed char x;
    signed char y;
    signed char z;
} ACCEL_DATA;

typedef struct tag_watch_data {
    ACCEL_DATA *pData;
} WATCH_DATA;


//滑窗
void average_data(signed char *pdata, short len) {
    short index;
    for (index = 0; index < (len - 2); index++) {
        pdata[index] = (pdata[index] + pdata[index + 1] + pdata[index + 2]) / 3;
    }
}


//过滤一半数据
void
filter_data(signed char *in_data, short in_data_len, signed char *out_data, short *out_data_len) {
    short in_index;
    short out_index = 0;
    for (in_index = 0; in_index < in_data_len; in_index += 4) {
        out_data[out_index] = in_data[in_index];
        out_index++;
    }
    *out_data_len = out_index;
}


//计算活跃度
int extent(signed char *in_data_wa, short in_data_len) {
    int total_extent = 0;
    short index;
    for (index = 0; index < in_data_len - 1; index++) {
        total_extent = total_extent + abs(in_data_wa[index + 1] - in_data_wa[index]);
    }
    return total_extent;
}

//确定活跃轴
void active_axis(char *active_data, char *axis_data_wa, short in_data_len) {
    short index;
    for (index = 0; index < in_data_len; index++) {
        active_data[index] = axis_data_wa[index];
    }
}

// 计次函数
short times(char *active_data, short in_data_len,short min_high_thr,short time_thr) {
    short count_times = 0;
    short high_thr;
    short index_h;
    char max;
    char min;
    char max_final;
    char min_final;
    short index_s;
    short index_sj;

    char count_max1;
    char count_min;
    char count_max2;
    //max = active_data[0];
    //min = active_data[0];
    count_max1 = active_data[0];
    count_min = active_data[0];

    max_final = active_data[0];
    min_final = active_data[0];
    index_s = 0;
    high_thr = min_high_thr;
    while (index_s < in_data_len - 1) {
        //printf("in_data_len:%d\n", in_data_len);
        if (index_s % time_thr == 0) {
            max = active_data[index_s];
            min = active_data[index_s];
            //max_final = active_data[index_s];
            //min_final = active_data[index_s];
            for (index_h = 0; index_h < time_thr; index_h++) {
                if (active_data[index_s + index_h] > max)
                    max = active_data[index_s + index_h];
                if (active_data[index_s + index_h] < min)
                    min = active_data[index_s + index_h];
                if (index_s + index_h > in_data_len - 2) {
                    max = max_final;
                    min = min_final;
                    break;
                }

            }
            max_final = max;
            min_final = min;
            high_thr = (max_final - min_final) * HIGH_THR_P;
            if (high_thr<min_high_thr)
                high_thr = min_high_thr;

            //printf("high_thr:%d\n", high_thr);
        }
        if (active_data[index_s + 1] > active_data[index_s] &&
            active_data[index_s + 1] > count_max1) {
            count_max1 = active_data[index_s + 1];
            count_min = active_data[index_s + 1];
        } else if (active_data[index_s + 1] < active_data[index_s] &&
                   active_data[index_s + 1] < count_min) {
            count_min = active_data[index_s + 1];
        }
        index_s = index_s + 1;
        //printf("index_s:%d\n", index_s);
        if (count_max1 - count_min > high_thr) {
            count_max2 = count_min;
            index_sj = index_s;
            while (index_sj < in_data_len - 1) {
                if (index_s % time_thr == 0) {
                    max = active_data[index_s];
                    min = active_data[index_s];
                    //max_final = active_data[index_s];
                    //min_final = active_data[index_s];
                    for (index_h = 0; index_h < time_thr; index_h++) {
                        if (active_data[index_s + index_h] > max)
                            max = active_data[index_s + index_h];
                        if (active_data[index_s + index_h] < min)
                            min = active_data[index_s + index_h];
                        if (index_s + index_h > in_data_len - 2) {
                            max = max_final;
                            min = min_final;
                            break;
                        }

                    }
                    max_final = max;
                    min_final = min;
                    high_thr = (max_final - min_final) * HIGH_THR_P;
                    if (high_thr<min_high_thr)
                        high_thr = min_high_thr;
                    //printf("max:%d\n", max);
                    //printf("min:%d\n", min);
                    //printf("high_thr:%d\n", high_thr);
                }
                if (active_data[index_sj + 1] < active_data[index_sj] &&
                    active_data[index_sj + 1] < count_min) {
                    count_min = active_data[index_sj + 1];
                    count_max2 = active_data[index_sj + 1];

                } else if (active_data[index_sj + 1] > active_data[index_sj] &&
                           active_data[index_sj + 1] > count_max2) {
                    count_max2 = active_data[index_sj + 1];
                }
                if (count_max2 - count_min > high_thr) {
                    count_times = count_times + 1;
                    count_max1 = count_max2;
                    count_min = count_max2;
                    //printf("index_sj:%d\n", index_sj);
                    break;
                }
                index_sj++;
                index_s++;
            }
        }
    }
    return count_times;
}


//主要代码部分
unsigned int match_data(unsigned char *device_data, short de_data_len, WATCH_DATA *watch_data,
                        short wa_data_len) {

    int extent_x;
    int extent_y;
    int extent_z;
    int extent_value;

    short device_data_len;
    short watch_data_len;
    short device_filtlen;

    int out_data = 0;


    device_data_len = de_data_len;
    watch_data_len = wa_data_len;

    device_filtlen = (device_data_len - 1) / 4 + 1;

    unsigned char wa_times;
    unsigned char device_times;
    //short match_rate;

    short index;
    short index_s;
    signed char *x_raw_data = NULL;
    signed char *y_raw_data = NULL;
    signed char *z_raw_data = NULL;
    signed char *extent_raw_data = NULL;

    x_raw_data = (char *) malloc(sizeof(char) * (watch_data_len));
    y_raw_data = (char *) malloc(sizeof(char) * (watch_data_len));
    z_raw_data = (char *) malloc(sizeof(char) * (watch_data_len));
    extent_raw_data = (char *) malloc(sizeof(char) * (watch_data_len));
    if (x_raw_data == NULL || y_raw_data == NULL || z_raw_data == NULL) {
        printf("error1 \r\n");
        return -1;
    }
    char *device_raw_data = NULL;
    char *device_smooth_data_raw = NULL;
    char *device_smooth_data = NULL;
    char *flip_device_data = NULL;

    device_raw_data = (char *) malloc(sizeof(char) * device_filtlen);
    device_smooth_data_raw = (char *) malloc(sizeof(char) * device_filtlen);
    device_smooth_data = (char *) malloc(sizeof(char) * (device_filtlen - MOVE));
    flip_device_data = (char *) malloc(sizeof(char) * (device_filtlen - MOVE));
    if (device_raw_data == NULL || device_smooth_data_raw == NULL || device_smooth_data == NULL ||
        flip_device_data == NULL) {
        printf("error1 \r\n");
        return -1;
    }
    short device_real_data_len;


    /* trans data to buffer */
    for (index = 0; index < watch_data_len; index++) {
        x_raw_data[index] = watch_data->pData[index].x;
        y_raw_data[index] = watch_data->pData[index].y;
        z_raw_data[index] = watch_data->pData[index].z;
    }


    filter_data(device_data, device_data_len, &device_raw_data[0], &device_real_data_len);



    //平滑160ms设备曲线
    for (index_s = 0; index_s < device_filtlen - 2; index_s++) {
        *(device_smooth_data_raw + index_s) =
                (*(device_raw_data + index_s) + *(device_raw_data + index_s + 1) +
                 *(device_raw_data + index_s + 2)) / 3;
    }
    *(device_smooth_data_raw + device_filtlen - 2) = *(device_raw_data + device_filtlen - 2);
    *(device_smooth_data_raw + device_filtlen - 1) = *(device_raw_data + device_filtlen - 1);


    average_data(x_raw_data, watch_data_len);
    average_data(y_raw_data, watch_data_len);
    average_data(z_raw_data, watch_data_len);

    //各轴活跃度
    extent_x = extent(x_raw_data, watch_data_len);
    extent_y = extent(y_raw_data, watch_data_len);
    extent_z = extent(z_raw_data, watch_data_len);

    extent_value = extent_x;
    active_axis(extent_raw_data, x_raw_data, watch_data_len);
    if (extent_y > extent_value) {
        extent_value = extent_y;
        active_axis(extent_raw_data, y_raw_data, watch_data_len);
    }
    if (extent_z > extent_value) {
        extent_value = extent_z;
        active_axis(extent_raw_data, z_raw_data, watch_data_len);
    }

	wa_times = times(extent_raw_data, watch_data_len, WATCH_MIN_HIGH_THR, WATCH_THR_TIME) + 1 ;
	device_times = times(device_smooth_data_raw, device_filtlen, DEVICE_MIN_HIGH_THR, DEVICE_THR_TIME) + 1;

    printf("device_times:%d\n", device_times);
    printf("wa_times:%d\n", wa_times);


    free(x_raw_data);
    free(y_raw_data);
    free(z_raw_data);
    free(device_raw_data);
    free(device_smooth_data);
    free(flip_device_data);
    free(extent_raw_data);
    free(device_smooth_data_raw);


    out_data = (device_times & 0xff);
    out_data += (wa_times << 8);
    return out_data;

}


/*
 * Class:     NDKTools
 * Method:    getStringFromNDK
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_cn_linkfeeling_hankserve_bean_NDKTools_getStringFromNDK
        (JNIEnv *env, jclass jobj) {
    return (*env)->NewStringUTF(env, "JNI NDK demo");
}


JNIEXPORT jint JNICALL Java_cn_linkfeeling_hankserve_bean_NDKTools_match_1data
        (JNIEnv *env, jclass jobj, jbyteArray deviceData, jshort de_data_len, jobject watchData,
         jshort wa_data_len) {
    LOGD("########## size = %d", de_data_len);
    jint ret;
    WATCH_DATA watch_data_temp;
    ACCEL_DATA *pData;

    jbyte *pDevice = (*env)->GetByteArrayElements(env, deviceData, 0);


    // jclass clazz = (*env)->FindClass(env,"cn/linkfeeling/hankserve/bean/WatchData");
    jclass clazz = (*env)->GetObjectClass(env, watchData);
    jfieldID arrFieldId = (*env)->GetFieldID(env, clazz, "data",
                                             "[Lcn/linkfeeling/hankserve/bean/AccelData;");
    if (arrFieldId == 0) return 0;
    jobjectArray jarr = (jobjectArray) (*env)->GetObjectField(env, watchData, arrFieldId);
    // jobject *arr = (*env)->GetObjectArrayElement(env, jarr, 0);

    int watch_data_size = (*env)->GetArrayLength(env, jarr);
    LOGD("########## watch_data_size = %d", watch_data_size);

    pData = (signed char *) malloc(sizeof(ACCEL_DATA) * (watch_data_size));
    if (pData == 0) {
        LOGD("########## pData malloc failed");
        return -1; // return unvalid value
    }

    for (int index = 0; index < watch_data_size; index++) {
        jobject bb = (jobject) (*env)->GetObjectArrayElement(env, jarr, index);
        if (bb == NULL) {
            LOGD("##########我是null");

            return NULL;
        }
        jclass cls = (*env)->GetObjectClass(env, bb);
        jfieldID x_fID = (*env)->GetFieldID(env, cls, "x", "B");
        jfieldID y_fID = (*env)->GetFieldID(env, cls, "y", "B");
        jfieldID z_fID = (*env)->GetFieldID(env, cls, "z", "B");
        jbyte x_value = (*env)->GetByteField(env, bb, x_fID);
        jbyte y_value = (*env)->GetByteField(env, bb, y_fID);
        jbyte z_value = (*env)->GetByteField(env, bb, z_fID);

        pData[index].x = x_value;
        pData[index].y = y_value;
        pData[index].z = z_value;

        LOGD("##########我来也");

    }
    LOGD("##########我来也222");
    watch_data_temp.pData = pData;
    ret = match_data(pDevice, de_data_len, &watch_data_temp, wa_data_len);
    free(pData);
    return ret;
}

#ifdef __cplusplus
}
#endif
#endif

