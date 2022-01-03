package ru.meefik.busybox;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Environment;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

class PrefStore {

    static final String APP_PREF_NAME = "app_settings";
    private static final String LOG_FILE = "busybox.log";

    static String getFilesDir(Context c) {
        return c.getFilesDir().getAbsolutePath();
    }

    static String getLibDir(Context c) {
        return c.getApplicationInfo().nativeLibraryDir;
    }

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
            case "uk":
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
        int fontSizeInt;
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
        int maxLinesInt;
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
     * Trace mode is enabled
     *
     * @param c context
     * @return true if enabled
     */
    static boolean isTraceMode(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("trace", c.getString(R.string.trace).equals("true"));
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
        if (!logFile.contains("/")) {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            logFile = downloadDir.getAbsolutePath() + "/" + LOG_FILE;
        }
        return logFile;
    }

    /**
     * Get hardware architecture
     *
     * @param arch unformated architecture
     * @return arm, arm_64, x86, x86_64
     */
    private static String getArch(String arch) {
        String march = "unknown";
        if (arch.length() > 0) {
            char a = arch.toLowerCase().charAt(0);
            switch (a) {
                case 'a':
                    if (arch.equals("amd64")) march = "x86_64";
                    else if (arch.contains("64")) march = "arm64";
                    else march = "arm";
                    break;
                case 'i':
                case 'x':
                    if (arch.contains("64")) march = "x86_64";
                    else march = "x86";
                    break;
                case 'm':
                    if (arch.contains("64")) march = "mips64";
                    else march = "mips";
                    break;
            }
        }
        return march;
    }

    /**
     * Get current hardware architecture
     *
     * @return arm, arm_64, x86, x86_64
     */
    static String getArch() {
        return getArch(Objects.requireNonNull(System.getProperty("os.arch")));
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
     * Mount as RAM disk
     *
     * @param c context
     * @return true, if ram disk
     */
    static boolean isRamDisk(Context c) {
        SharedPreferences pref = c.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean("ramdisk", c.getString(R.string.ramdisk).equals("true"));
    }

    /**
     * Set application locale
     *
     * @param c context
     */
    static void setLocale(Context c) {
        Locale locale = getLocale(c);
        Locale.setDefault(locale);
        Configuration config = c.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        c.createConfigurationContext(config);
        c.getResources().updateConfiguration(config, c.getResources().getDisplayMetrics());
    }

}