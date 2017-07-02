package com.hudson.donglingmusic.utils;

/**
 * Created by Hudson on 2017/6/4.
 */

public class StringTextUtils {

    /**
     * 返回拥有颜色属性的Html字符串
     * @return
     * @param srcString
     * @param tag  需要改变颜色的词被什么tag包住
     * @param color 例如ff0000
     */
    public static String getHtmlStringWithColorElement(String srcString,String tag,String color){
        String modifyString = srcString.replaceAll("<"+tag+">","<font color='#"+color+"'>");
        modifyString = modifyString.replaceAll("</"+tag+">","</font>");
        return modifyString;
    }

    /**
     * 回拥有颜色属性的Html字符串
     * @param srcString
     * @param word 强调的词语或字
     * @return
     */
    public static String getHtmlStringWithColorElement(String srcString,String word,int color){
        return srcString.replaceAll(word,"<font color='"+color+"'>"+word+"</font>");
    }

    public static String recoverStringFromHtmlString(String srcString){
        if(srcString.contains("<")){
            int startIndex = srcString.indexOf("<");
            int endIndex = srcString.indexOf("font>");
            String needReplace =  srcString.substring(startIndex,endIndex+5);
            endIndex = srcString.indexOf("<",startIndex+4);
            startIndex = srcString.indexOf(">");
            String word = srcString.substring(startIndex+1,endIndex);
            needReplace = srcString.replaceAll(needReplace,word);
            return needReplace;
        }
        return null;
    }
}
