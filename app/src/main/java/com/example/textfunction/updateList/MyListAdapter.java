package com.example.textfunction.updateList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.textfunction.R;

import java.util.ArrayList;

public class MyListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ListViewActivity.MyListItem> dataListView;
    private ListView listView2;

    public MyListAdapter(ArrayList<ListViewActivity.MyListItem> list, Context applicationContext) {
        this.context = applicationContext;
        this.dataListView = list;
    }

    /**
     * 设置ListView的对象
     *
     * @param lv
     */
    public void setListView(ListView lv) {
        this.listView2 = lv;
    }

    /**
     * update listview 单条数据
     *
     * @param item 新数据对象
     */
    public void updateItemData(ListViewActivity.MyListItem item) {
        Message msg = Message.obtain();
        int ids = -1;
        // 进行数据对比获取对应数据在list中的位置
        for (int i = 0; i < dataListView.size(); i++) {
            if (dataListView.get(i).getPosition() == item.getPosition()) {
                ids = i;
            }
        }
        msg.arg1 = ids;
        // 更新mDataList对应位置的数据
        dataListView.set(ids, item);
        // handle刷新界面
        han.sendMessage(msg);
    }
    @SuppressLint("HandlerLeak")
    private Handler han = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            updateItem(msg.arg1);
        };
    };
    /**
     * 刷新指定item
     *
     * @param index item在listview中的位置
     */
    private void updateItem(int index)
    {
        if (listView2 == null)
        {
            return;
        }

        // 获取当前可以看到的item位置
        int visiblePosition = listView2.getFirstVisiblePosition();
        // 如添加headerview后 firstview就是hearderview
        // 所有索引+1 取第一个view
        // View view = listview.getChildAt(index - visiblePosition + 1);
        // 获取点击的view
        View view = listView2.getChildAt(index - visiblePosition);
        TextView txt = (TextView) view.findViewById(R.id.textView1);
        // 获取mDataList.set(ids, item);更新的数据
        ListViewActivity.MyListItem data = (ListViewActivity.MyListItem) getItem(index);
        // 重新设置界面显示数据
        txt.setText(data.getData());
    }

    @Override
    public int getCount() {
        return dataListView.size();
    }

    @Override
    public Object getItem(int position) {
        return dataListView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
        }
        TextView txt = (TextView) convertView.findViewById(R.id.textView1);
        txt.setText(dataListView.get(position).getData());
        return convertView;
    }
}
