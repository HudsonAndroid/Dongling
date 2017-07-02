package com.hudson.donglingmusic.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.Lyrics;
import com.hudson.donglingmusic.utils.LyricsDecodeUtils;
import com.hudson.donglingmusic.utils.StorageUtils;
import com.hudson.donglingmusic.utils.TimeUtils;

import java.util.ArrayList;

import static java.lang.System.currentTimeMillis;

/**
 * Created by Hudson on 2017/1/2.
 * 2017/1/26 可以提升的地方：虽然我们限制了歌词的宽度，但是高度上我们并没有处理，原因在于歌词我们认为
 * 行数不会超过2行，如果超过2行我们认定歌词不正常，所以我们并没有处理。
 * 2017/2/17 可以提升的地方：歌词的快进与快退可以做出更好优化（当前是快进或者快退后是下下句歌词生效）
 * 2017/2/17 可以提升的地方：歌词中包含有中英文的情况下，对歌词的预分行处理由于中英文字符占用空间不同的问题，
 *                         会出现小bug
 * 2017/2    个人想要增加的功能：给歌词控件添加横向切换的效果，然后实现中国传统风格的画卷效果。
 */

public class LyricsView extends ScrollView {
    /**
     * 这个值最好是奇数值，以确保中央的上下一致性
     */
    private static final int DEFAULT_ITEM_COUNT = 7;//默认的歌词控件数目
    private static final float DEFAULT_FOCUS_TEXT_SIZE = 12;//sp，高亢部分歌词默认字体大小
    private static final int DEFAULT_FOCUS_TEXT_COLOR = Color.YELLOW;//高亢部分歌词默认颜色
    private static final float DEFAULT_NORMAL_TEXT_SIZE = 11;//sp,普通歌词默认字体大小
    private static final int DEFAULT_NORMAL_TEXT_COLOR = Color.BLACK;//普通歌词默认颜色
    private static final int FLAG_EMPTY_LYRICS = 1;//歌词为空
    private static final int FLAG_UPDATE_PROGRESS = 2;//更新歌词进度
    private static final int FLAG_UPDATE_FOCUS_TEXT = 3;//更新高亢歌词
    private static final int FLAG_ADJUST_SCROLLED_LYRICS = 4;//校准歌词滚动位置
    private static final int FLAG_LOCATE_SCROLLED_DISTANCE = 6;//定位控件滑过的距离
    private boolean isAdjustScrolledLyrics = false;//true表示正在校准歌词滚动位置
    private int DEFAULT_SCROLL_TIME = 850;//默认滑动的时长
    private int DEFAULT_UPDATE_TEXT_TIME = 300;//歌词由大变小由小变大的时长
    private long mUpdateFocusTime;
    public enum Status{//歌词状态，分别是播放、暂停、空闲
        PLAYING,PAUSE,IDLE
    }
    private Status curStatus = Status.IDLE;//当前歌词状态是空闲的
    public enum StatusModifyLocation{
        SCROLL_CHANGING_LYRICS,//正在切换歌词的滑动
        WAITING_LOCATE_CENTER,//等待切换歌词完成来重定位高亢歌词到中央
        NONE//其他
    }
    private StatusModifyLocation modifyStatus;
    private long offsetTime = -500;//歌词的快进与后退
    private Context context;//用于创建textView
    private int itemHeight;//每一句歌词控件的高度
    private int itemCount;//整个控件中包含的item歌词的数目
    private ArrayList<Lyrics> lrcList;//歌词的内容
    private ArrayList<TextView> mTextViewList;//歌词控件的集合
    private float focusTextSize;//高亢歌词字体大小
    private int focusTextColor;//高亢歌词字体颜色
    private float normalTextSize;//普通歌词字体大小
    private int normalTextColor;//普通歌词字体颜色
    private float offsetSize;//高亢与普通的大小差值
    private LyricsDecodeUtils mLyricsDecodeUtils;//歌词加载帮助类
    private int viewHeight;//本控件的高度
    private Typeface typeface;//歌词文本的字体
    private int curIndex = 0;//当前播放的歌词下标
    private TextView curTextView;//当前高亢显示的textView
    private TextView lastTextView;//旧的textView
    private Scroller mScroller;//配合滑动的scroller类,也就是计算工具类
//    private int changeTimes = 5;//歌词变大更新的次数
    private float curFocusTextSize;//高亢歌词变化过程中的字体大小
    private float lastTextViewSize;//上一个高亢歌词字体大小
//    private float eachTextSizeChangeValue;//每次增加的大小
    private boolean isScrolling = false;//控件是否正在滑动过程中（包括惯性滑动）
    //**********************外部修改方法*****************//

    /**
     * 获取控件当前是否正在滑动（包括惯性滑动）
     * @return true表示正在滑动
     */
    public boolean isScrolling(){
        return isScrolling;
    }
    /**
     * 获取歌词切换滑动的时长
     * @return time
     */
    public int getDefaultScrollTime() {
        return DEFAULT_SCROLL_TIME;
    }

