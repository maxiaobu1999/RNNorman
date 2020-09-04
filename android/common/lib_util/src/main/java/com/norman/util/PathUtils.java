package com.norman.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.norman.runtime.AppRuntime;

import java.io.File;

/** 这个类负责提供得到SDCard、缓存的目录的接口 */
public final class PathUtils {
    /** DEBUG */
    private static final boolean DEBUG = AppRuntime.isDebug() & true;
    /** TAG */
    private static final String TAG = "PathUtils";
    /** 用在 sdcard 根目录 */
    private static final String DIRCTORY_NORMAN = "norman";
    /** 下载目录 内置外置各一个 */
    private static final String DIRCTORY_DOWNLOAD = "downloads";
    /** 图片缓存目录（预留） */
    private static final String DIRCTORY_IMAGE_CACHE = "img_cache";
    /** 数据目录：norman */
    private static final String DIRECTORY_DATA_CACHE = DIRCTORY_NORMAN;

    /** 应用的缓存目录 */
    private static String sCacheDir = null;

    static {

    }

    /** 获取下载路径，外置或内置，应用私有目录 */
    public static String getDownloadPath(Context context) {
        boolean sdcardWriteable = isExternalStorageWritable();
        String parentDir=null;
        if (sdcardWriteable) {
            //外置sdcard可写
            File externalFile = context.getExternalFilesDir(null);
            if (externalFile!=null)
                parentDir = externalFile.getAbsolutePath();
        }
        if (TextUtils.isEmpty(parentDir)) {
            // 外置不可用，使用内置
            parentDir = context.getFilesDir().getAbsolutePath();
        }
        String downloadPath = parentDir + File.separator + DIRCTORY_DOWNLOAD;
        File file = new File(downloadPath);
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
        return downloadPath;
    }

