package com.hudson.donglingmusic.utils;

import com.hudson.donglingmusic.db.MusicInfo;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PinYinUtil {
    static HanyuPinyinOutputFormat outputFormat;

    static {
        try {
            outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
            outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 汉字返回拼音，字母原样返回 必须在Android中使用
     *
     * @param source stirng
     * @return string
     */

    public static String getFullPinYin(String source) {
        StringBuilder sb = new StringBuilder();

        if (source.length() > 0) {
            for (int i = 0; i < source.length(); i++) {
                try {

                    String[] arrays = PinyinHelper.toHanyuPinyinStringArray(
                            source.charAt(i), outputFormat);
                    if (arrays != null && arrays.length > 0) {
                        sb.append(arrays[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    /**
     * 获取汉字拼音首字母大写 必须在Android中使用
     *
     * @param source string
     * @return string
     */
    public static String getFirstPinYin(String source) {

        // StringBuilder sb = new StringBuilder();

        if (source.length() > 0) {

            if (!isEnglish(source)) {
                try {
                    String[] arrays = PinyinHelper.toHanyuPinyinStringArray(
                            source.charAt(0), outputFormat);
                    if (arrays != null && arrays.length > 0) {
                        return arrays[0];
                    }
                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }
            } else {
                return String.valueOf(source.charAt(0));
            }

        }
        return "#";
    }

    public static boolean isEnglish(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对musics的集合按照字母排序（中英混合）
     * @param musics
     * @return
     */
    public static ArrayList<MusicInfo> sortMusics(ArrayList<MusicInfo> musics){
        if(musics == null){
            return null;
        }
        Collections.sort(musics, new Comparator<MusicInfo>() {
            /*
             * 返回负数表示：o1 小于o2， 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
             */
            @Override
            public int compare(MusicInfo lhs, MusicInfo rhs) {
                char l = Character.toLowerCase(PinYinUtil.getFirstPinYin(lhs.getTitle()).charAt(0));
                char s = Character.toLowerCase(PinYinUtil.getFirstPinYin(rhs.getTitle()).charAt(0));
                if (l == s ||Math.abs(l-s) == 32 ) {
                    return 0;
                } else if (l > s) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return musics;
    }


}
