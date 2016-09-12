package com.tylz.aelos.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.tylz.aelos.bean.Status;

import java.util.List;

/**
 * @项目名: Aelos1
 * @包名: com.gxy.utils
 * @类名: CommUtils
 * @创建者: 陈选文
 * @创建时间: 2016-6-26 下午9:37:34
 * @描述: TODO
 *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */
public class CommUtils {
    public static long getTotalPlayTime(List<Status> statusList){
        long totalTime = 0;
        for(int i = 1; i < statusList.size(); i++){
            Status statusPre = statusList.get(i-1);
            Status statusNext = statusList.get(i);
            long   playTime = getBetweenTwoStatusPlayTime(statusPre, statusNext);
            totalTime += playTime;
        }
        return totalTime;
    }
    public static long getBetweenTwoStatusPlayTime(Status statusPre, Status statusNext) {
        long totalTime = 0;
        int max       = statusNext.arr[0] - statusPre.arr[0];

        for (int i = 0; i < 16; i++) {
            if (statusNext.arr[i] - statusPre.arr[i] > max) {
                max = statusNext.arr[i] - statusPre.arr[i];
            }
        }
        if (max < 0) {
            max = 0;
        }
        totalTime = max * 100 / 9 / statusNext.progress;
        return totalTime;
    }

    public static int[] str2arr(String result) {
        int[] arr = new int[16];
        //result = "92000000000a0000501f65645e387c65beab65656c914d64";
        if (result.length() != 48) {
            return null;
        }
        result = result.substring(16);
        try {
            for (int i = 0, j = 0; j < 16; i = i + 2, j++) {
                String hex = result.substring(i, i + 2);
                arr[j] = Integer.parseInt(hex, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return arr;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName()
                               .equals("WIFI") && info[i].isConnected())
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 得到act文件的长度，去掉.act后的
     *
     * @param fileName
     * @return
     */
    public static String getAsciiActNamelength(String fileName)
    {
        String actName = toAsciiByActName(fileName);
        String len     = Integer.toHexString(actName.length() / 2);
        if (len.length() == 1) {
            len = "0" + len;
        }
        return len;
    }

    public static String toAsciiByActName(String fileName)
    {
        fileName = fileName.replace(".act", "");
        return StringToAscii.parseAscii(fileName);
    }

    /**
     * 长度 + ascii 不带.act的
     *
     * @param fileName
     * 		动作的filename
     * @return
     * 		长度 + ascii 不带.act的
     */
    public static String toAsciiAddLen(String fileName)
    {
        return getAsciiActNamelength(fileName) + toAsciiByActName(fileName);
    }

    /**
     * 生成act相关东西
     *
     * @return
     * @param fileName
     *            文件名
     */
    public static String toActString(String fileName, int process, List<Integer[]> arrs)
    {
        String str    = "AAAA";
        String ascill = StringToAscii.parseAscii(fileName);
        // 拼接长度
        String length = Integer.toHexString(ascill.length() / 2);
        if (length.length() == 1) {
            length = "0" + length;
        }
        str = str + length + ascill;
        // AAAA + 0e + 313436343333313536322E616374

        // 拼接 状态值
        for (Integer[] arr : arrs) {
            // 拼接 81
            String processLength = Integer.toHexString(process);
            if (processLength.length() == 1) {
                processLength = "0" + processLength;
            }
            str = str + "81" + processLength; // 拼接了速度
            str = str + "8020";
            for (int i : arr) {
                String iLen = Integer.toHexString(i);
                if (iLen.length() == 1) {
                    iLen = "0" + iLen;
                }
                str = str + iLen;
            }
            str = str + "82";
        }
        str = str + "49";
        int    byteLen1 = (str.length() / 2) / 512;
        String heigh    = "" + Integer.toHexString(byteLen1);
        if (heigh.length() == 1) {
            heigh = "0" + heigh;
        }
        System.out.println("heigh = " + heigh);
        int    bytelen2 = (str.length() / 2) % 512;
        String low      = "" + Integer.toHexString(bytelen2);
        if (low.length() == 1) {
            low = "0" + low;

        }
        System.out.println("low = " + low);
        int y = 1024 - str.length() - 16;
        for (int i = 0; i < y; i++) {
            str = str + "0";
        }

        String s = "70010001" + heigh + low + "0000";
        str = s + str;
        System.out.println(str);
        return str;
    }

    /**
     * 生成act相关东西
     *
     * @return
     * @param fileName
     *            文件名
     */
    public static String toActString11(String fileName, List<Status> arrs)
    {
        String str    = "AAAA";
        String ascill = StringToAscii.parseAscii(fileName);
        // 拼接长度
        String length = Integer.toHexString(ascill.length() / 2);
        if (length.length() == 1) {
            length = "0" + length;
        }
        str = str + length + ascill;
        // AAAA + 0e + 313436343333313536322E616374

        // 拼接 状态值
        for (Status status : arrs) {
            // 拼接 81
            Log.d("chen", "speed = " + status);
            String processLength = Integer.toHexString(status.progress);
            if (processLength.length() == 1) {
                processLength = "0" + processLength;
            }

            str = str + "81" + processLength; // 拼接了速度
            str = str + "8020";
            for (int i : status.arr) {
                String iLen = Integer.toHexString(i);
                if (iLen.length() == 1) {
                    iLen = "0" + iLen;
                }
                str = str + iLen;
            }
            str = str + "82";
        }
        str = str + "49";
        int    byteLen1 = (str.length() / 2) / 512;
        String heigh    = "" + Integer.toHexString(byteLen1);
        if (heigh.length() == 1) {
            heigh = "0" + heigh;
        }
        System.out.println("heigh = " + heigh);
        int    bytelen2 = (str.length() / 2) % 512;
        String low      = "" + Integer.toHexString(bytelen2);
        if (low.length() == 1) {
            low = "0" + low;

        }
        System.out.println("low = " + low);
        int y = 1024 - str.length() - 16;
        for (int i = 0; i < y; i++) {
            str = str + "0";
        }

        String s = "70010001" + heigh + low + "0000";
        str = s + str;
        System.out.println(str);
        return str;
    }

    /**
     * 将一个数组转化成16进制字符串
     *
     * @param arr
     * @return
     */
    public static String arr2String(int[] arr)
    {
        String str = "";
        for (int i = 0; i < arr.length; i++) {
            String string = Integer.toHexString(arr[i]);
            if (string.length() == 1) {
                string = "-" + string;
            }
            str = str + string;
        }
        return str;
    }


}
