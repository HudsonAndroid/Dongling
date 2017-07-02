package com.hudson.donglingmusic.ui.recyclerview.ViewHolder;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.GedanListHeaderBean;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.ui.itempager.MyGedanItemPager;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.UIUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import java.io.File;

import static com.hudson.donglingmusic.ui.activity.ModifyGedanInfoActivity.MY_GEDAN_DATABASE_CHANGE;

/**
 * Created by Hudson on 2017/4/21.
 * 歌单详情页的头布局的ViewHolder
 */

public class GedanListHeaderViewHolder extends BaseRecyclerViewHolder<GedanListHeaderBean> {
    private ImageView mListBg;
    private TextView mListTitle,mListenCount, mStarCount, mTag,mDesc;
    private GedanListHeaderBean mHeaderBean;

    public GedanListHeaderViewHolder(View itemView) {
        super(itemView);
        mListBg = (ImageView) itemView.findViewById(R.id.iv_gedan_list_bg);
        mListTitle = (TextView) itemView.findViewById(R.id.tv_gedan_name);
        mListenCount = (TextView) itemView.findViewById(R.id.tv_listen_count);
        mStarCount = (TextView) itemView.findViewById(R.id.tv_star_count);
        mStarCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHeaderBean!=null){
                    LitePalDB litePalDB = new LitePalDB(MyGedanItemPager.MY_GEDAN_DATABASE, 1);
                    litePalDB.addClassName(MyGedanBean.class.getName());
                    LitePal.use(litePalDB);
                    MyGedanBean myGedanBean = new MyGedanBean();
                    myGedanBean.setDesc(mHeaderBean.getDesc());
                    myGedanBean.setTag(mHeaderBean.getTag());
                    String title = mHeaderBean.getTitle();
                    myGedanBean.setTitle(title);
                    String gedanBgFilePath = StorageUtils.getGedanBgFilePath(title);
                    myGedanBean.setImagePath(gedanBgFilePath);
                    myGedanBean.setNetGedanId(mHeaderBean.getListenId());
                    myGedanBean.save();
                    XUtilsManager.getHttpUtilsInstance().download(mHeaderBean.getListBgUrl(),gedanBgFilePath, false, false, new RequestCallBack<File>() {
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            System.out.println("图片下载成功");
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            System.out.println("图片下载失败");
                        }
                    });
                    //发一个广播通知我的最爱页面
                    UIUtils.getContext().sendBroadcast(new Intent(MY_GEDAN_DATABASE_CHANGE));
                }
            }
        });
        mTag = (TextView) itemView.findViewById(R.id.tv_tag);
        mDesc = (TextView) itemView.findViewById(R.id.tv_gedan_info);
    }

    @Override
    public void refreshView(GedanListHeaderBean data) {
        mHeaderBean = data;
        XUtilsManager.getBitmapUtilsInstance().display(mListBg,data.getListBgUrl());
        mListTitle.setText(data.getTitle());
        mListenCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getListenCount()));
        mStarCount.setText(AlbumListHeaderViewHolder.getHumanData(data.getStarCount()));
        mTag.setText(data.getTag());
        mDesc.setText(data.getDesc());
        mDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int maxLines = mDesc.getMaxLines();
                if(maxLines==3){
                    mDesc.setMaxLines(1000);
                    mDesc.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.up_arrow));
                }else{
                    mDesc.setMaxLines(3);
                    mDesc.setCompoundDrawablesWithIntrinsicBounds(null,null,null, UIUtils.getContext().getResources().getDrawable(R.drawable.down_arrow));
                }
                mDesc.invalidate();
            }
        });
    }
}
