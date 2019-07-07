package cn.lang.spring.framework.support;

public class StringUtil {

    public static String lowFirst(String str){
        if (str==null) return null;
        return str.substring(0,1).toLowerCase()+str.substring(1);
    }
}
