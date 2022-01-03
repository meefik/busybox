package ru.meefik.busybox;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class Logger {

    final private Context context;
    final private TextView output;
    final private ScrollView scroll;

    /**
     * Logger constructor
     *
     * @param context
     * @param output
     * @param scroll
     */
    Logger(Context context, TextView output, ScrollView scroll) {
        this.context = context;
        this.output = output;
        this.scroll = scroll;
        // enable context clickable
        output.setMovementMethod(LinkMovementMethod.getInstance());
//        output.setMaxLines(PrefStore.getMaxLines(context));
        output.setTextSize(TypedValue.COMPLEX_UNIT_SP, PrefStore.getFontSize(context));
    }

    /**
     * Generate timestamp
     *
     * @return timestamp
     */
    private String getTimeStamp() {
        return "[" + new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(new Date()) + "] ";
    }

    /**
     * Append the message to protocol and show
     *
     * @param line message
     */
    public synchronized void log(final String line) {
        if (line.length() == 0) return;
        // show line
        appendLine(line);
        // write log to file
        if (PrefStore.isLogger(context)) {
            write(line);
        }
    }

    /**
     * Clear protocol
     */
    void clear() {
        output.setText("");
    }

    /**
     * Show log on main activity
     *
     * @param line
     */
    private void appendLine(String line) {
        output.post(() -> {
            if (PrefStore.isTimestamp(context)) {
                output.append(getTimeStamp() + line);
            } else {
                output.append(line);
            }
            // scroll TextView to bottom
            scroll.post(() -> {
                scroll.fullScroll(View.FOCUS_DOWN);
                scroll.clearFocus();
            });
        });
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
     * Write to log file
     *
     * @param msg message
     */
    private void write(String msg) {
        String logFile = PrefStore.getLogFile(context);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(writer);
        }
    }
}
