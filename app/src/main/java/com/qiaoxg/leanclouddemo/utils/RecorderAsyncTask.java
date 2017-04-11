package com.qiaoxg.leanclouddemo.utils;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.qiaoxg.leanclouddemo.MyConstants.AUDIO_TEMP_FILE_NAME;

/**
 * Created by admin on 2017/3/27.
 */

public class RecorderAsyncTask {

    private static final String TAG = "RecorderAsyncTask";

    //    private AudioRecord mAudioRecord;
    private static MediaRecorder iMediaRecorder;

    public RecorderAsyncTask() {
        initRecord();
    }

    protected void initRecord() {
//        Log.e(TAG, "initRecord: start init");
//        if (mAudioRecord == null) {
//            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                    8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                    AudioFormat.ENCODING_PCM_16BIT, 8000 * 6);
//            Log.e(TAG, "initRecord: success");
//        }
    }

//    public boolean startRecord() {
//        boolean isOk = true;
//        int frequency = 11025;
//        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//        File file = new File(FileUtils.getLocalAudioDir() + AUDIO_TEMP_FILE_NAME);
//
//        // Delete any previous recording.
//        if (file.exists())
//            file.delete();
//
//
//        // Create the new file.
//        try {
//            file.createNewFile();
//            Log.e(TAG, "startRecord: audioFile is " + file.getAbsolutePath());
//        } catch (IOException e) {
//            Log.e(TAG, "startRecord: createNeFile error");
//            return false;
//        }
//
//        try {
//            // Create a DataOuputStream to write the audio data into the saved file.
//            OutputStream os = new FileOutputStream(file);
//            BufferedOutputStream bos = new BufferedOutputStream(os);
//            DataOutputStream dos = new DataOutputStream(bos);
//
//            // Create a new AudioRecord object to record the audio.
//            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
//
//            short[] buffer = new short[bufferSize];
//            mAudioRecord.startRecording();
//
//            boolean isRecording = true;
//            while (isRecording) {
//                int bufferReadResult = mAudioRecord.read(buffer, 0, bufferSize);
//                for (int i = 0; i < bufferReadResult; i++)
//                    dos.writeShort(buffer[i]);
//            }
//            dos.close();
//        } catch (Throwable t) {
//            Log.e("AudioRecord", "Recording Failed");
//            return false;
//        }
//        return isOk;
//    }


    public static void recordByMediaRecorder() {
        /* 创建录音文件 */
        File file = new File(FileUtils.getLocalAudioDir() + AUDIO_TEMP_FILE_NAME);

        // Delete any previous recording.
        if (file.exists())
            file.delete();


        // Create the new file.
        try {
            file.createNewFile();
            Log.e(TAG, "startRecord: audioFile is " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "startRecord: createNeFile error");
        }

        iMediaRecorder = new MediaRecorder();
            /* 设置录音来源为MIC */
        iMediaRecorder
                .setAudioSource(MediaRecorder.AudioSource.MIC);
        iMediaRecorder
                .setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        iMediaRecorder
                .setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        iMediaRecorder.setOutputFile(file
                .getAbsolutePath());
        try {
            iMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        iMediaRecorder.start();
    }

//    public void stopRecord() {
//        if (mAudioRecord != null
//                && mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
//            mAudioRecord.stop();
//            mAudioRecord.release();
//        }
//    }

    public static void stopMediaRecorder() {
         /* 停止录音 */
        if (iMediaRecorder != null) {
            iMediaRecorder.stop();
            iMediaRecorder.release();
            iMediaRecorder = null;
        }
    }
}
