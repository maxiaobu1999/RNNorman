package com.norman.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.norman.runtime.AppRuntime;

import java.io.File;
import java.util.List;

/** Activity 相关操作工具类，由Utility部分API迁移而来。 */
public final class ActivityUtils {

    /** DEBUG flag */
    private static final boolean DEBUG = AppRuntime.GLOBAL_DEBUG;
    /** DEBUG tag */
    private static final String TAG = "ActivityUtils";

    /**
     * 安全启动应用程序，截获Exception。
     *
     * @param activity Activity
     * @param intent   Intent
     */
    public static void startActivitySafely(Activity activity, Intent intent) {
        startActivitySafely(activity, intent, true);
    }

    /**
     * 安全启动应用程序，截获Exception。
     *
     * @param activity activity
     * @param intent   Intent
     * @param newTask  是否添加Intent.FLAG_ACTIVITY_NEW_TASK
     * @return 是否调起成功
     */
    public static boolean startActivitySafely(Context activity, Intent intent, boolean newTask) {
        return startActivitySafely(activity, intent, newTask, true);
    }

    /**
     * 安全启动应用程序，截获Exception
     *
     * @param activity  {@link Context}
     * @param intent    {@link Intent}
     * @param newTask   是否添加Intent中的FLAG_ACTIVITY_NEW_TASK标志
     * @param withToast 是否弹出Toast
     * @return 是否调起成功
     */
    public static boolean startActivitySafely(Context activity, Intent intent, boolean newTask, boolean withToast) {
        boolean ret = false;
        if (newTask || !(activity instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            activity.startActivity(intent);
            ret = true;
        } catch (ActivityNotFoundException e) {
            if (withToast) {
                Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (SecurityException e) {
            if (withToast) {
                Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            }
            if (DEBUG) {
                Log.e(TAG, "Launcher does not have the permission to launch " + intent
                        + ". Make sure to create a MAIN intent-filter for the corresponding "
                        + "activity or use the exported attribute for this activity.", e);
            }
        }
        return ret;
    }

    /**
     * 安全启动应用程序，截获Exception，必须在主线程被调用。
     *
     * @param context context
     * @param intent  Intent
     * @return 是否成功启动Activity。
     */
    public static boolean startActivitySafely(Context context, Intent intent) {
        return startActivitySafely(context, intent, false);
    }

    /**
     * 安全启动应用程序，截获Exception，并返回是否成功启动。
     *
     * @param context      Context.
     * @param packageName  包名.
     * @param activityName Activity全名（加上包名前缀）.
     * @return 是否成功启动Activity。
     */
    public static boolean startActivitySafely(Context context, String packageName, String activityName) {
        return startActivitySafely(context, packageName, activityName, true);
    }

    /**
     * 安全启动应用程序，截获Exception，并返回是否成功启动。
     *
     * @param context      Context.
     * @param packageName  包名.
     * @param activityName Activity全名（加上包名前缀）.
     * @param withToast    是否弹出Toast
     * @return 是否成功启动Activity。
     */
    public static boolean startActivitySafely(Context context, String packageName,
                                              String activityName, boolean withToast) {

        boolean result = false;
        if (!TextUtils.isEmpty(packageName) && !TextUtils.isEmpty(activityName)) {
            ComponentName component = new ComponentName(packageName, activityName);
            result = startActivitySafely(context, component, withToast);
        }
        return result;
    }

    /**
     * 安全启动应用程序，截获Exception，并返回是否成功启动。
     *
     * @param context   Context.
     * @param component 组件名，由包名和Activity全名（加上包名前缀）共同生成.
     * @return 是否成功启动Activity。
     */
    public static boolean startActivitySafely(Context context, ComponentName component) {
        return startActivitySafely(context, component, true);
    }

    /**
     * 安全启动应用程序，截获Exception，并返回是否成功启动。
     *
     * @param context   Context.
     * @param component 组件名，由包名和Activity全名（加上包名前缀）共同生成.
     * @param withToast 是否弹出Toast
     * @return 是否成功启动Activity。
     */
    public static boolean startActivitySafely(Context context, ComponentName component, boolean withToast) {

        boolean ret = false;
        if (component != null) {
            Intent intent = new Intent();
            intent.setComponent(component);
            ret = startActivitySafely(context, intent, true, withToast);
        }
        return ret;
    }

    /**
     * @param activity Activity that should get the task description update.
     * @param title    Title of the activity.
     * @param icon     Icon of the activity.
     * @param color    Color of the activity. It must be a fully opaque color.
     */
    public static void setTaskDescription(Activity activity, String title, Bitmap icon, int color) {
        int taskDescriptionColor = color;
        // get fully opaque color
        if (Color.alpha(color) != 255) {
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            taskDescriptionColor = Color.argb(255, red, green, blue);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.TaskDescription description =
                    new ActivityManager.TaskDescription(title, icon, taskDescriptionColor);
            activity.setTaskDescription(description);
        }
    }

    /**
     * Indicates whether the specified action can be used as an intent. This method queries the
     * package manager for installed packages that can respond to an intent with the specified
     * action. If no suitable package is found, this method returns false.
     *
     * @param context The application's environment.
     * @param intent  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (intent == null) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    /**
     * Android N开始，应用不能向外提供scheme为file://的uri。
     * 使用Uri.fromFile() 且 设到 intent 调起 activity 这个应用场景时，如调起系统apk安装界面，
     * 就会调不起来，需要使用FileProvider能力修复该问题。
     *
     * @param context 应用程序上下文
     * @param file    文件路径
     * @param intent  Intent
     */
    public static void processFileUriIntent(Context context, File file, Intent intent) {
        if (APIUtils.hasNougat()) {
            // 异常处理
            Uri fileUri = null;
            try {
                fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".downloads", file);
            } catch (IllegalArgumentException e) {
                if (DEBUG) {
                    throw e;
                }
            }
            if (fileUri == null) {
                return;
            }
            intent.setDataAndType(fileUri, intent.getType());
            List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(intent, 0);
            if (apps == null) {
                return;
            }
            for (ResolveInfo app : apps) {
                if (app.activityInfo != null && app.activityInfo.packageName != null) {
                    context.grantUriPermission(
                            app.activityInfo.packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        }
    }


    /**
     * 安全启动应用程序，获得activity的返回值
     *
     * @param activity {@link Context}
     * @param intent   {@link Intent}
     * @return 是否调起成功
     */
    public static boolean startActivityForResultSafely(Context activity, Intent intent, int requestCode) {
        return startActivityForResultSafely((Activity) activity, intent, requestCode, false, false);
    }


    /**
     * 安全启动应用程序，需要获得activity返回结果
     *
     * @param activity  {@link Activity}
     * @param intent    {@link Intent}
     * @param newTask   是否添加Intent中的FLAG_ACTIVITY_NEW_TASK标志
     * @param withToast 是否弹出Toast
     * @return 是否调起成功
     */

    public static boolean startActivityForResultSafely(Activity activity,
                                                       Intent intent, int requestCode, boolean newTask, boolean withToast) {
        boolean ret = false;
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            activity.startActivityForResult(intent, requestCode);
            ret = true;
        } catch (ActivityNotFoundException e) {
            if (withToast) {
                Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (SecurityException e) {
            if (withToast) {
                Toast.makeText(activity, R.string.activity_not_found, Toast.LENGTH_SHORT).show();
            }
            if (DEBUG) {
                Log.e(TAG, "Launcher does not have the permission to launch " + intent
                        + ". Make sure to create a MAIN intent-filter for the corresponding "
                        + "activity or use the exported attribute for this activity.", e);
            }
        }
        return ret;
    }

    /**
     * activity 是否已经 destroy
     *
     * @param activity activity
     * @return 是否 destroy
     */
    public static boolean isDestroyed(Activity activity) {
        if (activity == null) {
            return true;
        }
        if (activity.isFinishing()) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                && activity.isDestroyed()) {
            return true;
        }
        return false;
    }

}
