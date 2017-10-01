package androidlab.edu.cn.nucyixue.ui.findPack;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.ui.common.live.LiveFragment;
import androidlab.edu.cn.nucyixue.utils.ActivityUtils;
import androidlab.edu.cn.nucyixue.utils.config.LiveFragmentType;

/**
 * Created by MurphySL on 2017/9/28.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Bundle b = getIntent().getExtras();
        ArrayList<String> keys = b.getParcelable("keys");
        String type = b.getString(LiveFragmentType.getLIVE_FRAGMENT_TYPE());

        LiveFragment fragment = new LiveFragment();
        fragment.setArguments(getIntent().getExtras());
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content);
    }
}
