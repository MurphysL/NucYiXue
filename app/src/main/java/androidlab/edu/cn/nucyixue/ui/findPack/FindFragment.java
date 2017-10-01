package androidlab.edu.cn.nucyixue.ui.findPack;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.base.BaseFragment;
import androidlab.edu.cn.nucyixue.data.bean.Live;
import androidlab.edu.cn.nucyixue.data.bean.Subject;
import androidlab.edu.cn.nucyixue.net.AVService;
import androidlab.edu.cn.nucyixue.ui.common.live.LiveFragment;
import androidlab.edu.cn.nucyixue.ui.findPack.subject.SubjectContentActivity;
import androidlab.edu.cn.nucyixue.utils.FlexTextUtil;
import androidlab.edu.cn.nucyixue.utils.config.LiveFragmentType;
import androidlab.edu.cn.nucyixue.utils.config.LiveType;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import io.reactivex.functions.Consumer;

/**
 * FindFragment
 *
 * Update on 2017.10.1
 *
 * A simple {@link Fragment} subclass.
 */
public class FindFragment extends BaseFragment {
    private static final String TAG = "FindFragment";

    @BindView(R.id.banner_guide_content)
    BGABanner mBannerGuideContent;
    @BindView(R.id.flexsubject)
    FlexboxLayout mFlexSubject;
    @BindView(R.id.find_search_by_text)
    RelativeLayout mFindSearchByText;
    @BindView(R.id.type_recycler)
    RecyclerView typeRecycler;
    @BindView(R.id.image_left1)
    ImageView imageLeft1;
    @BindView(R.id.image_left2)
    ImageView imageLeft2;

    private int keywordDisplayNum = 0;


    public static FindFragment getInstance() {
        return new FindFragment();
    }

    @Override
    protected int getResourcesLayout() {
        return R.layout.fragment_find;
    }


    @Override
    protected void init(View mView, Bundle mSavedInstanceState) {
        initBanner();

        initRecyclerView();

        initFragment();
    }

    private void initFragment() {
        Fragment fragment = new LiveFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LiveFragmentType.getLIVE_FRAGMENT_TYPE(), LiveFragmentType.getHOT());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.content_find, fragment).commit();
    }

    private void initRecyclerView() {
        List<LiveType> types = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            types.add(LiveType.toList().get(i));


        CommonAdapter<LiveType> adapter = new CommonAdapter<LiveType>(getContext(), R.layout.item_type, types) {
            @Override
            protected void convert(ViewHolder holder, final LiveType liveType, int position) {
                holder.setImageResource(R.id.type_icon, liveType.getIcon());
                holder.setText(R.id.type_name, liveType.getValue());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString("subjectName", liveType.getValue());
                        Intent mIntent = new Intent(getContext(), SubjectContentActivity.class);
                        mIntent.putExtras(mBundle);
                        startActivity(mIntent);
                    }
                });
            }
        };

        typeRecycler.setAdapter(adapter);
        typeRecycler.setLayoutManager(new GridLayoutManager(getContext(), 5));
    }

    private void initBanner() {
        mBannerGuideContent.setAdapter(new BGABanner.Adapter<ImageView, Integer>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, Integer model, int position) {
                Glide.with(getContext())
                        .load(model)
                        .placeholder(R.drawable.hold)
                        .error(R.drawable.hold)
                        .centerCrop()
                        .dontAnimate()
                        .into(itemView);
            }

        });

        mBannerGuideContent.setData(Arrays.asList(R.drawable.live, R.drawable.xuanshang, R.drawable.xianxia), Arrays.asList("", "", ""));
        mBannerGuideContent.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {

            }
        });
    }


    @Override
    protected void logic() {
        fetchKeywordData();
    }

    private void fetchKeywordData() {
        AVService.INSTANCE.queryKeywordsLive().subscribe(
                new Consumer<Live>() {
                    @Override
                    public void accept(Live live) throws Exception {
                        List<String> list = live.getKeyword();
                        if(keywordDisplayNum <= 10 && list != null && !list.isEmpty()){
                            displayFlexSubject(live.getKeyword());
                            keywordDisplayNum += list.size();
                        }
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "GET KEYWORD ERROR : " + throwable.toString());
                        toast("GET KEYWORD ERROR !");
                    }
                }
        );
    }


    private void displayFlexSubject(List<String> keywords) {
        if(mFlexSubject != null)
            for (int i = 0; i < keywords.size(); i++) {
                Subject model = new Subject();
                model.setId(i);
                model.setName(keywords.get(i));
                mFlexSubject.addView(createNewFlexItemTextView(model));
            }
    }

    private TextView createNewFlexItemTextView(final Subject book) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setText(book.getName());
        textView.setTextSize(15);
        textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        textView.setBackgroundResource(R.drawable.shape_back);
        textView.setTag(book.getId());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, book.getName());
                Bundle mBundle = new Bundle();
                mBundle.putString("subjectName", book.getName());
                mBundle.putInt("subjectId", book.getId());
                Intent mIntent = new Intent(getContext(), SubjectContentActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
        int padding = FlexTextUtil.dpToPixel(getContext(), 3);
        int paddingLeftAndRight = FlexTextUtil.dpToPixel(getContext(), 4);
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight + 4, padding, paddingLeftAndRight + 4, padding);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = FlexTextUtil.dpToPixel(getContext(), 4);
        int marginTop = FlexTextUtil.dpToPixel(getContext(), 8);
        layoutParams.setMargins(margin + 10, marginTop, margin, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    @OnClick(R.id.find_search_by_text)
    public void search() {
        Intent mIntent = new Intent(getContext(), FindSearchActivity.class);
        startActivity(mIntent);
    }

}


