package androidlab.edu.cn.nucyixue.ui.findPack;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.android.flexbox.FlexboxLayout;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.base.BaseActivity;
import androidlab.edu.cn.nucyixue.data.bean.Subject;
import androidlab.edu.cn.nucyixue.ocr.FileUtil;
import androidlab.edu.cn.nucyixue.ui.findPack.subject.SubjectContentActivity;
import androidlab.edu.cn.nucyixue.ui.findPack.zxing.MipcaActivityCapture;
import androidlab.edu.cn.nucyixue.utils.FlexTextUtil;
import butterknife.BindView;
import butterknife.OnClick;

public class FindSearchActivity extends BaseActivity {


    private static final int SCANNIN_GREQUEST_CODE = 1;
    private static final int REQUEST_CODE_GENERAL = 105;
    private boolean hasGotToken = false;

    private static final String TAG = "FindSearchActivity";
    @BindView(R.id.search_edit)
    EditText mSearchEdit;
    @BindView(R.id.camera_search)
    ImageView mCameraSearch;
    @BindView(R.id.flexsubject_search)
    FlexboxLayout mFlexsubjectSearch;

    @Override
    protected void logicActivity(Bundle mSavedInstanceState) {

        initAccessTokenWithAkSk();
        String[] tags = {"Java程序设计", "计算机网络", "英语", "高等数学", "线性代数", "离散数学", "大学计算机基础"};
        for (int i = 0; i < tags.length; i++) {
            Subject model = new Subject();
            model.setId(i);
            model.setName(tags[i]);
            mFlexsubjectSearch.addView(createNewFlexItemTextView(model));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_search;
    }



    @OnClick(R.id.camera_search)
    public void onViewClicked() {
        //自定义底部弹出dialog
        final Dialog mDialog = new Dialog(this, R.style.BottomDialog);
        final View mBottom = LayoutInflater.from(mActivity).inflate(R.layout.search_bottom_dialog, null);

        TextView mTextViewCamera = mBottom.findViewById(R.id.search_camera_bottom_select);
        TextView mTextViewSelect = mBottom.findViewById(R.id.search_qr_bottom_select);
        mDialog.setContentView(mBottom);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mBottom.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - FlexTextUtil.dp2px(mActivity, 16f);
        params.bottomMargin = FlexTextUtil.dp2px(mActivity, 8f);
        mBottom.setLayoutParams(params);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mDialog.show();
        mTextViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                onClickStartCamera(mView);
                mDialog.cancel();
            }
        });
        mTextViewSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                onClickStartSelectQr(mView);
                mDialog.cancel();
            }
        });
    }
    private void onClickStartSelectQr(View mView) {
        this.startActivityForResult(new Intent(this, MipcaActivityCapture.class), SCANNIN_GREQUEST_CODE);
    }
    private void onClickStartCamera(View mView) {
        if(!checkTokenStatus()){
            return;
        }

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(this).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
        this.startActivityForResult(intent, REQUEST_CODE_GENERAL);

    }
    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(this, "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.i(TAG, "AK，SK方式获取token失败"+error.getMessage());
            }
        },this, "AL2QSX22moztT8ir6GsW0cc6", "agAiIP7f4ydgSkpGa92fycEGSe742TG0");
    }
    private TextView createNewFlexItemTextView(final Subject book) {
        TextView textView = new TextView(this);
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
                mBundle.putString("subjectName",book.getName());
                mBundle.putInt("subjectId",book.getId());
                Intent mIntent = new Intent(FindSearchActivity.this, SubjectContentActivity.class);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });
        int padding = FlexTextUtil.dpToPixel(this, 3);
        int paddingLeftAndRight = FlexTextUtil.dpToPixel(this, 4);
        ViewCompat.setPaddingRelative(textView, paddingLeftAndRight + 4, padding, paddingLeftAndRight + 4, padding);
        FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = FlexTextUtil.dpToPixel(this, 4);
        int marginTop = FlexTextUtil.dpToPixel(this, 8);
        layoutParams.setMargins(margin + 10, marginTop, margin, 0);
        textView.setLayoutParams(layoutParams);
        return textView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, requestCode + " " + resultCode);
    }
}
