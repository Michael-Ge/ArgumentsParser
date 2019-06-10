package com.ge81.tool;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串常用工具
 * @author Michael Ge
 * @date 2018/5/25
 */
public class StringUtils {
    private final static String[] HEX_CHARACTER_TABLE = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
    private final static int HEX_VALUE_TABLE[] = new int[256];
    
    static {
        Arrays.fill(HEX_VALUE_TABLE,(byte)0);

        int length = HEX_CHARACTER_TABLE.length;
        for (int i = 0;i<length;i++){
            String c = HEX_CHARACTER_TABLE[i];
            HEX_VALUE_TABLE[c.charAt(0)] = (byte)i;
            HEX_VALUE_TABLE[c.toLowerCase().charAt(0)] = (byte)i;
        }
    }
    
    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    /**
     * 获取字符串的长度
     * @param str 字符串对象
     * @return null 或 空串返回 0，否则返回字符串长度
     */
    public static int getStringLength(Object str){
        if (str instanceof String){
            return ((String) str).length();
        }
        else {
            return 0;
        }
    }

    /**
     * 安全的比较两个字符串是否相等，字符串可以传空，两个 null 相比较返回 true。
     *
     * @param str1  字符串1
     * @param str2  字符串2
     * @return 比较结果，两个 null 相比较返回 true。
     */
    public static boolean isEqualIgnoreCase(String str1, String str2){
        if (str1 == str2){
            return true;
        }

        if (str1 != null && str2 != null && str1.length() == str2.length()){
            return str1.equalsIgnoreCase(str2);
        }

        return false;
    }

    /**
     * 安全的比较两个字符串是否相等，字符串可以传空，两个 null 相比较返回 true。
     *
     * @param str1  字符串1
     * @param str2  字符串2
     * @return 比较结果，两个 null 相比较返回 true。
     */
    public static boolean isEqual(String str1, String str2){
        if (str1 == str2){
            return true;
        }

        if (str1 != null && str2 != null && str1.length() == str2.length()){
            return str1.equals(str2);
        }

        return false;
    }

    /**
     * 判断是否是纯数字字符串
     * @param str 字符串
     * @return 如果是纯数字字符串返回 true，否则返回 false
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) return false;
        
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum   = pattern.matcher(str);
        return isNum.matches();
    }
    /**
     * 安全的将字符串转 double 类型，如果转换失败则返回 Double.NaN
     *
     * @param string 待转换的字符串
     * @return 如果转换失败则返回 Double.NaN
     */
    public static double str2Double(String string) {
        return str2Double(string, Double.NaN);
    }

    /**
     * 安全的将字符串转 double 类型，如果转换失败则返回 defaultValue
     *
     * @param string       待转换的字符串
     * @param defaultValue 缺省值
     * @return 如果转换失败则返回 defaultValue
     */
    public static double str2Double(String string, double defaultValue) {
        if (isEmpty(string)) {
            return defaultValue;
        }
        double temp;
        try {
            temp = Double.parseDouble(string);
        } catch (Exception e) {
            temp = defaultValue;
        }
        return temp;
    }

    /**
     * 安全的将字符串转 Int 类型，如果转换失败则返回 0
     *
     * @param string 待转换的字符串
     * @return 如果转换失败则返回 0
     */
    public static int str2Int(String string) {
        return str2Int(string, 0);
    }

    /**
     * 安全的将字符串转 Int 类型，如果转换失败则返回 defaultValue
     *
     * @param string       待转换的字符串
     * @param defaultValue 缺省值
     * @return 如果转换失败则返回 0
     */
    public static int str2Int(String string, int defaultValue) {
        if (isEmpty(string)) {
            return defaultValue;
        }
        int temp;
        try {
            temp = Integer.parseInt(string);
        } catch (Exception e) {
            temp = defaultValue;
        }
        return temp;
    }

    public static int obj2Int(Object obj, Integer def){
        if(obj!=null){
            String val = String.valueOf(obj);
            if(val.matches("[0-9]+")){
                return Integer.parseInt(val);
            }
        }
        return def;
    }

    /**
     * 安全的将字符串转 long 类型，如果转换失败则返回 0
     *
     * @param string 待转换的字符串
     * @return 如果转换失败则返回 0
     */
    public static long str2Long(String string) {
        return str2Long(string, 0);
    }

    /**
     * 安全的将字符串转 Int 类型，如果转换失败则返回 defaultValue
     *
     * @param string       待转换的字符串
     * @param defaultValue 缺省值
     * @return 如果转换失败则返回 缺省值
     */
    public static long str2Long(String string, long defaultValue) {
        if (isEmpty(string)) {
            return defaultValue;
        }
        long temp;
        try {
            temp = Long.parseLong(string);
        } catch (Exception e) {
            temp = defaultValue;
        }
        return temp;
    }

    /**
     * @param src 字节组
     * @return 16进制字符串表达
     */
    public static String bytes2HexString(byte[] src) {
        if (src == null){
            return null;
        }
        StringBuilder hexString = new StringBuilder();
        for (byte b : src) {
            hexString.append(HEX_CHARACTER_TABLE[(b >> 4) & 0xF]);
            hexString.append(HEX_CHARACTER_TABLE[b & 0x0F]);
        }
        return hexString.toString();
    }

    /**
     * 十六进制字符串转字节
     *
     * @param hexString 十六进制字符串
     * @return 输出十进制数据
     */
    public static byte[] hexString2bytes(String hexString) {
        int length = hexString == null ? 0 : hexString.length();

        if (length == 0){
            return new byte[0];
        }

        if (length % 2 != 0){
            hexString += "0";
            length += 1;
        }

        byte[] out = new byte[length / 2];

        for (int n = 0; n < length; n += 2) {
            char h = hexString.charAt(n);
            char l = hexString.charAt(n + 1);
            out[n >> 1] = (byte)(HEX_VALUE_TABLE[h] << 4 | HEX_VALUE_TABLE[l]);
        }

        return out;
    }
}
