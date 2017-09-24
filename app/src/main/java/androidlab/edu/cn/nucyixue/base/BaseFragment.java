package androidlab.edu.cn.nucyixue.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.avos.avoscloud.AVUser;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseFragment
 *
 * Created by dreamY on 2017/7/20.
 */

public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";

    protected  Unbinder unbinder;
    protected AVUser mAVUserFinal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(getResourcesLayout(),container,false);
        unbinder=ButterKnife.bind(this,mView);
        mAVUserFinal = AVUser.getCurrentUser();
        init(mView,savedInstanceState);
        logic();
        return mView;
    }

    protected abstract void init(View mView, Bundle mSavedInstanceState);

    protected abstract int getResourcesLayout();

    protected abstract void logic();

    public void toast(String toast,int time){
        if (time == 0){
            Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

    public void snackBar(View v,String snackBar, int time){
        if (time == 0){
            Snackbar.make(v,snackBar,Snackbar.LENGTH_SHORT)
                    .show();
        }else {
            Snackbar.make(v,snackBar,Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: "+getResourcesLayout());
    }
}
