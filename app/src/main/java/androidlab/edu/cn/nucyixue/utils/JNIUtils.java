package androidlab.edu.cn.nucyixue.utils;

/**
 * Created by MurphySL on 2017/9/18.
 */

public class JNIUtils {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public native int[] getEdge(Object bitmap);
}
