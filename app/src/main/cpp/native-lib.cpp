#include "androidlab_edu_cn_nucyixue_utils_JNIUtils.h"
#include <android/bitmap.h>
#include <opencv2/opencv.hpp>
#include <iostream>
#include <vector>

using namespace cv;
using namespace std;

JNIEXPORT jintArray JNICALL Java_androidlab_edu_cn_nucyixue_utils_JNIUtils_getEdge
        (JNIEnv *env, jobject, jobject bitmap){

    AndroidBitmapInfo info;
    void *pixels;

    jintArray result ;


    CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info) >= 0);
    CV_Assert(info.format == ANDROID_BITMAP_FORMAT_RGBA_8888 ||
              info.format == ANDROID_BITMAP_FORMAT_RGB_565);
    CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels) >= 0);
    CV_Assert(pixels);
    if (info.format == ANDROID_BITMAP_FORMAT_RGBA_8888) {
        Mat temp(info.height, info.width, CV_8UC4, pixels);
        Mat gray = temp;
        cvtColor(temp, gray, COLOR_RGBA2GRAY);

        GaussianBlur(gray, gray, Size(3, 3), 0, 0, BORDER_DEFAULT);

        int lowTresholde = 30;
        int highTresholde = lowTresholde * 3;
        Canny(gray, gray, lowTresholde, highTresholde, 3);

        Mat dilation = gray;
        Mat element2 = getStructuringElement(MORPH_RECT, Size(5, 5));//膨胀、腐蚀操作核设定
        dilate(gray,dilation ,element2);

        Mat mat;
        dilation.convertTo(mat, CV_8UC1);

        vector<vector<Point>> contours;
        vector<Vec4i> hierarchy;
        vector<RotatedRect> rects;
        findContours(mat, contours, hierarchy, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));

        for (int i = 0; i < contours.size(); i++)
        {
            double area = contourArea(contours[i]);
            if (area < 200)
                continue;

            double epsilon = 0.001*arcLength(contours[i], true);
            Mat approx;
            approxPolyDP(contours[i], approx, epsilon, true);

            RotatedRect rect = minAreaRect(contours[i]);

            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            if (m_height > m_width * 3 || m_width > m_height * 3)
                continue;

            rects.push_back(rect);
        }

        int origin[rects.size()*4];
        int i = 0;

        for(RotatedRect rect : rects){
            Point2f P[4];
            rect.points(P);

            rectangle(temp, P[1], P[3], Scalar(0, 255, 0), 3);

            int height = (int) (P[0].y - P[2].y);
            int width = (int) (P[0].x - P[2].x);
            int new_x = (int) P[2].x;
            if (width < 0) {
                new_x = (int) P[0].x;
            }
            int new_y = (int) P[2].y;

            origin[i++] = new_x;
            origin[i++] = new_y;
            origin[i++] = width;
            origin[i++] = height;
        }
        result = (*env).NewIntArray(rects.size()*4);
        (*env).SetIntArrayRegion(result, 0, rects.size()*4, origin);


    } else {
        Mat temp(info.height, info.width, CV_8UC2, pixels);
        Mat gray = temp;
        cvtColor(temp, gray, COLOR_RGB2GRAY);

        GaussianBlur(gray, gray, Size(3, 3), 0, 0, BORDER_DEFAULT);

        int lowTresholde = 30;
        int highTresholde = lowTresholde * 3;
        Canny(gray, gray, lowTresholde, highTresholde, 3);

        Mat dilation = gray;
        Mat element2 = getStructuringElement(MORPH_RECT, Size(5, 5));//膨胀、腐蚀操作核设定
        dilate(gray,dilation ,element2);

        Mat mat;
        dilation.convertTo(mat, CV_8UC1);

        vector<vector<Point>> contours;
        vector<Vec4i> hierarchy;
        vector<RotatedRect> rects;
        findContours(mat, contours, hierarchy, CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, Point(0, 0));

        for (int i = 0; i < contours.size(); i++)
        {
            double area = contourArea(contours[i]);
            if (area < 200)
                continue;

            double epsilon = 0.001*arcLength(contours[i], true);
            Mat approx;
            approxPolyDP(contours[i], approx, epsilon, true);

            RotatedRect rect = minAreaRect(contours[i]);

            int m_width = rect.boundingRect().width;
            int m_height = rect.boundingRect().height;

            if (m_height > m_width * 3 || m_width > m_height * 3)
                continue;

            rects.push_back(rect);
        }


        int origin[rects.size()*4];
        int i = 0;

        for(RotatedRect rect : rects){
            Point2f P[4];
            rect.points(P);

            rectangle(temp, P[1], P[3], Scalar(0, 255, 0), 3);

            int height = (int) (P[0].y - P[2].y);
            int width = (int) (P[0].x - P[2].x);
            int new_x = (int) P[2].x;
            if (width < 0) {
                new_x = (int) P[0].x;
            }
            int new_y = (int) P[2].y;

            origin[i++] = new_x;
            origin[i++] = new_y;
            origin[i++] = width;
            origin[i++] = height;
        }
        result = (*env).NewIntArray(rects.size()*4);
        (*env).SetIntArrayRegion(result, 0, rects.size()*4, origin);
    }


    AndroidBitmap_unlockPixels(env, bitmap);
    return result;

}