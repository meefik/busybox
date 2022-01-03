package ru.meefik.busybox;

import android.content.Context;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.Shell;

public class ShellInitializer extends Shell.Initializer {
    @Override
    public boolean onInit(@NonNull Context context, Shell shell) {
        String binDir = PrefStore.getFilesDir(context) + "/bin";
        String path = String.format("PATH=%s:$PATH", binDir);
        String traceMode = String.format("export TRACE_MODE=\"%s\"", PrefStore.isTraceMode(context));
        String installDir = String.format("export INSTALL_DIR=\"%s\"", PrefStore.getInstallDir(context));
        String ramDisk = String.format("export MOUNT_RAMDISK=\"%s\"", PrefStore.isRamDisk(context));
        String replaceApplets = String.format("export REPLACE_APPLETS=\"%s\"", PrefStore.isReplaceApplets(context));
        String installApplets = String.format("export INSTALL_APPLETS=\"%s\"", PrefStore.isInstallApplets(context));
        shell.newJob()
                .add(traceMode)
                .add(path)
                .add(installDir)
                .add(ramDisk)
                .add(replaceApplets)
                .add(installApplets)
                .exec();
        return true;
    }
}
