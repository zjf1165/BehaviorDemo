package com.zjf.behaviordemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_layout);

        RecyclerView mRecyclerView = findViewById(R.id.recycleView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new MyItemDecoration());

        MyAdapter adapter = new MyAdapter(this);
        mRecyclerView.setAdapter(adapter);

        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add("item" + i);
        }

        adapter.setData(datas);
    }

    static class MyItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            RecyclerView.LayoutManager manager = parent.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {

                int position = parent.getChildAdapterPosition(view);

                if (position == manager.getItemCount() - 1) {
                    //最后一条数据
                    outRect.set(0, 30, 0, 30);
                } else {
                    outRect.set(0, 30, 0, 0);
                }

            }
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private int[] mColors;
        private List<String> mData;
        private LayoutInflater mLayoutInflater;

        MyAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mColors = new int[]{Color.parseColor("#33FF0000"), Color.parseColor("#3300FF00"), Color.parseColor("#330000FF")};
        }

        @androidx.annotation.NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup viewGroup, int position) {

            return new MyViewHolder(mLayoutInflater.inflate(R.layout.item_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull MyViewHolder holder, int i) {

            holder.setText(mData.get(i));
            holder.setBackgroundColor(mColors[i % 3]);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        void setData(List<String> list) {
            mData = list;
            notifyDataSetChanged();
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        MyViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_tv);
        }

        void setText(String text) {
            mTextView.setText(text);
        }

        public void setBackgroundColor(int color) {
            mTextView.setBackgroundColor(color);
        }
    }
}