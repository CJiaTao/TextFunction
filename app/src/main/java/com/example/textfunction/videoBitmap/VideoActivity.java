package com.example.textfunction.videoBitmap;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.textfunction.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class VideoActivity extends AppCompatActivity {
    @BindView(R.id.btnVideo)
    Button btnVideo;
    @BindView(R.id.tvVideo)
    TextView tvVideo;
    @BindView(R.id.ivVideo)
    ImageView ivVideo;

    private static final int REQUEST_PERMISSION = 1;
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    private static final int GET_FILE = 5;
    private AlertDialog alertDialog;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);

        myRequetPermission();
    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {//用户选择了禁止不再询问

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();


                    } else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(VideoActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOT_NOTICE) {
            myRequetPermission();//由于不知道是否选择了允许所以需要再次判断
        } else if (requestCode == GET_FILE) {
            if (resultCode != 0) {
                String filePath = UsualUtil.getPath(this, data.getData());
                File uploadFile = new File(filePath);
                Long size = uploadFile.length();
                String name = uploadFile.getName();

                tvVideo.setText(uploadFile.getAbsolutePath());
                getVideoBitmap(uploadFile);
            }
        }
    }

    @OnClick({R.id.btnVideo})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btnVideo:
                getVideo();
                break;
        }
    }

    private void getVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, GET_FILE);
    }

    public void getVideoBitmap(File file) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(file.getAbsolutePath());
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.e("该视频的长度", time);
        // 取得视频的长度(单位为秒)
        int seconds = Integer.valueOf(time) / 1000;
        int current = seconds / 2;
        Log.e("视频长度", seconds + "");
        Bitmap bitmap = retriever.getFrameAtTime(current * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        ivVideo.setImageBitmap(bitmap);

        String name=file.getName();
        saveImage(this,bitmap,name);
    }

    private void saveImage(Context context, Bitmap bitmap, String fileName) {
        //此处范围的所谓外部存储是手机的自带内存32G,64G，并不是SD卡，是否有访问权限

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File sdFile = this.getExternalCacheDir();
            File newFileDir = new File(sdFile, "/img");
            try {
                if (!newFileDir.exists())
                    newFileDir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }

//            File file = new File(newFileDir, System.currentTimeMillis() + ".jpg");
            File file = new File(newFileDir, fileName + ".jpg");

            Log.e("TAG", "根目录里面的所有目录：" + newFileDir.exists());

            //打开文件输出流
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
