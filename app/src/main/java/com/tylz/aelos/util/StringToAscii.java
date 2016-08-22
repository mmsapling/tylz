package com.tylz.aelos.util;


/**
 * @项目名: 	Aelos1
 * @包名:	com.gxy.utils
 * @类名:	StringToAscii
 * @创建者:	陈选文
 * @创建时间:	2016-6-28	下午7:51:11 
 * @描述:	将字符串转化成Ascii
 * 
 * @svn版本:	$Rev$
 * @更新人:	$Author$
 * @更新时间:	$Date$
 * @更新描述:	TODO
 */
public class StringToAscii
{
	private static String toHexUtil(int n){
        String rt="";
        switch(n){
        case 10:rt+="A";break;
        case 11:rt+="B";break;
        case 12:rt+="C";break;
        case 13:rt+="D";break;
        case 14:rt+="E";break;
        case 15:rt+="F";break;
        default:
            rt+=n;
        }
        return rt;
    }
    
    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }
    
    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }

}
