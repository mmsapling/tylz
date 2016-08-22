package com.tylz.aelos.util;

import android.media.MediaPlayer;

import com.tylz.aelos.manager.Constants;

import java.io.IOException;
import java.util.List;

/*
 *  @项目名：  Aelos 
 *  @包名：    com.tylz.aelos.util
 *  @文件名:   MusicPlayerUtils
 *  @创建者:   陈选文
 *  @创建时间:  2016/8/17 19:52
 *  @描述：    动作音乐播放工具
 */
public class MusicPlayerUtils {
    private MediaPlayer mMediaPlayer;
    private List<String> mMusics;
    private boolean   isPlaying;
    public void setMusics(List<String> musics) {
        mMusics = musics;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    /**
     * 是否正在播放
     * @return
     *      播放中返回true
     */
    public boolean isPlaying() {
        return isPlaying;
    }


    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }
    public MusicPlayerUtils() {
        isPlaying = false;
        mMediaPlayer = new MediaPlayer();
    }
    public void play(){
        if(mMusics == null || mMusics.size() == 0){
            return;
        }
        isPlaying = true;
        mMediaPlayer.reset();
        String path = Constants.DIR_AUDIO + mMusics.get(0);
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mMusics.size() > 0){
                        mMusics.remove(0);
                        if(mMusics.size() > 0){
                            play();
                        }else{
                            isPlaying = false;
                        }
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        isPlaying = false;
        mMediaPlayer.stop();
    }
    public void clear(){
        isPlaying = false;
        if(mMusics != null){
            mMusics.clear();
        }
        mMediaPlayer.stop();
    }
}
