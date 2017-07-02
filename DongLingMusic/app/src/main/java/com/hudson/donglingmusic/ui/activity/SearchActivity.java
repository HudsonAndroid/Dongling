package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hudson.donglingmusic.IDonglingMusicAidlInterface;
import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.HotSearchWord;
import com.hudson.donglingmusic.bean.SearchMergeBean;
import com.hudson.donglingmusic.bean.SearchSuggestionBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.global.XUtilsManager;
import com.hudson.donglingmusic.net.BMA;
import com.hudson.donglingmusic.service.MusicService;
import com.hudson.donglingmusic.ui.recyclerview.Adapter.SearchResultRecyclerViewAdapter;
import com.hudson.donglingmusic.ui.view.AutoAdapterLayout;
import com.hudson.donglingmusic.ui.view.SearchHistoryItemView;
import com.hudson.donglingmusic.ui.view.SlideRelativeLayout;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import static com.hudson.donglingmusic.ui.itempager.LocalItemPager.LOCAL_LIST_INFO;

public class SearchActivity extends Activity implements TextWatcher, SearchResultRecyclerViewAdapter.OnItemClickListener, SearchResultRecyclerViewAdapter.OnItemMenuClickListener {
    private EditText mSearchEt;
    private AutoAdapterLayout mHotWordContainer;
    private LinearLayout mHistoryContainer;
    private View mNoInputView,mEmptySearchView;
    private RecyclerView mRecyclerView;
    private SearchResultRecyclerViewAdapter mAdapter;
    private ArrayList<String> mSearchHistory;
    private List<SearchSuggestionBean.ArtistBean> mArtist;
    private List<MusicInfo> mSongBeanList;
    private List<SearchSuggestionBean.AlbumBean> mAlbum;
    private IDonglingMusicAidlInterface mInterface;//远程服务
    private boolean mHasSetPlayList = false;
    private SearchServiceConnection mConnection;
    public static final String SEARCH_LIST_INFO = "SearchList";
    private MySharePreferences mInstance;
    private RadioGroup mSelectGroup;

    private boolean isSearchMerge = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bind();
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });
        mInstance = MySharePreferences.getInstance();
        mSearchHistory = mInstance.getHistorySearch();
        mHistoryContainer = (LinearLayout) findViewById(R.id.ll_search_history);
        mNoInputView = findViewById(R.id.sv_no_input_show);
        mSelectGroup = (RadioGroup) this.findViewById(R.id.rg_group);
        //标签切换监听
        mSelectGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rb_artist:
                        if(isSearchMerge){
                            tabSelectItem(0);
                        }
                        break;
                    case R.id.rb_song:
                        if(isSearchMerge){
                            tabSelectItem(1);
                        }
                        break;
                    case R.id.rb_album:
                        if(isSearchMerge){
                            tabSelectItem(2);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        mEmptySearchView = findViewById(R.id.tv_empty_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_search_result);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new SearchResultRecyclerViewAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemMenuClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        ((SlideRelativeLayout)findViewById(R.id.srl_search_root)).setOnFinishedListener(new SlideRelativeLayout.OnFinishedListener() {
            @Override
            public void whileFinished() {
                SearchActivity.this.finish();
            }
        });
        mSearchEt = (EditText) this.findViewById(R.id.et_search);
        mSearchEt.addTextChangedListener(this);
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {//输入法变搜索
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法
                    startSearchFromServer(mSearchEt.getText().toString().trim(),0);
                    return true;
                }
                return false;
            }
        });
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                BMA.Search.hotWord(), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        System.out.println("result:   "+responseInfo.result);
                        processData(responseInfo.result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                });
        mHotWordContainer = (AutoAdapterLayout) this.findViewById(R.id.aal_hot_word);
        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//开始搜索
