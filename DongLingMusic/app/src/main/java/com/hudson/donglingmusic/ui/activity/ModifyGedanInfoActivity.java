package com.hudson.donglingmusic.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudson.donglingmusic.R;
import com.hudson.donglingmusic.bean.MyGedanBean;
import com.hudson.donglingmusic.global.MySharePreferences;
import com.hudson.donglingmusic.utils.DialogUtils;
import com.hudson.donglingmusic.utils.ToastUtils;
import com.hudson.donglingmusic.utils.UIUtils;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

import static com.hudson.donglingmusic.ui.itempager.MyGedanItemPager.MY_GEDAN_DATABASE;

/**
 * Created by Hudson on 2017/4/20.
 * 修改歌单信息界面
 */

public class ModifyGedanInfoActivity extends BaseNormalActivity {
    private ImageView mBgImage;
    private TextView mTitle,mTag,mDesc;
    private MySharePreferences mInstance;
    private boolean mHasModify = false;
    private String mSrcTitle;
    private String mModifyTitle,mModifyTag,mModifyDesc,mModifyImagePath;
    public static final String MY_GEDAN_DATABASE_CHANGE = "com.hudson.donglingmusic.mygedan_change";

    @Override
    public View initView() {
        Intent intent = getIntent();
        if(intent!=null){
            mModifyTitle = mSrcTitle = intent.getStringExtra("title");
            mModifyTag = intent.getStringExtra("tag");
            mModifyDesc = intent.getStringExtra("desc");
            mModifyImagePath = intent.getStringExtra("imagePath");
            mInstance = MySharePreferences.getInstance();
            setActivityTitle(getString(R.string.modify_gedan_info));
            View v = View.inflate(this, R.layout.activity_modify_gedan_info,null);
            v.findViewById(R.id.ll_change_bg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getImageIntent.setType("image/*");
                    getImageIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(getImageIntent,1);

                }
            });
            mBgImage = (ImageView) v.findViewById(R.id.iv_gedan_bg);
            if(!TextUtils.isEmpty(mModifyImagePath)){
                mBgImage.setImageBitmap(BitmapFactory.decodeFile(mModifyImagePath));
            }
            v.findViewById(R.id.ll_gedan_title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showEditInfoDialog(ModifyGedanInfoActivity.this, DialogUtils.TYPE_MODIFY_GEDAN_TITLE,
                            UIUtils.getString(R.string.gedan_title_should_not_empty), "修改歌单名称", mModifyTitle,"请输入歌单名", new Runnable() {
                                @Override
                                public void run() {
                                    String newGedanTitle = mInstance.getNewGedanTitle();
                                    mModifyTitle = newGedanTitle;
                                    mTitle.setText(newGedanTitle);
                                }
                            });
                    mHasModify = true;
                }
            });
            mTitle = (TextView) v.findViewById(R.id.tv_gedan_title);
            mTitle.setText(mSrcTitle);
            v.findViewById(R.id.ll_gedan_tag).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showEditInfoDialog(ModifyGedanInfoActivity.this, DialogUtils.TYPE_MODIFY_GEDAN_TAG,
                            UIUtils.getString(R.string.gedan_tag_empty), "修改歌单标签",mModifyTag, "请输入标签", new Runnable() {
                                @Override
                                public void run() {
                                    String newGedanTag = mInstance.getNewGedanTag();
                                    mModifyTag = newGedanTag;
                                    mTag.setText(newGedanTag);
                                }
                            });
                    mHasModify = true;
                }
            });
            mTag = (TextView) v.findViewById(R.id.tv_gedan_tag);
            if(!TextUtils.isEmpty(mModifyTag)){
                mTag.setText(mModifyTag);
            }
            v.findViewById(R.id.ll_gedan_desc).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这里sharePreference共用了title
                    DialogUtils.showEditBigInfoDialog(ModifyGedanInfoActivity.this, DialogUtils.TYPE_MODIFY_GEDAN_DESC,
                            UIUtils.getString(R.string.gedan_desc_empty), "编辑歌单描述",mModifyDesc, "请输入描述", new Runnable() {
                                @Override
                                public void run() {
                                    String newGedanTitle = mInstance.getNewGedanTitle();
                                    mModifyDesc = newGedanTitle;
                                    mDesc.setText(newGedanTitle);
                                }
                            });
                    mHasModify = true;
                }
            });
            mDesc = (TextView) v.findViewById(R.id.tv_gedan_desc);
            if(!TextUtils.isEmpty(mModifyDesc)){
                mDesc.setText(mModifyDesc);
            }
            return v;
        }else{
            finish();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK&&data!=null){
            Uri uri = data.getData();//得到返回的data数据
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String imagePath = cursor.getString(columnIndex);
            if(imagePath == null){
                ToastUtils.showToast("图片选取失败，可能选取结果不兼容，请更换选取方式！");
            }else{
                if(imagePath.endsWith("png")|imagePath.endsWith("jpg")|imagePath.endsWith("JPG")|
                        imagePath.endsWith("BMP")|imagePath.endsWith("jpeg")|imagePath.endsWith("JPEG")){
                    Intent cropIntent = new Intent(ModifyGedanInfoActivity.this,ChoosePicturePartActivity.class);
                    cropIntent.putExtra("srcImagePath",imagePath);
                    cropIntent.putExtra("title",mModifyTitle);
                    startActivityForResult(cropIntent,2);
                }else {
                    ToastUtils.showToast("所选文件并非图片！");
                }
            }
            //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
            if(Integer.parseInt(Build.VERSION.SDK) < 14)
            {
                cursor.close();
            }
        }else if(requestCode==2&&resultCode==RESULT_OK&&data!=null){
            try{
                String path = data.getStringExtra("path");
                if(!TextUtils.isEmpty(path)){
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    mBgImage.setImageBitmap(bm);
                    mModifyImagePath = path;
                    mHasModify = true;
                }
            }catch (Exception e){
                e.printStackTrace();
                ToastUtils.showToast("背景图片设置失败，请检查文件是否是图片！");
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHasModify){
            LitePalDB litePalDB = new LitePalDB(MY_GEDAN_DATABASE, 1);
            litePalDB.addClassName(MyGedanBean.class.getName());
            LitePal.use(litePalDB);
            MyGedanBean myGedanBean = new MyGedanBean();
            myGedanBean.setDesc(mModifyDesc);
            myGedanBean.setTag(mModifyTag);
            myGedanBean.setTitle(mModifyTitle);
            myGedanBean.setImagePath(mModifyImagePath);
            myGedanBean.updateAll("mtitle = ?", mSrcTitle);//修改
            Intent intent = new Intent(MY_GEDAN_DATABASE_CHANGE);
            intent.putExtra("title",mModifyTitle);
            intent.putExtra("tag",mModifyTag);
            intent.putExtra("desc",mModifyDesc);
            intent.putExtra("imagePath",mModifyImagePath);
            sendBroadcast(intent);
        }
    }
}
