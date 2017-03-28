package me.wuwenbin.dao.sql.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some utils of sql building
 * <p>
 * Created by wuwenbin on 2017/1/9.
 */
public class SQLDefineUtils {

    /**
     * 驼峰命名转下划线
     *
     * @param param to transfer
     * @return {@link String}
     */
    private static String camelToUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        } else {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c))
                    sb.append('_').append(Character.toLowerCase(c));
                else sb.append(c);
            }
            return sb.toString();
        }
    }

    /**
     * 下划线转驼峰命名
     *
     * @param param to transfer
     * @return {@link String}
     */
    public static String underline2Camel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(param);
            Matcher mc = Pattern.compile("_").matcher(param);
            int i = 0;
            while (mc.find()) {
                int position = mc.end() - (i++);
                sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
            }
            return sb.toString();
        }
    }

    /**
     * 如果用户指定了column的列名就按用户指定的来，没有则驼峰命名转化为下划线命名
     *
     * @param column the column of db
     * @param fieldName the custom name of column
     * @return {@link String}
     */
    public static String java2SQL(String column, String fieldName) {
        if (column == null || column == "") {
            String newName = camelToUnderline(fieldName);
            if (newName.substring(0, 1).equals("_"))
                return newName.substring(1, newName.length());
            else return newName;
        } else return column;

    }

}
