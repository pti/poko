package fi.reuna.poko;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static android.util.Log.WARN;

public class Log {

    private static final int OFF = 100;

    private static final String MD_LOG_LEVEL_NAME = "logLevel";

    private static final String[] MD_LOG_LEVEL_VALUES = {
            "verbose", "debug", "info", "warn", "error", "off"
    };

    private static final int[] LOG_LEVELS = {
            VERBOSE, DEBUG, INFO, WARN, ERROR, OFF
    };

    private static int LEVEL = OFF;
    private static String TAG_PREFIX = null;

    public static void initialize(Context context) {
        final String packageName = context.getPackageName();
        TAG_PREFIX = convertPackageNameToTagPrefix(packageName);
        ApplicationInfo info = null;

        try {
            info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
        } catch (Throwable ignored) {
        }

        if (info == null || (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0) {
            LEVEL = OFF;

        } else {
            String metaData = info.metaData.getString(MD_LOG_LEVEL_NAME, null);
            LEVEL = convertLogLevelMetaDataValueToLogLevel(metaData, VERBOSE);
        }
    }

    private static int convertLogLevelMetaDataValueToLogLevel(String metaData,
                                                              int defaultLevel)
    {
        for (int i = 0; i < MD_LOG_LEVEL_VALUES.length; i++) {

            if (MD_LOG_LEVEL_VALUES[i].equalsIgnoreCase(metaData)) {
                return LOG_LEVELS[i];
            }
        }

        return defaultLevel;
    }

    private static String convertPackageNameToTagPrefix(String packageName) {
        int lastDot = packageName.lastIndexOf('.');
        String lastPart;

        if (lastDot < 0) {
            lastPart = packageName;
        } else {
            lastPart = packageName.substring(lastDot + 1);
        }

        return lastPart + ".";
    }

    public static void v(String msg) {
        if (VERBOSE >= LEVEL) android.util.Log.v(completeTag(null), msg);
    }

    public static void v(String format, Object... args) {
        if (VERBOSE >= LEVEL) android.util.Log.v(completeTag(null), String.format(format, args));
    }

    public static void v(String msg, Throwable t) {
        if (VERBOSE >= LEVEL) android.util.Log.v(completeTag(null), msg, t);
    }

    public static void v(String format, Throwable t, Object... args) {
        if (VERBOSE >= LEVEL) android.util.Log.v(completeTag(null), String.format(format, args), t);
    }

    public static void d(String msg) {
        if (DEBUG >= LEVEL) android.util.Log.d(completeTag(null), msg);
    }

    public static void d(String format, Object... args) {
        if (DEBUG >= LEVEL) android.util.Log.d(completeTag(null), String.format(format, args));
    }

    public static void d(String msg, Throwable t) {
        if (DEBUG >= LEVEL) android.util.Log.d(completeTag(null), msg, t);
    }

    public static void d(String format, Throwable t, Object... args) {
        if (DEBUG >= LEVEL) android.util.Log.d(completeTag(null), String.format(format, args), t);
    }

    public static void i(String msg) {
        if (INFO >= LEVEL) android.util.Log.i(completeTag(null), msg);
    }

    public static void i(String format, Object... args) {
        if (INFO >= LEVEL) android.util.Log.i(completeTag(null), String.format(format, args));
    }

    public static void i(String msg, Throwable t) {
        if (INFO >= LEVEL) android.util.Log.i(completeTag(null), msg, t);
    }

    public static void i(String format, Throwable t, Object... args) {
        if (INFO >= LEVEL) android.util.Log.i(completeTag(null), String.format(format, args), t);
    }

    public static void w(String msg) {
        if (WARN >= LEVEL) android.util.Log.w(completeTag(null), msg);
    }

    public static void w(String format, Object... args) {
        if (WARN >= LEVEL) android.util.Log.w(completeTag(null), String.format(format, args));
    }

    public static void w(String msg, Throwable t) {
        if (WARN >= LEVEL) android.util.Log.w(completeTag(null), msg, t);
    }

    public static void w(String format, Throwable t, Object... args) {
        if (WARN >= LEVEL) android.util.Log.w(completeTag(null), String.format(format, args), t);
    }

    public static void e(String msg) {
        if (ERROR >= LEVEL) android.util.Log.e(completeTag(null), msg);
    }

    public static void e(String format, Object... args) {
        if (ERROR >= LEVEL) android.util.Log.e(completeTag(null), String.format(format, args));
    }

    public static void e(String msg, Throwable t) {
        if (ERROR >= LEVEL) android.util.Log.e(completeTag(null), msg, t);
    }

    public static void e(String format, Throwable t, Object... args) {
        if (ERROR >= LEVEL) android.util.Log.e(completeTag(null), String.format(format, args), t);
    }

    public static void wtf(String msg) {
        if (ERROR >= LEVEL) android.util.Log.wtf(completeTag(null), msg);
    }

    public static void wtf(String format, Object... args) {
        if (ERROR >= LEVEL) android.util.Log.wtf(completeTag(null), String.format(format, args));
    }

    public static void wtf(String msg, Throwable t) {
        if (ERROR >= LEVEL) android.util.Log.wtf(completeTag(null), msg, t);
    }

    public static void wtf(String format, Throwable t, Object... args) {
        if (ERROR >= LEVEL) android.util.Log.wtf(completeTag(null), String.format(format, args), t);
    }

    protected static String completeTag(String tag) {

        if (tag == null) {
            // Figure out the class and method names from the stack trace and used them
            // to create the tag string.

            // We need to manually set source class and method because
            // the standard implementation would just use this class and method as the source.
            StackTraceElement stack[] = Thread.currentThread().getStackTrace();
            String className = null;
            String methodName = null;
            boolean foundLogElement = false;

            for (StackTraceElement ste : stack) {
                if (Log.class.getName().equals(ste.getClassName())) {
                    foundLogElement = true;

                } else if (foundLogElement) {
                    className = ste.getClassName();

                    int lastDot = className.lastIndexOf('.');

                    if (lastDot > -1) {
                        className = className.substring(lastDot + 1);
                    }

                    methodName = ste.getMethodName();
                    break;
                }
            }

            StringBuilder tmp = new StringBuilder();

            if (TAG_PREFIX != null) {
                tmp.append(TAG_PREFIX);
            }

            tmp.append(className).append('.').append(methodName);
            return tmp.toString();

        } else if (TAG_PREFIX != null && !tag.startsWith(TAG_PREFIX)) {
            return TAG_PREFIX + tag;

        } else {
            return tag;
        }
    }
}
