package com.cjx.zhiai.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.ProgressResponseListener;
import com.cjx.zhiai.util.Tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by cjx on 2016/9/2.
 * 更新下载包的对话框
 */
public class DownloadDialog extends BaseDialog implements View.OnClickListener {
    private final int DOWNLOAD_FAIL = 201, DOWNLOAD_SUCCESS = 200, DOWNLOAD_FAIL_FILE = 202;

    private String url;
    private TextView titleView, describeView, comfirmView;
    private ProgressBar progressBar;

    public DownloadDialog(Context context) {
        super(context);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(false);
        View v = View.inflate(getContext(), R.layout.dialog_download, null);
        titleView = (TextView) v.findViewById(R.id.tip_title);
        describeView = (TextView) v.findViewById(R.id.tip_describe);
        comfirmView = (TextView) v.findViewById(R.id.tip_comfirm);
        comfirmView.setOnClickListener(this);

        comfirmView.setBackgroundResource(R.drawable.dialog_right_button);
        progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        setContentView(v);
    }

    @Override
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    public DownloadDialog setText(String title, String describe, String comfirm, String url) {
        titleView.setText(title);
        describeView.setText(describe);
        comfirmView.setText(comfirm);
        this.url = url;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tip_comfirm:
                v.setClickable(false);
                ((TextView) v).setText("正在下载...");
                startDownload();
                break;
        }
    }

    public void onCancel() {
        if (call != null) {
            call.cancel();
            Toast.makeText(getContext(), "下载被取消", Toast.LENGTH_SHORT).show();
        }
    }

    Handler downloadHandler;
    Call call;
    File tempFile;

    private void startDownload() {
        downloadHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWNLOAD_FAIL_FILE:
                        closeDialog();
                        Toast.makeText(getContext(), "创建下载文件失败,请检查sdcard", Toast.LENGTH_SHORT).show();
                        break;
                    case DOWNLOAD_FAIL:
                        closeDialog();
                        Toast.makeText(getContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        break;
                    case DOWNLOAD_SUCCESS:
                        closeDialog();
                        File saveFile = new File(Tools.getTempPath(getContext()), "kamfat.apk");
                        if (saveFile.exists()) {
                            saveFile.delete();
                        }
                        tempFile.renameTo(saveFile);
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        Uri uri;
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            Context context = getContext();
                            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",
                                    saveFile);
                        }else{
                            uri = Uri.fromFile(saveFile);
                        }
                        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                        getContext().startActivity(installIntent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        break;
                    default:
                        progressBar.setProgress(msg.what);
                        break;
                }
                return false;
            }
        });
        tempFile = new File(Tools.getTempPath(getContext()), "kamfat.temp");
        if (tempFile.exists()) {
            tempFile.delete();
        }
        call = HttpUtils.getInstance().download(url, getProgressResponseListener(), getCallback());
    }

    private void closeDialog() {
        call = null;
        dismiss();
    }

    // 创建一个下载进度监听回调
    private ProgressResponseListener getProgressResponseListener() {
        return new ProgressResponseListener() {
            int progress;

            @Override
            public void onResponseProgress(long bytesRead, long contentLength, boolean done) {
                if (contentLength != -1) {
                    //长度未知的情况下回返回-1
                    int p = (int) ((100 * bytesRead) / contentLength);
                    if (progress != p) {
                        progress = p;
                        downloadHandler.sendEmptyMessage(progress);
                    }
                    if (progress == 100 && done) {
                        downloadHandler.sendEmptyMessage(DOWNLOAD_SUCCESS);
                    }

                }
            }
        };
    }

    // 获取一个请求回调
    private Callback getCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                downloadHandler.sendEmptyMessage(DOWNLOAD_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    Log.e("TAG", "file create ");
                    if (tempFile.createNewFile()) {
                        InputStream is = response.body().byteStream();
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        byte[] buf = new byte[1024];
                        int hasRead;
                        while ((hasRead = is.read(buf)) > 0) {
                            fos.write(buf, 0, hasRead);
                        }
                        fos.close();
                        is.close();
                    } else {
                        downloadHandler.sendEmptyMessage(DOWNLOAD_FAIL_FILE);
                    }
                } else {
                    downloadHandler.sendEmptyMessage(DOWNLOAD_FAIL);
                }
            }
        };
    }

}
