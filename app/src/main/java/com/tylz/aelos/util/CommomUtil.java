package com.tylz.aelos.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.tylz.aelos.bean.User;
import com.tylz.aelos.bean.UserBean;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.util
 *  @文件名:   CommomUtil
 *  @创建者:   陈选文
 *  @创建时间:  2016/7/24 22:18
 *  @描述：    封装常用方法
 */
public class CommomUtil {
    /**
     * 获取视频第一帧
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap                 bitmap    = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int                    kind      = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                                                     width,
                                                     height,
                                                     ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /**
     * userBean 转化为user
     * @param user
     * @param pwd
     * @return
     */
    public static User bean2user(UserBean user, String pwd) {
        User useinfo = new User();
        useinfo.phone = user.phone;
        useinfo.id = user.id;
        useinfo.avatar = user.avatar;
        useinfo.nickname = user.nickname;
        useinfo.gender = user.gender;
        useinfo.address = user.address;
        useinfo.selfInfo = user.selfInfo;
        useinfo.qq = user.qq;
        useinfo.mailbox = user.mailbox;
        useinfo.birth = user.birth;
        useinfo.age = user.age;
        useinfo.hobby = user.hobby;
        useinfo.password = pwd;
        return useinfo;

    }

    /**
     * 从网络音频路径中获取文件名
     * @param url
     * @return
     */
    public static String getFileNameByUrl(String url) {
        if(TextUtils.isEmpty(url)){
            return "";
        }
        try{
            int i = url.lastIndexOf("/");
            return url.substring(i + 1);
        }catch (Exception e){

        }
        return "";
    }

    /**
     * 字节数组转十六进制
     * @param src
     *      字节数组
     * @return
     *      十六进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int    v  = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制转化为字节数组
     * @param hexString
     *         十六进制字符串
     * @return
     *          字节数组
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int    length   = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d        = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * Toast封装方法
     * @param resId
     *      资源id
     */
    public static void showToast(int resId) {
        String str = UIUtils.getString(resId);
        Toast.makeText(UIUtils.getContext(), str, Toast.LENGTH_SHORT)
             .show();
    }
    public static int getAge(Date birthDay) throws Exception {
        Calendar cal = Calendar.getInstance();

        if (cal.before(birthDay)) {
            throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");
        }

        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                //monthNow==monthBirth
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                } else {
                    //do nothing
                }
            } else {
                //monthNow>monthBirth
                age--;
            }
        } else {
            //monthNow<monthBirth
            //donothing
        }

        return age;
    }
}
