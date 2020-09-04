
package com.norman.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;


import com.norman.runtime.AppRuntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * 存储管理util
 * 
 * @author shanghuibo
 * @since 2015/1/5
 *
 */
public final class StorageUtils {
    /** debug switch*/
    private static final boolean DEBUG = AppRuntime.GLOBAL_DEBUG;
    /** debug tag*/
    private static final String TAG = "StorageUtils";
    /** 计算大小的被除数*/
    private static final int DIVIDER = 1024;

    /**
     * constructor
     */
    private StorageUtils() {
        
    }
    
    /**
     * 存储状态info类
     * 
     * @author shanghuibo
     *
     */
    public static class StorageInfo {
        /** dev path*/
        public final String mPath;
        /** 是否是内部存储器*/
        public final boolean mInternal;
        /** 是否只读 */
        public final boolean mReadonly;
        /** display number*/
        public final int mDisplayNumber;

        /**
         * constructor
         * 
         * @param path dev path
         * @param internal 是否是内部存储器
         * @param readonly 是否只读
         * @param displayNumber display number
         */
        StorageInfo(String path, boolean internal, boolean readonly, int displayNumber) {
            this.mPath = path;
            this.mInternal = internal;
            this.mReadonly = readonly;
            this.mDisplayNumber = displayNumber;
        }

        /**
         * 获取存储设备名
         * 
         * @return 存储设备名
         */
        public String getDisplayName() {
            StringBuilder res = new StringBuilder();
            if (mInternal) {
                res.append("Internal SD card");
            } else if (mDisplayNumber > 1) {
                res.append("SD card " + mDisplayNumber);
            } else {
                res.append("SD card" + mDisplayNumber);
            }
            if (mReadonly) {
                res.append(" (Read only)");
            }
            return res.toString();
        }
    }

