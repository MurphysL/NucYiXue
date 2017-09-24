package androidlab.edu.cn.nucyixue.ui.teachPack.reward;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.base.BaseFragment;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class RewardFragment extends BaseFragment {


    @BindView(R.id.xuanshang_recyclerview)
    RecyclerView mXuanshangRecyclerview;
    @BindView(R.id.xuanshang_progressbar)
    ProgressBar mXuanshangProgressbar;
    @BindView(R.id.xuanshang_add_floatingActionButton)
    FloatingActionButton mXuanshangAddFloatingActionButton;

    public static RewardFragment getInstance() {
        return new RewardFragment();
    }


    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
        mXuanshangProgressbar.setVisibility(View.GONE);

    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_xuanshang;
    }

    @Override
    protected void logic() {
    }
    @OnClick(R.id.xuanshang_add_floatingActionButton)
    public void onViewClicked() {
        Bundle mBundle = new Bundle();
        Intent mIntent = new Intent(getContext(),XuanshangSendActivity.class);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}
