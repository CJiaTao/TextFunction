package com.example.textfunction.pageTurning.viewPager;

import android.os.Bundle;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.textfunction.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 翻页
 */
public class ViewPagerActivity extends AppCompatActivity {

    @BindView(R.id.vp_text)
    VerticalViewPager viewPager;
    private List<String> textLst;
    private ViewPagerAdapter textAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_text);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        makeData();
        textAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        textAdapter.setUrlList(textLst);
        viewPager.setVertical(true);
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(textAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("TAG", "当前页面索引" + position);
                int count = textAdapter.getCount();
                if (position == count - 2) {
                    Log.e("TAG", "已经是倒数第二页");
                    List<String> lst = new ArrayList<>();
                    for (int i = 0; i < 8; i++) {
                        lst.add("Fragment 文本" + count+i);
                    }
                    textLst.addAll(lst);
                    textAdapter.setUrlList(textLst);
                    textAdapter.notifyDataSetChanged();
//                    textAdapter.instantiateItem(viewPager,count);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void makeData() {
        textLst = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            textLst.add("Fragment 文本" + i);
        }
    }
}