    /**
     * 获取存储设备列表
     * 
     * @return 存储设备列表
     */
    @SuppressLint("NewApi")
    public static List<StorageInfo> getStorageList() {

        HashMap<String, StorageInfo> map = new HashMap<String, StorageInfo>();
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        String defPath = Environment.getExternalStorageDirectory().getPath();
        boolean defPathInternal = false;
        if (APIUtils.hasGingerbread()) {
            defPathInternal = !Environment.isExternalStorageRemovable();
        }
        String defPathState = Environment.getExternalStorageState();
        boolean defPathAvailable = defPathState.equals(Environment.MEDIA_MOUNTED)
                                    || defPathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
        boolean defPathReadonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
        BufferedReader bufReader = null;
        try {
            HashSet<String> paths = new HashSet<String>();
            bufReader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            int curDisplayNumber = 1;
            if (DEBUG) {
                Log.d(TAG, "/proc/mounts");
            }
            while ((line = bufReader.readLine()) != null) {
                if (DEBUG) {
                    Log.d(TAG, line);
                }
                StringTokenizer tokens = new StringTokenizer(line, " ");
                String device = tokens.nextToken(); // device
                String mountPoint = tokens.nextToken(); // mount point
                if (paths.contains(mountPoint)) {
                    continue;
                }
                String unused = tokens.nextToken(); // file system
                List<String> flags = Arrays.asList(tokens.nextToken().split(",")); // flags
                boolean readonly = flags.contains("ro");
                
                if (line.contains("vfat") || line.contains("/mnt")) {
                    if (mountPoint.equals(defPath)) {
                        paths.add(defPath);
                        map.put(device, new StorageInfo(defPath, defPathInternal, readonly, -1));
                    } else if (line.contains("/dev/block/vold")) {
                        if (!line.contains("/mnt/secure")
                            && !line.contains("/mnt/asec")
                            && !line.contains("/mnt/obb")
                            && !line.contains("/dev/mapper")
                            && !line.contains("tmpfs")) {
                            paths.add(mountPoint);
                            if (!map.containsKey(device)) {
                                map.put(device, new StorageInfo(mountPoint, false, readonly, curDisplayNumber++));
                            }
                        }
                    } else if (paths.contains(device)) {
                        // 4.4手机无法直接访问/mnt/media_rw/extSdCard路径，会导致EACCESS错误出现崩溃
                        // 追查发现会将这个路径以/storage/sdcard的名字挂载，因此在这里添加替换机制，发现有以device为路径的storageInfo
                        // 则将storageInfo删掉，以当前的mount point作为新的storageInfo
                        String removeDevice = null;
                        for (String key : map.keySet()) {
                            StorageInfo info = map.get(key);
                            if (TextUtils.equals(info.mPath, device)) {
                                removeDevice = key;
                                break;
                            }
                        }
                        map.remove(removeDevice);
                        paths.add(mountPoint);
                        if (!map.containsKey(device)) {
                            map.put(device, new StorageInfo(mountPoint, false, readonly, curDisplayNumber++));
                        }
                       
                    }
                } else if (isFuseStorage(device, mountPoint)) {
                    // 判断/dev/fuse开头的设备
                    paths.add(mountPoint);
                    if (isPathAccessable(mountPoint)) {
                        list.add(new StorageInfo(mountPoint, false, readonly, curDisplayNumber++));
                    }
                }

            }

            for (StorageInfo info : map.values()) {
                if (isPathAccessable(info.mPath)) {
                    // 将不可读的筛选出去
                    list.add(info);
                }
            }
            if (!paths.contains(defPath) && defPathAvailable) {
                list.add(0, new StorageInfo(defPath, defPathInternal, defPathReadonly, -1));
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return list;
    }
    
    /**
     * 检查路径是否可访问
     * 
     * @param path 需要访问的路径
     * @return 路径是否可访问
     */
    private static boolean isPathAccessable(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            return file.canRead();
        }
        return false;
    }
    
    /**
     * 是否是/dev/fuse开头且可读的设备
     * ES浏览器RD友情提供
     * 
     * @param device Device
     * @param mountPoint Mount Point
     * @return 是否是/dev/fuse开头且可读的设备
     */
    private static boolean isFuseStorage(String device, String mountPoint) {
        if (device != null && device.contains("/dev/fuse") 
                && mountPoint != null 
                && !mountPoint.startsWith("/storage/emulated/legacy")
                && !mountPoint.contains("/Android/obb")) {
            
            
            if (mountPoint.startsWith("/storage/")) {
                return true;
            }
            // for 4.4 above, all /dev/fuse devices which are not mounted on /mnt/ directory, consider available
            if (APIUtils.hasKitKat() 
                    && !mountPoint.startsWith("/mnt/") 
                    && !mountPoint.startsWith("/data/")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 错误
     */
    private static final int ERROR = -1;

    /**
     * 外部存储是否可用
     * 
     * @return 外部存储是否可用
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部可用空间大小(Byte)
     * 
     * @return 手机内部可用空间大小
     */
    public static long getAvailableInternalMemorySize() {
        long blockSize;
        long availableBlocks;
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        // 防止Int型越界返回负值
        if (APIUtils.hasJellyBeanMR2()) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间大小(Byte)
     * 
     * @return 手机内部空间大小
     */
    public static long getTotalInternalMemorySize() {
        long blockSize;
        long totalBlocks;
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        // 防止Int型越界返回负值
        if (APIUtils.hasJellyBeanMR2()) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机外部可用空间大小(Byte)
     * 
     * @return 手机外部可用空间大小
     */
    public static long getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            long blockSize;
            long availableBlocks;
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            // 防止Int型越界返回负值
            if (APIUtils.hasJellyBeanMR2()) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            return availableBlocks * blockSize;
        } else {
            return ERROR;
        }
    }
    
    /**
     * 获取指定设备路径的可用存储空间(Byte)
     * 
     * @param path 指定设备路径
     * @return 可用存储空间
     */
    public static long getAvailaleMemorySize(String path) {
        try {
            long blockSize;
            long availableBlocks;
            StatFs stat = new StatFs(path);
            // 防止Int型越界返回负值
            if (APIUtils.hasJellyBeanMR2()) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            return availableBlocks * blockSize;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 获取指定设备路径的全部存储空间(Byte)
     * 
     * @param path 指定设备路径
     * @return 可用存储空间
     */
    public static long getTotalMemorySize(String path) {
        try {
            long blockSize;
            long totalBlocks;
            StatFs stat = new StatFs(path);
            // 防止Int型越界返回负值
            if (APIUtils.hasJellyBeanMR2()) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            }
            return totalBlocks * blockSize;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取手机外部空间大小(Byte)
     * 
     * @return 手机外部存储空间大小
     */
    public static long getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            long blockSize;
            long totalBlocks;
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            // 防止Int型越界返回负值
            if (APIUtils.hasJellyBeanMR2()) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            }
            return totalBlocks * blockSize;
        } else {
            return ERROR;
        }
    }

    /**
     * 格式化size
     * 
     * @param size 存储空间大小
     * @return 格式化的存储空间大小
     */
    public static String formatSize(long size) {
        String suffix = "KB";
        double dSize = size;
        if (dSize >= DIVIDER) {
            suffix = "KB";
            dSize /= DIVIDER;
            if (dSize >= DIVIDER) {
                suffix = "MB";
                dSize /= DIVIDER;
                if (dSize >= DIVIDER) {
                    suffix = "GB";
                    dSize /= DIVIDER;
                }
            }
        } else {
            dSize = 0;
        }

        return String.format(Locale.CHINESE, "%.2f%s", dSize, suffix);
    }

    /**
     * @Description: 判断所传的目录是否有足够的空间可用
     * @param path 需要判断的目录
     * @param size 需要的大小
     * @return boolean 有足够的空间则为true，否则为false
     */
    public static boolean isEnoughSpace(File path, final long size) {
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSize();
        long freeBolcks = sf.getAvailableBlocks();

        if (DEBUG) {
            Log.d(TAG, "Available size:" + (blockSize * freeBolcks));
        }
        if ((blockSize * freeBolcks) > size) {
            return true;
        }
        return false;
    }
    
    /**
     * Returns the mount path for the volume.
     *
     * @param volume the mount path
     */
    public static String getVolumePath(Object volume) {
        String result = "";
        Object o = ReflectionUtils.invokeHideMethodForObject(volume, "getPath", null, null);
        if (o != null) {
            result = (String) o;
        }
        
        return result;
    }
    
    /**
     * Returns list of all mountable volumes
     */
    public static Object[] getVolumeList() {
        StorageManager manager =
                (StorageManager) AppRuntime.getAppContext().getSystemService(Context.STORAGE_SERVICE);
        Object[] result = null;
        Object o = ReflectionUtils.invokeHideMethodForObject(manager, "getVolumeList", null, null);
        if (o != null) {
            result = (Object[]) o;
        }
        
        return result;
    }
    
    /**
     * Gets the state of a volume via its mountpoint.
     *
     * @param volumePath
     */
    public static String getVolumeState(String volumePath) {
        StorageManager manager =
                (StorageManager) AppRuntime.getAppContext().getSystemService(Context.STORAGE_SERVICE);
        String result = "";
        Object o =
                ReflectionUtils.invokeHideMethodForObject(manager, "getVolumeState", new Class[]{String.class},
                        new Object[]{volumePath});
        if (o != null) {
            result = (String) o;
        }
        
        return result;
    }
}