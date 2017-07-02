package com.hudson.donglingmusic.ui.itempager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.ui.activity.GedanListActivity;
import com.hudson.donglingmusic.ui.activity.MyGedanListActivity;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.GedanRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.MyGedanRecyclerViewAdapter;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static com.hudson.donglingmusic.ui.activity.ModifyGedanInfoActivity.MY_GEDAN_DATABASE_CHANGE;

/**
 * Created by Hudson on 2017/6/11.
 * 我的最爱(我的歌单)
 *
 * 分为自己创建的歌单与网络收藏的歌单，歌单的title上有按钮可以新建歌单
 */

public class MyGedanItemPager extends BaseItemPager {
    private List<MyGedanBean> mLocalGedanList;
    private List<MyGedanBean> mNetGedanList;
    private MyGedanRecyclerViewAdapter mAdapter;
    public static final String MY_GEDAN_DATABASE = "mygedandatabase";
    private boolean mHasLoadData = false;
    private GedanInfoModifyReceiver mReceiver;

    public MyGedanItemPager(Activity activity) {
        super(activity);
        //每一行显示4个子标签
        final GridLayoutManager manager = new GridLayoutManager(mActivity, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

            @Override
            public int getSpanSize(int position) {
                int type = mRecyclerView.getAdapter().getItemViewType(position);
                if(type == GedanRecyclerViewAdapter.TYPE_NORMAL){//实际内容的情况下是2个
                    return 1;
                }else{//其他,一个item占据一行
                    return 2;
                }
            }
        });
        mReceiver = new GedanInfoModifyReceiver();
        IntentFilter filter = new IntentFilter(MY_GEDAN_DATABASE_CHANGE);
        mActivity.registerReceiver(mReceiver,filter);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new MyGedanRecyclerViewAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MyGedanRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(boolean netOrNot, String netIdOrLocalIndex) {
                if(netOrNot){
                    Intent intent = new Intent(mActivity, GedanListActivity.class);
                    intent.putExtra("gedanListId",netIdOrLocalIndex);
                    mActivity.startActivity(intent);
                }else{
                    Intent intent = new Intent(mActivity, MyGedanListActivity.class);
                    MyGedanBean myGedanBean = mLocalGedanList.get(Integer.valueOf(netIdOrLocalIndex));
                    intent.putExtra("desc",myGedanBean.getDesc());
                    intent.putExtra("title",myGedanBean.getTitle());
                    intent.putExtra("databaseName",myGedanBean.getDatabaseName());
                    intent.putExtra("tag",myGedanBean.getTag());
                    intent.putExtra("imagePath",myGedanBean.getImagePath());
                    mActivity.startActivity(intent);
                }
            }
        });
        mAdapter.setOnNewLocalGedanClickListener(new MyGedanRecyclerViewAdapter.OnNewLocalGedanClickListener() {
            @Override
            public void onNewLocalGedanClick() {
                DialogUtils.showEditInfoDialog(mActivity,DialogUtils.TYPE_MODIFY_GEDAN_TITLE,UIUtils.getString(R.string.gedan_title_should_not_empty),"新建歌单",null,"输入歌单名", new Runnable() {
                    @Override
                    public void run() {
                        new AsyncTask<Void,Void,Boolean>(){

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                String newGedanTitle = MySharePreferences.getInstance().getNewGedanTitle();
                                if(mLocalGedanList!=null){
                                    MyGedanBean gedanBean;
                                    for (int i = 0; i < mLocalGedanList.size(); i++) {
                                        gedanBean = mLocalGedanList.get(i);
                                        if(gedanBean.getTitle().equals(newGedanTitle)){//歌单已经存在
                                            return true;
                                        }
                                    }
                                }
                                LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
                                litePalDB.addClassName(MyGedanBean.class.getName());
                                LitePal.use(litePalDB);
                                MyGedanBean mNewGedan = new MyGedanBean();
                                mNewGedan.setTitle(newGedanTitle);
                                mNewGedan.setDatabaseName(newGedanTitle);
                                mNewGedan.save();
                                //创建新创建歌单的数据库
                                litePalDB = new LitePalDB(getMyGedanDatabaseName(newGedanTitle), 1);
                                litePalDB.addClassName(MusicInfo.class.getName());
                                LitePal.use(litePalDB);
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean aVoid) {
                                super.onPostExecute(aVoid);
                                if(aVoid){//说明歌单存在
                                    ToastUtils.showToast("创建失败，歌单已经存在！");
                                }else{
                                    reLoadData();
                                }
                            }
                        }.execute();

                    }
                });
            }
        });
        mAdapter.setOnDeleteGedanHappenedListener(new MyGedanRecyclerViewAdapter.OnDeleteGedanHappenedListener() {
            @Override
            public void onDeleteGedan(final boolean isNet, final String title) {
                DialogUtils.showInformationDialog(mActivity, "删除歌单", "确定删除 "+title+" 歌单？", "删除", "取消", null, new Runnable() {
                    @Override
                    public void run() {
                        deleteGedanFromList(isNet,title);
                    }
                });
            }
        });
        mLoadingView.setVisibility(View.VISIBLE);
    }

    private void deleteGedanFromList(boolean isNet,String title){
        if(!TextUtils.isEmpty(title)){
            LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
            litePalDB.addClassName(MyGedanBean.class.getName());
            LitePal.use(litePalDB);
            DataSupport.deleteAll(MyGedanBean.class, "mtitle = ? ", title);
        }
        if(!isNet){
            if(mLocalGedanList!=null){
                MyGedanBean gedanBean;
                for (int i = 0; i < mLocalGedanList.size(); i++) {
                    gedanBean = mLocalGedanList.get(i);
                    if(gedanBean.getTitle().equals(title)){
                        //如果是本地的，需要删除对应的列表的数据库
                        LitePalDB litePalDB = new LitePalDB(getMyGedanDatabaseName(gedanBean.getDatabaseName()), 1);
                        litePalDB.addClassName(MusicInfo.class.getName());
                        LitePal.use(litePalDB);
                        DataSupport.deleteAll(MusicInfo.class);//删除列表
                        mLocalGedanList.remove(i);
                        break;
                    }
                }
            }
        }else{
            if(mNetGedanList!=null){
                MyGedanBean gedanBean;
                for (int i = 0; i < mNetGedanList.size(); i++) {
                    gedanBean = mNetGedanList.get(i);
                    if(gedanBean.getTitle().equals(title)){
                        mNetGedanList.remove(i);
                        break;
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        ToastUtils.showToast("歌单删除成功！");
    }

    @Override
    public void initData() {
        //读取数据
        readMyGedanData();
    }

    @Override
    public void reLoadData() {
        mHasLoadData = false;
        readMyGedanData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(mReceiver);
    }

    /**
     * 读取我的最爱数据(歌单数据）
     */
    private void readMyGedanData() {
        if(!mHasLoadData){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    mLocalGedanList =  new ArrayList<>();
                    mNetGedanList = new ArrayList<>();
                    LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
                    litePalDB.addClassName(MyGedanBean.class.getName());
                    LitePal.use(litePalDB);
                    List<MyGedanBean> myAllGedanBeen = DataSupport.findAll(MyGedanBean.class);
                    if(myAllGedanBeen!=null&&myAllGedanBeen.size()!=0){
                        for (MyGedanBean myGedanBean : myAllGedanBeen) {
                            if(myGedanBean.getNetGedanId()!=null){
                                mNetGedanList.add(myGedanBean);
                            }else{
                                mLocalGedanList.add(myGedanBean);
                            }
                        }
                    }else{//说明连我的最爱歌单也没有,创建我的最爱歌单
                        MyGedanBean myFavorite = new MyGedanBean();
                        String myFavoriteTitle = UIUtils.getString(R.string.my_favorite_gedan);
                        myFavorite.setTitle(myFavoriteTitle);
                        myFavorite.setTag("最爱");
                        myFavorite.setDatabaseName(myFavoriteTitle);
                        myFavorite.save();
                        //创建我的最爱歌单数据库
                        litePalDB = new LitePalDB(getMyGedanDatabaseName(myFavoriteTitle), 1);
                        litePalDB.addClassName(MusicInfo.class.getName());
                        LitePal.use(litePalDB);
                        mLocalGedanList.add(myFavorite);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mLoadingView.setVisibility(View.GONE);
                    if(mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    mAdapter.setGedanData(mLocalGedanList,mNetGedanList);
                    mHasLoadData = true;
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
    }


    /**
     * 获取歌单的数据库名
     * @param gedanTitle
     * @return
     */
    public static String getMyGedanDatabaseName(String gedanTitle){
        return "donglingGedan"+gedanTitle;
    }


    class GedanInfoModifyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            reLoadData();
        }
    }

}
