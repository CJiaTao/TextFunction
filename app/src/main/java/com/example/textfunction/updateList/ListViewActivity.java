package com.example.textfunction.updateList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.textfunction.service.GroundService;
import com.example.textfunction.R;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    private ArrayList<MyListItem> list = null;
    private ListView lv;
    private MyListAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);


        initData();
        lv = (ListView) findViewById(R.id.listView1);
        adapter1 = new MyListAdapter(list, getApplicationContext());
        adapter1.setListView(lv);
        lv.setAdapter(adapter1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取listview中点击item的数据
                MyListItem item = (MyListItem) parent.getItemAtPosition(position);
                Log.e("eee", item.getData() + " == " + item.getPosition());
                // 更新数据
                item.setData("update item " + position);
                // 更新界面
                adapter1.updateItemData(item);
            }
        });
    }

    private void initData() {
        list = new ArrayList<MyListItem>();
        for (int i = 0; i < 20; i++) {
            MyListItem item = new MyListItem();
            item.setData("item " + i);
            item.setPosition(i);
            list.add(item);
        }
    }

    public void startService(View view) {
        Intent intent=new Intent(this, GroundService.class);
        startService(intent);
    }

    /**
     * 自定义item数据类型
     */
    class MyListItem {
        /**
         * 数据id
         */
        private int dataId;
        /**
         * 数据
         */
        private String data;

        public int getPosition() {
            return dataId;
        }

        public void setPosition(int position) {
            this.dataId = position;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

    }
}
