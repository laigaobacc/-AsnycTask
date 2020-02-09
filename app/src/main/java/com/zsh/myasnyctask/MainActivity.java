package com.zsh.myasnyctask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String URL_APK = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";
    public static final String FileNmae = "down.apk";
    ;
    private Button btn;
    private ProgressBar progressBar;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化视图和监听
        initView();
        //设置UI数据
        setData();
        Test my=new Test();
        my.execute("成功");
    }

    private void initView() {
        progressBar = findViewById(R.id.progress_Bar);
        btn = findViewById(R.id.down_button);
        textView = findViewById(R.id.text_View);

        btn.setOnClickListener(this);
    }
    private void setData() {
        progressBar.setProgress(0);
        btn.setText("点击下载");
        textView.setText("准备下载");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.down_button:
                // TODO: 2020/1/9 下载任务
                Test t = new Test();
                t.execute(URL_APK);
                break;
            default:
                break;
        }
    }

    class Test extends MAsnycTask<String,Integer,Boolean>{
        public final String M_FILE_PATH = Environment.getExternalStorageDirectory()
                + File.separator + FileNmae;

        @Override
        public void onPreExecute() {
            textView.setText("正在下载...");
        }

        @Override
        public Boolean doBackGround(String... strings) {
            //下载
            if (strings != null && strings.length > 0) {
                String apkurl = strings[0];
                try {
                    URL url = new URL(apkurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    //获取下载内容总长度
                    int contentlength = conn.getContentLength();

                    //下载地址准备
                    //对下载地址进行处理
                    File apkFile = new File(M_FILE_PATH);
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }

                    //已下载大小
                    int downSize = 0;

                    //读取字节流的缓存
                    byte[] bytes = new byte[1024];
                    int length;
                    //创建输出流
                    OutputStream outputStream = new FileOutputStream(M_FILE_PATH);
                    while ((length = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, length);
                        downSize += length;
                        //发送进度
                        publishProgress(downSize*100/contentlength);
                    }
                    inputStream.close();
                    outputStream.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
            return true;

        }

        @Override
        public void onProgressUpdate(Integer... progress) {
            //收到进度，然后处理：也是在UI线程中
            progressBar.setProgress(progress[0]);
        }

        @Override
        public void onPostExecute(Boolean aBoolean) {
            btn.setText("下载完成");
            textView.setText(aBoolean?"下载完成"+M_FILE_PATH:"下载失败");
        }
    }
}
