package woo.mikasa.xrv;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class XXRecyclerView extends RecyclerView {
    private Context mContext=this.getContext();
    private View mHeaderView;
    private View mFooterView;
    private View mEmptyView;
    private XAdapter mAdapter;
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoadMoreEnable=false;
    private boolean mIsLoadingMore=false;
    private int limitedNumberToCallLoadMore=1;
    private static int lastVisibleItemPosition;
    public XXRecyclerView(Context context) {
        this(context, null);
    }

    public XXRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XXRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void refresh(){
        View view=LayoutInflater.from(this.getContext()).inflate(R.layout.layout_header,this,false);
        DefaultItemAnimator animator=new DefaultItemAnimator();
        //animator.setRemoveDuration(2000);
        this.setItemAnimator(animator);
        this.addHeaderView(view);
    }
    public void refreshComplete(){
        removeHeaderView();
    }
    public void setLimitedNumberToCallLoadMore(int num){
        this.limitedNumberToCallLoadMore=num;
    }
    public void addHeaderView(View view){
        if (view!=null&&mHeaderView==null){
            mHeaderView=view;
            mAdapter.notifyItemInserted(0);
            this.scrollToPosition(0);
        }
    }
    public void removeHeaderView(){
        if (mHeaderView!=null){
            mHeaderView=null;
            mAdapter.notifyItemRemoved(0);
        }
    }
    public void addFooterView(View view){
        if (view!=null&&mFooterView==null){
            mFooterView=view;
            mAdapter.notifyItemInserted(mAdapter.getItemCount()-1);
        }
    }
    public void removeFooterView(){
        if (mFooterView!=null){
            mFooterView=null;
            mAdapter.notifyItemRemoved(mAdapter.getItemCount()-1);
        }
    }

    public void setEmptyView(View view){
        if (view!=null){
            mEmptyView=view;
            mAdapter.notifyDataSetChanged();
        }
    }
    public void showLoadingView(){
        View footerView=LayoutInflater.from(mContext).inflate(R.layout.layout_footer,this,false);
        addFooterView(footerView);
    }
    public void noMore(){
        View noMoreView=LayoutInflater.from(mContext).inflate(R.layout.layout_no_more,this,false);
        addFooterView(noMoreView);
    }
    public void setOnLoadMoreListener(OnLoadMoreListener listener){
        this.mLoadMoreListener=listener;
        mIsLoadMoreEnable=true;
        this.addOnScrollListener(scrollListener);//addOnScrollListener
    }
    /**
     * 重写adapter，一定要先删掉原来的super
     * 后面需要继承再super,不知为何
     * @param adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter != null) {
            mAdapter = new XAdapter(adapter);
        }
        super.setAdapter(mAdapter);
    }
    public void loadMoreComplete(){
        removeFooterView();
        mIsLoadingMore=false;
        this.scrollToPosition(lastVisibleItemPosition+limitedNumberToCallLoadMore);
    }
    private class XAdapter extends Adapter<ViewHolder>{
        private int ITEM_TYPE_NORMAL = 0;
        private int ITEM_TYPE_HEADER = 1;
        private int ITEM_TYPE_FOOTER = 2;
        private int ITEM_TYPE_EMPTY = 3;
        private Adapter innerAdapter;
        public XAdapter(Adapter adapter){
            innerAdapter=adapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                return new XViewHolder(mHeaderView);
            } else if (viewType == ITEM_TYPE_EMPTY) {
                return new XViewHolder(mEmptyView);
            } else if (viewType == ITEM_TYPE_FOOTER) {
                return new XViewHolder(mFooterView);
            }  else {
                return innerAdapter.onCreateViewHolder(parent, viewType);
            }
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == ITEM_TYPE_HEADER || type == ITEM_TYPE_FOOTER || type == ITEM_TYPE_EMPTY) {
                return;
            }
            int realPosition = getRealItemPosition(position);
            innerAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            int itemCount = innerAdapter.getItemCount();
            if (null != mHeaderView) itemCount++;
            if (null != mFooterView) itemCount++;
            if (null != mEmptyView && itemCount == 0) {
                itemCount++;
                return itemCount;
            }
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            if (null != mHeaderView && position == 0) {
                return ITEM_TYPE_HEADER;
            }
            if (null != mFooterView && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
            if (null != mEmptyView && innerAdapter.getItemCount() == 0) {
                return ITEM_TYPE_EMPTY;
            }
            return ITEM_TYPE_NORMAL;
        }

        private int getRealItemPosition(int position) {
            if (null != mHeaderView) {
                return position - 1;
            }
            return position;
        }
        class XViewHolder extends ViewHolder{
            XViewHolder(View itemView){
                super(itemView);
            }
        }
    }
    private OnScrollListener scrollListener=new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (mIsLoadMoreEnable&&!mIsLoadingMore&&dy>0){
                if (findLastVisibleItemPosition()==mAdapter.getItemCount()-limitedNumberToCallLoadMore){
                    lastVisibleItemPosition=findLastVisibleItemPosition();
                    mIsLoadingMore=true;
                    showLoadingView();
                    mLoadMoreListener.onLoadMore();
                }
            }
        }
    };
    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    private int findLastVisibleItemPosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }
    private int findMaxPosition(int[] positions) {
        int maxPosition = 0;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }
}
