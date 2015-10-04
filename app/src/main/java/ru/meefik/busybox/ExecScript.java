package ru.meefik.busybox;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 19.09.15.
 */
public class ExecScript extends Thread {

    private Context context;
    private String command;

    public ExecScript(Context c, String command) {
        this.context = c;
        this.command = command;
    }

    private void info() {
        String envDir = PrefStore.getEnvDir(context);
        List<String> params = new ArrayList<>();
        params.add("ENV_DIR=" + envDir);
        params.add(". " + envDir + "/bin/info.sh");
        EnvUtils.exec(context, "/system/bin/sh", params);
    }

    private void install() {
        // check root
        if (!EnvUtils.isRooted(context)) return;
        String envDir = PrefStore.getEnvDir(context);
        List<String> params = new ArrayList<>();
        params.add("INSTALL_DIR=" + PrefStore.getInstallDir(context));
        params.add("INSTALL_APPLETS=" + PrefStore.isInstallApplets(context));
        params.add("REPLACE_APPLETS=" + PrefStore.isReplaceApplets(context));
        params.add(". " + envDir + "/bin/install.sh");
        params.add("busybox printf '.'");
        EnvUtils.exec(context, "su", params);
    }

    private void remove() {
        // check root
        if (!EnvUtils.isRooted(context)) return;
        String envDir = PrefStore.getEnvDir(context);
        List<String> params = new ArrayList<>();
        params.add("INSTALL_DIR=" + PrefStore.getInstallDir(context));
        params.add(". " + envDir + "/bin/remove.sh");
        params.add("busybox printf '.'");
        EnvUtils.exec(context, "su", params);
    }

    @Override
    public void run() {
        Logger.clear();
        // update env
        if (!EnvUtils.update(context)) return;
        // exec command
        switch (command) {
            case "info":
                info();
                break;
            case "install":
                Logger.log(context, ">>> INSTALL BUSYBOX\n");
                install();
                break;
            case "remove":
                Logger.log(context, ">>> REMOVE BUSYBOX\n");
                remove();
                break;
        }
    }

}