    /**
     * 设置歌词切换滑动的时长
     * @param scrollTime 歌词切换时长
     */
    public void setDefaultScrollTime(int scrollTime) {
        this.DEFAULT_SCROLL_TIME = scrollTime;
    }

    /**
     * 获取歌词切换时字体切换的时长
     * @return 字体更新时长
     */
    public int getDefaultUpdateTextTime() {
        return DEFAULT_UPDATE_TEXT_TIME;
    }

    /**
     * 设置歌词切换时字体切换的时长
     * @param updateTextTime 字体更新时长
     */
    public void setDefaultUpdateTextTime(int updateTextTime) {
        this.DEFAULT_UPDATE_TEXT_TIME = updateTextTime;
    }

    /**
     * 获取整个View一次能显示的歌词数目
     * @return int值
     */
    public int getItemCount() {
        return itemCount;
    }

    /**
     * 设置整个View一次能显示歌词的数目,你最好使用奇数
     * @param itemCount use odd number better
     */
    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * 获取高亢歌词的字体大小
     * @return 像素
     */
    public float getFocusTextSize() {
        return focusTextSize;
    }

    /**
     * 设置高亢歌词的字体大小
     * warning: the focus text size must be larger
     * @param focusTextSize 像素
     */
    public void setFocusTextSize(float focusTextSize) {
        if(focusTextSize<normalTextSize){
            throw new IllegalStateException("the focus text size must be larger!");
        }
        this.focusTextSize = focusTextSize;
        offsetSize = focusTextSize - normalTextSize;
    }

    /**
     * 获取高亢歌词的颜色
     * @return 颜色
     */
    public int getFocusTextColor() {
        return focusTextColor;
    }

    /**
     * 设置高亢歌词的颜色
     * @param focusTextColor 颜色
     */
    public void setFocusTextColor(int focusTextColor) {
        this.focusTextColor = focusTextColor;
    }

    /**
     * 获取正常歌词的字体大小
     * @return 像素
     */
    public float getNormalTextSize() {
        return normalTextSize;
    }

    /**
     * 设置正常歌词的字体大小
     * warning: the focus text size must larger
     * @param normalTextSize 单位像素
     */
    public void setNormalTextSize(float normalTextSize) {
        if(focusTextSize<normalTextSize){
            throw new IllegalStateException("the focus text size must be larger!");
        }
        this.normalTextSize = normalTextSize;
        offsetSize = focusTextSize - normalTextSize;
    }

    /**
     * 获取正常歌词的颜色
     * @return 颜色
     */
    public int getNormalTextColor() {
        return normalTextColor;
    }

    /**
     * 设置正常歌词的颜色
     * @param normalTextColor 颜色
     */
    public void setNormalTextColor(int normalTextColor) {
        this.normalTextColor = normalTextColor;
    }

    /**
     * 获取字体
     * @return 字体
     */
    public Typeface getTypeface() {
        return typeface;
    }

    /**
     * 设置歌词字体
     * @param typeface 字体
     */
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

//    /**
//     * 获取更新频率
//     * @return 指定时间内更新次数
//     */
//    public int getChangeTimes() {
//        return changeTimes;
//    }
//
//    /**
//     * 设置指定时间内的更新次数
//     * @param changeTimes
//     */
//    public void setChangeTimes(int changeTimes) {
//        this.changeTimes = changeTimes;
//    }

    /**
     * 获取快进或者快退时长
     * @return 相对原生歌词的offset
     */
    public long getOffsetTime(){
        return offsetTime;
    }

    /**
     * 设置快进或者快退时长
     * 提示：如果时长与歌词本身延迟相加小于0，控件将忽略该时长。
     * 即时长的负值不宜过小
     * @param offsetTime 时长
     */
    public void setOffsetTimeAndPlay(long offsetTime){
        this.offsetTime += offsetTime;
        playLyricsFromPosition(mHelper.getAccurateTime()+this.offsetTime,false);//直接在播放的进度基础上增加时间，相当于歌词快进
        mHasModify = true;
    }

//    public void setOffsetTimeNotPlay(long offsetTime){
//        this.offsetTime += offsetTime;
//        MySharePreferences.getInstance().saveCurLyricsOffset((int)offsetTime);
//        mHasModify = true;
//    }

    /**
     * 获取歌词文本
     * @return
     */
    public String getLyricsContent(){
        StringBuffer lyricsString = new StringBuffer("");
        if(lrcList==null|(lrcList!=null&&lrcList.size()==0)){
            return "";
        }
        for(Lyrics lyrics:lrcList){
            lyricsString = lyricsString.append(lyrics.getLrcStr()).append("\n");
        }
        return lyricsString.toString();
    }

    public int getCurIndex(){
        return curIndex;
    }

    public String getPositionLyrics(int position){
        if(lrcList==null|(lrcList!=null&& lrcList.size() ==0)){
            return null;
        }else{
            if(position<0|position>=lrcList.size()){
                return null;
            }
            return lrcList.get(position).getLrcStr();
        }
    }

