package com.cloudminds.roc;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by cloud on 2019/3/13.
 */

public class SoundPoolPlayer {
    private static SoundPoolPlayer soundPoolPlayer;
    SoundPool sp;
    HashMap<Integer, Integer> sounddata;
    Context mcontext;
    Boolean isLoaded;
    private String TAG = "SoundPoolPlayer";

    public static SoundPoolPlayer getInstance(){
        if(soundPoolPlayer==null) {
            soundPoolPlayer = new SoundPoolPlayer();
        }
        return soundPoolPlayer;
    }

    private SoundPoolPlayer() {
    }
    //初始化声音
    public void initSound(Context context) {
        this.mcontext=context;
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        sounddata = new HashMap<Integer, Integer>();
        sounddata.put(1, sp.load(mcontext, R.raw.attribute, 1));
        sounddata.put(2, sp.load(mcontext, R.raw.config, 1));
        sounddata.put(3, sp.load(mcontext, R.raw.metrics, 1));
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
            @Override
            public void onLoadComplete(SoundPool sound,int sampleId,int status){
                isLoaded=true;
                Log.d(TAG, "onLoadComplete: ");;
            }
        });
    }
    public void playSound(int sound, int number) {
        Log.d(TAG, "playSound: "+isLoaded);
        if(!isLoaded){
            throw new IllegalStateException("未加载音频文件或加载失败");
        }
        AudioManager am = (AudioManager) mcontext
                .getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        Log.e(TAG, "playSound: "+sound );
        sp.play(sounddata.get(sound),
                0.5f,// 左声道音量
                0.5f,// 右声道音量
                1, // 优先级
                number,// 循环播放次数
                1);// 回放速度，该值在0.5-2.0之间 1为正常速度
        Log.d(TAG, "playSound: "+sound);
    }

}
