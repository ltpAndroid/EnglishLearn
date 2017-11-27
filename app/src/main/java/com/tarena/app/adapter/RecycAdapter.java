package com.tarena.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tarena.app.R;
import com.tarena.app.entity.Store;
import com.tarena.app.entity.User;

import java.util.List;

/**
 * Created by tarena on 2017/7/24.
 */

public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.MyViewHolder> {

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

   private Context context;

   private List<Store> storeList;

    public RecycAdapter(Context context, List<Store> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        1.如果root为null，attachToRoot将失去作用，设置任何值都没有意义。同时这个布局的最外层参数就没有效了
//        2.如果root不为null，attachToRoot设为false，则会将布局文件最外层的所有layout属性进行设置，
//        当该view被添加到父view当中时，这些layout属性会自动生效。
//        3.如果root不为null，attachToRoot设为true，则会给加载的布局文件的指定一个父布局，即root。
        View view = LayoutInflater.from(
                context).inflate(R.layout.item_store_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Store store = storeList.get(position);

        User user = store.getUser();

        holder.tvTitle.setText(store.getTitle());

        Picasso.with(context).load(store.getImagePaths().get(0)).into(holder.iv);


        holder.tvMoney.setText("金币:"+store.getMoney());

        holder.tvScore.setText("积分:"+store.getScore());

        holder.tvUpLoader.setText(user.getNick()+"老师");

        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.rlRoot, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return storeList == null ? 0 : storeList.size();
    }




    class MyViewHolder extends RecyclerView.ViewHolder
    {

        ImageView iv;
        TextView tvTitle;
        TextView tvMoney;
        TextView tvScore;
        TextView tvUpLoader;
        RelativeLayout rlRoot;

        //创建时 需要传入 父视图
        public MyViewHolder(View view)
        {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.item_tv_title);
            iv = (ImageView) view.findViewById(R.id.item_iv_store);
            tvMoney = (TextView) view.findViewById(R.id.item_tv_money);
            tvScore = (TextView) view.findViewById(R.id.item_tv_score);
            tvUpLoader = (TextView) view.findViewById(R.id.item_tv_uploader);
            rlRoot = (RelativeLayout) view.findViewById(R.id.rl_root);
        }
    }
}
