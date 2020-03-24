package com.example.textfunction.videoBitmap;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;


import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UsualUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        } else {
            if (str.length() == 0)
                return true;
            return false;
        }
    }

    /**
     * 获取当前时间戳
     */
    public static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 将时间转换为时间戳（年月日，时分秒）
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    /**
     * 将字符串转换为日期格式（年月日）
     *
     * @param s
     * @return
     */
    public static String strToDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(s);
        return date;
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取文件大小
     *
     * @param byteSize
     * @return
     */
    public static String getSize(long byteSize) {
        double dSize = byteSize;
        DecimalFormat df = new DecimalFormat("#.00");
//        df.format()
        // 如果字节数(byte)少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (dSize < 1024) {
            return byteSize + "B";
        } else {

            dSize = dSize / 1024;
            if (dSize < 1024) {
                return df.format(dSize) + "KB";
            } else {

                dSize = dSize / 1024;
                if (dSize < 1024) {
                    return df.format(dSize) + "MB";
                } else {

                    dSize = dSize / 1024;
                    return df.format(dSize) + "GB";
                }
            }
        }
    }

    public static String getSpeed(long byteSize, long startLong, long endLong) {
        long time = (endLong - startLong) / 1000;
        DecimalFormat df = new DecimalFormat("#.00");
        if (byteSize < 1024) {
            return byteSize / time + "B/s";
        } else {

            byteSize = byteSize / 1024;
            if (byteSize < 1024) {
                return df.format(byteSize / time) + "K/s";
            } else {

                byteSize = byteSize / 1024;
                return df.format(byteSize / time) + "M/s";
            }
        }
    }


    /**
     * 获取本地文件路径
     *
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[]
            selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getDate(String dateStr) {
        String dateString = dateStr.substring(0, 10);
        return dateString;
    }


    /**
     * 图片压缩 并上传
     * @param pic
     * @param context
     */
    public static void compressToUpload(String pic, Context context){
        File imageFile=new File(pic);
        String fileName=imageFile.getName();
        String fileType=fileName.substring(fileName.lastIndexOf(".")+1);
        String tagFile=UsualUtil.compress(50,context,fileType,pic);
        if(!UsualUtil.isEmpty(tagFile))
            Log.e("TAG","图片压缩成功："+tagFile);
//        UploadPic.uploadPicture(tagFile);
    }
    /**
     * 质量压缩
     *
     * @param fileType 图片格式 jpeg,png,webp
     * @param quality  图片的质量,0-100,数值越小质量越差
     */
    public static String compress(int quality, Context context, String fileType, String filePath) {
        try {
            String directory = context.getExternalCacheDir().toString() + "/img";
            Bitmap.CompressFormat format = getFormat(fileType);
            File originFile = new File(filePath);
//        Bitmap originBitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());
//            Bitmap originBitmap = BitmapFactory.decodeFile(filePath);
            Bitmap originBitmap = getSmallBitmap(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            originBitmap.compress(format, quality, bos);

            File tagFile = new File(directory, originFile.getName());
            FileOutputStream fos = new FileOutputStream(tagFile);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return tagFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filePath, options);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        //避免出现内存溢出的情况，进行相应的属性设置。
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        return BitmapFactory.decodeFile(filePath, options);
    }
    public static Bitmap.CompressFormat getFormat(String type) {
        switch (type) {
            case "jpg":
            case "JPG":
            case "jpeg":
            case "JPEG":
                return Bitmap.CompressFormat.JPEG;
            case "png":
            case "PNG":
                return Bitmap.CompressFormat.PNG;
            case "webp":
            case "WEBP":
                return Bitmap.CompressFormat.WEBP;
            default:
                return null;
        }
    }



}


//                Glide.get(getApplicationContext()).clearDiskCache();    //清除图片磁盘缓存
//                Glide.get(getApplicationContext()).clearMemory();    //清除图片磁盘缓存