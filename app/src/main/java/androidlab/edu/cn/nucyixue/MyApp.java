package androidlab.edu.cn.nucyixue;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

import androidlab.edu.cn.nucyixue.data.bean.LU;
import androidlab.edu.cn.nucyixue.data.bean.Live;
import androidlab.edu.cn.nucyixue.data.bean.Reward;
import androidlab.edu.cn.nucyixue.data.bean.UserInfo;
import c.b.BP;

/**
 * MyApp
 *
 * Created by dreamY on 2017/7/24.
 */
public class MyApp extends Application {

    public static final Boolean isDebug = true;
    //public static final SensitiveFilter filter = new SensitiveFilter();

    Intent intent;
    int resultCode;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        AVObject.registerSubclass(Live.class);
        AVObject.registerSubclass(UserInfo.class);
        AVObject.registerSubclass(LU.class);
        AVObject.registerSubclass(Reward.class);

        AVOSCloud.initialize(this,"O5aEuqARNjtbvT2tGTW23bB5-gzGzoHsz","XMaxhc0a9L5cDOIAXrBeqoS8");

        AVOSCloud.setDebugLogEnabled(isDebug);

       /* filter.addWord("色情");
        filter.addWord("反动");
        filter.addWord("江泽民");
*/
        BP.init("e9aba613dc365a00e3508de5fbf105a7");
    }


    public MediaProjectionManager getMediaProjectionManager() {
        return mMediaProjectionManager;
    }

    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager) {
        this.mMediaProjectionManager = mMediaProjectionManager;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

}
