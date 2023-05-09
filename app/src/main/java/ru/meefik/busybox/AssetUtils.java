package ru.meefik.busybox;

import android.content.Context;
import android.content.res.AssetManager;
import android.system.ErrnoException;
import android.system.Os;

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
import java.util.Objects;

class AssetUtils {

    final private Context context;

    /**
     * AssetsUtils constructor
     *
     * @param context application context
     */
    AssetUtils(Context context) {
        this.context = context;
    }

    /**
     * Closeable helper
     *
     * @param c closable object
     */
    private void close(Closeable c) {
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
    private void extractFile(String rootAsset, String path) {
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
            // ignore
        } finally {
            close(in);
            close(out);
        }
    }

    /**
     * Extract path to env directory
     *
     * @param rootAsset root asset name
     * @param path      path to asset directory
     * @return false if error
     */
    private boolean extractDir(String rootAsset, String path) {
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
                extractFile(rootAsset, path);
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
    private void cleanDirectory(File path) {
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
    private void setPermissions(File path) {
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
    private boolean setVersion() {
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
    private boolean isLatestVersion() {
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
        if (isLatestVersion()) return false;

        // prepare bin directory
        String binDir = PrefStore.getFilesDir(context) + "/bin";
        File binFd = new File(binDir);
        if (!binFd.exists()) {
            if (!binFd.mkdirs()) return false;
        }
        cleanDirectory(binFd);

        // extract assets
        if (!extractDir("all", "")) {
            return false;
        }
        String mArch = PrefStore.getArch();
        if (!extractDir(mArch, "")) {
            return false;
        }

        // create symlinks for libs
        String libDir = PrefStore.getLibDir(context);
        File libFd = new File(libDir);
        File [] files = libFd.listFiles();
        for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
            File f = files[i];
            String libName = f.getName();
            String binName = libName
                    .replaceAll("^lib","")
                    .replaceAll ("\\.so$", "");
            try {
                File targetFd = new File(binDir + "/" + binName);
                targetFd.delete();
                Os.symlink(libDir + "/" + libName, binDir + "/" + binName);
            } catch (ErrnoException e) {
                e.printStackTrace();
                return false;
            }
        }

        // create .nomedia
        File noMediaFd = new File(PrefStore.getFilesDir(context) + "/.nomedia");
        try {
            noMediaFd.createNewFile();
        } catch (IOException ignored) {
        }

        // set permissions
        setPermissions(binFd);

        // update version
        return setVersion();
    }

}
