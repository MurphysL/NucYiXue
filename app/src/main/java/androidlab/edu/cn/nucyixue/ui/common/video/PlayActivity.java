package androidlab.edu.cn.nucyixue.ui.common.video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidlab.edu.cn.nucyixue.MyApp;
import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.data.bean.Live;
import androidlab.edu.cn.nucyixue.utils.config.LCConfig;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bigkoo.pickerview.view.WheelTime.dateFormat;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_BEGIN;
import static com.tencent.rtmp.TXLiveConstants.PLAY_EVT_PLAY_END;

public class PlayActivity extends AppCompatActivity {
    private static final String TAG = "PlayActivity";

    @BindView(R.id.live_play)
    TXCloudVideoView mCaptView;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.change_state)
    Button changeState;

    private TXLivePlayer mLivePlayer;
    private String flvUrl;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;
    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager1 = null;
    private WindowManager mWindowManager1 = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private String strDate;
    private String pathImage;
    private String nameImage;

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private ImageView iv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);

        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) getDrawable(R.drawable.anim_path);
        iv.setImageDrawable(drawable);
        if (drawable != null) {
            drawable.start();
        }

        init();
        createVirtualEnvironment();

        iv1 = (ImageView) findViewById(R.id.snap);
    }

    /*
       播放地址 (RTMP)：	rtmp://10305.liveplay.myqcloud.com/live/10305_d716621c0f
       播放地址 (FLV)：	http://10305.liveplay.myqcloud.com/live/10305_d716621c0f.flv
       播放地址 (HLS)：	http://10305.liveplay.myqcloud.com/live/10305_d716621c0f.m3u8
     */
    private void init() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Live live = bundle.getParcelable(LCConfig.getLIVE_TABLE());
            if (live != null) {
                //mPlayerView即step1中添加的界面view
                //创建player对象
                mLivePlayer = new TXLivePlayer(this);
                //关键player对象与界面view
                mLivePlayer.setPlayerView(mCaptView);
                //当前知道的主播的id
                String url = live.getObjectId();
                flvUrl = "http://10305.liveplay.myqcloud.com/live/10305_" + url + ".flv";
                Log.i(TAG, "onCreate: " + flvUrl);
                mLivePlayer.startPlay(flvUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
                mLivePlayer.setRenderMode(0);
                mLivePlayer.setPlayListener(new ITXLivePlayListener() {
                    @Override
                    public void onPlayEvent(int i, Bundle bundle) {
                        switch (i) {
                            case PLAY_EVT_PLAY_BEGIN:
                                iv.setVisibility(View.GONE);
                                changeState.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                                break;
                            case PLAY_EVT_PLAY_END:
                                iv.setVisibility(View.VISIBLE);
                                changeState.setBackgroundResource(R.drawable.ic_play_circle_outline_black_24dp);
                        }
                    }

                    @Override
                    public void onNetStatus(Bundle bundle) {

                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true); // true代表清除最后一帧画面
        mCaptView.onDestroy();
        tearDownMediaProjection();
    }

    @OnClick(R.id.change_state)
    public void onViewClicked() {
        if (mLivePlayer.isPlaying()) {
            mLivePlayer.pause();
        } else {
            mLivePlayer.resume();
        }
    }

    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CHINA);
        strDate = dateFormat.format(new Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager1 = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager1 = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = mWindowManager1.getDefaultDisplay().getWidth();
        windowHeight = mWindowManager1.getDefaultDisplay().getHeight();
        metrics = new DisplayMetrics();
        mWindowManager1.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        Log.i(TAG, "width :"+ windowWidth + "height" + windowHeight);
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        startActivityForResult(mMediaProjectionManager1.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    @OnClick(R.id.screen_shot)
    public void onScreenshotViewClicked() {
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                //start virtual
                startVirtual();
            }
        }, 500);

        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                //capture the screen
                startCapture();
            }
        }, 1500);
    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void virtualDisplay() {
        if(mMediaProjection != null)
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                    windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
    }

    public void setUpMediaProjection() {
        mResultData = ((MyApp) getApplication()).getIntent();
        mResultCode = ((MyApp) getApplication()).getResultCode();
        //mMediaProjectionManager1 = ((MyApp) getApplication()).getMediaProjectionManager();
        mMediaProjection = mMediaProjectionManager1.getMediaProjection(mResultCode, mResultData);
    }

    private void startCapture() {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";
        Image image = mImageReader.acquireLatestImage();
        Image im = mImageReader.acquireNextImage();
        if(image == null){
            Log.i(TAG, "123");
        }
        if(im == null)
            Log.i(TAG, "234");
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        image.close();

        if (bitmap != null) {
            try {
                File fileImage = new File(nameImage);
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
                Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(fileImage);
                media.setData(contentUri);
                this.sendBroadcast(media);

                Bitmap b = BitmapFactory.decodeFile(nameImage);
                iv1.setImageBitmap(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
               Log.i(TAG, "123");
            }
        }
    }
}
