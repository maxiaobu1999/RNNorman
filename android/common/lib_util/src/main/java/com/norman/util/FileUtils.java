package com.norman.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;


import com.norman.runtime.AppRuntime;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 文件处理辅助类
 * <br>
 * 这个类新加了得到缓存文件的路径的方法
 *
 */
public final class FileUtils {

    /** DEBUG */
    private static final boolean DEBUG = true & AppRuntime.isDebug();
    /** DEBUG */
    private static final String TAG = "FileUtils";
    /** 缓存路径，形式是：sdcard/Android/data/package-name */
    private static String sCacheDir = null;
    /** 缓存大小设置 */
    private static final int BUFFER_SIZE = 1024;
    /** File buffer stream size*/
    public static final int FILE_STREAM_BUFFER_SIZE = 8192;
    /** unzip buffer size. */
    private static final int UNZIP_BUFFER = 2048;
    /** file的schema */
    public static final String FILE_SCHEMA = "file://";
    /** invalid index */
    public static int INVALID_INDEX = -1;
    /** increament one step */
    public static int ONE_INCREAMENT = 1;
    /** 1KB. */
    public static final int KB = 1024;
    /** 1MB. */
    public static final int MB = 1024 * 1024;
    /** 1GB. */
    public static final int GB = 1024 * 1024 * 1024;
    /** 针对拿到的size为<=0的情况 ，展示未知 */
    public static final String UNKNOW = "未知";

    /**
     * private
     */
    private FileUtils() {

    }

    /**
     * 得到应用程序的缓存根目录
     * <p>
     * <li>API Level >= 8时，路径是/mnt/sdcard/Android/data/package-name/cache，这个目录在应用卸载后会自动删除
     * <li>API Level < 8时，路径是/sdcard/cache，当应用删除时，这个目录不会被删除
     *
     * @return 缓存目录
     */
    public static String getCacheDir() {
        if (TextUtils.isEmpty(sCacheDir)) {
            sCacheDir = getCacheDir(AppRuntime.getAppContext());
        }

        if (DEBUG) {
            if (TextUtils.isEmpty(sCacheDir)) {
                Log.e(TAG, "FileUtils#getCacheDir  cache dir = null");
            } else {
                Log.d(TAG, "FileUtils#getCacheDir  cache dir = " + sCacheDir);
            }
        }

        return sCacheDir;
    }
    
    /**
     * 判断文件是否存在
     *
     * @param fileName
     *
     * @return
     */
    public static boolean isExistFile(String fileName) {
        return !TextUtils.isEmpty(fileName) && new File(fileName).exists();
    }
    
