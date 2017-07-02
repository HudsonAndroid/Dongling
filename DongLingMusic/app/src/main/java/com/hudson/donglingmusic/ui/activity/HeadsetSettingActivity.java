package com.hudson.donglingmusic.ui.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.global.MySharePreferences;

/**
 * Created by Hudson on 2017/4/20.
 * 耳机按键设置（注意：该事件的广播必须是全局广播，所以必须在Manifest文件中处理）
 */

public class HeadsetSettingActivity extends BaseNormalActivity {
    private static final String[] mChoices={"暂停、播放","下一曲","上一曲","取消该功能"};
    private MySharePreferences mInstance;

    @Override
    public View initView() {
        setActivityTitle(getString(R.string.headset_setting));
        View v = View.inflate(this, R.layout.activity_headset_setting,null);
        mInstance = MySharePreferences.getInstance();
        Spinner mSpinner = (Spinner) v.findViewById(R.id.spinner1);
        Spinner mSpinner2 = (Spinner) v.findViewById(R.id.spinner2);
        Spinner mSpinner3 = (Spinner) v.findViewById(R.id.spinner3);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.item_spinner,mChoices);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        mSpinner.setAdapter(adapter);
        mSpinner2.setAdapter(adapter);
        mSpinner3.setAdapter(adapter);
        mSpinner.setSelection(mInstance.getHeadsetOnePressed());
        //添加事件Spinner事件监听
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mInstance.saveHeadsetOnePressed(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //设置默认值
        mSpinner.setVisibility(View.VISIBLE);
        //添加事件Spinner事件监听
        int headsetTwoPressed = mInstance.getHeadsetTwoPressed();
        System.out.println("两次按下是"+headsetTwoPressed);
        mSpinner2.setSelection(headsetTwoPressed);
        mSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mInstance.saveHeadsetTwoPressed(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //设置默认值
        mSpinner2.setVisibility(View.VISIBLE);

        //添加事件Spinner事件监听
        mSpinner3.setSelection(mInstance.getHeadsetLongClick());
        mSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                mInstance.saveHeadsetLongClick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //设置默认值
        mSpinner3.setVisibility(View.VISIBLE);
        return v;
    }
}
