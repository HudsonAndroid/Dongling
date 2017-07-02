package com.hudson.donglingmusic.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.db.MusicInfo;
import com.hudson.donglingmusic.global.MyApplication;
import com.hudson.donglingmusic.ui.adapter.MyFilterAdapter;
import com.hudson.donglingmusic.ui.itempager.LocalItemPager;
import com.hudson.donglingmusic.utils.StringTextUtils;

import java.util.ArrayList;

/**
 * 列表搜索，并非网络搜索
 */
public class ListSearchActivity extends Activity  {
    private AutoCompleteTextView mAutoCompleteTextView;
    private ArrayList<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListSearchActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        if(intent==null){
            finish();
        }else{
            mAutoCompleteTextView = (AutoCompleteTextView) this.findViewById(R.id.act_search);
            String listInfo = intent.getStringExtra("listInfo");
            if(listInfo.equals(LocalItemPager.LOCAL_LIST_INFO)){
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                mAutoCompleteTextView.requestFocus();//请求焦点
                // 接受软键盘输入的编辑文本或其它视图
                inputMethodManager.showSoftInput(mAutoCompleteTextView,InputMethodManager.SHOW_FORCED);
                ArrayList<MusicInfo> musics = MyApplication.getMyApplication().getAllList();
                mDatas = new ArrayList<>();
                for (MusicInfo musicInfo : musics) {
                    mDatas.add(musicInfo.getContentString());
                }
                MyFilterAdapter<String> filterAdapter = new MyFilterAdapter<>(this,R.layout.item_autocomplete_edit,mDatas);
                mAutoCompleteTextView.setAdapter(filterAdapter);
                mAutoCompleteTextView.enoughToFilter();
                mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int index = getPositionByContent(parent.getItemAtPosition(position).toString());// 必须这样获取内容
                        Intent backIntent = new Intent();
                        System.out.println("位置"+index);
                        backIntent.putExtra("position",index);//放返回的数据。。。
                        setResult(RESULT_OK,backIntent);//RESULT_OK是在Activity原系统源码中定义的
                        finish();
                    }
                });
            }
        }
    }

    private int getPositionByContent(String content){
        content = StringTextUtils.recoverStringFromHtmlString(content);
        if(mDatas!=null){
            for (int i = 0; i < mDatas.size(); i++) {
                if(mDatas.get(i).equals(content)){
                    return i;
                }
            }
        }
        return -1;
    }

}