    /**
     * 删除指定文件
     *
     * @param path 文件路径
     * @return 是否成功删除
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (file.exists()) {
            return deleteFile(file);
        }
        return false;
    }

    /**
     * 删除指定文件、文件夹内容
     *
     * @param file 文件或文件夹
     * @return 是否成功删除
     */
    public static boolean deleteFile(File file) {
        if (DEBUG) {
            Log.d(TAG, "delete file:" + file);
        }

        if (file == null) {
            return false;
        }

        boolean isDeletedAll = true;

        if (file.exists()) {
            // 判断是否是文件,直接删除文件
            if (file.isFile()) {
                isDeletedAll &= file.delete();

                // 遍历删除一个文件目录
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        isDeletedAll &= deleteFile(files[i]); // 迭代删除文件夹内容
                    }
                }

                isDeletedAll &= file.delete();

            } else {
                if (DEBUG) {
                    Log.d(TAG, "a special file:" + file);
                }
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "not found the file to delete:" + file);
            }
        }

        return isDeletedAll;
    }

    /**
     * 保存文件
     *
     * @param data     data
     * @param saveFile saveFile
     *
     * @return true 成功
     */
    public static boolean saveFile(String data, File saveFile) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }

        if (saveFile.exists()) {
            return false;
        }

        saveFileCommon(data.getBytes(), saveFile);

        return true;
    }

    /**
     * 保存文件
     *
     * @param context  Context
     * @param data     要保存的数据
     * @param dirName  存储目录
     * @param fileName 存储文件名
     * @return 保存的文件
     */
    public static File saveFile(Context context, byte[] data, String dirName, String fileName) {
        if (context == null || data == null || TextUtils.isEmpty(dirName) || TextUtils.isEmpty(fileName)) {
            if (DEBUG) {
                Log.e(TAG, "saveFile: invalid parameter!");
            }
            return null;
        }

        File dirFile;
        if (PathUtils.isExternalStorageWritable()) {
            // 如果SD卡可以使用
            dirFile = new File(android.os.Environment.getExternalStorageDirectory(), dirName);
        } else {
            // SD卡不能使用的时候，选择使用应用的缓存文件夹，这里可能会有问题
            dirFile = new File(context.getCacheDir(), dirName);
        }
        if (!dirFile.exists()) {
            // 如果不存在文件夹，创建文件夹
            dirFile.mkdirs();
        }

        File fileToSave = new File(dirFile, fileName);

        saveFileCommon(data, fileToSave);

        return fileToSave;
    }

    /**
     * 保存文件common
     *
     * @param data data
     * @param saveTo 保存文件
     */
    public static void saveFileCommon(byte[] data, File saveTo) {
        InputStream is = new ByteArrayInputStream(data);
        saveToFile(is, saveTo);
        Closeables.closeSafely(is);
    }

    /**
     * 存文件到应用Cache目录
     *
     * @param context  应用上下文
     * @param data     数据
     * @param fileName 文件名
     * @return 存储的文件
     */
    public static File saveCacheFile(Context context, byte[] data, String fileName) {
        if (context == null || data == null || TextUtils.isEmpty(fileName)) {
            if (DEBUG) {
                Log.e(TAG, "saveFile: invalid parameter!");
            }
            return null;
        }

        File fileToSave = new File(context.getCacheDir(), fileName);

        InputStream is = new ByteArrayInputStream(data);
        saveToFile(is, fileToSave);
        Closeables.closeSafely(is);

        return fileToSave;
    }

    /**
     * 将输入流存储到指定文件
     *
     * @param inputStream 输入流
     * @param file        存储的文件
     */
    public static void saveToFile(InputStream inputStream, File file) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            copyStream(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                android.util.Log.d(TAG, "catch FileNotFoundException");
            }
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    if (DEBUG) {
                        android.util.Log.d(TAG, "catch IOException");
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将文本内容存储到指定文件
     * @param content 内容
     * @param file 目标文件
     * @param append 追加还是覆盖
     */
    public static void saveToFile(String content, File file, boolean append){

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        saveToFile(inputStream,file,append);
    }

    /**
     * 将输入流存储到指定文件
     *
     * @param inputStream 输入流
     * @param file        存储的文件
     * @param append      是否追加
     */
    public static void saveToFile(InputStream inputStream, File file, boolean append) {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file, append);
            copyStream(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            if (DEBUG) {
                android.util.Log.d(TAG, "catch FileNotFoundException");
            }
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    if (DEBUG) {
                        android.util.Log.d(TAG, "catch IOException");
                    }
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 拷贝文件
     *
     * @param src 源文件
     * @param dst 目标文件
     * @return 拷贝的字节数
     */
    public static long copyFile(File src, File dst) {
        if (null == src || null == dst) {
            return 0;
        }

        if (!src.exists()) {
            return 0;
        }

        long size = 0;

        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(src);
            os = new FileOutputStream(dst);
            size = copyStream(is, os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Closeables.closeSafely(is);
            Closeables.closeSafely(os);
        }

        return size;
    }

    /**
     * 从输入流中读取字节写入输出流
     *
     * @param is 输入流
     * @param os 输出流
     * @return 复制大字节数
     */
    public static long copyStream(InputStream is, OutputStream os) {
        if (null == is || null == os) {
            return 0;
        }

        try {
            final int defaultBufferSize = 1024 * 3;
            byte[] buf = new byte[defaultBufferSize];
            long size = 0;
            int len = 0;
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
                size += len;
            }
            os.flush();
            return size;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到应用程序的缓存目录
     * <p>
     * <li>API Level >= 8时，路径是/mnt/sdcard/Android/data/package-name/cache，这个目录会自动删除
     * <li>API Level < 8时，路径是/sdcard/，当应用删除时，这个目录不会被删除
     *
     * @param context context
     * @return 缓存目录
     */
    @SuppressLint("NewApi")
    private static String getCacheDir(Context context) {
        File cacheDir = null;
        // 如果SDK版本大于等于8
        if (APIUtils.hasFroyo()) {
            // getExternalCacheDir是在API Level 8引入的，所有必须加以判断
            cacheDir = context.getExternalCacheDir();
        }

        if (null == cacheDir) {
            // 如果SDCard可写，得到SDCard路径
            boolean sdcardWritable = PathUtils.isExternalStorageWritable();
            if (sdcardWritable) {
                cacheDir = android.os.Environment.getExternalStorageDirectory();
            }
        }

        if (null == cacheDir) {
            cacheDir = context.getCacheDir();
        }

        if (null == cacheDir) {
            cacheDir = context.getFilesDir();
        }

        return (null != cacheDir) ? cacheDir.getAbsolutePath() : null;
    }

    /**
     * 从网络下载数据流
     *
     * @param dir  存储路径
     * @param name 存储文件名
     * @param url  URL
     * @return 返回字节大小
     */
    public static long downloadStream(String dir, String name, String url) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(url)) {
            return 0;
        }

        return downloadStream(new File(dir, name), url);
    }

    /**
     * 从网络下载数据流
     *
     * @param file 文件名
     * @param url  URL
     * @return 返回字节大小
     */
    public static long downloadStream(File file, String url) {
        if (TextUtils.isEmpty(url) || (null == file)) {
            return 0;
        }

        long size = 0;
        HttpURLConnection conn = null;
        InputStream is = null;
        FileOutputStream os = null;

        try {
            URL imageUrl = new URL(url);
            conn = (HttpURLConnection) (imageUrl.openConnection());
            conn.setConnectTimeout(10000); // SUPPRESS CHECKSTYLE
            conn.setReadTimeout(10000); // SUPPRESS CHECKSTYLE
            // conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() == 200/*HttpStatus.SC_OK*/) {
                is = conn.getInputStream();
                if (null != is) {
                    File imageFile = file;
                    os = new FileOutputStream(imageFile);
                    size = FileUtils.copyStream(is, os);
                }
            }
        } catch (InterruptedIOException | OutOfMemoryError | MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Closeables.closeSafely(is);
            if (null != conn) {
                conn.disconnect();
            }
            Closeables.closeSafely(os);
        }

        return size;
    }

    /**
     * 将字节压缩成gzip文件
     *
     * @param bytes
     * @param outputFile
     */
    public static void saveToGzip(byte[] bytes, File outputFile) {
        if (bytes == null || bytes.length <= 0 || outputFile == null) {
            return;
        }
        InputStream in = null;
        GZIPOutputStream gzipOutputStream = null;
        int len = 0;
        try {
            gzipOutputStream = new GZIPOutputStream(new FileOutputStream(outputFile, false));
            byte[] buffer = new byte[BUFFER_SIZE];
            in = new ByteArrayInputStream(bytes);
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) > 0) {
                gzipOutputStream.write(buffer, 0, len);
            }
            gzipOutputStream.finish();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closeables.closeSafely(gzipOutputStream);
            Closeables.closeSafely(in);
        }
    }

    /**
     * 读取文件内容
     *
     * @param file file
     *
     * @return 文件内容String
     */
    public static String readFileData(File file) {
        try {
            return readInputStream(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
        }

        return "";
    }

    /**
     * 缓存文件
     *
     * @param context Context Object
     * @param file    本地文件名
     * @param data    要保存的数据
     * @param mode    打开文件的方式
     *
     * @return 是否保存成功
     */
    public static boolean cache(Context context, String file, String data, int mode) {
        return cache(context, file, data.getBytes(), mode);
    }

    /**
     * 缓存文件
     *
     * @param context Context Object
     * @param file    本地文件名
     * @param data    要保存的数据
     * @param mode    打开文件的方式
     *
     * @return 是否保存成功
     */
    public static boolean cache(Context context, String file, byte[] data, int mode) {
        boolean bResult = false;
        if (null == data) {
            data = new byte[0];
        }

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(file, mode);
            fos.write(data);
            fos.flush();
            bResult = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bResult;
    }

    /**
     * 删除缓存文件，通常这个文件是存在应用程序的系统数据目录里面，典型的目录是data/data/package-name/files
     *
     * @param context context
     * @param name    本地文件名，不要包含路径分隔符
     *
     * @return true：成功，false：失败
     */
    public static boolean deleteCache(Context context, String name) {
        boolean succeed = false;

        try {
            succeed = context.deleteFile(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return succeed;
    }

    /**
     * 读取缓存文件中的数据
     *
     * @param context 上下文
     * @param file    文件名
     *
     * @return 文件中的字符串数据
     */
    public static String readCacheData(Context context, String file) {
        try {
            return readInputStream(context.openFileInput(file));
        } catch (FileNotFoundException ex) {
            if (DEBUG) {
                ex.printStackTrace();
            }
        }

        return "";
    }

    /**
     * 读取文件数据
     *
     * @param inputStream inputStream
     *
     * @return string
     */
    private static String readInputStream(FileInputStream inputStream) {
        if (inputStream == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除指定的文件
     *
     * @param file 需删除的文件
     * @return true/false
     */
    public static boolean safeDeleteFile(File file) {
        // 由于在某些手机上面File#delete()方法是异步的，会导致后续的创建文件夹失败，
        // 推荐的做法是对要删除的文件重命名，然后再删除，这样就不会影响后续创建文件夹。
        try {
            if (file == null || !file.exists()) {
                return true;
            }
            String filePath = file.getAbsolutePath();
            File oldFile = new File(filePath);
            // 构造一个不存在的文件名
            long time = System.currentTimeMillis();
            File tempFile = new File(filePath + time + ".tmp");
            oldFile.renameTo(tempFile);
            return tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 安全的创建一个新文件.如果其上层文件夹不存在，则会先创建上层文件夹，避免No such file exception
     * @param saveFile file
     * @return result
     */
    public static boolean createNewFileSafely(File saveFile) {
        if (saveFile == null || saveFile.exists()) {
            return false;
        }
        File saveFileParent = saveFile.getParentFile();
        if (saveFileParent != null && !saveFileParent.exists()) {
            saveFileParent.mkdirs();
        }
        try {
            return saveFile.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断是否是zip文件，zip文件前4个字节是：504B0304
     *
     * @param srcfile 指定文件file，不能为空
     * @return true 是zip文件，false不是zip文件
     */
    public static boolean isZipFile(File srcfile) {
        if (!srcfile.exists()) {
            return false;
        }
        // 取出前4个字节进行判断
        byte[] filetype = new byte[4]; // SUPPRESS CHECKSTYLE
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(srcfile);
            fis.read(filetype);
            if ("504B0304".equalsIgnoreCase(toHexString(filetype, "", true))) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
            return false;
        } finally {
            Closeables.closeSafely(fis);
        }
    }

    /**
     * 判断是否是Gzip文件，gzip文件前4个字节是：1F8B0800
     *
     * @param srcfile 指定文件file，不能为空
     * @return true 是Gzip文件，false不是Gzip文件
     */
    public static boolean isGzipFile(String srcfile) {
        File file = new File(srcfile);
        if (!file.exists()) {
            return false;
        }
        // 取出前4个字节进行判断
        byte[] filetype = new byte[4]; // SUPPRESS CHECKSTYLE
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(filetype);
            if ("1F8B0800".equalsIgnoreCase(toHexString(filetype, "", true))) {
                return true;
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            Closeables.closeSafely(fis);
        }
        return false;
    }

    /**
     * 解压gzip文件
     *
     * @param srcFileName 源file
     * @param outFileName 解压后的file
     * @return 解压是否成功
     */
    public static boolean unGzipFile(File srcFileName, File outFileName) {
        if (srcFileName == null) {
            return false;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPInputStream gzip = null;
        try {
            fis = new FileInputStream(srcFileName);
            gzip = new GZIPInputStream(fis);
            fos = new FileOutputStream(outFileName);
            byte[] buf = new byte[FILE_STREAM_BUFFER_SIZE];
            int num = -1;
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, num);
            }
            fos.flush();
            return true;
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            Closeables.closeSafely(fis);
            Closeables.closeSafely(fos);
            Closeables.closeSafely(gzip);
        }
        return false;
    }

    /**
     * unzip file.
     *
     * @param srcFileName 源file绝对路径.
     * @param savePath 目标file父目录路径
     * @return boolean 是否解压成功
     */
    public static boolean unzipFile(String srcFileName, String savePath) {
        long startTime = System.currentTimeMillis();
        if (srcFileName == null) {
            return false;
        }
        if (savePath == null) {
            savePath = new File(srcFileName).getParent();
        }

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            ZipFile zipFile = new ZipFile(srcFileName);
            Enumeration<? extends ZipEntry> enu = zipFile.entries();

            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = enu.nextElement();
                if (zipEntry.getName().contains("../")) {
                    continue;
                }
                File saveFile = new File(savePath + "/" + zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!saveFile.exists()) {
                        saveFile.mkdirs();
                    }
                    continue;
                }
                if (!saveFile.exists()) {
                    createNewFileSafely(saveFile);
                }
                FileOutputStream fos = null;
                try {
                    bis = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                    fos = new FileOutputStream(saveFile);
                    bos = new BufferedOutputStream(fos, UNZIP_BUFFER);

                    int count = -1;
                    byte[] buf = new byte[UNZIP_BUFFER];
                    while ((count = bis.read(buf, 0, UNZIP_BUFFER)) != -1) {
                        bos.write(buf, 0, count);
                    }

                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                } finally {
                    Closeables.closeSafely(bos);
                    Closeables.closeSafely(bis);
                    Closeables.closeSafely(fos);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            long endTime = System.currentTimeMillis();
            if (DEBUG) {
                Log.i(TAG, "unZip:" + srcFileName + "cost:" + (endTime - startTime) + "ms");
            }
        }
        return true;
    }

    /**
     * 从asset目录释放并解压zip文件
     *
     * @param assetPath assetPath
     * @param savePath  savePath
     *
     * @return true 解压成功
     */
    public static boolean unzipFileFromAsset(String assetPath, String savePath) {
        if (TextUtils.isEmpty(assetPath) || TextUtils.isEmpty(savePath)) {
            return false;
        }

        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }

        InputStream inputStream = null;
        ZipInputStream zipInputStream = null;
        BufferedOutputStream bos = null;
        try {
            inputStream = AppRuntime.getAppContext().getAssets().open(assetPath);
            zipInputStream = new ZipInputStream(inputStream);
            ZipEntry nextEntry;
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;

            while ((nextEntry = zipInputStream.getNextEntry()) != null) {
                if (nextEntry.getName().contains("../")) {
                    continue;
                }
                saveFile = new File(savePath + File.separator + nextEntry.getName());
                if (nextEntry.isDirectory()) {
                    if (!saveFile.exists()) {
                        saveFile.mkdir();
                    }
                    continue;
                }

                if (!saveFile.exists()) {
                    createNewFileSafely(saveFile);
                    try {
                        bos = new BufferedOutputStream(new FileOutputStream(saveFile), UNZIP_BUFFER);
                        while ((count = zipInputStream.read(buffer)) != -1) {
                            bos.write(buffer, 0, count);
                        }
                    } finally {
                        Closeables.closeSafely(bos);
                    }
                }
            }
        } catch (IOException ioe) {
            if (DEBUG) {
                ioe.printStackTrace();
            }

            return false;
        } finally {
            Closeables.closeSafely(inputStream);
            Closeables.closeSafely(zipInputStream);
        }

        return true;
    }

    /**
     * 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     *
     * @param bytes 输入
     * @param separator 分割线
     * @param upperCase true：大写， false 小写字符串
     * @return 把二进制byte数组生成十六进制字符串，单个字节小于0xf，高位补0。
     */
    public static String toHexString(byte[] bytes, String separator, boolean upperCase) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(0xFF & b); // SUPPRESS CHECKSTYLE
            if (upperCase) {
                str = str.toUpperCase();
            }
            if (str.length() == 1) {
                hexString.append("0");
            }
            hexString.append(str).append(separator);
        }
        return hexString.toString();
    }

    /**
     * 获取不带文件扩展名的文件名称
     *
     * @param filename 文件全名称
     * @return 不带文件扩展名的文件名称
     */
    public static String getFileNameNoExt(String filename) {
        if (!TextUtils.isEmpty(filename)) {
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex > -1 && dotIndex < filename.length()) {
                return filename.substring(0, dotIndex);
            }
        }
        return filename;
    }

    /**
     * 获取不带文件扩展名的文件名称
     *
     * @param filename 文件全名称
     * @param tag      要插入的tag
     * @return 不带文件扩展名的文件名称
     */
    public static String insertTagInFileName(String filename, String tag) {
        if (!TextUtils.isEmpty(filename)) {
            int dotIndex = filename.lastIndexOf('.');
            StringBuilder sb = new StringBuilder();
            if (dotIndex > -1 && dotIndex < filename.length()) {
                sb.append(filename.substring(0, dotIndex));
                if (!TextUtils.isEmpty(tag)) {
                    sb.append(tag);
                }
                sb.append(filename.substring(dotIndex));
                return sb.toString();
            }
        }
        return filename;
    }

    /**
     * 从文件路径中获取文件名(包括文件后缀)
     * @param path 文件路径
     * @return
     */
    public static String getFileNameFromPath(String path) {
        if (TextUtils.isEmpty(path) || path.endsWith(File.separator)) {
            return "";
        }
        int start = path.lastIndexOf(File.separator);
        int end = path.length();
        if (start != INVALID_INDEX && end > start) {
            return path.substring(start + ONE_INCREAMENT, end);
        } else {
            return path;
        }
    }

    /**
     * 读取Asset文件数据
     *
     * @param context   context
     * @param assetPath assetPath
     *
     * @return string data
     */
    public static @Nullable
    String readAssetData(Context context, String assetPath) {
        if (context == null || TextUtils.isEmpty(assetPath)) {
            return null;
        }

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = context.getAssets().open(assetPath);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        } catch (IOException e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /** SD卡存储cache路径 */
    private static final String EXTERNAL_STORAGE_DIRECTORY = "/norman/malong";

    public static File getPublicExternalDiretory(String fileName) {
        File dir = new File(Environment.getExternalStorageDirectory(), EXTERNAL_STORAGE_DIRECTORY);
        File file = null;
        if (ensureDirectoryExist(dir)) {
            file = new File(dir, fileName);
        }

        return file;
    }

    public static boolean ensureDirectoryExist(final File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成文件大小的字符串.
     *
     * @param size 文件大小
     * @return 表示经过格式的字符串
     */
    public static String generateFileSizeText(long size) {
        String unit;
        Float outNumber;
        if (size <= 0) {
            return UNKNOW;
        } else if (size < KB) {
            return size + "B";
        } else if (size < MB) {
            unit = "KB";
            outNumber = (float) size / KB;
        } else if (size < GB) {
            unit = "MB";
            outNumber = (float) size / MB;
        } else {
            unit = "GB";
            outNumber = (float) size / GB;
        }
        /*
         * 文件大小显示格式化. 大于1KB的文件大小数字显示形如1011.11,小于1KB的文件显示具体大小
         */
        DecimalFormat formatter = new DecimalFormat("####.##");
        return formatter.format(outNumber) + unit;
    }
    
    /**
     * 获取文件夹里所有文件的总大小
     *
     * @param f
     */
    public static long getDirectorySize(File f) throws IOException {
        long size = 0;
        File[] flist = f.listFiles();
        if (flist == null) {
            return f.length();
        }
        int length = flist.length;
        for (int i = 0; i < length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getDirectorySize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
    
    /**
     * 通过文件夹路径获取文件夹大小
     *
     * @param fp 文件夹路径
     */
    public static long getDirectorySize(String fp) throws IOException {
        long size = 0;
        File f = new File(fp);
        File[] flist = f.listFiles();
        if (flist == null) {
            return f.length();
        }
        int length = flist.length;
        for (int i = 0; i < length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getDirectorySize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 从url从抽取文件名
     *
     * @param url String
     * @return /xxxx.mp4?yyy, 返回xxxx.mp4
     */
    public static String getFileNameFromUrl(String url) {
        String filename = null;
        String decodedUrl = Uri.decode(url);
        if (decodedUrl != null) {
            int queryIndex = decodedUrl.indexOf('?');
            // If there is a query string strip it, same as desktop browsers
            if (queryIndex > 0) {
                decodedUrl = decodedUrl.substring(0, queryIndex);
            }
            if (!decodedUrl.endsWith("/")) {
                int index = decodedUrl.lastIndexOf('/') + 1;
                if (index > 0) {
                    filename = decodedUrl.substring(index);
                }
            }
        }

        return filename;
    }
}
