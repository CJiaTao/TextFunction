package com.example.textfunction.pageTurning.viewPager;

import android.util.Log;
import android.widget.TextView;

import com.example.textfunction.R;

import butterknife.BindView;

public class TextFragment extends BaseTextFragment {
    @BindView(R.id.tvText)
    TextView tvText;

    private String text;
    private int position;
    public static final String TEXT = "TEXT";
    public static final String POSITION = "POSITION";

    @Override
    protected int getLayoutId() {
        return R.layout.fm_text;
    }

    @Override
    protected void initView() {

        text = getArguments().getString(TEXT);
        tvText.setText(text);
        position = getArguments().getInt(POSITION);
        if(position==0){
            //TODO 执行网络请求
        }
    }

    /**
     * isVisibleToUser:表示该Fragment的UI 用户是否可见
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //TODO 执行网络请求
            Log.e("TAG", "Fragment可见时，值：" + text);

        }
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
