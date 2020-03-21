package com.example.textfunction.pageTurning.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textfunction.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.VideoViewHolder> {

    private VideoViewHolder viewHolder;
    private LayoutInflater inflater;
    private Context context;
    private List<String> lst;

    public PagerAdapter(List<String> list, Context context) {
        this.lst = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = inflater.inflate(R.layout.fm_text, parent, false);
        viewHolder = new VideoViewHolder(root);

//        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
//        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.tv_title.setText("第" + position + "个视频");
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }


    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvText)
        public TextView tv_title;

        public VideoViewHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this, rootView);
        }

    }
}
