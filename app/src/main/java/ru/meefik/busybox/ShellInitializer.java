package ru.meefik.busybox;

import android.content.Context;

import androidx.annotation.NonNull;

import com.topjohnwu.superuser.Shell;

public class ShellInitializer extends Shell.Initializer {

    @Override
    public boolean onInit(@NonNull Context context, Shell shell) {
        String binDir = PrefStore.getFilesDir(context) + "/bin";
        String path = String.format("PATH=%s:$PATH", binDir);
        shell.newJob().add(path).exec();
        return true;
    }

}
