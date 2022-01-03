package ru.meefik.busybox;

import android.content.Context;
import android.content.res.AssetManager;
import android.system.ErrnoException;
import android.system.Os;

import com.topjohnwu.superuser.CallbackList;
import com.topjohnwu.superuser.Shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

class AssetsUtils {
    final private Context context;
    final private Logger logger;

    AssetsUtils(Context context, Logger logger) {
        this.context = context;
        this.logger = logger;
    }

    /**
     * Closeable helper
     *
     * @param c closable object
     */
    static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Extract file to env directory
     *
     * @param rootAsset root asset name
     * @param path      path to asset file
     * @return false if error
     */
    boolean extractFile(String rootAsset, String path) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(rootAsset + path);
            String fullPath = PrefStore.getFilesDir(context) + path;
            out = new FileOutputStream(fullPath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(in);
            close(out);
        }
        return true;
    }

    /**
     * Extract path to env directory
     *
     * @param rootAsset root asset name
     * @param path      path to asset directory
     * @return false if error
     */
    boolean extractDir(String rootAsset, String path) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] assets = assetManager.list(rootAsset + path);
            if (assets.length > 0) {
                String fullPath = PrefStore.getFilesDir(context) + path;
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    if (!dir.mkdir()) return false;
                }
                for (String asset : assets) {
                    if (!extractDir(rootAsset, path + "/" + asset)) return false;
                }
            } else {
                if (!extractFile(rootAsset, path)) return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursive remove all from directory
     *
     * @param path path to directory
     */
    void cleanDirectory(File path) {
        if (path == null) return;
        if (path.exists()) {
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) cleanDirectory(f);
                f.delete();
            }
        }
    }

    /**
     * Recursive set permissions to directory
     *
     * @param path path to directory
     */
    void setPermissions(File path) {
        if (path == null) return;
        if (path.exists()) {
            path.setReadable(true, false);
            path.setExecutable(true, false);
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) setPermissions(f);
                f.setReadable(true, false);
                f.setExecutable(true, false);
            }
        }
    }

    /**
     * Update version file
     *
     * @return false if error
     */
    boolean setVersion() {
        boolean result = false;
        String f = PrefStore.getFilesDir(context) + "/version";
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(f));
            bw.write(PrefStore.getVersion(context));
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(bw);
        }
        return result;
    }

    /**
     * Check latest env version
     *
     * @return false if error
     */
    boolean isLatestVersion() {
        File f = new File(PrefStore.getFilesDir(context) + "/version");
        if (!f.exists()) return false;
        BufferedReader br = null;
        boolean result = false;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = br.readLine();
            if (PrefStore.getVersion(context).equals(line)) result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(br);
        }
        return result;
    }

    /**
     * Update env directory
     *
     * @return false if error
     */
    boolean extractAssets() {
        if (isLatestVersion()) return true;

        // prepare bin directory
        String binDir = PrefStore.getFilesDir(context) + "/bin";
        File fd = new File(binDir);
        if (!fd.exists()) {
            if (!fd.mkdirs()) return false;
        }
        cleanDirectory(fd);

        // create .nomedia
        File noMediaFd = new File(PrefStore.getFilesDir(context) + "/.nomedia");
        if (!noMediaFd.exists()) {
            try {
                noMediaFd.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // create symlink to busybox
        String libDir = PrefStore.getLibDir(context);
        File busybox = new File(binDir + "/busybox");
        if (!busybox.exists()) {
            try {
                Os.symlink(libDir + "/libbusybox.so", binDir + "/busybox");
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }

        // create symlink to ssl_helper
        File sslHelper = new File(binDir + "/ssl_helper");
        if (!sslHelper.exists()) {
            try {
                Os.symlink(libDir + "/libssl_helper.so", binDir + "/ssl_helper");
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }

        // extract assets
        extractDir("all", "");
        String mArch = PrefStore.getArch();
        switch (mArch) {
            case "arm":
                extractDir("arm", "");
                break;
            case "arm64":
                extractDir("arm64", "");
                break;
            case "x86":
                extractDir("x86", "");
                break;
            case "x86_64":
                extractDir("x86_64", "");
                break;
        }

        // set permissions
        setPermissions(fd);

        // install applets
        String cmd = String.format("busybox --install -s %s", binDir);
        Shell.sh(cmd).exec();

        // update version
        return setVersion();
    }
}