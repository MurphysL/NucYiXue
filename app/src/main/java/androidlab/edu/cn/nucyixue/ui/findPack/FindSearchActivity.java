package androidlab.edu.cn.nucyixue.ui.findPack;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidlab.edu.cn.nucyixue.R;
import androidlab.edu.cn.nucyixue.base.BaseActivity;
import androidlab.edu.cn.nucyixue.data.bean.Book;
import androidlab.edu.cn.nucyixue.data.bean.Keyword;
import androidlab.edu.cn.nucyixue.data.bean.OCRResult;
import androidlab.edu.cn.nucyixue.net.Service;
import androidlab.edu.cn.nucyixue.ocr.FileUtil;
import androidlab.edu.cn.nucyixue.ocr.RecognizeService;
import androidlab.edu.cn.nucyixue.IdentificationActivity;
import androidlab.edu.cn.nucyixue.ui.common.live.LiveFragment;
import androidlab.edu.cn.nucyixue.ui.findPack.zxing.MipcaActivityCapture;
import androidlab.edu.cn.nucyixue.utils.ActivityUtils;
import androidlab.edu.cn.nucyixue.utils.FlexTextUtil;
import androidlab.edu.cn.nucyixue.utils.config.LiveFragmentType;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FindSearchActivity extends BaseActivity {
    private static final String TAG = "FindSearchActivity";

    private static final int SCANNIN_GREQUEST_CODE = 1;
    private static final int REQUEST_CODE_GENERAL = 105;
    private static final int HANDWRITING_CODE = 110;
    private boolean hasGotToken = false;


    @BindView(R.id.camera_search)
    ImageView mCameraSearch;

    @Override
    protected void logicActivity(Bundle mSavedInstanceState) {
        initAccessTokenWithAkSk();
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
        TextView mTextViewHandwriting = mBottom.findViewById(R.id.search_handwriting_bottom_select);
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
        mTextViewHandwriting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickStartHandwriting(view);
                mDialog.cancel();
            }
        });
    }

    private void onClickStartHandwriting(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, FileUtil.getSaveFile(this).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
        this.startActivityForResult(intent, HANDWRITING_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE_GENERAL:
                    fetchOCRData();
                    break;
                case HANDWRITING_CODE:
                    fetchHandWritingData();
                    break;
                case SCANNIN_GREQUEST_CODE:
                    fetchQRData(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void fetchQRData(Intent data) {
        Bundle bundle = data.getExtras();
        String result = bundle.getString("result");
        if(bundle.getString("result") != null){
            Log.i(TAG, "result:" + result);
            Service.INSTANCE.getApi_douban().getBookInfo(result)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            new Consumer<Book>() {
                                @Override
                                public void accept(Book book) throws Exception {
                                    searchKeyword(book);
                                }
                            },
                            new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Log.i(TAG, "get Book Fail :" + throwable.toString());
                                }
                            }
                    );
        }
    }

    private void fetchHandWritingData() {
        Intent intent = new Intent(FindSearchActivity.this, IdentificationActivity.class);
        Bundle b = new Bundle();
        b.putString("path", FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void fetchOCRData() {
        RecognizeService.recGeneral(FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                new RecognizeService.ServiceListener() {
                    @Override
                    public void onResult(String result) {
                        Log.i(TAG, result);
                        OCRResult ocr = new Gson().fromJson(result, OCRResult.class);
                        List<OCRResult.WordsResult> wordsResult = ocr.getWords_result();
                        StringBuilder sb = new StringBuilder();
                        for (OCRResult.WordsResult word : wordsResult){
                            sb.append(word.getWords());
                        }

                        Service.INSTANCE.getApi_keyword()
                                .getKeyword(sb.toString(), 3)
                                .observeOn(Schedulers.io())
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        new Consumer<Keyword>() {
                                            @Override
                                            public void accept(Keyword keyword) throws Exception {
                                                Log.i(TAG, "keys : " + keyword.getShowapi_res_body().getList().get(0));
                                                ArrayList<String> keys = new ArrayList<>();
                                                keys.addAll(keyword.getShowapi_res_body().getList());
                                                Bundle b = new Bundle();
                                                b.putSerializable("keys", keys);
                                                b.putString(LiveFragmentType.getLIVE_FRAGMENT_TYPE(), LiveFragmentType.getRECOMMEND());
                                                LiveFragment fragment = new LiveFragment();
                                                fragment.setArguments(b);
                                                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.content_search);
                                            }
                                        },
                                        new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                Log.i(TAG, "Get Keyword error:" + throwable.toString());
                                            }
                                        }
                                );
                    }
                });
    }

    private void searchKeyword(Book book){
        Log.i(TAG, "book : " + book.toString());
        List<Book.Tags> tags = book.getTags();
        ArrayList<String> keys = new ArrayList<>();
        for(Book.Tags tag : tags){
            keys.add(tag.getName());
            Log.i(TAG,"tag:"+ tag.getName());
        }

        Fragment fragment = new LiveFragment();
        Bundle b = new Bundle();
        b.putSerializable("keys", keys);
        b.putString(LiveFragmentType.getLIVE_FRAGMENT_TYPE(), LiveFragmentType.getRECOMMEND());
        fragment.setArguments(b);

        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), fragment, R.id.camera_search);
    }
}
