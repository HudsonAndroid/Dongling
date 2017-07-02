package com.hudson.donglingmusic.ui.recyclerview.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.SearchSuggestionBean;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.BaseRecyclerViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SearchAlbumItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SearchArtistItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SearchSongItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SearchTitleItemViewHolder;
import com.hudson.donglingmusic.ui.recyclerview.ViewHolder.SongListBottomViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Hudson on 2017/3/21.
 */

public class SearchResultRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    public static final int TYPE_TITLE_ARTIST = 1;
    public static final int TYPE_TITLE_SONG = 2;
    public static final int TYPE_TITLE_ALBUM = 3;
    public static final int TYPE_CONTENT_ARTIST = 4;
    public static final int TYPE_CONTENT_SONG = 5;
    public static final int TYPE_CONTENT_ALBUM = 6;
    public static final int TYPE_LOAD_MORE = 7;//加载更多布局

    private List<MusicInfo> mSongList;
    private List<SearchSuggestionBean.ArtistBean> mArtistList;
    private List<SearchSuggestionBean.AlbumBean> mAlbumList;
    private int mPlayingId = -1;
    private int mArtistSize;
    private int mSongSize;
    private int mAlbumSize;


    public int getPlayingId() {
        return mPlayingId;
    }

    public void setPlayingId(int playingId) {
        mPlayingId = playingId;
        notifyDataSetChanged();
    }

    public void setSongList(List<MusicInfo> songList) {
        mSongList = songList;
        notifyDataSetChanged();
    }

    public void setArtistList(List<SearchSuggestionBean.ArtistBean> artistList) {
        mArtistList = artistList;
        notifyDataSetChanged();
    }

    public void setAlbumList(List<SearchSuggestionBean.AlbumBean> albumList) {
        mAlbumList = albumList;
        notifyDataSetChanged();
    }

    public SearchResultRecyclerViewAdapter(Context context){
        mLayoutInflater = LayoutInflater.from(context);
        mSongList = new ArrayList<>();
        mArtistList = new ArrayList<>();
        mAlbumList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TITLE_ALBUM:
            case TYPE_TITLE_ARTIST:
            case TYPE_TITLE_SONG:
                return new SearchTitleItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_search_title,parent,false));
            case TYPE_CONTENT_ALBUM:
                return new SearchAlbumItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_artist_album,parent,false),mOnItemClickListener);
            case TYPE_CONTENT_ARTIST:
                return new SearchArtistItemViewHolder(mLayoutInflater.inflate(R.layout.item_recyclerview_artist_album,parent,false),mOnItemClickListener);
            case TYPE_LOAD_MORE:
                return new SongListBottomViewHolder(mLayoutInflater.inflate(R.layout.item_music_list_bottom,parent,false));
            case TYPE_CONTENT_SONG:
                return new SearchSongItemViewHolder(mLayoutInflater.inflate(
                        R.layout.item_recyclerview_local,parent,false),mOnItemClickListener,
                        mOnItemMenuClickListener);
        }
        return null;
    }

    private int mLoadMoreStatus = -1;//没有加载更多（一开始设计时想应该有）

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case TYPE_CONTENT_ALBUM:
                ((BaseRecyclerViewHolder)holder).refreshView(mAlbumList.get(position- mArtistSize - mSongSize-1));
                break;
            case TYPE_CONTENT_ARTIST:
                ((BaseRecyclerViewHolder)holder).refreshView(mArtistList.get(position- 1));
                break;
            case TYPE_CONTENT_SONG:
                ((BaseRecyclerViewHolder)holder).refreshView(mSongList.get(position- mArtistSize-1));
                break;
            case TYPE_LOAD_MORE:
                switch (mLoadMoreStatus){
                    case SongListBottomViewHolder.STATUS_NONE:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_NONE);
                        break;
                    case SongListBottomViewHolder.STATUS_LOADING:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_LOADING);
                        break;
                    case SongListBottomViewHolder.STATUS_NO_MORE:
                        ((BaseRecyclerViewHolder)holder).refreshView(SongListBottomViewHolder.STATUS_NO_MORE);
                        break;
                }
                break;
            case TYPE_TITLE_ALBUM:
                ((SearchTitleItemViewHolder)holder).refreshView(TYPE_TITLE_ALBUM,"专辑");
                break;
            case TYPE_TITLE_ARTIST:
                ((SearchTitleItemViewHolder)holder).refreshView(TYPE_TITLE_ARTIST,"歌手");
                break;
            case TYPE_TITLE_SONG:
                ((SearchTitleItemViewHolder)holder).refreshView(TYPE_TITLE_SONG,"歌曲");
                break;
        }
//        LocalItemViewHolder itemViewHolder = (LocalItemViewHolder) holder;
//        if(position == mPlayingId){
//            itemViewHolder.refreshItemState(TYPE_SONG);
//        }else{
//            itemViewHolder.refreshItemState(TYPE_ALBUM);
//        }
//        itemViewHolder.refreshView(mSongList.get(position));
    }

    @Override
    public int getItemCount() {
        if(mArtistList!=null){
            mArtistSize = mArtistList.size();
        }else{
            mArtistSize = 0;
        }
        if(mArtistSize != 0){
            mArtistSize++;
        }

        if(mSongList!=null){
            mSongSize = mSongList.size();
        }else{
            mSongSize = 0;
        }
        if(mSongSize !=0){
            mSongSize++;
        }

        if(mAlbumList!=null){
            mAlbumSize = mAlbumList.size();
        }else{
            mAlbumSize = 0;
        }
        if(mAlbumSize !=0){
            mAlbumSize++;
        }
        return mArtistSize + mSongSize + mAlbumSize+ 1;
    }

    //顺序是：歌手、歌曲、专辑
    @Override
    public int getItemViewType(int position) {
        if(getItemCount()!=0){
            if(position == 0){
                if(mArtistSize == 0){
                    if(mSongSize==0 && mAlbumSize == 0){
                        return TYPE_LOAD_MORE;//这种情况下三种数据全为空
                    }
                    if(mSongSize == 0){
                        return TYPE_TITLE_ALBUM;
                    }
                    return TYPE_TITLE_SONG;
                }
                return TYPE_TITLE_ARTIST;
            }else if (position == mArtistSize&&mSongSize!=0){
                return TYPE_TITLE_SONG;
            }else if (position == (mArtistSize+mSongSize)&&mAlbumSize!=0){
                return TYPE_TITLE_ALBUM;
            }else if(position == mArtistSize + mSongSize+mAlbumSize){
                return TYPE_LOAD_MORE;
            }else if(position>mArtistSize&&position<(mSongSize+mArtistSize)){
                return TYPE_CONTENT_SONG;
            }else if(position>0&&position<mArtistSize){
                return TYPE_CONTENT_ARTIST;
            }else if(position>(mSongSize+mArtistSize)&&position<(mAlbumSize+mSongSize+mArtistSize)){
                return TYPE_CONTENT_ALBUM;
            }
        }
        return -1;
    }

    //每一项点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    //每一项更多的点击事件
    public void setOnItemMenuClickListener(OnItemMenuClickListener onItemMenuClickListener) {
        mOnItemMenuClickListener = onItemMenuClickListener;
    }
    private OnItemMenuClickListener mOnItemMenuClickListener;
    public interface  OnItemMenuClickListener{
        void onItemMenuClick(View v, int position);
    }

}