    /**
     * 修改某句歌词
     * @param position
     * @param modifyLyrics
     */
    public void modifyPositionLyrics(int position,String modifyLyrics){
        if(lrcList==null|(lrcList!=null&& lrcList.size() ==0)){
            return;
        }
        lrcList.get(position).setLrcStr(modifyLyrics);
        //modifyLineLyricsLen(itemLrc.getPaint(),lrc.getLrcStr())
        TextView textView = mTextViewList.get(position);
        textView.setText(modifyLineLyricsLen(textView.getPaint(),modifyLyrics));
        mHasModify = true;
    }

    private boolean mHasModify = false;
    /**
     * 将对歌词的修改同步到本地
     * 本方法一般在歌词切换或者销毁时回调
     */
    public void saveLyricsModifyToLocal(){
        if(mHasModify&&lrcList!=null){
            int size = lrcList.size();
            String[] lyrics = new String[size];
            Lyrics lyricsInfo;
            for (int i = 0; i < size; i++) {
                lyricsInfo = lrcList.get(i);
                lyrics[i] = TimeUtils.getTime((int) (lyricsInfo.getLrcTime()-offsetTime))+lyricsInfo.getLrcStr();
            }
            StorageUtils.saveLyricsFileByLyricsPath(lyrics,mLyricsPath);
        }
        if(lrcList!=null){
            lrcList.clear();
            lrcList = null;
        }
    }



    /**
     * 判断是否控件无法滚动
     * @return
     */
    public boolean isNoScrollDistance(){
        if(lrcList == null||lrcList.size() == 0||lrcList.size() == 1){
            return true;
        }
        return false;
    }

    //**********************外部修改方法*****************//

    /**
     * 歌词切换监听器
     */
    public interface OnFocusLyricsChangeListener{
        /**
         * 当歌词切换时的回调方法
         * @param position 当前播放的歌词id
         * @param focusTextView 当前播放的歌词对应的textView
         * @param focusLyrics 当前播放的歌词对应的实例
         */
        void onFocusLyricsChange(int position, TextView focusTextView, Lyrics focusLyrics);
    }
    private OnFocusLyricsChangeListener mOnFocusLyricsChangeListener;
    public void setOnFocusLyricsChangeListener(OnFocusLyricsChangeListener onFocusLyricsChangeListener) {
        mOnFocusLyricsChangeListener = onFocusLyricsChangeListener;
    }

    public interface OnScrollEventListener{
        /**
         * 滚动发生时
         */
        void onScrollMove();
        /**
         * 滚动抬起并且scrollView的滚动停止（包括惯性滚动，ps：由于惯性滚动导致定位中央歌词不准）
         * 本方法是在moveup之后会判断此后的getScrollY是否发生变化，即是否产生了惯性滚动，也就是
         * 是否发生了快速滑动事件，如果发生了，那么在惯性滚动完成再回调本方法。如果你需要在Up事件后
         * 立刻处理，请使用onScrollUp方法
         * @param screenCenterLrcPosition 滚动时屏幕中央歌词的播放时间
         */
        void onScrollUpAfterViewScroll(long screenCenterLrcPosition);

        /**
         * 当up事件发生时的回调
         */
        void onScrollUp();
    }
    private OnScrollEventListener mOnScrollEventListener;
    public void setOnScrollEventListener(OnScrollEventListener onScrollEventListener) {
        mOnScrollEventListener = onScrollEventListener;
    }

    /**
     * OnPlayerPreparedAndLyricsLoadedListener is better
     */
    public interface OnLyricsLoadCompletedListener{
        /**
         * 当歌词与控件加载完毕时回调
         */
        void onLyricsLoadCompleted();
    }
    private OnLyricsLoadCompletedListener mOnLyricsLoadCompletedListener;
    public void setOnLyricsLoadCompletedListener(OnLyricsLoadCompletedListener onLyricsLoadCompletedListener) {
        mOnLyricsLoadCompletedListener = onLyricsLoadCompletedListener;
    }

