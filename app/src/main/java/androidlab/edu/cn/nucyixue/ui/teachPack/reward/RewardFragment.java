package androidlab.edu.cn.nucyixue.ui.teachPack.reward;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.base.AnimCommonAdapter;
import androidlab.edu.cn.nucyixue.base.BaseFragment;
import androidlab.edu.cn.nucyixue.data.bean.Reward;
import butterknife.BindView;
import butterknife.OnClick;

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

    private List<Reward> list = new ArrayList<>();
    private AnimCommonAdapter<Reward> adapter;

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
        mXuanshangRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnimCommonAdapter<Reward>(getContext(), R.layout.item_reward, list) {
            @Override
            protected void convert(ViewHolder holder, Reward reward, int position) {
                if(reward != null && holder != null){
                    //holder.setText(R.id.name, )
                }
            }
        };
        mXuanshangRecyclerview.setAdapter(adapter);

    }


    @OnClick(R.id.xuanshang_add_floatingActionButton)
    public void onViewClicked() {
        Bundle mBundle = new Bundle();
        Intent mIntent = new Intent(getContext(),XuanshangSendActivity.class);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }
}
