package com.tylz.aelos.util;

import android.util.Xml;

import com.tylz.aelos.bean.UpdateInfo;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.util
 *  @文件名:   UpdateInfoParser
 *  @创建者:   陈选文
 *  @创建时间:  2016/9/2 17:06
 *  @描述：    TODO
 */
public class UpdateInfoParser {
    public static UpdateInfo getUpdateInfo(String result)
            throws Exception
    {
        XmlPullParser parser = Xml.newPullParser();
        InputStream is = new ByteArrayInputStream(result.getBytes("UTF-8"));
        parser.setInput(is, "utf-8");
        int        type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if ("version".equals(parser.getName())) {
                        info.version = parser.nextText();
                    } else if ("url".equals(parser.getName())) {
                        info.url = parser.nextText();
                    } else if ("description".equals(parser.getName())) {
                        info.description = parser.nextText();
                    }
                    break;
            }
            type = parser.next();
        }
        return info;
    }
}