//                System.out.println("地址"+BMA.Search.searchMerge("周杰伦",0,20));
                startSearchFromServer(mSearchEt.getText().toString().trim(),0);
            }
        });
    }

    private void updateHistorySearchData(String query){
        query = query.trim();
        if(!query.equals("")){
            if(mSearchHistory == null){
                mSearchHistory = new ArrayList<>();
            }
            String s;
            for (int i = 0; i < mSearchHistory.size(); i++) {
                s = mSearchHistory.get(i);
                if(s.equals(query)){
                    mSearchHistory.remove(s);
                }
            }
            mSearchHistory.add(0,query);
            if(mSearchHistory.size()>5){
                mSearchHistory.remove(5);
            }
            mInstance.saveHistorySearch(mSearchHistory);
        }
    }

    private void removeHistorySearchItem(String removeString){
        String s;
        for (int i = 0; i < mSearchHistory.size(); i++) {
            s = mSearchHistory.get(i);
            if(s.equals(removeString)){
                mSearchHistory.remove(s);
            }
        }
        mInstance.saveHistorySearch(mSearchHistory);
    }

    /**
     * 开始搜索
     * @param query
     */
    private void startSearchFromServer(String query, final int selectedItem) {
        if(TextUtils.isEmpty(query)){
            return ;
        }
        mSearchEt.setText(query);
        updateHistorySearchData(query);
        //网址:关键词 pageNo  pageSize
        XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                BMA.Search.searchMerge(query,0,20), new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //综合搜索结果
                        processSearchMergeData(responseInfo.result,selectedItem);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                });
    }

    /**
     * 综合搜索
     * @param result
     * @param selectedItem 选中的类别
     */
    private void processSearchMergeData(String result,int selectedItem) {
        Gson gson = new Gson();
        int index = 0;
        switch (selectedItem){
            case 1:
                index++;
                break;
            case 2:
                index+=2;
                break;
        }
        ((RadioButton)mSelectGroup.getChildAt(index)).setChecked(true);
        SearchMergeBean searchMergeBean = gson.fromJson(result,SearchMergeBean.class);
        if(searchMergeBean!=null&&searchMergeBean.getError_code()==BMA.RESPONSE_CODE){
            isSearchMerge = true;
            mArtistSize = 0;
            mSongSize = 0;
            mAlbumSize = 0;
            mNoInputView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptySearchView.setVisibility(View.INVISIBLE);
            mSelectGroup.setVisibility(View.VISIBLE);
            SearchMergeBean.ResultBean resultBean = searchMergeBean.getResult();
            List<SearchMergeBean.ResultBean.AlbumInfoBean.AlbumListBean> album_list = resultBean.getAlbum_info().getAlbum_list();
            List<SearchMergeBean.ResultBean.ArtistInfoBean.ArtistListBean> artist_list = resultBean.getArtist_info().getArtist_list();
            List<SearchMergeBean.ResultBean.SongInfoBean.SongListBean> song_list = resultBean.getSong_info().getSong_list();


            if(mArtist == null){
                mArtist = new ArrayList<>();
            }
            mArtist.clear();
            SearchSuggestionBean.ArtistBean artistBean;
            if(artist_list!=null)
            for (SearchMergeBean.ResultBean.ArtistInfoBean.ArtistListBean artistListBean : artist_list) {
                artistBean = new SearchSuggestionBean.ArtistBean();
                artistBean.setTing_uid(artistListBean.getTing_uid());
                artistBean.setArtistid(artistListBean.getArtist_id());
                artistBean.setArtistname(artistListBean.getAuthor());
                mArtist.add(artistBean);
            }
            if(mArtist !=null){
                mArtistSize = mArtist.size();
            }
            if(mArtistSize != 0){
                mArtistSize ++;
            }
            mTmpArtistSize = mArtistSize;

            if(mAlbum == null){
                mAlbum = new ArrayList<>();
            }
            mAlbum.clear();
            SearchSuggestionBean.AlbumBean albumBean;
            if(album_list!=null)
            for (SearchMergeBean.ResultBean.AlbumInfoBean.AlbumListBean albumListBean : album_list) {
                albumBean = new SearchSuggestionBean.AlbumBean();
                albumBean.setArtistname(albumListBean.getAuthor());
                albumBean.setAlbumid(albumListBean.getAlbum_id());
                albumBean.setAlbumname(albumListBean.getTitle());
                mAlbum.add(albumBean);

            }
            if(mAlbum !=null){
                mAlbumSize = mAlbum.size();
            }
            if(mAlbumSize!=0){
                mAlbumSize++;
            }
            mTmpAlbumSize = mAlbumSize;

            MusicInfo musicInfo;
            if(mSongBeanList == null){
                mSongBeanList = new ArrayList<>();
            }
            mSongBeanList.clear();
            if(song_list!=null)
            for (SearchMergeBean.ResultBean.SongInfoBean.SongListBean songListBean : song_list) {
                musicInfo = new MusicInfo();
                musicInfo.setArtist(songListBean.getAuthor());
                musicInfo.setTitle(songListBean.getTitle());
                musicInfo.setSongId(Integer.valueOf(songListBean.getSong_id()));
                musicInfo.setDuration(0);
                musicInfo.setMusicId(-1);
                musicInfo.setData("http??");//歌曲地址未知,但是确定是网络音乐
                mSongBeanList.add(musicInfo);
            }
            if(mSongBeanList !=null){
                mSongSize = mSongBeanList.size();
            }
            if(mSongSize!=0){
                mSongSize ++;
            }
            mTmpSongSize = mSongSize;
            System.out.println("个数"+mArtistSize+","+mSongSize+","+mAlbumSize);
            if(mArtistSize == 0&& mSongSize == 0&& mAlbumSize == 0){//没有数据
                mEmptySearchView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                return ;
            }
            tabSelectItem(selectedItem);
        }else{
            mNoInputView.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptySearchView.setVisibility(View.VISIBLE);
            mSelectGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 选中的Item
     * 前提是综合搜索的结果以后
     * @param selectedItem
     */
    private void tabSelectItem(int selectedItem){
        switch (selectedItem){
            case 0:
                mAdapter.setArtistList(mArtist);
                mAdapter.setSongList(null);
                mAdapter.setAlbumList(null);
                mSongSize = 0;
                mAlbumSize = 0;
                mArtistSize = mTmpArtistSize;
                break;
            case 1:
                mAdapter.setArtistList(null);
                mAdapter.setSongList(mSongBeanList);
                mAdapter.setAlbumList(null);
                mSongSize = mTmpSongSize;
                mAlbumSize = 0;
                mArtistSize = 0;
                break;
            case 2:
                mAdapter.setArtistList(null);
                mAdapter.setSongList(null);
                mAdapter.setAlbumList(mAlbum);
                mSongSize = 0;
                mAlbumSize = mTmpAlbumSize;
                mArtistSize = 0;
                break;
        }
    }


    private void bind(){
        mConnection = new SearchServiceConnection();
        bindService(new Intent(this, MusicService.class),mConnection,BIND_AUTO_CREATE);
    }

    private void unBind(){
        unbindService(mConnection);
    }


    class SearchServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mInterface = IDonglingMusicAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    //热门关键词搜索
    private void processData(String result) {
        Gson gson = new Gson();
        HotSearchWord word = gson.fromJson(result,HotSearchWord.class);
        if(word!=null&&word.getError_code()==BMA.RESPONSE_CODE){
            List<HotSearchWord.ResultBean> wordsInfo = word.getResult();
            for (int i = 0; i < wordsInfo.size(); i++) {
                HotSearchWord.ResultBean resultBean = wordsInfo.get(i);
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.drawable.selector_hot_search_bg);
                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(10,10,10,0);
                textView.setLayoutParams(params);
                final String queryWord = resultBean.getWord();
                textView.setText(queryWord);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSearchFromServer(queryWord,0);
                    }
                });
                mHotWordContainer.addView(textView);
            }
            mHotWordContainer.invalidate();
        }
        updateHistorySearch();
    }


    private void updateHistorySearch(){
        //历史记录的
        ArrayList<String> historySearch = mInstance.getHistorySearch();
        if(historySearch!=null){
            mHistoryContainer.removeAllViews();
            for (int i = 0; i < historySearch.size(); i++) {
                String query = historySearch.get(i).trim();
                if(!query.equals("")){
                    final SearchHistoryItemView searchHistoryItemView = new SearchHistoryItemView(this);
                    searchHistoryItemView.setText(query);
                    searchHistoryItemView.setOnClickHappenedListener(new SearchHistoryItemView.OnClickHappenedListener() {
                        @Override
                        public void onViewClick(String query) {
                            startSearchFromServer(query,0);
                        }

                        @Override
                        public void onDeleteClick(String query) {
                            removeHistorySearchItem(query);
                            mHistoryContainer.removeView(searchHistoryItemView);
                            mHistoryContainer.invalidate();
                        }
                    });
                    mHistoryContainer.addView(searchHistoryItemView,new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
            mHistoryContainer.invalidate();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        isSearchMerge = false;
        String search = mSearchEt.getText().toString().trim();
        if(!TextUtils.isEmpty(search)){
            XUtilsManager.getHttpUtilsInstance().send(HttpRequest.HttpMethod.GET,
                    BMA.Search.searchSugestion(search), new RequestCallBack<String>() {
                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
//                        System.out.println("result:   "+responseInfo.result);
                            processSearchSuggestionData(responseInfo.result);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {

                        }
                    });
        }else{
            mNoInputView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptySearchView.setVisibility(View.INVISIBLE);
            mSelectGroup.setVisibility(View.GONE);
            updateHistorySearch();
        }
    }

    private int mArtistSize=0,mSongSize=0,mAlbumSize=0;
    //记录临时数据，为了避免由于综合搜索导致出现问题(综合搜索我们会把其中一个列表显示，其他两个为空，所以大小为0）
    private int mTmpArtistSize = 0,mTmpSongSize = 0,mTmpAlbumSize = 0;
    /**
     * 解析搜索建议数据
     * @param result json数据
     */
    private void processSearchSuggestionData(String result) {
        mNoInputView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptySearchView.setVisibility(View.INVISIBLE);
        mSelectGroup.setVisibility(View.GONE);
        Gson gson = new Gson();
        mArtistSize = 0;
        mSongSize = 0;
        mAlbumSize = 0;
        SearchSuggestionBean suggestionBean = gson.fromJson(result,SearchSuggestionBean.class);
        if(suggestionBean!=null&&suggestionBean.getError_code()==BMA.RESPONSE_CODE){
            mArtist = suggestionBean.getArtist();
            if(mArtist !=null){
                mArtistSize = mArtist.size();
            }
            if(mArtistSize != 0){
                mArtistSize ++;
            }
            mAlbum = suggestionBean.getAlbum();
            if(mAlbum !=null){
                mAlbumSize = mAlbum.size();
            }
            if(mAlbumSize!=0){
                mAlbumSize++;
            }
            List<SearchSuggestionBean.SongBean> songBeanList = suggestionBean.getSong();
            SearchSuggestionBean.SongBean songBean;
            MusicInfo musicInfo;
            mSongBeanList = new ArrayList<>();
            for (int i = 0; i < songBeanList.size(); i++) {
                songBean = songBeanList.get(i);
                musicInfo = new MusicInfo();
                musicInfo.setArtist(songBean.getArtistname());
                musicInfo.setTitle(songBean.getSongname());
                musicInfo.setSongId(Integer.valueOf(songBean.getSongid()));
                musicInfo.setDuration(0);
                musicInfo.setMusicId(-1);
                musicInfo.setData("http??");//歌曲地址未知,但是确定是网络音乐
                mSongBeanList.add(musicInfo);
            }
            if(mSongBeanList !=null){
                mSongSize = mSongBeanList.size();
            }
            if(mSongSize!=0){
                mSongSize ++;
            }

            System.out.println("个数"+mArtistSize+","+mSongSize+","+mAlbumSize);
            if(mArtistSize == 0&& mSongSize == 0&& mAlbumSize == 0){//没有数据
                mEmptySearchView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                return ;
            }

            mAdapter.setArtistList(mArtist);
//            List<String>
//            mAdapter.setArtistList();
            mAdapter.setSongList(mSongBeanList);

            mAdapter.setAlbumList(mAlbum);

//            List<HotSearchWord.ResultBean> wordsInfo = word.getResult();
//            for (int i = 0; i < wordsInfo.size(); i++) {
//                HotSearchWord.ResultBean resultBean = wordsInfo.get(i);
//                TextView textView = new TextView(this);
//                textView.setBackgroundResource(R.drawable.selector_hot_search_bg);
//                ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.setMargins(10,10,10,0);
//                textView.setLayoutParams(params);
//                textView.setText(resultBean.getWord());
//                mHotWordContainer.addView(textView);
//            }
//            mHotWordContainer.invalidate();
        }else{//没有数据
            mEmptySearchView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemMenuClick(View v, int position) {
        if(mSongBeanList!=null){
            int modifyPosition = position - mArtistSize - 1;
            Intent intent = new Intent(SearchActivity.this, MoreInfoActivity.class);
            intent.putExtra("music_info",mSongBeanList.get(modifyPosition));
            intent.putExtra("selected_index",modifyPosition);
            intent.putExtra("list_info",SEARCH_LIST_INFO);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        updateHistorySearchData(mSearchEt.getText().toString().trim());
        int twoListSize = mArtistSize + mSongSize;
        if(position>0&&position<mArtistSize){//歌手点击事件
            int modifyPosition = position - 1;
            SearchSuggestionBean.ArtistBean artistBean = mArtist.get(modifyPosition);
            if(isSearchMerge){//那么是在有radioGroup情况
                Intent intent = new Intent(SearchActivity.this, ArtistListActivity.class);
                intent.putExtra("tingUid",artistBean.getTing_uid());
                intent.putExtra("artistId",artistBean.getArtistid());
                startActivity(intent);
//                System.out.println("歌手信息"+BMA.Artist.artistInfo(artistBean.getTing_uid(),artistBean.getArtistid()));
//                System.out.println("歌手地址"+BMA.Artist.artistSongList(artistBean.getTing_uid(),artistBean.getArtistid(),0,10));
            }else{//进入全局搜索
                startSearchFromServer(artistBean.getArtistname(),0);
            }
        }else if(position>mArtistSize&&position< twoListSize){//歌曲点击事件
            int modifyPosition = position - mArtistSize - 1;
//            System.out.println("歌曲信息网络地址"+BMA.Song.songInfo(mSongBeanList.get(modifyPosition).getSongId()+""));
            if(mInterface!=null){
                try {
                    if(!mHasSetPlayList){
                        mInstance.savePlayerPlaylistInfo(SEARCH_LIST_INFO);
                        mInterface.setPlayList(mSongBeanList);
                        mHasSetPlayList = true;
                    }
                    mInterface.play(modifyPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else{
                ToastUtils.showToast("数据正在初始化，请稍后重试！");
            }
        }else if(position>twoListSize&&position<(twoListSize+mAlbumSize)){//专辑点击事件
//            System.out.println("专辑信息地址"+BMA.Album.albumInfo(mAlbum.get(position- mArtistSize - mSongSize-1).getAlbumid()+""));
            Intent intent = new Intent(this, AlbumListActivity.class);
            intent.putExtra("albumId",mAlbum.get(position- mArtistSize - mSongSize-1).getAlbumid());
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
    }
}
