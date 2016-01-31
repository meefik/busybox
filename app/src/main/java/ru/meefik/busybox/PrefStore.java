package ru.meefik.busybox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;

import java.io.File;
import java.util.Locale;

/**
 * Created by anton on 18.09.15.
 */
public class PrefStore {

    public static final boolean STATIC_VERSION = false;
    public static final String APP_PREF_NAME = "app_settings";
    private static final String LOG_FILE = "busybox.log";

    /**
     * Get application version
     *
     * @param c context
     * @return version, format versionName-versionCode
     */
    public static String getVersion(Context c) {
        String version = "";
        try {
            PackageInfo pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            version = pi.versionName + "-" + pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * Get external storage path
     *
     * @return path
     */
    public static String getStorage() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get environment directory
     *
     * @param c context
     * @return path, e.g. /data/data/com.example.app/files
     */
    public static String getEnvDir(Context c) {
        return c.getFilesDir().getAbsolutePath();
    }

    /**
     * Get language code
     *
     * @param c context
     * @return language code, e.g. "en"
     */
    public static String getLanguage(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString("language", c.getString(R.string.language));
        if (language.length() == 0) {
            String countryCode = Locale.getDefault().getLanguage();
            switch (countryCode) {
                case "ru":
                    language = countryCode;
                    break;
                default:
                    language = "en";
            }
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("language", language);
            editor.commit();
        }
        return language;
    }

    /**
     * Get application theme resource id
     *
     * @param c context
     * @return resource id
     */
    public static int getTheme(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String theme = pref.getString("theme", c.getString(R.string.theme));
        int themeId = R.style.DarkTheme;
        switch (theme) {
            case "dark":
                themeId = R.style.DarkTheme;
                break;
            case "light":
                themeId = R.style.LightTheme;
                break;
        }
        return themeId;
    }

    /**
     * Get font size
     *
     * @param c context
     * @return font size
     */
    public static int getFontSize(Context c) {
        Integer fontSizeInt;
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String fontSize = pref.getString("fontsize", c.getString(R.string.fontsize));
        try {
            fontSizeInt = Integer.parseInt(fontSize);
        } catch (Exception e) {
            fontSize = c.getString(R.string.fontsize);
            fontSizeInt = Integer.parseInt(fontSize);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("fontsize", fontSize);
            editor.apply();
        }
        return fontSizeInt;
    }

    /**
     * Get maximum limit to scroll
     *
     * @param c context
     * @return number of lines
     */
    public static int getMaxLines(Context c) {
        Integer maxLinesInt;
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String maxLines = pref.getString("maxlines", c.getString(R.string.maxlines));
        try {
            maxLinesInt = Integer.parseInt(maxLines);
        } catch (Exception e) {
            maxLines = c.getString(R.string.maxlines);
            maxLinesInt = Integer.parseInt(maxLines);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("maxlines", maxLines);
            editor.apply();
        }
        return maxLinesInt;
    }

    /**
     * Timestamp is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isTimestamp(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("timestamp", c.getString(R.string.timestamp).equals("true"));
    }

    /**
     * Debug mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isDebugMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("debug", c.getString(R.string.debug).equals("true"));
    }

    /**
     * Trace mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isTraceMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("debug", c.getString(R.string.debug).equals("true")) &&
                pref.getBoolean("trace", c.getString(R.string.trace).equals("true"));
    }

    /**
     * Logging is enabled
     *
     * @param c context
     * @return true if enabled
     */
    public static boolean isLogger(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("logger", c.getString(R.string.logger).equals("true"));
    }

    /**
     * Get path of log file
     *
     * @param c context
     * @return path
     */
    public static String getLogFile(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String logFile = pref.getString("logfile", c.getString(R.string.logfile));
        if (logFile.length() == 0) {
            logFile = getStorage() + File.separator + LOG_FILE;
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("logfile", logFile);
            editor.commit();
        }
        return logFile;
    }

    /**
     * Get terminal script
     *
     * @param c context
     * @return script
     */
    public static String getTerminalCmd(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return c.getString(R.string.terminalcmd).replace("${ENV_DIR}", getEnvDir(c));
    }

    /**
     * Get hardware architecture
     *
     * @param arch unformated architecture
     * @return intel, arm or mips
     */
    public static String getArch(String arch) {
        String march = "unknown";
        if (arch.length() > 0) {
            char a = arch.toLowerCase().charAt(0);
            switch (a) {
                case 'a':
                    if (arch.equals("amd64"))
                        march = "intel";
                    else
                        march = "arm";
                    break;
                case 'm':
                    march = "mips";
                    break;
                case 'i':
                case 'x':
                    march = "intel";
                    break;
            }
        }
        return march;
    }

    /**
     * Get current hardware architecture
     *
     * @return intel, arm or mips
     */
    public static String getArch() {
        return getArch(System.getProperty("os.arch"));
    }

    /**
     * Get directory of BusyBox installation
     *
     * @param c context
     * @return path, e.g. /system/xbin
     */
    public static String getInstallDir(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("installdir", c.getString(R.string.installdir));
    }

    /**
     * Applets is enabled
     *
     * @param c context
     * @return true, if install applets
     */
    public static boolean isInstallApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("applets", c.getString(R.string.applets).equals("true"));
    }

    /**
     * Replace mode is enabled
     *
     * @param c context
     * @return true, if replace applets
     */
    public static boolean isReplaceApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("replace", c.getString(R.string.replace).equals("true"));
    }

    /**
     * Set application locale
     *
     * @param c context
     */
    public static void setLocale(Context c) {
        String language = getLanguage(c);
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        c.getResources().updateConfiguration(config, c.getResources().getDisplayMetrics());
    }

}