    private OnPlayerPreparedAndLyricsLoadedListener mOnPlayerPreparedAndLyricsLoadedListener;
    private boolean playerPreparedAndLyricLoaded = false;//播放器是否准备完毕
    private static final int FLAG_PLAYER_PREPARED_LYRIC_LOADED = 5;
    /**
     * 播放器准备完毕与歌词加载完毕监听器
     * 注意：如果使用本监听器之后，我们播放器中就不能重写mediaplayer的onPreparedListener方法
     * 如果需要重写，你可以在本监听器中使用，以替换上述方法。
     */
    public interface OnPlayerPreparedAndLyricsLoadedListener{
        void onPlayerPreparedAndLyricsLoaded();
    }
    public void setOnPlayerPreparedAndLyricsLoadedListener(OnPlayerPreparedAndLyricsLoadedListener onPlayerPreparedAndLyricsLoadedListener,MediaPlayer mediaPlayer) {
        mOnPlayerPreparedAndLyricsLoadedListener = onPlayerPreparedAndLyricsLoadedListener;
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerPreparedAndLyricLoaded = true;
            }
        });
    }

    private boolean mNeedScrollTwiceDistance = false;//是否需要滑动两个歌词的距离,用于歌词时间错误的
    private Handler mHandler = new Handler() {
        private long mCurCallTime;
        private long mLastCallTime;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FLAG_LOCATE_SCROLLED_DISTANCE:
                    if(msg.arg1!=getScrollY()){//说明还在惯性滚动中
                        Message message = Message.obtain();
                        message.what = FLAG_LOCATE_SCROLLED_DISTANCE;
                        message.arg1 = getScrollY();
                        mHandler.sendMessageDelayed(message,50);//过一段时间再次检测
                    }else{//说明控件不在滑动，可以定位中央歌词了
                        isScrolling = false;//惯性滑动也停止了
                        mOnScrollEventListener.onScrollUpAfterViewScroll(getScreenCenterPosition());
                    }
                    break;
                case FLAG_PLAYER_PREPARED_LYRIC_LOADED:
                    if(playerPreparedAndLyricLoaded){
                        mOnPlayerPreparedAndLyricsLoadedListener.onPlayerPreparedAndLyricsLoaded();
                    }else{
                        mHandler.sendEmptyMessageDelayed(FLAG_PLAYER_PREPARED_LYRIC_LOADED,5);
                    }
                    break;
                case FLAG_EMPTY_LYRICS://该状态下mTextViewList中只有一个textView
                    mTextViewList.get(0).setText(R.string.no_lrc_show_after);
                    break;
                case FLAG_UPDATE_PROGRESS:
                    if(curIndex == -1){
                        curTextView = new TextView(getContext());
                    }else{
                        curTextView = mTextViewList.get(curIndex);
                    }
                    curTextView.setTextColor(focusTextColor);
                    if(lastTextView!=null){
                        lastTextView.setTextColor(normalTextColor);
                    }
                    long scrollTime;
                    if(curIndex+1<lrcList.size()){
                        long tmpTime = lrcList.get(curIndex+1).getDelayTime();
//                        System.out.println("延迟时间"+tmpTime);
//                        System.out.println("执行下一句歌词的睡眠时间"+tmpTime+"歌词内容"+lrcList.get(curIndex +1).getLrcStr());
                        if(tmpTime<=0){//如果两句歌词时间一致，说明歌词有问题，我们直接给到下一句歌词
                            curIndex = curIndex +1;
                            lastTextView = curTextView;
                            mHandler.sendEmptyMessage(FLAG_UPDATE_PROGRESS);
                            mNeedScrollTwiceDistance = true;
//                            mHandler.sendEmptyMessageDelayed(FLAG_ADJUST_SCROLLED_LYRICS,3000);//由于歌词出错，3s后更正位置
                            return;//后面的逻辑不跑了，不用滚动
                        }
                        scrollTime = (DEFAULT_SCROLL_TIME > tmpTime) ? tmpTime : DEFAULT_SCROLL_TIME;
//                        if(offsetTime!=0){
//                            tmpTime = ((tmpTime+offsetTime)<0)?tmpTime:(tmpTime+offsetTime);
//                            offsetTime = 0;
//                        }
//                        System.out.println("处理后的延迟时间"+tmpTime);
                        mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, tmpTime);
                    }else{
                        scrollTime = DEFAULT_SCROLL_TIME;
                    }
                    //更新高亢歌词的时间必须小于切换滑动的时间
                    mUpdateFocusTime = (DEFAULT_UPDATE_TEXT_TIME*1.2f>scrollTime)?(long)(scrollTime*0.6):DEFAULT_UPDATE_TEXT_TIME;
//                    System.out.println("更新时长"+mUpdateFocusTime);
                    mLastCallTime = currentTimeMillis();//记录系统时间
                    mHandler.sendEmptyMessage(FLAG_UPDATE_FOCUS_TEXT);
//                    curIndex ++;
                    if(!isAdjustScrolledLyrics){
                        if(!mNeedScrollTwiceDistance){//一般情况
                            mScroller.startScroll(getScrollX(), getScrollY(), 0, itemHeight,
                                    (int) scrollTime);
                        }else{//两句歌词时间一致的情况
                            mScroller.startScroll(getScrollX(), getScrollY(), 0, itemHeight*2,
                                    (int) scrollTime);
                            mNeedScrollTwiceDistance = false;
                        }
                    }
                    //歌词切换监听回调
                    if(mOnFocusLyricsChangeListener!=null){
                        mOnFocusLyricsChangeListener.onFocusLyricsChange(curIndex,curTextView,lrcList.get(curIndex));
                    }
                    break;
                case FLAG_UPDATE_FOCUS_TEXT:
                    // TODO: 2017/1/26 修改了changeTimes：由于我们的每部手机性能不同，所以如果固定设置changeTimes理论上虽然可以实现，但是handler执行本身由于代码执行需要时间，所以不能达到准时效果，因此换成如下思路
                    //思路：根据系统时间来计算已经耗费的时间，根据我们设置的更新时间长度所占据的百分比来计算字体大小应该变化的值，即changeSizeValue
