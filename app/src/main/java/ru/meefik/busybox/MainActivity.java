package ru.meefik.busybox;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static TextView output;
    public static ScrollView scroll;
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PrefStore.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.outputView);
        scroll = (ScrollView) findViewById(R.id.scrollView);

        // enable context clickable
        output.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        PrefStore.setLocale(this);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_terminal:
                openTerminal();
                break;
            case R.id.action_settings:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                break;
            case R.id.action_help:
                new ExecScript(this, "info").start();
                break;
            case R.id.action_zip:
                requestWritePermissions();
                break;
            case R.id.action_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(PrefStore.getTheme(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView outputView = (TextView) findViewById(R.id.outputView);
        // restore font size
        outputView.setTextSize(TypedValue.COMPLEX_UNIT_SP, PrefStore.getFontSize(this));
        // restore logs
        String log = Logger.get();
        if (log.length() == 0) {
            // show info if empty
            new ExecScript(getApplicationContext(), "info").start();
        } else {
            showLog(log);
        }
    }

    /**
     * Show message in TextView, used from Logger
     *
     * @param log message
     */
    public static void showLog(final String log) {
        // show log in TextView
        output.post(new Runnable() {
            @Override
            public void run() {
                output.setText(log);
                // scroll TextView to bottom
                scroll.post(new Runnable() {
                    @Override
                    public void run() {
                        scroll.fullScroll(View.FOCUS_DOWN);
                        scroll.clearFocus();
                    }
                });
            }
        });
    }

    public void installBtnOnClick(final View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_install_dialog)
                .setMessage(R.string.message_confirm_install_dialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                new ExecScript(getApplicationContext(), "install").start();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    public void removeBtnOnClick(final View view) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_confirm_remove_dialog)
                .setMessage(R.string.message_confirm_remove_dialog)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                new ExecScript(getApplicationContext(), "remove").start();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
    }

    private void openTerminal() {
        try {
            Intent intent_terminal = new Intent("jackpal.androidterm.RUN_SCRIPT");
            intent_terminal.addCategory(Intent.CATEGORY_DEFAULT);
            intent_terminal.putExtra("jackpal.androidterm.iInitialCommand",
                    PrefStore.getTerminalCmd(this));
            startActivity(intent_terminal);
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_terminal_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void makeZipArchiveDialog() {
        String archiveName = PrefStore.getStorage() + "/busybox-" + PrefStore.getArch() + ".zip";
        final EditText input = new EditText(this);
        input.setText(archiveName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_export_dialog)
                .setCancelable(false)
                .setView(input, 16, 32, 16, 0)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String archiveName = input.getText().toString();
                                if (!archiveName.isEmpty()) {
                                    if (EnvUtils.makeZipArchive(getApplicationContext(), archiveName)) {
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
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
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
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeZipArchiveDialog();
                } else {
                    Toast.makeText(this, getString(R.string.write_permissions_disallow), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

}
