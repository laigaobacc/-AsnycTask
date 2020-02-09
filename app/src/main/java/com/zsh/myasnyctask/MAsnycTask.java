package com.zsh.myasnyctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public abstract class MAsnycTask<Params, Progress, Result> {
    private final static int CODE = 100;
    private Handler mHandler;
    Result result = null;
    Params params[] = null;

    public MAsnycTask() {
        mHandler = new android.os.Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what) {
                    case 100:
                        onPostExecute(result);   //运行完以后把结果返回给onPostExecute
                        break;
                }
            }
        };
    }

    public void execute(final Params... params) {
        onPreExecute();//后台任务开始前调用
        new Thread(() -> {
            this.params = params;
            result = doBackGround(params);
            Message msg = new Message();
            msg.what = CODE;
            mHandler.sendMessage(msg);
        }).start();
    }


    public abstract void onPreExecute();//任务开始前调用

    public abstract Result doBackGround(Params... params);//后台运行

    public abstract void onProgressUpdate(Progress... progress);//后台任务执行时传递进度

    public abstract void onPostExecute(Result result);//后台任务执行结束返回结果

    public void publishProgress(Progress... progress) {
        onProgressUpdate(progress);
    }
}
