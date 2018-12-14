package woo.mikasa.lib.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by mikasacos on 2018/9/7.
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter {
    protected List<T>mDataList=new ArrayList<>();
    private OnRvItemClickListener mListener;
    //分页加载，追加数据
    public void appendData(List<T>dataList){
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }
    public void refreshData(List<T>dataList){
        if (mDataList!=null){
            mDataList.clear();
        }
        appendData(dataList);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    public void setOnRvItemClickListener(OnRvItemClickListener listener){
        this.mListener=listener;
    }
    //abstract viewHolder
    public abstract class BaseRvViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public BaseRvViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);//itemView
            //itemView.setOnLongClickListener(this);//itemView
        }
        protected abstract void bindView(T t);//bindView

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                //ViewHolder的getLayoutPosition()
                mListener.onItemClick(getLayoutPosition());//getLayoutPosition()，mDataList从0开始
            }
        }
    }
    public interface OnRvItemClickListener{
        void onItemClick(int pos);
    }

}
