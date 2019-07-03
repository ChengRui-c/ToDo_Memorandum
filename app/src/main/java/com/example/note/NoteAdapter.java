package com.example.note;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private Context mContext;
    private List<OneNote> mNoteList;
    int[] tag_color={R.drawable.tag_yellow,R.drawable.tag_blue,R.drawable.tag_green,R.drawable.tag_red,R.drawable.tag_white};

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView tag;//cardview的标签图片
        ImageView alarm;//cardview的闹钟图片
        TextView mainText;//cardview的内容
        TextView textDate;//cardview的日期
        TextView textTime;//cardview的时间

        public ViewHolder(View view) {//view是RecyclerView子项(CardView)的最外层布局
            super(view);
            cardView = (CardView) view;
            tag = (ImageView) view.findViewById(R.id.tag);
            alarm = (ImageView) view.findViewById(R.id.alarm);
            mainText = (TextView) view.findViewById(R.id.mainText);
            textDate = (TextView) view.findViewById(R.id.textDate);
            textTime = (TextView) view.findViewById(R.id.textTime);
        }
    }

    /**
     * 把要展示的数据源传进来
     */
    public NoteAdapter(List<OneNote> noteList) {
        mNoteList = noteList;
    }

    /**
     * 加载布局cardView，以及布局中的事件
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_card,
                parent, false);

        return new ViewHolder(view);
    }


    /**
     * 对cardView的内容赋值
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        final OneNote oneNote=mNoteList.get(position);
        holder.mainText.setText(oneNote.getMainText());
        holder.textDate.setText(oneNote.getTextDate());
        holder.textTime.setText(oneNote.getTextTime());
        if (oneNote.getTag()<tag_color.length){
            Glide.with(mContext).load(tag_color[oneNote.getTag()]).into(holder.tag);
        }
        if (oneNote.getAlarm()){
            //有提醒就设置显示
            holder.alarm.setVisibility(View.VISIBLE);
        }
        else {
            holder.alarm.setVisibility(View.GONE);
        }

        if (mItemClickListener!=null){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 这里利用回调来给RecyclerView设置点击事件
                    mItemClickListener.onItemClick(position);
                }
            });
        }
        if (mItemLongClickListener!=null){
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 这里利用回调来给RecyclerView设置长按事件
                    mItemLongClickListener.onItemLongClick(position);
                    return true;
                }
            });
        }


    }

    /**
     * 获取cardView的数量
     */
    @Override
    public int getItemCount() {
        return mNoteList.size();
    }


    /**
     * 定义接口
     */
    private ItemClickListener mItemClickListener;
    private ItemLongClickListener mItemLongClickListener;
    public interface ItemClickListener{
        public void onItemClick(int position);
    }
    public interface ItemLongClickListener{
        public boolean onItemLongClick(int position);
    }
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.mItemClickListener=itemClickListener;
    }
    public void setOnItemLongClickListener(ItemLongClickListener itemLongClickListener){
        this.mItemLongClickListener=itemLongClickListener;
    }
}
