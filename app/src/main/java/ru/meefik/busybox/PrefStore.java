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
class PrefStore {

    static final boolean STATIC_VERSION = true;
    static final String APP_PREF_NAME = "app_settings";
    private static final String LOG_FILE = "busybox.log";

    /**
     * Get application version
     *
     * @param c context
     * @return version, format versionName-versionCode
     */
    static String getVersion(Context c) {
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
    static String getStorage() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * Get environment directory
     *
     * @param c context
     * @return path, e.g. /data/data/com.example.app/files
     */
    static String getEnvDir(Context c) {
        return c.getFilesDir().getAbsolutePath();
    }

    /**
     * Get language code
     *
     * @param c context
     * @return locale
     */
    private static Locale getLocale(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String language = pref.getString("language", c.getString(R.string.language));
        boolean emptyLang = language.isEmpty();
        if (emptyLang) {
            language = Locale.getDefault().getLanguage();
        }
        Locale locale;
        switch (language.toLowerCase()) {
                case "be":
                case "de":
                case "es":
                case "fr":
                case "ko":
                case "pl":
                case "ru":
                    locale = new Locale(language);
                    break;
                case "zh_cn":
                    locale = Locale.SIMPLIFIED_CHINESE;
                    break;
                case "zh_tw":
                    locale = Locale.TRADITIONAL_CHINESE;
                    break;
                default:
                    language = "en";
                    locale = Locale.ENGLISH;
        }
        if (emptyLang) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("language", language);
            editor.apply();
        }
        return locale;
    }

    /**
     * Get application theme resource id
     *
     * @param c context
     * @return resource id
     */
    static int getTheme(Context c) {
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
    static int getFontSize(Context c) {
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
    static int getMaxLines(Context c) {
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
    static boolean isTimestamp(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("timestamp", c.getString(R.string.timestamp).equals("true"));
    }

    /**
     * Debug mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    static boolean isDebugMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("debug", c.getString(R.string.debug).equals("true"));
    }

    /**
     * Trace mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    static boolean isTraceMode(Context c) {
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
    static boolean isLogger(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("logger", c.getString(R.string.logger).equals("true"));
    }

    /**
     * Get path of log file
     *
     * @param c context
     * @return path
     */
    static String getLogFile(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        String logFile = pref.getString("logfile", c.getString(R.string.logfile));
        if (logFile.length() == 0) {
            logFile = getStorage() + File.separator + LOG_FILE;
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("logfile", logFile);
            editor.apply();
        }
        return logFile;
    }

    /**
     * Get hardware architecture
     *
     * @param arch unformated architecture
     * @return x86, arm or mips
     */
    private static String getArch(String arch) {
        String march = "unknown";
        if (arch.length() > 0) {
            char a = arch.toLowerCase().charAt(0);
            switch (a) {
                case 'a':
                    if (arch.equals("amd64"))
                        march = "x86";
                    else
                        march = "arm";
                    break;
                case 'm':
                    march = "mips";
                    break;
                case 'i':
                case 'x':
                    march = "x86";
                    break;
            }
        }
        return march;
    }

    /**
     * Get current hardware architecture
     *
     * @return x86, arm or mips
     */
    static String getArch() {
        return getArch(System.getProperty("os.arch"));
    }

    /**
     * Get directory of BusyBox installation
     *
     * @param c context
     * @return path, e.g. /system/xbin
     */
    static String getInstallDir(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString("installdir", c.getString(R.string.installdir));
    }

    /**
     * Applets is enabled
     *
     * @param c context
     * @return true, if install applets
     */
    static boolean isInstallApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("applets", c.getString(R.string.applets).equals("true"));
    }

    /**
     * Replace mode is enabled
     *
     * @param c context
     * @return true, if replace applets
     */
    static boolean isReplaceApplets(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("replace", c.getString(R.string.replace).equals("true"));
    }

    /**
     * Set application locale
     *
     * @param c context
     */
    static void setLocale(Context c) {
        Locale locale = getLocale(c);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        c.getResources().updateConfiguration(config, c.getResources().getDisplayMetrics());
    }

}