//                    System.out.println("刷新大小"+curIndex+ currentTimeMillis());
                    //处理高亢歌词与上一个歌词
                    if (curFocusTextSize < focusTextSize) {
                        mCurCallTime = System.currentTimeMillis();
                        mHandler.sendEmptyMessage(FLAG_UPDATE_FOCUS_TEXT);
                        float changeSizeValue = offsetSize*(mCurCallTime - mLastCallTime)/mUpdateFocusTime;
                        mLastCallTime = mCurCallTime;
                        curFocusTextSize += changeSizeValue;
                        if(lastTextView!=null){
                            lastTextViewSize -= changeSizeValue;
                            lastTextView.setTextSize(lastTextViewSize);
                        }
                        curTextView.setTextSize(curFocusTextSize);
                    }else{
                        if (curIndex<lrcList.size()) {
                            lastTextView = curTextView;
                            curIndex ++;
//                            System.out.println("完毕");
                        }else{//歌词播放完毕
                            curStatus = Status.IDLE;
                        }
                        curFocusTextSize = normalTextSize;
                        lastTextViewSize = focusTextSize;
                    }
                    break;
                case FLAG_ADJUST_SCROLLED_LYRICS://立刻滑动到高亢位置
                    //首先判断是否有歌词切换滑动，如果有，等待歌词切换滑动完成
                    if(modifyStatus == StatusModifyLocation.SCROLL_CHANGING_LYRICS){
                        modifyStatus = StatusModifyLocation.WAITING_LOCATE_CENTER;
                    }else{
                        scrollFocusLyricToViewCenter();
                    }
                    break;
            }
        }
    };

    public LyricsView(Context context) {
        this(context, null);
    }

    public LyricsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 滑动高亢歌词到控件中央
     */
    public void scrollFocusLyricToViewCenter(){
        if(lrcList!=null&&lrcList.size()!=0){
            smoothScrollTo(0,itemHeight*curIndex);
        }
        isAdjustScrolledLyrics = false;
    }

    /***
     * 配合Scroller 进行动态滚动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            modifyStatus = StatusModifyLocation.SCROLL_CHANGING_LYRICS;
//            mScroller.forceFinished(true);
            this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            this.postInvalidate();
        }else{
            if(modifyStatus == StatusModifyLocation.SCROLL_CHANGING_LYRICS){
                //说明正在等待歌词切换完成，随后定位中央位置
                scrollFocusLyricToViewCenter();
            }
            modifyStatus = StatusModifyLocation.NONE;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewHeight = h;
        itemHeight = h / itemCount;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param attrs 自定义属性
     */
    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        mScroller = new Scroller(context);
        if(attrs!=null){//解析自定义属性
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.LyricsView);
            itemCount = ta.getInteger(R.styleable.LyricsView_item_count,DEFAULT_ITEM_COUNT);
            focusTextSize = sp2px(ta.getDimension(R.styleable.LyricsView_focus_text_size,DEFAULT_FOCUS_TEXT_SIZE),context);
            focusTextColor = ta.getColor(R.styleable.LyricsView_focus_text_color,DEFAULT_FOCUS_TEXT_COLOR);
            normalTextSize = sp2px(ta.getDimension(R.styleable.LyricsView_normal_text_size,DEFAULT_NORMAL_TEXT_SIZE),context);
            normalTextColor = ta.getColor(R.styleable.LyricsView_normal_text_color,DEFAULT_NORMAL_TEXT_COLOR);
            DEFAULT_SCROLL_TIME = ta.getInteger(R.styleable.LyricsView_scroll_time,DEFAULT_SCROLL_TIME);
            DEFAULT_UPDATE_TEXT_TIME = ta.getInteger(R.styleable.LyricsView_update_text_time,DEFAULT_UPDATE_TEXT_TIME);
            ta.recycle();
        }else{
            itemCount = DEFAULT_ITEM_COUNT;
            focusTextSize = sp2px(DEFAULT_FOCUS_TEXT_SIZE,context);
            focusTextColor = DEFAULT_FOCUS_TEXT_COLOR;
            normalTextSize = sp2px(DEFAULT_NORMAL_TEXT_SIZE,context);
            normalTextColor = DEFAULT_NORMAL_TEXT_COLOR;
        }
        offsetSize = focusTextSize - normalTextSize;
        mLyricsDecodeUtils = new LyricsDecodeUtils();
    }

    /**
     * 必须先设置监听器然后才能设置初始化歌词与view。由于这个原因（这两个方法调用顺序必须固定），
     * 所以封装一下，更简单
     * @param lyricsPath 歌词文件路径
     * @param listener 歌词加载完毕且播放器准备完毕监听器
     * @param mediaPlayer 播放器实例
     */
    public void loadLyricsInitViewAndAddListener(String lyricsPath,OnPlayerPreparedAndLyricsLoadedListener listener,MediaPlayer mediaPlayer){
        setOnPlayerPreparedAndLyricsLoadedListener(listener,mediaPlayer);
        loadLyricsAndInitView(lyricsPath);
    }

    /**
     * 修改textView显示的字符串.目的：使得不能一行显示的歌词分行显示（加换行符号）
     * @param textPaint 注意不能使用new Paint测量，paint需要通过textView.getPaint获取.原因在于textView的paint与New处理的不一致
     * @param srcLyric 歌词
     * @return 修改歌词
     * 注意：本方法必须在本控件的宽高确定之后调用
     */
    public String modifyLineLyricsLen(Paint textPaint,String srcLyric){
        // 所加的0.25是为了float向int转换的过渡。由于有些歌词是中英文混合的，所以容易出现一行上的字的长度在变大后超出范围的现象
        int count = (int) (textPaint.measureText(srcLyric)/(getWidth()-getPaddingRight()-getPaddingLeft())+0.25f);
        if(count<=0){
            return srcLyric;
        }else{
            int eachLen = srcLyric.length()/(count+1);
            int startIndex = 0,endIndex = 0;
            String tmpString = "";
            for (int i = 0; i <= count; i++) {
                if(i == count){
                    tmpString+=srcLyric.substring(startIndex);
                }else{
                    endIndex =startIndex+eachLen;
                    if(srcLyric.contains(" ")){
                        endIndex = getMoreValidIndex(srcLyric,endIndex);
                    }
                    tmpString+=srcLyric.substring(startIndex,endIndex)+"\n";
                }
                startIndex = endIndex;
            }
            return tmpString;
        }
    }

    /**
     * 获取英文歌词中的（与切割位置相近的）空格位置id
     * @param text 原始歌词
     * @param srcIndex 原始切割id
     * @return
     */
    public int getMoreValidIndex(String text,int srcIndex){
        //查找附近是否存在空格字符，最多找5个字符
        int findCount = (text.length() - srcIndex)>5?5:(text.length() - srcIndex);
        for (int i = 0; i < findCount; i++) {
            if(text.charAt(srcIndex+i)== ' '){
                return srcIndex+i;
            }
            if(text.charAt(srcIndex - i)== ' '){
                return srcIndex - i;
            }
        }
        return srcIndex;
    }

    private String mLyricsPath;
    /**
     * 加载歌词
     *  注意：本方法必须在本控件的宽高确定之后调用
     * @param lyricsPath 歌词文件路径
     */
    public void loadLyricsAndInitView(String lyricsPath) {
        if (mLyricsDecodeUtils != null) {
            mTextViewList = new ArrayList<>();
            //加载歌词
            lrcList = mLyricsDecodeUtils.readLRC(lyricsPath);
            mLyricsPath = lyricsPath;
            /*
             * 初始化item歌词控件。
             * 1.首先我们添加一个linearlayout
             * 2.为了在正中央显示，我们先往里面添加一个空白的View
             * 3.添加歌词控件
             */
            LinearLayout parent = new LinearLayout(context);
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            View blankView = new View(context);
            int startBlankHeight;
            if(lrcList==null||lrcList.size()==0||lrcList.size() == 1){//匹配居中
                startBlankHeight = (viewHeight - itemHeight) / 2;
            }else{
                startBlankHeight = (viewHeight - itemHeight) / 2 + itemHeight;
            }
            LayoutParams blankParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, startBlankHeight);
            blankView.setLayoutParams(blankParams);
            parent.addView(blankView);
            LayoutParams textParams = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
            TextView itemLrc;
            if (lrcList != null && lrcList.size() != 0) {//有歌词
                for (Lyrics lrc : lrcList) {
                    itemLrc = new TextView(context);
                    itemLrc.setTextColor(normalTextColor);
                    itemLrc.setTextSize(focusTextSize);
                    itemLrc.setGravity(Gravity.CENTER);
                    if (typeface != null)
                        itemLrc.setTypeface(typeface);
                    itemLrc.setText(modifyLineLyricsLen(itemLrc.getPaint(),lrc.getLrcStr()));
                    itemLrc.setTextSize(normalTextSize);
                    itemLrc.setLayoutParams(textParams);
                    parent.addView(itemLrc);
                    mTextViewList.add(itemLrc);
                }
            } else {//没有歌词
                itemLrc = new TextView(context);
                itemLrc.setText(R.string.no_lrc);
                if (typeface != null)
                    itemLrc.setTypeface(typeface);
                itemLrc.setTextColor(focusTextColor);
                itemLrc.setTextSize(focusTextSize);
                itemLrc.setGravity(Gravity.CENTER);
                itemLrc.setLayoutParams(textParams);
                parent.addView(itemLrc);
                mTextViewList.add(itemLrc);
                mHandler.sendEmptyMessageDelayed(FLAG_EMPTY_LYRICS, 20000);//过段时间显示别的内容
            }
            View endBlankView = new View(context);
            int endBlankHeight;
            if(lrcList==null||lrcList.size()==0||lrcList.size() == 1){//匹配居中
                endBlankHeight = viewHeight - startBlankHeight - itemHeight;
            }else{
                endBlankHeight = viewHeight - startBlankHeight;
            }
            endBlankView.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, endBlankHeight));
            parent.addView(endBlankView);
            addView(parent);
            invalidate();
            if(mOnLyricsLoadCompletedListener!=null){
                mOnLyricsLoadCompletedListener.onLyricsLoadCompleted();
            }
            if(mOnPlayerPreparedAndLyricsLoadedListener!=null){
                mHandler.sendEmptyMessage(FLAG_PLAYER_PREPARED_LYRIC_LOADED);
            }
            scrollTo(0,0);//滑到起点
        }
    }

    /**
     * 开始播放歌词（使用之前需要先loadLyricsAndInitView）
     * 该方法融合了播放与暂停
     * @param pauseTime 暂停后播放的时间位置(即mediaplayer获取到的当前时长).如果不是pause直接放入0
     */
    public void startPlayLyrics(long pauseTime) {
        if (lrcList != null && lrcList.size() != 0) {
            if(curStatus == Status.IDLE){//从头开始播放歌词
                curFocusTextSize = normalTextSize;
                lastTextViewSize = focusTextSize;
                mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, lrcList.get(0).getDelayTime());
                curStatus = Status.PLAYING;
            }else if(curStatus == Status.PAUSE&&curIndex<lrcList.size()){//从暂停处开始播放。注意curIndex不要+1，原因在于curIndex++一般都执行完成了
                mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, lrcList.get(curIndex).getLrcTime()-pauseTime);
                curStatus = Status.PLAYING;
            }else if(curStatus == Status.PLAYING){//暂停歌词
                pauseLyrics();
            }
        }
    }


    // TODO: 2017/1/2  完成了歌词正常显示
    /**
     * 暂停歌词播放（实际上是停止）
     */
    public void pauseLyrics(){
        if(curStatus!= Status.PLAYING){//如果当前并没有开始播放歌词
            return ;
        }else{
            mHandler.removeCallbacksAndMessages(null);//移除所有消息
            curStatus = Status.PAUSE;
        }
    }

    /**
     * 获取当前歌词状态
     * @return maybe PLAYING,PAUSE,IDLE
     */
    public Status getCurStatus(){
        return curStatus;
    }

    // TODO: 2017/1/7 中途由于学业问题，暂停工程。今日重新开始

    /**
     * 从某个位置开始播放歌词
     * 使用场景：在播放音乐时，并没有在显示歌词的界面，在切入显示歌词界面时，我们需要让歌词跳转到指定位置
     * @param currentPosition 播放器当前播放的位置
     * @param curPause 当前播放器是否是暂停状态
     */
    public void playLyricsFromPosition(long currentPosition,boolean curPause){
        if(lrcList==null|(lrcList!=null&&lrcList.size()==0)){
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        //检查当前位置所处的index，用来设置curIndex
        if(lrcList.size()==1){//只有一句歌词情况下，curIndex始终为0
            curIndex = 0;
        }
        if(curTextView!=null){//清除原有的高亢歌词
            curTextView.setTextSize(normalTextSize);
            curTextView.setTextColor(normalTextColor);
            lastTextView.setTextSize(normalTextSize);
            lastTextView.setTextColor(normalTextColor);
        }
        for(int i=0;i<lrcList.size();i++){
            if(currentPosition<lrcList.get(0).getLrcTime()){//第一句歌词还没播放
                curIndex = -1;
                curFocusTextSize = normalTextSize;
                lastTextViewSize = focusTextSize;
                if(curPause){
                    curStatus = Status.PAUSE;
                }else{
                    mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, lrcList.get(0).getLrcTime()-currentPosition);
                    curStatus = Status.PLAYING;
                }
                curIndex = 0;
                return ;
            }
            if(currentPosition>lrcList.get(lrcList.size()-1).getLrcTime()){//最后一句歌词播放完了
                curIndex = lrcList.size();//这里不是size - 1，因为我们实际的curIndex都是在当前显示的歌词上+1的
                if(curPause){
                    curStatus = Status.PAUSE;
                }else{
                    curStatus = Status.PLAYING;
                }
                mHandler.sendEmptyMessage(FLAG_ADJUST_SCROLLED_LYRICS);//立刻滑动到该位置
                return ;
            }
            if(currentPosition == lrcList.get(i).getLrcTime()){//刚好是这个位置，那么需要特殊处理
                curIndex = i;
                curFocusTextSize = normalTextSize;
                lastTextViewSize = focusTextSize;
                mHandler.sendEmptyMessage(FLAG_ADJUST_SCROLLED_LYRICS);//立刻滑动到该位置
                /*
                 * 这里虽然用的是lastTextView,但实际上是当前显示的textView（即curTextView)
                 * 主要原因在于，如果用curTextView的话，我们在curIndex++之后需要将
                 * curTextView设置为lastTextView，由于这个过程中curTextView不需要
                 * 使用，所以直接用lastTextView。
                 */
                lastTextView = mTextViewList.get(i);
                lastTextView.setTextColor(focusTextColor);
                lastTextView.setTextSize(focusTextSize);
                if(curIndex<lrcList.size()-1){
                    if(curPause){
                        curStatus = Status.PAUSE;
                    }else{
                        mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, lrcList.get(curIndex+1).getDelayTime());
                        curStatus = Status.PLAYING;
                    }
                    curIndex ++;
                }
                return ;
            }
            if(currentPosition>lrcList.get(i).getLrcTime()&&currentPosition<lrcList.get(i+1).getLrcTime()){
                curIndex = i;
//                System.out.println("curpisiont"+currentPosition+"i的"+lrcList.get(i).getLrcTime()+"i+1的"+lrcList.get(i+1).getLrcTime());
                currentPosition = mHelper.getAccurateTime()+offsetTime;
//                System.out.println("不是第一句的，延迟"+(lrcList.get(curIndex+1).getLrcTime()-currentPosition)+"当前时间"+ System.currentTimeMillis());
                curFocusTextSize = normalTextSize;
                lastTextViewSize = focusTextSize;
                lastTextView = mTextViewList.get(i);
                lastTextView.setTextColor(focusTextColor);
                lastTextView.setTextSize(focusTextSize);
                mHandler.sendEmptyMessage(FLAG_ADJUST_SCROLLED_LYRICS);//立刻滑动到该位置
                if(curPause){
                    curStatus = Status.PAUSE;
                }else{
                    mHandler.sendEmptyMessageDelayed(FLAG_UPDATE_PROGRESS, lrcList.get(curIndex+1).getLrcTime()-currentPosition);
                    curStatus = Status.PLAYING;
                }
                curIndex++;
                return ;
            }
        }
    }

    public void setHelper(AccurateTimeHelper helper) {
        mHelper = helper;
    }

    private AccurateTimeHelper mHelper;

    /**
     * 用于获取实时时间
     */
    public interface AccurateTimeHelper{
        long getAccurateTime();
    }

    /**
     * 清理工作
     */
    public void clear(){
        saveLyricsModifyToLocal();//检查歌词是否被修改过了
        mHasModify = false;
        playerPreparedAndLyricLoaded = false;
        removeAllViews();
        mHandler.removeCallbacksAndMessages(null);
        if(mTextViewList!=null){
            mTextViewList.clear();
            mTextViewList = null;
        }
        curStatus = Status.IDLE;
        curIndex = 0;
        offsetTime = 0;
    }


    private int firstY,firstX;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);//先按照原有的scrollView逻辑走
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                firstY = (int) ev.getY();
                firstX = (int) ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                int disY = (int) Math.abs(ev.getY() - firstY);
                if(disY>(int) Math.abs(ev.getX() - firstX)&&disY>5){
                    if(!mScroller.isFinished()){//如果切换歌词正在滑动，我们强制停止
                        mScroller.forceFinished(true);
                    }
                    isScrolling = true;
                    isAdjustScrolledLyrics = true;//用户操作时，我们不需要歌词自动滚动
                    if(mOnScrollEventListener!=null){
                        mOnScrollEventListener.onScrollMove();
                    }
                    mHandler.removeMessages(FLAG_ADJUST_SCROLLED_LYRICS);//由于滚动，我们不能让控件自动滑动
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(Math.abs((int) (ev.getY() - firstY))>10){//修正歌词位置
                    if(curStatus!= Status.IDLE){
                        isAdjustScrolledLyrics = true;
                        mHandler.sendEmptyMessageDelayed(FLAG_ADJUST_SCROLLED_LYRICS,3000);//重新发送新消息
                    }
                    if(mOnScrollEventListener!=null&&lrcList!=null&&lrcList.size()!=0){
                        mOnScrollEventListener.onScrollUp();
                        Message msg = Message.obtain();
                        msg.what = FLAG_LOCATE_SCROLLED_DISTANCE;
                        msg.arg1 = getScrollY();
                        mHandler.sendMessageDelayed(msg,50);
                    }
                }else{
                    isAdjustScrolledLyrics = false;
                }
                break;
        }
        return true;
    }

    // TODO: 2017/1/7 完成基本功能。后续需要完成歌词快进与后退 

    public static float sp2px(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    // TODO: 2017/1/12 定位中间

    /**
     * 获取屏幕正中央所在歌词的播放位置
     * 实现思路：通过getScroll/itemHeight来计算出显示在正中央歌词的id
     * 问题：getScrollY在快速滑动状态下会出问题
     * 解决(on 2017/1/19):我们等到惯性滑动完成再getScrollY即可。所以本方法需要在Moveup之后一段时间才调用
     * @return lrcProgressPosition
     */
    private long getScreenCenterPosition(){
        int id = getScrollY() / itemHeight;
        return lrcList.get((id<lrcList.size())?id:(lrcList.size()-1)).getLrcTime();
    }

    /**
     * 在高亢歌词布局中显示的情况下，调用该方法使高亢歌词居中
     * 场景：屏幕解锁高亢歌词位置不居中
     * 使用：在activity的onStart()可见界面方法中调用本方法最佳
     */
    public void moveFocusLyricsToCenter(){
        mHandler.sendEmptyMessage(FLAG_ADJUST_SCROLLED_LYRICS);
    }

    // TODO: 2017/1/12 getScreenCenterPosition()方法的问题
    // TODO: 2017/1/19 解决问题：经过分析，发现问题只有在快速滑动时才会产生，因此，问题肯定与快速滑动有关。于是我们联想到惯性滑动导致，所以做出修改
    // TODO: 2017/1/20 问题 歌词偶尔出问题、中央控件 
}
