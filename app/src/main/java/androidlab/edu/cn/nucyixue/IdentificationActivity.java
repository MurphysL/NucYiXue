package androidlab.edu.cn.nucyixue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.List;

import androidlab.edu.cn.nucyixue.utils.JNIUtils;

/**
 * IdentificationActivity
 *
 * Created by MurphySL on 2017/9/28.
 */

public class IdentificationActivity extends AppCompatActivity {
    private static final String TAG = "IActivity";

    public static final String MODEL_FILE = "file:///android_asset/handwriting.pd"; //asserts目录下的pb文件名字
    public static final String INPUT_NODE = "image_batch";       //输入节点的名称
    public static final String OUTPUT_NODE = "predicted";  //输出节点的名称
    public static final String KEEP_PROB = "keep_prob"; // 下降速率
    public static final char[] c = {'代','件','作','原','向','字','学','对','工','序','库','性','成','据','据','操','散','数','数','数','数','数','机','机','构','法','理','理','电','离','程','程','算','算','算','系','系','线','组','结','络','统','统','编','网','计','计','计','设','译','象','路','软','面'};

    private ImageView im;

    private int num = 0;
    private List<Bitmap> list = new ArrayList();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final int t = msg.what;
            if(t < num)
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TensorFlowInferenceInterface inferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_FILE);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inScaled = false;
                        Bitmap origin = list.get(t);
                        Bitmap bitmap = zoomImage(origin, 64, 64);

                        float[]result = bitmapToFloatArray(bitmap);

                        Trace.beginSection("feed");
                        inferenceInterface.feed(INPUT_NODE, result, 1, 64, 64, 1);

                        Trace.beginSection("feed");
                        float[] keep_prob = {1.0f};
                        inferenceInterface.feed(KEEP_PROB, keep_prob, 1, 1);
                        //5 9 6 8
                        Trace.beginSection("run");
                        String[] outputNames = new String[]{OUTPUT_NODE};
                        inferenceInterface.run(outputNames);

                        float[] outputs = new float[50];
                        Trace.beginSection("fetch");
                        inferenceInterface.fetch(OUTPUT_NODE, outputs);

                        Log.i(TAG, argmax(outputs) + " ");
                        Log.i(TAG, "result : " + argmax(outputs));
                        handler.sendEmptyMessage(t + 1);
                    }
                }).start();
            Log.i(TAG, msg.what + " ");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("path");
        //Bitmap origin = BitmapFactory.decodeFile(path);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.test11, options);
        options.inSampleSize = calculateInSampleSize(options, 500, 500);
        options.inJustDecodeBounds = false;

        Bitmap origin ;

        origin = BitmapFactory.decodeResource(getResources(), R.drawable.test11, options);

        initView();
        im.setImageBitmap(origin);
        initData(origin);
    }

    private void initView() {
        im = (ImageView) findViewById(R.id.img);
    }

    private void initData(Bitmap origin) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap2 = zoomImage(origin, 300, 300);

        JNIUtils utils = new JNIUtils();
        int[] result = utils.getEdge(bitmap2);
        Log.i(TAG, result.length+" ");
        num = result.length / 4;
        for(int i : result){
            Log.i(TAG, "result : "+ i);
        }

        for(int i = 0 ;i < result.length ;i += 4){
            Bitmap o = origin;
            Bitmap t = zoomImage(o, 300, 300);
            final Bitmap sub = Bitmap.createBitmap(t, result[i], result[i+1], Math.abs(result[i+2]), Math.abs(result[i+3]));
            list.add(sub);
        }

        handler.sendEmptyMessage(0);
    }

    private Bitmap zoomImage(Bitmap bitmap, int newWidth, int newHeight){
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        Matrix matrix = new Matrix();
        float scaleWidth = (float)newWidth/(float)width;
        float scaleHeight = (float)newHeight/(float)height;
        matrix.preScale(scaleWidth, scaleHeight);
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0 ,width, height, matrix, true);
        return bitmap1;
    }

    public static int argmax(float[] prob){
        int result = 0;
        for(int i=0;i<prob.length;i++) {
            if (prob[result] < prob[i]) {
                result = i;
            }
        }
        return result;
    }

    /**
     * 将bitmap转为（按行优先）一个float数组。其中的每个像素点都归一化到0~1之间。
     * @param bitmap 灰度图，r,g,b分量都相等。
     * @return
     */
    public static float[] bitmapToFloatArray(Bitmap bitmap){
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float[] result = new float[height*width];
        //float[][] result = new float[height][width];
        // float[][][][] conv = new float[1][64][64][1];
        Log.i(TAG,"bitmap width:"+width+",height:"+height);
        Log.i(TAG,"bitmap.getConfig():"+bitmap.getConfig());

        int k = 0;

        //行优先
        for(int j = 0;j < height;j++){
            for (int i = 0;i < width;i++){
                int argb = bitmap.getPixel(i,j);

                int r = Color.red(argb);
                int g = Color.green(argb);
                int b = Color.blue(argb);
                int a = Color.alpha(argb);

                //由于是灰度图，所以r,g,b分量是相等的。
                assert(r==g && g==b);

                //result[j][i] = r/255.0f;
                result[k ++] = r/255.0f;
            }
        }
        return result;

      /*  for(int i = 0 ;i < result.length ;i ++){
            for(int j = 0 ;j < result[0].length ;j ++){
                conv[0][i][j][0] = result[i][j];
            }
        }

        for(int i = 0 ;i < conv.length ;i ++){
            for(int j = 0 ;j < conv[0].length ;j ++){
                for(int n = 0 ;n < conv[0][0].length;n ++){
                    for(int m = 0 ;m < conv[0][0][0].length ;m ++){
                        System.out.println(conv[i][j][n][m] + " ");
                    }
                    System.out.println();
                }
                System.out.println();
            }
            System.out.println();
        }

        return conv;*/
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
