package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MyGedanRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.DeviceUtils;
import com.hudson.donglingmusic.utils.UIUtils;

/**
 * Created by Hudson on 2017/6/11.
 * 我的歌单一项的viewHolder
 */

public class MyGedanItemViewHolder extends BaseRecyclerViewHolder<MyGedanBean> {
    private ImageView mBg,mGedanDelete;
    private TextView mTag,mTitle;
    private String mListId = null;
    private boolean mIsDeleteShow = false;
    private String mGedanTitle;

    public MyGedanItemViewHolder(View itemView, final MyGedanRecyclerViewAdapter.OnItemClickListener listener,
                                 final MyGedanRecyclerViewAdapter.OnDeleteGedanHappenedListener deleteListener) {
        super(itemView);
        mBg = (ImageView) itemView.findViewById(R.id.iv_gedan_bg);
        mGedanDelete = (ImageView) itemView.findViewById(R.id.iv_gedan_delete);
        mGedanDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteListener!=null){
                    if(mListId!=null){
                        deleteListener.onDeleteGedan(true,mGedanTitle);
                    }else{
                        deleteListener.onDeleteGedan(false,mGedanTitle);
                    }
                }
            }
        });
        itemView.findViewById(R.id.tv_listen_count).setVisibility(View.GONE);
        mTag = (TextView) itemView.findViewById(R.id.tv_gedan_tag);
        mTitle = (TextView) itemView.findViewById(R.id.tv_gedan_title);
        View rootView = itemView.findViewById(R.id.ll_gedan_item_root);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsDeleteShow){
                    mGedanDelete.setVisibility(View.GONE);
                    mIsDeleteShow = false;
                }else{
                    if(listener!=null){
                        if(mListId!=null){
                            listener.onItemClick(true,mListId);
                        }else{
                            listener.onItemClick(false,(getLayoutPosition()-1)+"");
                        }
                    }
                }
            }
        });
        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(getAdapterPosition() != 1){
                    DeviceUtils.Vibrate(UIUtils.getContext(),200);
                    mIsDeleteShow = true;
                    mGedanDelete.setVisibility(View.VISIBLE);
                    return true;
                }else{//我的最爱歌单是不允许被删除的！
                    return false;
                }
            }
        });
    }

    @Override
    public void refreshView(MyGedanBean data) {
        mGedanDelete.setVisibility(View.INVISIBLE);
        mListId = data.getNetGedanId();
        String imagePath = data.getImagePath();
        if(!TextUtils.isEmpty(imagePath)){
            mBg.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }else{
            mBg.setImageResource(R.drawable.gedan_disk);
        }
        String tag = data.getTag();
        if(TextUtils.isEmpty(tag)){
            mTag.setVisibility(View.INVISIBLE);
        }else{
            mTag.setVisibility(View.VISIBLE);
            mTag.setText(tag);
        }
        String title = data.getTitle();
        if(TextUtils.isEmpty(title)){
            mTitle.setVisibility(View.INVISIBLE);
        }else{
            mTitle.setVisibility(View.VISIBLE);
            mGedanTitle = title;
            mTitle.setText(title);
        }
    }
}