    /**
     * 得到外部存储SDCard的路径
     *
     * @param context context
     * @return 路径
     */
    public static String getExternalStorageDir(Context context) {
        boolean sdcardWriteable = isExternalStorageWritable();
        File file;
        // 判断SDCard是否可写
        if (sdcardWriteable) {
            // 如果SD卡可以使用
            file = android.os.Environment.getExternalStorageDirectory();
        } else {
            // SD卡不能使用的时候，选择使用应用的缓存文件夹，这里可能会有问题
            file = context.getCacheDir();
        }

        if (null == file) {
            return "";
        }

        if (!file.exists()) {
            // 如果不存在文件夹，创建文件夹
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    /**
     * 得到当前应用的缓存目录，这个目录在应用删除后会自动被删除。
     *
     * <li>API Level >= 8时，路径是/mnt/sdcard/Android/data/[package-name]/cache/，这个目录会自动删除
     * <li>API Level < 8时，/sdcard/norman/searchbox/，当应用删除时，这个目录不会被删除
     * <li>如果这个目录不可用，存在/data/data/[package-name]/cache下面
     * <li>如果这个目录不可用，存在/data/data/[package-name]/files下面
     *
     * @param context context
     * @return 缓存目录，可能是空，在使用的时候需要判断合法性。
     */
    @SuppressLint("NewApi")
    public static String getCacheDirectory(Context context) {
        // 如果不为空，直接返回
        if (!TextUtils.isEmpty(sCacheDir)) {
            return sCacheDir;
        }

        File cacheDir = null;
        // 如果SDK版本大于等于8
        if (APIUtils.hasFroyo()) {
            // getExternalCacheDir是在API Level 8引入的，所有必须加以判断
            try {
                // 集中在系统版本4.4.4和4.4.3的手机上。查看源码是IMountService$Stub$Proxy.mkdirs抛出NullPointerException.
                // 此处捕获该异常，继续执行后面的逻辑
                cacheDir = context.getExternalCacheDir();
            } catch (Exception e) {
                if (DEBUG) {
                    e.printStackTrace();
                }
            }
        }

        if (null == cacheDir) {
            // 如果SDCard可写，得到SDCard路径
            boolean sdcardWriteable = isExternalStorageWritable();
            if (sdcardWriteable) {
                cacheDir = android.os.Environment.getExternalStorageDirectory();
                if (null != cacheDir) {
                    cacheDir = new File(cacheDir, DIRECTORY_DATA_CACHE);
                }
            }
        }

        if (null == cacheDir) {
            cacheDir = context.getCacheDir();
        }

        if (null == cacheDir) {
            cacheDir = context.getFilesDir();
        }

        if (null != cacheDir) {
            // 如果目录不存在，则创建
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            sCacheDir = cacheDir.getAbsolutePath();
        }

        return sCacheDir;
    }

    /**
     * 递归删除文件夹
     *
     * @return
     */
    public static boolean deleteDirectory(String directory) {
        String cacheDir = directory;
        if (TextUtils.isEmpty(cacheDir)) {
            return false;
        }

        try {
            File file = new File(cacheDir);
            if (file.exists()) {
                File[] files = file.listFiles();
                if (null != files) {
                    for (File f : files) {
                        f.delete();
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }

        return true;
    }


    /**
     * 判断SDCard是否可用，该方法不是线程安全的。
     *
     * @return true 外置SDCard可写
     */
    public static boolean isExternalStorageWritable() {
        boolean writable = false;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            File file = AppRuntime.getAppContext().getExternalCacheDir();
            String cacheDir = null;
            if (file != null) {
                cacheDir = file.getAbsolutePath();
            }
            if (!TextUtils.isEmpty(cacheDir)) {
                try {
                    // 在/sdcard/Android/data/com.norman.malong/cache/目录下创建一个文件
                    File tempFile = new File(cacheDir, ".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB");
                    if (tempFile.exists()) {
                        writable = true;
                    } else {
                        writable = tempFile.createNewFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return writable;
    }


    /**
     * 判断指定的目录是否可写
     *
     * @param file file
     * @return true/false
     */
    private static boolean isDirectoryWritable(File file) {
        long start = System.currentTimeMillis();

        boolean writable = false;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            File esd = Environment.getExternalStorageDirectory();
            if (esd.exists() && esd.canWrite()) {
                if (null != file && file.exists()) {
                    try {
                        if (file.isDirectory()) {
                            File newFile = new File(file, ".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB");
                            if (newFile.exists()) {
                                File newFile2 = new File(file, ".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB__temp");
                                writable = newFile.renameTo(newFile2);
                                if (writable) {
                                    newFile2.renameTo(newFile);
                                }
                            } else {
                                writable = newFile.createNewFile();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        long end = System.currentTimeMillis();

        if (DEBUG) {
            Log.d(TAG, "PathUtils#isDirectoryWritable(),  time = "
                    + (end - start)
                    + " ms,  file" + file
                    + ",  writable = " + writable);
        }

        return writable;
    }


//    /**
//     * 删除指定的文件
//     *
//     * @param file file
//     * @return true/false
//     * @deprecated 请使用 lib-util 下的 {@link FileUtils#safeDeleteFile(File)}
//     */
//    @Deprecated
//    private static boolean deleteFile(File file) {
//        // 由于在某些手机上面File#delete()方法是异步的，会导致后续的创建文件夹失败，
//        // 推荐的做法是对要删除的文件重命名，然后再删除，这样就不会影响后续创建文件夹。
//        try {
//            String filePath = file.getAbsolutePath();
//            File newFile = new File(filePath);
//            // 构造一个不存在的文件名
//            long time = System.currentTimeMillis();
//            File tempFile = new File(filePath + time + ".tmp");
//            newFile.renameTo(tempFile);
//            boolean succeed = tempFile.delete();
//            return succeed;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }

    /**
     * 删除过期的文件，在v6.1之前系统在sdcard根目录创建了一个.696E5309-E4A7-27C0-A787-0B2CEBF1F1AB文件，
     * 现在需要删除。这个方法内部只会执行一次
     */
    public static void deleteOldFiles() {
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            final String key = "key_path_utils_delete_old_file";
            boolean hasDelete = PreferenceUtils.getBoolean(key, false);
            if (!hasDelete) {
                File root = Environment.getExternalStorageDirectory();
                File newFile = new File(root, ".696E5309-E4A7-27C0-A787-0B2CEBF1F1AB");
                if (newFile.exists()) {
                    boolean succeed = newFile.delete();
                    if (succeed) {
                        PreferenceUtils.setBoolean(key, true);
                    }
                } else {
                    PreferenceUtils.setBoolean(key, true);
                }
            }
        }
    }

    /**
     * 根据文件路径获取文件后缀
     *
     * @param filePath 文件路径
     * @return String 文件后缀
     */
    public static String getFileExtFromUrl(String filePath) {
        String ext = "";
        if (TextUtils.isEmpty(filePath)) {
            return ext;
        }
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            ext = filePath.substring(i + 1);
        }
        return ext;
    }

    public static final String PATH_DEFAULT_DOWNLOAD = DIRCTORY_DOWNLOAD;

    /**
     * 得到下载文件的路径。
     * 1、首选外置应用私有路径。eg：/sdcard/Android/data/com.norman.malong/files/downloads
     * 2、外置sdCard不可用时，使用内置应用私有路径。eg：/data/data/com.norman.malong/files/downloads
     * <p>这个方法会根据当前目录的状态来返回不同的路径，如果"xxx"目录不可用，将返回另一个目录</p>
     *
     * @param context context
     * @return path 下载文件保存路径
     */
    public static File getDownloadDirectory(Context context) {
        boolean sdcardWriteable = isExternalStorageWritable();
        if (sdcardWriteable) {
            // 判断"/sdcard/Android/data/com.norman.malong/files/"根目录是否可用
            File root = context.getExternalFilesDir(null);
            // /sdcard/Android/data/com.norman.malong/files/downloads
            File downloads = new File(root, PATH_DEFAULT_DOWNLOAD);

            boolean createDirs = false;// 目标文件是否创建成功
            if (!downloads.exists()) {
                createDirs = true;
            } else if (!downloads.isDirectory()) {
                // 如果不是文件夹，删除掉，再创建文件夹
                FileUtils.safeDeleteFile(downloads);
                createDirs = true;
            }

            if (createDirs) {
                // 创建目录
                boolean succeed = downloads.mkdirs();
                if (DEBUG && !succeed) {
                    Log.e(TAG, "PathUtils#getDownloadDirectory()," +
                            "创建下载文件失败, directory = " + downloads);
                }
            }
            return downloads;
        }

        return null;
    }

    /**
     * 判断"xxx/xxx"目录是否可用。
     *
     * @return true/false
     */
    private static boolean isBaiduDirectoryWritable() {
        File esd = Environment.getExternalStorageDirectory();
        File file = new File(esd, DIRCTORY_NORMAN);
        boolean writable = isDirectoryWritable(file);

        if (DEBUG) {
            Log.d(TAG, "PathUtils#isBaiduDirectoryWritable(),  path = " + file + ",  writable = " + writable);
        }

        return writable;
    }
}
