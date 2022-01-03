package ru.meefik.busybox;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.topjohnwu.superuser.CallbackList;
import com.topjohnwu.superuser.Shell;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private Logger logger;
    private AssetsUtils assets;

    static {
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.Builder builder = Shell.Builder.create();

        Shell.setDefaultBuilder(builder
                .setInitializers(ShellInitializer.class)
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView output = findViewById(R.id.outputView);
        ScrollView scroll = findViewById(R.id.scrollView);

        Shell.getShell(shell -> {
            logger = new Logger(this, output, scroll);
            assets = new AssetsUtils(this, logger);
            assets.extractAssets();
            execScript("info.sh", false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                execScript("info.sh", false);
                return true;
            case R.id.action_zip:
                requestWritePermissions();
                return true;
            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.action_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void installBtnOnClick(final View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_install_dialog)
                .setMessage(R.string.message_confirm_install_dialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        (dialog, id) -> execScript("install.sh", true))
                .setNegativeButton(android.R.string.no,
                        (dialog, id) -> dialog.cancel())
                .show();
    }

    public void removeBtnOnClick(final View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_remove_dialog)
                .setMessage(R.string.message_confirm_remove_dialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        (dialog, id) -> execScript("remove.sh", true))
                .setNegativeButton(android.R.string.no,
                        (dialog, id) -> dialog.cancel())
                .show();
    }

    /**
     * Execute a shell script.
     *  @param script shell script
     * @param root   from superuser
     */
    private void execScript(String script, Boolean root) {
        logger.clear();
        String absoluteScriptPath = PrefStore.getFilesDir(this) + "/bin/" + script;
        String cmd = String.format("busybox ash \"%s\"", absoluteScriptPath);
        List<String> callbackList = new CallbackList<String>() {
            @Override
            public void onAddElement(String s) {
                logger.log(s + '\n');
            }
        };
        if (root) {
            if (Shell.rootAccess()) {
                Shell.su(cmd).to(callbackList).submit();
            } else {
                logger.log("Require superuser privileges (root).\n");
            }
        } else {
            Shell.sh(cmd).to(callbackList).submit();
        }
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
     * Add file to zip archive
     *
     * @param srcFile file
     * @param zip     zip stream
     */
    private void addFileToZip(File srcFile, ZipOutputStream zip) {
        byte[] buf = new byte[1024];
        int len;
        FileInputStream in = null;
        try {
            in = new FileInputStream(srcFile);
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(in);
        }
    }

    /**
     * Make zip archive
     *
     * @param archiveName archive file name
     */
    boolean makeZipArchive(String archiveName) {
        boolean result = false;
        FileOutputStream fos = null;
        ZipOutputStream zip = null;
        String libDir = PrefStore.getLibDir(this);
        String binDir = PrefStore.getFilesDir(this) + "/bin";
        try {
            fos = new FileOutputStream(archiveName);
            zip = new ZipOutputStream(new BufferedOutputStream(fos));
            File busybox = new File(libDir + "/libbusybox.so");
            zip.putNextEntry(new ZipEntry("busybox"));
            addFileToZip(busybox, zip);
            File sslHelper = new File(libDir + "/libssl_helper.so");
            zip.putNextEntry(new ZipEntry("ssl_helper"));
            addFileToZip(sslHelper, zip);
            File updateBinary = new File(binDir + "/recovery.sh");
            zip.putNextEntry(new ZipEntry("META-INF/com/google/android/update-binary"));
            addFileToZip(updateBinary, zip);
            File addonBinary = new File(binDir + "/addon_d.sh");
            zip.putNextEntry(new ZipEntry("addon_d.sh"));
            addFileToZip(addonBinary, zip);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(zip);
            close(fos);
        }
        return result;
    }

    @SuppressLint("RestrictedApi")
    private void makeZipArchiveDialog() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String archiveName = downloadDir.getAbsolutePath() + "/busybox-" + PrefStore.getArch() + ".zip";
        final EditText input = new EditText(this);
        input.setText(archiveName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_export_dialog)
                .setCancelable(false)
                .setView(input, 16, 32, 16, 0)
                .setPositiveButton(android.R.string.yes,
                        (dialog, id) -> {
                            String archiveName1 = input.getText().toString();
                            if (!archiveName1.isEmpty()) {
                                if (makeZipArchive(archiveName1)) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.toast_export_success),
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            getString(R.string.toast_export_error),
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        (dialog, id) -> dialog.cancel())
                .show();
    }

    /**
     * Request permission to write to storage.
     */
    private void requestWritePermissions() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        } else {
            makeZipArchiveDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeZipArchiveDialog();
            } else {
                Toast.makeText(this, getString(R.string.write_permissions_disallow), Toast.LENGTH_LONG).show();
            }
        }
    }

}
