package com.norman.util;

import android.os.Environment;
import android.os.Process;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static boolean sLogEnabled = true;
    private static boolean sLog2File = false;
    private static Logger sFilelogger;
    public static final int FILE_LIMETE = 10485760;
    public static final int FILE_NUMBER = 2;

    private Log() {
    }

    public static void v(String tag, String msg) {
        if (sLogEnabled) {
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.v(tag, msg);
            }
        }

    }

    public static void v(String tag, String msg, Throwable tr) {
        v(tag, msg + '\n' + getStackTraceString(tr));
    }

    public static void i(String tag, String msg) {
        if (sLogEnabled) {
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.i(tag, msg);
            }
        }

    }

    public static void i(String tag, String msg, Throwable tr) {
        i(tag, msg + '\n' + getStackTraceString(tr));
    }

    public static void d(String tag, String msg) {
        if (sLogEnabled) {
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.INFO, tag + ": " + msg);
            } else {
                android.util.Log.d(tag, msg);
            }
        }

    }

    public static void d(String tag, String msg, Throwable tr) {
        d(tag, msg + '\n' + getStackTraceString(tr));
    }

    public static void w(String tag, String msg) {
        if (sLogEnabled) {
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.WARNING, tag + ": " + msg);
            } else {
                android.util.Log.w(tag, msg);
            }
        }

    }

    public static void w(String tag, String msg, Throwable tr) {
        w(tag, msg + '\n' + getStackTraceString(tr));
    }

    public static void e(String tag, String msg) {
        if (sLogEnabled) {
            if (sLog2File && sFilelogger != null) {
                sFilelogger.log(Level.SEVERE, tag + ": " + msg);
            } else {
                android.util.Log.e(tag, msg);
            }
        }

    }

    public static void e(String tag, Throwable e) {
        String msg = getStackTraceString(e);
        e(tag, msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        msg = msg + '\n' + getStackTraceString(tr);
        e(tag, msg);
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            return sw.toString();
        }
    }

    private static String getLogFileName() {
        int pid = Process.myPid();
        String name = getProcessNameForPid(pid);
        if (TextUtils.isEmpty(name)) {
            name = "FileLog";
        }

        name = name.replace(':', '_');
        return name;
    }

    private static String getProcessNameForPid(int pid) {
        String cmdlinePath = "/proc/" + pid + "/cmdline";
        String statusPath = "/proc/" + pid + "/status";
        String name = "";

        try {
            File file = new File(cmdlinePath);
            BufferedReader bf = new BufferedReader(new FileReader(file));
            String line = null;
            line = bf.readLine();
            int index;
            if (!TextUtils.isEmpty(line)) {
                index = line.indexOf(0);
                name = line.substring(0, index);
            } else {
                file = new File(statusPath);
                bf = new BufferedReader(new FileReader(file));

                for(line = bf.readLine(); line != null; line = bf.readLine()) {
                    if (line.startsWith("Name:")) {
                        index = line.indexOf("\t");
                        if (index >= 0) {
                            name = line.substring(index + 1);
                        }
                        break;
                    }
                }
            }

            bf.close();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return name;
    }

    public static void setLogEnabled(boolean enableOrNot) {
        sLogEnabled = enableOrNot;
    }

    public static void setLog2File(boolean log2file) {
        sLog2File = log2file;
        if (sLog2File && sFilelogger == null) {
            String LOGGER_NAME = getLogFileName();
            String LOG_FILE_NAME = (new File(Environment.getExternalStorageDirectory(), LOGGER_NAME)).getAbsolutePath();

            try {
                FileHandler fhandler = new FileHandler(LOG_FILE_NAME + "_%g.log", 10485760, 2, true);
                fhandler.setFormatter(new SimpleFormatter());
                sFilelogger = Logger.getLogger(LOGGER_NAME);
                sFilelogger.setLevel(Level.ALL);
                sFilelogger.addHandler(fhandler);
            } catch (SecurityException var5) {
                var5.printStackTrace();
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

    }
}
